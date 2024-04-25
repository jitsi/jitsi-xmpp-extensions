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
        // We're calling toASCII and toUnicode without the [IDN.USE_STD3_ASCII_RULES] flag, so we have to do the
        // (relaxed) verification.
        val ascii = verifyLDHU(IDN.toASCII(input.toString()))
        return verifyLDHU(IDN.toUnicode(ascii))
    }

    /**
     * Assert that, after splitting [s] into labels separated, each label:
     *  -- Is not empty.
     *  -- All ASCII characters are Letters/Digits/Hyphen/Underscore.
     *  -- Does not begin or end with a hyphen.
     *
     * Based on the implementation in java's IDN.
     *
     * @throws IllegalStateException if any of the assertions fail.
     */
    private fun verifyLDHU(s: String) = s.also {
        val dest = StringBuffer(s)
        require(dest.isNotEmpty()) { "Empty label is not a legal name" }

        for (i in s.indices) {
            require(!dest[i].code.isNonLDHUAsciiCodePoint()) { "Contains non-LDHU ASCII characters: ${dest[i]}" }
            if (dest[i].isLabelSeparator()) {
                require(i != 0) { "Empty label is not a legal name" }
                require(dest[i - 1] != '-') { "Label has trailing hyphen" }
                require(!dest[i - 1].isLabelSeparator()) { "Empty label is not a legal name" }
                require(i == dest.length - 1 || dest[i + 1] != '-') { "Label has leading hyphen" }
                require(i == dest.length - 1 || !dest[i + 1].isLabelSeparator()) { "Empty label" }
            }
        }
        require(dest[0] != '-' && dest[dest.length - 1] != '-') { "Has leading or trailing hyphen" }
    }

    override fun applyWidthMappingRule(charSequence: CharSequence) = widthMap(charSequence)
    override fun applyAdditionalMappingRule(charSequence: CharSequence) =
        LABEL_SEPARATOR.matcher(charSequence).replaceAll(".")
    override fun applyCaseMappingRule(charSequence: CharSequence) = charSequence.toString().lowercase()

    override fun applyNormalizationRule(charSequence: CharSequence) =
        Normalizer.normalize(charSequence, Normalizer.Form.NFC)

    override fun applyDirectionalityRule(charSequence: CharSequence) = charSequence

    companion object {
        private val dots = listOf('.', '\u3002', '\uFF0E', '\uFF61').toCharArray()
        private val LABEL_SEPARATOR = Pattern.compile("[${dots.joinToString(separator = "")}]")

        private fun Char.isLabelSeparator() = dots.contains(this)

        /** Return true if [this] is a code for an ASCII character that is not a Letter/Digit/Hyphen/Underscore. */
        private fun Int.isNonLDHUAsciiCodePoint(): Boolean {
            return (this in 0x0000..0x002C) ||
                (this == 0x002F) ||
                (this in 0x003A..0x0040) ||
                (this in 0x005B..0x005e) ||
                (this == 0x0060) ||
                (this in 0x007B..0x007F)
        }
    }
}
