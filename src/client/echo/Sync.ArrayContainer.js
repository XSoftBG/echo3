/**
 * Abstract base class for column/row peers.
 * This class should not be extended by developers, the implementation is subject to change.
 */
Echo.Sync.ArrayContainer = Core.extend(Echo.Render.ComponentSync, {

    $abstract: {
      
        /** 
         * Abstract method which renders layout data on child cell element.
         * 
         * @param {Echo.Component} child the child component
         * @param {Element} the DOM element containing the child
         */
        renderChildLayoutData: function(child, cellElement) { }
    },
    
    $virtual: {
      
        /**
         * The prototype of child DOM element to be cloned when rendering.
         * @type Element 
         */
        childPrototype: null,
        
        /** 
         * The key code which should move focus to the previous child cell. 
         * @type Number
         */
        prevFocusKey: null,
        
        /** 
         * The Echo.Render.ComponentSync focus flag indicating which keys should trigger focus changes to the previous child. 
         * @type Boolean
         */
        prevFocusFlag: null,
        
        /** 
         * The key code which should move focus to the next child cell. 
         * @type Number
         */
        nextFocusKey: null,

        /** 
         * The Echo.Render.ComponentSync focus flag indicating which keys should trigger focus changes to the next child.
         * @type Boolean
         */
        nextFocusFlag: null,
        
        /** 
         * Flag indicating whether focus key should be inverted when the component is rendered with an RTL layout direction.
         * @type Boolean 
         */
        invertFocusRtl: false
    },
    
    /**
     * The root DOM element of the rendered array container.
     * @type Element
     */
    element: null,

    /**
     * The DOM element to which child elements should be added.  May be equivalent to <code>element</code>.
     * @type Element
     */
    containerElement: null,
    
    /**
     * Prototype Element to be cloned and added between cells of the array container.
     * 
     * @type Element
     */
    spacingPrototype: null,

    /** 
     * Number of pixels to be rendered as spacing between child cells of the container.
     * @type Number
     */
    cellSpacing: null,

    /**
     * Mapping between child render ids and child container cell elements. 
     */
    _childIdToElementMap: null,

    /**
     * Processes a key press event.  Provides support for adjusting focus via arrow keys.
     * 
     * @param e the event
     */
    clientKeyDown: function(e) {
        switch (e.keyCode) {
        case this.prevFocusKey:
        case this.nextFocusKey:
            var focusPrevious = e.keyCode == this.prevFocusKey;
            if (this.invertFocusRtl && !this.component.getRenderLayoutDirection().isLeftToRight()) {
                focusPrevious = !focusPrevious;
            }
            var focusedComponent = this.client.application.getFocusedComponent();
            if (focusedComponent && focusedComponent.peer && focusedComponent.peer.getFocusFlags) {
                var focusFlags = focusedComponent.peer.getFocusFlags();
                if ((focusPrevious && focusFlags & this.prevFocusFlag) || (!focusPrevious && focusFlags & this.nextFocusFlag)) {
                    var focusChild = this.client.application.focusManager.findInParent(this.component, focusPrevious);
                    if (focusChild) {
                        this.client.application.setFocusedComponent(focusChild);
                        Core.Web.DOM.preventEventDefault(e.domEvent);
                        return false;
                    }
                }
            }
            break;
        }
        return true;
    },

    /**
     * Renders the specified child to the containerElement.
     * 
     * @param {Echo.Update.ComponentUpdate} the update
     * @param {Echo.Component} the child component
     * @param {Number} index the index of the child within the parent 
     */
    _renderAddChild: function(update, child, index) {      
        var cellElement = this.childPrototype.cloneNode(false);
        this._childIdToElementMap[child.renderId] = cellElement;
        Echo.Render.renderComponentAdd(update, child, cellElement);

        this.renderChildLayoutData(child, cellElement);

        if (index != null) {
            var currentChildCount;
            if (this.containerElement.childNodes.length >= 3 && this.cellSpacing) {
                currentChildCount = (this.containerElement.childNodes.length + 1) / 2;
            } else {
                currentChildCount = this.containerElement.childNodes.length;
            }
            if (index == currentChildCount) {
                index = null;
            }
        }
        if (index == null || !this.containerElement.firstChild) {
            // Full render, append-at-end scenario, or index 0 specified and no children rendered.
            
            // Render spacing cell first if index != 0 and cell spacing enabled.
            if (this.cellSpacing && this.containerElement.firstChild) {
                this.containerElement.appendChild(this.spacingPrototype.cloneNode(false));
            }
    
            // Render child cell second.
            this.containerElement.appendChild(cellElement);
        } else {
            // Partial render insert at arbitrary location scenario (but not at end)
            var insertionIndex = this.cellSpacing ? index * 2 : index;
            var beforeElement = this.containerElement.childNodes[insertionIndex];
            
            // Render child cell first.
            this.containerElement.insertBefore(cellElement, beforeElement);
            
            // Then render spacing cell if required.
            if (this.cellSpacing) {
                this.containerElement.insertBefore(this.spacingPrototype.cloneNode(false), beforeElement);
            }
        }
    },
    
    /**
     * Renders all children.  Must be invoked by derived <code>renderAdd()</code> implementations.
     * 
     * @param {Echo.Update.ComponentUpdate} the update
     */
    renderAddChildren: function(update) {
        this._childIdToElementMap = {};
    
        var componentCount = this.component.getComponentCount();
        for (var i = 0; i < componentCount; ++i) {
            var child = this.component.getComponent(i);
            this._renderAddChild(update, child);
        }
    },

    /** @see Echo.Render.ComponentSync#renderDispose */
    renderDispose: function(update) { 
        this.element = null;
        this.containerElement = null;
        this._childIdToElementMap = null;
        this.spacingPrototype = null;
    },

    /**
     * Removes a child cell.
     * 
     * @param {Echo.Update.ComponentUpdate} the update
     * @param {Echo.Component} the child to remove
     */
    _renderRemoveChild: function(update, child) {
        var childElement = this._childIdToElementMap[child.renderId];
        if (!childElement) {
            return;
        }
        
        if (this.cellSpacing) {
            // If cell spacing is enabled, remove a spacing element, either before or after the removed child.
            // In the case of a single child existing in the Row, no spacing element will be removed.
            if (childElement.previousSibling) {
                this.containerElement.removeChild(childElement.previousSibling);
            } else if (childElement.nextSibling) {
                this.containerElement.removeChild(childElement.nextSibling);
            }
        }
        
        this.containerElement.removeChild(childElement);
        
        delete this._childIdToElementMap[child.renderId];
    },

    /** @see Echo.Render.ComponentSync#renderUpdate */
    renderUpdate: function(update) {
        var i, fullRender = false;
        if (update.hasUpdatedProperties() || update.hasUpdatedLayoutDataChildren()) {
            // Full render
            fullRender = true;
        } else {
            var removedChildren = update.getRemovedChildren();
            if (removedChildren) {
                // Remove children.
                for (i = 0; i < removedChildren.length; ++i) {
                    this._renderRemoveChild(update, removedChildren[i]);
                }
            }
            var addedChildren = update.getAddedChildren();
            if (addedChildren) {
                // Add children.
                for (i = 0; i < addedChildren.length; ++i) {
                    this._renderAddChild(update, addedChildren[i], this.component.indexOf(addedChildren[i])); 
                }
            }
        }
        if (fullRender) {
            var element = this.element;
            var containerElement = element.parentNode;
            Echo.Render.renderComponentDispose(update, update.parent);
            containerElement.removeChild(element);
            this.renderAdd(update, containerElement);
        }
        
        return fullRender;
    }
});

