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

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.json.simple.*;

/**
 * Implements (utility) functions to deserialize instances of
 * {@link ColibriConferenceIQ} and related classes from JSON instances.
 *
 * @author Lyubomir Marinov
 */
@SuppressWarnings("unchecked")
public final class JSONDeserializer
{
    /**
     * Deserializes the values of a <tt>JSONObject</tt> which are neither
     * <tt>JSONArray</tt>, nor <tt>JSONObject</tt> into attribute values
     * a <tt>AbstractPacketExtension</tt>.
     *
     * @param jsonObject the <tt>JSONObject</tt> whose values which are neither
     * <tt>JSONArray</tt>, nor <tt>JSONObject</tt> to deserialize into attribute
     * values of <tt>abstractPacketExtension</tt>
     * @param abstractPacketExtension the <tt>AbstractPacketExtension</tt> in
     * the attributes of which the values of <tt>jsonObject</tt> which are
     * neither <tt>JSONObject</tt>, nor <tt>JSONArray</tt> are to be
     * deserialized
     */
    public static void deserializeAbstractPacketExtensionAttributes(
            JSONObject jsonObject,
            AbstractPacketExtension abstractPacketExtension)
    {

        for (Map.Entry<Object, Object> e : (Iterable<Map.Entry<Object,
            Object>>) jsonObject
            .entrySet())
        {
            Object key = e.getKey();

            if (key != null)
            {
                String name = key.toString();

                if (name != null)
                {
                    Object value = e.getValue();

                    if (!(value instanceof JSONObject)
                        && !(value instanceof JSONArray))
                    {
                        abstractPacketExtension.setAttribute(name, value);
                    }
                }
            }
        }
    }

    public static <T extends CandidatePacketExtension> T deserializeCandidate(
            JSONObject candidate,
            Class<T> candidateIQClass,
            IceUdpTransportPacketExtension transportIQ)
    {
        T candidateIQ;

        if (candidate == null)
        {
            candidateIQ = null;
        }
        else
        {
            try
            {
                candidateIQ = candidateIQClass.getConstructor().newInstance();
            }
            catch (IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException iae)
            {
                throw new UndeclaredThrowableException(iae);
            }
            // attributes
            deserializeAbstractPacketExtensionAttributes(
                    candidate,
                    candidateIQ);

            transportIQ.addChildExtension(candidateIQ);
        }
        return candidateIQ;
    }

    public static void deserializeCandidates(
            JSONArray candidates,
            IceUdpTransportPacketExtension transportIQ)
    {
        if ((candidates != null) && !candidates.isEmpty())
        {
            for (Object candidate : candidates)
            {
                deserializeCandidate(
                        (JSONObject) candidate,
                        IceCandidatePacketExtension.class,
                        transportIQ);
            }
        }
    }

    public static void deserializeWebsocket(
        String webSocketUrl,
        IceUdpTransportPacketExtension transportIQ)
    {
        WebSocketPacketExtension webSocketIQ;

        if (webSocketUrl == null)
        {
            webSocketIQ = null;
        }
        else
        {
            webSocketIQ = new WebSocketPacketExtension();

            if (webSocketUrl.equals("active"))
            {
                webSocketIQ.setActive(true);
            }
            else
            {
                webSocketIQ.setUrl(webSocketUrl);
            }

            transportIQ.addChildExtension(webSocketIQ);
        }
    }

    public static void deserializeWebsockets(
        JSONArray webSockets,
        IceUdpTransportPacketExtension transportIQ)
    {
        if ((webSockets != null) && !webSockets.isEmpty())
        {
            for (Object webSocket : webSockets)
            {
                deserializeWebsocket(
                    (String)webSocket,
                    transportIQ);
            }
        }
    }

    private static Boolean objectToBoolean(Object o)
    {
        if (o instanceof Boolean)
        {
            return (Boolean) o;
        }
        else
        {
            return Boolean.valueOf(o.toString());
        }
    }

