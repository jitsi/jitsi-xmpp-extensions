/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2024-Present 8x8, Inc.
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
package org.jitsi.xmpp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jitsi.xmpp.stringprep.IDNWithUnderscoreProfile
import org.jitsi.xmpp.stringprep.JitsiXmppStringprep
import org.jxmpp.JxmppContext
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException

/**
 * Test JID parsing. The lists below are based on the jxmpp corpora here, plus a couple additional ones:
 * https://github.com/igniterealtime/jxmpp/tree/master/jxmpp-strings-testframework/src/main/resources/xmpp-strings/jids/valid/main
 * https://github.com/igniterealtime/jxmpp/blob/master/jxmpp-strings-testframework/src/main/resources/xmpp-strings/jids/invalid/main
 */
class JidTest : ShouldSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance
    override suspend fun beforeAny(testCase: TestCase) {
        super.beforeAny(testCase)
        Smack.initialize()
    }

    init {
        context("Parsing valid JIDs") {
            JxmppContext.getDefaultContext().xmppStringprep.shouldBeInstanceOf<JitsiXmppStringprep>()
            validJids.forEach {
                withClue(it) {
                    JidCreate.from(it) shouldNotBe null
                }
            }
        }
        context("Parsing invalid JIDs") {
            JxmppContext.getDefaultContext().xmppStringprep.shouldBeInstanceOf<JitsiXmppStringprep>()
            invalidJids.forEach {
                withClue(it) {
                    shouldThrow<XmppStringprepException> {
                        JidCreate.from((it))
                    }
                }
            }
        }
        context("Parsing internationalized domains") {
            val idnWithUnderscoreProfile = IDNWithUnderscoreProfile()

            idns.forEach { idnGroup ->
                idnGroup.forEach { idn ->
                    withClue(idn) {
                        // prepare() doesn't always normalize, but it shouldn't throw an exception.
                        idnWithUnderscoreProfile.prepare(idn)

                        idnWithUnderscoreProfile.enforce(idn) shouldBe idnGroup[0]
                        JidCreate.from(idn).toString() shouldBe idnGroup[0]
                    }
                }
            }
        }
        context("Parsing invalid domains") {
            val idnWithUnderscoreProfile = IDNWithUnderscoreProfile()

            invalidIdns.forEach { idn ->
                withClue(idn) {
                    shouldThrow<IllegalArgumentException> {
                        idnWithUnderscoreProfile.prepare(idn)
                    }
                    shouldThrow<IllegalArgumentException> {
                        idnWithUnderscoreProfile.enforce(idn)
                    }
                }
            }
        }
        context("JIDs with IDNs") {
            listOf("user", "юзер", "π", "测试").forEach { username ->
                listOf("resource", "ресурс", "🍺").forEach { resource ->
                    idns.forEach { idnGroup ->
                        idnGroup.forEach { idn ->
                            val str = "$username@$idn/$resource"
                            withClue(str) {
                                val jid = JidCreate.from(str)
                                jid.resourceOrNull.toString() shouldBe resource
                                jid.localpartOrNull.toString() shouldBe username
                                jid.domain.toString() shouldBe idnGroup[0]
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Valid internationalized domain names. The first entry in each group is the normalized form that we expect, the other
 * entries are other encodings of the same domain.
 *
 * https://www.iana.org/domains/reserved
 */
val idns = listOf(
    listOf("إختبار", "XN--KGBECHTV", "xn--KGBEchtv"),
    listOf("آزمایشی", "XN--HGBK6AJ7F53BBA", "xn--hgbK6AJ7F53BBA"),
    listOf("测试", "XN--0ZWM56D", "Xn--0Zwm56D"),
    listOf("測試", "XN--G6W251D", "Xn--G6W251d"),
    listOf("испытание", "XN--80AKHBYKNJ4F", "XN--80AKHBYKNJ4f", "испытаНИЕ"),
    listOf("испыта-ние", "испыта-НИЕ", "xn----7sbqjc3alpk3g"),
    listOf("abc-испытание-def", "ABc-испытаНИЕ-DeF", "xn--abc--def-46g4c5ab8d0a3ar6m"),
    listOf("испытание.com", "XN--80AKHBYKNJ4F\u3002com", "XN--80AKHBYKNJ4f\uFF0Ecom", "испытаНИЕ\uFF61com"),
    listOf("испыта-ние.com", "испыта-НИЕ\u3002com", "xn----7sbqjc3alpk3g\uFF0Ecom", "XN----7SBQJC3ALPK3G\uFF61com"),
    listOf(
        "abc-испытание-def.com",
        "ABc-испытаНИЕ-DeF\u3002com",
        "xn--abc--def-46g4c5ab8d0a3ar6m\uFF0Ecom",
        "ABc-испытаНИЕ-DeF\uFF61com"
    ),
    listOf("abc.испытание-def.com", "ABc.испытаНИЕ-DeF\u3002com", "ABc.испытаНИЕ-DeF\uFF61com"),
    listOf("परीक्षा", "XN--11B5BS3A9AJ6G", "xn--11B5bs3A9AJ6G"),
    listOf("δοκιμή", "XN--JXALPDLP"),
    listOf("테스트", "XN--9T4B11YI5A"),
    listOf("טעסט", "XN--DEBA0AD"),
    listOf("テスト", "XN--ZCKZAH"),
    listOf("பரிட்சை", "XN--HLCJ6AYA9ESC7A"),
    listOf("bücher.de", "xn--bcher-kva.de"),
    listOf("büchxr.de", "xn--bchxr-kva.de"),
    listOf("büch_r.de", "xn--bch_r-kva.de"),
    listOf("buch_ü"),
    listOf("__б__"),
    listOf("büch%r.de", "xn--bch%r-kva.de"),
    listOf("buch%ü"),
    listOf("_%б%_"),
    // IDNA2003 converts this to "fussball", while IDNA2008 leaves the "ß" as is. OpenJDK 11, 17, 21 implement the older
    // standard. This is here to document the behavior and alert if it changes.
    listOf("fussball", "fußball")
)

val invalidIdns = listOf(
    // Invalid ascii characters
    "buch?r",
    "büch?r",
    "büch[r",
    // Leading hyphens
    "-bücher",
    "-bücher.com",
    "sub.-bücher.com",
    "sub\u3002-bücher\uFF61com",
    "sub\uFF0E-bücher.com",
    "sub\uFF61-bücher\uFF61com",
    // Trailing hyphens
    "bücher-",
    "bücher-.com",
    "sub-.bücher.com",
    "bücher-\u3002com",
    "bücher-\uFF0Ecom",
    "bücher-\uFF61com",
    // Empty labels
    "example..com",
    "example\uFF0E\u3002com",
    "example.com..",
    "example.com...",
    "example\uFF61com\uFF0E\u3002",
    "\u3002\uFF61example.com",
    ".example.com",
)

val validJids = listOf(
    "juliet@example.com",
    "juliet@example.com/foo",
    "juliet@example.com/foo bar",
    "juliet@example.com/foo@bar",
    "foo\\20bar@example.com",
    "foo%bar@example.com/f%b",
    "fussball@example.com",
    "fußball@example.com",
    "π@example.com",
    "Σ@example.com",
    "ς@example.com",
    "king@example.com/♚",
    "example.com",
    "example.com/foobar",
    "a.example.com/b@example.net",
    "server/resource@foo",
    "server/resource@foo/bar",
    "user@CaSe-InSeNsItIvE",
    "user@192.168.1.1",
    "long-conference-name-1245c711a15e466687b6333577d83e0b@" +
        "conference.vpaas-magic-cookie-a32a0c3311ee432eab711fa1fdf34793.8x8.vc",
    "user@example.org/🍺",
    // These are not valid according to the XMPP spec, but we accept them intentionally.
    "do_main.com",
    "u_s_e_r@_do_main_.com",
    "user@do_ma-in.com",
    "do%main.com",
    "u%s%e%r@%do%main%.com",
    "user@do%ma-in.com"
)

val invalidJids = listOf(
    "jul\u0001iet@example.c",
    "\"juliet\"@example.com",
    "foo bar@example.com",
    // This fails due to a corner case in JidCreate when "example.com" is already cached as a DomainpartJid
    // "@example.com/",
    "henryⅣ@example.com",
    "♚@example.com",
    "juliet@",
    "/foobar",
    "node@/server",
    "@server",
    "@server/resource",
    "@/resource",
    "@/",
    "/",
    "@",
    "user@",
    "user@@",
    "user@@host",
    "user@@host/resource",
    "user@@host/",
    "xsf@muc.xmpp.org/؜x",
    "username@example.org@example.org",
    "foo\u0000bar@example.org",
    "foobar@ex\u0000ample.org",
    // Leading - in domain part.
    "user@-do-main.com",
    // Trailing - in domain part.
    "user@do-main-.com",
    "user@conference..example.org",
    // These are VALID according to the XMPP spec (see the valid corpus), but we currently do not accept them.
    // [ is an ASSCI symbol that's not allowed in domain names.
    "user@[2001:638:a000:4134::ffff:40]",
    "user@[2001:638:a000:4134::ffff:40%eno1]",
    // A single label in the domain part is limited to 63
    "user@averylongdomainpartisstillvalideventhoughitexceedsthesixtyfourbytelimitofdnslabels",
)