/**
 * Component rendering peer: Column
 */
Echo.Sync.Column = Core.extend(Echo.Sync.ArrayContainer, {

    $static: {
    
        /** 
         * Creates a prototype DOM element hierarchy to be cloned when rendering.   
         * 
         * @return the prototype Element
         * @type Element
         */
        _createColumnPrototype: function() {
            var div = document.createElement("div");
            div.style.outlineStyle = "none";
            div.tabIndex = "-1";
            return div;
        },
        
        /** 
         * Creates a prototype of row child DOM element to be cloned when rendering.
         * 
         * @return the prototype Element
         * @type Element
         */
         _createChildPrototype: function() {
            return document.createElement("div");
         },
        
        /** 
         * The prototype DOM element hierarchy to be cloned when rendering.
         * @type Element 
         */
        _columnPrototype: null
    },


    $load: function() {
        this._columnPrototype = this._createColumnPrototype();
        Echo.Render.registerPeer("Column", this);
    },
        
    /** @see Echo.Sync.ArrayContainer#childPrototype */
    childPrototype: null,
    
    /** @see Echo.Sync.ArrayContainer#prevFocusKey */
    prevFocusKey: 38,
    
    /** @see Echo.Sync.ArrayContainer#prevFocusFlag */
    prevFocusFlag: Echo.Render.ComponentSync.FOCUS_PERMIT_ARROW_UP,

    /** @see Echo.Sync.ArrayContainer#nextFocusKey */
    nextFocusKey: 40,

    /** @see Echo.Sync.ArrayContainer#nextFocusFlag */
    nextFocusFlag: Echo.Render.ComponentSync.FOCUS_PERMIT_ARROW_DOWN,
    
    /** Default Column layout constructor */
    $construct: function() {
        this.childPrototype = Echo.Sync.Column._createChildPrototype();
    },
    
    /** @see Echo.Render.ComponentSync#renderAdd */
    renderAdd: function(update, parentElement) {
        this.element = this.containerElement = Echo.Sync.Column._columnPrototype.cloneNode(true);
        this.element.id = this.component.renderId;
    
        Echo.Sync.renderComponentDefaults(this.component, this.element);
        Echo.Sync.Border.render(this.component.render("border"), this.element);
        Echo.Sync.Insets.render(this.component.render("insets"), this.element, "padding");
    
        var height = this.component.render("height", "auto");
        var width = this.component.render("width", "auto");
        
        // we should not set width to table cells!
        if (parentElement.tagName.toLowerCase() == "td") {
            this.element.style.height = height;
            this.element.style.width = width;
        } else {
            if(height != "auto") {
                this.element.style.height = "100%";
            }
            if(width != "auto") {
                this.element.style.width = "100%";
            }
            parentElement.style.height = height;
            parentElement.style.width = width;
        }
        
        this.cellSpacing = Echo.Sync.Extent.toPixels(this.component.render("cellSpacing"), false);
        if (this.cellSpacing) {
            this.spacingPrototype = document.createElement("div");
            this.spacingPrototype.style.height = this.cellSpacing + "px";
            this.spacingPrototype.style.fontSize = "1px";
            this.spacingPrototype.style.lineHeight = "0";
        }
        
        this.renderAddChildren(update);
        
        parentElement.appendChild(this.element);
    },
        
    /** @see Echo.Sync.ArrayContainer#renderChildLayoutData */
    renderChildLayoutData: function(child, cellElement) {
        var layoutData = child.render("layoutData");
        if (layoutData) {
            Echo.Sync.Color.render(layoutData.background, cellElement, "backgroundColor");
            Echo.Sync.FillImage.render(layoutData.backgroundImage, cellElement);
            Echo.Sync.Insets.render(layoutData.insets, cellElement, "padding");
            Echo.Sync.Alignment.render(layoutData.alignment, cellElement, true, this.component);
            if (layoutData.height) {
                if (Echo.Sync.Extent.isPercent(layoutData.height)) {
                    cellElement.style.height = layoutData.height;
                } else {
                    cellElement.style.height = Echo.Sync.Extent.toPixels(layoutData.height, false) + "px";
                }
            }
        }
    }
});