    public static DtlsFingerprintPacketExtension deserializeFingerprint(
            JSONObject fingerprint,
            IceUdpTransportPacketExtension transportIQ)
    {
        DtlsFingerprintPacketExtension fingerprintIQ;

        if (fingerprint == null)
        {
            fingerprintIQ = null;
        }
        else
        {
            Object theFingerprint
                = fingerprint.get(DtlsFingerprintPacketExtension.ELEMENT);

            fingerprintIQ = new DtlsFingerprintPacketExtension();
            // fingerprint
            if (theFingerprint != null)
            {
                fingerprintIQ.setFingerprint(theFingerprint.toString());
            }
            // attributes
            deserializeAbstractPacketExtensionAttributes(
                    fingerprint,
                    fingerprintIQ);
            /*
             * XXX The fingerprint is stored as the text of the
             * DtlsFingerprintPacketExtension instance. But it is a Java String
             * and, consequently, the
             * deserializeAbstractPacketExtensionAttributes method will
             * deserialize it into an attribute of the
             * DtlsFingerprintPacketExtension instance.
             */
            fingerprintIQ.removeAttribute(
                    DtlsFingerprintPacketExtension.ELEMENT);

            transportIQ.addChildExtension(fingerprintIQ);
        }
        return fingerprintIQ;
    }

    public static void deserializeFingerprints(
            JSONArray fingerprints,
            IceUdpTransportPacketExtension transportIQ)
    {
        if ((fingerprints != null) && !fingerprints.isEmpty())
        {
            for (Object fingerprint : fingerprints)
            {
                deserializeFingerprint((JSONObject) fingerprint, transportIQ);
            }
        }
    }

    public static void deserializeParameters(
            JSONObject parameters,
            PayloadTypePacketExtension payloadTypeIQ)
    {
        if (parameters != null)
        {

            for (Map.Entry<Object, Object> e
                        : (Iterable<Map.Entry<Object, Object>>) parameters
                                .entrySet())
            {
                Object name = e.getKey();
                Object value = e.getValue();

                /* Some payload formats - notably red - have a parameter without a name, but
                 * JSON doesn't allow null as a key name */
                if (name instanceof String && name.equals("null"))
                {
                    name = null;
                }

                if ((name != null) || (value != null))
                {
                    payloadTypeIQ.addParameter(
                            new ParameterPacketExtension(
                                    Objects.toString(name, null),
                                    Objects.toString(value, null)));
                }
            }
        }
    }

    public static void deserializeRtcpFbs(
            JSONArray rtcpFbs,
            PayloadTypePacketExtension payloadTypeIQ)
    {
        if (rtcpFbs != null)
        {
            for (Object iter : rtcpFbs)
            {
                JSONObject rtcpFb = (JSONObject) iter;
                String type = (String)
                        rtcpFb.get(RtcpFbPacketExtension.TYPE_ATTR_NAME);
                String subtype = (String)
                        rtcpFb.get(RtcpFbPacketExtension.SUBTYPE_ATTR_NAME);
                if (type != null)
                {
                    RtcpFbPacketExtension ext = new RtcpFbPacketExtension();
                    ext.setFeedbackType(type);
                    if (subtype != null)
                    {
                        ext.setFeedbackSubtype(subtype);
                    }
                    payloadTypeIQ.addRtcpFeedbackType(ext);
                }
            }
        }
    }

    public static RTPHdrExtPacketExtension deserializeHeaderExtension(
            JSONObject headerExtension)
    {
        RTPHdrExtPacketExtension headerExtensionIQ;
        if (headerExtension == null)
        {
            headerExtensionIQ = null;
        }
        else
        {
            Long id = (Long)headerExtension.get(RTPHdrExtPacketExtension.ID_ATTR_NAME);
            String uriString = (String)headerExtension.get(RTPHdrExtPacketExtension.URI_ATTR_NAME);
            URI uri;
            try
            {
                uri = new URI(uriString);
            }
            catch (URISyntaxException e)
            {
                uri = null;
            }
            if (uri != null)
            {
                headerExtensionIQ = new RTPHdrExtPacketExtension();
                headerExtensionIQ.setID(String.valueOf(id));
                headerExtensionIQ.setURI(uri);
            }
            else
            {
                headerExtensionIQ = null;
            }
        }
        return headerExtensionIQ;
    }

    public static Collection<RTPHdrExtPacketExtension> deserializeHeaderExtensions(
        JSONArray headerExtensions)
    {
        Collection<RTPHdrExtPacketExtension> headerExtensionIQs = new ArrayList<>();
        for (Object headerExtension : headerExtensions)
        {
            RTPHdrExtPacketExtension headerExtensionIQ = deserializeHeaderExtension((JSONObject) headerExtension);
            if (headerExtensionIQ != null)
            {
                headerExtensionIQs.add(headerExtensionIQ);
            }
        }
        return headerExtensionIQs;
    }

