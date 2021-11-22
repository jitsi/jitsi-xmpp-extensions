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
 * An implementation of a Coin IQ provider that parses incoming Coin IQs.
 *
 * @author Sebastien Vincent
 */
public class CoinIQProvider
    extends IqProvider<CoinIQ>
{
    /**
     * Provider for description packet extension.
     */
    private final DescriptionProvider descriptionProvider
        = new DescriptionProvider();

    /**
     * Provider for users packet extension.
     */
    private final UsersProvider usersProvider = new UsersProvider();

    /**
     * Provider for state packet extension.
     */
    private final StateProvider stateProvider = new StateProvider();

    /**
     * Provider for URIs packet extension.
     */
    private final DefaultPacketExtensionProvider<URIsPacketExtension>
       urisProvider = new DefaultPacketExtensionProvider<>(
               URIsPacketExtension.class);

    /**
     * Provider for sidebars by val packet extension.
     */
    private final DefaultPacketExtensionProvider<SidebarsByValPacketExtension>
       sidebarsByValProvider =
           new DefaultPacketExtensionProvider<>(
                   SidebarsByValPacketExtension.class);

    /**
     * Constructor.
     */
    public CoinIQProvider()
    {
        ProviderManager.addExtensionProvider(
                UserRolesPacketExtension.ELEMENT,
                UserRolesPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(UserRolesPacketExtension.class));

        ProviderManager.addExtensionProvider(
                URIPacketExtension.ELEMENT,
                URIPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(URIPacketExtension.class));

        ProviderManager.addExtensionProvider(
                SIPDialogIDPacketExtension.ELEMENT,
                SIPDialogIDPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(SIPDialogIDPacketExtension.class));

        ProviderManager.addExtensionProvider(
                ConferenceMediumPacketExtension.ELEMENT,
                ConferenceMediumPacketExtension.NAMESPACE,
                new ConferenceMediumProvider());

        ProviderManager.addExtensionProvider(
                ConferenceMediaPacketExtension.ELEMENT,
                ConferenceMediaPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                        <>(ConferenceMediaPacketExtension.class));

        ProviderManager.addExtensionProvider(
                CallInfoPacketExtension.ELEMENT,
                CallInfoPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                        <>(CallInfoPacketExtension.class));
    }

    /**
     * Parse the Coin IQ sub-document and returns the corresponding
     * <tt>CoinIQ</tt>.
     *
     * @param parser XML parser
     * @return <tt>CoinIQ</tt>
     * @throws Exception if something goes wrong during parsing
     */
    @Override
    public CoinIQ parse(XmlPullParser parser, int initialDepth, IqData data, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        CoinIQ coinIQ = new CoinIQ();

        String entity = parser
            .getAttributeValue("", CoinIQ.ENTITY_ATTR_NAME);
        String version = parser.getAttributeValue("", CoinIQ.VERSION_ATTR_NAME);
        StateType state = StateType.full;
        String stateStr = parser.getAttributeValue("",
                EndpointPacketExtension.STATE_ATTR_NAME);
        String sid = parser.getAttributeValue("", CoinIQ.SID_ATTR_NAME);

        if (stateStr != null)
        {
            state = StateType.parseString(stateStr);
        }

        coinIQ.setEntity(entity);
        coinIQ.setVersion(Integer.parseInt(version));
        coinIQ.setState(state);
        coinIQ.setSID(sid);

        // Now go on and parse the jingle element's content.
        XmlPullParser.Event eventType;
        String elementName;
        boolean done = false;

        while (!done)
        {
            eventType = parser.next();
            elementName = parser.getName();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
                switch (elementName)
                {
                case DescriptionPacketExtension.ELEMENT:
                {
                    coinIQ.addExtension(descriptionProvider.parse(parser));
                    break;
                }
                case UsersPacketExtension.ELEMENT:
                {
                    coinIQ.addExtension(usersProvider.parse(parser));
                    break;
                }
                case StatePacketExtension.ELEMENT:
                {
                    coinIQ.addExtension(stateProvider.parse(parser));
                    break;
                }
                case URIsPacketExtension.ELEMENT:
                {
                    coinIQ.addExtension(urisProvider.parse(parser));
                    break;
                }
                case SidebarsByValPacketExtension.ELEMENT:
                {
                    coinIQ.addExtension(sidebarsByValProvider.parse(parser));
                    break;
                }
                }
            }

            if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                if (parser.getName().equals(CoinIQ.ELEMENT))
                {
                    done = true;
                }
            }
        }

        return coinIQ;
    }

    /**
     * Returns the content of the next {@link XmlPullParser.Event#TEXT_CHARACTERS} element that
     * we encounter in <tt>parser</tt>.
     *
     * @param parser the parse that we'll be probing for text.
     *
     * @return the content of the next {@link XmlPullParser.Event#TEXT_CHARACTERS} element we
     * come across or <tt>null</tt> if we encounter a closing tag first.
     *
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    public static String parseText(XmlPullParser parser)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        boolean done = false;

        XmlPullParser.Event eventType;
        String text = null;

        while (!done)
        {
            eventType = parser.next();

            if (eventType == XmlPullParser.Event.TEXT_CHARACTERS)
            {
                text = parser.getText();
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                done = true;
            }
        }

        return text;
    }
}
