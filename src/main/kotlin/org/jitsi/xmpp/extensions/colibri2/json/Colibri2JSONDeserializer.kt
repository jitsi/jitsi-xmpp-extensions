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

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.jitsi.utils.MediaType
import org.jitsi.xmpp.extensions.colibri.json.JSONDeserializer
import org.jitsi.xmpp.extensions.colibri2.AbstractConferenceEntity
import org.jitsi.xmpp.extensions.colibri2.AbstractConferenceModificationIQ
import org.jitsi.xmpp.extensions.colibri2.Colibri2Endpoint
import org.jitsi.xmpp.extensions.colibri2.Colibri2Relay
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifiedIQ
import org.jitsi.xmpp.extensions.colibri2.ConferenceModifyIQ
import org.jitsi.xmpp.extensions.colibri2.Connect
import org.jitsi.xmpp.extensions.colibri2.Connects
import org.jitsi.xmpp.extensions.colibri2.Endpoints
import org.jitsi.xmpp.extensions.colibri2.ForceMute
import org.jitsi.xmpp.extensions.colibri2.InitialLastN
import org.jitsi.xmpp.extensions.colibri2.Media
import org.jitsi.xmpp.extensions.colibri2.MediaSource
import org.jitsi.xmpp.extensions.colibri2.Sctp
import org.jitsi.xmpp.extensions.colibri2.Sources
import org.jitsi.xmpp.extensions.colibri2.Transport
import org.jitsi.xmpp.extensions.jingle.ExtmapAllowMixedPacketExtension
import org.jitsi.xmpp.extensions.jingle.IceUdpTransportPacketExtension
import org.jivesoftware.smackx.muc.MUCRole
import java.lang.IllegalArgumentException
import java.net.URI

