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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.util.PacketParserUtils

class NotificationTest : ShouldSpec() {
    init {
        IqProviderUtils.registerProviders()
        val provider = Notification.Provider()
        val testId = "test-123"

        context("Parsing a valid extension") {
            context("With required attributes only") {
                val notification = provider.parse(
                    PacketParserUtils.getParserFor("<notification id='$testId' type='connect-failed'/>")
                )
                notification.id shouldBe testId
                notification.type shouldBe Notification.Types.CONNECT_FAILED
                notification.description shouldBe null
            }

            context("With description") {
                val description = "Connection timeout occurred"
                val notification = provider.parse(
                    PacketParserUtils.getParserFor(
                        "<notification id='$testId' type='ice-failed' description='$description'/>"
                    )
                )
                notification.id shouldBe testId
                notification.type shouldBe Notification.Types.ICE_FAILED
                notification.description shouldBe description
            }
        }

        context("Parsing with missing id") {
            shouldThrow<SmackParsingException> {
                provider.parse(
                    PacketParserUtils.getParserFor("<notification type='connect-failed'/>")
                )
            }
        }

        context("Parsing with missing type") {
            shouldThrow<SmackParsingException> {
                provider.parse(
                    PacketParserUtils.getParserFor("<notification id='$testId'/>")
                )
            }
        }

        context("Parsing with invalid type") {
            shouldThrow<SmackParsingException> {
                provider.parse(
                    PacketParserUtils.getParserFor("<notification id='$testId' type='invalid-type'/>")
                )
            }
        }
    }
}
