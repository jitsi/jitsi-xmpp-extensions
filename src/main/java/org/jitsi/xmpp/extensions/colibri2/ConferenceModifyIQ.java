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

public class ConferenceModifyIQ
    extends IQ
{
    /**
     * The XML element name of the Jitsi Videobridge <tt>conference-modify</tt> IQ.
     */
    public static final String ELEMENT = "conference-modify";

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

    protected final List<AbstractConferenceEntity> conferenceEntities;

    protected final List<AbstractConferenceEntity.Source> feedbackSources;

    /** Initializes a new <tt>ColibriConferenceIQ</tt> instance. */
    private ConferenceModifyIQ(List<AbstractConferenceEntity> conferenceEntities,
        List<AbstractConferenceEntity.Source> feedbackSources)
    {
        super(ELEMENT, NAMESPACE);

        if (conferenceEntities != null)
        {
            this.conferenceEntities = Collections.unmodifiableList(conferenceEntities);
        }
        else
        {
            this.conferenceEntities = Collections.emptyList();
        }

        if (feedbackSources != null)
        {
            this.feedbackSources = Collections.unmodifiableList(feedbackSources);
        }
        else
        {
            this.feedbackSources = null;
        }
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        return null;
    }

    public static Builder getBuilder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private List<AbstractConferenceEntity> conferenceEntities;

        private List<AbstractConferenceEntity.Source> feedbackSources;

        Builder() {

        }

        public Builder addConferenceEntity(AbstractConferenceEntity entity)
        {
            if (conferenceEntities == null)
            {
                conferenceEntities = new ArrayList<>();
            }

            conferenceEntities.add(entity);

            return this;
        }

        public Builder addFeedbackSource(AbstractConferenceEntity.Source source)
        {
            if (feedbackSources == null)
            {
                feedbackSources = new ArrayList<>();
            }

            feedbackSources.add(source);

            return this;
        }

        public ConferenceModifyIQ build()
        {
            return new ConferenceModifyIQ(conferenceEntities, feedbackSources);
        }
    }
}
