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
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import java.io.IOException
import java.net.URI

class Connect(
    val url: URI,
    val protocol: Protocols,
    val type: Types,
    audio: Boolean = false,
    video: Boolean = false
) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    init {
        setAttribute(URL_ATTR_NAME, url)
        setAttribute(PROTOCOL_ATTR_NAME, protocol.toString().lowercase())
        setAttribute(TYPE_ATTR_NAME, type.toString().lowercase())
        if (audio) {
            setAttribute(AUDIO_ATTR_NAME, true)
        }
        if (video) {
            setAttribute(VIDEO_ATTR_NAME, true)
        }
    }

    val audio: Boolean
        get() = getAttributeAsString(AUDIO_ATTR_NAME)?.toBoolean() ?: false
    val video: Boolean
        get() = getAttributeAsString(VIDEO_ATTR_NAME)?.toBoolean() ?: false

    enum class Protocols(val value: String) {
        MEDIAJSON("mediajson")
    }

    enum class Types(val value: String) {
        RECORDER("recorder"),
        TRANSCRIBER("transcriber")
    }

    companion object {
        const val ELEMENT = "connect"
        const val NAMESPACE = ConferenceModifyIQ.NAMESPACE
        const val URL_ATTR_NAME = "url"
        const val PROTOCOL_ATTR_NAME = "protocol"
        const val TYPE_ATTR_NAME = "type"
        const val AUDIO_ATTR_NAME = "audio"
        const val VIDEO_ATTR_NAME = "video"
    }
}

class ConnectProvider : DefaultPacketExtensionProvider<Connect>(Connect::class.java) {
    @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
    override fun parse(parser: XmlPullParser, depth: Int, xml: XmlEnvironment?): Connect {
        val url = parser.getAttributeValue("", Connect.URL_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'url' attribute")
        val uri = try {
            URI(url)
        } catch (e: Exception) {
            throw SmackParsingException("Invalid 'url': ${e.message}")
        }
        val audio = parser.getAttributeValue("", Connect.AUDIO_ATTR_NAME)?.toBoolean() ?: false
        val video = parser.getAttributeValue("", Connect.VIDEO_ATTR_NAME)?.toBoolean() ?: false
        val protocolStr = parser.getAttributeValue("", Connect.PROTOCOL_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'protocol' attribute")
        val protocol = try {
            Connect.Protocols.valueOf(protocolStr.uppercase())
        } catch (e: Exception) {
            throw SmackParsingException("Invalid 'protocol': $protocolStr")
        }
        val typeStr = parser.getAttributeValue("", Connect.TYPE_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'type' attribute")
        val type = try {
            Connect.Types.valueOf(typeStr.uppercase())
        } catch (e: Exception) {
            throw SmackParsingException("Invalid 'type': $typeStr")
        }

        return Connect(url = uri, protocol = protocol, type = type, audio = audio, video = video)
    }
}
