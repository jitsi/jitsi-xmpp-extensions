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

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Provider for Colibri2 conference-modify IQs.
 */
public class ConferenceModifyIQProvider extends IqProvider<ConferenceModifyIQ>
{
    @Override
    public ConferenceModifyIQ parse(
            XmlPullParser parser,
            int initialDepth,
            IqData iqData,
            XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
    {
        ConferenceModifyIQ.Builder builder = ConferenceModifyIQ.builder(iqData);

        String meetingId = parser.getAttributeValue(ConferenceModifyIQ.MEETING_ID_ATTR_NAME);
        if (meetingId == null)
        {
            throw new SmackParsingException.RequiredAttributeMissingException(ConferenceModifyIQ.MEETING_ID_ATTR_NAME);
        }
        builder.setMeetingId(meetingId);

        builder.setConferenceName(parser.getAttributeValue(ConferenceModifyIQ.NAME_ATTR_NAME));

        String rtcStatsEnabled = parser.getAttributeValue(ConferenceModifyIQ.RTCSTATS_ENABLED_ATTR_NAME);
        if (rtcStatsEnabled != null)
        {
            builder.setRtcstatsEnabled(Boolean.parseBoolean(rtcStatsEnabled));
        }

        String create = parser.getAttributeValue(ConferenceModifyIQ.CREATE_ATTR_NAME);
        if (create != null)
        {
            builder.setCreate(Boolean.parseBoolean(create));
        }

        String expire = parser.getAttributeValue(ConferenceModifyIQ.EXPIRE_ATTR_NAME);
        if (expire != null)
        {
            builder.setExpire(Boolean.parseBoolean(expire));
        }

        ConferenceModifyIQ iq = builder.build();
        IqProviderUtils.parseExtensions(parser, initialDepth, iq);

        return iq;
    }
}
