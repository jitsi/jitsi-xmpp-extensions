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
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.jingle.element.*;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.junit.*;
import static org.junit.Assert.*;

public class Colibri2IQTest
{
    private static final String CONFERENCE_NAME = "myconference@jitsi.example";
    private static final String MEETING_ID = "88ff288c-5eeb-4ea9-bc2f-93ea38c43b78";

    private static final String ENDPOINT_ID = "bd9b6765";
    private static final String STATS_ID = "Jayme-Clv";

    @Test
    public void buildColibriConferenceModifyTest()
    {
        ConferenceModifyIQ.Builder iqBuilder = ConferenceModifyIQ.builder("id");

        iqBuilder.setConferenceName(CONFERENCE_NAME);
        iqBuilder.setMeetingId(MEETING_ID);

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

        endpointBuilder.addMedia(mediaBuilder.build());
        endpointBuilder.setTransport(transportBuilder.build());

        iqBuilder.addEndpoint(endpointBuilder.build());
        ConferenceModifyIQ iq = iqBuilder.build();

        assertEquals("Conference name",CONFERENCE_NAME, iq.getConferenceName());
        assertEquals("Meeting ID", MEETING_ID, iq.getMeetingId());

        assertEquals("Endpoint ID", ENDPOINT_ID, iq.getEndpoints().get(0).getId());
        assertEquals("Stats ID", STATS_ID, iq.getEndpoints().get(0).getStatsId());

        assertEquals("Media type", MediaType.AUDIO, iq.getEndpoints().get(0).getMedia().get(0).getType());
        assertEquals("Payload type name", "opus",
            iq.getEndpoints().get(0).getMedia().get(0).getPayloadTypes().get(0).getName());

        CharSequence xml = iq.toXML();

        String expectedXml =
            "<iq xmlns='jabber:client' id='id' type='get'>"
                + "<conference-modify xmlns='http://jitsi.org/protocol/colibri2' meeting-id='88ff288c-5eeb-4ea9-bc2f-93ea38c43b78' name='myconference@jitsi.example'>"
                /* Smack 4.4.4 will remove the redundant xmlns from this line. */
                + "<endpoint xmlns='http://jitsi.org/protocol/colibri2' id='bd9b6765' stats-id='Jayme-Clv'>"
                + "<media type='audio'>"
                + "<payload-type xmlns='urn:xmpp:jingle:apps:rtp:1' name='opus' clockrate='48000' channels='2'/>"
                + "</media>"
                + "<transport initiator='true'/>"
                + "</endpoint>"
                + "</conference-modify>"
                + "</iq>";

        assertEquals("XML not serialized as expected", expectedXml, xml.toString());
    }
}