/**
 * Component rendering peer: Row
 */
Echo.Sync.Row = Core.extend(Echo.Sync.ArrayContainer, {

    $static: {
    
        /** 
         * Creates a prototype DOM element hierarchy to be cloned when rendering.   
         * 
         * @return the prototype Element
         * @type Element
         */
        _createRowPrototype: function() {
            var divTable = document.createElement("div");
            divTable.style.display = "table";
            divTable.style.width = "auto";
            divTable.style.height = "auto";
            divTable.style.outlineStyle = "none";
            divTable.tabIndex = "-1";
            divTable.style.borderCollapse = "collapse";
            
            var divRow = document.createElement("div");
            divRow.style.width = "auto";
            divRow.style.height = "auto";
            divRow.style.display = "table-row";
            
            divTable.appendChild(divRow);
            
            return divTable;
        },
        
        /** 
         * Creates a prototype of row child DOM element to be cloned when rendering.
         * 
         * @return the prototype Element
         * @type Element
         */
         _createChildPrototype: function() {
            var divCell = document.createElement("div");
            divCell.style.display = "table-cell";
            divCell.style.verticalAlign = "middle";
            return divCell;
         },
        
        /** 
         * The prototype DOM element hierarchy to be cloned when rendering.
         * @type Element 
         */
        _rowPrototype: null
    },
    
    $load: function() {
        this._rowPrototype = this._createRowPrototype();
        Echo.Render.registerPeer("Row", this);
    },

    /** @see Echo.Render.ArrayContainer#childPrototype */
    childPrototype: null,

    /** @see Echo.Sync.ArrayContainer#prevFocusKey */
    prevFocusKey: 37,
    
    /** @see Echo.Sync.ArrayContainer#prevFocusFlag */
    prevFocusFlag: Echo.Render.ComponentSync.FOCUS_PERMIT_ARROW_LEFT,
    
    /** @see Echo.Sync.ArrayContainer#nextFocusKey */
    nextFocusKey: 39,

    /** @see Echo.Sync.ArrayContainer#nextFocusFlag */
    nextFocusFlag: Echo.Render.ComponentSync.FOCUS_PERMIT_ARROW_RIGHT,
    
    /** @see Echo.Sync.ArrayContainer#invertFocusRtl */
    invertFocusRtl: true,
    
    /** Default Row layout constructor */
    $construct: function() {
        this.childPrototype = Echo.Sync.Row._createChildPrototype();
    },
    
    /** @see Echo.Render.ComponentSync#renderAdd */
    renderAdd: function(update, parentElement) {
        this.element = Echo.Sync.Row._rowPrototype.cloneNode(true);
        this.element.id = this.component.renderId;

        Echo.Sync.renderComponentDefaults(this.component, this.element);
        Echo.Sync.Border.render(this.component.render("border"), this.element);
        Echo.Sync.Insets.render(this.component.render("insets"), this.element, "padding");
        Echo.Sync.Alignment.render(this.component.render("alignment"), this.element, true, this.component);
        
        //                      div (table)  div (table-row)
        this.containerElement = this.element.firstChild;
        
        var width = this.component.render("width");
        var height = this.component.render("height");
        
        var change_width  = (!parentElement.style.width)  || parentElement.style.width  == '' || parentElement.style.width  == 'auto';
        var change_height = (!parentElement.style.height) || parentElement.style.height == '' || parentElement.style.height == 'auto'; 
        
        if (width && change_width) {
            parentElement.style.height = width;
            this.element.style.width = "100%";
        } else if (width) {
            this.element.style.width = width;
            this.element.firstChild.style.width = "100%";
        }
        
        if (height && change_height) {
            parentElement.style.height = height;
            this.element.style.height = "100%";
        } else if (height) {
            this.element.style.height = height;
            this.element.firstChild.style.height = "100%";
        }
        
        this.cellSpacing = Echo.Sync.Extent.toPixels(this.component.render("cellSpacing"), false);
        if (this.cellSpacing) {
            this.spacingPrototype = document.createElement("div");
            this.spacingPrototype.style.display = "table-cell";
            this.spacingPrototype.style.padding = 0;
            this.spacingPrototype.style.width = this.cellSpacing + "px";
            this.spacingPrototype.style.height = "auto";
        }
        
        this.renderAddChildren(update);
                
        parentElement.appendChild(this.element);
    },

    /** @see Echo.Sync.ArrayContainer#renderChildLayoutData */
    renderChildLayoutData: function(child, cellElement) {
        var layoutData = child.render("layoutData");
        if (layoutData) {
            Echo.Sync.Color.render(layoutData.background, cellElement, "backgroundColor");
            Echo.Sync.FillImage.render(layoutData.backgroundImage, cellElement);
            Echo.Sync.Alignment.render(layoutData.alignment, cellElement, { horizontal: true, vertical: false }, this.component);
            Echo.Sync.Insets.render(layoutData.insets, cellElement, "padding");
            if (layoutData.width) {
                if (Echo.Sync.Extent.isPercent(layoutData.width)) {
                    cellElement.style.width = layoutData.width;
                    if (this.element.firstChild.style.width != "100%") {
                        this.element.firstChild.style.width = "100%";
                    }
                } else {
                    cellElement.style.width = Echo.Sync.Extent.toPixels(layoutData.width, true) + "px";
                }
            }
        }
    }
});