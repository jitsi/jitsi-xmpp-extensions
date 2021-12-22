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
import org.jitsi.utils.*;
import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;

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
     * The name of the "id" attribute.
     */
    public static final String ID_ATTR_NAME = "id";

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

        if (b.id == null)
        {
            throw new IllegalArgumentException("Endpoint ID must be set");
        }
        setAttribute(ID_ATTR_NAME, b.id);

        if (b.statsId != null)
        {
            setAttribute(STATS_ID_ATTR_NAME, b.statsId);
        }

        if (b.forceMute != null)
        {
            addChildExtension(b.forceMute);
        }
    }

    /**
     * Get the ID of the endpoint.
     */
    public @NotNull String getId()
    {
        return getAttributeAsString(ID_ATTR_NAME);
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
     * Get a builder for Colibri2Endpoint objects.
     */
    @Contract(" -> new")
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Colibri2Endpoint objects.
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

        /**
         * The force-mute element of the endpoint being built.
         */
        @Nullable private ForceMute forceMute = null;

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

        @Contract(" -> new")
        public @NotNull Colibri2Endpoint build()
        {
            return new Colibri2Endpoint(this);
        }
    }

    public static class Provider extends DefaultPacketExtensionProvider<Colibri2Endpoint>
    {
        /**
         * Creates a new packet provider for Colibri2Endpoint packet extensions.
         */
        public Provider()
        {
            super(Colibri2Endpoint.class);
        }

        @Override
        public Colibri2Endpoint parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
        {
            Colibri2Endpoint e = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            String type = e.getAttributeAsString(ID_ATTR_NAME);
            if (type == null)
            {
                throw new SmackParsingException.RequiredAttributeMissingException(ID_ATTR_NAME);
            }

            return e;
        }
    }
}
