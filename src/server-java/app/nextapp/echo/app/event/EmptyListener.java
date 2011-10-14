package nextapp.echo.app.event;

import java.io.Serializable;
import java.util.EventListener;
    

/**     
 * The listener interface for receiving empty events.
 *
 * @see nextapp.echo.app.event.EmptyListener
 */
public interface EmptyListener
extends EventListener, Serializable {
    
    /**
     * Invoked when an action occurs.
     *
     * @param e the fired <code>ActionEvent</code>
     */
    public void emptyPerformed(EmptyEvent e);
    public void noEmptyPerformed(EmptyEvent e);
}
