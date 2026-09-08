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
import org.jitsi.xmpp.extensions.colibri2.*;
import org.jivesoftware.smack.packet.*;
import org.junit.jupiter.api.*;
import org.jxmpp.jid.impl.*;

/**
 * Few basic tests for parsing JibriIQ
 *
 * @author Pawel Domas
 */
public class JibriIqProviderTest
{
    @BeforeAll
    public static void registerProviders()
    {
        IqProviderUtils.registerProviders();
    }

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

    @Test
    public void testParseSupportsBadRequest()
        throws Exception
    {
        JibriIqProvider provider = new JibriIqProvider();

        String iqWithTrue =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start' supports_bad_request='true'" +
                "/>" +
                "</iq>";
        assertEquals(Boolean.TRUE, IQUtils.parse(iqWithTrue, provider).getSupportsBadRequest());

        String iqWithFalse =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start' supports_bad_request='false'" +
                "/>" +
                "</iq>";
        assertEquals(Boolean.FALSE, IQUtils.parse(iqWithFalse, provider).getSupportsBadRequest());

        // An old Jicofo does not send the attribute at all. It must read as "not supported", never as supported.
        String iqWithoutFlag =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start'" +
                "/>" +
                "</iq>";
        assertNull(IQUtils.parse(iqWithoutFlag, provider).getSupportsBadRequest());
    }

    @Test
    public void testSerializeSupportsBadRequest()
    {
        JibriIq iq = new JibriIq();
        iq.setType(IQ.Type.set);
        iq.setAction(JibriIq.Action.START);

        iq.setSupportsBadRequest(true);
        assertTrue(iq.toXML().toString().contains("supports_bad_request='true'"));

        iq.setSupportsBadRequest(null);
        assertFalse(iq.toXML().toString().contains("supports_bad_request"));
    }

    @Test
    public void testParseBadRequest()
        throws Exception
    {
        BadRequestPacketExt.registerExtensionProvider();
        JibriIqProvider provider = new JibriIqProvider();

        String iqXml =
            "<iq to='t' from='f' type='result'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   status='off' failure_reason='error' should_retry='false'>" +
                "<bad-request xmlns='http://jitsi.org/protocol/jibri'>" +
                "The YouTube stream key has an invalid format</bad-request>" +
                "</jibri>" +
                "</iq>";

        JibriIq jibriIq = IQUtils.parse(iqXml, provider);
        assertNotNull(jibriIq);

        BadRequestPacketExt badRequest = jibriIq.getExtension(BadRequestPacketExt.class);
        assertNotNull(badRequest, "the bad-request extension must be attached to the parsed IQ");
        assertEquals("The YouTube stream key has an invalid format", badRequest.getDetail());
    }

    @Test
    public void testSerializeBadRequest()
        throws Exception
    {
        BadRequestPacketExt.registerExtensionProvider();

        JibriIq iq = new JibriIq();
        iq.setType(IQ.Type.result);
        iq.setStatus(JibriIq.Status.OFF);
        iq.setFailureReason(JibriIq.FailureReason.ERROR);
        iq.setShouldRetry(false);
        iq.setTo(JidCreate.from("t@example.com"));
        iq.setFrom(JidCreate.from("f@example.com"));
        iq.addExtension(new BadRequestPacketExt("a parameter has a bad value"));

        String xml = iq.toXML().toString();
        assertTrue(xml.contains("<bad-request"), "the extension must be nested inside <jibri>: " + xml);

        // The extension must survive a round trip, otherwise the receiver can never act on it.
        JibriIq reparsed = IQUtils.parse(xml, new JibriIqProvider());
        assertNotNull(reparsed);
        assertEquals(
            "a parameter has a bad value",
            reparsed.getExtension(BadRequestPacketExt.class).getDetail());
    }

    @Test
    public void testParseExtension()
        throws Exception
    {
        JibriIqProvider provider = new JibriIqProvider();

        String iqXml =
            "<iq to='t' from='f' type='set'>" +
                "<jibri xmlns='http://jitsi.org/protocol/jibri'" +
                "   action='start'>" +
                "<capability xmlns='jitsi:colibri2' name='cap1'/>" +
                "</jibri>" +
                "</iq>";

        JibriIq jibriIq = IQUtils.parse(iqXml, provider);

        assertNotNull(jibriIq);
        Capability capability = jibriIq.getExtension(Capability.class);
        assertNotNull(capability, "extension must be attached to the parsed IQ");
        assertEquals("cap1", capability.getName());
    }
}
