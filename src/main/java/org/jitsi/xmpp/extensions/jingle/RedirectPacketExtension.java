/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.xmpp.extensions.jingle;

import org.jitsi.xmpp.extensions.*;

/**
 * The redirect <tt>ExtensionElement</tt>.
 *
 * @author Sebastien Vincent
 */
public class RedirectPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The name of the "redirect" element.
     */
    public static final String ELEMENT = "redirect";

    /**
     * The namespace.
     */
    public static final String NAMESPACE = "http://www.google.com/session";

    /**
     * The redirect text.
     */
    private String redir = null;

    /**
     * Creates a new {@link RedirectPacketExtension} instance.
     */
    public RedirectPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Set redirection.
     *
     * @param redir redirection
     */
    public void setRedir(String redir)
    {
        this.setText(redir);
        this.redir = redir;
    }

    /**
     * Get redirection.
     *
     * @return redirection
     */
    public String getRedir()
    {
        return redir;
    }
}
