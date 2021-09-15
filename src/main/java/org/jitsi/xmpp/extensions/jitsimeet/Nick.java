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
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.provider.*;

import static org.jivesoftware.smackx.nick.packet.Nick.*;

/**
 * A custom Nick extension to use {@code AbstractPacketExtension} as base which will take care
 * of escaping unwanted chars from the name when creating xmpp messages.
 */
public class Nick
    extends AbstractPacketExtension
{
    /**
     * The display name.
     */
    private String name = null;

    /**
     * Constructs Nick extension using original namespace and element name.
     * @param name The display name.
     */
    public Nick(String name)
    {
        super(NAMESPACE, ELEMENT_NAME);

        setName(name);
    }

    /**
     * Returns the name.
     * @return the display name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the display name.
     * @param name the new value.
     */
    public void setName(String name)
    {
        this.name = name;

        this.setText(name);
    }

    /**
     * Registers this IQ provider into given <tt>ProviderManager</tt>.
     */
    public static void registerNickProvider()
    {
        ProviderManager.addExtensionProvider(
            QNAME.getLocalPart(), NAMESPACE, new DefaultPacketExtensionProvider<>(Nick.class));
    }
}
