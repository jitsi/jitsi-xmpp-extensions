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
package org.jitsi.xmpp.extensions.rayo;

import org.apache.commons.lang3.StringUtils;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri2.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

/**
 * Provider handles parsing of Rayo IQ stanzas and converting objects back to
 * their XML representation.
 *
 * FIXME: implements only the minimum required to start and hang up a call
 *
 * @author Pawel Domas
 */
public class RayoIqProvider
    extends SafeParseIqProvider<RayoIq>
{
    /**
     * Rayo namespace.
     */
    public final static String NAMESPACE = "urn:xmpp:rayo:1";

    /**
     * Registers this IQ provider into <tt>ProviderManager</tt>.
     */
    public void registerRayoIQs()
    {
        // <dial>
        ProviderManager.addIQProvider(
            DialIq.ELEMENT,
            NAMESPACE,
            this);

        // <ref>
        ProviderManager.addIQProvider(
            RefIq.ELEMENT,
            NAMESPACE,
            this);

        // <hangup>
        ProviderManager.addIQProvider(
            HangUp.ELEMENT,
            NAMESPACE,
            this);

        // <end> presence extension
        ProviderManager.addExtensionProvider(
            EndExtension.ELEMENT,
            NAMESPACE,
            new DefaultPacketExtensionProvider<>(EndExtension.class));

        // <header> extension
        ProviderManager.addExtensionProvider(
            HeaderExtension.ELEMENT,
            NAMESPACE,
            new DefaultPacketExtensionProvider<>(HeaderExtension.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RayoIq doParse(XmlPullParser parser)
        throws Exception
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!NAMESPACE.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();

        RayoIq iq;
        DialIq dial;
        RefIq ref;
        //End end = null;

        if (DialIq.ELEMENT.equals(rootElement))
        {
            iq = dial = new DialIq();
            String src = parser.getAttributeValue("", DialIq.SRC_ATTR_NAME);
            String dst = parser.getAttributeValue("", DialIq.DST_ATTR_NAME);

            // Destination is mandatory
            if (StringUtils.isEmpty(dst))
                return null;

            dial.setSource(src);
            dial.setDestination(dst);
        }
        else if (RefIq.ELEMENT.equals(rootElement))
        {
            iq = ref = new RefIq();
            String uri = parser.getAttributeValue("", RefIq.URI_ATTR_NAME);

            if (StringUtils.isEmpty(uri))
                return null;

            ref.setUri(uri);
        }
        else if (HangUp.ELEMENT.equals(rootElement))
        {
            iq = new HangUp();
        }
        /*else if (End.ELEMENT.equals(rootElement))
        {
            iq = end = new End();
        }*/
        else
        {
            return null;
        }

        boolean done = false;
        HeaderExtension header = null;
        //ReasonExtension reason = null;

        while (!done)
        {
            switch (parser.next())
            {
                case END_ELEMENT:
                {
                    String name = parser.getName();

                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                    else if (HeaderExtension.ELEMENT.equals(
                        name))
                    {
                        if (header != null)
                        {
                            iq.addExtension(header);

                            header = null;
                        }
                    }
                    /*else if (End.isValidReason(name))
                    {
                        if (end != null && reason != null)
                        {
                            end.setReason(reason);

                            reason = null;
                        }
                    }*/
                    break;
                }

                case START_ELEMENT:
                {
                    String name = parser.getName();

                    if (HeaderExtension.ELEMENT.equals(name))
                    {
                        header = new HeaderExtension();

                        String nameAttr
                            = parser.getAttributeValue(
                            "", HeaderExtension.NAME_ATTR_NAME);

                        header.setName(nameAttr);

                        String valueAttr
                            = parser.getAttributeValue(
                            "", HeaderExtension.VALUE_ATTR_NAME);

                        header.setValue(valueAttr);
                    }
                    /*else if (End.isValidReason(name))
                    {
                        reason = new ReasonPacketExtension(name);

                        String platformCode
                            = parser.getAttributeValue(
                            "", ReasonPacketExtension.PLATFORM_CODE_ATTRIBUTE);

                        if (StringUtils.isNotEmpty(platformCode))
                        {
                            reason.setPlatformCode(platformCode);
                        }
                    }*/
                    break;
                }

                case TEXT_CHARACTERS:
                {
                    // Parse some text here
                    break;
                }
            }
        }

        return iq;
    }

}
