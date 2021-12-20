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
import org.jitsi.xmpp.extensions.*;

import java.util.*;

/**
 * An abstract representation of a participant in a conference as described by Colibri signaling.
 * This is the base class of both Colibri2Endpoint and Colibri2Relay.
 */
public abstract class AbstractConferenceEntity
    extends AbstractPacketExtension
{
    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference-modify</tt> IQ.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    public static final String EXPIRE_ATTR_NAME = "expire";

    protected AbstractConferenceEntity(String element)
    {
        super(NAMESPACE, element);
    }

    /**
     * Construct a source from a builder - used by Builder#build().
     */
    protected AbstractConferenceEntity(Builder b, String element)
    {
        super(NAMESPACE, element);

        if (b.expire)
        {
            setAttribute(EXPIRE_ATTR_NAME, b.expire);
        }

        for (Media m: b.medias)
        {
            addChildExtension(m);
        }

        if (b.transport != null)
        {
            addChildExtension(b.transport);
        }

        if (b.sources != null)
        {
            addChildExtension(b.sources);
        }
    }

    /**
     * Get the media associated with this conference entity.
     */
    public @NotNull List<Media> getMedia()
    {
        return getChildExtensionsOfType(Media.class);
    }

    /**
     * Get the transport associated with this conference entity.
     */
    public @Nullable Transport getTransport()
    {
        return getFirstChildOfType(Transport.class);
    }

    /**
     * Get the sources associated with this conference entity.
     */
    public @Nullable Sources getSources()
    {
        return getFirstChildOfType(Sources.class);
    }

    /**
     * Get whether this conference entity was marked to expire.
     */
    public boolean getExpire()
    {
        /* Anything other than "true" (including null) parses as "false" to parseBoolean, which is fine. */
        return Boolean.parseBoolean(getAttributeAsString(EXPIRE_ATTR_NAME));
    }

    /**
     * Builder for conference entities.
     */
    public abstract static class Builder
    {
        /** The transport. */
        private Transport transport;

        private final List<Media> medias = new ArrayList<>();

        private boolean expire = false;

        /* Do we need SctpConnection here? */

        private Sources sources;

        protected Builder()
        {
        }

        public Builder addMedia(Media m)
        {
            medias.add(m);

            return this;
        }

        public Builder setTransport(Transport t)
        {
            transport = t;

            return this;
        }

        public Builder setSources(Sources s)
        {
            sources = s;

            return this;
        }

        public Builder setExpire(boolean e)
        {
            expire = e;

            return this;
        }

        @Contract(" -> new")
        public abstract @NotNull AbstractPacketExtension build();
    }
}
