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

import org.jivesoftware.smack.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class Colibri2EndpointTest
{
    @Test
    public void diarizeRoundTripTest()
            throws Exception
    {
        Colibri2Endpoint.Provider provider = new Colibri2Endpoint.Provider();

        // Set via builder -> serialize -> parse -> getDiarize() == true
        Colibri2Endpoint.Builder builder = Colibri2Endpoint.getBuilder();
        builder.setId("endpoint-id");
        builder.setDiarize(true);
        Colibri2Endpoint built = builder.build();
        assertEquals(Boolean.TRUE, built.getDiarize());

        Colibri2Endpoint parsed = provider.parse(PacketParserUtils.getParserFor(built.toXML().toString()));
        assertEquals(Boolean.TRUE, parsed.getDiarize());

        // diarize=false round-trips as false.
        Colibri2Endpoint.Builder builderFalse = Colibri2Endpoint.getBuilder();
        builderFalse.setId("endpoint-id");
        builderFalse.setDiarize(false);
        Colibri2Endpoint builtFalse = builderFalse.build();
        assertEquals(Boolean.FALSE, builtFalse.getDiarize());
        assertEquals(Boolean.FALSE,
                provider.parse(PacketParserUtils.getParserFor(builtFalse.toXML().toString())).getDiarize());
    }

    @Test
    public void diarizeAbsentTest()
            throws Exception
    {
        Colibri2Endpoint.Provider provider = new Colibri2Endpoint.Provider();

        // Absent attribute -> null (fall back to default).
        Colibri2Endpoint.Builder builder = Colibri2Endpoint.getBuilder();
        builder.setId("endpoint-id");
        Colibri2Endpoint built = builder.build();
        assertNull(built.getDiarize());

        Colibri2Endpoint parsed = provider.parse(PacketParserUtils.getParserFor(built.toXML().toString()));
        assertNull(parsed.getDiarize());
    }
}
