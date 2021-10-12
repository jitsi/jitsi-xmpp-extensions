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
import org.jitsi.xmpp.extensions.jingle.*;

import javax.xml.namespace.*;

public class Transport
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 Transport element.
     */
    public static final String ELEMENT = "transport";

    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference-modify</tt> IQ.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the <tt>use-unique-port</tt> attribute.
     */
    public static final String USE_UNIQUE_PORT_ATTR_NAME = "use-unique-port";

    /**
     * Construct a Transport.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Transport()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct a transport from a builder - used by Builder#build().
     */
    private Transport(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        if (b.useUniquePort)
        {
            super.setAttribute(USE_UNIQUE_PORT_ATTR_NAME, true);
        }

        if (b.iceUdpExtension != null)
        {
            super.addChildExtension(b.iceUdpExtension);
        }
    }

    /**
     * Creates an {@link Transport} instance for the specified
     * <tt>namespace</tt> and <tt>elementName</tt>.
     *
     * @param namespace   the XML namespace for this element.
     * @param elementName the name of the element
     */
    protected Transport(String namespace, String elementName)
    {
        super(namespace, elementName);
    }

    /**
     * Gets whether a unique candidate port should be used.  Only meaningful
     * in a conference-modify request.
     */
    public boolean getUseUniquePort()
    {
        Object use = super.getAttribute(USE_UNIQUE_PORT_ATTR_NAME);
        if (use instanceof Boolean)
        {
            return (Boolean) use;
        }
        else if (use instanceof String)
        {
            return Boolean.parseBoolean((String) use);
        }
        return false;
    }

    /**
     * Return the contained ICE UDP Transport object, or null.
     */
    public IceUdpTransportPacketExtension getIceUdpTransport()
    {
        return getChildExtension(IceUdpTransportPacketExtension.class);
    }

    /**
     * Get a builder for Transport objects.
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Transport objects.
     */
    public static final class Builder
    {
        private boolean useUniquePort;

        private IceUdpTransportPacketExtension iceUdpExtension;

        public void setIceUdpExtension(IceUdpTransportPacketExtension iceUdpExtension)
        {
            this.iceUdpExtension = iceUdpExtension;
        }

        public void setUseUniquePort(boolean useUniquePort)
        {
            this.useUniquePort = useUniquePort;
        }

        private Builder()
        {
        }

        public Transport build()
        {
            return new Transport(this);
        }
    }
}
