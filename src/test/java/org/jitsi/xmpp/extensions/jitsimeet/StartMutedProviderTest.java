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

import org.jivesoftware.smack.util.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class StartMutedProviderTest
{
    private final StartMutedProvider provider = new StartMutedProvider();

    @Test
    public void testParseSimple()
        throws Exception
    {
        String xml = "<startmuted xmlns='http://jitsi.org/jitmeet/start-muted' audio='true' video='false'/>";
        StartMutedPacketExtension ext = provider.parse(PacketParserUtils.getParserFor(xml));

        assertTrue(ext.getAudioMuted());
        assertFalse(ext.getVideoMuted());
    }

    @Test
    public void testParseWithChildElement()
    {
        String xml = "<startmuted xmlns='http://jitsi.org/jitmeet/start-muted'><x></x></startmuted>";

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            StartMutedPacketExtension ext = provider.parse(PacketParserUtils.getParserFor(xml));
            assertFalse(ext.getAudioMuted());
            assertFalse(ext.getVideoMuted());
        }, "Parsing failed");
    }
}
