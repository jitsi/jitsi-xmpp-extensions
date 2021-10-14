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

import org.jitsi.xmpp.extensions.*;

import java.util.*;

/**
 * A collection of endpoints, in colibri2 signaling.  Base class of
 * <tt>endpoints</tt>, <tt>endpoint-add</tt>, and <tt>endpoint-remove</tt>.
 */
public abstract class AbstractEndpointSet
    extends AbstractPacketExtension
{
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    protected AbstractEndpointSet(String element)
    {
        super(NAMESPACE, element);
    }

    /**
     * Construct a source from a builder - used by Builder#build().
     */
    protected AbstractEndpointSet(Builder b, String element)
    {
        super(NAMESPACE, element);

        for (Endpoint e: b.endpoints)
        {
            super.addChildExtension(e);
        }
    }

    /**
     * Get the endpoints in this endpoint set
     */
    public List<Endpoint> getEndpoints()
    {
        return super.getChildExtensionsOfType(Endpoint.class);
    }

    /**
     * Builder for endpoint sets.
     */
    protected abstract static class Builder
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

        public abstract AbstractPacketExtension build();
    }
}
