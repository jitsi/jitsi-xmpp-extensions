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

import javax.xml.namespace.*;
import java.util.LinkedList;
import java.util.List;

/**
 * An endpoint in Colibri2 signaling.
 */
public class Colibri2Endpoint
    extends AbstractConferenceEntity
{
    /**
     * The XML element name of the Colibri2 Colibri2Endpoint element.
     */
    public static final String ELEMENT = "endpoint";

    /**
     * The qualified name of the Colibri2Endpoint element
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the "stats-id" attribute.
     */
    public static final String STATS_ID_ATTR_NAME = "stats-id";

    /**
     * Construct Colibri2Endpoint.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Colibri2Endpoint()
    {
        super(ELEMENT);
    }

    /**
     * Construct endpoint from a builder - used by Builder#build().
     */
    private Colibri2Endpoint(Builder b)
    {
        super(b, ELEMENT);

        if (b.statsId != null)
        {
            setAttribute(STATS_ID_ATTR_NAME, b.statsId);
        }

        if (b.forceMute != null)
        {
            addChildExtension(b.forceMute);
        }

        for (Capability c : b.capabilities)
        {
            addChildExtension(c);
        }
    }

    /**
     * Get the stats-id of the endpoint.
     */
    public @Nullable String getStatsId()
    {
        return getAttributeAsString(STATS_ID_ATTR_NAME);
    }

    /**
     * @return the force-mute extension of this {@link Colibri2Endpoint}, if it has one.
     */
    public @Nullable ForceMute getForceMute()
    {
        return getFirstChildOfType(ForceMute.class);
    }

    /**
     * @return the list of this endpoint's capabilities.
     */
    public List<Capability> getCapabilities()
    {
        return getChildExtensionsOfType(Capability.class);
    }

    /**
     * Get a builder for Colibri2Endpoint objects.
     */
    @Contract(" -> new")
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * @param capabilityName the capability name to check for.
     * @return true if this endpoint has the capability of the given name.
     */
    public boolean hasCapability(String capabilityName)
    {
        return getCapabilities().stream().anyMatch(c -> capabilityName.equals(c.getName()));
    }

    /**
     * Builder for Colibri2Endpoint objects.
     */
    public static class Builder extends AbstractConferenceEntity.Builder
    {
        /**
         * The stats-id of the endpoint being built.
         */
        private String statsId;

        /**
         * The force-mute element of the endpoint being built.
         */
        @Nullable private ForceMute forceMute = null;

        /**
         * The list of this endpoint's capabilities.
         */
        private final List<Capability> capabilities = new LinkedList<>();

        private Builder()
        {
            super();
        }

        /**
         * Set the stats-id for the endpoint being built.
         */
        public Builder setStatsId(String id)
        {
            this.statsId = id;

            return this;
        }

        /**
         * Sets the force-mute element of the endpoint being built.
         */
        public Builder setForceMute(@Nullable ForceMute forceMute)
        {
            this.forceMute = forceMute;
            return this;
        }

        /**
         * Sets the force-mute element of the endpoint being built.
         */
        public Builder setForceMute(boolean audio, boolean video)
        {
            this.forceMute = new ForceMute(audio, video);
            return this;
        }

        /**
         * Adds next capability to the list of this endpoint's capabilities.
         * @param capabilityName - the name of the capability to add.
         */
        public Builder addCapability(String capabilityName)
        {
            capabilities.add(new Capability(capabilityName));
            return this;
        }

        @Contract(" -> new")
        public @NotNull Colibri2Endpoint build()
        {
            return new Colibri2Endpoint(this);
        }
    }

    public static class Provider extends AbstractConferenceEntity.Provider<Colibri2Endpoint>
    {
        /**
         * Creates a new packet provider for Colibri2Endpoint packet extensions.
         */
        public Provider()
        {
            super(Colibri2Endpoint.class);
        }
    }
}
