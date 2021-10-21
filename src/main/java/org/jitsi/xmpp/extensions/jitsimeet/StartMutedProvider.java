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
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * The parser of {@link StartMutedPacketExtension}
 *
 * @author Hristo Terezov
 */
public class StartMutedProvider
    extends ExtensionElementProvider<StartMutedPacketExtension>
{
    /**
     * Registers this extension provider into the <tt>ProviderManager</tt>.
     */
    public static void registerStartMutedProvider()
    {
        ProviderManager.addExtensionProvider(
            StartMutedPacketExtension.ELEMENT,
            StartMutedPacketExtension.NAMESPACE,
            new StartMutedProvider());
    }

    @Override
    public StartMutedPacketExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        StartMutedPacketExtension packetExtension
            = new StartMutedPacketExtension();

        //now parse the sub elements
        boolean done = false;
        String elementName;
        while (!done)
        {
            switch (parser.getEventType())
            {
            case START_ELEMENT:
            {
                elementName = parser.getName();
                if (StartMutedPacketExtension.ELEMENT.equals(
                    elementName))
                {
                    boolean audioMute = Boolean.parseBoolean(
                        parser.getAttributeValue("",
                            StartMutedPacketExtension.AUDIO_ATTRIBUTE_NAME));
                    boolean videoMute = Boolean.parseBoolean(
                        parser.getAttributeValue("",
                            StartMutedPacketExtension.VIDEO_ATTRIBUTE_NAME));

                    packetExtension.setAudioMute(audioMute);
                    packetExtension.setVideoMute(videoMute);
                }
                parser.next();
                break;
            }
            case END_ELEMENT:
            {
                elementName = parser.getName();
                if (StartMutedPacketExtension.ELEMENT.equals(
                    elementName))
                {
                    done = true;
                }
                break;
            }
            default:
                parser.next();
            }
        }
        return packetExtension;
    }
}
