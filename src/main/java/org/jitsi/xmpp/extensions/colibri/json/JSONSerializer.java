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
package org.jitsi.xmpp.extensions.colibri.json;

import java.net.*;
import java.util.*;

import com.fasterxml.jackson.databind.node.*;
import org.jetbrains.annotations.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;

/**
 * Implements (utility) functions to serialize instances of
 * {@link ColibriConferenceIQ} and related classes into JSON instances.
 *
 * @author Lyubomir Marinov
 */
public final class JSONSerializer
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    /**
     * The name of the JSON pair which specifies the value of the
     * <tt>candidateList</tt> property of
     * <tt>IceUdpTransportPacketExtension</tt>.
     */
    static final String CANDIDATE_LIST = CandidatePacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the array of
     * <tt>DtlsFingerprintPacketExtension</tt> child extensions of
     * <tt>IceUdpTransportPacketExtension</tt>.
     */
    static final String FINGERPRINTS = DtlsFingerprintPacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the value of the
     * <tt>parameters</tt> property of <tt>PayloadTypePacketExtension</tt>.
     */
    static final String PARAMETERS = ParameterPacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the value of the
     * <tt>rtcp-fb</tt> property of <tt>ColibriConferenceIQ.Channel</tt>.
     */
    static final String RTCP_FBS = RtcpFbPacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the value of the
     * <tt>sources</tt> property of <tt>ColibriConferenceIQ.Channel</tt>.
     */
    static final String SOURCES = SourcePacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the value of the
     *  <tt>webSockets</tt> property of <tt>WebSocketPacketExtension</tt>.
     */
    static final String WEBSOCKET_LIST = WebSocketPacketExtension.ELEMENT + "s";

    /**
     * The name of the JSON pair which specifies the value of the
     * <tt>namespace</tt> property of <tt>IceUdpTransportPacketExtension</tt>.
     */
    static final String XMLNS = "xmlns";

    /**
     * Serializes the attribute values of an <tt>AbstractPacketExtension</tt>
     * into values of a <tt>ObjectNode</tt>.
     *
     * @param abstractPacketExtension the <tt>AbstractPacketExtension</tt> whose
     * attribute values are to be serialized into values of <tt>jsonObject</tt>
     * @param jsonObject the <tt>ObjectNode</tt> into which the attribute values
     * of <tt>abstractPacketExtension</tt> are to be serialized
     */
    public static void serializeAbstractPacketExtensionAttributes(
            AbstractPacketExtension abstractPacketExtension,
            ObjectNode jsonObject)
    {
        for (String name : abstractPacketExtension.getAttributeNames())
        {
            Object value = abstractPacketExtension.getAttribute(name);

            /*
             * The JSON.simple library that is in use at the time of this
             * writing will fail to encode Enum values as JSON strings so
             * convert the Enum value to a Java String.
             */
            if (value instanceof Enum)
                value = value.toString();

            if (value instanceof String)
                jsonObject.put(name, (String) value);
            else if (value instanceof Boolean)
                jsonObject.put(name, (Boolean) value);
            else if (value instanceof Integer)
                jsonObject.put(name, (Integer) value);
            else if (value instanceof Long)
                jsonObject.put(name, (Long) value);
            else if (value != null)
                jsonObject.put(name, value.toString());
        }
    }

    public static ObjectNode serializeCandidate(
            CandidatePacketExtension candidate)
    {
        ObjectNode candidateJSONObject;

        if (candidate == null)
        {
            candidateJSONObject = null;
        }
        else
        {
            candidateJSONObject = factory.objectNode();
            // attributes
            serializeAbstractPacketExtensionAttributes(
                    candidate,
                    candidateJSONObject);
        }
        return candidateJSONObject;
    }

    public static ArrayNode serializeCandidates(
            Collection<CandidatePacketExtension> candidates)
    {
        ArrayNode candidatesJSONArray;

        if (candidates == null)
        {
            candidatesJSONArray = null;
        }
        else
        {
            candidatesJSONArray = factory.arrayNode();
            for (CandidatePacketExtension candidate : candidates)
                candidatesJSONArray.add(serializeCandidate(candidate));
        }
        return candidatesJSONArray;
    }

    public static ObjectNode serializeFingerprint(
            DtlsFingerprintPacketExtension fingerprint)
    {
        ObjectNode fingerprintJSONObject;

        if (fingerprint == null)
        {
            fingerprintJSONObject = null;
        }
        else
        {
            String theFingerprint = fingerprint.getFingerprint();

            fingerprintJSONObject = factory.objectNode();
            // fingerprint
            if (theFingerprint != null)
            {
                fingerprintJSONObject.put(
                        fingerprint.getElementName(),
                        theFingerprint);
            }
            // attributes
            serializeAbstractPacketExtensionAttributes(
                    fingerprint,
                    fingerprintJSONObject);
            com.fasterxml.jackson.databind.JsonNode cryptex =
                fingerprintJSONObject.get(DtlsFingerprintPacketExtension.CRYPTEX_ATTR_NAME);
            if (cryptex != null && cryptex.isTextual())
            {
                /* Represent cryptex as a boolean. */
                fingerprintJSONObject.put(DtlsFingerprintPacketExtension.CRYPTEX_ATTR_NAME,
                    Boolean.parseBoolean(cryptex.asText()));
            }
        }
        return fingerprintJSONObject;
    }

    public static ArrayNode serializeFingerprints(
            Collection<DtlsFingerprintPacketExtension> fingerprints)
    {
        ArrayNode fingerprintsJSONArray;

        if (fingerprints == null)
        {
            fingerprintsJSONArray = null;
        }
        else
        {
            fingerprintsJSONArray = factory.arrayNode();
            for (DtlsFingerprintPacketExtension fingerprint : fingerprints)
                fingerprintsJSONArray.add(serializeFingerprint(fingerprint));
        }
        return fingerprintsJSONArray;
    }

    public static ObjectNode serializeParameters(
            Collection<ParameterPacketExtension> parameters)
    {
        /*
         * A parameter is a key-value pair and the order of the parameters in a
         * payload-type does not appear to matter so a natural representation of
         * a parameter set is a ObjectNode rather than a ArrayNode.
         */
        ObjectNode parametersJSONObject;

        if (parameters == null)
        {
            parametersJSONObject = null;
        }
        else
        {
            parametersJSONObject = factory.objectNode();
            for (ParameterPacketExtension parameter : parameters)
            {
                String name = parameter.getName();
                String value = parameter.getValue();

                if ((name != null) || (value != null))
                {
                    /* JSON doesn't allow null keys; use the string "null" to match deserializer convention. */
                    parametersJSONObject.put(name != null ? name : "null", value);
                }
            }
        }
        return parametersJSONObject;
    }

    public static ArrayNode serializeRtcpFbs(
            @NotNull Collection<RtcpFbPacketExtension> rtcpFbs)
    {
        ArrayNode rtcpFbsJSON = factory.arrayNode();
        /*
         * A rtcp-fb is an ObjectNode with type / subtype data.
         * "rtcp-fbs": [ {
                "type": "ccm",
                "subtype": "fir"
              }, {
                "type": "nack"
              }, {
                "type": "goog-remb"
              } ]
         */
        for (RtcpFbPacketExtension ext : rtcpFbs)
        {
            String type = ext.getFeedbackType();
            String subtype = ext.getFeedbackSubtype();

            if (type != null)
            {
                ObjectNode rtcpFbJSON = factory.objectNode();
                rtcpFbJSON.put(RtcpFbPacketExtension.TYPE_ATTR_NAME, type);
                if (subtype != null)
                {
                    rtcpFbJSON.put(
                            RtcpFbPacketExtension.SUBTYPE_ATTR_NAME,
                            subtype);
                }
                rtcpFbsJSON.add(rtcpFbJSON);
            }
        }
        return rtcpFbsJSON;
    }

    public static ObjectNode serializePayloadType(
            PayloadTypePacketExtension payloadType)
    {
        ObjectNode payloadTypeJSONObject;

        if (payloadType == null)
        {
            payloadTypeJSONObject = null;
        }
        else
        {
            List<ParameterPacketExtension> parameters
                = payloadType.getParameters();

            payloadTypeJSONObject = factory.objectNode();
            // attributes
            serializeAbstractPacketExtensionAttributes(
                    payloadType,
                    payloadTypeJSONObject);
            // parameters
            if ((parameters != null) && !parameters.isEmpty())
            {
                payloadTypeJSONObject.set(
                        PARAMETERS,
                        serializeParameters(parameters));
            }
            final List<RtcpFbPacketExtension> rtcpFeedbackTypeList =
                    payloadType.getRtcpFeedbackTypeList();
            if ((rtcpFeedbackTypeList != null) &&
                    !rtcpFeedbackTypeList.isEmpty())
            {
                payloadTypeJSONObject.set(
                        RTCP_FBS,
                        serializeRtcpFbs(rtcpFeedbackTypeList));
            }
        }
        return payloadTypeJSONObject;
    }

    public static ArrayNode serializePayloadTypes(
            Collection<PayloadTypePacketExtension> payloadTypes)
    {
        ArrayNode payloadTypesJSONArray;

        if (payloadTypes == null)
        {
            payloadTypesJSONArray = null;
        }
        else
        {
            payloadTypesJSONArray = factory.arrayNode();
            for (PayloadTypePacketExtension payloadType : payloadTypes)
                payloadTypesJSONArray.add(serializePayloadType(payloadType));
        }
        return payloadTypesJSONArray;
    }

    public static ObjectNode serializeRtpHdrExt(
        RTPHdrExtPacketExtension rtpHdrExt)
    {
        ObjectNode rtpHdrExtJSONObject;

        if (rtpHdrExt == null)
        {
            rtpHdrExtJSONObject = null;
        }
        else
        {
            rtpHdrExtJSONObject = factory.objectNode();

            String id = rtpHdrExt.getID();
            if (id != null)
            {
                rtpHdrExtJSONObject.put(
                    RTPHdrExtPacketExtension.ID_ATTR_NAME,
                    Long.valueOf(id));
            }

            URI uri = rtpHdrExt.getURI();
            if (uri != null)
            {
                rtpHdrExtJSONObject.put(
                    RTPHdrExtPacketExtension.URI_ATTR_NAME,
                    uri.toString());
            }

            ContentPacketExtension.SendersEnum senders = rtpHdrExt.getSenders();
            if (senders != null)
            {
                rtpHdrExtJSONObject.put(
                    RTPHdrExtPacketExtension.SENDERS_ATTR_NAME,
                    senders.toString());
            }

            String attributes = rtpHdrExt.getAttributes();
            if (attributes != null)
            {
                rtpHdrExtJSONObject.put(
                    RTPHdrExtPacketExtension.ATTRIBUTES_ATTR_NAME,
                    attributes);
            }
        }
        return rtpHdrExtJSONObject;
    }

    public static ArrayNode serializeRtpHdrExts(
        Collection<RTPHdrExtPacketExtension> rtpHdrExts)
    {
        ArrayNode rtpHdrExtsJSONArray;

        if (rtpHdrExts == null)
        {
            rtpHdrExtsJSONArray = null;
        }
        else
        {
            rtpHdrExtsJSONArray = factory.arrayNode();
            for (RTPHdrExtPacketExtension rtpHdrExt : rtpHdrExts)
                rtpHdrExtsJSONArray.add(serializeRtpHdrExt(rtpHdrExt));
        }
        return rtpHdrExtsJSONArray;
    }

    public static Object serializeSource(SourcePacketExtension source)
    {
        if (source == null)
        {
            return null;
        }

        String name = source.getName();
        String videoType = source.getVideoType();
        String rid = source.getRid();
        List<ParameterPacketExtension> parameters = source.getParameters();

        /* Backward compatibility - sources used to just be their ssrc values. */
        if (name == null && rid == null && parameters.isEmpty())
        {
            return source.getSSRC();
        }

        ObjectNode sourceJSONObject = factory.objectNode();

        sourceJSONObject.put(SourcePacketExtension.SSRC_ATTR_NAME, source.getSSRC());
        if (name != null)
        {
            sourceJSONObject.put(SourcePacketExtension.NAME_ATTR_NAME, name);
        }
        if (videoType != null)
        {
            sourceJSONObject.put(SourcePacketExtension.VIDEO_TYPE_ATTR_NAME, videoType);
        }
        if (rid != null)
        {
            sourceJSONObject.put(SourcePacketExtension.RID_ATTR_NAME, rid);
        }
        if (!parameters.isEmpty())
        {
            sourceJSONObject.set(JSONSerializer.PARAMETERS, serializeParameters(parameters));
        }

        return sourceJSONObject;
    }

    private static Object serializeSourceGroup(
            SourceGroupPacketExtension sourceGroup)
    {
        if (sourceGroup.getSemantics() != null
                && sourceGroup.getSemantics().length() != 0
                && sourceGroup.getSources() != null
                && sourceGroup.getSources().size() != 0)
        {
            ObjectNode sourceGroupJSONObject = factory.objectNode();

            // Add semantics
            sourceGroupJSONObject.put(
                    SourceGroupPacketExtension.SEMANTICS_ATTR_NAME,
                    sourceGroup.getSemantics());

            // Add sources
            ArrayNode ssrcsJSONArray = factory.arrayNode();
            for (SourcePacketExtension source : sourceGroup.getSources())
                ssrcsJSONArray.add(source.getSSRC());

            sourceGroupJSONObject.set(SOURCES, ssrcsJSONArray);

            return sourceGroupJSONObject;
        }
        else
        {
            return null;
        }
    }

    public static ArrayNode serializeSourceGroups(
            Collection<SourceGroupPacketExtension> sourceGroups)
    {
        ArrayNode sourceGroupsJSONArray;

        if (sourceGroups == null || sourceGroups.size() == 0)
        {
            sourceGroupsJSONArray = null;
        }
        else
        {
            sourceGroupsJSONArray = factory.arrayNode();
            for (SourceGroupPacketExtension sourceGroup : sourceGroups)
            {
                Object serialized = serializeSourceGroup(sourceGroup);
                if (serialized instanceof ObjectNode)
                    sourceGroupsJSONArray.add((ObjectNode) serialized);
                else if (serialized == null)
                    sourceGroupsJSONArray.addNull();
            }
        }
        return sourceGroupsJSONArray;
    }

    public static ArrayNode serializeSources(
            Collection<SourcePacketExtension> sources)
    {
        ArrayNode sourcesJSONArray;

        if (sources == null)
        {
            sourcesJSONArray = null;
        }
        else
        {
            sourcesJSONArray = factory.arrayNode();
            for (SourcePacketExtension source : sources)
            {
                Object serialized = serializeSource(source);
                if (serialized instanceof ObjectNode)
                    sourcesJSONArray.add((ObjectNode) serialized);
                else if (serialized instanceof Long)
                    sourcesJSONArray.add((Long) serialized);
                else if (serialized instanceof Integer)
                    sourcesJSONArray.add((Integer) serialized);
                else if (serialized == null)
                    sourcesJSONArray.addNull();
            }
        }
        return sourcesJSONArray;
    }

    public static ObjectNode serializeTransport(
            IceUdpTransportPacketExtension transport)
    {
        ObjectNode jsonObject;

        if (transport == null)
        {
            jsonObject = null;
        }
        else
        {
            String xmlns = transport.getNamespace();
            List<DtlsFingerprintPacketExtension> fingerprints
                = transport.getChildExtensionsOfType(
                        DtlsFingerprintPacketExtension.class);
            List<CandidatePacketExtension> candidateList
                = transport.getCandidateList();
            List<WebSocketPacketExtension> webSocketList
                = transport.getChildExtensionsOfType(
                        WebSocketPacketExtension.class);
            RemoteCandidatePacketExtension remoteCandidate
                = transport.getRemoteCandidate();
            boolean rtcpMux = transport.isRtcpMux();

            jsonObject = factory.objectNode();
            // xmlns
            if (xmlns != null)
                jsonObject.put(XMLNS, xmlns);
            // attributes
            serializeAbstractPacketExtensionAttributes(transport, jsonObject);
            // fingerprints
            if ((fingerprints != null) && !fingerprints.isEmpty())
            {
                jsonObject.set(
                        FINGERPRINTS,
                        serializeFingerprints(fingerprints));
            }
            // candidateList
            if ((candidateList != null) && !candidateList.isEmpty())
            {
                jsonObject.set(
                        CANDIDATE_LIST,
                        serializeCandidates(candidateList));
            }
            // remoteCandidate
            if (remoteCandidate != null)
            {
                jsonObject.set(
                        remoteCandidate.getElementName(),
                        serializeCandidate(remoteCandidate));
            }
            if ( (webSocketList != null) && (!webSocketList.isEmpty()) )
            {
                jsonObject.set(
                        WEBSOCKET_LIST,
                        serializeWebSockets(webSocketList));
            }
            // rtcpMux
            if (rtcpMux)
            {
                jsonObject.put(
                        IceRtcpmuxPacketExtension.ELEMENT,
                        Boolean.TRUE);
            }
        }
        return jsonObject;
    }

    private static String serializeWebSocket(
             WebSocketPacketExtension webSocket)
    {
        if (webSocket.getActive())
        {
            return "active";
        }
        else
        {
            return webSocket.getUrl();
        }
    }

    private static ArrayNode serializeWebSockets(
             List<WebSocketPacketExtension> webSocketList)
    {
        ArrayNode webSocketsJSONArray;

        if (webSocketList == null)
        {
            webSocketsJSONArray = null;
        }
        else
        {
            webSocketsJSONArray = factory.arrayNode();
            for (WebSocketPacketExtension webSocket : webSocketList)
                webSocketsJSONArray.add(serializeWebSocket(webSocket));
        }
        return webSocketsJSONArray;
    }

    /** Prevents the initialization of new <tt>JSONSerializer</tt> instances. */
    private JSONSerializer()
    {
    }
}
