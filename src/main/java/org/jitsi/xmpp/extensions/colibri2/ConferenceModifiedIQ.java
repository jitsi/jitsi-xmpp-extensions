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

import java.util.*;

public class ConferenceModifiedIQ
    extends IQ
{
    /**
     * The XML element name of the Jitsi Videobridge <tt>conference-modified</tt> IQ.
     */
    public static final String ELEMENT = "conference-modified";

    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference-modified</tt> IQ.
     */
    public static final String NAMESPACE = "http://jitsi.org/protocol/colibri2";

    /**
     * The XML name of the <tt>name</tt> attribute of the Jitsi Videobridge
     * <tt>conference</tt> IQ which represents the value of the <tt>name</tt>
     * property of <tt>ConferenceModifiedIQ</tt> if available.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * The XML name of the <tt>meeting-id</tt> attribute of the Jitsi Videobridge
     * <tt>conference</tt> IQ which represents the value of the <tt>name</tt>
     * property of <tt>ConferenceModifiedIQ</tt> if available.
     */
    public static final String MEETING_ID_ATTR_NAME = "meeting-id";

    /**
     * The id of the conference
     */
    public String meetingId;

    /**
     * The name of the conference
     */
    public String name;

    /** Initializes a new <tt>ConferenceModifiedIQ</tt> instance. */
    private ConferenceModifiedIQ(Builder b)
    {
        super(ELEMENT, NAMESPACE);

        if (b.meetingId == null)
        {
            throw new IllegalArgumentException("meeting-id must be set for conference-modified IQ");
        }
        meetingId = b.meetingId;

        if (b.conferenceName == null)
        {
            throw new IllegalArgumentException("name must be set for conference-modified IQ");
        }
        name = b.conferenceName;

        for (AbstractConferenceEntity ce: b.conferenceEntities) {
            super.addExtension(ce);
        }

        if (b.sources != null) {
            super.addExtension(b.sources);
        }
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.optAttribute(MEETING_ID_ATTR_NAME, meetingId);
        xml.optAttribute(NAME_ATTR_NAME, name);

        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement();

        return xml;
    }

    public static Builder getBuilder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String conferenceName;

        private String meetingId;

        private final List<AbstractConferenceEntity> conferenceEntities = new ArrayList<>();

        private Sources sources;

        private Builder() {
        }

        public Builder addConferenceEntity(AbstractConferenceEntity entity)
        {
            conferenceEntities.add(entity);

            return this;
        }

        public Builder setConferenceName(String name)
        {
            conferenceName = name;

            return this;
        }

        public Builder setMeetingId(String id)
        {
            meetingId = id;

            return this;
        }

        public Builder setSources(Sources s)
        {
            sources = s;

            return this;
        }

        public ConferenceModifiedIQ build()
        {
            return new ConferenceModifiedIQ(this);
        }
    }
}
