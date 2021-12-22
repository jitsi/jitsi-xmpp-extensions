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
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

import static org.junit.jupiter.api.Assertions.*;

public class ForceMuteTest
{
    @Test
    public void parsingTest()
            throws Exception
    {
        ForceMute.Provider provider = new ForceMute.Provider();

        ForceMute forceMute = provider.parse(PacketParserUtils.getParserFor("<force-mute audio='true'/>"));

        assertTrue(forceMute.getAudio(), "audio should be true");
        assertFalse(forceMute.getVideo(), "video should default to false");

        forceMute = provider.parse(PacketParserUtils.getParserFor("<force-mute video='false'/>"));
        assertFalse(forceMute.getAudio(), "audio should default to false");
        assertFalse(forceMute.getVideo(), "video should be false");
    }

    @Test
    public void toXmlTest()
    {
        Diff diff1 = DiffBuilder.compare("<force-mute xmlns='jitsi:colibri2'/>").
            withTest(new ForceMute().toXML().toString()).
            checkForIdentical().build();
        assertFalse(diff1.hasDifferences(), diff1.toString());

        Diff diff2 = DiffBuilder.compare("<force-mute xmlns='jitsi:colibri2' audio='true' video='true'/>").
            withTest(new ForceMute(true, true).toXML().toString()).
            checkForIdentical().build();
        assertFalse(diff2.hasDifferences(), diff2.toString());
    }
}
