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
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;
import org.jivesoftware.smackx.muc.*;

import javax.xml.namespace.*;
import java.io.*;
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
     * The name of the "muc-role" attribute.
     */
    public static final String MUC_ROLE_ATTR_NAME = "muc-role";

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

        if (b.mucRole != null)
        {
            setAttribute(MUC_ROLE_ATTR_NAME, b.mucRole.toString());
        }

        if (b.forceMute != null)
        {
            addChildExtension(b.forceMute);
        }

        for (Capability c : b.capabilities)
        {
            addChildExtension(c);
        }

        if (b.initialLastN != null)
        {
            addChildExtension((b.initialLastN));
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
     * Get the muc-role of the endpoint.
     */
    public @Nullable MUCRole getMucRole()
    {
        return MUCRole.fromString(getAttributeAsString(MUC_ROLE_ATTR_NAME));
    }

    /**
     * @return the force-mute extension of this {@link Colibri2Endpoint}, if it has one.
     */
    public @Nullable ForceMute getForceMute()
    {
        return getFirstChildOfType(ForceMute.class);
    }

    /**
     * @return the initial-last-n extension of this {@link Colibri2Endpoint}, if it has one.
     */
    public @Nullable InitialLastN getInitialLastN()
    {
        return getFirstChildOfType(InitialLastN.class);
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
         * The muc-role of the endpoint being built.
         */
        private MUCRole mucRole;

        /**
         * The force-mute element of the endpoint being built.
         */
        @Nullable private ForceMute forceMute = null;

        /**
         * The list of this endpoint's capabilities.
         */
        private final List<Capability> capabilities = new LinkedList<>();

        @Nullable
        private InitialLastN initialLastN = null;

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
         * Set the muc-role for the endpoint being built.
         */
        public Builder setMucRole(MUCRole mucRole)
        {
            this.mucRole = mucRole;

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

        public Builder setInitialLastN(int initialLastN)
        {
            return setInitialLastN(new InitialLastN(initialLastN));
        }

        public Builder setInitialLastN(@Nullable InitialLastN initialLastN)
        {
            this.initialLastN = initialLastN;
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

        @Override
        public Colibri2Endpoint parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
        {
            Colibri2Endpoint ep = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            String mucRole = ep.getAttributeAsString(MUC_ROLE_ATTR_NAME);
            if (mucRole != null)
            {
                try
                {
                    MUCRole.fromString(mucRole);
                }
                catch (IllegalArgumentException e)
                {
                    throw new SmackParsingException(MUC_ROLE_ATTR_NAME + ":" + e.getMessage());
                }
            }
            return ep;
        }
    }
}
