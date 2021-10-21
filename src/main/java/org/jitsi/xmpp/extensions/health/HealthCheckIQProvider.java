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
package org.jitsi.xmpp.extensions.health;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;

import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * The <tt>IQProvider</tt> for {@link HealthCheckIQ}.
 *
 * @author Pawel Domas
 */
public class HealthCheckIQProvider
    extends IQProvider
{
    /**
     * Registers <tt>HealthCheckIQProvider</tt> as an <tt>IQProvider</tt>
     * in {@link AbstractSmackInteroperabilityLayer}.
     */
    public static void registerIQProvider()
    {
        // ColibriStatsIQ
        ProviderManager.addIQProvider(
            HealthCheckIQ.ELEMENT,
            HealthCheckIQ.NAMESPACE,
            new HealthCheckIQProvider());
    }

    /**
     * Parses <tt>HealthCheckIQ</tt>.
     *
     * {@inheritDoc}
     */
    @Override
    public IQ parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();
        IQ iq;

        if (HealthCheckIQ.ELEMENT.equals(parser.getName())
            && HealthCheckIQ.NAMESPACE.equals(namespace))
        {
            String rootElement = parser.getName();

            iq = new HealthCheckIQ();

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
