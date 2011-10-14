package nextapp.echo.app.event;

import java.util.EventObject;

/**
 * An event which listen if a TextComponent is empty or it`s not empty.
 */
public class EmptyEvent extends EventObject {
    
    /** Serial Version UID. */
    private static final long serialVersionUID = 20100402L;

    /**
     * Creates a new <code>EventEvent</code>.
     * 
     * @param source the object from which the event originated
     */
    public EmptyEvent(Object source) { super(source); }
}
