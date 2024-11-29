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

package org.jitsi.xmpp.extensions.colibri;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;

import java.util.*;

/**
 * A {@link SimpleIQ} which functions as a container for JVB API messages.
 * Designed to be used with an added
 * {@link org.jitsi.xmpp.extensions.JsonPacketExtension} child extension which
 * contains the message payload
 */
public class JvbApiIq extends SimpleIQ
{
    public static final String ELEMENT_NAME = "jvb-api";

    public static final String NAMESPACE =
        ColibriConferenceIQ.NAMESPACE + "/v2";

    public JvbApiIq()
    {
        super(ELEMENT_NAME, NAMESPACE);
    }


    /**
     * Helper to retrieve an extension by element name (but without an explicit
     * namespace, which all the existing getExtension methods require)
     *
     * @param elementName the element name to search for
     * @return the first {@link ExtensionElement} with an element name matchiing
     * the given one, or null if none is found.
     */
    private ExtensionElement getExtensionByElementName(String elementName)
    {
        return getExtensions().stream()
            .filter(e -> e.getElementName().equals(elementName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Return the content of the first {@link JsonPacketExtension} child
     * extension, or null if none is present.
     * @return the content of the first {@link JsonPacketExtension}, or null
     * if none is found
     */
    String getJsonContent()
    {
        JsonPacketExtension jsonPE =
            (JsonPacketExtension) getExtensionByElementName(JsonPacketExtension.ELEMENT_NAME);
        if (jsonPE != null)
        {
            return jsonPE.getJsonBody();
        }
        return null;
    }
}
