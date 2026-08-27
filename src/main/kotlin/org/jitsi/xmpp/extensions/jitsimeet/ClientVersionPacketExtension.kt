/*
 * Copyright @ 2026 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.jitsimeet

import org.jitsi.xmpp.extensions.AbstractPacketExtension
import org.jitsi.xmpp.extensions.DefaultPacketExtensionProvider
import org.jivesoftware.smack.provider.ProviderManager

/**
 * The version of a client, which the client includes in the Jingle session-accept that it sends to the focus. The
 * focus uses it for logging, for example when a client does not advertise a capability that the deployment requires.
 */
class ClientVersionPacketExtension() : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    constructor(version: String) : this() {
        setAttribute(VERSION_ATTR_NAME, version)
    }

    val version: String?
        get() = getAttributeAsString(VERSION_ATTR_NAME)

    companion object {
        const val ELEMENT = "client-version"

        /** The namespace used for the signaling between a client and the focus. */
        const val NAMESPACE = ConferenceIq.NAMESPACE

        const val VERSION_ATTR_NAME = "version"

        fun registerProvider() {
            ProviderManager.addExtensionProvider(
                ELEMENT,
                NAMESPACE,
                DefaultPacketExtensionProvider(ClientVersionPacketExtension::class.java)
            )
        }
    }
}
