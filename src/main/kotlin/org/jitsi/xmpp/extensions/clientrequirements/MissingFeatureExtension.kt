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

import org.jitsi.xmpp.extensions.AbstractPacketExtension
import org.jitsi.xmpp.extensions.StringValueEnum
import org.jitsi.xmpp.extensions.parseStringValue
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.provider.ExtensionElementProvider
import org.jivesoftware.smack.xml.XmlPullParser
import org.jivesoftware.smack.xml.XmlPullParserException
import java.io.IOException

/**
 * The severity of a missing feature.
 */
enum class RequirementLevel(override val value: String) : StringValueEnum {
    /** The endpoint is not invited to the conference. */
    HARD("hard"),

    /** The endpoint is invited, but the feature is expected to be present. */
    SOFT("soft");

    companion object {
        fun parseString(s: String): RequirementLevel? = parseStringValue<RequirementLevel>(s)
    }
}

/**
 * Describes one feature that a client is required to advertise, but does not.
 */
class MissingFeatureExtension(
    /** The feature (XEP-0030 "var"), e.g. "http://jitsi.org/ssrc-rewriting-1". */
    val feature: String,
    /** A stable symbolic name for the feature, e.g. "SSRC_REWRITING_V1". Clients may use it as a translation key. */
    val name: String?,
    val level: RequirementLevel,
    /** Human readable text (in English) which describes how to add support for the feature. */
    val details: String?,
    /** A URL with more information. */
    val url: String?
) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    init {
        setAttribute(FEATURE_ATTR_NAME, feature)
        name?.let { setAttribute(NAME_ATTR_NAME, it) }
        setAttribute(LEVEL_ATTR_NAME, level.value)
        details?.let { setAttribute(DETAILS_ATTR_NAME, it) }
        url?.let { setAttribute(URL_ATTR_NAME, it) }
    }

    companion object {
        const val ELEMENT = "missing-feature"
        const val NAMESPACE = ClientRequirementsIq.NAMESPACE
        const val FEATURE_ATTR_NAME = "var"
        const val NAME_ATTR_NAME = "name"
        const val LEVEL_ATTR_NAME = "level"
        const val DETAILS_ATTR_NAME = "details"
        const val URL_ATTR_NAME = "url"
    }
}

class MissingFeatureExtensionProvider : ExtensionElementProvider<MissingFeatureExtension>() {
    @Throws(XmlPullParserException::class, IOException::class, SmackParsingException::class)
    override fun parse(parser: XmlPullParser, depth: Int, xml: XmlEnvironment?): MissingFeatureExtension {
        // Note that getAttributeValue returns an empty string when the attribute is present but empty.
        val feature = parser.getAttributeValue("", MissingFeatureExtension.FEATURE_ATTR_NAME)
            ?.takeIf { it.isNotBlank() }
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'var' attribute")
        val levelString = parser.getAttributeValue("", MissingFeatureExtension.LEVEL_ATTR_NAME)
            ?: throw SmackParsingException.RequiredAttributeMissingException("Missing 'level' attribute")
        val level = RequirementLevel.parseString(levelString)
            ?: throw SmackParsingException("Invalid 'level' attribute: $levelString")

        return MissingFeatureExtension(
            feature = feature,
            name = parser.getAttributeValue("", MissingFeatureExtension.NAME_ATTR_NAME),
            level = level,
            details = parser.getAttributeValue("", MissingFeatureExtension.DETAILS_ATTR_NAME),
            url = parser.getAttributeValue("", MissingFeatureExtension.URL_ATTR_NAME)
        )
    }
}
