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

/**
 * An endpoint in Colibri2 signaling.
 */
public class Endpoint
    extends AbstractConferenceEntity
{
    /**
     * The XML element name of the Colibri2 Endpoint element.
     */
    public static final String ELEMENT = "endpoint";

    /**
     * The qualified name of the Endpoint element
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the "id" attribute.
     */
    public static final String ID_ATTR_NAME = "id";

    /**
     * The name of the "stats-id" attribute.
     */
    public static final String STATS_ID_ATTR_NAME = "stats-id";

    /**
     * Construct Endpoint.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Endpoint()
    {
        super(ELEMENT);
    }

    /**
     * Construct endpoint from a builder - used by Builder#build().
     */
    private Endpoint(Builder b)
    {
        super(b, ELEMENT);

        /* Should this enforce a non-null id? */
        if (b.id != null)
        {
            super.setAttribute(ID_ATTR_NAME, b.id);
        }

        if (b.statsId != null)
        {
            super.setAttribute(STATS_ID_ATTR_NAME, b.statsId);
        }
    }

    /**
     * Get the ID of the endpoint.
     */
    public String getId()
    {
        return super.getAttributeAsString(ID_ATTR_NAME);
    }

    /**
     * Get the stats-id of the endpoint.
     */
    public String getStatsId()
    {
        return super.getAttributeAsString(STATS_ID_ATTR_NAME);
    }

    /**
     * Get a builder for Endpoint objects.
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Endpoint objects.
     */
    public static class Builder extends AbstractConferenceEntity.Builder
    {
        /**
         * The id of the endpoint being built.
         */
        private String id;

        /**
         * The stats-id of the endpoint being built.
         */
        private String statsId;

        private Builder()
        {
            super();
        }

        /**
         * Set the id for the endpoint being built.
         */
        public Builder setId(String id)
        {
            this.id = id;

            return this;
        }

        /**
         * Set the stats-id for the endpoint being built.
         */
        public Builder setStatsId(String id)
        {
            this.statsId = id;

            return this;
        }

        public Endpoint build()
        {
            return new Endpoint(this);
        }
    }
}
