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

import org.jetbrains.annotations.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;

import java.util.*;

public abstract class AbstractConferenceModificationIQ<I extends AbstractConferenceModificationIQ>
    extends IQ
{
    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference-modify</tt> IQ.
     */
    public static final String NAMESPACE = "http://jitsi.org/protocol/colibri2";

    /**
     * The XML name of the <tt>name</tt> attribute of the Jitsi Videobridge
     * <tt>conference</tt> IQ which represents the value of the <tt>name</tt>
     * property of <tt>ConferenceModifyIQ</tt> if available.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * The XML name of the <tt>meeting-id</tt> attribute of the Jitsi Videobridge
     * <tt>conference</tt> IQ which represents the value of the <tt>name</tt>
     * property of <tt>ConferenceModifyIQ</tt> if available.
     */
    public static final String MEETING_ID_ATTR_NAME = "meeting-id";

    /**
     * The id of the conference
     */
    private final String meetingId;

    /**
     * The name of the conference
     */
    private final String name;

    /** Initializes a new <tt>ConferenceModifyIQ</tt> instance. */
    protected AbstractConferenceModificationIQ(Builder<I> b, String element)
    {
        super(b, element, NAMESPACE);

        if (b.meetingId == null)
        {
            throw new IllegalArgumentException("meeting-id must be set for " + element + " IQ");
        }
        meetingId = b.meetingId;

        if (b.conferenceName == null)
        {
            throw new IllegalArgumentException("name must be set for " + element + " IQ");
        }
        name = b.conferenceName;

        for (AbstractConferenceEntity ce: b.conferenceEntities) {
            addExtension(ce);
        }
    }

    /**
     * Get the name of the conference.
     */
    public @NotNull String getConferenceName()
    {
        return name;
    }

    /**
     * Get the ID of the conference.
     */
    public @NotNull String getMeetingId()
    {
        return meetingId;
    }

    /**
     * Get endpoints described by the message
     */
    public @NotNull List<Endpoint> getEndpoints()
    {
        return getExtensions(Endpoint.class);
    }

    /**
     * Get relays described by the message.
     */
    public @NotNull List<Relay> getRelays()
    {
        return getExtensions(Relay.class);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.attribute(MEETING_ID_ATTR_NAME, meetingId);
        xml.attribute(NAME_ATTR_NAME, name);

        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement();

        return xml;
    }

    public abstract static class Builder<I extends AbstractConferenceModificationIQ>
        extends IqBuilder<Builder<I>, I>
    {
        private final List<AbstractConferenceEntity> conferenceEntities = new ArrayList<>();

        private String conferenceName;

        private String meetingId;

        protected Builder(IqData iqCommon) {
            super(iqCommon);
        }

        protected Builder(XMPPConnection connection) {
            super(connection);
        }

        protected Builder(String stanzaId) {
            super(stanzaId);
        }

        public Builder<I> addConferenceEntity(AbstractConferenceEntity entity)
        {
            conferenceEntities.add(entity);

            return this;
        }

        public Builder<I> addEndpoint(Endpoint ep)
        {
            return addConferenceEntity(ep);
        }

        public Builder<I> addRelay(Relay r)
        {
            return addConferenceEntity(r);
        }

        public Builder<I> setConferenceName(String name)
        {
            conferenceName = name;

            return this;
        }

        public Builder<I> setMeetingId(String id)
        {
            meetingId = id;

            return this;
        }

        @Contract(" -> new")
        public abstract @NotNull I build();

        @Override
        public Builder<I> getThis()
        {
            return this;
        }
    }
}
