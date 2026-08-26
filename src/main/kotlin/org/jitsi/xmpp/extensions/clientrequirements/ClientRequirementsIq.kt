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
package org.jitsi.xmpp.extensions.clientrequirements

import org.jitsi.xmpp.extensions.SafeParseIqProvider
import org.jitsi.xmpp.extensions.StringValueEnum
import org.jitsi.xmpp.extensions.colibri2.IqProviderUtils
import org.jitsi.xmpp.extensions.parseStringValue
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.IqBuilder
import org.jivesoftware.smack.packet.IqData
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.xml.XmlPullParser

/**
 * What the sender of a [ClientRequirementsIq] does about the missing features.
 */
enum class RequirementsAction(override val value: String) : StringValueEnum {
    /** At least one feature with a "hard" level is missing, and the endpoint will not be invited. */
    REJECT("reject"),

    /** Only features with a "soft" level are missing. The endpoint is invited as usual. */
    WARN("warn");

    companion object {
        fun parseString(s: String): RequirementsAction? = parseStringValue<RequirementsAction>(s)
    }
}

/**
 * Sent by jicofo to a client which does not advertise all the features that the deployment requires. Sent as an IQ of
 * type "set" from the focus' occupant JID to the client's occupant JID. Clients which support it advertise the
 * "http://jitsi.org/client-requirements-1" feature.
 */
class ClientRequirementsIq private constructor(b: Builder) : IQ(b, ELEMENT, NAMESPACE) {
    val action: RequirementsAction = b.action ?: throw IllegalArgumentException("The 'action' attribute must be set")

    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder) = xml.apply {
        attribute(ACTION_ATTR_NAME, action.value)
        rightAngleBracket()
    }

    val missingFeatures: List<MissingFeatureExtension>
        get() = getExtensions(MissingFeatureExtension::class.java)

    companion object {
        const val NAMESPACE = "jitsi:client-requirements"
        const val ELEMENT = "client-requirements"
        const val ACTION_ATTR_NAME = "action"

        /** The feature advertised by clients which understand this IQ. */
        const val FEATURE = "http://jitsi.org/client-requirements-1"

        fun registerProviders() {
            ProviderManager.addIQProvider(ELEMENT, NAMESPACE, ClientRequirementsIqProvider())
            ProviderManager.addExtensionProvider(
                MissingFeatureExtension.ELEMENT,
                NAMESPACE,
                MissingFeatureExtensionProvider()
            )
        }
    }

    class Builder : IqBuilder<Builder, ClientRequirementsIq> {
        constructor(id: String) : super(id)
        constructor(connection: XMPPConnection) : super(connection)
        constructor(iqCommon: IqData) : super(iqCommon)

        var action: RequirementsAction? = null

        override fun build(): ClientRequirementsIq = ClientRequirementsIq(this)

        override fun getThis() = this
    }
}

class ClientRequirementsIqProvider : SafeParseIqProvider<ClientRequirementsIq>() {
    @Throws(Exception::class)
    override fun doParse(
        parser: XmlPullParser,
        initialDepth: Int,
        data: IqData,
        xmlEnvironment: XmlEnvironment
    ): ClientRequirementsIq? {
        if (parser.namespace != ClientRequirementsIq.NAMESPACE || parser.name != ClientRequirementsIq.ELEMENT) {
            return null
        }

        val actionString = parser.getAttributeValue("", ClientRequirementsIq.ACTION_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'action' attribute")

        return ClientRequirementsIq.Builder(data).apply {
            action = RequirementsAction.parseString(actionString)
                ?: throw SmackParsingException("Invalid 'action' attribute: $actionString")
            addExtensions(IqProviderUtils.parseExtensions(parser, initialDepth))
        }.build()
    }
}
