/*
 * Copyright @ 2025 - present 8x8, Inc.
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
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import java.io.IOException

class Notification(
    val type: Types,
    val id: String,
    val description: String? = null
) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    init {
        setAttribute(ID_ATTR_NAME, id)
        setAttribute(TYPE_ATTR_NAME, type.toString().lowercase())
        description?.let {
            setAttribute(DESCRIPTION_ATTR_NAME, it)
        }
    }

    enum class Types(val value: String) {
        CONNECT_FAILED("connect-failed"),
        ICE_FAILED("ice-failed")
    }

    companion object {
        const val ELEMENT = "notification"
        const val NAMESPACE = ConferenceModifyIQ.NAMESPACE
        const val ID_ATTR_NAME = "id"
        const val TYPE_ATTR_NAME = "type"
        const val DESCRIPTION_ATTR_NAME = "description"
    }

    class Provider : DefaultPacketExtensionProvider<Notification>(Notification::class.java) {
        @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
        override fun parse(parser: XmlPullParser, depth: Int, xml: XmlEnvironment?): Notification {
            val id = parser.getAttributeValue("", ID_ATTR_NAME)
                ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'id' attribute")
            val typeStr = parser.getAttributeValue("", TYPE_ATTR_NAME)
                ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'type' attribute")
            val type = try {
                Types.valueOf(typeStr.uppercase().replace("-", "_"))
            } catch (e: Exception) {
                throw SmackParsingException("Invalid 'type': $typeStr")
            }
            val description = parser.getAttributeValue("", DESCRIPTION_ATTR_NAME)

            return Notification(type, id, description)
        }
    }
}
