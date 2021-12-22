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

public class ConferenceModifyIQ
    extends AbstractConferenceModificationIQ<ConferenceModifyIQ>
{
    /**
     * The XML element name of the Colibri2 <tt>conference-modify</tt> IQ.
     */
    public static final String ELEMENT = "conference-modify";

    /**
     * The XML name of the attribute which controls whether rtcstats reporting should be enabled.
     */
    public static final String RTCSTATS_ENABLED_ATTR_NAME = "rtcstats-enabled";

    /**
     * The default value of the "rtcstats-enabled" attribute.
     */
    public static final boolean RTCSTATS_ENABLED_DEFAULT = true;

    /**
     * The XML name of the attribute which controls whether callstats reporting should be enabled.
     */
    public static final String CALLSTATS_ENABLED_ATTR_NAME = "callstats-enabled";

    /**
     * The default value of the "callstats-enabled" attribute.
     */
    public static final boolean CALLSTATS_ENABLED_DEFAULT = true;

    /**
     * The XML name of the <tt>name</tt> attribute.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * The XML name of the <tt>meeting-id</tt> attribute.
     */
    public static final String MEETING_ID_ATTR_NAME = "meeting-id";

    /**
     * The name of the attribute which controls whether this is a request to create a new conference, or modify an
     * existing one. This defaults to "false" when the XML attribute is missing.
     */
    public static final String CREATE_ATTR_NAME = "create";

    /**
     * The default value of the "create" attribute.
     */
    public static final boolean CREATE_DEFAULT = false;

    /**
     * The id of the conference
     */
    private final String meetingId;

    /**
     * The name of the conference
     */
    private final String name;

    /**
     * Whether rtcstats reporting should be enabled for the conference. This defaults to "true" if the XML attribute is
     * missing.
     */
    private final boolean rtcstatsEnabled;

    /**
     * Whether callstats reporting should be enabled for the conference. This defaults to "true" if the XML attribute is
     * missing.
     */
    private final boolean callstatsEnabled;

    private final boolean create;

    /** Initializes a new {@link ConferenceModifyIQ} instance. */
    private ConferenceModifyIQ(Builder b)
    {
        super(b, ELEMENT);

        rtcstatsEnabled = b.rtcstatsEnabled;
        callstatsEnabled = b.callstatsEnabled;
        create = b.create;

        if (b.meetingId == null)
        {
            throw new IllegalArgumentException("meeting-id must be set for " + ELEMENT + " IQ");
        }
        meetingId = b.meetingId;

        if (b.conferenceName == null)
        {
            throw new IllegalArgumentException("name must be set for " + ELEMENT + " IQ");
        }
        name = b.conferenceName;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.attribute(MEETING_ID_ATTR_NAME, meetingId);
        xml.attribute(NAME_ATTR_NAME, name);

        if (rtcstatsEnabled != RTCSTATS_ENABLED_DEFAULT)
        {
            xml.attribute(RTCSTATS_ENABLED_ATTR_NAME, rtcstatsEnabled);
        }
        if (callstatsEnabled != CALLSTATS_ENABLED_DEFAULT)
        {
            xml.attribute(CALLSTATS_ENABLED_ATTR_NAME, callstatsEnabled);
        }
        if (create != CREATE_DEFAULT)
        {
            xml.attribute(CREATE_ATTR_NAME, create);
        }

        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement();

        return xml;
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

    public boolean isRtcstatsEnabled()
    {
        return rtcstatsEnabled;
    }

    public boolean isCallstatsEnabled()
    {
        return callstatsEnabled;
    }

    /**
     * @return "true" iff this os a request for a new conference to be created.
     */
    public boolean getCreate()
    {
        return create;
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(XMPPConnection connection)
    {
        return new Builder(connection);
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(IqData iqData)
    {
        return new Builder(iqData);
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(String stanzaId)
    {
        return new Builder(stanzaId);
    }

    public static final class Builder
        extends AbstractConferenceModificationIQ.Builder<ConferenceModifyIQ>
    {
        private boolean rtcstatsEnabled = RTCSTATS_ENABLED_DEFAULT;
        private boolean callstatsEnabled = CALLSTATS_ENABLED_DEFAULT;
        private boolean create = CREATE_DEFAULT;
        private String conferenceName;
        private String meetingId;

        private Builder(IqData iqCommon) {
            super(iqCommon);
        }

        private Builder(XMPPConnection connection) {
            super(connection);
        }

        private Builder(String stanzaId) {
            super(stanzaId);
        }

        public Builder setRtcstatsEnabled(boolean rtcstatsEnabled)
        {
            this.rtcstatsEnabled = rtcstatsEnabled;
            return this;
        }

        public Builder setCallstatsEnabled(boolean callstatsEnabled)
        {
            this.callstatsEnabled = callstatsEnabled;
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

        public Builder setCreate(boolean create)
        {
            this.create = create;

            return this;
        }

        @Override
        @Contract(" -> new")
        public @NotNull ConferenceModifyIQ build()
        {
            return new ConferenceModifyIQ(this);
        }
    }
}
