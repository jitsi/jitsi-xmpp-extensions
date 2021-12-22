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
     * The Colibri2 XML namespace.
     */
    public static final String NAMESPACE = "jitsi:colibri2";


    /** Initializes a new {@link ConferenceModifyIQ} instance. */
    protected AbstractConferenceModificationIQ(Builder<I> b, String element)
    {
        super(b, element, NAMESPACE);

        for (AbstractConferenceEntity ce: b.conferenceEntities)
        {
            addExtension(ce);
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
     * Get relays described by the message.
     */
    public @NotNull List<Colibri2Relay> getRelays()
    {
        return getExtensions(Colibri2Relay.class);
    }

    public abstract static class Builder<I extends AbstractConferenceModificationIQ>
        extends IqBuilder<Builder<I>, I>
    {
        private final List<AbstractConferenceEntity> conferenceEntities = new ArrayList<>();

        protected Builder(IqData iqCommon)
        {
            super(iqCommon);
        }

        protected Builder(XMPPConnection connection)
        {
            super(connection);
        }

        protected Builder(String stanzaId)
        {
            super(stanzaId);
        }

        public Builder<I> addConferenceEntity(AbstractConferenceEntity entity)
        {
            conferenceEntities.add(entity);

            return this;
        }

        public Builder<I> addEndpoint(Colibri2Endpoint ep)
        {
            return addConferenceEntity(ep);
        }

        public Builder<I> addRelay(Colibri2Relay r)
        {
            return addConferenceEntity(r);
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
