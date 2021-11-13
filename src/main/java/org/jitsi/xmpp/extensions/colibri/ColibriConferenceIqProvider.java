/*
 * Copyright @ 2018 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.colibri;

import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.jingle.*;

import org.jitsi.xmpp.extensions.jitsimeet.*;
import org.jitsi.utils.logging2.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;
import org.jxmpp.jid.impl.*;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Implements an {@link IqProvider} for the Jitsi Videobridge extension {@link
 * ColibriConferenceIQ}.
 *
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class ColibriConferenceIqProvider
    extends IqProvider<ColibriConferenceIQ>
{
    /**
     * The logger instance used by this class.
     */
    private final static Logger logger = new LoggerImpl(
        ColibriConferenceIqProvider.class.getName());

    /** Initializes a new <tt>ColibriIQProvider</tt> instance. */
    public ColibriConferenceIqProvider()
    {
        ProviderManager.addExtensionProvider(
                PayloadTypePacketExtension.ELEMENT,
                ColibriConferenceIQ.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        PayloadTypePacketExtension.class));
        ProviderManager.addExtensionProvider(
                RtcpFbPacketExtension.ELEMENT,
                RtcpFbPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        RtcpFbPacketExtension.class));
        ProviderManager.addExtensionProvider(
                RTPHdrExtPacketExtension.ELEMENT,
                ColibriConferenceIQ.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        RTPHdrExtPacketExtension.class));
        ProviderManager.addExtensionProvider(
                SourcePacketExtension.ELEMENT,
                SourcePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        SourcePacketExtension.class));
        ProviderManager.addExtensionProvider(
                SourceGroupPacketExtension.ELEMENT,
                SourceGroupPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        SourceGroupPacketExtension.class));
        ProviderManager.addExtensionProvider(
                SourceRidGroupPacketExtension.ELEMENT,
                SourceRidGroupPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(
                        SourceRidGroupPacketExtension.class));

        ExtensionElementProvider<ParameterPacketExtension> parameterProvider
                = new DefaultPacketExtensionProvider<>(
                ParameterPacketExtension.class);

        ProviderManager.addExtensionProvider(
                ParameterPacketExtension.ELEMENT,
                ColibriConferenceIQ.NAMESPACE,
                parameterProvider);
        ProviderManager.addExtensionProvider(
                ParameterPacketExtension.ELEMENT,
                SourcePacketExtension.NAMESPACE,
                parameterProvider);

        // ssrc-info
        ProviderManager.addExtensionProvider(
            SSRCInfoPacketExtension.ELEMENT,
            SSRCInfoPacketExtension.NAMESPACE,
            new DefaultPacketExtensionProvider<>(
                SSRCInfoPacketExtension.class));
    }

    private void addChildExtension(
            ColibriConferenceIQ.Channel channel,
            ExtensionElement childExtension)
    {
        if (childExtension instanceof PayloadTypePacketExtension)
        {
            PayloadTypePacketExtension payloadType
                = (PayloadTypePacketExtension) childExtension;

            if ("opus".equals(payloadType.getName())
                    && (payloadType.getChannels() != 2))
            {
                /*
                 * We only have a Format for opus with 2 channels, because it
                 * MUST be advertised with 2 channels. Fixing the number of
                 * channels here allows us to be compatible with agents who
                 * advertise it with 1 channel.
                 */
                payloadType.setChannels(2);
            }
            channel.addPayloadType(payloadType);
        }
        else if (childExtension instanceof IceUdpTransportPacketExtension)
        {
            IceUdpTransportPacketExtension transport
                = (IceUdpTransportPacketExtension) childExtension;

            channel.setTransport(transport);
        }
        else if (childExtension instanceof SourcePacketExtension)
        {
            channel.addSource((SourcePacketExtension) childExtension);
        }
        else if (childExtension instanceof SourceGroupPacketExtension)
        {
            SourceGroupPacketExtension sourceGroup
                    = (SourceGroupPacketExtension)childExtension;

            channel.addSourceGroup(sourceGroup);
        }
        else if (childExtension instanceof RTPHdrExtPacketExtension)
        {
            RTPHdrExtPacketExtension rtpHdrExtPacketExtension
                    = (RTPHdrExtPacketExtension) childExtension;

            channel.addRtpHeaderExtension(rtpHdrExtPacketExtension);
        }
        else
        {
            logger.error(
                "Ignoring a child of 'channel' of unknown type: "
                    + childExtension);
        }
    }

    private void addChildExtension(
            ColibriConferenceIQ.ChannelBundle bundle,
            ExtensionElement childExtension)
    {
        if (childExtension instanceof IceUdpTransportPacketExtension)
        {
            IceUdpTransportPacketExtension transport
                = (IceUdpTransportPacketExtension) childExtension;

            bundle.setTransport(transport);
        }
    }

    private void addChildExtension(
            ColibriConferenceIQ.SctpConnection sctpConnection,
            ExtensionElement childExtension)
    {
        if (childExtension instanceof IceUdpTransportPacketExtension)
        {
            IceUdpTransportPacketExtension transport
                = (IceUdpTransportPacketExtension) childExtension;

            sctpConnection.setTransport(transport);
        }
    }

    private ExtensionElement parseExtension(
            XmlPullParser parser,
            String name,
            String namespace)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        ExtensionElementProvider<?> extensionProvider
            = ProviderManager.getExtensionProvider(
                        name,
                        namespace);
        ExtensionElement extension;

        if (extensionProvider == null)
        {
            /*
             * No ExtensionElementProvider for the specified name and namespace
             * has been registered. Throw away the element.
             */
            throwAway(parser, name);
            extension = null;
        }
        else
        {
            extension = extensionProvider.parse(parser);
        }
        return extension;
    }

    /**
     * Parses an IQ sub-document and creates an
     * <tt>org.jivesoftware.smack.packet.IQ</tt> instance.
     *
     * @param parser an <tt>XmlPullParser</tt> which specifies the IQ
     * sub-document to be parsed into a new <tt>IQ</tt> instance
     * @return a new <tt>IQ</tt> instance parsed from the specified IQ
     * sub-document
     */
    public ColibriConferenceIQ parse(XmlPullParser parser, int initialDepth, IqData data, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();
        ColibriConferenceIQ iq;

        if (ColibriConferenceIQ.ELEMENT.equals(parser.getName())
                && ColibriConferenceIQ.NAMESPACE.equals(namespace))
        {
            ColibriConferenceIQ conference = new ColibriConferenceIQ();
            String conferenceID = parser.getAttributeValue("", ColibriConferenceIQ.ID_ATTR_NAME);

            if ((conferenceID != null) && (conferenceID.length() != 0))
            {
                conference.setID(conferenceID);
            }

            String conferenceGID = parser.getAttributeValue("", ColibriConferenceIQ.GID_ATTR_NAME);

            if ((conferenceGID != null) && (conferenceGID.length() != 0))
            {
                conference.setGID(conferenceGID);
            }

            String conferenceName = parser.getAttributeValue("", ColibriConferenceIQ.NAME_ATTR_NAME);
            if (isNotEmpty(conferenceName))
            {
                conference.setName(JidCreate.entityBareFrom(conferenceName));
            }

            String meetingId = parser.getAttributeValue("", ColibriConferenceIQ.MEETING_ID_ATTR_NAME);
            if (isNotEmpty(meetingId))
            {
                conference.setMeetingId(meetingId);
            }

            boolean done = false;
            ColibriConferenceIQ.Channel channel = null;
            ColibriConferenceIQ.RTCPTerminationStrategy rtcpTerminationStrategy = null;
            ColibriConferenceIQ.SctpConnection sctpConnection = null;
            ColibriConferenceIQ.ChannelBundle bundle = null;
            ColibriConferenceIQ.Content content = null;
            ColibriConferenceIQ.Recording recording = null;
            ColibriConferenceIQ.Endpoint conferenceEndpoint = null;
            StringBuilder ssrc = null;

            while (!done)
            {
                switch (parser.next())
                {
                case END_ELEMENT:
                {
                    String name = parser.getName();

                    if (ColibriConferenceIQ.ELEMENT.equals(name))
                    {
                        done = true;
                    }
                    else if (ColibriConferenceIQ.Channel.ELEMENT.equals(
                            name))
                    {
                        content.addChannel(channel);
                        channel = null;
                    }
                    else if (ColibriConferenceIQ.SctpConnection.ELEMENT
                            .equals(name))
                    {
                        if (sctpConnection != null)
                            content.addSctpConnection(sctpConnection);

                        sctpConnection = null;
                    }
                    else if (ColibriConferenceIQ.ChannelBundle.ELEMENT
                            .equals(name))
                    {
                        if (bundle != null)
                        {
                            if (conference.addChannelBundle(bundle) != null)
                            {
                                logger.warn(
                                    "Replacing a channel-bundle with the same"
                                        + "ID (not a valid Colibri packet).");
                            }

                            bundle = null;
                        }
                    }
                    else if (ColibriConferenceIQ.Endpoint.ELEMENT
                            .equals(name))
                    {
                        if (conference.addEndpoint(conferenceEndpoint) != null)
                        {
                            logger.warn(
                                "Replacing an endpoint element with the same"
                                    + "ID (not a valid Colibri packet).");
                        }
                        conferenceEndpoint = null;
                    }
                    else if (ColibriConferenceIQ.Channel.SSRC_ELEMENT
                            .equals(name))
                    {
                        String s = ssrc.toString().trim();

                        if (s.length() != 0)
                        {
                            int i;

                            /*
                             * Legacy versions of Jitsi and Jitsi Videobridge
                             * may send a synchronization source (SSRC)
                             * identifier as a negative integer.
                             */
                            if (s.startsWith("-"))
                                i = Integer.parseInt(s);
                            else
                                i = (int) Long.parseLong(s);
                            channel.addSSRC(i);
                        }
                        ssrc = null;
                    }
                    else if (ColibriConferenceIQ.Content.ELEMENT.equals(
                            name))
                    {
                        conference.addContent(content);
                        content = null;
                    }
                    else if (ColibriConferenceIQ.RTCPTerminationStrategy
                            .ELEMENT.equals(name))
                    {
                        conference.setRTCPTerminationStrategy(
                                rtcpTerminationStrategy);
                        rtcpTerminationStrategy = null;
                    }
                    else if (ColibriConferenceIQ.Recording.ELEMENT.equals(
                            name))
                    {
                        conference.setRecording(recording);
                        recording = null;
                    }
                    else if (ColibriConferenceIQ.GracefulShutdown.ELEMENT
                        .equals(name))
                    {
                        conference.setGracefulShutdown(true);
                    }
                    break;
                }

                case START_ELEMENT:
                {
                    String name = parser.getName();

                    if (ColibriConferenceIQ.Channel.ELEMENT.equals(name))
                    {
                        String type
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel.TYPE_ATTR_NAME);

                        if (ColibriConferenceIQ.OctoChannel.TYPE.equals(type))
                        {
                            channel = new ColibriConferenceIQ.OctoChannel();
                        }
                        else
                        {
                            channel = new ColibriConferenceIQ.Channel();
                        }

                        // direction
                        String direction
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .DIRECTION_ATTR_NAME);

                        if ((direction != null) && (direction.length() != 0))
                        {
                            channel.setDirection(direction);
                        }

                        // endpoint
                        String endpoint
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .ENDPOINT_ATTR_NAME);

                        if (isNotEmpty(endpoint))
                        {
                            channel.setEndpoint(endpoint);
                        }

                        String channelBundleId
                            = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.ChannelCommon
                                        .CHANNEL_BUNDLE_ID_ATTR_NAME);
                        if (isNotEmpty(channelBundleId))
                        {
                            channel.setChannelBundleId(channelBundleId);
                        }

                        // expire
                        String expire
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .EXPIRE_ATTR_NAME);

                        if ((expire != null) && (expire.length() != 0))
                            channel.setExpire(Integer.parseInt(expire));

                        String packetDelay
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .PACKET_DELAY_ATTR_NAME);
                        if (isNotEmpty(packetDelay))
                            channel.setPacketDelay(
                                    Integer.parseInt(packetDelay));

                        // host
                        String host
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel.HOST_ATTR_NAME);

                        if ((host != null) && (host.length() != 0))
                            channel.setHost(host);

                        // id
                        String channelID
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel.ID_ATTR_NAME);

                        if ((channelID != null) && (channelID.length() != 0))
                            channel.setID(channelID);

                        // initiator
                        String initiator
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .INITIATOR_ATTR_NAME);

                        if ((initiator != null) && (initiator.length() != 0))
                        {
                            channel.setInitiator(Boolean.valueOf(initiator));
                        }

                        // lastN
                        String lastN
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .LAST_N_ATTR_NAME);

                        if ((lastN != null) && (lastN.length() != 0))
                        {
                            channel.setLastN(Integer.parseInt(lastN));
                        }

                        // simulcastMode
                        String simulcastMode
                                = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Channel
                                        .SIMULCAST_MODE_ATTR_NAME);

                        if (isNotEmpty(simulcastMode))
                        {
                            channel.setSimulcastMode(
                                    SimulcastMode.fromString(simulcastMode));
                        }

                        // receiving simulcast layer
                        String receivingSimulcastLayer
                                = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Channel
                                            .RECEIVING_SIMULCAST_LAYER);

                        if ((receivingSimulcastLayer != null)
                                && (receivingSimulcastLayer.length() != 0))
                            channel.setReceivingSimulcastLayer(
                                    Integer.parseInt(receivingSimulcastLayer));

                        // rtcpPort
                        String rtcpPort
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .RTCP_PORT_ATTR_NAME);

                        if ((rtcpPort != null) && (rtcpPort.length() != 0))
                        {
                            channel.setRTCPPort(Integer.parseInt(rtcpPort));
                        }

                        // rtpLevelRelayType
                        String rtpLevelRelayType
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .RTP_LEVEL_RELAY_TYPE_ATTR_NAME);

                        if ((rtpLevelRelayType != null)
                                && (rtpLevelRelayType.length() != 0))
                        {
                            channel.setRTPLevelRelayType(rtpLevelRelayType);
                        }

                        // rtpPort
                        String rtpPort
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Channel
                                            .RTP_PORT_ATTR_NAME);

                        if ((rtpPort != null) && (rtpPort.length() != 0))
                        {
                            channel.setRTPPort(Integer.parseInt(rtpPort));
                        }
                    }
                    else if (ColibriConferenceIQ.ChannelBundle
                            .ELEMENT.equals(name))
                    {
                        String bundleId
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ
                                        .ChannelBundle.ID_ATTR_NAME);

                        if (isNotEmpty(bundleId))
                        {
                            bundle = new ColibriConferenceIQ
                                        .ChannelBundle(bundleId);
                        }
                    }
                    else if (ColibriConferenceIQ.RTCPTerminationStrategy
                            .ELEMENT.equals(name))
                    {
                        rtcpTerminationStrategy =
                                new ColibriConferenceIQ.RTCPTerminationStrategy();

                        // name
                        String strategyName
                                = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.RTCPTerminationStrategy
                                        .NAME_ATTR_NAME);

                        if ((strategyName != null)
                                && (strategyName.length() != 0))
                            rtcpTerminationStrategy.setName(strategyName);

                    }
                    else if (ColibriConferenceIQ.OctoChannel
                                    .RELAY_ELEMENT.equals(name))
                    {
                        String id
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.OctoChannel.RELAY_ID_ATTR_NAME);

                        if (id != null &&
                            channel instanceof ColibriConferenceIQ.OctoChannel)
                        {
                            ((ColibriConferenceIQ.OctoChannel) channel)
                                .addRelay(id);
                        }
                    }
                    else if (ColibriConferenceIQ.Channel.SSRC_ELEMENT
                            .equals(name))
                    {
                        ssrc = new StringBuilder();
                    }
                    else if (ColibriConferenceIQ.Content.ELEMENT.equals(
                            name))
                    {
                        content = new ColibriConferenceIQ.Content();

                        String contentName
                            = parser.getAttributeValue(
                                    "",
                                    ColibriConferenceIQ.Content.NAME_ATTR_NAME);

                        if ((contentName != null)
                                && (contentName.length() != 0))
                            content.setName(contentName);
                    }
                    else if (ColibriConferenceIQ.Recording.ELEMENT.equals(
                            name))
                    {
                        String stateStr
                                = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Recording.STATE_ATTR_NAME);
                        String token
                                = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Recording.TOKEN_ATTR_NAME);

                        recording
                                = new ColibriConferenceIQ.Recording(
                                stateStr,
                                token);
                    }
                    else if (ColibriConferenceIQ.SctpConnection.ELEMENT
                        .equals(name))
                    {
                        // Endpoint
                        String endpoint
                            = parser.getAttributeValue(
                            "",
                            ColibriConferenceIQ.
                                SctpConnection.ENDPOINT_ATTR_NAME);

                        // id
                        String connID
                            = parser.getAttributeValue(
                            "",
                            ColibriConferenceIQ.
                                ChannelCommon.ID_ATTR_NAME);

                        if (isEmpty(connID) && isEmpty(endpoint))
                        {
                            sctpConnection = null;
                            continue;
                        }

                        sctpConnection
                            = new ColibriConferenceIQ.SctpConnection();

                        if (isNotEmpty(connID))
                            sctpConnection.setID(connID);

                        if (isNotEmpty(endpoint))
                        {
                            sctpConnection.setEndpoint(endpoint);
                        }

                        // port
                        String port
                            = parser.getAttributeValue(
                            "",
                            ColibriConferenceIQ.SctpConnection.PORT_ATTR_NAME);
                        if (isNotEmpty(port))
                            sctpConnection.setPort(Integer.parseInt(port));

                        String channelBundleId
                            = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.ChannelCommon
                                        .CHANNEL_BUNDLE_ID_ATTR_NAME);
                        if (isNotEmpty(channelBundleId))
                        {
                            sctpConnection.setChannelBundleId(channelBundleId);
                        }

                        // initiator
                        String initiator
                            = parser.getAttributeValue(
                            "",
                            ColibriConferenceIQ.SctpConnection
                                .INITIATOR_ATTR_NAME);

                        if (isNotEmpty(initiator))
                            sctpConnection.setInitiator(
                                Boolean.valueOf(initiator));

                        // expire
                        String expire
                            = parser.getAttributeValue(
                            "",
                            ColibriConferenceIQ.SctpConnection
                                .EXPIRE_ATTR_NAME);

                        if (isNotEmpty(expire))
                            sctpConnection.setExpire(Integer.parseInt(expire));
                    }
                    else if (ColibriConferenceIQ.Endpoint.ELEMENT
                            .equals(name))
                    {
                        String id
                            = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Endpoint.ID_ATTR_NAME);

                        String displayName
                            = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Endpoint
                                    .DISPLAYNAME_ATTR_NAME);

                        String statsId
                            = parser.getAttributeValue(
                                "",
                                ColibriConferenceIQ.Endpoint
                                    .STATS_ID_ATTR_NAME);

                        if (isNotEmpty(id))
                        {
                            conferenceEndpoint
                                = new ColibriConferenceIQ.Endpoint(
                                    id, statsId, displayName);
                        }
                    }
                    else if ( channel != null
                              || sctpConnection != null
                              || bundle != null )
                    {
                        String peName = null;
                        String peNamespace = null;

                        if (IceUdpTransportPacketExtension.ELEMENT
                                    .equals(name)
                                && IceUdpTransportPacketExtension.NAMESPACE
                                        .equals(parser.getNamespace()))
                        {
                            peName = name;
                            peNamespace
                                = IceUdpTransportPacketExtension.NAMESPACE;
                        }
                        else if (PayloadTypePacketExtension.ELEMENT.equals(
                                name))
                        {
                            /*
                             * The channel element of the Jitsi Videobridge
                             * protocol reuses the payload-type element defined
                             * in XEP-0167: Jingle RTP Sessions.
                             */
                            peName = name;
                            peNamespace = namespace;
                        }
                        else if (RtcpFbPacketExtension.ELEMENT.equals(
                                name)
                                && RtcpFbPacketExtension.NAMESPACE
                                .equals(parser.getNamespace()))
                        {
                            /*
                             * The channel element of the Jitsi Videobridge
                             * protocol reuses the payload-type element defined
                             * in XEP-0167: Jingle RTP Sessions.
                             */
                            peName = name;
                            peNamespace = namespace;
                        }
                        else if (RTPHdrExtPacketExtension.ELEMENT.equals(
                                name))
                        {
                            /*
                             * The channel element of the Jitsi Videobridge
                             * protocol reuses the rtp-hdrext element defined
                             * in XEP-0167: Jingle RTP Sessions.
                             */
                            peName = name;
                            peNamespace = namespace;
                        }
                        else if (RawUdpTransportPacketExtension.ELEMENT
                                    .equals(name)
                                && RawUdpTransportPacketExtension.NAMESPACE
                                        .equals(parser.getNamespace()))
                        {
                            peName = name;
                            peNamespace
                                = RawUdpTransportPacketExtension.NAMESPACE;
                        }
                        else if (SourcePacketExtension.ELEMENT.equals(name)
                                && SourcePacketExtension.NAMESPACE.equals(
                                        parser.getNamespace()))
                        {
                            peName = name;
                            peNamespace = SourcePacketExtension.NAMESPACE;
                        }
                        else if (SourceGroupPacketExtension.ELEMENT
                                                .equals(name)
                                && SourceGroupPacketExtension.NAMESPACE
                                                .equals(parser.getNamespace()))
                        {
                            peName = name;
                            peNamespace = SourceGroupPacketExtension.NAMESPACE;
                        }
                        else if (SourceRidGroupPacketExtension.ELEMENT
                                                .equals(name)
                                && SourceRidGroupPacketExtension.NAMESPACE
                                                .equals(parser.getNamespace()))
                        {
                            peName = name;
                            peNamespace = SourceRidGroupPacketExtension.NAMESPACE;
                        }
                        if (peName == null)
                        {
                            throwAway(parser, name);
                        }
                        else
                        {
                            ExtensionElement extension
                                = parseExtension(parser, peName, peNamespace);

                            if (extension != null)
                            {
                                if (channel != null)
                                {
                                    addChildExtension(channel, extension);
                                }
                                else if (sctpConnection != null)
                                {
                                    addChildExtension(sctpConnection,
                                                      extension);
                                }
                                else
                                {
                                    addChildExtension(bundle, extension);
                                }
                            }
                        }
                    }
                    break;
                }

                case TEXT_CHARACTERS:
                {
                    if (ssrc != null)
                        ssrc.append(parser.getText());
                    break;
                }
                }
            }

            iq = conference;
        }

        else
            iq = null;

        return iq;
    }

    /**
     * Parses using a specific <tt>XmlPullParser</tt> and ignores XML content
     * presuming that the specified <tt>parser</tt> is currently at the start
     * tag of an element with a specific name and throwing away until the end
     * tag with the specified name is encountered.
     *
     * @param parser the <tt>XmlPullParser</tt> which parses the XML content
     * @param name the name of the element at the start tag of which the
     * specified <tt>parser</tt> is presumed to currently be and until the end
     * tag of which XML content is to be thrown away
     * @throws Exception if an errors occurs while parsing the XML content
     */
    private void throwAway(XmlPullParser parser, String name)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        while ((XmlPullParser.Event.END_ELEMENT != parser.next())
                || !name.equals(parser.getName()));
    }
}
