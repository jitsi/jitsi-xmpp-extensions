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
 * Description packet extension.
 *
 * @author Sebastien Vincent
 */
public class DescriptionPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that description belongs to.
     */
    public static final String NAMESPACE = null;

    /**
     * The name of the element that contains the description data.
     */
    public static final String ELEMENT_NAME = "conference-description";

    /**
     * Subject element name.
     */
    public static final String ELEMENT_SUBJECT = "subject";

    /**
     * Display text element name.
     */
    public static final String ELEMENT_DISPLAY_TEXT = "display-text";

    /**
     * Free text element name.
     */
    public static final String ELEMENT_FREE_TEXT = "free-text";

    /**
     * Max user count element name.
     */
    public static final String ELEMENT_MAX_USER_COUNT =
        "maximum-user-count";

    /**
     * The subject.
     */
    private String subject = "";

    /**
     * Display text.
     */
    private String displayText = null;

    /**
     * Free text.
     */
    private String freeText = null;

    /**
     * Maximum user count.
     */
    private int maximumUserCount = 0;

    /**
     * Constructor.
     */
    public DescriptionPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    /**
     * Set subject.
     *
     * @param subject subject
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * Set display text.
     *
     * @param displayText display text
     */
    public void setDisplayText(String displayText)
    {
        this.displayText = displayText;
    }

    /**
     * Set free text.
     *
     * @param freeText free text
     */
    public void setFreeText(String freeText)
    {
        this.freeText = freeText;
    }

    /**
     * Get subject.
     *
     * @return subject
     */
    public String getSubject()
    {
        return subject;
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
     * Get free text.
     *
     * @return free text
     */
    public String getFreeText()
    {
        return freeText;
    }

    /**
     * The child elements content.
     * @return the child elements content.
     */
    @Override
    public XmlStringBuilder getChildElementBuilder()
    {
        XmlStringBuilder xml = new XmlStringBuilder();

        xml.optElement(ELEMENT_SUBJECT, subject);
        xml.optElement(ELEMENT_DISPLAY_TEXT, displayText);
        xml.optElement(ELEMENT_FREE_TEXT, freeText);
        if(maximumUserCount != 0)
        {
            xml.optElement(ELEMENT_MAX_USER_COUNT, maximumUserCount);
        }

        return xml;
    }
}
