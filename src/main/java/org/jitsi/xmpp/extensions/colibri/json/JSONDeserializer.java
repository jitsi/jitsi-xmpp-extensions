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

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;

/**
 * Implements (utility) functions to deserialize instances of
 * {@link ColibriConferenceIQ} and related classes from JSON instances.
 *
 * @author Lyubomir Marinov
 */
public final class JSONDeserializer
{
    /**
     * Deserializes the values of a <tt>ObjectNode</tt> which are neither
     * <tt>ArrayNode</tt>, nor <tt>ObjectNode</tt> into attribute values
     * a <tt>AbstractPacketExtension</tt>.
     *
     * @param jsonObject the <tt>ObjectNode</tt> whose values which are neither
     * <tt>ArrayNode</tt>, nor <tt>ObjectNode</tt> to deserialize into attribute
     * values of <tt>abstractPacketExtension</tt>
     * @param abstractPacketExtension the <tt>AbstractPacketExtension</tt> in
     * the attributes of which the values of <tt>jsonObject</tt> which are
     * neither <tt>ObjectNode</tt>, nor <tt>ArrayNode</tt> are to be
     * deserialized
     */
    public static void deserializeAbstractPacketExtensionAttributes(
            ObjectNode jsonObject,
            AbstractPacketExtension abstractPacketExtension)
    {
        jsonObject.fields().forEachRemaining(e ->
        {
            String name = e.getKey();
            JsonNode value = e.getValue();

            if (name != null && !(value instanceof ObjectNode) && !(value instanceof ArrayNode))
            {
                if (value.isTextual())
                    abstractPacketExtension.setAttribute(name, value.asText());
                else if (value.isBoolean())
                    abstractPacketExtension.setAttribute(name, value.asBoolean());
                else if (value.isNumber())
                    abstractPacketExtension.setAttribute(name, value.asLong());
                else
                    abstractPacketExtension.setAttribute(name, value.asText());
            }
        });
    }

