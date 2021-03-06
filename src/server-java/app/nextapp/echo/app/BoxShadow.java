/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo.app;

import java.io.Serializable;

/**
 * @author sieskei (XSoft Ltd.)
 */
public class BoxShadow implements Serializable {
   
    /** Serial Version UID. */
    private static final long serialVersionUID = 20111221L;
    
    public static final int STYLE_DEFAULT = 0;    
    public static final int STYLE_INSET   = 1;
        
    private final Extent hShadowPos;
    private final Extent vShadowPos;
    private final Extent blurDistance;
    private final Extent spreadSize;
    private final Color color;
    private final int style;

    /**
     * Creates a new <code>BoxShadow (CSS3)</code>.
     * 
     * @param hShadow the position of the horizontal shadow. Negative values are allowed. 
     *        (this property only supports <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param vShadow the position of the vertical shadow. Negative values are allowed.
     *        (this property only supports <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param blur the blur distance.
     *        (this property only supports <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param spread the size of shadow.
     *        (this property only supports <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param color the color of the shadow. Look at CSS Color Values for a complete list of possible color values.
     * @param style changes the shadow from an outer shadow (outset) to an inner shadow.
     */
    public BoxShadow(Extent hShadow, Extent vShadow, Extent blur, Extent spread, Color color, int style) {
      this.hShadowPos = hShadow;
      this.vShadowPos = vShadow;
      this.blurDistance = blur;
      this.spreadSize = spread;
      this.color = color;
      this.style = style;
    }
    
    /**
     * @see BoxShadow#BoxShadow(nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Color, int) 
     */
    public BoxShadow(int hShadowPx, int vShadowPx) {
      this(new Extent(hShadowPx), new Extent(vShadowPx), new Extent(0), new Extent(0), Color.BLACK, STYLE_DEFAULT);
    }
    
    /**
     * @see BoxShadow#BoxShadow(nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Extent, nextapp.echo.app.Color, int) 
     */
    public BoxShadow(int hShadowPx, int vShadowPx, Color color) {
      this(new Extent(hShadowPx), new Extent(vShadowPx), new Extent(0), new Extent(0), color, STYLE_DEFAULT);
    }
    
    public Extent getHorizontalShadowPosition() {
        return hShadowPos;
    }

    public Extent getVerticalShadowPosition() {
        return vShadowPos;
    }

    public Extent getBlurDistance() {
      return blurDistance;
    }

    public Extent getSpreadSize() {
        return spreadSize;
    }

    public Color getColor() {
        return color;
    }

    public int getStyle() {
        return style;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
      if (obj == null) {
          return false;
      }
      if (getClass() != obj.getClass()) {
          return false;
      }
      final BoxShadow other = (BoxShadow) obj;
      if (this.hShadowPos != other.hShadowPos && (this.hShadowPos == null || !this.hShadowPos.equals(other.hShadowPos))) {
          return false;
      }
      if (this.vShadowPos != other.vShadowPos && (this.vShadowPos == null || !this.vShadowPos.equals(other.vShadowPos))) {
          return false;
      }
      if (this.blurDistance != other.blurDistance && (this.blurDistance == null || !this.blurDistance.equals(other.blurDistance))) {
          return false;
      }
      if (this.spreadSize != other.spreadSize && (this.spreadSize == null || !this.spreadSize.equals(other.spreadSize))) {
          return false;
      }
      if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
          return false;
      }
      if (this.style != other.style) {
          return false;
      }
      return true;
    }

    /**
     * Returns a string describing the state of the BoxShadow.  
     * For debugging purposes only, do not rely on formatting.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return vShadowPos + " " + hShadowPos + " " + blurDistance + " " + spreadSize + " " + color + 
                (style == STYLE_DEFAULT ? "" : style == STYLE_INSET ? "inset" : "outset");
    }
}
