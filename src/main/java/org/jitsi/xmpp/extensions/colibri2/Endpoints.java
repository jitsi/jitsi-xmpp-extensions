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

import javax.xml.namespace.*;
import java.util.*;

public class Endpoints
    extends AbstractPacketExtension
{
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The XML element name of the Colibri2 Endpoints element.
     */
    public static final String ELEMENT = "endpoints";

    /**
     * The qualified name of the Endpoints element
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * Construct Endpoints.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Endpoints()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct endpoints from a builder - used by Builder#build().
     */
    private Endpoints(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        for (Endpoint e: b.endpoints)
        {
            addChildExtension(e);
        }
    }

    /**
     * Get the endpoints in this endpoint set
     */
    public @NotNull List<Endpoint> getEndpoints()
    {
        return getChildExtensionsOfType(Endpoint.class);
    }

    /**
     * Get a builder for Endpoint objects.
     */
    @Contract(" -> new")
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<Endpoint> endpoints = new ArrayList<>();

        protected Builder()
        {
        }

        public Builder addEndpoint(Endpoint m)
        {
            endpoints.add(m);

            return this;
        }

        @Contract(" -> new")
        public @NotNull Endpoints build()
        {
            return new Endpoints(this);
        }
    }
}