    public static <T extends CandidatePacketExtension> T deserializeCandidate(
            ObjectNode candidate,
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
            ArrayNode candidates,
            IceUdpTransportPacketExtension transportIQ)
    {
        if ((candidates != null) && candidates.size() > 0)
        {
            for (JsonNode candidate : candidates)
            {
                deserializeCandidate(
                        (ObjectNode) candidate,
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
        ArrayNode webSockets,
        IceUdpTransportPacketExtension transportIQ)
    {
        if ((webSockets != null) && webSockets.size() > 0)
        {
            for (JsonNode webSocket : webSockets)
            {
                deserializeWebsocket(
                    webSocket.isTextual() ? webSocket.asText() : null,
                    transportIQ);
            }
        }
    }

    private static Boolean jsonNodeToBoolean(JsonNode node)
    {
        if (node == null)
            return null;
        if (node.isBoolean())
            return node.asBoolean();
        return Boolean.valueOf(node.asText());
    }

    public static DtlsFingerprintPacketExtension deserializeFingerprint(
            ObjectNode fingerprint,
            IceUdpTransportPacketExtension transportIQ)
    {
        DtlsFingerprintPacketExtension fingerprintIQ;

        if (fingerprint == null)
        {
            fingerprintIQ = null;
        }
        else
        {
            JsonNode theFingerprint
                = fingerprint.get(DtlsFingerprintPacketExtension.ELEMENT);

            fingerprintIQ = new DtlsFingerprintPacketExtension();
            // fingerprint
            if (theFingerprint != null)
            {
                fingerprintIQ.setFingerprint(theFingerprint.asText());
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
            ArrayNode fingerprints,
            IceUdpTransportPacketExtension transportIQ)
    {
        if ((fingerprints != null) && fingerprints.size() > 0)
        {
            for (JsonNode fingerprint : fingerprints)
            {
                deserializeFingerprint((ObjectNode) fingerprint, transportIQ);
            }
        }
    }

    public static void deserializeParameters(
            ObjectNode parameters,
            PayloadTypePacketExtension payloadTypeIQ)
    {
        if (parameters != null)
        {
            parameters.fields().forEachRemaining(e ->
            {
                String name = e.getKey();
                JsonNode valueNode = e.getValue();
                String value = valueNode.isNull() ? null : valueNode.asText();

                /* Some payload formats - notably red - have a parameter without a name, but
                 * JSON doesn't allow null as a key name */
                String actualName = "null".equals(name) ? null : name;

                if ((actualName != null) || (value != null))
                {
                    payloadTypeIQ.addParameter(
                            new ParameterPacketExtension(actualName, value));
                }
            });
        }
    }

    public static void deserializeRtcpFbs(
            ArrayNode rtcpFbs,
            PayloadTypePacketExtension payloadTypeIQ)
    {
        if (rtcpFbs != null)
        {
            for (JsonNode iter : rtcpFbs)
            {
                ObjectNode rtcpFb = (ObjectNode) iter;
                JsonNode typeNode = rtcpFb.get(RtcpFbPacketExtension.TYPE_ATTR_NAME);
                JsonNode subtypeNode = rtcpFb.get(RtcpFbPacketExtension.SUBTYPE_ATTR_NAME);
                String type = (typeNode != null && typeNode.isTextual()) ? typeNode.asText() : null;
                String subtype = (subtypeNode != null && subtypeNode.isTextual()) ? subtypeNode.asText() : null;
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
            ObjectNode headerExtension)
    {
        RTPHdrExtPacketExtension headerExtensionIQ;
        if (headerExtension == null)
        {
            headerExtensionIQ = null;
        }
        else
        {
            JsonNode idNode = headerExtension.get(RTPHdrExtPacketExtension.ID_ATTR_NAME);
            JsonNode uriNode = headerExtension.get(RTPHdrExtPacketExtension.URI_ATTR_NAME);
            Long id = (idNode != null && idNode.isNumber()) ? idNode.asLong() : null;
            String uriString = (uriNode != null && uriNode.isTextual()) ? uriNode.asText() : null;
            URI uri;
            try
            {
                uri = uriString != null ? new URI(uriString) : null;
            }
            catch (URISyntaxException e)
            {
                uri = null;
            }
            if (uri != null && id != null)
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
        ArrayNode headerExtensions)
    {
        Collection<RTPHdrExtPacketExtension> headerExtensionIQs = new ArrayList<>();
        for (JsonNode headerExtension : headerExtensions)
        {
            RTPHdrExtPacketExtension headerExtensionIQ = deserializeHeaderExtension((ObjectNode) headerExtension);
            if (headerExtensionIQ != null)
            {
                headerExtensionIQs.add(headerExtensionIQ);
            }
        }
        return headerExtensionIQs;
    }

    public static PayloadTypePacketExtension deserializePayloadType(
            ObjectNode payloadType)
    {
        PayloadTypePacketExtension payloadTypeIQ;

        if (payloadType == null)
        {
            payloadTypeIQ = null;
        }
        else
        {
            JsonNode parameters = payloadType.get(JSONSerializer.PARAMETERS);

            payloadTypeIQ = new PayloadTypePacketExtension();
            // attributes
            deserializeAbstractPacketExtensionAttributes(
                    payloadType,
                    payloadTypeIQ);
            // parameters
            if (parameters instanceof ObjectNode)
            {
                deserializeParameters((ObjectNode) parameters, payloadTypeIQ);
            }

            JsonNode rtcpFbs = payloadType.get(JSONSerializer.RTCP_FBS);

            if (rtcpFbs instanceof ArrayNode)
            {
                deserializeRtcpFbs((ArrayNode) rtcpFbs, payloadTypeIQ);
            }
        }
        return payloadTypeIQ;
    }

    public static Collection<PayloadTypePacketExtension> deserializePayloadTypes(
            ArrayNode payloadTypes)
    {
        Collection<PayloadTypePacketExtension> payloadTypeIQs = new ArrayList<>();
        for (JsonNode payloadType : payloadTypes)
        {
            payloadTypeIQs.add(deserializePayloadType((ObjectNode) payloadType));
        }
        return payloadTypeIQs;
    }


    public static SourcePacketExtension deserializeSource(JsonNode source)
    {
        SourcePacketExtension sourceIQ;

        if (source == null || source.isNull())
        {
            sourceIQ = null;
        }
        else if (source.isNumber() || source.isTextual())
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
        else if (source instanceof ObjectNode)
        {
            ObjectNode sourceJSONObject = (ObjectNode) source;
            JsonNode ssrcAttr = sourceJSONObject.get(SourcePacketExtension.SSRC_ATTR_NAME);
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

            JsonNode name = sourceJSONObject.get(SourcePacketExtension.NAME_ATTR_NAME);
            JsonNode videoType = sourceJSONObject.get(SourcePacketExtension.VIDEO_TYPE_ATTR_NAME);
            JsonNode rid = sourceJSONObject.get(SourcePacketExtension.RID_ATTR_NAME);
            JsonNode parameters = sourceJSONObject.get(JSONSerializer.PARAMETERS);
            if (name != null && name.isTextual())
            {
                sourceIQ.setName(name.asText());
            }
            if (videoType != null && videoType.isTextual())
            {
                sourceIQ.setVideoType(videoType.asText());
            }
            if (rid != null && rid.isTextual())
            {
                sourceIQ.setRid(rid.asText());
            }
            if (parameters instanceof ObjectNode)
            {
                ((ObjectNode) parameters).fields().forEachRemaining(e ->
                {
                    JsonNode paramValueNode = e.getValue();
                    String paramName = e.getKey();
                    String paramValue = paramValueNode.isNull() ? null : paramValueNode.asText();

                    if ((paramName != null) || (paramValue != null))
                    {
                        sourceIQ.addParameter(
                            new ParameterPacketExtension(
                                Objects.toString(paramName, null),
                                paramValue));
                    }
                });
            }
        }
        else
        {
            sourceIQ = null;
        }
        return sourceIQ;
    }

    public static SourceGroupPacketExtension deserializeSourceGroup(
            JsonNode sourceGroup)
    {
        SourceGroupPacketExtension sourceGroupIQ;

        if (!(sourceGroup instanceof ObjectNode))
        {
            sourceGroupIQ = null;
        }
        else
        {
            ObjectNode sourceGroupJSONObject = (ObjectNode) sourceGroup;

            // semantics
            JsonNode semantics = sourceGroupJSONObject
                    .get(SourceGroupPacketExtension.SEMANTICS_ATTR_NAME);

            if (semantics != null && semantics.isTextual() && semantics.asText().length() != 0)
            {
                // ssrcs
                JsonNode sourcesObject = sourceGroupJSONObject
                        .get(JSONSerializer.SOURCES);

                if (sourcesObject instanceof ArrayNode && ((ArrayNode) sourcesObject).size() != 0)
                {
                    ArrayNode sourcesJSONArray = (ArrayNode) sourcesObject;
                    List<SourcePacketExtension> sourcePacketExtensions
                        = new ArrayList<>();

                    for (JsonNode source : sourcesJSONArray)
                    {
                        SourcePacketExtension sourcePacketExtension
                                = deserializeSource(source);

                        if (sourcePacketExtension != null)
                        {
                            sourcePacketExtensions.add(sourcePacketExtension);
                        }
                    }

                    sourceGroupIQ = new SourceGroupPacketExtension();
                    sourceGroupIQ.setSemantics(semantics.asText());
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

    public static int deserializeSSRC(JsonNode o)
        throws NumberFormatException
    {
        int i = 0;

        if (o != null && !o.isNull())
        {
            if (o.isNumber())
            {
                i = o.asInt();
            }
            else
            {
                String s = o.asText();

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
            ObjectNode transport)
    {
        IceUdpTransportPacketExtension transportIQ;

        if (transport == null)
        {
            transportIQ = null;
        }
        else
        {
            JsonNode xmlns = transport.get(JSONSerializer.XMLNS);
            JsonNode fingerprints = transport.get(JSONSerializer.FINGERPRINTS);
            JsonNode candidateList = transport.get(JSONSerializer.CANDIDATE_LIST);
            JsonNode webSocketList = transport.get(JSONSerializer.WEBSOCKET_LIST);
            JsonNode remoteCandidate
                = transport.get(RemoteCandidatePacketExtension.ELEMENT);
            JsonNode rtcpMux = transport.get(IceRtcpmuxPacketExtension.ELEMENT);

            if (IceUdpTransportPacketExtension.NAMESPACE.equals(
                    xmlns != null ? xmlns.asText() : null))
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
                if (fingerprints instanceof ArrayNode)
                {
                    deserializeFingerprints(
                            (ArrayNode) fingerprints,
                            transportIQ);
                }
                // candidateList
                if (candidateList instanceof ArrayNode)
                {
                    deserializeCandidates(
                            (ArrayNode) candidateList,
                            transportIQ);
                }
                if (webSocketList instanceof ArrayNode)
                {
                    deserializeWebsockets(
                        (ArrayNode) webSocketList,
                        transportIQ);
                }
                // remoteCandidate
                if (remoteCandidate instanceof ObjectNode)
                {
                    deserializeCandidate(
                            (ObjectNode) remoteCandidate,
                            RemoteCandidatePacketExtension.class,
                            transportIQ);
                }
                // rtcpMux
                if (rtcpMux != null)
                {
                    Boolean b = jsonNodeToBoolean(rtcpMux);
                    if (b != null && b)
                    {
                        transportIQ.addChildExtension(
                            new IceRtcpmuxPacketExtension());
                    }
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
