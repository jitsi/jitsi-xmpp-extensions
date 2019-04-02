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
 * Execution packet extension.
 *
 * @author Sebastien Vincent
 */
public class ExecutionPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that media belongs to.
     */
    public static final String NAMESPACE = null;

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_REFERRED_NAME = "referred";

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_DISCONNECTION_NAME =
        "disconnection-info";

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_JOINING_NAME = "joining-info";

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_MODIFIED_NAME = "modified";

    /**
     * "By" element name.
     */
    public static final String ELEMENT_BY = "by";

    /**
     * "Reason" element name.
     */
    public static final String ELEMENT_REASON = "reason";

    /**
     * "When" element name.
     */
    public static final String ELEMENT_WHEN = "display-text";

    /**
     * Date of the execution.
     */
    private String when = null;

    /**
     * By.
     */
    private String by = null;

    /**
     * Reason.
     */
    private String reason = null;

    /**
     * Set "by" field.
     *
     * @param by string to set
     */
    public void setBy(String by)
    {
        this.by = by;
    }

    /**
     * Get "by" field.
     *
     * @return "by" field
     */
    public String getBy()
    {
        return by;
    }

    /**
     * Set "when" field.
     *
     * @param when string to set
     */
    public void setWhen(String when)
    {
        this.when = when;
    }

    /**
     * Get "when" field.
     *
     * @return "when" field
     */
    public String getWhen()
    {
        return when;
    }

    /**
     * Set "reason" field.
     *
     * @param reason string to set
     */
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    /**
     * Get "reason" field.
     *
     * @return "reason" field
     */
    public String getReason()
    {
        return reason;
    }

    /**
     * Constructor.
     *
     * @param elementName name of the element
     */
    public ExecutionPacketExtension(String elementName)
    {
        super(NAMESPACE, elementName);
    }

    /**
     * The child elements content.
     * @return the child elements content.
     */
    @Override
    public XmlStringBuilder getChildElementBuilder()
    {
        XmlStringBuilder xml = new XmlStringBuilder();

        xml.optElement(ELEMENT_BY, by);
        xml.optElement(ELEMENT_WHEN, when);
        xml.optElement(ELEMENT_REASON, reason);

        return xml;
    }
}
