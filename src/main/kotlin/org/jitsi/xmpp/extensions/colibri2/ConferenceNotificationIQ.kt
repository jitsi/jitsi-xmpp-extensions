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

import org.jitsi.xmpp.extensions.SafeParseIqProvider
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.IqBuilder
import org.jivesoftware.smack.packet.IqData
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import java.io.IOException

class ConferenceNotificationIQ private constructor(b: Builder) : IQ(b, ELEMENT, NAMESPACE) {
    val meetingId: String = b.meetingId ?: throw IllegalArgumentException("meeting-id must be set")
    val notifications: List<Notification>
        get() = extensions.filterIsInstance<Notification>()

    init {
        notifications.forEach { addExtension(it) }
    }

    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder): IQChildElementXmlStringBuilder {
        xml.attribute(MEETING_ID_ATTR_NAME, meetingId)

        /* All our elements are extensions, so we just need to return empty here. */
        xml.setEmptyElement()

        return xml
    }

    companion object {
        const val ELEMENT = "conference-notification"
        const val NAMESPACE = AbstractConferenceModificationIQ.NAMESPACE
        const val MEETING_ID_ATTR_NAME: String = "meeting-id"
    }

    class Builder : IqBuilder<Builder, ConferenceNotificationIQ> {
        constructor(xmppConnection: XMPPConnection) : super(xmppConnection)
        constructor(iqCommon: IqData) : super(iqCommon)
        constructor(stanzaId: String) : super(stanzaId)

        var meetingId: String? = null

        fun addNotification(notification: Notification): Builder = this.also {
            addExtension(notification)
        }

        override fun build(): ConferenceNotificationIQ = ConferenceNotificationIQ(this)

        override fun getThis(): Builder = this
    }

    class Provider : SafeParseIqProvider<ConferenceNotificationIQ>() {
        @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
        override fun doParse(
            parser: XmlPullParser,
            initialDepth: Int,
            iqData: IqData,
            xmlEnvironment: XmlEnvironment
        ): ConferenceNotificationIQ = Builder(iqData).apply {
            meetingId = parser.getAttributeValue("", MEETING_ID_ATTR_NAME)
        }.build().apply {
            IqProviderUtils.parseExtensions(parser, initialDepth, this)
        }
    }
}
