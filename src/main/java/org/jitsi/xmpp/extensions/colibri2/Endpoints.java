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

public class Endpoints
    extends AbstractEndpointSet
{
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
        super(ELEMENT);
    }

    /**
     * Construct endpoints from a builder - used by Builder#build().
     */
    private Endpoints(Builder b)
    {
        super(b, ELEMENT);
    }

    /**
     * Get a builder for Endpoint objects.
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }

    public static class Builder extends AbstractEndpointSet.Builder
    {
        public Endpoints build()
        {
            return new Endpoints(this);
        }
    }
}
