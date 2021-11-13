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
package org.jitsi.xmpp.extensions.colibri;

import java.io.*;
import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;
import org.jivesoftware.smack.xml.XmlPullParser.*;

/**
 * Implements an {@link IqProvider} for the Jitsi Videobridge extension {@link
 * ShutdownIQ}.
 *
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class ShutdownIqProvider
    extends IqProvider<ShutdownIQ>
{
    /** Initializes a new <tt>ColibriIQProvider</tt> instance. */
    public ShutdownIqProvider()
    {
        // Shutdown IQ
        ProviderManager.addIQProvider(
            ShutdownIQ.GRACEFUL_ELEMENT_NAME,
            ShutdownIQ.NAMESPACE,
            this);
        ProviderManager.addIQProvider(
            ShutdownIQ.FORCE_ELEMENT_NAME,
            ShutdownIQ.NAMESPACE,
            this);

        // Shutdown extension
        ExtensionElementProvider<ColibriConferenceIQ.GracefulShutdown>
            shutdownProvider = new DefaultPacketExtensionProvider<>(
            ColibriConferenceIQ.GracefulShutdown.class);

        ProviderManager.addExtensionProvider(
            ColibriConferenceIQ.GracefulShutdown.ELEMENT,
            ColibriConferenceIQ.GracefulShutdown.NAMESPACE,
            shutdownProvider);
    }

    /**
     * Parses an IQ sub-document and creates an
     * <tt>org.jivesoftware.smack.packet.IQ</tt> instance.
     *
     * @param parser an <tt>XmlPullParser</tt> which specifies the IQ
     * sub-document to be parsed into a new <tt>IQ</tt> instance
     * @return a new <tt>IQ</tt> instance parsed from the specified IQ
     * sub-document
     */
    public ShutdownIQ parse(XmlPullParser parser, int initialDepth, IqData data, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();
        ShutdownIQ iq;

        if (ShutdownIQ.NAMESPACE.equals(namespace) &&
                 ShutdownIQ.isValidElementName(parser.getName()))
        {
            String rootElement = parser.getName();

            iq = ShutdownIQ.createShutdownIQ(rootElement);

            boolean done = false;

            while (!done)
            {
                if (parser.next() == Event.END_ELEMENT)
                {
                    String name = parser.getName();

                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                }
            }
        }
        else
            iq = null;

        return iq;
    }
}
