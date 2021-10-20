/*
 * Copyright @ 2021 - present 8x8, Inc.
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

package org.jitsi.xmpp.extensions.colibri2;

import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Provider for Colibri2 IQs.
 */
public class ConferenceModifyIQProvider
    extends IqProvider<ConferenceModifyIQ>
{
    @Override
    public ConferenceModifyIQ parse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        ConferenceModifyIQ.Builder builder = ConferenceModifyIQ.builder(iqData);

        String meetingId = parser.getAttributeValue(AbstractConferenceModificationIQ.MEETING_ID_ATTR_NAME);
        if (meetingId != null)
        {
            builder.setMeetingId(meetingId);
        }

        String conferenceName = parser.getAttributeValue(AbstractConferenceModificationIQ.NAME_ATTR_NAME);
        if (conferenceName != null)
        {
            builder.setConferenceName(conferenceName);
        }

        ConferenceModifyIQ iq = builder.build();

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String tagName = parser.getName();
                String namespace = parser.getNamespace();

                ExtensionElement extension
                    = parseExtension(parser, tagName, namespace);

                if (extension != null)
                {
                    iq.addExtension(extension);
                }
                break;

            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }

        return iq;
    }

    private ExtensionElement parseExtension(XmlPullParser parser, String name, String namespace)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        ExtensionElementProvider<ExtensionElement> extensionProvider =
            ProviderManager.getExtensionProvider(name, namespace);
        ExtensionElement extension;

        if (extensionProvider == null)
        {
            /*
             * No ExtensionElementProvider for the specified name and namespace
             * has been registered. Throw away the element.
             */
            throwAway(parser, name);
            extension = null;
        }
        else
        {
            extension = extensionProvider.parse(parser);
        }
        return extension;
    }

    /**
     * Parses using a specific <tt>XmlPullParser</tt> and ignores XML content
     * presuming that the specified <tt>parser</tt> is currently at the start
     * tag of an element with a specific name and throwing away until the end
     * tag with the specified name is encountered.
     *
     * @param parser the <tt>XmlPullParser</tt> which parses the XML content
     * @param name the name of the element at the start tag of which the
     * specified <tt>parser</tt> is presumed to currently be and until the end
     * tag of which XML content is to be thrown away
     * @throws Exception if an errors occurs while parsing the XML content
     */
    private void throwAway(XmlPullParser parser, String name)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        int initialDepth = parser.getDepth();
        while ((XmlPullParser.Event.END_ELEMENT != parser.next())
            || !name.equals(parser.getName())
            || parser.getDepth() != initialDepth)
        {
            /* Do nothing */
        }
    }

    public static void registerProviders()
    {
        ProviderManager.addIQProvider(ConferenceModifyIQ.ELEMENT, ConferenceModifyIQ.NAMESPACE,
            new ConferenceModifyIQProvider());

        ProviderManager.addExtensionProvider(Endpoint.ELEMENT, Endpoint.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Endpoint.class));
        ProviderManager.addExtensionProvider(Relay.ELEMENT, Relay.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Relay.class));

        ProviderManager.addExtensionProvider(Endpoints.ELEMENT, Endpoints.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Endpoints.class));
        ProviderManager.addExtensionProvider(EndpointAdd.ELEMENT, EndpointAdd.NAMESPACE,
            new DefaultPacketExtensionProvider<>(EndpointAdd.class));
        ProviderManager.addExtensionProvider(EndpointRemove.ELEMENT, EndpointRemove.NAMESPACE,
            new DefaultPacketExtensionProvider<>(EndpointRemove.class));

        ProviderManager.addExtensionProvider(Sources.ELEMENT, Sources.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Sources.class));
        ProviderManager.addExtensionProvider(MediaSource.ELEMENT, MediaSource.NAMESPACE,
            new DefaultPacketExtensionProvider<>(MediaSource.class));

        ProviderManager.addExtensionProvider(Media.ELEMENT, Media.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Media.class));
        ProviderManager.addExtensionProvider(Transport.ELEMENT, Transport.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Transport.class));

        /* Colibri2 shares extensions with original colibri, so register both. */
        ProviderManager.addIQProvider(ColibriConferenceIQ.ELEMENT_NAME, ColibriConferenceIQ.NAMESPACE,
            new ColibriIQProvider());

        /* Original colibri does something weird with these elements' namespaces, so register them here. */
        ProviderManager.addExtensionProvider(PayloadTypePacketExtension.ELEMENT_NAME,
            PayloadTypePacketExtension.NAMESPACE,
            new DefaultPacketExtensionProvider<>(PayloadTypePacketExtension.class));
        ProviderManager.addExtensionProvider(RTPHdrExtPacketExtension.ELEMENT_NAME,
            RTPHdrExtPacketExtension.NAMESPACE,
            new DefaultPacketExtensionProvider<>(RTPHdrExtPacketExtension.class));
    }
}
