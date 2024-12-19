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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.util.PacketParserUtils
import java.net.URI

class ConnectTest : ShouldSpec() {
    init {
        IqProviderUtils.registerProviders()
        val provider = ConnectProvider()
        val url = "ws://example.com"

        context("Parsing a valid extension") {
            context("Without audio/video") {
                val connect = provider.parse(
                    PacketParserUtils.getParserFor("<connect url='$url' protocol='mediajson' type='recorder'/>")
                )
                connect.url shouldBe URI(url)
                connect.protocol shouldBe Connect.Protocols.MEDIAJSON
                connect.type shouldBe Connect.Types.RECORDER
                connect.audio shouldBe false
                connect.video shouldBe false
            }
            context("With audio") {
                val connect = provider.parse(
                    PacketParserUtils.getParserFor(
                        "<connect url='$url' protocol='mediajson' type='recorder' audio='true'/>"
                    )
                )
                connect.url shouldBe URI(url)
                connect.protocol shouldBe Connect.Protocols.MEDIAJSON
                connect.type shouldBe Connect.Types.RECORDER
                connect.audio shouldBe true
                connect.video shouldBe false
            }
            context("With video") {
                val connect = provider.parse(
                    PacketParserUtils.getParserFor(
                        "<connect url='$url' protocol='mediajson' type='transcriber' audio='false' video='true'/>"
                    )
                )
                connect.url shouldBe URI(url)
                connect.protocol shouldBe Connect.Protocols.MEDIAJSON
                connect.type shouldBe Connect.Types.TRANSCRIBER
                connect.audio shouldBe false
                connect.video shouldBe true
            }
        }
        context("Parsing with missing url") {
            shouldThrow<SmackParsingException> {
                provider.parse(
                    PacketParserUtils.getParserFor("<connect protocol='mediajson' type='recorder '></connect>")
                )
            }
        }
        context("Parsing with invalid url") {
            shouldThrow<SmackParsingException> {
                provider.parse(
                    PacketParserUtils.getParserFor("<connect url='in val id' protocol='mediajson' type='recorder'/>")
                )
            }
        }
        context("Parsing with missing protocol") {
            shouldThrow<SmackParsingException> {
                provider.parse(PacketParserUtils.getParserFor("<connect url='$url' type='recorder'/>"))
            }
        }
        context("Parsing with invalid protocol") {
            shouldThrow<SmackParsingException> {
                provider.parse(PacketParserUtils.getParserFor("<connect url='$url' protocol='abc' type='recorder'/>"))
            }
        }
        context("Parsing with missing type") {
            shouldThrow<SmackParsingException> {
                provider.parse(PacketParserUtils.getParserFor("<connect url='$url' protocol='mediajson'/>"))
            }
        }
        context("Parsing with invalid type") {
            shouldThrow<SmackParsingException> {
                provider.parse(PacketParserUtils.getParserFor("<connect url='$url' protocol='mediajson' type='inv'/>"))
            }
        }
    }
}
