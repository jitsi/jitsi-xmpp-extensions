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

public class Colibri2Relay
    extends AbstractConferenceEntity
{
    /**
     * The XML element name of the Colibri2 Colibri2Relay element.
     */
    public static final String ELEMENT = "relay";

    /**
     * The qualified name of the Colibri2Relay element
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the "mesh-id" attribute.
     */
    public static final String MESH_ID_ATTR_NAME = "mesh-id";

    /**
     * Construct Colibri2Relay.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Colibri2Relay()
    {
        super(ELEMENT);
    }

    /**
     * Construct endpoint from a builder - used by Builder#build().
     */
    private Colibri2Relay(Builder b)
    {
        super(b, ELEMENT);

        if (b.meshId != null)
        {
            setAttribute(MESH_ID_ATTR_NAME, b.meshId);
        }

        if (b.endpoints != null)
        {
            addChildExtension(b.endpoints);
        }
    }

    /**
     * Get the mesh-id of the relay connection.
     */
    public @Nullable String getMeshId()
    {
        return getAttributeAsString(MESH_ID_ATTR_NAME);
    }

    /**
     * Get the remote endpoints associated with this relay.
     */
    public @Nullable Endpoints getEndpoints()
    {
        return getChildExtension(Endpoints.class);
    }

    /**
     * Get a builder for Colibri2Relay objects.
     */
    @Contract(" -> new")
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Colibri2Relay objects.
     */
    public static class Builder extends AbstractConferenceEntity.Builder
    {
        /**
         * The mesh-id of the relay connection being built.
         */
        private String meshId;

        /**
         * Remote endpoints sent from the relay.
         */
        private Endpoints endpoints = null;

        /**
         * Set the mesg-id for the endpoint being built.
         */
        public Builder setMeshId(String id)
        {
            this.meshId = id;

            return this;
        }

        public Builder setEndpoints(Endpoints e)
        {
            endpoints = e;

            return this;
        }

        private Builder()
        {
            super();
        }

        @Contract(" -> new")
        public @NotNull Colibri2Relay build()
        {
            return new Colibri2Relay(this);
        }
    }

    public static class Provider extends AbstractConferenceEntity.Provider<Colibri2Relay>
    {
        /**
         * Creates a new packet provider for Colibri2Endpoint packet extensions.
         */
        public Provider()
        {
            super(Colibri2Relay.class);
        }
    }
}
