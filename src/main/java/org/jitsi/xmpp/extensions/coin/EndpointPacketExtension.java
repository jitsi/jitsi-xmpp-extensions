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
 * Endpoint packet extension.
 *
 * @author Sebastien Vincent
 */
public class EndpointPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that endpoint belongs to.
     */
    public static final String NAMESPACE = null;

    /**
     * The name of the element that contains the endpoint data.
     */
    public static final String ELEMENT_NAME = "endpoint";

    /**
     * Entity attribute name.
     */
    public static final String ENTITY_ATTR_NAME = "entity";

    /**
     * Entity attribute name.
     */
    public static final String STATE_ATTR_NAME = "state";

    /**
     * Display text element name.
     */
    public static final String ELEMENT_DISPLAY_TEXT = "display-text";

    /**
     * Status element name.
     */
    public static final String ELEMENT_STATUS = "status";

    /**
     * Disconnection element name.
     */
    public static final String ELEMENT_DISCONNECTION = "disconnection-method";

    /**
     * Joining element name.
     */
    public static final String ELEMENT_JOINING = "joining-method";

    /**
     * Display text.
     */
    private String displayText = null;

    /**
     * Status.
     */
    private EndpointStatusType status = null;

    /**
     * Disconnection type.
     */
    private DisconnectionType disconnectionType = null;

    /**
     * Joining type.
     */
    private JoiningType joiningType = null;

    /**
     * Constructor.
     *
     * @param entity entity
     */
    public EndpointPacketExtension(String entity)
    {
        super(NAMESPACE, ELEMENT_NAME);
        setAttribute("entity", entity);
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
     * Set status.
     *
     * @param status status
     */
    public void setStatus(EndpointStatusType status)
    {
        this.status = status;
    }

    /**
     * Set disconnection type.
     *
     * @param disconnectionType disconnection type.
     */
    public void setDisconnectionType(DisconnectionType disconnectionType)
    {
        this.disconnectionType = disconnectionType;
    }

    /**
     * Set joining type.
     *
     * @param joiningType joining type.
     */
    public void setJoiningType(JoiningType joiningType)
    {
        this.joiningType = joiningType;
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
     * Get status.
     *
     * @return status.
     */
    public EndpointStatusType getStatus()
    {
        return status;
    }

    /**
     * Get disconnection type.
     *
     * @return disconnection type.
     */
    public DisconnectionType getDisconnectionType()
    {
        return disconnectionType;
    }

    /**
     * Get joining type.
     *
     * @return joining type.
     */
    public JoiningType getJoiningType()
    {
        return joiningType;
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
        xml.optElement(ELEMENT_STATUS, status);
        xml.optElement(ELEMENT_DISCONNECTION, disconnectionType);
        xml.optElement(ELEMENT_JOINING, joiningType);

        return xml;
    }
}
