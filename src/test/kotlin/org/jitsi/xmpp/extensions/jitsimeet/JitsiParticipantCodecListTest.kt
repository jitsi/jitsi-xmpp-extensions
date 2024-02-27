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
package org.jitsi.xmpp.extensions.jitsimeet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jitsi.xmpp.extensions.DefaultPacketExtensionProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.util.PacketParserUtils

class JitsiParticipantCodecListTest : ShouldSpec() {
    private val sampleXml =
        """
            <presence xmlns="jabber:client" xml:lang="en" to="focus@auth.example" from="test@conference.example/deadbeef">
              <jitsi_participant_codecList>vp9,vp8,h264</jitsi_participant_codecList>
            </presence>
        """.trimIndent()

    init {
        context("Getting a codec list set from its text") {
            val codecList = JitsiParticipantCodecList()
            codecList.text = "vp9,vp8,h264"
            should("Parse as the appropriate list of codecs") {
                codecList.codecs.shouldContainExactly("vp9", "vp8", "h264")
            }
        }
        context("codec values in text") {
            val codecList = JitsiParticipantCodecList()
            codecList.text = "VP9,VP8,H264"
            should("be case-normalized when read") {
                codecList.codecs.shouldContainExactly("vp9", "vp8", "h264")
            }
        }
        context("Setting a codec list") {
            val codecList = JitsiParticipantCodecList()
            codecList.codecs = listOf("vp9", "vp8", "h264")
            should("Be serialized properly") {
                codecList.text shouldBe "vp9,vp8,h264"
            }
        }
        context("Codec values as set") {
            val codecList = JitsiParticipantCodecList()
            codecList.codecs = listOf("VP9", "VP8", "H264")
            should("be case-normalized when serialized") {
                codecList.text shouldBe "vp9,vp8,h264"
            }
        }
        context("Parsing a Presence stanza containing a codec list") {
            ProviderManager.addExtensionProvider(
                JitsiParticipantCodecList.ELEMENT,
                JitsiParticipantCodecList.NAMESPACE,
                DefaultPacketExtensionProvider(JitsiParticipantCodecList::class.java)
            )

            val parser = PacketParserUtils.getParserFor(sampleXml)
            val presence = PacketParserUtils.parsePresence(parser)
            val codecList = presence.getExtension(JitsiParticipantCodecList::class.java)
            should("Parse properly") {
                codecList shouldNotBe null
                codecList.codecs.shouldContainExactly("vp9", "vp8", "h264")
            }
        }
    }
}
