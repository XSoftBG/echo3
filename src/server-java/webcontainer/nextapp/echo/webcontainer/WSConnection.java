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

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Miro Yozov
 */
public class WSConnection extends AbstractConnection {
  
    /**
     * Prefix to use for <code>UserInstanceContainer</code> <code>HttpSession</code> keys.
     */
    private static final String WEB_SOCKET_SESSION_KEY_PREFIX = "EchoWebSocket";
    
    static String getWebSocketSessionKey(GenericServlet servlet) {
        return WEB_SOCKET_SESSION_KEY_PREFIX + ":" + servlet.getServletName();
    }
  
    private final String protocol;
    private ApplicationWebSocket applicationWebSocket = null;

    public WSConnection(HttpServlet servlet, HttpServletRequest request, String protocol)
    {
      super(servlet, request);
      this.protocol = protocol;
      HttpSession session = request.getSession();
      if (session != null) {
          applicationWebSocket = (ApplicationWebSocket) session.getAttribute(getWebSocketSessionKey(this.servlet));
      }
    }
  
    protected void storeUiid() {
        String uiidParam = this.request.getParameter("uiid");
        if (uiidParam != null) {
            uiid = uiidParam;
        }
    }
    
    public String getProtocol() {
        return protocol;
    }

    public ApplicationWebSocket getApplicationWebSocket() {
      return applicationWebSocket;
    }
    
    void init(UserInstanceContainer userInstanceContainer, ApplicationWebSocket applicationWebSocket) {        
        this.userInstanceContainer = userInstanceContainer;
        this.userInstance = userInstanceContainer.getUserInstanceById(uiid);
        this.applicationWebSocket = applicationWebSocket;
        
        HttpSession session = request.getSession();
        session.setAttribute(getUserInstanceContainerSessionKey(this.servlet), userInstanceContainer);
        session.setAttribute(getWebSocketSessionKey(this.servlet), applicationWebSocket);
        
        this.userInstance.initWS(this);
    }
    
    /**
     * Determines if the <code>WSConnection</code> has been initialized, 
     * i.e., whether its <code>init()</code> method has been invoked.
     * 
     * @return true if the <code>WSConnection</code> is initialized
     */
    public boolean isInitialized() {
        return this.userInstance != null;
    }
}
