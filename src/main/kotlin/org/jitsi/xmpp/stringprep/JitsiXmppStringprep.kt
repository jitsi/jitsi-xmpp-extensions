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
package org.jitsi.xmpp.stringprep

import org.jxmpp.stringprep.XmppStringprep
import org.jxmpp.stringprep.XmppStringprepException
import org.jxmpp.stringprep.rocksxmppprecis.RocksXmppPrecisStringprep
import rocks.xmpp.precis.PrecisProfile
import java.net.IDN
import java.text.Normalizer
import java.util.regex.Pattern

/**
 * Extends [RocksXmppPrecisStringprep] to allow underscores (_) in the domain part.
 *
 * This is needed because jitsi-meet URLs of the form https://domain/tenant/room get translated into a JID of the
 * form room@tenant.conference.domain, and the tenant field has been allowed to use underscores for a long time (in
 * fact '.' in the tenant is translated into '_').
 */
class JitsiXmppStringprep : XmppStringprep by RocksXmppPrecisStringprep.INSTANCE {
    override fun domainprep(string: String?): String {
        try {
            return idnWithUnderscoreProfile.enforce(string)
        } catch (e: IllegalArgumentException) {
            throw XmppStringprepException(string, e)
        }
    }

    companion object {
        val INSTANCE = JitsiXmppStringprep()
        private val idnWithUnderscoreProfile = IDNWithUnderscoreProfile()
    }
}

/**
 * Based on [PrecisProfiles.IDN], but allows underscores.
 */
class IDNWithUnderscoreProfile : PrecisProfile(false) {
    override fun prepare(input: CharSequence): String {
        val str = input.toString()

        // Throws if it contains invalid characters
        IDN.toASCII(str.replace("_", ""), IDN.USE_STD3_ASCII_RULES)

        return IDN.toUnicode(IDN.toASCII(str), IDN.USE_STD3_ASCII_RULES)
    }

    override fun applyWidthMappingRule(charSequence: CharSequence) = widthMap(charSequence)
    override fun applyAdditionalMappingRule(charSequence: CharSequence) =
        LABEL_SEPARATOR.matcher(charSequence).replaceAll(".")
    override fun applyCaseMappingRule(charSequence: CharSequence) = charSequence.toString().lowercase()

    override fun applyNormalizationRule(charSequence: CharSequence) =
        Normalizer.normalize(charSequence, Normalizer.Form.NFC)

    override fun applyDirectionalityRule(charSequence: CharSequence) = charSequence

    companion object {
        private const val DOTS: String = "[.\u3002\uFF0E\uFF61]"
        private val LABEL_SEPARATOR: Pattern = Pattern.compile(DOTS)
    }
}
