/*
 * Copyright @ 2018 - present 8x8, Inc.
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

import org.apache.commons.lang3.StringUtils;

import org.jivesoftware.smack.provider.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;
import org.xmlpull.v1.*;

/**
 * Parses {@link JibriIq}.
 */
public class JibriIqProvider
    extends IQProvider<JibriIq>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public JibriIq parse(XmlPullParser parser, int depth)
        throws Exception
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!JibriIq.NAMESPACE.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();

        JibriIq iq;

        if (JibriIq.ELEMENT_NAME.equals(rootElement))
        {
            iq = new JibriIq();

            String action
                = parser.getAttributeValue("", JibriIq.ACTION_ATTR_NAME);
            iq.setAction(JibriIq.Action.parse(action));

            String status
                = parser.getAttributeValue("", JibriIq.STATUS_ATTR_NAME);
            iq.setStatus(JibriIq.Status.parse(status));

            String recordingMode
                = parser.getAttributeValue(
                        "", JibriIq.RECORDING_MODE_ATTR_NAME);
            if (StringUtils.isNotEmpty(recordingMode))
                iq.setRecordingMode(
                        JibriIq.RecordingMode.parse(recordingMode));

            String room
                = parser.getAttributeValue("", JibriIq.ROOM_ATTR_NAME);
            if (StringUtils.isNotEmpty(room))
            {
                EntityBareJid roomJid = JidCreate.entityBareFrom(room);
                iq.setRoom(roomJid);
            }

            String streamId
                = parser.getAttributeValue("", JibriIq.STREAM_ID_ATTR_NAME);
            if (StringUtils.isNotEmpty(streamId))
                iq.setStreamId(streamId);

            String youTubeBroadcastId
                    = parser.getAttributeValue("", JibriIq.YOUTUBE_BROADCAST_ID_ATTR_NAME);
            if (StringUtils.isNotEmpty(youTubeBroadcastId))
                iq.setYouTubeBroadcastId(youTubeBroadcastId);

            String sessionId = parser.getAttributeValue("", JibriIq.SESSION_ID_ATTR_NAME);
            if (StringUtils.isNotEmpty(sessionId))
            {
                iq.setSessionId(sessionId);
            }

            String appData = parser.getAttributeValue("", JibriIq.APP_DATA_ATTR_NAME);
            if (StringUtils.isNotEmpty(appData)) {
                iq.setAppData(appData);
            }

            String failureStr
                    = parser.getAttributeValue("", JibriIq.FAILURE_REASON_ATTR_NAME);
            if (StringUtils.isNotEmpty(failureStr))
            {
                iq.setFailureReason(JibriIq.FailureReason.parse(failureStr));
            }
            String shouldRetryStr
                = parser.getAttributeValue(
                    "", JibriIq.SHOULD_RETRY_ATTR_NAME);
            if (StringUtils.isNotEmpty(shouldRetryStr))
            {
                iq.setShouldRetry(Boolean.valueOf(shouldRetryStr));
            }

            String displayName
                = parser.getAttributeValue("", JibriIq.DISPLAY_NAME_ATTR_NAME);
            if (StringUtils.isNotEmpty(displayName))
                iq.setDisplayName(displayName);

            String sipAddress
                = parser.getAttributeValue("", JibriIq.SIP_ADDRESS_ATTR_NAME);
            if (StringUtils.isNotEmpty(sipAddress))
                iq.setSipAddress(sipAddress);
        }
        else
        {
            return null;
        }

        return iq;
    }
}
