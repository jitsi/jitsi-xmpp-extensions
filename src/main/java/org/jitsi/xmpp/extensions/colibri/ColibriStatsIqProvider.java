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

/**
 * Implements an {@link IqProvider} for the Jitsi Videobridge extension {@link
 * ColibriStatsIQ}.
 *
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class ColibriStatsIqProvider
    extends IqProvider<ColibriStatsIQ>
{
    /** Initializes a new <tt>ColibriIQProvider</tt> instance. */
    public ColibriStatsIqProvider()
    {
        // ColibriStatsIQ
        ProviderManager.addIQProvider(
            ColibriStatsIQ.ELEMENT,
            ColibriStatsIQ.NAMESPACE,
            this);

        // ColibriStatsExtension
        ProviderManager.addExtensionProvider(
            ColibriStatsExtension.ELEMENT,
            ColibriStatsExtension.NAMESPACE,
            new DefaultPacketExtensionProvider<>(ColibriStatsExtension.class));

        // ColibriStatsExtension.Stat
        ProviderManager.addExtensionProvider(
            ColibriStatsExtension.Stat.ELEMENT,
            ColibriStatsExtension.NAMESPACE,
            new DefaultPacketExtensionProvider<>(ColibriStatsExtension.Stat.class));
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
    public ColibriStatsIQ parse(XmlPullParser parser, int initialDepth, IqData data, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();
        ColibriStatsIQ iq;

        if (ColibriStatsIQ.ELEMENT.equals(parser.getName())
            && ColibriStatsIQ.NAMESPACE.equals(namespace))
        {
            String rootElement = parser.getName();

            ColibriStatsIQ statsIQ = new ColibriStatsIQ();
            iq = statsIQ;
            ColibriStatsExtension.Stat stat = null;

            boolean done = false;

            while (!done)
            {
                switch (parser.next())
                {
                    case START_ELEMENT:
                    {
                        String name = parser.getName();

                        if (ColibriStatsExtension.Stat.ELEMENT.equals(name))
                        {
                            stat = new ColibriStatsExtension.Stat();

                            String statName
                                = parser.getAttributeValue(
                                    "",
                                    ColibriStatsExtension.Stat.NAME_ATTR_NAME);
                            stat.setName(statName);

                            String statValue
                                = parser.getAttributeValue(
                                    "",
                                    ColibriStatsExtension.Stat.VALUE_ATTR_NAME);
                            stat.setValue(statValue);
                        }
                        break;
                    }
                    case END_ELEMENT:
                    {
                        String name = parser.getName();

                        if (rootElement.equals(name))
                        {
                            done = true;
                        }
                        else if (ColibriStatsExtension.Stat.ELEMENT
                            .equals(name))
                        {
                            if (stat != null)
                            {
                                statsIQ.addStat(stat);
                                stat = null;
                            }
                        }
                        break;
                    }
                }
            }
        }
        else
            iq = null;

        return iq;
    }
}
