package nextapp.echo.webcontainer;

import java.io.IOException;
import nextapp.echo.app.util.Context;

/**
 *
 * @author sieskei
 */
public interface ClientMessageProcessor {
  
    /**
     * 
     * @param context
     * @throws IOException 
     */
    public void process(Context context) throws IOException;
}
