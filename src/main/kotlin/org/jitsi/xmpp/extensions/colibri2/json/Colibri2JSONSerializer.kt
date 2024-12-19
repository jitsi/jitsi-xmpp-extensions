/*
 * Copyright @ 2015 - Present, 8x8 Inc
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

import org.jitsi.xmpp.extensions.colibri.SourcePacketExtension
import org.jitsi.xmpp.extensions.colibri.json.JSONSerializer
import org.jitsi.xmpp.extensions.colibri2.AbstractConferenceEntity
import org.jitsi.xmpp.extensions.colibri2.AbstractConferenceModificationIQ
import org.jitsi.xmpp.extensions.colibri2.Capability
import org.jitsi.xmpp.extensions.colibri2.Colibri2Endpoint
import org.jitsi.xmpp.extensions.colibri2.Colibri2Relay
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifiedIQ
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifyIQ
import org.jitsi.xmpp.extensions.colibri2.Connect
import org.jitsi.xmpp.extensions.colibri2.Connects
import org.jitsi.xmpp.extensions.colibri2.ForceMute
import org.jitsi.xmpp.extensions.colibri2.InitialLastN
import org.jitsi.xmpp.extensions.colibri2.Media
import org.jitsi.xmpp.extensions.colibri2.MediaSource
import org.jitsi.xmpp.extensions.colibri2.Sctp
import org.jitsi.xmpp.extensions.colibri2.Sources
import org.jitsi.xmpp.extensions.colibri2.Transport
import org.jitsi.xmpp.extensions.jingle.ExtmapAllowMixedPacketExtension
import org.jitsi.xmpp.extensions.jingle.IceUdpTransportPacketExtension
import org.jitsi.xmpp.extensions.jingle.PayloadTypePacketExtension
import org.jitsi.xmpp.extensions.jingle.RTPHdrExtPacketExtension
import org.jitsi.xmpp.extensions.jingle.SourceGroupPacketExtension
import org.json.simple.JSONArray
import org.json.simple.JSONObject

object Colibri2JSONSerializer {
    /**
     * The name of the JSON item which specifies the value of the
     * [ConferenceModifyIQ.getEndpoints] property of [ConferenceModifyIQ].
     */
    const val ENDPOINTS = "endpoints"

    /**
     * The name of the JSON item which specifies the value of the
     * [ConferenceModifyIQ.getRelays] property of [ConferenceModifyIQ].
     */
    const val RELAYS = Colibri2Relay.ELEMENT + "s"

    /**
     * The name of the JSON item which specifies the value of the
     * [Colibri2Endpoint.getMedia] property of [Colibri2Endpoint]
     */
    const val MEDIA_LIST = Media.ELEMENT + "s"

    /**
     * The name of the JSON item which specifies the value of the
     * [Colibri2Endpoint.getCapabilities] property of [Colibri2Endpoint]
     */
    const val CAPABILITIES_LIST = "capabilities"

    /**
     * The name of the JSON item which specifies the value of the
     * [Media.getPayloadTypes] property of [Media].
     */
    const val PAYLOAD_TYPES = PayloadTypePacketExtension.ELEMENT + "s"

    /**
     * The name of the JSON item which specifics the value of the
     * [Media.getRtpHdrExts] property of [Media].
     */
    const val RTP_HEADER_EXTS = RTPHdrExtPacketExtension.ELEMENT + "s"

    /**
     * The name of the JSON item which specifies the value of the
     * [MediaSource.getSsrcGroups] property of [MediaSource].
     */
    const val SOURCE_GROUPS = SourceGroupPacketExtension.ELEMENT + "s"

    /**
     * The name of the JSON pair which specifies the value of the
     * [MediaSource.getSources] property of [MediaSource].
     */
    const val SOURCES = SourcePacketExtension.ELEMENT + "s"

    private fun serializeMedia(media: Media): JSONObject {
        return JSONObject().apply {
            put(Media.TYPE_ATTR_NAME, media.type.toString())
            if (media.payloadTypes.isNotEmpty()) {
                put(PAYLOAD_TYPES, JSONSerializer.serializePayloadTypes(media.payloadTypes))
            }
            if (media.rtpHdrExts.isNotEmpty()) {
                put(RTP_HEADER_EXTS, JSONSerializer.serializeRtpHdrExts(media.rtpHdrExts))
            }
            media.extmapAllowMixed?.let { put(ExtmapAllowMixedPacketExtension.ELEMENT, true) }
        }
    }

    private fun serializeSctp(sctp: Sctp): JSONObject {
        return JSONObject().apply {
            sctp.port?.let { put(Sctp.PORT_ATTR_NAME, it) }
            sctp.role?.let { put(Sctp.ROLE_ATTR_NAME, it) }
        }
    }

    private fun serializeTransport(transport: Transport): JSONObject {
        return JSONObject().apply {
            if (transport.iceControlling != Transport.ICE_CONTROLLING_DEFAULT) {
                put(Transport.ICE_CONTROLLING_ATTR_NAME, transport.iceControlling)
            }
            if (transport.useUniquePort != Transport.USE_UNIQUE_PORT_DEFAULT) {
                put(Transport.USE_UNIQUE_PORT_ATTR_NAME, transport.useUniquePort)
            }
            transport.iceUdpTransport?.let {
                put(IceUdpTransportPacketExtension.ELEMENT, JSONSerializer.serializeTransport(it))
            }
            transport.sctp?.let {
                put(Sctp.ELEMENT, serializeSctp(it))
            }
        }
    }

    private fun serializeMediaSource(source: MediaSource): JSONObject {
        return JSONObject().apply {
            put(MediaSource.TYPE_ATTR_NAME, source.type.toString())
            put(MediaSource.ID_NAME, source.id)
            if (source.sources.isNotEmpty()) {
                put(SOURCES, JSONSerializer.serializeSources(source.sources))
            }
            if (source.ssrcGroups.isNotEmpty()) {
                put(SOURCE_GROUPS, JSONSerializer.serializeSourceGroups(source.ssrcGroups))
            }
        }
    }

    private fun serializeMedias(medias: Collection<Media>): JSONArray {
        return JSONArray().apply {
            medias.forEach { add(serializeMedia(it)) }
        }
    }

    private fun serializeSources(sources: Sources): JSONArray {
        return JSONArray().apply {
            sources.mediaSources.forEach { add(serializeMediaSource(it)) }
        }
    }

    private fun serializeAbstractConferenceEntity(entity: AbstractConferenceEntity): JSONObject {
        return JSONObject().apply {
            put(AbstractConferenceEntity.ID_ATTR_NAME, entity.id)

            if (entity.create != AbstractConferenceEntity.CREATE_DEFAULT) {
                put(AbstractConferenceEntity.CREATE_ATTR_NAME, entity.create)
            }

            if (entity.expire != AbstractConferenceEntity.EXPIRE_DEFAULT) {
                put(AbstractConferenceEntity.EXPIRE_ATTR_NAME, entity.expire)
            }

            if (entity.media.isNotEmpty()) {
                put(MEDIA_LIST, serializeMedias(entity.media))
            }

            entity.transport?.let { put(Transport.ELEMENT, serializeTransport(it)) }

            entity.sources?.let { put(Sources.ELEMENT, serializeSources(it)) }
        }
    }

    private fun serializeForceMute(forceMute: ForceMute): JSONObject {
        return JSONObject().apply {
            put(ForceMute.AUDIO_ATTR_NAME, forceMute.audio)
            put(ForceMute.VIDEO_ATTR_NAME, forceMute.video)
        }
    }

    private fun serializeInitialLastN(initialLastN: InitialLastN) = JSONObject().apply {
        put(InitialLastN.VALUE_ATTR_NAME, initialLastN.value)
    }

    private fun serializeCapabilities(capabilities: Collection<Capability>): JSONArray {
        return JSONArray().apply {
            capabilities.forEach { add(it.name) }
        }
    }

    private fun serializeEndpoint(endpoint: Colibri2Endpoint): JSONObject {
        return serializeAbstractConferenceEntity(endpoint).apply {
            endpoint.statsId?.apply { put(Colibri2Endpoint.STATS_ID_ATTR_NAME, this) }
            endpoint.mucRole?.apply { put(Colibri2Endpoint.MUC_ROLE_ATTR_NAME, this.toString()) }
            endpoint.forceMute?.apply { put(ForceMute.ELEMENT, serializeForceMute(this)) }
            endpoint.initialLastN?.apply { put(InitialLastN.ELEMENT, serializeInitialLastN(this)) }
            if (endpoint.capabilities.isNotEmpty()) {
                put(CAPABILITIES_LIST, serializeCapabilities(endpoint.capabilities))
            }
        }
    }

    private fun serializeRelay(relay: Colibri2Relay): JSONObject {
        return serializeAbstractConferenceEntity(relay).apply {
            relay.meshId?.apply { put(Colibri2Relay.MESH_ID_ATTR_NAME, this) }
            relay.endpoints?.let { put(ENDPOINTS, serializeEndpoints(it.endpoints)) }
        }
    }

    private fun serializeEndpoints(endpoints: Collection<Colibri2Endpoint>): JSONArray {
        return JSONArray().apply {
            endpoints.forEach { add(serializeEndpoint(it)) }
        }
    }

    private fun serializeRelays(relays: Collection<Colibri2Relay>): JSONArray {
        return JSONArray().apply {
            relays.forEach { add(serializeRelay(it)) }
        }
    }

    private fun serializeAbstractConferenceModificationIQ(iq: AbstractConferenceModificationIQ<*>): JSONObject {
        return JSONObject().apply {
            if (iq.endpoints.isNotEmpty()) {
                put(ENDPOINTS, serializeEndpoints(iq.endpoints))
            }
            if (iq.relays.isNotEmpty()) {
                put(RELAYS, serializeRelays(iq.relays))
            }
        }
    }

    private fun serializeConnect(connect: Connect) = JSONObject().apply {
        put(Connect.URL_ATTR_NAME, connect.url.toString())
        put(Connect.PROTOCOL_ATTR_NAME, connect.protocol.toString().lowercase())
        put(Connect.TYPE_ATTR_NAME, connect.type.toString().lowercase())
        if (connect.audio) put(Connect.AUDIO_ATTR_NAME, true)
        if (connect.video) put(Connect.VIDEO_ATTR_NAME, true)
    }

    private fun serializeConnects(connects: Connects) = JSONArray().apply {
        connects.getConnects().forEach { add(serializeConnect(it)) }
    }

    @JvmStatic
    fun serializeConferenceModify(iq: ConferenceModifyIQ): JSONObject {
        return serializeAbstractConferenceModificationIQ(iq).apply {
            if (iq.create != ConferenceModifyIQ.CREATE_DEFAULT) {
                put(ConferenceModifyIQ.CREATE_ATTR_NAME, iq.create)
            }

            if (iq.expire != ConferenceModifyIQ.EXPIRE_DEFAULT) {
                put(ConferenceModifyIQ.EXPIRE_ATTR_NAME, iq.expire)
            }

            if (iq.isRtcstatsEnabled != ConferenceModifyIQ.RTCSTATS_ENABLED_DEFAULT) {
                put(ConferenceModifyIQ.RTCSTATS_ENABLED_ATTR_NAME, iq.isRtcstatsEnabled)
            }

            iq.connects?.let {
                put(Connects.ELEMENT, serializeConnects(it))
            }

            put(ConferenceModifyIQ.MEETING_ID_ATTR_NAME, iq.meetingId)

            iq.conferenceName?.let { put(ConferenceModifyIQ.NAME_ATTR_NAME, it) }
        }
    }

    @JvmStatic
    fun serializeConferenceModified(iq: ConferenceModifiedIQ): JSONObject {
        return serializeAbstractConferenceModificationIQ(iq).apply {
            iq.sources?.let { put(Sources.ELEMENT, serializeSources(it)) }
        }
    }
}