    public static PayloadTypePacketExtension deserializePayloadType(
            JSONObject payloadType)
    {
        PayloadTypePacketExtension payloadTypeIQ;

        if (payloadType == null)
        {
            payloadTypeIQ = null;
        }
        else
        {
            Object parameters = payloadType.get(JSONSerializer.PARAMETERS);

            payloadTypeIQ = new PayloadTypePacketExtension();
            // attributes
            deserializeAbstractPacketExtensionAttributes(
                    payloadType,
                    payloadTypeIQ);
            // parameters
            if (parameters != null)
            {
                deserializeParameters((JSONObject) parameters, payloadTypeIQ);
            }

            Object rtcpFbs = payloadType.get(JSONSerializer.RTCP_FBS);

            if (rtcpFbs instanceof JSONArray)
            {
                deserializeRtcpFbs((JSONArray) rtcpFbs, payloadTypeIQ);
            }
        }
        return payloadTypeIQ;
    }

    public static Collection<PayloadTypePacketExtension> deserializePayloadTypes(
            JSONArray payloadTypes)
    {
        Collection<PayloadTypePacketExtension> payloadTypeIQs = new ArrayList<>();
        for (Object payloadType : payloadTypes)
        {
            payloadTypeIQs.add(deserializePayloadType((JSONObject) payloadType));
        }
        return payloadTypeIQs;
    }


    public static SourcePacketExtension deserializeSource(Object source)
    {
        SourcePacketExtension sourceIQ;

        if (source == null)
        {
            sourceIQ = null;
        }
        else if (source instanceof Number || source instanceof String)
        {
            long ssrc;
            try
            {
                ssrc = deserializeSSRC(source);
            }
            catch (NumberFormatException nfe)
            {
                return null;
            }
            sourceIQ = new SourcePacketExtension();
            sourceIQ.setSSRC(ssrc);
        }
        else if (source instanceof JSONObject)
        {
            JSONObject sourceJSONObject = (JSONObject) source;
            Object ssrcAttr = sourceJSONObject.get(SourcePacketExtension.SSRC_ATTR_NAME);
            long ssrc;

            try
            {
                ssrc = deserializeSSRC(ssrcAttr);
            }
            catch (NumberFormatException nfe)
            {
                return null;
            }
            sourceIQ = new SourcePacketExtension();
            sourceIQ.setSSRC(ssrc);

            Object name = sourceJSONObject.get(SourcePacketExtension.NAME_ATTR_NAME);
            Object videoType = sourceJSONObject.get(SourcePacketExtension.VIDEO_TYPE_ATTR_NAME);
            Object rid = sourceJSONObject.get(SourcePacketExtension.RID_ATTR_NAME);
            Object parameters = sourceJSONObject.get(JSONSerializer.PARAMETERS);
            if (name instanceof String)
            {
                sourceIQ.setName((String)name);
            }
            if (videoType instanceof String)
            {
                sourceIQ.setVideoType((String) videoType);
            }
            if (rid instanceof String)
            {
                sourceIQ.setRid((String)rid);
            }
            if (parameters instanceof JSONObject)
            {
                for (Map.Entry<Object, Object> e
                    : (Iterable<Map.Entry<Object, Object>>)((JSONObject)parameters).entrySet())
                {
                    Object paramName = e.getKey();
                    Object paramValue = e.getValue();

                    if ((paramName != null) || (paramValue != null))
                    {
                        sourceIQ.addParameter(
                            new ParameterPacketExtension(
                                Objects.toString(paramName, null),
                                Objects.toString(paramValue, null)));
                    }
                }
            }
        }
        else
        {
            sourceIQ = null;
        }
        return sourceIQ;
    }

