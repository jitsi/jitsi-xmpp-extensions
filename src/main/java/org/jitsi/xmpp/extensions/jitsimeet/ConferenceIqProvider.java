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
package org.jitsi.xmpp.extensions.jitsimeet;


import org.apache.commons.lang3.StringUtils;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;

import org.jivesoftware.smack.xml.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.jid.parts.*;
import org.jxmpp.stringprep.*;

import java.io.*;

/**
 * Provider handles parsing of {@link ConferenceIq} and {@link LoginUrlIq}
 * stanzas and converting objects back to their XML representation.
 *
 * @author Pawel Domas
 */
public class ConferenceIqProvider
    extends SafeParseIqProvider<ConferenceIq>
{
    /**
     * Creates new instance of <tt>ConferenceIqProvider</tt>.
     */
    public ConferenceIqProvider()
    {
        // <conference>
        ProviderManager.addIQProvider(
                ConferenceIq.ELEMENT, ConferenceIq.NAMESPACE, this);
    }

    @Override
    protected ConferenceIq doParse(XmlPullParser parser)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!ConferenceIq.NAMESPACE.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();

        ConferenceIq iq;
        if (ConferenceIq.ELEMENT.equals(rootElement))
        {
            iq = new ConferenceIq();
            EntityBareJid room
                = getRoomJid(
                    parser.getAttributeValue("", ConferenceIq.ROOM_ATTR_NAME));

            iq.setRoom(room);
            String vnode = parser.getAttributeValue("", ConferenceIq.VNODE_ATTR_NAME);
            if (StringUtils.isNotEmpty(vnode))
            {
                iq.setVnode(vnode);
            }

            String token = parser.getAttributeValue("", ConferenceIq.TOKEN_ATTR_NAME);
            if (StringUtils.isNotEmpty(token))
            {
                iq.setToken(token);
            }

            String ready
                = parser.getAttributeValue("", ConferenceIq.READY_ATTR_NAME);
            if (StringUtils.isNotEmpty(ready))
            {
                iq.setReady(Boolean.valueOf(ready));
            }
            String focusJid
                = parser.getAttributeValue(
                        "", ConferenceIq.FOCUS_JID_ATTR_NAME);
            if (StringUtils.isNotEmpty(focusJid))
            {
                iq.setFocusJid(focusJid);
            }
            String sessionId
                = parser.getAttributeValue(
                        "", ConferenceIq.SESSION_ID_ATTR_NAME);
            if (StringUtils.isNotEmpty(sessionId))
            {
                iq.setSessionId(sessionId);
            }
            String machineUID = parser.getAttributeValue(
                    "", ConferenceIq.MACHINE_UID_ATTR_NAME);
            if (StringUtils.isNotEmpty(machineUID))
            {
                iq.setMachineUID(machineUID);
            }
            String identity = parser.getAttributeValue(
                    "", ConferenceIq.IDENTITY_ATTR_NAME);
            if (StringUtils.isNotEmpty(identity))
            {
                iq.setIdentity(identity);
            }
        }
        else
        {
            return null;
        }

        ConferenceIq.Property property = null;

        boolean done = false;

        while (!done)
        {
            switch (parser.next())
            {
                case END_ELEMENT:
                {
                    String name = parser.getName();

                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                    else if (ConferenceIq.Property.ELEMENT.equals(name))
                    {
                        if (property != null)
                        {
                            iq.addProperty(property);
                            property = null;
                        }
                    }
                    break;
                }

                case START_ELEMENT:
                {
                    String name = parser.getName();

                    if (ConferenceIq.Property.ELEMENT.equals(name))
                    {
                        property = new ConferenceIq.Property();

                        // Name
                        String propName
                            = parser.getAttributeValue(
                                    "",
                                    ConferenceIq.Property.NAME_ATTR_NAME);
                        if (StringUtils.isNotEmpty(propName))
                        {
                            property.setName(propName);
                        }

                        // Value
                        String propValue
                            = parser.getAttributeValue(
                                    "",
                                    ConferenceIq.Property.VALUE_ATTR_NAME);
                        if (StringUtils.isNotEmpty(propValue))
                        {
                            property.setValue(propValue);
                        }
                    }
                }
            }
        }

        return iq;
    }

    /**
     * Constructs the jid for the room by taking the last '@' part as domain
     * and everything before it as the node part. Doing validation on the node
     * part for allowed chars.
     *
     * @param unescapedValue the unescaped jid as received in the iq
     * @return a bare JID constructed from the given parts.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityBareJid getRoomJid(String unescapedValue)
        throws XmppStringprepException
    {
        // the node part of the jid may contain '@' which is not allowed
        // and passing the correct node value to Localpart.from will check
        // for all not allowed jid characters
        int ix = unescapedValue == null ? -1 : unescapedValue.lastIndexOf("@");

        if (ix == -1)
        {
            throw new XmppStringprepException(unescapedValue, "wrong room name jid format");
        }

        String domainPart = unescapedValue.substring(ix + 1);
        String localPart = unescapedValue.substring(0, ix);

        // Work around bug in stringprep in jxmpp 1.0.1 - it throws ArrayOutOfBoundsException in this case.
        if (localPart.contains("@"))
        {
            throw new XmppStringprepException(unescapedValue, "Localpart must not contain '@'");
        }

        return JidCreate.entityBareFrom(Localpart.from(localPart), Domainpart.from(domainPart));
    }
}
