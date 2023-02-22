/*
 * Copyright @ 2023 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.visitors

import org.jitsi.xmpp.extensions.AbstractPacketExtension
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.provider.ExtensionElementProvider
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import java.io.IOException

class ConnectVnodePacketExtension(val vnode: String) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    init {
        setAttribute(VNODE_ATTR_NAME, vnode)
    }

    companion object {
        const val ELEMENT = "connect-vnode"
        const val NAMESPACE = VisitorsIq.NAMESPACE
        const val VNODE_ATTR_NAME = "vnode"
    }
}

class ConnectVnodePacketExtensionProvider : ExtensionElementProvider<ConnectVnodePacketExtension>() {
    @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
    override fun parse(parser: XmlPullParser, depth: Int, xml: XmlEnvironment?): ConnectVnodePacketExtension {
        val vnode = parser.getAttributeValue("", ConnectVnodePacketExtension.VNODE_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'vnode' attribute")
        return ConnectVnodePacketExtension(vnode)
    }
}
