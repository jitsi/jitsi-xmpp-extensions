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

    fun getHttpHeaders(): List<HttpHeader> = getChildExtensionsOfType(HttpHeader::class.java)
    fun addHttpHeader(name: String, value: String) = addChildExtension(HttpHeader(name, value))
    fun addHttpHeader(header: HttpHeader) = addChildExtension(header)
    fun removeHttpHeader(name: String) =
        getHttpHeaders().filter { it.name == name }.forEach { removeChildExtension(it) }

    fun getPing(): Ping? = getChildExtensionsOfType(Ping::class.java).firstOrNull()
    fun setPing(interval: Int, timeout: Int) {
        removePing()
        addChildExtension(Ping(interval, timeout))
    }
    fun setPing(ping: Ping) {
        removePing()
        addChildExtension(ping)
    }
    fun removePing() = getPing()?.let { removeChildExtension(it) }

    fun getExports(): List<String> = getChildExtension(Exports::class.java)?.getNames() ?: emptyList()
    fun addExport(name: String) {
        val exports = getChildExtension(Exports::class.java) ?: Exports().also { addChildExtension(it) }
        exports.addExport(name)
    }
    fun setExports(names: List<String>) {
        getChildExtension(Exports::class.java)?.let { removeChildExtension(it) }
        if (names.isNotEmpty()) {
            addChildExtension(Exports(names))
        }
    }

    fun getRequests(): List<String> = getChildExtension(Requests::class.java)?.getNames() ?: emptyList()
    fun addRequest(name: String) {
        val requests = getChildExtension(Requests::class.java) ?: Requests().also { addChildExtension(it) }
        requests.addRequest(name)
    }
    fun setRequests(names: List<String>) {
        getChildExtension(Requests::class.java)?.let { removeChildExtension(it) }
        if (names.isNotEmpty()) {
            addChildExtension(Requests(names))
        }
    }

    class HttpHeader(val name: String, val value: String) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        init {
            setAttribute(NAME_ATTR_NAME, name)
            setAttribute(VALUE_ATTR_NAME, value)
        }

        companion object {
            const val ELEMENT = "http-header"
            const val NAME_ATTR_NAME = "name"
            const val VALUE_ATTR_NAME = "value"
        }
    }

    class Ping(val interval: Int, val timeout: Int) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        init {
            setAttribute(INTERVAL_ATTR_NAME, interval)
            setAttribute(TIMEOUT_ATTR_NAME, timeout)
        }

        companion object {
            const val ELEMENT = "ping"
            const val INTERVAL_ATTR_NAME = "interval"
            const val TIMEOUT_ATTR_NAME = "timeout"
        }
    }

    /** A container for the source names this connection should export (send out). */
    class Exports() : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        constructor(names: List<String>) : this() {
            names.forEach { addChildExtension(Export(it)) }
        }

        fun getNames(): List<String> = getChildExtensionsOfType(Export::class.java).map { it.name }
        fun addExport(name: String) = addChildExtension(Export(name))

        companion object {
            const val ELEMENT = "exports"
        }
    }

    /** A single exported source, identified by its source name. */
    class Export(name: String) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        init {
            setAttribute(NAME_ATTR_NAME, name)
        }

        val name: String
            get() = getAttributeAsString(NAME_ATTR_NAME)

        companion object {
            const val ELEMENT = "export"
            const val NAME_ATTR_NAME = "name"
        }
    }

    /** A container for the source names this connection requests (wants to receive). */
    class Requests() : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        constructor(names: List<String>) : this() {
            names.forEach { addChildExtension(Request(it)) }
        }

        fun getNames(): List<String> = getChildExtensionsOfType(Request::class.java).map { it.name }
        fun addRequest(name: String) = addChildExtension(Request(name))

        companion object {
            const val ELEMENT = "requests"
        }
    }

    /** A single requested source, identified by its source name. */
    class Request(name: String) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
        init {
            setAttribute(NAME_ATTR_NAME, name)
        }

        val name: String
            get() = getAttributeAsString(NAME_ATTR_NAME)

        companion object {
            const val ELEMENT = "request"
            const val NAME_ATTR_NAME = "name"
        }
    }

    enum class Protocols(val value: String) {
        MEDIAJSON("mediajson")
    }

    enum class Types(val value: String) {
        RECORDER("recorder"),
        TRANSCRIBER("transcriber"),
        TRANSLATOR("translator")
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
        if (uri.scheme !in listOf("ws", "wss")) {
            throw SmackParsingException("Invalid 'url' scheme: ${uri.scheme}. Only 'ws' and 'wss' are allowed.")
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

        val connect = Connect(url = uri, protocol = protocol, type = type, audio = audio, video = video)

        // Parse child elements manually
        var done = false
        while (!done) {
            val eventType = parser.next()
            when (eventType) {
                XmlPullParser.Event.START_ELEMENT -> {
                    when (parser.name) {
                        Connect.HttpHeader.ELEMENT -> {
                            val headerName = parser.getAttributeValue("", Connect.HttpHeader.NAME_ATTR_NAME)
                                ?: throw SmackParsingException.RequiredAttributeMissingException(
                                    "Missing 'name' attribute in http-header element"
                                )
                            if (!headerName.matches(Regex("[A-Za-z0-9\\-]+"))) {
                                throw SmackParsingException("Invalid HTTP header name: $headerName")
                            }
                            val headerValue = parser.getAttributeValue("", Connect.HttpHeader.VALUE_ATTR_NAME)
                                ?: throw SmackParsingException.RequiredAttributeMissingException(
                                    "Missing 'value' attribute in http-header element"
                                )
                            val sanitizedValue = headerValue.replace("\r", "").replace("\n", "")
                            connect.addHttpHeader(headerName, sanitizedValue)
                        }
                        Connect.Ping.ELEMENT -> {
                            val intervalStr = parser.getAttributeValue("", Connect.Ping.INTERVAL_ATTR_NAME)
                                ?: throw SmackParsingException.RequiredAttributeMissingException(
                                    "Missing 'interval' attribute in ping element"
                                )
                            val timeoutStr = parser.getAttributeValue("", Connect.Ping.TIMEOUT_ATTR_NAME)
                                ?: throw SmackParsingException.RequiredAttributeMissingException(
                                    "Missing 'timeout' attribute in ping element"
                                )
                            val interval = try {
                                intervalStr.toInt()
                            } catch (e: NumberFormatException) {
                                throw SmackParsingException("Invalid 'interval' value: $intervalStr")
                            }
                            val timeout = try {
                                timeoutStr.toInt()
                            } catch (e: NumberFormatException) {
                                throw SmackParsingException("Invalid 'timeout' value: $timeoutStr")
                            }
                            connect.setPing(interval, timeout)
                        }
                        Connect.Exports.ELEMENT -> {
                            connect.setExports(
                                parseSourceNames(parser, Connect.Exports.ELEMENT, Connect.Export.ELEMENT)
                            )
                        }
                        Connect.Requests.ELEMENT -> {
                            connect.setRequests(
                                parseSourceNames(parser, Connect.Requests.ELEMENT, Connect.Request.ELEMENT)
                            )
                        }
                    }
                }
                XmlPullParser.Event.END_ELEMENT -> {
                    if (parser.name == Connect.ELEMENT && parser.depth <= depth) {
                        done = true
                    }
                }
                else -> { /* ignore */ }
            }
        }

        return connect
    }

    /**
     * Parses a container element (e.g. <exports>) holding a list of source-name items (e.g. <export name='...'/>),
     * returning the list of source names. The parser is left positioned on the container's end element.
     */
    @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
    private fun parseSourceNames(parser: XmlPullParser, containerElement: String, itemElement: String): List<String> {
        val names = mutableListOf<String>()
        var done = false
        while (!done) {
            when (parser.next()) {
                XmlPullParser.Event.START_ELEMENT -> {
                    if (parser.name == itemElement) {
                        val name = parser.getAttributeValue("", Connect.Export.NAME_ATTR_NAME)
                            ?: throw SmackParsingException.RequiredAttributeMissingException(
                                "Missing 'name' attribute in $itemElement element"
                            )
                        names.add(name)
                    }
                }
                XmlPullParser.Event.END_ELEMENT -> {
                    if (parser.name == containerElement) {
                        done = true
                    }
                }
                else -> { /* ignore */ }
            }
        }
        return names
    }
}
