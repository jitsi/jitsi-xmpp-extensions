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
package org.jitsi.xmpp.extensions.condesc;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Parses elements with the {@value ConferenceDescriptionExtension#NAMESPACE}
 * namespace.
 */
public class ConferenceDescriptionExtensionProvider
    extends ExtensionElementProvider<ConferenceDescriptionExtension>
{
    /**
     * Creates a <tt>ConferenceDescriptionPacketExtension</tt> by parsing
     * an XML document.
     * @param parser the parser to use.
     * @return the created <tt>ConferenceDescriptionPacketExtension</tt>.
     * @throws Exception
     */
    @Override
    public ConferenceDescriptionExtension parse(XmlPullParser parser, int depth)
            throws Exception
    {
        ConferenceDescriptionExtension packetExtension
                = new ConferenceDescriptionExtension();

        //first, set all attributes
        int attrCount = parser.getAttributeCount();

        for (int i = 0; i < attrCount; i++)
        {
            packetExtension.setAttribute(
                    parser.getAttributeName(i),
                    parser.getAttributeValue(i));
        }

        //now parse the sub elements
        boolean done = false;
        String elementName;
        TransportExtension transportExt = null;

        while (!done)
        {
            switch (parser.next())
            {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    if (TransportExtension.ELEMENT_NAME.equals(elementName))
                    {
                        String transportNs = parser.getNamespace();
                        if (transportNs != null)
                        {
                            transportExt = new TransportExtension(transportNs);
                        }
                    }

                    break;

                case XmlPullParser.END_TAG:
                    switch (parser.getName())
                    {
                        case ConferenceDescriptionExtension.ELEMENT_NAME:
                            done = true;
                            break;

                        case TransportExtension.ELEMENT_NAME:
                            if (transportExt != null)
                            {
                                packetExtension.addChildExtension(transportExt);
                            }
                            break;
                    }
            }
        }

        return packetExtension;
    }
}
