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
 * URI packet extension.
 *
 * @author Sebastien Vincent
 */
public class URIPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that URI belongs to.
     */
    public static final String NAMESPACE = "";

    /**
     * The name of the element that contains the URI data.
     */
    public static final String ELEMENT_NAME = "uri";

    /**
     * Display text element name.
     */
    public static final String ELEMENT_DISPLAY_TEXT = "display-text";

    /**
     * Purpose element name.
     */
    public static final String ELEMENT_PURPOSE = "purpose";

    /**
     * Display text.
     */
    private String displayText = null;

    /**
     * Purpose.
     */
    private String purpose = null;

    /**
     * Constructor.
     *
     * @param elementName element name
     */
    public URIPacketExtension(String elementName)
    {
        super(NAMESPACE, elementName);
    }

    /**
     * Set the display text.
     *
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
     * Set the purpose.
     *
     * @param purpose purpose
     */
    public void setPurpose(String purpose)
    {
        this.purpose = purpose;
    }

    /**
     * Get purpose.
     *
     * @return purpose
     */
    public String getPurpose()
    {
        return purpose;
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
        xml.optElement(ELEMENT_PURPOSE, purpose);

        return xml;
    }
}
