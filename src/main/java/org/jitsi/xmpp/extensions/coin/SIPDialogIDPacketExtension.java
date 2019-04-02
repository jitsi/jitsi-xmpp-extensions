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
 * SIP Dialog ID packet extension.
 *
 * @author Sebastien Vincent
 */
public class SIPDialogIDPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that SIP Dialog ID belongs to.
     */
    public static final String NAMESPACE = "";

    /**
     * The name of the element that contains the SIP Dialog ID data.
     */
    public static final String ELEMENT_NAME = "sip";

    /**
     * Display text element name.
     */
    public static final String ELEMENT_DISPLAY_TEXT = "display-text";

    /**
     * Call ID element name.
     */
    public static final String ELEMENT_CALLID = "call-id";

    /**
     * From tag element name.
     */
    public static final String ELEMENT_FROMTAG = "from-tag";

    /**
     * From tag element name.
     */
    public static final String ELEMENT_TOTAG = "to-tag";

    /**
     * Display text.
     */
    private String displayText = null;

    /**
     * Call ID.
     */
    private String callID = null;

    /**
     * From tag.
     */
    private String fromTag = null;

    /**
     * To tag.
     */
    private String toTag = null;

    /**
     * Constructor
     */
    public SIPDialogIDPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
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
        xml.optElement(ELEMENT_CALLID, callID);
        xml.optElement(ELEMENT_FROMTAG, fromTag);
        xml.optElement(ELEMENT_TOTAG, toTag);

        return xml;
    }
}
