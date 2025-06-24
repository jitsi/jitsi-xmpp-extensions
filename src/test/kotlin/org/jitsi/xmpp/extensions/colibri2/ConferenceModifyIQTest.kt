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
import org.jitsi.xmpp.extensions.IQUtils
import org.jivesoftware.smack.parsing.SmackParsingException

class ConferenceModifyIQTest : ShouldSpec() {
    val provider = ConferenceModifyIQProvider()
    init {
        context("Parse a simple IQ correctly") {
            val conferenceModifyIQ = IQUtils.parse(
                """
                    <iq type='get' from='example.com' to='example.com'>
                        <conference-modify xmlns='http://jitsi.org/protocol/colibri2' meeting-id='abc'/>
                    </iq>
                """.trimIndent(),
                provider
            )
            conferenceModifyIQ.meetingId shouldBe "abc"
        }
        context("Parsing should fail when meetingId is not set") {
            shouldThrow<SmackParsingException> {
                IQUtils.parse(
                    """
                        <iq type='get' from='example.com' to='example.com'>
                            <conference-modify xmlns='http://jitsi.org/protocol/colibri2'/>
                        </iq>
                    """.trimIndent(),
                    provider
                )
            }
        }
        context("Parsing should fail when meetingId is blank (empty string or only whitespace)") {
            shouldThrow<SmackParsingException> {
                IQUtils.parse(
                    """
                        <iq type='get' from='example.com' to='example.com'>
                            <conference-modify xmlns='http://jitsi.org/protocol/colibri2' meetingId='  '/>
                        </iq>
                    """.trimIndent(),
                    provider
                )
            }
        }
    }
}
