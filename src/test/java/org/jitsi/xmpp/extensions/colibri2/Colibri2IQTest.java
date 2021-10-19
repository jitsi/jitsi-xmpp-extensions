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

    @Test
    public void buildColibriConferenceModifyTest()
    {
        ConferenceModifyIQ.Builder iqBuilder = ConferenceModifyIQ.getBuilder();

        iqBuilder.setConferenceName(CONFERENCE_NAME);
        iqBuilder.setMeetingId(MEETING_ID);

        Endpoint.Builder endpointBuilder = Endpoint.getBuilder();

        endpointBuilder.setId("bd9b6765");
        endpointBuilder.setStatsId("Jayme-Clv");

        Media.Builder mediaBuilder = Media.getBuilder();
        mediaBuilder.setType(MediaType.AUDIO);
        PayloadTypePacketExtension pt = new PayloadTypePacketExtension();
        pt.setName("opus");
        pt.setClockrate(48000);
        pt.setChannels(2);
        mediaBuilder.addPayloadType(pt);

        endpointBuilder.addMedia(mediaBuilder.build());
        iqBuilder.addEndpoint(endpointBuilder.build());
        ConferenceModifyIQ iq = iqBuilder.build();

        assertEquals("Conference name", iq.getConferenceName(), CONFERENCE_NAME);
        assertEquals("Meeting ID", iq.getMeetingId(), MEETING_ID);

        CharSequence xml = iq.toXML();

        /* TODO: test this once we're sure its form is stable. */
        assertNotNull(xml);
    }
}
