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
                require(it.isTextual) { "Expected string for ${Media.TYPE_ATTR_NAME}, got ${it.nodeType}" }
                setType(MediaType.parseString(it.asText()))
            }

            media[Colibri2JSONSerializer.PAYLOAD_TYPES]?.let { payloadTypes ->
                require(payloadTypes is ArrayNode) { "Expected array for payloadTypes, got ${payloadTypes.nodeType}" }
                JSONDeserializer.deserializePayloadTypes(payloadTypes).forEach { addPayloadType(it) }
            }

            media[Colibri2JSONSerializer.RTP_HEADER_EXTS]?.let { rtpHdrExts ->
                require(rtpHdrExts is ArrayNode) { "Expected array for rtpHdrExts, got ${rtpHdrExts.nodeType}" }
                JSONDeserializer.deserializeHeaderExtensions(rtpHdrExts).forEach { addRtpHdrExt(it) }
            }

            media[ExtmapAllowMixedPacketExtension.ELEMENT]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${ExtmapAllowMixedPacketExtension.ELEMENT}, got ${it.nodeType}"
                }
                setExtmapAllowMixed(ExtmapAllowMixedPacketExtension())
            }
        }.build()
    }

    private fun deserializeSctp(sctp: ObjectNode): Sctp {
        return Sctp.Builder().apply {
            sctp[Sctp.ROLE_ATTR_NAME]?.let {
                require(it.isTextual) { "Expected string for ${Sctp.ROLE_ATTR_NAME}, got ${it.nodeType}" }
                setRole(Sctp.Role.parseString(it.asText()))
            }

            sctp[Sctp.PORT_ATTR_NAME]?.let {
                require(it.isNumber) { "Expected number for ${Sctp.PORT_ATTR_NAME}, got ${it.nodeType}" }
                setPort(it.asInt())
            }
        }.build()
    }

    private fun deserializeTransport(transport: ObjectNode): Transport {
        return Transport.getBuilder().apply {
            transport[Transport.ICE_CONTROLLING_ATTR_NAME]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${Transport.ICE_CONTROLLING_ATTR_NAME}, got ${it.nodeType}"
                }
                setIceControlling(it.asBoolean())
            }

            transport[Transport.USE_UNIQUE_PORT_ATTR_NAME]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${Transport.USE_UNIQUE_PORT_ATTR_NAME}, got ${it.nodeType}"
                }
                setUseUniquePort(it.asBoolean())
            }

            transport[IceUdpTransportPacketExtension.ELEMENT]?.let {
                require(it is ObjectNode) {
                    "Expected object for ${IceUdpTransportPacketExtension.ELEMENT}, got ${it.nodeType}"
                }
                setIceUdpExtension(JSONDeserializer.deserializeTransport(it))
            }

            transport[Sctp.ELEMENT]?.let {
                require(it is ObjectNode) { "Expected object for ${Sctp.ELEMENT}, got ${it.nodeType}" }
                setSctp(deserializeSctp(it))
            }
        }.build()
    }

    private fun deserializeMediaSource(mediaSource: ObjectNode): MediaSource {
        return MediaSource.getBuilder().apply {
            mediaSource[MediaSource.TYPE_ATTR_NAME]?.let {
                require(it.isTextual) { "Expected string for ${MediaSource.TYPE_ATTR_NAME}, got ${it.nodeType}" }
                setType(MediaType.parseString(it.asText()))
            }

            mediaSource[MediaSource.ID_NAME]?.let {
                require(it.isTextual) { "Expected string for ${MediaSource.ID_NAME}, got ${it.nodeType}" }
                setId(it.asText())
            }

            mediaSource[Colibri2JSONSerializer.SOURCES]?.let { sources ->
                require(sources is ArrayNode) { "Expected array for sources, got ${sources.nodeType}" }
                sources.forEach { addSource(JSONDeserializer.deserializeSource(it)) }
            }

            mediaSource[Colibri2JSONSerializer.SOURCE_GROUPS]?.let { sourceGroups ->
                require(sourceGroups is ArrayNode) { "Expected array for sourceGroups, got ${sourceGroups.nodeType}" }
                sourceGroups.forEach { addSsrcGroup(JSONDeserializer.deserializeSourceGroup(it)) }
            }
        }.build()
    }

    private fun deserializeMedias(medias: ArrayNode): Collection<Media> {
        return ArrayList<Media>().apply {
            medias.forEach {
                require(it is ObjectNode) { "Expected object for media element, got ${it.nodeType}" }
                add(deserializeMedia(it))
            }
        }
    }

    private fun deserializeSources(sources: ArrayNode): Sources {
        return Sources.getBuilder().apply {
            sources.forEach {
                require(it is ObjectNode) { "Expected object for source element, got ${it.nodeType}" }
                addMediaSource(deserializeMediaSource(it))
            }
        }.build()
    }

    private fun deserializeAbstractConferenceEntityToBuilder(
        entity: ObjectNode,
        builder: AbstractConferenceEntity.Builder
    ) {
        entity[AbstractConferenceEntity.ID_ATTR_NAME]?.let {
            require(it.isTextual) { "Expected string for ${AbstractConferenceEntity.ID_ATTR_NAME}, got ${it.nodeType}" }
            builder.setId(it.asText())
        }

        entity[AbstractConferenceEntity.CREATE_ATTR_NAME]?.let {
            require(it.isBoolean) {
                "Expected boolean for ${AbstractConferenceEntity.CREATE_ATTR_NAME}, got ${it.nodeType}"
            }
            builder.setCreate(it.asBoolean())
        }

        entity[AbstractConferenceEntity.EXPIRE_ATTR_NAME]?.let {
            require(it.isBoolean) {
                "Expected boolean for ${AbstractConferenceEntity.EXPIRE_ATTR_NAME}, got ${it.nodeType}"
            }
            builder.setExpire(it.asBoolean())
        }

        entity[Colibri2JSONSerializer.MEDIA_LIST]?.let { medias ->
            require(medias is ArrayNode) { "Expected array for mediaList, got ${medias.nodeType}" }
            deserializeMedias(medias).forEach { builder.addMedia(it) }
        }

        entity[Transport.ELEMENT]?.let {
            require(it is ObjectNode) { "Expected object for ${Transport.ELEMENT}, got ${it.nodeType}" }
            builder.setTransport(deserializeTransport(it))
        }

        entity[Sources.ELEMENT]?.let {
            require(it is ArrayNode) { "Expected array for ${Sources.ELEMENT}, got ${it.nodeType}" }
            builder.setSources(deserializeSources(it))
        }
    }

    private fun deserializeInitialLastN(initialLastN: ObjectNode) = InitialLastN(
        initialLastN[InitialLastN.VALUE_ATTR_NAME]?.takeIf { it.isNumber }?.asInt()
            ?: throw IllegalArgumentException("Invalid 'value'")
    )

    private fun deserializeForceMute(forceMute: ObjectNode): ForceMute {
        val audio = forceMute[ForceMute.AUDIO_ATTR_NAME]
        val video = forceMute[ForceMute.VIDEO_ATTR_NAME]

        if (audio != null) {
            require(audio.isBoolean) { "Expected boolean for ${ForceMute.AUDIO_ATTR_NAME}, got ${audio.nodeType}" }
        }
        if (video != null) {
            require(video.isBoolean) { "Expected boolean for ${ForceMute.VIDEO_ATTR_NAME}, got ${video.nodeType}" }
        }

        return ForceMute(
            audio?.asBoolean() ?: ForceMute.AUDIO_DEFAULT,
            video?.asBoolean() ?: ForceMute.VIDEO_DEFAULT
        )
    }

    private fun deserializeEndpoint(endpoint: ObjectNode): Colibri2Endpoint {
        return Colibri2Endpoint.getBuilder().apply {
            deserializeAbstractConferenceEntityToBuilder(endpoint, this)

            endpoint[Colibri2Endpoint.STATS_ID_ATTR_NAME]?.let {
                require(it.isTextual) {
                    "Expected string for ${Colibri2Endpoint.STATS_ID_ATTR_NAME}, got ${it.nodeType}"
                }
                setStatsId(it.asText())
            }

            endpoint[Colibri2Endpoint.MUC_ROLE_ATTR_NAME]?.let {
                require(it.isTextual) {
                    "Expected string for ${Colibri2Endpoint.MUC_ROLE_ATTR_NAME}, got ${it.nodeType}"
                }
                setMucRole(MUCRole.fromString(it.asText()))
            }

            endpoint[ForceMute.ELEMENT]?.let {
                require(it is ObjectNode) { "Expected object for ${ForceMute.ELEMENT}, got ${it.nodeType}" }
                setForceMute(deserializeForceMute(it))
            }

            endpoint[InitialLastN.ELEMENT]?.let {
                require(it is ObjectNode) { "Expected object for ${InitialLastN.ELEMENT}, got ${it.nodeType}" }
                setInitialLastN(deserializeInitialLastN(it))
            }

            endpoint[Colibri2JSONSerializer.CAPABILITIES_LIST]?.let { capabilities ->
                require(capabilities is ArrayNode) {
                    "Expected array for capabilitiesList, got ${capabilities.nodeType}"
                }
                capabilities.forEach {
                    require(it.isTextual) { "Expected string capability, got ${it.nodeType}" }
                    addCapability(it.asText())
                }
            }
        }.build()
    }

    private fun deserializeRelay(relay: ObjectNode): Colibri2Relay {
        return Colibri2Relay.getBuilder().apply {
            deserializeAbstractConferenceEntityToBuilder(relay, this)

            relay[Colibri2Relay.MESH_ID_ATTR_NAME]?.let {
                require(it.isTextual) { "Expected string for ${Colibri2Relay.MESH_ID_ATTR_NAME}, got ${it.nodeType}" }
                setMeshId(it.asText())
            }

            relay[Colibri2JSONSerializer.ENDPOINTS]?.let { endpoints ->
                require(endpoints is ArrayNode) { "Expected array for endpoints, got ${endpoints.nodeType}" }
                setEndpoints(
                    Endpoints.getBuilder().apply {
                        deserializeEndpoints(endpoints).forEach { addEndpoint(it) }
                    }.build()
                )
            }
        }.build()
    }

    private fun deserializeEndpoints(endpoints: ArrayNode): Collection<Colibri2Endpoint> {
        return ArrayList<Colibri2Endpoint>().apply {
            endpoints.forEach {
                require(it is ObjectNode) { "Expected object for endpoint element, got ${it.nodeType}" }
                add(deserializeEndpoint(it))
            }
        }
    }

    private fun deserializeRelays(relays: ArrayNode): Collection<Colibri2Relay> {
        return ArrayList<Colibri2Relay>().apply {
            relays.forEach {
                require(it is ObjectNode) { "Expected object for relay element, got ${it.nodeType}" }
                add(deserializeRelay(it))
            }
        }
    }

    private fun deserializeAbstractConferenceModificationToBuilder(
        modification: ObjectNode,
        builder: AbstractConferenceModificationIQ.Builder<*>
    ) {
        modification[Colibri2JSONSerializer.ENDPOINTS]?.let { endpoints ->
            require(endpoints is ArrayNode) { "Expected array for endpoints, got ${endpoints.nodeType}" }
            deserializeEndpoints(endpoints).forEach { builder.addConferenceEntity(it) }
        }

        modification[Colibri2JSONSerializer.RELAYS]?.let { relays ->
            require(relays is ArrayNode) { "Expected array for relays, got ${relays.nodeType}" }
            deserializeRelays(relays).forEach { builder.addConferenceEntity(it) }
        }
    }

    @JvmStatic
    fun deserializeConferenceModify(conferenceModify: ObjectNode): ConferenceModifyIQ.Builder {
        return ConferenceModifyIQ.builder("id").apply {
            deserializeAbstractConferenceModificationToBuilder(conferenceModify, this)

            conferenceModify[ConferenceModifyIQ.MEETING_ID_ATTR_NAME]?.let {
                require(it.isTextual) {
                    "Expected string for ${ConferenceModifyIQ.MEETING_ID_ATTR_NAME}, got ${it.nodeType}"
                }
                setMeetingId(it.asText())
            }

            conferenceModify[ConferenceModifyIQ.NAME_ATTR_NAME]?.let {
                require(it.isTextual) { "Expected string for ${ConferenceModifyIQ.NAME_ATTR_NAME}, got ${it.nodeType}" }
                setConferenceName(it.asText())
            }

            conferenceModify[ConferenceModifyIQ.CREATE_ATTR_NAME]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${ConferenceModifyIQ.CREATE_ATTR_NAME}, got ${it.nodeType}"
                }
                setCreate(it.asBoolean())
            }

            conferenceModify[ConferenceModifyIQ.EXPIRE_ATTR_NAME]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${ConferenceModifyIQ.EXPIRE_ATTR_NAME}, got ${it.nodeType}"
                }
                setExpire(it.asBoolean())
            }

            conferenceModify[ConferenceModifyIQ.RTCSTATS_ENABLED_ATTR_NAME]?.let {
                require(it.isBoolean) {
                    "Expected boolean for ${ConferenceModifyIQ.RTCSTATS_ENABLED_ATTR_NAME}, got ${it.nodeType}"
                }
                setRtcstatsEnabled(it.asBoolean())
            }

            conferenceModify[Connects.ELEMENT]?.let {
                require(it is ArrayNode) { "Expected array for ${Connects.ELEMENT}, got ${it.nodeType}" }
                var added = false
                it.forEach { connect ->
                    require(connect is ObjectNode) { "Expected object for connect element, got ${connect.nodeType}" }
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
                        require(headers is ObjectNode) { "Expected object for headers, got ${headers.nodeType}" }
                        headers.properties().forEach { e ->
                            require(e.value.isTextual) {
                                "Expected string header value for ${e.key}, got ${e.value.nodeType}"
                            }
                            connectObj.addHttpHeader(e.key, e.value.asText())
                        }
                    }

                    // Deserialize ping
                    connect["ping"]?.let { ping ->
                        require(ping is ObjectNode) { "Expected object for ping, got ${ping.nodeType}" }
                        val interval = ping[Connect.Ping.INTERVAL_ATTR_NAME]
                            ?.also {
                                require(it.isNumber) {
                                    "Expected number for ${Connect.Ping.INTERVAL_ATTR_NAME}, got ${it.nodeType}"
                                }
                            }
                            ?.asInt()
                        val timeout = ping[Connect.Ping.TIMEOUT_ATTR_NAME]
                            ?.also {
                                require(it.isNumber) {
                                    "Expected number for ${Connect.Ping.TIMEOUT_ATTR_NAME}, got ${it.nodeType}"
                                }
                            }
                            ?.asInt()
                        if (interval != null && timeout != null) {
                            connectObj.setPing(interval, timeout)
                        }
                    }

                    addConnect(connectObj)
                    added = true
                }
                // An empty array is distinct from no value specified.
                if (!added) setEmptyConnects()
            }
        }
    }

    @JvmStatic
    fun deserializeConferenceModified(conferenceModified: ObjectNode): ConferenceModifiedIQ.Builder {
        return ConferenceModifiedIQ.builder("id").apply {
            deserializeAbstractConferenceModificationToBuilder(conferenceModified, this)
            conferenceModified[Sources.ELEMENT]?.let {
                require(it is ArrayNode) { "Expected array for ${Sources.ELEMENT}, got ${it.nodeType}" }
                setSources(deserializeSources(it))
            }
        }
    }
}
