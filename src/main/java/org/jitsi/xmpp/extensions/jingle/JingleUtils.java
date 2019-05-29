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
package org.jitsi.xmpp.extensions.jingle;

import org.apache.commons.lang3.StringUtils;
import org.jitsi.utils.MediaType;

/**
 * The class contains a number of utility methods that are meant to facilitate
 * creating and parsing jingle media rtp description descriptions and
 * transports.
 *
 * @author Emil Ivov
 * @author Lyubomir Marinov
 */
public class JingleUtils
{
    /**
     * Returns the <tt>MediaType</tt> for <tt>content</tt> by looking for it
     * in the <tt>content</tt>'s <tt>description</tt>, if any.
     *
     * @param content the content to return the <tt>MediaType</tt> of
     * @return the <tt>MediaType</tt> for <tt>content</tt> by looking for it
     * in the <tt>content</tt>'s <tt>description</tt>, if any.
     * <tt>contentName</tt>
     */
    public static MediaType getMediaType(ContentPacketExtension content)
    {
        if (content == null)
            return null;

        // We will use content name for determining media type
        // if no RTP description is present(SCTP connection case)
        String mediaTypeName = content.getName();

        RtpDescriptionPacketExtension desc = getRtpDescription(content);
        if (desc != null)
        {
            String rtpMedia = desc.getMedia().toLowerCase();
            if (StringUtils.isNotEmpty(rtpMedia))
            {
                mediaTypeName = rtpMedia;
            }
        }
        if ("application".equals(mediaTypeName))
        {
            return MediaType.DATA;
        }
        return MediaType.parseString(mediaTypeName);
    }

    /**
     * Extracts and returns an {@link RtpDescriptionPacketExtension} provided
     * with <tt>content</tt> or <tt>null</tt> if there is none.
     *
     * @param content the media content that we'd like to extract the
     * {@link RtpDescriptionPacketExtension} from.
     *
     * @return an {@link RtpDescriptionPacketExtension} provided with
     * <tt>content</tt> or <tt>null</tt> if there is none.
     */
    public static RtpDescriptionPacketExtension getRtpDescription(
        ContentPacketExtension content)
    {
        return content.getFirstChildOfType(RtpDescriptionPacketExtension.class);
    }
}
