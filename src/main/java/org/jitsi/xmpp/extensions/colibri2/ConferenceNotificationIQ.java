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

import org.jetbrains.annotations.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;
import java.util.*;

public class ConferenceNotificationIQ extends IQ
{
    /**
     * The XML element name of the Colibri2 <tt>conference-notification</tt> IQ.
     */
    public static final String ELEMENT = "conference-notification";

    /**
     * The Colibri2 XML namespace.
     */
    public static final String NAMESPACE = AbstractConferenceModificationIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The XML name of the <tt>meeting-id</tt> attribute.
     */
    public static final String MEETING_ID_ATTR_NAME = "meeting-id";


    @Contract("_ -> new")
    public static @NotNull ConferenceNotificationIQ.Builder builder(IqData iqData)
    {
        return new ConferenceNotificationIQ.Builder(iqData);
    }

    @Contract("_ -> new")
    public static @NotNull ConferenceNotificationIQ.Builder builder(String stanzaId)
    {
        return new ConferenceNotificationIQ.Builder(stanzaId);
    }

    @Contract("_ -> new")
    public static @NotNull ConferenceNotificationIQ.Builder builder(XMPPConnection connection)
    {
        return new ConferenceNotificationIQ.Builder(connection);
    }

    /**
     * The id of the conference
     */
    private final @NotNull String meetingId;

    /** Initializes a new {@link ConferenceModifyIQ} instance. */
    protected ConferenceNotificationIQ(ConferenceNotificationIQ.Builder b)
    {
        super(b, ELEMENT, NAMESPACE);

        if (b.meetingId == null)
        {
            throw new IllegalArgumentException("meeting-id must be set for " + ELEMENT + " IQ");
        }
        meetingId = b.meetingId;

        for (Colibri2Endpoint endpoint: b.endpoints)
        {
            addExtension(endpoint);
        }
    }

    /**
     * Get endpoints described by the message
     */
    public @NotNull List<Colibri2Endpoint> getEndpoints()
    {
        return getExtensions(Colibri2Endpoint.class);
    }

    /**
     * Get the ID of the conference.
     */
    public @NotNull String getMeetingId()
    {
        return meetingId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.attribute(MEETING_ID_ATTR_NAME, meetingId);

        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement();

        return xml;
    }


    public static class Builder
            extends IqBuilder<Builder, ConferenceNotificationIQ>
    {
        private final List<Colibri2Endpoint> endpoints = new ArrayList<>();
        private String meetingId;

        private Builder(IqData iqCommon)
        {
            super(iqCommon);
        }

        private Builder(XMPPConnection connection)
        {
            super(connection);
        }

        private Builder(String stanzaId)
        {
            super(stanzaId);
        }

        public Builder addEndpoint(Colibri2Endpoint endpoint)
        {
            endpoints.add(endpoint);
            return this;
        }

        public Builder setMeetingId(String meetingId)
        {
            this.meetingId = meetingId;
            return this;
        }

        @Override
        @Contract(" -> new")
        public ConferenceNotificationIQ build()
        {
            return new ConferenceNotificationIQ(this);
        }

        @Override
        public Builder getThis()
        {
            return this;
        }
    }

    public static class Provider extends IqProvider<ConferenceNotificationIQ>
    {
        @Override
        public ConferenceNotificationIQ parse(
                XmlPullParser parser,
                int initialDepth,
                IqData iqData,
                XmlEnvironment xmlEnvironment)
                throws XmlPullParserException, IOException, SmackParsingException
        {
            ConferenceNotificationIQ.Builder builder = ConferenceNotificationIQ.builder(iqData);
            String meetingId = parser.getAttributeValue(MEETING_ID_ATTR_NAME);
            if (meetingId == null)
            {
                throw new SmackParsingException.RequiredAttributeMissingException(MEETING_ID_ATTR_NAME);
            }
            builder.setMeetingId(meetingId);

            ConferenceNotificationIQ iq = builder.build();
            IqProviderUtils.parseExtensions(parser, initialDepth, iq);
            return iq;
        }
    }
}
