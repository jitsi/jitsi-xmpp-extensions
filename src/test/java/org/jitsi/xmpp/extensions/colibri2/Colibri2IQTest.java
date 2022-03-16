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
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.*;
import org.jivesoftware.smack.xml.*;
import org.junit.jupiter.api.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Colibri2IQTest
{
    private static final String CONFERENCE_NAME = "myconference@jitsi.example";
    private static final String MEETING_ID = "88ff288c-5eeb-4ea9-bc2f-93ea38c43b78";

    private static final String ENDPOINT_ID = "bd9b6765";
    private static final String STATS_ID = "Jayme-Clv";

    private static final int SSRC = 803354056;
    private static final String SOURCE_ID = ENDPOINT_ID + "-v1";

    private static final String expectedXml =
        "<iq xmlns='jabber:client' id='id' type='get'>"
            + "<conference-modify xmlns='jitsi:colibri2' meeting-id='88ff288c-5eeb-4ea9-bc2f-93ea38c43b78' name='myconference@jitsi.example' callstats-enabled='false' create='true'>"
            /* I thought Smack 4.4.4 would remove the redundant xmlns from this line, but it didn't.  TODO. */
            + "<endpoint xmlns='jitsi:colibri2' id='bd9b6765' stats-id='Jayme-Clv'>"
            + "<media type='audio'>"
            + "<payload-type xmlns='urn:xmpp:jingle:apps:rtp:1' name='opus' clockrate='48000' channels='2'/>"
            + "</media>"
            + "<transport ice-controlling='true'>"
            + "<sctp/>"
            + "</transport>"
            + "<sources>"
            + "<media-source type='video' id='bd9b6765-v1'>"
            + "<source xmlns='urn:xmpp:jingle:apps:rtp:ssma:0' ssrc='803354056'/>"
            + "</media-source>"
            + "</sources>"
            + "<force-mute audio='true' video='true'/>"
            + "<capability name='cap1'/>"
            + "<capability name='cap2'/>"
            + "</endpoint>"
            + "</conference-modify>"
            + "</iq>";

    @BeforeAll
    static void registerProviders()
    {
        IqProviderUtils.registerProviders();
    }

    @Test
    public void buildColibriConferenceModifyTest()
    {
        ConferenceModifyIQ.Builder iqBuilder = ConferenceModifyIQ.builder("id");

        iqBuilder.setConferenceName(CONFERENCE_NAME);
        iqBuilder.setMeetingId(MEETING_ID);
        iqBuilder.setCallstatsEnabled(false);
        iqBuilder.setCreate(true);

        Colibri2Endpoint.Builder endpointBuilder = Colibri2Endpoint.getBuilder();

        endpointBuilder.setId(ENDPOINT_ID);
        endpointBuilder.setStatsId(STATS_ID);

        Media.Builder mediaBuilder = Media.getBuilder();
        mediaBuilder.setType(MediaType.AUDIO);
        PayloadTypePacketExtension pt = new PayloadTypePacketExtension();
        pt.setName("opus");
        pt.setClockrate(48000);
        pt.setChannels(2);
        mediaBuilder.addPayloadType(pt);

        Transport.Builder transportBuilder = Transport.getBuilder();
        transportBuilder.setIceControlling(true);
        transportBuilder.setUseUniquePort(false);
        transportBuilder.setSctp(new Sctp.Builder().build());

        Sources.Builder sourcesBuilder = Sources.getBuilder();
        SourcePacketExtension ssrc = new SourcePacketExtension();
        ssrc.setSSRC(SSRC);
        sourcesBuilder.addMediaSource(MediaSource.getBuilder().
            setType(MediaType.VIDEO).
            setId(SOURCE_ID).
            addSource(ssrc).build());

        endpointBuilder.addMedia(mediaBuilder.build());
        endpointBuilder.setTransport(transportBuilder.build());
        endpointBuilder.setSources(sourcesBuilder.build());
        endpointBuilder.setForceMute(true, true);
        endpointBuilder.addCapability("cap1");
        endpointBuilder.addCapability("cap2");

        iqBuilder.addEndpoint(endpointBuilder.build());
        ConferenceModifyIQ iq = iqBuilder.build();

        assertEquals(CONFERENCE_NAME, iq.getConferenceName(), "Conference name");
        assertEquals(MEETING_ID, iq.getMeetingId(), "Meeting ID");

        Colibri2Endpoint endpoint = iq.getEndpoints().get(0);
        assertEquals(ENDPOINT_ID, endpoint.getId(), "Endpoint ID");
        assertEquals(STATS_ID, endpoint.getStatsId(), "Stats ID");

        List<Capability> caps = endpoint.getCapabilities();
        assertEquals(2, caps.size());
        assertEquals("cap1", caps.get(0).getName());
        assertEquals("cap2", caps.get(1).getName());

        assertEquals(MediaType.AUDIO, endpoint.getMedia().get(0).getType(), "Media type");
        assertEquals("opus", endpoint.getMedia().get(0).getPayloadTypes().get(0).getName(), "Payload type name");

        assertEquals(MediaType.VIDEO, endpoint.getSources().getMediaSources().get(0).getType(), "Source type");
        assertEquals(SSRC, endpoint.getSources().getMediaSources().get(0).getSources().get(0).getSSRC(), "SSRC");

        assertNotNull(endpoint.getTransport());
        assertNotNull(endpoint.getTransport().getSctp());

        assertNotNull(endpoint.getForceMute(), "force-mute must be present");
        assertTrue(endpoint.getForceMute().getAudio(), "force-mute audio must be true");
        assertTrue(endpoint.getForceMute().getVideo(), "force-mute video must be true");

        Diff diff = DiffBuilder.compare(expectedXml).
            withTest(iq.toXML().toString()).
            checkForIdentical().build();

        assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void parseColibriConferenceModifyTest()
        throws Exception
    {
        XmlPullParser parser = PacketParserUtils.getParserFor(expectedXml);
        IQ parsedIq = PacketParserUtils.parseIQ(parser);

        assertInstanceOf(ConferenceModifyIQ.class, parsedIq);

        ConferenceModifyIQ iq = (ConferenceModifyIQ)parsedIq;

        assertEquals(CONFERENCE_NAME, iq.getConferenceName(), "Conference name");
        assertEquals(MEETING_ID, iq.getMeetingId(), "Meeting ID");
        assertFalse(iq.isCallstatsEnabled(), "Callstats enabled");
        assertTrue(iq.getCreate(), "Create flag");

        Colibri2Endpoint endpoint = iq.getEndpoints().get(0);
        assertNotNull(endpoint, "endpoint must not be null");
        assertEquals(ENDPOINT_ID, endpoint.getId(), "Endpoint ID");
        assertEquals(STATS_ID, endpoint.getStatsId(), "Stats ID");
        assertEquals(2, endpoint.getCapabilities().size(), "number of capabilities");
        assertEquals("cap1", endpoint.getCapabilities().get(0).getName(), "capability #1");
        assertEquals("cap2", endpoint.getCapabilities().get(1).getName(), "capability #2");

        assertEquals(MediaType.AUDIO, endpoint.getMedia().get(0).getType(), "Media type");
        assertEquals("opus", endpoint.getMedia().get(0).getPayloadTypes().get(0).getName(), "Payload type name");

        assertEquals(MediaType.VIDEO, endpoint.getSources().getMediaSources().get(0).getType(), "Source type");
        assertEquals(SSRC, endpoint.getSources().getMediaSources().get(0).getSources().get(0).getSSRC(), "SSRC");

        assertNotNull(endpoint.getTransport());
        assertNotNull(endpoint.getTransport().getSctp());

        assertNotNull(endpoint.getForceMute(), "force-mute must not be null");
        assertTrue(endpoint.getForceMute().getAudio(), "force-mute audio must be true");
        assertTrue(endpoint.getForceMute().getVideo(), "force-mute video must be true");
    }
}
