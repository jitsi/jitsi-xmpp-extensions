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

import org.jitsi.utils.logging2.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;
import java.util.*;

/**
 * Utility class for parsing Colibri2 IQs.
 */
public class IqProviderUtils
{
    private static final Logger logger = new LoggerImpl(IqProviderUtils.class.getName());

    public static void parseExtensions(XmlPullParser parser, int initialDepth, IQ iq)
            throws XmlPullParserException, IOException, SmackParsingException
    {
        for (ExtensionElement ext : parseExtensions(parser, initialDepth))
        {
            iq.addExtension(ext);
        }
    }

    public static List<ExtensionElement> parseExtensions(XmlPullParser parser, int initialDepth)
            throws XmlPullParserException, IOException, SmackParsingException
    {
        List<ExtensionElement> ret = new ArrayList<>();
        while (true)
        {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType)
            {
            case START_ELEMENT:
                String tagName = parser.getName();
                String namespace = parser.getNamespace();

                ExtensionElement extension = parseExtension(parser, tagName, namespace);

                if (extension != null)
                {
                    ret.add(extension);
                }
                break;

            case END_ELEMENT:
                if (parser.getDepth() == initialDepth)
                {
                    return ret;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
    }

    private static ExtensionElement parseExtension(XmlPullParser parser, String name, String namespace)
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
            logger.warn(() -> "Should parse {" + namespace + "}:" + name + ", but found no extension provider");
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
     * Parses using a specific {@link XmlPullParser} and ignores XML content
     * presuming that the specified <tt>parser</tt> is currently at the start
     * tag of an element with a specific name and throwing away until the end
     * tag with the specified name is encountered.
     *
     * @param parser the {@link XmlPullParser} which parses the XML content
     * @param name the name of the element at the start tag of which the
     * specified <tt>parser</tt> is presumed to currently be and until the end
     * tag of which XML content is to be thrown away
     * @throws Exception if an errors occurs while parsing the XML content
     */
    private static void throwAway(XmlPullParser parser, String name)
        throws XmlPullParserException, IOException
    {
        int initialDepth = parser.getDepth();
        while ((XmlPullParser.Event.END_ELEMENT != parser.next())
            || !name.equals(parser.getName())
            || parser.getDepth() != initialDepth)
        {
            /* Do nothing */
        }
    }

    /** Whether the colibri2 extension and IQ providers have been registered with Smack's ProviderManager. */
    private static boolean providersRegistered = false;

    public static void registerProviders()
    {
        synchronized (IqProviderUtils.class)
        {
            if (!providersRegistered)
            {
                doRegisterProviders();
                providersRegistered = true;
            }
        }
    }

    private static void doRegisterProviders()
    {
        ProviderManager.addIQProvider(ConferenceModifyIQ.ELEMENT, ConferenceModifyIQ.NAMESPACE,
            new ConferenceModifyIQProvider());
        ProviderManager.addIQProvider(ConferenceModifiedIQ.ELEMENT, ConferenceModifiedIQ.NAMESPACE,
            new ConferenceModifiedIQProvider());
        ProviderManager.addIQProvider(
                ConferenceNotificationIQ.ELEMENT,
                ConferenceNotificationIQ.NAMESPACE,
                new ConferenceNotificationIQ.Provider());

        ProviderManager.addExtensionProvider(Colibri2Endpoint.ELEMENT, Colibri2Endpoint.NAMESPACE,
            new Colibri2Endpoint.Provider());
        ProviderManager.addExtensionProvider(Colibri2Relay.ELEMENT, Colibri2Relay.NAMESPACE,
            new Colibri2Relay.Provider());

        ProviderManager.addExtensionProvider(Endpoints.ELEMENT, Endpoints.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Endpoints.class));

        ProviderManager.addExtensionProvider(Sources.ELEMENT, Sources.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Sources.class));
        ProviderManager.addExtensionProvider(MediaSource.ELEMENT, MediaSource.NAMESPACE,
            new MediaSource.Provider());

        ProviderManager.addExtensionProvider(Media.ELEMENT, Media.NAMESPACE,
            new Media.Provider());
        ProviderManager.addExtensionProvider(Transport.ELEMENT, Transport.NAMESPACE,
            new DefaultPacketExtensionProvider<>(Transport.class));

        ProviderManager.addExtensionProvider(ParameterPacketExtension.ELEMENT, ColibriConferenceIQ.NAMESPACE,
            new DefaultPacketExtensionProvider<>(ParameterPacketExtension.class));

        // Colibri2 shares extensions with jingle. Instantiating JingleIQProvider registers the extensions, but not
        // Jingle provider itself.
        new JingleIQProvider();

        /* Original colibri does something weird with these elements' namespaces, so register them here. */
        ProviderManager.addExtensionProvider(ForceMute.ELEMENT, ForceMute.NAMESPACE, new ForceMute.Provider());
        ProviderManager.addExtensionProvider(InitialLastN.ELEMENT, InitialLastN.NAMESPACE, new InitialLastNProvider());
        ProviderManager.addExtensionProvider(Connect.ELEMENT, Connect.NAMESPACE, new ConnectProvider());
        ProviderManager.addExtensionProvider(Connects.ELEMENT, Connects.NAMESPACE, new ConnectsProvider());
        ProviderManager.addExtensionProvider(Capability.ELEMENT, Capability.NAMESPACE, new Capability.Provider());
        ProviderManager.addExtensionProvider(Notification.ELEMENT, Notification.NAMESPACE, new Notification.Provider());
        ProviderManager.addExtensionProvider(Sctp.ELEMENT, Sctp.NAMESPACE, new Sctp.Provider());
        ProviderManager.addExtensionProvider(Colibri2Error.ELEMENT,
                Colibri2Error.NAMESPACE,
                new Colibri2Error.Provider());
    }
}
