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
     * The name of the <tt>ice-controlling</tt> attribute.
     */
    public static final String ICE_CONTROLLING_ATTR_NAME = "ice-controlling";

    /**
     * Default value for the ice-controlling attribute.
     */
    public static final boolean ICE_CONTROLLING_DEFAULT = false;

    /**
     * The name of the <tt>use-unique-port</tt> attribute.
     */
    public static final String USE_UNIQUE_PORT_ATTR_NAME = "use-unique-port";

    /**
     * Default value for the use-unique-port attribute.
     */
    public static final boolean USE_UNIQUE_PORT_DEFAULT = false;

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

        if (b.iceControlling != ICE_CONTROLLING_DEFAULT)
        {
            setAttribute(ICE_CONTROLLING_ATTR_NAME, b.iceControlling);
        }

        if (b.useUniquePort != USE_UNIQUE_PORT_DEFAULT)
        {
            setAttribute(USE_UNIQUE_PORT_ATTR_NAME, b.useUniquePort);
        }

        if (b.iceUdpExtension != null)
        {
            addChildExtension(b.iceUdpExtension);
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
     * Gets whether the transport is the initiator.  Return may be null if not set.
     */
    public boolean getIceControlling()
    {
        String iceControlling = getAttributeAsString(ICE_CONTROLLING_ATTR_NAME);
        return iceControlling == null ? ICE_CONTROLLING_DEFAULT : Boolean.parseBoolean(iceControlling);
    }

    /**
     * Gets whether a unique candidate port should be used.  Only meaningful
     * in a conference-modify request.
     */
    public boolean getUseUniquePort()
    {
        String use = getAttributeAsString(USE_UNIQUE_PORT_ATTR_NAME);
        return use == null ? USE_UNIQUE_PORT_DEFAULT : Boolean.parseBoolean(use);
    }

    /**
     * Return the contained ICE UDP Transport object, or null.
     */
    public @Nullable IceUdpTransportPacketExtension getIceUdpTransport()
    {
        return getChildExtension(IceUdpTransportPacketExtension.class);
    }

    /**
     * Get a builder for Transport objects.
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Transport objects.
     */
    public static final class Builder
    {
        private boolean useUniquePort = USE_UNIQUE_PORT_DEFAULT;

        private boolean iceControlling = ICE_CONTROLLING_DEFAULT;

        private IceUdpTransportPacketExtension iceUdpExtension;

        public Builder setIceUdpExtension(IceUdpTransportPacketExtension iceUdpExtension)
        {
            this.iceUdpExtension = iceUdpExtension;
            return this;
        }

        public Builder setUseUniquePort(boolean useUniquePort)
        {
            this.useUniquePort = useUniquePort;
            return this;
        }

        public Builder setIceControlling(boolean i)
        {
            this.iceControlling = i;
            return this;
        }

        private Builder()
        {
        }

        @Contract(" -> new")
        public @NotNull Transport build()
        {
            return new Transport(this);
        }
    }
}
