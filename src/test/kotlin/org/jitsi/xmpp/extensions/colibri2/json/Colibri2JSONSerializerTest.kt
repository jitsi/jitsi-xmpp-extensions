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
package org.jitsi.xmpp.extensions.colibri2.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jitsi.xmpp.extensions.colibri2.AbstractConferenceModificationIQ
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifiedIQ
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifyIQ
import org.jitsi.xmpp.extensions.colibri2.IqProviderUtils
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.util.PacketParserUtils
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.xmlunit.builder.DiffBuilder
import java.lang.IllegalStateException
import kotlin.reflect.KClass

class Colibri2JSONSerializerTest : ShouldSpec() {

    init {
        IqProviderUtils.registerProviders()

        context("serializing an IQ") {
            expectedMappings.forEach {
                val parser = PacketParserUtils.getParserFor(it.xml)
                val iq = PacketParserUtils.parseIQ(parser)

                iq should beInstanceOf(it.clazz)

                val json = when (iq) {
                    is ConferenceModifyIQ -> Colibri2JSONSerializer.serializeConferenceModify(iq)
                    is ConferenceModifiedIQ -> Colibri2JSONSerializer.serializeConferenceModified(iq)
                    else -> throw IllegalStateException("Bad type in test")
                }.toJSONString()

                should("To JSON: ${it.name}") {
                    json.shouldEqualJson(it.json)
                }
            }
        }

        context("deserializing JSON") {
            expectedMappings.forEach {
                val parser = JSONParser()
                val json = parser.parse(it.json)
                json.shouldBeInstanceOf<JSONObject>()

                val builder = when (it.clazz) {
                    ConferenceModifyIQ::class -> Colibri2JSONDeserializer.deserializeConferenceModify(json)
                    ConferenceModifiedIQ::class -> {
                        Colibri2JSONDeserializer.deserializeConferenceModified(json).also {
                            it.ofType(IQ.Type.result)
                        }
                    }
                    else -> throw IllegalStateException("Bad type in test")
                }

                val xml = builder.build().toXML().toString()

                should("To XML: ${it.name}") {
                    val diff = DiffBuilder.compare(xml).withTest(it.xml)
                        .ignoreWhitespace()
                        .checkForIdentical().build()

                    diff.asClue {
                        diff.hasDifferences() shouldBe false
                    }
                }
            }
        }
    }
}

private data class Mapping(
    val xml: String,
    val json: String,
    val clazz: KClass<out AbstractConferenceModificationIQ<*>>,
    val name: String
)

