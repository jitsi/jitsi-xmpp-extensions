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
package org.jitsi.xmpp.extensions.jitsimeet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jitsi.xmpp.extensions.jingle.JingleIQ
import org.jitsi.xmpp.extensions.jingle.JingleIQProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.util.PacketParserUtils

class ClientVersionPacketExtensionTest : ShouldSpec() {
    init {
        ClientVersionPacketExtension.registerProvider()
        ProviderManager.addIQProvider(JingleIQ.ELEMENT, JingleIQ.NAMESPACE, JingleIQProvider())

        context("Serializing") {
            val extension = ClientVersionPacketExtension("abc1234")

            extension.version shouldBe "abc1234"
            extension.toXML().toString() shouldBe
                "<client-version xmlns='http://jitsi.org/protocol/focus' version='abc1234'/>"
        }

        context("Parsing a session-accept which carries the extension") {
            // Whitespace matters, the jingle provider does not expect character data between the elements.
            val iq = PacketParserUtils.parseStanza<JingleIQ>(
                "<iq from='room@conference.example.com/abcdabcd' to='focus@auth.example.com' type='set' id='id'>" +
                    "<jingle xmlns='urn:xmpp:jingle:1' action='session-accept' sid='sid'>" +
                    "<client-version xmlns='http://jitsi.org/protocol/focus' version='abc1234'/>" +
                    "</jingle>" +
                    "</iq>"
            )

            iq.getExtension(ClientVersionPacketExtension::class.java).let {
                it.shouldNotBeNull()
                it.version shouldBe "abc1234"
            }
        }

        context("Parsing a session-accept with no extension") {
            val iq = PacketParserUtils.parseStanza<JingleIQ>(
                "<iq from='room@conference.example.com/abcdabcd' to='focus@auth.example.com' type='set' id='id'>" +
                    "<jingle xmlns='urn:xmpp:jingle:1' action='session-accept' sid='sid'/>" +
                    "</iq>"
            )

            iq.getExtension(ClientVersionPacketExtension::class.java) shouldBe null
        }
    }
}
