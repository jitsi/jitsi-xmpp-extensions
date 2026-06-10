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

import org.jitsi.utils.*;
import org.jivesoftware.smack.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MediaSourceTest
{
    private final MediaSource.Provider provider = new MediaSource.Provider();

    @Test
    public void parsingTest()
            throws Exception
    {
        MediaSource mediaSource = provider.parse(PacketParserUtils.getParserFor("<media-source type='audio' id='id'/>"));
        assertNotNull(mediaSource);

        assertThrows(
                Exception.class,
                () -> provider.parse(PacketParserUtils.getParserFor("<media-source type='audio'/>")),
                "Missing ID must result in an exception"
        );

        assertThrows(
                Exception.class,
                () -> provider.parse(PacketParserUtils.getParserFor("<media-source id='id'/>")),
                "Missing type must result in an exception"
        );

        assertThrows(
                Exception.class,
                () -> provider.parse(PacketParserUtils.getParserFor("<media-source type='invalid' id='id'/>")),
                "An invalid type must result in an exception"
        );
    }

    @Test
    public void syntheticTest()
            throws Exception
    {
        MediaSource notSynthetic =
                provider.parse(PacketParserUtils.getParserFor("<media-source type='audio' id='id'/>"));
        assertFalse(notSynthetic.isSynthetic(), "Synthetic must default to false when the attribute is absent");

        MediaSource synthetic =
                provider.parse(PacketParserUtils.getParserFor("<media-source type='audio' id='id' synthetic='true'/>"));
        assertTrue(synthetic.isSynthetic());

        MediaSource built = MediaSource.getBuilder()
                .setType(MediaType.AUDIO)
                .setId("id")
                .setSynthetic(true)
                .build();
        assertTrue(built.isSynthetic());

        // The attribute is only emitted when true, so it round-trips through XML.
        MediaSource reparsed = provider.parse(PacketParserUtils.getParserFor(built.toXML().toString()));
        assertTrue(reparsed.isSynthetic());
    }
}
