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
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;

public class Sctp
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 {@link Sctp} element.
     */
    public static final String ELEMENT = "sctp";

    /**
     * The XML namespace of the Colibri2 {@link Sctp} element
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the <tt>audio</tt> attribute.
     */
    public static final String PORT_ATTR_NAME = "port";

    /**
     * The name of the <tt>audio</tt> attribute.
     */
    public static final String ROLE_ATTR_NAME = "role";

    private Sctp(Builder b)
    {
        this();

        if (b.role != null)
        {
            setAttribute(ROLE_ATTR_NAME, b.role.toString().toLowerCase());
        }
        if (b.port != null)
        {
            setAttribute(PORT_ATTR_NAME, b.port.toString());
        }
    }

    public Sctp()
    {
        super(NAMESPACE, ELEMENT);
    }

    @Nullable
    public Role getRole()
    {
        String roleString = getAttributeAsString(ROLE_ATTR_NAME);
        if (roleString == null) return null;
        return Role.valueOf(roleString.toUpperCase());
    }

    @Nullable
    public Integer getPort()
    {
        String portString = getAttributeAsString(PORT_ATTR_NAME);
        if (portString == null) return null;
        return Integer.parseInt(portString);
    }

    public static class Provider extends DefaultPacketExtensionProvider<Sctp>
    {
        public Provider()
        {
            super(Sctp.class);
        }

        @Override
        public Sctp parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
                throws XmlPullParserException, IOException, SmackParsingException
        {
            Sctp sctp = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            String role = sctp.getAttributeAsString(ROLE_ATTR_NAME);
            if (role != null)
            {
                try
                {
                    Role.parseString(role);
                }
                catch (IllegalArgumentException e)
                {
                    throw new SmackParsingException("Invalid value for the '" + ROLE_ATTR_NAME +"' attribute: " + role);
                }
            }

            String port = sctp.getAttributeAsString(PORT_ATTR_NAME);
            if (port != null)
            {
                try
                {
                    Integer.parseInt(port);
                }
                catch (NumberFormatException e)
                {
                    throw new SmackParsingException("Invalid value for the '" + PORT_ATTR_NAME +"' attribute: " + port);
                }
            }

            return sctp;
        }
    }

    public static class Builder
    {
        private Integer port;
        private Role role;

        public Builder setPort(int port)
        {
            this.port = port;
            return this;
        }

        public Builder setRole(@NotNull Role role)
        {
            this.role = role;
            return this;
        }

        public Sctp build()
        {
            return new Sctp(this);
        }
    }

    public enum Role
    {
        CLIENT,
        SERVER;

        public static Role parseString(String s)
        {
            return valueOf(s.toUpperCase());
        }
    }
}
