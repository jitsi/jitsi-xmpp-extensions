/*
 * Copyright @ 2022 - present 8x8, Inc.
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

public class Colibri2ErrorExtensionTest
{
    private static Colibri2Error.Provider provider = new Colibri2Error.Provider();

    @Test
    public void parseValid()
            throws Exception
    {
        assertThrows(SmackParsingException.class, () ->
                provider.parse(getParserFor("<error xmlns='jitsi:colibri2' reason='invalid'/>")));

        Colibri2Error e;
        e = provider.parse(getParserFor("<error xmlns='jitsi:colibri2' reason='conference_not_found'/>"));
        assertNotNull(e);
        assertEquals(Colibri2Error.Reason.CONFERENCE_NOT_FOUND, e.getReason());

        e = provider.parse(getParserFor("<error xmlns='jitsi:colibri2' reason='CONFERENCE_not_FOUND'/>"));
        assertNotNull(e);
        assertEquals(Colibri2Error.Reason.CONFERENCE_NOT_FOUND, e.getReason());

        e = provider.parse(getParserFor("<error xmlns='jitsi:colibri2'/>"));
        assertNotNull(e);
        assertEquals(Colibri2Error.Reason.UNSPECIFIED, e.getReason());
    }
}
