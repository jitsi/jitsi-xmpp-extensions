/*
 * Copyright @ 2018 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.coin;

import java.util.*;

import org.jitsi.xmpp.extensions.*;

import org.jivesoftware.smack.util.*;

/**
 * User roles packet extension.
 *
 * @author Sebastien Vincent
 */
public class UserRolesPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that user roles belongs to.
     */
    public static final String NAMESPACE = CoinIQ.NAMESPACE;

    /**
     * The name of the element that contains the user roles data.
     */
    public static final String ELEMENT_NAME = "roles";

    /**
     * Subject element name.
     */
    public static final String ELEMENT_ROLE = "entry";

    /**
     * List of roles.
     */
    private List<String> roles = new ArrayList<String>();

    /**
     * Constructor.
     */
    public UserRolesPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    /**
     * Add roles.
     *
     * @param role role to add
     */
    public void addRoles(String role)
    {
        roles.add(role);
    }

    /**
     * Get list of roles.
     *
     * @return list of roles
     */
    public List<String> getRoles()
    {
        return roles;
    }

    /**
     * The child elements content.
     * @return the child elements content.
     */
    @Override
    public XmlStringBuilder getChildElementBuilder()
    {
        XmlStringBuilder xml = new XmlStringBuilder();

        for(String role : roles)
        {
            xml.optElement(ELEMENT_ROLE, role);
        }

        return xml;
    }
}
