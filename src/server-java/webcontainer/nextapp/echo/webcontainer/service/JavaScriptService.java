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

package nextapp.echo.webcontainer.service;


import nextapp.echo.webcontainer.ContentType;
import nextapp.echo.webcontainer.util.Resource;

/**
 * A service which renders <code>JavaScript</code> resource files.
 */
public class JavaScriptService extends DefaultStringVersionService {
    
    /**
     * Creates a new <code>JavaScript</code> service from the specified
     * resource in the <code>CLASSPATH</code>.
     * 
     * @param id the <code>Service</code> id
     * @param resourceName the <code>CLASSPATH</code> resource name containing
     *        the JavaScript content
     * @return the created <code>JavaScriptService</code>
     */
    public static JavaScriptService forResource(String id, String resourceName) {
        String content = Resource.getResourceAsString(resourceName);
        return new JavaScriptService(id, content);
    }
    
    public static JavaScriptService forResources(String id, String[] resourceNames) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < resourceNames.length; ++i) {
            out.append(Resource.getResourceAsString(resourceNames[i]));
            out.append("\n\n");
        }
        return new JavaScriptService(id, out.toString());
    }

    /**
     * Creates a new <code>JavaScriptService</code>.
     * 
     * @param id the <code>Service</code> id
     * @param content the <code>JavaScript content</code>
     */
    public JavaScriptService(String id, String content) {
        super(id, content);
    }

    /**
     * @see DefaultStringVersionService#getContnentType()
     */
    @Override
    ContentType getContnentType() {
        return ContentType.TEXT_JAVASCRIPT;
    }
}
