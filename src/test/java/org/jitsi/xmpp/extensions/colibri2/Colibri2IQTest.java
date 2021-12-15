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
            + "<conference-modify xmlns='http://jitsi.org/protocol/colibri2' meeting-id='88ff288c-5eeb-4ea9-bc2f-93ea38c43b78' name='myconference@jitsi.example' callstats-enabled='false' create='true'>"
            /* Smack 4.4.4 will remove the redundant xmlns from this line. */
            + "<endpoint xmlns='http://jitsi.org/protocol/colibri2' id='bd9b6765' stats-id='Jayme-Clv'>"
            + "<media type='audio'>"
            + "<payload-type xmlns='urn:xmpp:jingle:apps:rtp:1' name='opus' clockrate='48000' channels='2'/>"
            + "</media>"
            + "<transport initiator='true'/>"
            + "<sources>"
            + "<media-source type='video' id='bd9b6765-v1'>"
            + "<source xmlns='urn:xmpp:jingle:apps:rtp:ssma:0' ssrc='803354056'/>"
            + "</media-source>"
            + "</sources>"
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

        Endpoint.Builder endpointBuilder = Endpoint.getBuilder();

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
        transportBuilder.setInitiator(true);
        transportBuilder.setUseUniquePort(false);

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

        iqBuilder.addEndpoint(endpointBuilder.build());
        ConferenceModifyIQ iq = iqBuilder.build();

        assertEquals(CONFERENCE_NAME, iq.getConferenceName(), "Conference name");
        assertEquals(MEETING_ID, iq.getMeetingId(), "Meeting ID");

        assertEquals(ENDPOINT_ID, iq.getEndpoints().get(0).getId(), "Endpoint ID");
        assertEquals(STATS_ID, iq.getEndpoints().get(0).getStatsId(), "Stats ID");

        assertEquals(MediaType.AUDIO, iq.getEndpoints().get(0).getMedia().get(0).getType(), "Media type");
        assertEquals("opus",
            iq.getEndpoints().get(0).getMedia().get(0).getPayloadTypes().get(0).getName(), "Payload type name");

        assertEquals(MediaType.VIDEO,
            iq.getEndpoints().get(0).getSources().getMediaSources().get(0).getType(), "Source type");
        assertEquals(SSRC,
            iq.getEndpoints().get(0).getSources().getMediaSources().get(0).getSources().get(0).getSSRC(), "SSRC");

        CharSequence xml = iq.toXML();

        assertEquals(expectedXml, xml.toString(), "XML serialization");
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
        assertEquals(false, iq.isCallstatsEnabled(), "Callstats enabled");
        assertEquals(true, iq.getCreate(), "Create flag");

        assertEquals(ENDPOINT_ID, iq.getEndpoints().get(0).getId(), "Endpoint ID");
        assertEquals(STATS_ID, iq.getEndpoints().get(0).getStatsId(), "Stats ID");

        assertEquals(MediaType.AUDIO, iq.getEndpoints().get(0).getMedia().get(0).getType(), "Media type");
        assertEquals("opus",
            iq.getEndpoints().get(0).getMedia().get(0).getPayloadTypes().get(0).getName(), "Payload type name");

        assertEquals(MediaType.VIDEO,
            iq.getEndpoints().get(0).getSources().getMediaSources().get(0).getType(), "Source type");
        assertEquals(SSRC,
            iq.getEndpoints().get(0).getSources().getMediaSources().get(0).getSources().get(0).getSSRC(), "SSRC");
    }
}
