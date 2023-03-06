/*
 * Copyright @ 2023 - present 8x8, Inc.
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

package org.jitsi.xmpp.extensions.visitors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jitsi.xmpp.extensions.IQUtils
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.provider.ProviderManager
import org.jxmpp.jid.impl.JidCreate
import org.xmlunit.builder.DiffBuilder

class VisitorsIqTest : ShouldSpec() {
    init {
        VisitorsIq.registerProviders()
        val provider = ProviderManager.getIQProvider(VisitorsIq.ELEMENT, VisitorsIq.NAMESPACE)
        val jid = JidCreate.entityBareFrom("room@example.com")

        context("Parsing a valid IQ") {
            IQUtils.parse(validXml, provider).let { iq ->
                iq.shouldBeInstanceOf<VisitorsIq>()
                iq.room shouldBe jid
                iq.getConnectVnodeExtensions().let {
                    it.size shouldBe 1
                    it[0].vnode shouldBe "v1"
                }
                iq.getDisconnectVnodeExtensions().let {
                    it.size shouldBe 1
                    it[0].vnode shouldBe "v2"
                }
            }
        }

        context("Parsing invalid IQs") {
            shouldThrow<Exception> {
                IQUtils.parse(
                    """
<iq to='t' from='f' type='set'>
    <visitors xmlns='jitsi:visitors' room='not-a-good-entity-bare-jid'/>
</iq> 
                    """.trimIndent(),
                    provider
                )
            }
            shouldThrow<Exception> {
                IQUtils.parse(
                    """
<iq to='t' from='f' type='set'>
    <visitors xmlns='jitsi:visitors' room='room@example.com'>
        <connect-vnode/>
    </visitors>
</iq> 
                    """.trimIndent(),
                    provider
                )
            }
        }

        context("Serializing") {
            val iq = VisitorsIq.Builder("id").apply {
                room = jid
                addExtension(ConnectVnodePacketExtension("v1"))
                addExtension(DisconnectVnodePacketExtension("v2"))
                to(JidCreate.from("t"))
                from(JidCreate.from("f"))
                ofType(IQ.Type.set)
            }.build()

            iq.room shouldBe jid
            iq.getConnectVnodeExtensions()[0].vnode shouldBe "v1"
            iq.getDisconnectVnodeExtensions()[0].vnode shouldBe "v2"

            val diff = DiffBuilder.compare(validXml).withTest(iq.toXML().toString()).checkForIdentical().build()
            diff.hasDifferences() shouldBe false
        }
    }
}

// Whitespace matters.
private val validXml = "<iq xmlns='jabber:client' to='t' from='f' id='id' type='set'>" +
    "<visitors xmlns='jitsi:visitors' room='room@example.com'>" +
    "<connect-vnode xmlns='jitsi:visitors' vnode='v1'/>" +
    "<disconnect-vnode xmlns='jitsi:visitors' vnode='v2'/>" +
    "</visitors>" +
    "</iq>"
