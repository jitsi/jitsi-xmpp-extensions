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

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.*;
import org.jivesoftware.smack.xml.*;
import org.junit.jupiter.api.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConferenceNotificationIQTest
{
    private static final String MEETING_ID = "88ff288c-5eeb-4ea9-bc2f-93ea38c43b78";
    private static final String IQ_ID = "iq-id";
    private static final String ENDPOINT_ID = "bd9b6765";

    private static final String expectedXml =
        "<iq xmlns='jabber:client' id='" + IQ_ID + "' type='get'>"
            + "<conference-notification xmlns='jitsi:colibri2' meeting-id='" + MEETING_ID + "'>"
                + "<endpoint id='" + ENDPOINT_ID + "' expire='true'/>"
            + "</conference-notification>"
        + "</iq>";

    @BeforeAll
    static void registerProviders()
    {
        IqProviderUtils.registerProviders();
    }

    @Test
    public void buildColibriConferenceModifyTest()
    {
        ConferenceNotificationIQ.Builder iqBuilder = ConferenceNotificationIQ.builder(IQ_ID);

        iqBuilder.setMeetingId(MEETING_ID);

        Colibri2Endpoint.Builder endpointBuilder = Colibri2Endpoint.getBuilder();
        endpointBuilder.setId(ENDPOINT_ID);
        endpointBuilder.setExpire(true);

        iqBuilder.addEndpoint(endpointBuilder.build());

        ConferenceNotificationIQ iq = iqBuilder.build();

        assertEquals(MEETING_ID, iq.getMeetingId(), "The meeting-id should be correctly set.");

        Colibri2Endpoint endpoint = iq.getEndpoints().get(0);
        assertNotNull(endpoint);
        assertEquals(ENDPOINT_ID, endpoint.getId());
        assertTrue(endpoint.getExpire());

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

        assertInstanceOf(ConferenceNotificationIQ.class, parsedIq);

        ConferenceNotificationIQ iq = (ConferenceNotificationIQ) parsedIq;

        assertEquals(MEETING_ID, iq.getMeetingId(), "The meeting-id should be correctly parsed");

        Colibri2Endpoint endpoint = iq.getEndpoints().get(0);
        assertNotNull(endpoint);
        assertEquals(ENDPOINT_ID, endpoint.getId());
        assertTrue(endpoint.getExpire());
    }
}
