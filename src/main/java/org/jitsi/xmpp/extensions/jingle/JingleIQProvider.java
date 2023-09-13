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
package org.jitsi.xmpp.extensions.jingle;

import org.jitsi.xmpp.extensions.*;

import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.colibri2.*;
import org.jitsi.xmpp.extensions.condesc.*;
import org.jitsi.xmpp.extensions.jitsimeet.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.util.*;
import org.jivesoftware.smack.xml.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;

/**
 * An implementation of a Jingle IQ provider that parses incoming Jingle IQs.
 *
 * @author Emil Ivov
 */
public class JingleIQProvider extends SafeParseIqProvider<JingleIQ>
{
    /**
     * Creates a new instance of the <tt>JingleIQProvider</tt> and register all
     * jingle related extension providers. It is the responsibility of the
     * application to register the <tt>JingleIQProvider</tt> itself.
     */
    public JingleIQProvider()
    {
        //<description/> provider
        ProviderManager.addExtensionProvider(
                RtpDescriptionPacketExtension.ELEMENT,
                RtpDescriptionPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(RtpDescriptionPacketExtension.class));

        //<payload-type/> provider
        ProviderManager.addExtensionProvider(
                PayloadTypePacketExtension.ELEMENT,
                RtpDescriptionPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(PayloadTypePacketExtension.class));

        //<parameter/> provider
        ProviderManager.addExtensionProvider(
                ParameterPacketExtension.ELEMENT,
                RtpDescriptionPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(ParameterPacketExtension.class));

        //<rtp-hdrext/> provider
        ProviderManager.addExtensionProvider(
                RTPHdrExtPacketExtension.ELEMENT,
                RTPHdrExtPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(RTPHdrExtPacketExtension.class));

        //<extmap-allow-mixed> provider
        ProviderManager.addExtensionProvider(
                ExtmapAllowMixedPacketExtension.ELEMENT,
                ExtmapAllowMixedPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(ExtmapAllowMixedPacketExtension.class));

        // <sctpmap/> provider
        ProviderManager.addExtensionProvider(
                SctpMapExtension.ELEMENT,
                SctpMapExtension.NAMESPACE,
                new SctpMapExtensionProvider());

        //<encryption/> provider
        ProviderManager.addExtensionProvider(
                EncryptionPacketExtension.ELEMENT,
                RtpDescriptionPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(EncryptionPacketExtension.class));

        //<zrtp-hash/> provider
        ProviderManager.addExtensionProvider(
                ZrtpHashPacketExtension.ELEMENT,
                ZrtpHashPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(ZrtpHashPacketExtension.class));

        //<crypto/> provider
        ProviderManager.addExtensionProvider(
                CryptoPacketExtension.ELEMENT,
                RtpDescriptionPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(CryptoPacketExtension.class));

        // <group/> provider
        ProviderManager.addExtensionProvider(
                GroupPacketExtension.ELEMENT,
                GroupPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(GroupPacketExtension.class));

        //ice-udp transport
        ProviderManager.addExtensionProvider(
                IceUdpTransportPacketExtension.ELEMENT,
                IceUdpTransportPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(IceUdpTransportPacketExtension.class));

        //<raw-udp/> provider
        ProviderManager.addExtensionProvider(
                RawUdpTransportPacketExtension.ELEMENT,
                RawUdpTransportPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(RawUdpTransportPacketExtension.class));

        //ice-udp <candidate/> provider
        ProviderManager.addExtensionProvider(
                IceCandidatePacketExtension.ELEMENT,
                IceCandidatePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(IceCandidatePacketExtension.class));

        //raw-udp <candidate/> provider
        ProviderManager.addExtensionProvider(
                UdpCandidatePacketExtension.ELEMENT,
                UdpCandidatePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(UdpCandidatePacketExtension.class));

        //ice-udp <remote-candidate/> provider
        ProviderManager.addExtensionProvider(
                RemoteCandidatePacketExtension.ELEMENT,
                RemoteCandidatePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(RemoteCandidatePacketExtension.class));

        //inputevt <inputevt/> provider
        ProviderManager.addExtensionProvider(
                InputEvtPacketExtension.ELEMENT,
                InputEvtPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(InputEvtPacketExtension.class));

        //coin <conference-info/> provider
        ProviderManager.addExtensionProvider(
                CoinPacketExtension.ELEMENT,
                CoinPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(CoinPacketExtension.class));

        // DTLS-SRTP
        ProviderManager.addExtensionProvider(
                DtlsFingerprintPacketExtension.ELEMENT,
                DtlsFingerprintPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(DtlsFingerprintPacketExtension.class));

        /*
         * XEP-0251: Jingle Session Transfer <transfer/> and <transferred>
         * providers
         */
        ProviderManager.addExtensionProvider(
                TransferPacketExtension.ELEMENT,
                TransferPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(TransferPacketExtension.class));

        ProviderManager.addExtensionProvider(
                TransferredPacketExtension.ELEMENT,
                TransferredPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(TransferredPacketExtension.class));

        //conference description <callid/> provider
        ProviderManager.addExtensionProvider(
                CallIdExtension.ELEMENT,
                ConferenceDescriptionExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(CallIdExtension.class));

        //rtcp-fb
        ProviderManager.addExtensionProvider(
                RtcpFbPacketExtension.ELEMENT,
                RtcpFbPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(RtcpFbPacketExtension.class));

        //rtcp-mux (within ice transport)
        ProviderManager.addExtensionProvider(
                IceRtcpmuxPacketExtension.ELEMENT,
                IceRtcpmuxPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(IceRtcpmuxPacketExtension.class));

        //rtcp-mux (within rtp description)
        ProviderManager.addExtensionProvider(
            JingleRtcpmuxPacketExtension.ELEMENT,
            JingleRtcpmuxPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(JingleRtcpmuxPacketExtension.class));

        //web-socket
        ProviderManager.addExtensionProvider(
                WebSocketPacketExtension.ELEMENT,
                WebSocketPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(WebSocketPacketExtension.class));

        //ssrcInfo
        ProviderManager.addExtensionProvider(
                SSRCInfoPacketExtension.ELEMENT,
                SSRCInfoPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider
                    <>(SSRCInfoPacketExtension.class));

        ProviderManager.addExtensionProvider(
                SourcePacketExtension.ELEMENT,
                SourcePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(SourcePacketExtension.class));

        ProviderManager.addExtensionProvider(
                SourceGroupPacketExtension.ELEMENT,
                SourceGroupPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(SourceGroupPacketExtension.class));

        ProviderManager.addExtensionProvider(
                SourceRidGroupPacketExtension.ELEMENT,
                SourceRidGroupPacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(SourceRidGroupPacketExtension.class));

        ProviderManager.addExtensionProvider(
                ParameterPacketExtension.ELEMENT,
                SourcePacketExtension.NAMESPACE,
                new DefaultPacketExtensionProvider<>(ParameterPacketExtension.class));
    }

