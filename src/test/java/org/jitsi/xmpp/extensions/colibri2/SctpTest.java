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

import org.jivesoftware.smack.parsing.*;
import org.junit.jupiter.api.*;

import static org.jivesoftware.smack.util.PacketParserUtils.getParserFor;
import static org.junit.jupiter.api.Assertions.*;

public class SctpTest
{
    @Test
    public void parsingTest()
            throws Exception
    {
        Sctp.Provider provider = new Sctp.Provider();

        Sctp sctp = provider.parse(getParserFor("<sctp/>"));
        assertNull(sctp.getPort());
        assertNull(sctp.getRole());

        sctp = provider.parse(getParserFor("<sctp role='server'/>"));
        assertEquals(Sctp.Role.SERVER, sctp.getRole());

        sctp = provider.parse(getParserFor("<sctp role='sERVer'/>"));
        assertEquals(Sctp.Role.SERVER, sctp.getRole());

        sctp = provider.parse(getParserFor("<sctp port='5000'/>"));
        assertEquals(5000, sctp.getPort());

        assertThrows(SmackParsingException.class, () -> {
            provider.parse(getParserFor("<sctp port='abcd'/>"));
        });

        assertThrows(SmackParsingException.class, () -> {
            provider.parse(getParserFor("<sctp role='abcd'/>"));
        });
    }
}
