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
package org.jitsi.xmpp.extensions.jingleinfo;

import org.jitsi.xmpp.extensions.coin.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Parser for {@link RelayPacketExtension}.
 *
 * @author Sebastien Vincent
 */
public class RelayProvider
    extends ExtensionElementProvider<RelayPacketExtension>
{
    /**
     * Parses a users extension sub-packet and creates a {@link
     * RelayPacketExtension} instance. At the beginning of the method
     * call, the xml parser will be positioned on the opening element of the
     * packet extension. As required by the smack API, at the end of the method
     * call, the parser will be positioned on the closing element of the packet
     * extension.
     *
     * @param parser an XML parser positioned at the opening
     * <tt>Server</tt> element.
     *
     * @return a new {@link RelayPacketExtension} instance.
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    @Override
    public RelayPacketExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        boolean done = false;
        XmlPullParser.Event eventType;
        String elementName;
        RelayPacketExtension ext = new RelayPacketExtension();

        while (!done)
        {
            eventType = parser.next();
            elementName = parser.getName();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
                if (elementName.equals(ServerPacketExtension.ELEMENT))
                {
                    ExtensionElementProvider<?> provider = ProviderManager.getExtensionProvider(
                        ServerPacketExtension.ELEMENT,
                        ServerPacketExtension.NAMESPACE);
                    ext.addChildExtension(provider.parse(parser));
                }
                else if (elementName.equals("token"))
                {
                    ext.setToken(ParsingUtils.parseText(parser));
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                if (parser.getName().equals(RelayPacketExtension.ELEMENT))
                {
                    done = true;
                }
            }
        }

        return ext;
    }
}