    /**
     * Parses a Jingle IQ sub-document and returns a {@link JingleIQ} instance.
     *
     * @param parser an XML parser.
     *
     * @return a new {@link JingleIQ} instance.
     *
     * @throws Exception if an error occurs parsing the XML.
     */
    @Override
    protected JingleIQ doParse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment)
        throws Exception
    {
        //let's first handle the "jingle" element params.
        JingleAction action = JingleAction.parseString(parser
                        .getAttributeValue("", JingleIQ.ACTION_ATTR_NAME));
        String initiator = parser
                         .getAttributeValue("", JingleIQ.INITIATOR_ATTR_NAME);
        String responder = parser
                        .getAttributeValue("", JingleIQ.RESPONDER_ATTR_NAME);
        String sid = parser
                        .getAttributeValue("", JingleIQ.SID_ATTR_NAME);

        JingleIQ jingleIQ = new JingleIQ(action, sid);
        if (initiator != null)
        {
            Jid initiatorJid = JidCreate.from(initiator);
            jingleIQ.setInitiator(initiatorJid);
        }

        if (responder != null)
        {
            Jid responderJid = JidCreate.from(responder);
            jingleIQ.setResponder(responderJid);
        }

        boolean done = false;

        // Sub-elements providers
        DefaultPacketExtensionProvider<ContentPacketExtension> contentProvider
            = new DefaultPacketExtensionProvider
            <>(ContentPacketExtension.class);
        ReasonProvider reasonProvider = new ReasonProvider();
        DefaultPacketExtensionProvider<TransferPacketExtension> transferProvider
            = new DefaultPacketExtensionProvider
            <>(TransferPacketExtension.class);
        DefaultPacketExtensionProvider<CoinPacketExtension> coinProvider
            = new DefaultPacketExtensionProvider<>(CoinPacketExtension.class);
        DefaultPacketExtensionProvider<CallIdExtension> callidProvider
            = new DefaultPacketExtensionProvider<>(CallIdExtension.class);

        // Now go on and parse the jingle element's content.
        XmlPullParser.Event eventType;
        String elementName;
        String namespace;

        while (!done)
        {
            eventType = parser.next();
            elementName = parser.getName();
            namespace = parser.getNamespace();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
                // <content/>
                if (elementName.equals(ContentPacketExtension.ELEMENT))
                {
                    ContentPacketExtension content
                        = contentProvider.parse(parser);
                    jingleIQ.addContent(content);
                }
                // <reason/>
                else if (elementName.equals(ReasonPacketExtension.ELEMENT))
                {
                    ReasonPacketExtension reason
                        = reasonProvider.parse(parser);
                    jingleIQ.setReason(reason);
                }
                // <transfer/>
                else if (elementName.equals(
                                TransferPacketExtension.ELEMENT)
                        && namespace.equals(TransferPacketExtension.NAMESPACE))
                {
                    jingleIQ.addExtension(transferProvider.parse(parser));
                }
                // <conference-info/>
                else if (elementName.equals(CoinPacketExtension.ELEMENT))
                {
                    jingleIQ.addExtension(coinProvider.parse(parser));
                }
                else if (elementName.equals(
                        CallIdExtension.ELEMENT))
                {
                    jingleIQ.addExtension(callidProvider.parse(parser));
                }
                else if (elementName.equals(
                        GroupPacketExtension.ELEMENT))
                {
                    jingleIQ.addExtension(
                        GroupPacketExtension.parseExtension(parser, xmlEnvironment));
                }
                //<mute/> <active/> and other session-info elements
                else if (namespace.equals(SessionInfoPacketExtension.NAMESPACE))
                {
                    SessionInfoType type = SessionInfoType.valueOf(elementName);

                    //<mute/>
                    if ( type == SessionInfoType.mute
                        || type == SessionInfoType.unmute)
                    {
                        String name = parser.getAttributeValue("",
                                MuteSessionInfoPacketExtension.NAME_ATTR_VALUE);

                        jingleIQ.setSessionInfo(
                                new MuteSessionInfoPacketExtension(
                                        type == SessionInfoType.mute, name));
                    }
                    //<hold/>, <unhold/>, <active/>, etc.
                    else
                    {
                        jingleIQ.setSessionInfo(
                                        new SessionInfoPacketExtension(type));
                    }
                }
                else
                {
                    PacketParserUtils.addExtensionElement(jingleIQ, parser, xmlEnvironment);
                }
            }

            if ((eventType == XmlPullParser.Event.END_ELEMENT)
                    && parser.getName().equals(JingleIQ.ELEMENT))
            {
                    done = true;
            }
        }
        return jingleIQ;
    }
}
