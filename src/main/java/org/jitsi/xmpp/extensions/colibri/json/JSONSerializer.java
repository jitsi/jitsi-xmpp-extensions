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

import org.jetbrains.annotations.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.json.simple.*;

/**
 * Implements (utility) functions to serialize instances of
 * {@link ColibriConferenceIQ} and related classes into JSON instances.
 *
 * @author Lyubomir Marinov
 */
@SuppressWarnings("unchecked")
public final class JSONSerializer
{
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
     * into values of a <tt>JSONObject</tt>.
     *
     * @param abstractPacketExtension the <tt>AbstractPacketExtension</tt> whose
     * attribute values are to be serialized into values of <tt>jsonObject</tt>
     * @param jsonObject the <tt>JSONObject</tt> into which the attribute values
     * of <tt>abstractPacketExtension</tt> are to be serialized
     */
    public static void serializeAbstractPacketExtensionAttributes(
            AbstractPacketExtension abstractPacketExtension,
            JSONObject jsonObject)
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

            jsonObject.put(name, value);
        }
    }

    public static JSONObject serializeCandidate(
            CandidatePacketExtension candidate)
    {
        JSONObject candidateJSONObject;

        if (candidate == null)
        {
            candidateJSONObject = null;
        }
        else
        {
            candidateJSONObject = new JSONObject();
            // attributes
            serializeAbstractPacketExtensionAttributes(
                    candidate,
                    candidateJSONObject);
        }
        return candidateJSONObject;
    }

    public static JSONArray serializeCandidates(
            Collection<CandidatePacketExtension> candidates)
    {
        JSONArray candidatesJSONArray;

        if (candidates == null)
        {
            candidatesJSONArray = null;
        }
        else
        {
            candidatesJSONArray = new JSONArray();
            for (CandidatePacketExtension candidate : candidates)
                candidatesJSONArray.add(serializeCandidate(candidate));
        }
        return candidatesJSONArray;
    }

    public static JSONObject serializeFingerprint(
            DtlsFingerprintPacketExtension fingerprint)
    {
        JSONObject fingerprintJSONObject;

        if (fingerprint == null)
        {
            fingerprintJSONObject = null;
        }
        else
        {
            String theFingerprint = fingerprint.getFingerprint();

            fingerprintJSONObject = new JSONObject();
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
            Object cryptex = fingerprintJSONObject.get(DtlsFingerprintPacketExtension.CRYPTEX_ATTR_NAME);
            if (cryptex instanceof String)
            {
                /* Represent cryptex as a boolean. */
                fingerprintJSONObject.put(DtlsFingerprintPacketExtension.CRYPTEX_ATTR_NAME,
                    Boolean.parseBoolean((String)cryptex));
            }
        }
        return fingerprintJSONObject;
    }

    public static JSONArray serializeFingerprints(
            Collection<DtlsFingerprintPacketExtension> fingerprints)
    {
        JSONArray fingerprintsJSONArray;

        if (fingerprints == null)
        {
            fingerprintsJSONArray = null;
        }
        else
        {
            fingerprintsJSONArray = new JSONArray();
            for (DtlsFingerprintPacketExtension fingerprint : fingerprints)
                fingerprintsJSONArray.add(serializeFingerprint(fingerprint));
        }
        return fingerprintsJSONArray;
    }

    public static JSONObject serializeParameters(
            Collection<ParameterPacketExtension> parameters)
    {
        /*
         * A parameter is a key-value pair and the order of the parameters in a
         * payload-type does not appear to matter so a natural representation of
         * a parameter set is a JSONObject rather than a JSONArray.
         */
        JSONObject parametersJSONObject;

        if (parameters == null)
        {
            parametersJSONObject = null;
        }
        else
        {
            parametersJSONObject = new JSONObject();
            for (ParameterPacketExtension parameter : parameters)
            {
                String name = parameter.getName();
                String value = parameter.getValue();

                if ((name != null) || (value != null))
                    parametersJSONObject.put(name, value);
            }
        }
        return parametersJSONObject;
    }

    public static JSONArray serializeRtcpFbs(
            @NotNull Collection<RtcpFbPacketExtension> rtcpFbs)
    {
        JSONArray rtcpFbsJSON = new JSONArray();
        /*
         * A rtcp-fb is an JSONObject with type / subtype data.
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
                JSONObject rtcpFbJSON = new JSONObject();
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

    public static JSONObject serializePayloadType(
            PayloadTypePacketExtension payloadType)
    {
        JSONObject payloadTypeJSONObject;

        if (payloadType == null)
        {
            payloadTypeJSONObject = null;
        }
        else
        {
            List<ParameterPacketExtension> parameters
                = payloadType.getParameters();

            payloadTypeJSONObject = new JSONObject();
            // attributes
            serializeAbstractPacketExtensionAttributes(
                    payloadType,
                    payloadTypeJSONObject);
            // parameters
            if ((parameters != null) && !parameters.isEmpty())
            {
                payloadTypeJSONObject.put(
                        PARAMETERS,
                        serializeParameters(parameters));
            }
            final List<RtcpFbPacketExtension> rtcpFeedbackTypeList =
                    payloadType.getRtcpFeedbackTypeList();
            if ((rtcpFeedbackTypeList != null) &&
                    !rtcpFeedbackTypeList.isEmpty())
            {
                payloadTypeJSONObject.put(
                        RTCP_FBS,
                        serializeRtcpFbs(rtcpFeedbackTypeList));
            }
        }
        return payloadTypeJSONObject;
    }

    public static JSONArray serializePayloadTypes(
            Collection<PayloadTypePacketExtension> payloadTypes)
    {
        JSONArray payloadTypesJSONArray;

        if (payloadTypes == null)
        {
            payloadTypesJSONArray = null;
        }
        else
        {
            payloadTypesJSONArray = new JSONArray();
            for (PayloadTypePacketExtension payloadType : payloadTypes)
                payloadTypesJSONArray.add(serializePayloadType(payloadType));
        }
        return payloadTypesJSONArray;
    }

    public static JSONObject serializeRtpHdrExt(
        RTPHdrExtPacketExtension rtpHdrExt)
    {
        JSONObject rtpHdrExtJSONObject;

        if (rtpHdrExt == null)
        {
            rtpHdrExtJSONObject = null;
        }
        else
        {
            rtpHdrExtJSONObject = new JSONObject();

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

    public static JSONArray serializeRtpHdrExts(
        Collection<RTPHdrExtPacketExtension> rtpHdrExts)
    {
        JSONArray rtpHdrExtsJSONArray;

        if (rtpHdrExts == null)
        {
            rtpHdrExtsJSONArray = null;
        }
        else
        {
            rtpHdrExtsJSONArray = new JSONArray();
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

        JSONObject sourceJSONObject = new JSONObject();

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
            sourceJSONObject.put(JSONSerializer.PARAMETERS, serializeParameters(parameters));
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
            JSONObject sourceGroupJSONObject = new JSONObject();

            // Add semantics
            sourceGroupJSONObject.put(
                    SourceGroupPacketExtension.SEMANTICS_ATTR_NAME,
                    JSONValue.escape(sourceGroup.getSemantics()));

            // Add sources
            JSONArray ssrcsJSONArray = new JSONArray();
            for (SourcePacketExtension source : sourceGroup.getSources())
                ssrcsJSONArray.add(source.getSSRC());

            sourceGroupJSONObject.put(SOURCES, ssrcsJSONArray);

            return sourceGroupJSONObject;
        }
        else
        {
            return null;
        }
    }

    public static JSONArray serializeSourceGroups(
            Collection<SourceGroupPacketExtension> sourceGroups)
    {
        JSONArray sourceGroupsJSONArray;

        if (sourceGroups == null || sourceGroups.size() == 0)
        {
            sourceGroupsJSONArray = null;
        }
        else
        {
            sourceGroupsJSONArray = new JSONArray();
            for (SourceGroupPacketExtension sourceGroup : sourceGroups)
                sourceGroupsJSONArray.add(serializeSourceGroup(sourceGroup));
        }
        return sourceGroupsJSONArray;
    }

    public static JSONArray serializeSources(
            Collection<SourcePacketExtension> sources)
    {
        JSONArray sourcesJSONArray;

        if (sources == null)
        {
            sourcesJSONArray = null;
        }
        else
        {
            sourcesJSONArray = new JSONArray();
            for (SourcePacketExtension source : sources)
                sourcesJSONArray.add(serializeSource(source));
        }
        return sourcesJSONArray;
    }

    public static JSONObject serializeTransport(
            IceUdpTransportPacketExtension transport)
    {
        JSONObject jsonObject;

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

            jsonObject = new JSONObject();
            // xmlns
            if (xmlns != null)
                jsonObject.put(XMLNS, xmlns);
            // attributes
            serializeAbstractPacketExtensionAttributes(transport, jsonObject);
            // fingerprints
            if ((fingerprints != null) && !fingerprints.isEmpty())
            {
                jsonObject.put(
                        FINGERPRINTS,
                        serializeFingerprints(fingerprints));
            }
            // candidateList
            if ((candidateList != null) && !candidateList.isEmpty())
            {
                jsonObject.put(
                        CANDIDATE_LIST,
                        serializeCandidates(candidateList));
            }
            // remoteCandidate
            if (remoteCandidate != null)
            {
                jsonObject.put(
                        remoteCandidate.getElementName(),
                        serializeCandidate(remoteCandidate));
            }
            if ( (webSocketList != null) && (!webSocketList.isEmpty()) )
            {
                jsonObject.put(
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

    private static JSONArray serializeWebSockets(
             List<WebSocketPacketExtension> webSocketList)
    {
        JSONArray webSocketsJSONArray;

        if (webSocketList == null)
        {
            webSocketsJSONArray = null;
        }
        else
        {
            webSocketsJSONArray = new JSONArray();
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
