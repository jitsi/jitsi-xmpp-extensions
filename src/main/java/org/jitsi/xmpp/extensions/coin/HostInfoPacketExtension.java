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
package org.jitsi.xmpp.extensions.coin;

import org.jitsi.xmpp.extensions.*;

import org.jivesoftware.smack.util.*;

/**
 * Host Information packet extension.
 *
 * @author Sebastien Vincent
 */
public class HostInfoPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that media belongs to.
     */
    public static final String NAMESPACE = null;

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_NAME = "host-info";

    /**
     * Display text element name.
     */
    public static final String ELEMENT_DISPLAY_TEXT = "display-text";

    /**
     * Web page element name.
     */
    public static final String ELEMENT_WEB_PAGE = "web-page";

    /**
     * Display text.
     */
    private String displayText = null;

    /**
     * Web page.
     */
    private String webPage = null;

    /**
     * Constructor.
     */
    public HostInfoPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    /**
     * Set display text.
     * @param displayText display text
     */
    public void setDisplayText(String displayText)
    {
        this.displayText = displayText;
    }

    /**
     * Get display text.
     *
     * @return display text
     */
    public String getDisplayText()
    {
        return displayText;
    }

    /**
     * Set web page.
     * @param webPage web page
     */
    public void setWebPage(String webPage)
    {
        this.webPage = webPage;
    }

    /**
     * Get web page.
     *
     * @return web page
     */
    public String getWebPage()
    {
        return webPage;
    }

    /**
     * The child elements content.
     * @return the child elements content.
     */
    @Override
    public XmlStringBuilder getChildElementBuilder()
    {
        XmlStringBuilder xml = new XmlStringBuilder();

        xml.optElement(ELEMENT_DISPLAY_TEXT, displayText);
        xml.optElement(ELEMENT_WEB_PAGE, webPage);

        return xml;
    }
}
