/*
 * Copyright @ 2015 - Present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.jibri;

import static org.junit.jupiter.api.Assertions.*;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.junit.jupiter.api.*;

/**
 * Few basic tests for parsing JibriIQ
 *
 * @author Pawel Domas
 */
public class JibriIqProviderTest
{
    @Test
    public void testParseIQ()
        throws Exception
    {
        JibriIqProvider provider = new JibriIqProvider();

        // JibriIq
        String iqXml =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   status='off' action='stop' failure_reason='error'" +
                "   should_retry='true'" +
                "   session_id='abcd'" +
                "/>" +
                "</iq>";

        JibriIq jibriIq = IQUtils.parse(iqXml, provider);

        assertNotNull(jibriIq);

        assertEquals(JibriIq.Status.OFF, jibriIq.getStatus());
        assertEquals(JibriIq.Action.STOP, jibriIq.getAction());
        assertEquals(JibriIq.FailureReason.ERROR, jibriIq.getFailureReason());
        assertEquals(true, jibriIq.getShouldRetry());
        assertTrue(jibriIq.getSessionId().equalsIgnoreCase("abcd"));

        assertNull(jibriIq.getError());
    }

    @Test
    public void testParseRtcStatsEnabled()
        throws Exception
    {
        JibriIqProvider provider = new JibriIqProvider();

        String iqWithTrue =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start' rtcstats_enabled='true'" +
                "/>" +
                "</iq>";
        JibriIq iqTrue = IQUtils.parse(iqWithTrue, provider);
        assertEquals(Boolean.TRUE, iqTrue.getRtcStatsEnabled());

        String iqWithFalse =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start' rtcstats_enabled='false'" +
                "/>" +
                "</iq>";
        JibriIq iqFalse = IQUtils.parse(iqWithFalse, provider);
        assertEquals(Boolean.FALSE, iqFalse.getRtcStatsEnabled());

        String iqWithoutFlag =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start'" +
                "/>" +
                "</iq>";
        JibriIq iqAbsent = IQUtils.parse(iqWithoutFlag, provider);
        assertNull(iqAbsent.getRtcStatsEnabled());
    }

    @Test
    public void testSerializeRtcStatsEnabled()
    {
        JibriIq iq = new JibriIq();
        iq.setType(IQ.Type.set);
        iq.setAction(JibriIq.Action.START);

        iq.setRtcStatsEnabled(true);
        assertTrue(iq.toXML().toString().contains("rtcstats_enabled='true'"));

        iq.setRtcStatsEnabled(false);
        assertTrue(iq.toXML().toString().contains("rtcstats_enabled='false'"));

        iq.setRtcStatsEnabled(null);
        assertFalse(iq.toXML().toString().contains("rtcstats_enabled"));
    }
}
