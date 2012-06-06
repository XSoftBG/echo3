/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2012 NextApp, Inc.
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

package nextapp.echo.webcontainer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.util.Log;
import nextapp.echo.app.util.Uid;

/**
 * 
 * 
 * @author Miro Yozov
 */
public abstract class WebSocketConnectionHandler {
  
    /** A <code>ThreadLocal</code> reference to the <code>HTTPConnection</code> relevant to the current thread. */ 
    private static final ThreadLocal activeConnection = new ThreadLocal();
  
    /** Request parameter identifying requested <code>UserInstance</code>. */
    public static final String USER_INSTANCE_ID_PARAMETER = "uiid";
           
    /**
     * Returns a reference to the <code>WSConnection</code> that is 
     * relevant to the current thread, or null if no connection is relevant.
     * 
     * @return the relevant <code>WSConnection</code>
     */
    public static final WSConnection getActiveConnection() {
        return (WSConnection) activeConnection.get();
    }
        
    private WebContainerServlet parent;
    
    void assignParent(WebContainerServlet parent) {
        this.parent = parent;
    }
    
    WebContainerServlet getParent() {
        return this.parent;
    }
    
    public ApplicationWebSocket process(HttpServlet servlet, HttpServletRequest request, String protocol) {
        WSConnection conn = null;
        try {
            conn = new WSConnection(servlet, request, protocol);
            // Log.log("WSCH: process!");
            if (!conn.isReady()) {
                // Log.log("WSCH: process [new web socket]!");
                final HttpSession session = request.getSession();
                if (session == null) {
                    throw new RuntimeException("WebSocketConnectionHandler: initialization of WSConnection is impossible without session!");
                }
                final String key = AbstractConnection.getUserInstanceContainerSessionKey(this.parent);
                final UserInstanceContainer userInstanceContainer = (UserInstanceContainer) session.getAttribute(key);
                
                conn.preInit(userInstanceContainer);
                conn.postInit(newApplicationWebSocket(conn.getUserInstance().getApplicationInstance()));
            }
            
        }
        catch (Exception ex) {
            processError(request, ex);
        }
        finally {
            activeConnection.set(null);
            return conn.getApplicationWebSocket();
        }
    }
    
    /**
     * Exception handler for process() method.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param ex the exception 
     * @throws ServletException
     * @throws IOException
     */
    private void processError(HttpServletRequest request, Exception ex) {
        String exceptionId = Uid.generateUidString();
        Log.log("Server Exception. ID: " + exceptionId, ex);
        throw new RuntimeException(ex);
    }
    
    public abstract ApplicationWebSocket newApplicationWebSocket(ApplicationInstance applicationInstance);
}