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

public class ConferenceModifiedIQ
    extends AbstractConferenceModificationIQ<ConferenceModifiedIQ>
{
    /**
     * The XML element name of the Jitsi Videobridge <tt>conference-modified</tt> IQ.
     */
    public static final String ELEMENT = "conference-modified";

    /** Initializes a new <tt>ConferenceModifiedIQ</tt> instance. */
    private ConferenceModifiedIQ(Builder b)
    {
        super(b, ELEMENT);

        if (b.sources != null)
        {
            addExtension(b.sources);
        }
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement();

        return xml;
    }

    /**
     * Get source list in the message.
     */
    public @Nullable Sources getSources()
    {
        return getExtension(Sources.class);
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
        extends AbstractConferenceModificationIQ.Builder<ConferenceModifiedIQ>
    {
        private Sources sources;

        private Builder(IqData iqCommon) {
            super(iqCommon);
        }

        private Builder(XMPPConnection connection) {
            super(connection);
        }

        private Builder(String stanzaId) {
            super(stanzaId);
        }

        public Builder setSources(Sources s)
        {
            sources = s;

            return this;
        }

        @Override
        @Contract(" -> new")
        public @NotNull ConferenceModifiedIQ build()
        {
            return new ConferenceModifiedIQ(this);
        }
    }
}
