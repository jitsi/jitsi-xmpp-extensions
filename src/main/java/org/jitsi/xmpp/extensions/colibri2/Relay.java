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

import javax.xml.namespace.*;
import java.util.*;

public class Relay
    extends AbstractConferenceEntity
{
    /**
     * The XML element name of the Colibri2 Relay element.
     */
    public static final String ELEMENT = "relay";

    /**
     * The qualified name of the Relay element
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the "id" attribute.
     */
    public static final String ID_ATTR_NAME = "id";

    /**
     * Construct Relay.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Relay()
    {
        super(ELEMENT);
    }

    /**
     * Construct endpoint from a builder - used by Builder#build().
     */
    private Relay(Builder b)
    {
        super(b, ELEMENT);

        if (b.id != null)
        {
            super.setAttribute(ID_ATTR_NAME, b.id);
        }

        for (AbstractEndpointSet e: b.endpointSets) {
            super.addChildExtension(e);
        }
    }

    /**
     * Get the ID of the relay.
     */
    public String getId()
    {
        return super.getAttributeAsString(ID_ATTR_NAME);
    }

    /**
     * Get a builder for Relay objects.
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Relay objects.
     */
    public static class Builder extends AbstractConferenceEntity.Builder
    {
        /**
         * The id of the relay being built.
         */
        private String id;

        /**
         * Remote endpoints sent from the relay.
         */
        private final List<AbstractEndpointSet> endpointSets = new ArrayList<>();

        public Builder setId(String id)
        {
            this.id = id;

            return this;
        }

        public Builder addEndpointSet(AbstractEndpointSet e)
        {
            endpointSets.add(e);

            return this;
        }

        private Builder()
        {
            super();
        }

        public Relay build()
        {
            return new Relay(this);
        }
    }
}
