/*
 * Copyright @ 2021 - present 8x8, Inc.
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
package org.jitsi.xmpp.util

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.jitsi.xmpp.util.RedactColibriIp.Companion.redact
import org.xmlunit.builder.DiffBuilder

class RedactColibriIpTest : ShouldSpec() {
    init {
        context("Redacting an IPv4 address from a Colibri message") {
            val sourceXml =
                """
<iq xmlns="jabber:client" to="jvb@auth.jvb.meet.jit.si/W3FO3dsVkgxJ" from="jvbbrewery@muc.jvb.meet.jit.si/focus" id="anZiQGF1dGguanZiLm1lZXQuaml0LnNpL1czRk8zZHNWa2d4SgBSTUNCRi04Nzg1AH+E6w3dEhtN" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="8f1b1e31-6f65-436c-b4f3-a3ad5665e614">
    <endpoint id="9374693a" stats-id="Flossie-5QS">
      <transport>
        <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1" ufrag="rthS" pwd="fVE8os5CpiAgboJ/EUEIg4DR">
          <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" hash="sha-256" setup="active">31:D8:D0:A2:E8:2F:A6:43:5D:0C:69:BE:23:7A:0C:B1:4D:22:56:18:0B:5D:79:78:F6:E2:71:62:2E:46:45:E9</fingerprint>
          <rtcp-mux/>
          <candidate id="mgrbo2620g" generation="0" port="46862" ip="192.0.2.0" foundation="2763476921" network="1" component="1" type="host" protocol="udp" priority="2122260223"/>
        </transport>
      </transport>
      <sources>
        <media-source id="9374693a-a0" type="audio">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-a0" ssrc="1808569840"/>
        </media-source>
        <media-source id="9374693a-v0" type="video">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2595344399" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2874594781" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="480307322" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2869554268" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="4212678704" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2166126985" videoType="camera"/>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="2595344399"/>
            <source ssrc="2874594781"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="SIM">
            <source ssrc="2595344399"/>
            <source ssrc="480307322"/>
            <source ssrc="2869554268"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="480307322"/>
            <source ssrc="4212678704"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="2869554268"/>
            <source ssrc="2166126985"/>
          </ssrc-group>
        </media-source>
      </sources>
      <initial-last-n value="10"/>
    </endpoint>
  </conference-modify>
</iq>
                """

            val expectedXml =
                """
<iq xmlns="jabber:client" to="jvb@auth.jvb.meet.jit.si/W3FO3dsVkgxJ" from="jvbbrewery@muc.jvb.meet.jit.si/focus" id="anZiQGF1dGguanZiLm1lZXQuaml0LnNpL1czRk8zZHNWa2d4SgBSTUNCRi04Nzg1AH+E6w3dEhtN" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="8f1b1e31-6f65-436c-b4f3-a3ad5665e614">
    <endpoint id="9374693a" stats-id="Flossie-5QS">
      <transport>
        <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1" ufrag="rthS" pwd="fVE8os5CpiAgboJ/EUEIg4DR">
          <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" hash="sha-256" setup="active">31:D8:D0:A2:E8:2F:A6:43:5D:0C:69:BE:23:7A:0C:B1:4D:22:56:18:0B:5D:79:78:F6:E2:71:62:2E:46:45:E9</fingerprint>
          <rtcp-mux/>
          <candidate id="mgrbo2620g" generation="0" port="46862" ip="xx.xx.xx.xx" foundation="2763476921" network="1" component="1" type="host" protocol="udp" priority="2122260223"/>
        </transport>
      </transport>
      <sources>
        <media-source id="9374693a-a0" type="audio">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-a0" ssrc="1808569840"/>
        </media-source>
        <media-source id="9374693a-v0" type="video">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2595344399" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2874594781" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="480307322" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2869554268" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="4212678704" videoType="camera"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="9374693a-v0" ssrc="2166126985" videoType="camera"/>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="2595344399"/>
            <source ssrc="2874594781"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="SIM">
            <source ssrc="2595344399"/>
            <source ssrc="480307322"/>
            <source ssrc="2869554268"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="480307322"/>
            <source ssrc="4212678704"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="2869554268"/>
            <source ssrc="2166126985"/>
          </ssrc-group>
        </media-source>
      </sources>
      <initial-last-n value="10"/>
    </endpoint>
  </conference-modify>
</iq>
                """

            val redacted = redact(sourceXml)

            should("Convert to expected xml") {
                val diff = DiffBuilder.compare(expectedXml).withTest(redacted)
                    .ignoreWhitespace()
                    .checkForIdentical().build()

                diff.asClue {
                    diff.hasDifferences() shouldBe false
                }
            }
        }

        context("Redacting IPv6 addresses from a Colibri message") {
            val sourceXml =
                """
<iq xmlns="jabber:client"><conference-modify xmlns="jitsi:colibri2"><endpoint><transport>
   <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1">
     <candidate ip="2001:db8:4e80:620:a8e0:6bd4:6606:4cae"/>
     <candidate ip="fe80::f482:48ff:fe0b:91ae"/>
     <candidate ip="::1"/>
    </transport>
</transport></endpoint></conference-modify></iq>
                    """
            val expectedXml =
                """
<iq xmlns="jabber:client"><conference-modify xmlns="jitsi:colibri2"><endpoint><transport>
   <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1">
     <candidate ip="2xxx::xxx"/>
     <candidate ip="fe80::xxx"/>
     <candidate ip="::1"/>
    </transport>
</transport></endpoint></conference-modify></iq>
                    """
            val redacted = redact(sourceXml)

            should("Convert to expected xml") {
                val diff = DiffBuilder.compare(expectedXml).withTest(redacted)
                    .ignoreWhitespace()
                    .checkForIdentical().build()

                diff.asClue {
                    diff.hasDifferences() shouldBe false
                }
            }
        }

        context("Turn candidates") {
            val sourceXml = """
<iq xmlns="jabber:client"><conference-modify xmlns="jitsi:colibri2"><endpoint><transport>
   <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1">
     <candidate ip="2001:db8:4e80:620:a8e0:6bd4:6606:4cae" type="relay"/>
     <candidate ip="192.0.2.0" type="relay"/>
     <candidate ip="::1"/>
    </transport>
</transport></endpoint></conference-modify></iq>
           """

            val redacted = redact(sourceXml)

            should("Be unchanged") {
                val diff = DiffBuilder.compare(sourceXml).withTest(redacted)
                    .ignoreWhitespace()
                    .checkForIdentical().build()

                diff.asClue {
                    diff.hasDifferences() shouldBe false
                }
            }
        }
        context("Redacting related addresses") {
            val sourceXml = """
<iq xmlns="jabber:client"><conference-modify xmlns="jitsi:colibri2"><endpoint><transport>
   <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1">
     <candidate ip="192.0.2.0" type="srflx" rel-addr="10.0.0.23"/>
     <candidate ip="192.0.4.0" type="srflx" rel-addr="0.0.0.0"/>
    </transport>
</transport></endpoint></conference-modify></iq>
            """
            val expectedXml = """
<iq xmlns="jabber:client"><conference-modify xmlns="jitsi:colibri2"><endpoint><transport>
   <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1">
     <candidate ip="xx.xx.xx.xx" type="srflx" rel-addr="xx.xx.xx.xx"/>
     <candidate ip="xx.xx.xx.xx" type="srflx" rel-addr="0.0.0.0"/>
    </transport>
</transport></endpoint></conference-modify></iq>
            """
            val redacted = redact(sourceXml)

            should("Convert to expected xml") {
                val diff = DiffBuilder.compare(expectedXml).withTest(redacted)
                    .ignoreWhitespace()
                    .checkForIdentical().build()

                diff.asClue {
                    diff.hasDifferences() shouldBe false
                }
            }
        }
        context("Candidates of relays") {
            val sourceXml = """
<iq xmlns="jabber:client" to="jvb@auth.jvb.meet.jit.si/QZsC1sBuSi9w" from="jvbbrewery@muc.jvb.meet.jit.si/focus" id="anZiQGF1dGguanZiLm1lZXQuaml0LnNpL1Fac0Mxc0J1U2k5dwBVMUNKWi0xNzUzNzE2AKg5GKSodsNR" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="b85fb8aa-638b-4298-82e3-0ab2788bf2a2">
    <relay id="meet-jit-si-jvb-40-116-18">
      <transport>
        <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1" ufrag="jnt61i1snq070" pwd="7rtljkbqec6m3k9m5qc3e99g3d">
          <rtcp-mux/>
          <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" hash="sha-256" setup="active" cryptex="true">E3:47:32:6B:70:67:56:EB:05:B3:B8:E2:95:E6:AD:A4:B7:5A:8D:CA:11:DF:5B:11:69:7C:C9:6E:4D:0B:E2:F4</fingerprint>
          <candidate id="47dea4f2fb13b3506bb446b1" network="0" generation="0" priority="2130706431" foundation="1" component="1" type="host" ip="10.40.116.18" protocol="udp" port="10000"/>
          <candidate id="67a05aecfb13b350ffffffffe3c9c3c9" network="0" rel-port="9" generation="0" priority="1694498815" foundation="2" component="1" protocol="udp" type="srflx" ip="130.61.241.42" rel-addr="0.0.0.0" port="10000"/>
        </transport>
      </transport>
    </relay>
  </conference-modify>
</iq>
            """
            val redacted = redact(sourceXml)

            should("Be unchanged") {
                val diff = DiffBuilder.compare(sourceXml).withTest(redacted)
                    .ignoreWhitespace()
                    .checkForIdentical().build()

                diff.asClue {
                    diff.hasDifferences() shouldBe false
                }
            }
        }
    }
}
