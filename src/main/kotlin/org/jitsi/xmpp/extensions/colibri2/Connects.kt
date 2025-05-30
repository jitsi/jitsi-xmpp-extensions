/*
 * Copyright @ 2024 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.colibri2

import org.jitsi.xmpp.extensions.AbstractPacketExtension
import org.jitsi.xmpp.extensions.DefaultPacketExtensionProvider

class Connects : AbstractPacketExtension(NAMESPACE, ELEMENT) {

    fun getConnects(): List<Connect> = getChildExtensionsOfType(Connect::class.java)
    fun addConnect(connect: Connect) = addChildExtension(connect)

    companion object {
        const val ELEMENT = "connects"
        const val NAMESPACE = ConferenceModifyIQ.NAMESPACE
    }
}

class ConnectsProvider : DefaultPacketExtensionProvider<Connects>(Connects::class.java)
