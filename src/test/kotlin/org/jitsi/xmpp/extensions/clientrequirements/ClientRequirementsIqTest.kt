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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jitsi.xmpp.extensions.IQUtils
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.provider.ProviderManager
import org.jxmpp.jid.impl.JidCreate
import org.xmlunit.builder.DiffBuilder

class ClientRequirementsIqTest : ShouldSpec() {
    init {
        ClientRequirementsIq.registerProviders()
        val provider = ProviderManager.getIQProvider(ClientRequirementsIq.ELEMENT, ClientRequirementsIq.NAMESPACE)

        context("Parsing a valid IQ") {
            IQUtils.parse(validXml, provider).let { iq ->
                iq.shouldBeInstanceOf<ClientRequirementsIq>()
                iq.action shouldBe RequirementsAction.REJECT
                iq.missingFeatures.size shouldBe 2
                iq.missingFeatures[0].let {
                    it.feature shouldBe "http://jitsi.org/ssrc-rewriting-1"
                    it.name shouldBe "SSRC_REWRITING_V1"
                    it.level shouldBe RequirementLevel.HARD
                    it.details shouldBe "Update to version 10.2 or later."
                    it.url shouldBe "https://example.com/docs"
                }
                iq.missingFeatures[1].let {
                    it.feature shouldBe "http://jitsi.org/start-muted-room-metadata"
                    it.name shouldBe "START_MUTED_RMD"
                    it.level shouldBe RequirementLevel.SOFT
                    it.details shouldBe null
                    it.url shouldBe null
                }
            }
        }

        context("Parsing an IQ with no missing features") {
            IQUtils.parse(
                """
<iq to='t' from='f' type='set'>
    <client-requirements xmlns='jitsi:client-requirements' action='warn'/>
</iq>
                """.trimIndent(),
                provider
            ).let { iq ->
                iq.shouldBeInstanceOf<ClientRequirementsIq>()
                iq.action shouldBe RequirementsAction.WARN
                iq.missingFeatures.size shouldBe 0
            }
        }

        context("Parsing invalid IQs") {
            should("Fail with no 'action'") {
                shouldThrow<Exception> {
                    IQUtils.parse(
                        """
<iq to='t' from='f' type='set'>
    <client-requirements xmlns='jitsi:client-requirements'/>
</iq>
                        """.trimIndent(),
                        provider
                    )
                }
            }
            should("Fail with an invalid 'action'") {
                shouldThrow<Exception> {
                    IQUtils.parse(
                        """
<iq to='t' from='f' type='set'>
    <client-requirements xmlns='jitsi:client-requirements' action='invalid'/>
</iq>
                        """.trimIndent(),
                        provider
                    )
                }
            }
            should("Fail with a missing-feature with no 'var'") {
                shouldThrow<Exception> {
                    IQUtils.parse(
                        """
<iq to='t' from='f' type='set'>
    <client-requirements xmlns='jitsi:client-requirements' action='reject'>
        <missing-feature xmlns='jitsi:client-requirements' level='hard'/>
    </client-requirements>
</iq>
                        """.trimIndent(),
                        provider
                    )
                }
            }
            should("Fail with a missing-feature with an invalid 'level'") {
                shouldThrow<Exception> {
                    IQUtils.parse(
                        """
<iq to='t' from='f' type='set'>
    <client-requirements xmlns='jitsi:client-requirements' action='reject'>
        <missing-feature xmlns='jitsi:client-requirements' var='f' level='invalid'/>
    </client-requirements>
</iq>
                        """.trimIndent(),
                        provider
                    )
                }
            }
        }

        context("Serializing") {
            val iq = ClientRequirementsIq.Builder("id").apply {
                action = RequirementsAction.REJECT
                addExtension(
                    MissingFeatureExtension(
                        feature = "http://jitsi.org/ssrc-rewriting-1",
                        name = "SSRC_REWRITING_V1",
                        level = RequirementLevel.HARD,
                        details = "Update to version 10.2 or later.",
                        url = "https://example.com/docs"
                    )
                )
                addExtension(
                    MissingFeatureExtension(
                        feature = "http://jitsi.org/start-muted-room-metadata",
                        name = "START_MUTED_RMD",
                        level = RequirementLevel.SOFT,
                        details = null,
                        url = null
                    )
                )
                to(JidCreate.from("t"))
                from(JidCreate.from("f"))
                ofType(IQ.Type.set)
            }.build()

            val diff = DiffBuilder.compare(validXml).withTest(iq.toXML().toString()).checkForIdentical().build()
            diff.hasDifferences() shouldBe false
        }
    }
}

// Whitespace matters.
private val validXml = "<iq xmlns='jabber:client' to='t' from='f' id='id' type='set'>" +
    "<client-requirements xmlns='jitsi:client-requirements' action='reject'>" +
    "<missing-feature xmlns='jitsi:client-requirements' var='http://jitsi.org/ssrc-rewriting-1' " +
    "name='SSRC_REWRITING_V1' level='hard' details='Update to version 10.2 or later.' " +
    "url='https://example.com/docs'/>" +
    "<missing-feature xmlns='jitsi:client-requirements' var='http://jitsi.org/start-muted-room-metadata' " +
    "name='START_MUTED_RMD' level='soft'/>" +
    "</client-requirements>" +
    "</iq>"