private val expectedMappings = listOf(
    Mapping(
        name = "Endpoint with sources (same as Colibri2IQTest)",
        xml = """
<iq xmlns='jabber:client' id='id' type='get'>
  <conference-modify xmlns='jitsi:colibri2' meeting-id='88ff288c-5eeb-4ea9-bc2f-93ea38c43b78' name='myconference@jitsi.example' create='true'>
    <endpoint xmlns='jitsi:colibri2' id='bd9b6765' stats-id='Jayme-Clv'>
      <media type='audio'>
        <payload-type xmlns='urn:xmpp:jingle:apps:rtp:1' name='opus' clockrate='48000' channels='2'/>
      </media>
      <transport ice-controlling='true'/>
      <sources>
        <media-source type='video' id='bd9b6765-v1'>
          <source xmlns='urn:xmpp:jingle:apps:rtp:ssma:0' ssrc='803354056'/>
        </media-source>
      </sources>
      <force-mute audio='true' video='true'/>
      <initial-last-n value='13'/>
    </endpoint>
  </conference-modify>
</iq>
        """,
        json = """
{
  "meeting-id":"88ff288c-5eeb-4ea9-bc2f-93ea38c43b78",
  "name":"myconference@jitsi.example",
  "create":true,
  "endpoints":[
    {
      "id": "bd9b6765",
      "stats-id": "Jayme-Clv",
      "medias": [{"type":"audio", "payload-types": [{"name":"opus", "clockrate":"48000", "channels": "2"}]}],
      "transport": {"ice-controlling":true},
      "sources": [{"type":"video", "id":"bd9b6765-v1", "sources":[803354056]}],
      "force-mute": {"audio":true, "video":true},
      "initial-last-n": {"value": 13 }
    }
  ]
}
        """,
        clazz = ConferenceModifyIQ::class
    ),
    Mapping(
        name = "Something that should be exercising all the fields",
        xml = """
<iq xmlns="jabber:client" id="id" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="beccf2ed-5441-4bfe-96d6-f0f3a6796378" name="torture819371@conference.beta.meet.jit.si" create="true">
    <endpoint xmlns="jitsi:colibri2" create="true" id="79f0273e" stats-id="Garett-w1o" muc-role="visitor">
      <media type="audio">
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" channels="2" name="red" id="112" clockrate="48000">
          <parameter value="111/111"/>
        </payload-type>
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" channels="2" name="opus" id="111" clockrate="48000">
          <parameter value="1" name="useinbandfec"/>
          <parameter value="10" name="minptime"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="transport-cc"/>
        </payload-type>
        <rtp-hdrext xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" uri="urn:ietf:params:rtp-hdrext:ssrc-audio-level" id="1"/>
        <rtp-hdrext xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" uri="http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01" id="5"/>
        <extmap-allow-mixed xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" />
      </media>
      <media type="video">
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" name="VP8" id="100" clockrate="90000">
          <parameter value="800" name="x-google-start-bitrate"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="ccm" subtype="fir"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack" subtype="pli"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="transport-cc"/>
        </payload-type>
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" name="VP9" id="101" clockrate="90000">
          <parameter value="800" name="x-google-start-bitrate"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="ccm" subtype="fir"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack" subtype="pli"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="transport-cc"/>
        </payload-type>
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" name="rtx" id="96" clockrate="90000">
          <parameter value="100" name="apt"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="ccm" subtype="fir"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack" subtype="pli"/>
        </payload-type>
        <payload-type xmlns="urn:xmpp:jingle:apps:rtp:1" name="rtx" id="97" clockrate="90000">
          <parameter value="101" name="apt"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="ccm" subtype="fir"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack"/>
          <rtcp-fb xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0" type="nack" subtype="pli"/>
        </payload-type>
        <rtp-hdrext xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" uri="http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time" id="3"/>
        <rtp-hdrext xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" uri="http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01" id="5"/>
        <extmap-allow-mixed xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0" />
      </media>
      <transport ice-controlling="true"/>
      <capability name="source-names"/>
    </endpoint>
    <connects>
      <connect url='wss://example.com/audio' protocol='mediajson' type='transcriber' audio='true'/>
      <connect url='wss://example.com/video' protocol='mediajson' type='recorder' video='true'/>
    </connects>
  </conference-modify>
</iq>
        """,
        json = """
{
  "meeting-id":"beccf2ed-5441-4bfe-96d6-f0f3a6796378",
  "name":"torture819371@conference.beta.meet.jit.si",
  "create":true,
  "endpoints":[
    {
      "create":true,
      "id":"79f0273e",
      "stats-id":"Garett-w1o",
      "muc-role":"visitor",
      "medias":[
        {
          "type":"audio",
          "payload-types":[
            {
              "name": "red", "id": "112", "channels": "2", "clockrate": "48000",
              "parameters": { "null": "111/111" }
            },
            {
              "name": "opus", "id": "111", "channels": "2", "clockrate": "48000",
              "parameters": {"useinbandfec": "1", "minptime": "10" },
              "rtcp-fbs": [{"type": "transport-cc"}]
            }
          ],
          "rtp-hdrexts":[
            { "uri":"urn:ietf:params:rtp-hdrext:ssrc-audio-level", "id":1 },
            { "uri":"http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01", "id":5 }
          ],
          "extmap-allow-mixed":true
        },
        {
          "type": "video",
          "payload-types":[
            {
              "name": "VP8", "id": "100", "clockrate": "90000",
              "parameters": {"x-google-start-bitrate": "800"},
              "rtcp-fbs":[
                { "type": "ccm", "subtype": "fir" },
                { "type": "nack" },
                { "type": "nack", "subtype": "pli" },
                { "type": "transport-cc" }
              ]
            },
            {
              "name": "VP9", "id": "101", "clockrate": "90000",
              "parameters": {"x-google-start-bitrate": "800"},
              "rtcp-fbs":[
                { "type": "ccm", "subtype": "fir" },
                { "type": "nack" },
                { "type": "nack", "subtype": "pli" },
                { "type": "transport-cc" }
              ]
            },
            {
              "name": "rtx", "id": "96", "clockrate": "90000",
              "parameters": {"apt": "100"},
              "rtcp-fbs":[
                { "type": "ccm", "subtype": "fir" },
                { "type": "nack" },
                { "type": "nack", "subtype": "pli" }
              ]
            },
            {
              "name": "rtx", "id": "97", "clockrate": "90000",
              "parameters": {"apt": "101"},
              "rtcp-fbs":[
                { "type": "ccm", "subtype": "fir" },
                { "type": "nack" },
                { "type": "nack", "subtype": "pli" }
              ]
            }
          ],
          "rtp-hdrexts":[
            { "uri":"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time", "id":3 },
            { "uri":"http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01", "id":5 }
          ],
          "extmap-allow-mixed":true
        }
      ],
      "transport": { "ice-controlling": true },
      "capabilities": [ "source-names" ]
    }
  ],
  "connects": [
    { "url": "wss://example.com/audio", "protocol": "mediajson", "type": "transcriber", "audio": true },
    { "url": "wss://example.com/video", "protocol": "mediajson", "type": "recorder", "video": true }
  ]
}
        """,
        clazz = ConferenceModifyIQ::class
    ),
    Mapping(
        name = "Conference-modified with transport for an endpoint and feedback sources",
        xml = """
<iq xmlns="jabber:client" id="id" type="result">
  <conference-modified xmlns="jitsi:colibri2">
    <endpoint xmlns="jitsi:colibri2" id="79f0273e">
      <transport>
        <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1" pwd="1a5ejbent91k6io6a3fauikg22" ufrag="2ivqh1fvtf0l3h">
          <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" setup="actpass" hash="sha-256" cryptex="true">2E:CC:85:71:32:5B:B5:60:64:C8:F6:7B:6D:45:D4:34:2B:51:A0:06:B5:EA:2F:84:BC:7B:64:1F:A3:0A:69:23</fingerprint>
          <web-socket xmlns="http://jitsi.org/protocol/colibri" url="wss://beta-us-ashburn-1-global-2808-jvb-83-102-26.jitsi.net:443/colibri-ws/default-id/3d937bbdf97a23e0/79f0273e?pwd=1a5ejbent91k6io6a3fauikg22"/>
          <rtcp-mux/>
          <candidate component="1" foundation="2" generation="0" id="653aa1ba295b62480ffffffffdc52c0d9" network="0" priority="1694498815" protocol="udp" type="srflx" ip="129.80.210.199" port="10000" rel-addr="0.0.0.0" rel-port="9"/>
        </transport>
      </transport>
    </endpoint>
    <sources xmlns="jitsi:colibri2">
      <media-source type="audio" id="jvb-a0">
        <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="411312308" name="jvb-a0">
          <parameter xmlns="urn:xmpp:jingle:apps:rtp:1" name="msid" value="mixedmslabel mixedlabelaudio0"/>
        </source>
      </media-source>
      <media-source type="video" id="jvb-v0">
        <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="3929652146" name="jvb-v0">
          <parameter xmlns="urn:xmpp:jingle:apps:rtp:1" name="msid" value="mixedmslabel mixedlabelvideo0"/>
        </source>
      </media-source>
    </sources>
  </conference-modified>
</iq>
        """,
        json =
"""
{
  "endpoints": [
    {
      "id":"79f0273e",
      "transport": {
        "transport": {
          "candidates": [
            {
              "generation": "0",
              "rel-port": "9",
              "component": "1",
              "protocol": "udp",
              "port": "10000",
              "ip": "129.80.210.199",
              "foundation": "2",
              "id": "653aa1ba295b62480ffffffffdc52c0d9",
              "rel-addr": "0.0.0.0",
              "priority": "1694498815",
              "type": "srflx",
              "network": "0"
            }
          ],
          "xmlns": "urn:xmpp:jingle:transports:ice-udp:1",
          "ufrag": "2ivqh1fvtf0l3h",
          "rtcp-mux": true,
          "pwd": "1a5ejbent91k6io6a3fauikg22",
          "web-sockets": [
            "wss://beta-us-ashburn-1-global-2808-jvb-83-102-26.jitsi.net:443/colibri-ws/default-id/3d937bbdf97a23e0/79f0273e?pwd=1a5ejbent91k6io6a3fauikg22"
          ],
          "fingerprints": [
            {
              "fingerprint": "2E:CC:85:71:32:5B:B5:60:64:C8:F6:7B:6D:45:D4:34:2B:51:A0:06:B5:EA:2F:84:BC:7B:64:1F:A3:0A:69:23",
              "setup": "actpass",
              "hash": "sha-256",
              "cryptex": true
            }
          ]
        }
      }
    }
  ],
  "sources": [
    {
      "type": "audio",
      "id": "jvb-a0",
      "sources": [
        { "ssrc":411312308, "name": "jvb-a0", "parameters": { "msid": "mixedmslabel mixedlabelaudio0" } }
      ]
    },
    {
      "type": "video",
      "id": "jvb-v0",
      "sources": [
        { "ssrc":3929652146, "name": "jvb-v0", "parameters": { "msid": "mixedmslabel mixedlabelvideo0" } }
      ]
    }
  ]
}
        """,
        clazz = ConferenceModifiedIQ::class
    ),
    Mapping(
        name = "Update endpoint with transport and sources",
        xml = """
<iq xmlns="jabber:client" id="id" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="beccf2ed-5441-4bfe-96d6-f0f3a6796378">
    <endpoint xmlns="jitsi:colibri2" id="79f0273e" stats-id="Garett-w1o">
      <transport>
        <transport xmlns="urn:xmpp:jingle:transports:ice-udp:1" pwd="PirYicPKtYw4+mkIUOm1wWQm" ufrag="sXoJ">
          <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" setup="active" hash="sha-256">BB:50:EE:83:47:4C:EB:04:4F:9E:32:5D:EC:42:9C:33:1E:E5:DF:17:46:C3:AA:20:E1:F5:C6:0B:E7:C4:78:BF</fingerprint>
          <rtcp-mux/>
        </transport>
      </transport>
      <sources>
        <media-source type="audio" id="79f0273e-a0">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-a0" ssrc="3166599606"/>
        </media-source>
        <media-source type="video" id="79f0273e-v0">
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="437485591"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="2958490935"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="1565856603"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="1153580044"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="23279666"/>
          <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" name="79f0273e-v0" ssrc="382686375"/>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="437485591"/>
            <source ssrc="2958490935"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="SIM">
            <source ssrc="437485591"/>
            <source ssrc="1565856603"/>
            <source ssrc="1153580044"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="1565856603"/>
            <source ssrc="23279666"/>
          </ssrc-group>
          <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
            <source ssrc="1153580044"/>
            <source ssrc="382686375"/>
          </ssrc-group>
        </media-source>
      </sources>
    </endpoint>
  </conference-modify>
</iq>
        """,
        json = """
{
  "meeting-id": "beccf2ed-5441-4bfe-96d6-f0f3a6796378",
  "endpoints": [
    {
      "id":"79f0273e",
      "stats-id":"Garett-w1o",
      "transport": {
        "transport": {
          "xmlns": "urn:xmpp:jingle:transports:ice-udp:1",
          "ufrag":"sXoJ",
          "pwd":"PirYicPKtYw4+mkIUOm1wWQm",
          "fingerprints": [
            {
              "fingerprint": "BB:50:EE:83:47:4C:EB:04:4F:9E:32:5D:EC:42:9C:33:1E:E5:DF:17:46:C3:AA:20:E1:F5:C6:0B:E7:C4:78:BF",
              "setup": "active",
              "hash": "sha-256"
            }
          ],
          "rtcp-mux": true
        }
      },
      "sources": [
        {
          "type": "audio",
          "id": "79f0273e-a0",
          "sources": [{ "ssrc": 3166599606, "name": "79f0273e-a0"}]
        },
        {
          "type": "video",
          "id": "79f0273e-v0",
          "sources": [
            { "ssrc": 437485591, "name": "79f0273e-v0" },
            { "ssrc": 2958490935, "name": "79f0273e-v0" },
            { "ssrc": 1565856603, "name": "79f0273e-v0" },
            { "ssrc": 1153580044, "name": "79f0273e-v0" },
            { "ssrc": 23279666, "name": "79f0273e-v0" },
            { "ssrc": 382686375, "name": "79f0273e-v0" }
          ],
          "ssrc-groups": [
            { "semantics": "FID", "sources": [ 437485591, 2958490935 ] },
            { "semantics": "SIM", "sources": [ 437485591, 1565856603, 1153580044 ] },
            { "semantics": "FID", "sources": [ 1565856603, 23279666 ] },
            { "semantics": "FID", "sources": [ 1153580044, 382686375 ] }
          ]
        }
      ]
    }
  ]
}
        """,
        clazz = ConferenceModifyIQ::class
    ),
    Mapping(
        name = "Relay with endpoints",
        xml = """
<iq xmlns="jabber:client" id="id" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="cbccc2bd-c3c7-4880-acb8-cdb7accc4073">
    <relay xmlns="jitsi:colibri2" id="10.44.106.218:4096" mesh-id="2">
      <endpoints>
        <endpoint id="6f14308e">
          <sources>
            <media-source type="video" id="6f14308e-v0">
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="215028838"/>
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="300407142"/>
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="443647824"/>
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="604327257"/>
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="1827691320"/>
              <source xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" ssrc="2580436281"/>
              <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
                <source ssrc="1827691320"/>
                <source ssrc="300407142"/>
              </ssrc-group>
              <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="SIM">
                <source ssrc="1827691320"/>
                <source ssrc="2580436281"/>
                <source ssrc="604327257"/>
              </ssrc-group>
              <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
                <source ssrc="2580436281"/>
                <source ssrc="443647824"/>
              </ssrc-group>
              <ssrc-group xmlns="urn:xmpp:jingle:apps:rtp:ssma:0" semantics="FID">
                <source ssrc="604327257"/>
                <source ssrc="215028838"/>
              </ssrc-group>
            </media-source>
          </sources>
        </endpoint>
      </endpoints>
    </relay>
  </conference-modify>
</iq>
        """,
        json = """
{
  "meeting-id":"cbccc2bd-c3c7-4880-acb8-cdb7accc4073",
  "relays": [
    {
      "id":"10.44.106.218:4096",
      "mesh-id":"2",
      "endpoints": [
        {
          "id":"6f14308e",
          "sources": [
            {
              "type": "video",
              "id": "6f14308e-v0",
              "sources": [ 215028838, 300407142, 443647824, 604327257, 1827691320, 2580436281 ],
              "ssrc-groups": [
                { "semantics": "FID", "sources": [ 1827691320, 300407142 ] },
                { "semantics": "SIM", "sources": [ 1827691320, 2580436281, 604327257 ] },
                { "semantics": "FID", "sources": [ 2580436281, 443647824 ] },
                { "semantics": "FID", "sources": [ 604327257, 215028838 ] }
              ]
            }
          ]
        }
      ]
    }
  ]
}
        """,
        clazz = ConferenceModifyIQ::class
    ),
    Mapping(
        name = "Expire conference",
        xml = """
<iq xmlns="jabber:client" id="id" type="get">
  <conference-modify xmlns="jitsi:colibri2" meeting-id="cbccc2bd-c3c7-4880-acb8-cdb7accc4073" expire="true"/>
</iq>
        """,
        json = """
{
  "meeting-id":"cbccc2bd-c3c7-4880-acb8-cdb7accc4073",
   "expire": true
}
        """,
        clazz = ConferenceModifyIQ::class
    )
)
