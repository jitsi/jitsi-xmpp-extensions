/*
 * Copyright @ 2015 - Present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.jitsimeet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.jitsi.xmpp.extensions.IQUtils
import org.jxmpp.jid.impl.JidCreate

/**
 * Tests for [MuteIq] parsing.
 */
class MuteIqTest : ShouldSpec() {
    init {
        fun getTestXml(namespace: String) = """
            <iq to='t' from='f' type='set'>
                <mute xmlns='$namespace' jid='somejid'>true</mute>
            </iq>
        """.trimIndent()

        should("parse MuteIq from XML") {
            val iq = IQUtils.parse(getTestXml(MuteIq.NAMESPACE), MuteIqProvider())

            iq.from shouldBe "f"
            iq.to shouldBe "t"
            iq.jid shouldBe "somejid"
            iq.mute shouldBe true
        }
        should("parse MuteVideoIq from XML") {
            val iq = IQUtils.parse(getTestXml(MuteVideoIq.NAMESPACE), MuteVideoIqProvider())

            iq.from shouldBe "f"
            iq.to shouldBe "t"
            iq.jid shouldBe "somejid"
            iq.mute shouldBe true
        }
        should("parse MuteDesktopIq from XML") {
            val iq = IQUtils.parse(getTestXml(MuteDesktopIq.NAMESPACE), MuteDesktopIqProvider())

            iq.from shouldBe "f"
            iq.to shouldBe "t"
            iq.jid shouldBe "somejid"
            iq.mute shouldBe true
        }

        should("convert MuteIq to XML") {
            val muteIq = MuteIq().apply {
                stanzaId = "123xyz"
                to = JidCreate.from("toJid")
                from = JidCreate.from("fromJid")
                jid = JidCreate.from("mucjid1234")
                mute = true
            }
            val parsed = IQUtils.parse(muteIq.toXML().toString(), MuteIqProvider())

            parsed.from shouldBe muteIq.from
            parsed.to shouldBe muteIq.to
            parsed.mute shouldBe muteIq.mute
            parsed.jid shouldBe muteIq.jid
        }
    }
}
