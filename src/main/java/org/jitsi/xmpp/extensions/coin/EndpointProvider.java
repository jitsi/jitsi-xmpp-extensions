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

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Parser for EndpointPacketExtension.
 *
 * @author Sebastien Vincent
 */
public class EndpointProvider
    extends ExtensionElementProvider<EndpointPacketExtension>
{
    /**
     * Parses a endpoint extension sub-packet and creates a {@link
     * EndpointPacketExtension} instance. At the beginning of the method
     * call, the xml parser will be positioned on the opening element of the
     * packet extension. As required by the smack API, at the end of the method
     * call, the parser will be positioned on the closing element of the packet
     * extension.
     *
     * @param parser an XML parser positioned at the opening
     * <tt>Endpoint</tt> element.
     *
     * @return a new {@link EndpointPacketExtension} instance.
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    @Override
    public EndpointPacketExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        boolean done = false;
        XmlPullParser.Event eventType;
        String elementName;
        String entity = parser.getAttributeValue("",
                EndpointPacketExtension.ENTITY_ATTR_NAME);
        StateType state = StateType.full;
        String stateStr = parser.getAttributeValue("",
                EndpointPacketExtension.STATE_ATTR_NAME);

        if (stateStr != null)
        {
            state = StateType.parseString(stateStr);
        }

        EndpointPacketExtension ext
            = new EndpointPacketExtension(entity);

        ext.setAttribute(EndpointPacketExtension.STATE_ATTR_NAME, state);

        while (!done)
        {
            eventType = parser.next();
            elementName = parser.getName();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
                switch (elementName)
                {
                case EndpointPacketExtension.ELEMENT_DISPLAY_TEXT:
                    ext.setDisplayText(ParsingUtils.parseText(parser));
                    break;
                case EndpointPacketExtension.ELEMENT_DISCONNECTION:
                    ext.setDisconnectionType(
                        DisconnectionType.parseString(parser.getText()));
                    break;
                case EndpointPacketExtension.ELEMENT_JOINING:
                    ext.setJoiningType(JoiningType.parseString(
                        ParsingUtils.parseText(parser)));
                    break;
                case EndpointPacketExtension.ELEMENT_STATUS:
                    ext.setStatus(EndpointStatusType.parseString(
                        ParsingUtils.parseText(parser)));
                    break;
                case CallInfoPacketExtension.ELEMENT:
                {
                    ExtensionElementProvider<CallInfoPacketExtension> provider
                        = new DefaultPacketExtensionProvider<>(
                        CallInfoPacketExtension.class);
                    CallInfoPacketExtension childExtension
                        = provider.parse(parser);
                    ext.addChildExtension(childExtension);
                    break;
                }
                case MediaPacketExtension.ELEMENT:
                {
                    MediaProvider provider = new MediaProvider();
                    MediaPacketExtension childExtension =
                        provider.parse(parser);
                    ext.addChildExtension(childExtension);
                    break;
                }
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                if (parser.getName().equals(
                        EndpointPacketExtension.ELEMENT))
                {
                    done = true;
                }
            }
        }

        return ext;
    }
}
