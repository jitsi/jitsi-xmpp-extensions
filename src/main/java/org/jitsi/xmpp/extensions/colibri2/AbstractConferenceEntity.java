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

import java.util.*;

/**
 * An abstract representation of a participant in a conference as described by Colibri signaling.
 * This is the base class of both Endpoint and Relay.
 */
public abstract class AbstractConferenceEntity
{
    private Transport transport;

    private List<Media> medias;

    /* Do we need SctpConnection here? */

    private List<Source> sources;

    public static class Media
    {
        public static final String TYPE_ATTR_NAME = "type";

        MediaType type;

        /**
         * The <tt>payload-type</tt> elements defined by XEP-0167: Jingle RTP
         * Sessions associated with this <tt>channel</tt>.
         */
        private final List<PayloadTypePacketExtension> payloadTypes
            = new ArrayList<>();

        /**
         * The <tt>rtp-hdrext</tt> elements defined by XEP-0294: Jingle RTP
         * Header Extensions Negotiation associated with this channel.
         */
        private final Map<Integer, RTPHdrExtPacketExtension> rtpHeaderExtensions
            = new HashMap<>();
    }

    public static class Source
    {

    }

}
