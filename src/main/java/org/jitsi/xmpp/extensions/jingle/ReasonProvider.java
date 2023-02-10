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

import org.jitsi.xmpp.extensions.coin.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * The <tt>ReasonProvider</tt> parses "reason" elements into {@link
 * ReasonPacketExtension} instances.
 *
 * @author Emil Ivov
 */
public class ReasonProvider extends ExtensionElementProvider<ReasonPacketExtension>
{

    /**
     * Parses a reason extension sub-packet and creates a {@link
     * ReasonPacketExtension} instance. At the beginning of the method call,
     * the xml parser will be positioned on the opening element of the packet
     * extension. As required by the smack API, at the end of the method call,
     * the parser will be positioned on the closing element of the packet
     * extension.
     *
     * @param parser an XML parser positioned at the opening <tt>reason</tt>
     * element.
     *
     * @return a new {@link ReasonPacketExtension} instance.
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    @Override
    public ReasonPacketExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String text = null;
        Reason reason = null;

        boolean done = false;

        XmlPullParser.Event eventType;
        String elementName;

        while (!done)
        {
            eventType = parser.next();
            elementName = parser.getName();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
                // the reason itself.
                if ( reason == null)
                {
                    //let the parse exception fly as it would mean we have
                    //some weird element first in the list.
                    reason = Reason.parseString(elementName);
                }
                else if (elementName.equals(
                                ReasonPacketExtension.TEXT_ELEMENT))
                {
                    text = ParsingUtils.parseText(parser);
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                if (parser.getName().equals(ReasonPacketExtension.ELEMENT))
                {
                    done = true;
                }
            }
        }

        return new ReasonPacketExtension(reason, text, null);

    }

}