object Colibri2JSONDeserializer {
    private fun deserializeMedia(media: ObjectNode): Media {
        return Media.getBuilder().apply {
            media[Media.TYPE_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setType(MediaType.parseString(it.asText()))
                }
            }

            media[Colibri2JSONSerializer.PAYLOAD_TYPES]?.let { payloadTypes ->
                if (payloadTypes is ArrayNode) {
                    JSONDeserializer.deserializePayloadTypes(payloadTypes).forEach { addPayloadType(it) }
                }
            }

            media[Colibri2JSONSerializer.RTP_HEADER_EXTS]?.let { rtpHdrExts ->
                if (rtpHdrExts is ArrayNode) {
                    JSONDeserializer.deserializeHeaderExtensions(rtpHdrExts).forEach { addRtpHdrExt(it) }
                }
            }

            media[ExtmapAllowMixedPacketExtension.ELEMENT]?.let {
                if (it.isBoolean) {
                    setExtmapAllowMixed(ExtmapAllowMixedPacketExtension())
                }
            }
        }.build()
    }

    private fun deserializeSctp(sctp: ObjectNode): Sctp {
        return Sctp.Builder().apply {
            sctp[Sctp.ROLE_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setRole(Sctp.Role.parseString(it.asText()))
                }
            }

            sctp[Sctp.PORT_ATTR_NAME]?.let {
                if (it.isNumber) {
                    setPort(it.asInt())
                }
            }
        }.build()
    }

    private fun deserializeTransport(transport: ObjectNode): Transport {
        return Transport.getBuilder().apply {
            transport[Transport.ICE_CONTROLLING_ATTR_NAME]?.let {
                if (it.isBoolean) {
                    setIceControlling(it.asBoolean())
                }
            }

            transport[Transport.USE_UNIQUE_PORT_ATTR_NAME]?.let {
                if (it.isBoolean) {
                    setUseUniquePort(it.asBoolean())
                }
            }

            transport[IceUdpTransportPacketExtension.ELEMENT]?.let {
                if (it is ObjectNode) {
                    setIceUdpExtension(JSONDeserializer.deserializeTransport(it))
                }
            }

            transport[Sctp.ELEMENT]?.let {
                if (it is ObjectNode) {
                    setSctp(deserializeSctp(it))
                }
            }
        }.build()
    }

    private fun deserializeMediaSource(mediaSource: ObjectNode): MediaSource {
        return MediaSource.getBuilder().apply {
            mediaSource[MediaSource.TYPE_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setType(MediaType.parseString(it.asText()))
                }
            }

            mediaSource[MediaSource.ID_NAME]?.let {
                if (it.isTextual) {
                    setId(it.asText())
                }
            }

            mediaSource[Colibri2JSONSerializer.SOURCES]?.let { sources ->
                if (sources is ArrayNode) {
                    sources.forEach { addSource(JSONDeserializer.deserializeSource(it)) }
                }
            }

            mediaSource[Colibri2JSONSerializer.SOURCE_GROUPS]?.let { sourceGroups ->
                if (sourceGroups is ArrayNode) {
                    sourceGroups.forEach { addSsrcGroup(JSONDeserializer.deserializeSourceGroup(it)) }
                }
            }
        }.build()
    }

    private fun deserializeMedias(medias: ArrayNode): Collection<Media> {
        return ArrayList<Media>().apply {
            medias.forEach {
                if (it is ObjectNode) {
                    add(deserializeMedia(it))
                }
            }
        }
    }

    private fun deserializeSources(sources: ArrayNode): Sources {
        return Sources.getBuilder().apply {
            sources.forEach {
                if (it is ObjectNode) {
                    addMediaSource(deserializeMediaSource(it))
                }
            }
        }.build()
    }

    private fun deserializeAbstractConferenceEntityToBuilder(
        entity: ObjectNode,
        builder: AbstractConferenceEntity.Builder
    ) {
        entity[AbstractConferenceEntity.ID_ATTR_NAME]?.let {
            if (it.isTextual) {
                builder.setId(it.asText())
            }
        }

        entity[AbstractConferenceEntity.CREATE_ATTR_NAME]?.let {
            if (it.isBoolean) {
                builder.setCreate(it.asBoolean())
            }
        }

        entity[AbstractConferenceEntity.EXPIRE_ATTR_NAME]?.let {
            if (it.isBoolean) {
                builder.setExpire(it.asBoolean())
            }
        }

        entity[Colibri2JSONSerializer.MEDIA_LIST]?.let { medias ->
            if (medias is ArrayNode) {
                deserializeMedias(medias).forEach { builder.addMedia(it) }
            }
        }

        entity[Transport.ELEMENT]?.let {
            if (it is ObjectNode) {
                builder.setTransport(deserializeTransport(it))
            }
        }

        entity[Sources.ELEMENT]?.let {
            if (it is ArrayNode) {
                builder.setSources(deserializeSources(it))
            }
        }
    }

    private fun deserializeInitialLastN(initialLastN: ObjectNode) = InitialLastN(
        initialLastN[InitialLastN.VALUE_ATTR_NAME]?.takeIf { it.isNumber }?.asInt()
            ?: throw IllegalArgumentException("Invalid 'value'")
    )

    private fun deserializeForceMute(forceMute: ObjectNode): ForceMute {
        val audio = forceMute[ForceMute.AUDIO_ATTR_NAME]
        val video = forceMute[ForceMute.VIDEO_ATTR_NAME]

        return ForceMute(
            if (audio != null && audio.isBoolean) {
                audio.asBoolean()
            } else {
                ForceMute.AUDIO_DEFAULT
            },
            if (video != null && video.isBoolean) {
                video.asBoolean()
            } else {
                ForceMute.VIDEO_DEFAULT
            }
        )
    }

    private fun deserializeEndpoint(endpoint: ObjectNode): Colibri2Endpoint {
        return Colibri2Endpoint.getBuilder().apply {
            deserializeAbstractConferenceEntityToBuilder(endpoint, this)

            endpoint[Colibri2Endpoint.STATS_ID_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setStatsId(it.asText())
                }
            }

            endpoint[Colibri2Endpoint.MUC_ROLE_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setMucRole(MUCRole.fromString(it.asText()))
                }
            }

            endpoint[ForceMute.ELEMENT]?.let {
                if (it is ObjectNode) {
                    setForceMute(deserializeForceMute(it))
                }
            }

            endpoint[InitialLastN.ELEMENT]?.let {
                if (it is ObjectNode) {
                    setInitialLastN(deserializeInitialLastN(it))
                }
            }

            endpoint[Colibri2JSONSerializer.CAPABILITIES_LIST]?.let { capabilities ->
                if (capabilities is ArrayNode) {
                    capabilities.forEach {
                        if (it.isTextual) {
                            addCapability(it.asText())
                        }
                    }
                }
            }
        }.build()
    }

    private fun deserializeRelay(relay: ObjectNode): Colibri2Relay {
        return Colibri2Relay.getBuilder().apply {
            deserializeAbstractConferenceEntityToBuilder(relay, this)

            relay[Colibri2Relay.MESH_ID_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setMeshId(it.asText())
                }
            }

            relay[Colibri2JSONSerializer.ENDPOINTS]?.let { endpoints ->
                if (endpoints is ArrayNode) {
                    setEndpoints(
                        Endpoints.getBuilder().apply {
                            deserializeEndpoints(endpoints).forEach { addEndpoint(it) }
                        }.build()
                    )
                }
            }
        }.build()
    }

    private fun deserializeEndpoints(endpoints: ArrayNode): Collection<Colibri2Endpoint> {
        return ArrayList<Colibri2Endpoint>().apply {
            endpoints.forEach {
                if (it is ObjectNode) {
                    add(deserializeEndpoint(it))
                }
            }
        }
    }

    private fun deserializeRelays(relays: ArrayNode): Collection<Colibri2Relay> {
        return ArrayList<Colibri2Relay>().apply {
            relays.forEach {
                if (it is ObjectNode) {
                    add(deserializeRelay(it))
                }
            }
        }
    }

    private fun deserializeAbstractConferenceModificationToBuilder(
        modification: ObjectNode,
        builder: AbstractConferenceModificationIQ.Builder<*>
    ) {
        modification[Colibri2JSONSerializer.ENDPOINTS].let { endpoints ->
            if (endpoints is ArrayNode) {
                deserializeEndpoints(endpoints).forEach { builder.addConferenceEntity(it) }
            }
        }

        modification[Colibri2JSONSerializer.RELAYS].let { relays ->
            if (relays is ArrayNode) {
                deserializeRelays(relays).forEach { builder.addConferenceEntity(it) }
            }
        }
    }

    @JvmStatic
    fun deserializeConferenceModify(conferenceModify: ObjectNode): ConferenceModifyIQ.Builder {
        return ConferenceModifyIQ.builder("id").apply {
            deserializeAbstractConferenceModificationToBuilder(conferenceModify, this)

            conferenceModify[ConferenceModifyIQ.MEETING_ID_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setMeetingId(it.asText())
                }
            }

            conferenceModify[ConferenceModifyIQ.NAME_ATTR_NAME]?.let {
                if (it.isTextual) {
                    setConferenceName(it.asText())
                }
            }

            conferenceModify[ConferenceModifyIQ.CREATE_ATTR_NAME]?.let {
                if (it.isBoolean) {
                    setCreate(it.asBoolean())
                }
            }

            conferenceModify[ConferenceModifyIQ.EXPIRE_ATTR_NAME]?.let {
                if (it.isBoolean) {
                    setExpire(it.asBoolean())
                }
            }

            conferenceModify[ConferenceModifyIQ.RTCSTATS_ENABLED_ATTR_NAME]?.let {
                if (it.isBoolean) {
                    setRtcstatsEnabled(it.asBoolean())
                }
            }

            conferenceModify[Connects.ELEMENT]?.let {
                if (it is ArrayNode) {
                    var added = false
                    it.forEach { connect ->
                        if (connect is ObjectNode) {
                            val connectObj = Connect(
                                URI(connect[Connect.URL_ATTR_NAME]!!.asText()),
                                protocol = Connect.Protocols.valueOf(
                                    connect[Connect.PROTOCOL_ATTR_NAME]!!.asText().uppercase()
                                ),
                                type = Connect.Types.valueOf(
                                    connect[Connect.TYPE_ATTR_NAME]!!.asText().uppercase()
                                ),
                                audio = connect[Connect.AUDIO_ATTR_NAME]?.asBoolean() ?: false,
                                video = connect[Connect.VIDEO_ATTR_NAME]?.asBoolean() ?: false
                            )

                            // Deserialize HTTP headers
                            connect["headers"]?.let { headers ->
                                if (headers is ObjectNode) {
                                    headers.fields().forEach { e ->
                                        if (e.value.isTextual) {
                                            connectObj.addHttpHeader(e.key, e.value.asText())
                                        }
                                    }
                                }
                            }

                            // Deserialize ping
                            connect["ping"]?.let { ping ->
                                if (ping is ObjectNode) {
                                    val interval = ping[Connect.Ping.INTERVAL_ATTR_NAME]
                                        ?.takeIf { it.isNumber }?.asInt()
                                    val timeout = ping[Connect.Ping.TIMEOUT_ATTR_NAME]
                                        ?.takeIf { it.isNumber }?.asInt()
                                    if (interval != null && timeout != null) {
                                        connectObj.setPing(interval, timeout)
                                    }
                                }
                            }

                            addConnect(connectObj)
                            added = true
                        }
                    }
                    // An empty array is distinct from no value specified.
                    if (!added) setEmptyConnects()
                }
            }
        }
    }

    @JvmStatic
    fun deserializeConferenceModified(conferenceModified: ObjectNode): ConferenceModifiedIQ.Builder {
        return ConferenceModifiedIQ.builder("id").apply {
            deserializeAbstractConferenceModificationToBuilder(conferenceModified, this)
            conferenceModified[Sources.ELEMENT]?.let {
                if (it is ArrayNode) {
                    setSources(deserializeSources(it))
                }
            }
        }
    }
}
