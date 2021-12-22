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
     * The XML namespace of Colibri2 {@link AbstractConferenceEntity} elements.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The XML name of the attribute which controls whether the entity should be created.
     */
    public static final String CREATE_ATTR_NAME = "create";

    /**
     * The default value of the "create" attribute.
     */
    public static final boolean CREATE_DEFAULT = false;

    /**
     * The XML name of the attribute which controls whether the entity should be expired.
     */
    public static final String EXPIRE_ATTR_NAME = "expire";

    /**
     * The default value of the "expire" attribute.
     */
    public static final boolean EXPIRE_DEFAULT = false;

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

        if (b.create != CREATE_DEFAULT)
        {
            setAttribute(CREATE_ATTR_NAME, b.create);
        }

        if (b.expire != EXPIRE_DEFAULT)
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
     * Get the medias associated with this conference entity.
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
     * Get whether this conference entity was marked to be created.
     */
    public boolean getCreate()
    {
        String create = getAttributeAsString(CREATE_ATTR_NAME);
        return create == null ? CREATE_DEFAULT : Boolean.parseBoolean(create);
    }

    /**
     * Get whether this conference entity was marked to expire.
     */
    public boolean getExpire()
    {
        String expire = getAttributeAsString(EXPIRE_ATTR_NAME);
        return expire == null ? EXPIRE_DEFAULT : Boolean.parseBoolean(expire);
    }

    /**
     * Builder for conference entities.
     */
    public abstract static class Builder
    {
        /** The transport. */
        private Transport transport;

        private final List<Media> medias = new ArrayList<>();

        private boolean create = CREATE_DEFAULT;

        private boolean expire = EXPIRE_DEFAULT;

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

        public Builder setCreate(boolean e)
        {
            create = e;

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