    public static SourceGroupPacketExtension deserializeSourceGroup(
            Object sourceGroup)
    {
        SourceGroupPacketExtension sourceGroupIQ;

        if (!(sourceGroup instanceof JSONObject))
        {
            sourceGroupIQ = null;
        }
        else
        {
            JSONObject sourceGroupJSONObject = (JSONObject) sourceGroup;

            // semantics
            Object semantics = sourceGroupJSONObject
                    .get(SourceGroupPacketExtension.SEMANTICS_ATTR_NAME);

            if (semantics instanceof String && ((String) semantics).length() != 0)
            {
                // ssrcs
                Object sourcesObject = sourceGroupJSONObject
                        .get(JSONSerializer.SOURCES);

                if (sourcesObject instanceof JSONArray && ((JSONArray) sourcesObject).size() != 0)
                {
                    JSONArray sourcesJSONArray = (JSONArray) sourcesObject;
                    List<SourcePacketExtension> sourcePacketExtensions
                        = new ArrayList<>();

                    for (Object source : sourcesJSONArray)
                    {
                        SourcePacketExtension sourcePacketExtension
                                = deserializeSource(source);

                        if (sourcePacketExtension != null)
                        {
                            sourcePacketExtensions.add(sourcePacketExtension);
                        }
                    }

                    sourceGroupIQ = new SourceGroupPacketExtension();
                    sourceGroupIQ.setSemantics(Objects.toString(semantics));
                    sourceGroupIQ.addSources(sourcePacketExtensions);
                }
                else
                {
                    sourceGroupIQ = null;
                }
            }
            else
            {
                sourceGroupIQ = null;
            }
        }
        return sourceGroupIQ;
    }

    public static int deserializeSSRC(Object o)
        throws NumberFormatException
    {
        int i = 0;

        if (o != null)
        {
            if (o instanceof Number)
            {
                i = ((Number) o).intValue();
            }
            else
            {
                String s = o.toString();

                if (s.startsWith("-"))
                {
                    i = Integer.parseInt(s);
                }
                else
                {
                    i = (int) Long.parseLong(s);
                }
            }
        }
        return i;
    }

    public static IceUdpTransportPacketExtension deserializeTransport(
            JSONObject transport)
    {
        IceUdpTransportPacketExtension transportIQ;

        if (transport == null)
        {
            transportIQ = null;
        }
        else
        {
            Object xmlns = transport.get(JSONSerializer.XMLNS);
            Object fingerprints = transport.get(JSONSerializer.FINGERPRINTS);
            Object candidateList = transport.get(JSONSerializer.CANDIDATE_LIST);
            Object webSocketList = transport.get(JSONSerializer.WEBSOCKET_LIST);
            Object remoteCandidate
                = transport.get(RemoteCandidatePacketExtension.ELEMENT);
            Object rtcpMux = transport.get(IceRtcpmuxPacketExtension.ELEMENT);

            if (IceUdpTransportPacketExtension.NAMESPACE.equals(xmlns))
            {
                transportIQ = new IceUdpTransportPacketExtension();
            }
            else
            {
                transportIQ = null;
            }

            if (transportIQ != null)
            {
                // attributes
                deserializeAbstractPacketExtensionAttributes(
                        transport,
                        transportIQ);
                // We don't want to put the xmlns as an attribute, it comes automatically from the namespace.
                transportIQ.removeAttribute(JSONSerializer.XMLNS);
                // deserializeAbstractPacketExtensionAttributes picks up rtcp-mux as an attribute, but
                // it's an element
                transportIQ.removeAttribute(IceRtcpmuxPacketExtension.ELEMENT);
                // fingerprints
                if (fingerprints != null)
                {
                    deserializeFingerprints(
                            (JSONArray) fingerprints,
                            transportIQ);
                }
                // candidateList
                if (candidateList != null)
                {
                    deserializeCandidates(
                            (JSONArray) candidateList,
                            transportIQ);
                }
                if (webSocketList != null)
                {
                    deserializeWebsockets(
                        (JSONArray) webSocketList,
                        transportIQ);
                }
                // remoteCandidate
                if (remoteCandidate != null)
                {
                    deserializeCandidate(
                            (JSONObject) remoteCandidate,
                            RemoteCandidatePacketExtension.class,
                            transportIQ);
                }
                // rtcpMux
                if (rtcpMux != null && objectToBoolean(rtcpMux))
                {
                    transportIQ.addChildExtension(
                        new IceRtcpmuxPacketExtension());
                }
            }
        }
        return transportIQ;
    }

    /**
     * Prevents the initialization of new <tt>JSONDeserializer</tt> instances.
     */
    private JSONDeserializer()
    {
    }
}
