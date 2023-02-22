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

import org.jitsi.xmpp.extensions.colibri2.IqProviderUtils
import org.jitsi.xmpp.extensions.jitsimeet.ConferenceIqProvider
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.IqBuilder
import org.jivesoftware.smack.packet.IqData
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.provider.IqProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import org.jxmpp.jid.EntityBareJid
import java.io.IOException
import java.lang.IllegalArgumentException

class VisitorsIq private constructor(b: Builder) : IQ(b, ELEMENT, NAMESPACE) {
    val room: EntityBareJid = b.room ?: throw IllegalArgumentException("The 'room' attribute must be set")

    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder) = xml.apply {
        optAttribute(ROOM_ATTR_NAME, room)
        rightAngleBracket()
    }

    fun getConnectVnodeExtensions(): List<ConnectVnodePacketExtension> = getExtensions(
        ConnectVnodePacketExtension::class.java
    )
    fun getDisconnectVnodeExtensions(): List<DisconnectVnodePacketExtension> = getExtensions(
        DisconnectVnodePacketExtension::class.java
    )

    companion object {
        const val NAMESPACE = "jitsi:visitors"
        const val ELEMENT = "visitors"
        const val ROOM_ATTR_NAME = "room"

        fun registerProviders() {
            ProviderManager.addIQProvider(ELEMENT, NAMESPACE, VisitorsIqProvider())
            ProviderManager.addExtensionProvider(
                ConnectVnodePacketExtension.ELEMENT,
                NAMESPACE,
                ConnectVnodePacketExtensionProvider()
            )
            ProviderManager.addExtensionProvider(
                DisconnectVnodePacketExtension.ELEMENT,
                NAMESPACE,
                DisconnectVnodePacketExtensionProvider()
            )
        }
    }

    class Builder : IqBuilder<Builder, VisitorsIq> {
        constructor(id: String) : super(id)
        constructor(connection: XMPPConnection) : super(connection)
        constructor(iqCommon: IqData) : super(iqCommon)

        var room: EntityBareJid? = null

        override fun build(): VisitorsIq {
            return VisitorsIq(this)
        }

        override fun getThis() = this
    }
}

class VisitorsIqProvider : IqProvider<VisitorsIq>() {
    @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
    override fun parse(
        parser: XmlPullParser,
        initialDepth: Int,
        data: IqData,
        xmlEnvironment: XmlEnvironment
    ): VisitorsIq? {
        if (parser.namespace != VisitorsIq.NAMESPACE || parser.name != VisitorsIq.ELEMENT) return null

        return VisitorsIq.Builder(data).apply {
            room = ConferenceIqProvider.getRoomJid(parser.getAttributeValue("", VisitorsIq.ROOM_ATTR_NAME))
            addExtensions(IqProviderUtils.parseExtensions(parser, initialDepth))
        }.build()
    }
}
