/*
 * Copyright @ 2021 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.colibri2;

import org.jetbrains.annotations.*;
import org.jitsi.utils.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;
import java.util.*;

public class Media
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 Media element.
     */
    public static final String ELEMENT = "media";

    /**
     * The XML namespace of the Colibri2 Media element.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the <tt>type</tt> attribute.
     */
    public static final String TYPE_ATTR_NAME = "type";

    /**
     * Construct a Media.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Media()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct a media from a builder - used by Builder#build().
     */
    private Media(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        if (b.type == null)
        {
            throw new IllegalArgumentException("Media type must be set");
        }
        setAttribute(TYPE_ATTR_NAME, b.type.toString());

        for (PayloadTypePacketExtension pt: b.payloadTypes)
        {
            addChildExtension(pt);
        }

        for (RTPHdrExtPacketExtension ext: b.rtpHeaderExtensions)
        {
            addChildExtension(ext);
        }
    }

    /**
     * Get the media type of this media.
     */
    public @NotNull MediaType getType()
    {
        return MediaType.parseString(getAttributeAsString(TYPE_ATTR_NAME));
    }

    /**
     * Get the payload types of this media.
     */
    public @NotNull List<PayloadTypePacketExtension> getPayloadTypes()
    {
        return getChildExtensionsOfType(PayloadTypePacketExtension.class);
    }

    /**
     * Get the RTP header extensions of this media.
     */
    public @NotNull List<RTPHdrExtPacketExtension> getRtpHdrExts()
    {
        return getChildExtensionsOfType(RTPHdrExtPacketExtension.class);
    }

   /**
     * Get a builder for Media objects.
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for Media objects.
     */
    public static final class Builder
    {
        /**
         * The media type of the media object being built.
         */
        MediaType type = null;

        /**
         * The <tt>payload-type</tt> elements defined by XEP-0167: Jingle RTP
         * Sessions associated with this <tt>media</tt>.
         */
        private final List<PayloadTypePacketExtension> payloadTypes
            = new ArrayList<>();

        /**
         * The <tt>rtp-hdrext</tt> elements defined by XEP-0294: Jingle RTP
         * Header Extensions Negotiation associated with this media.
         */
        private final List<RTPHdrExtPacketExtension> rtpHeaderExtensions
            = new ArrayList<>();

        /**
         * Sets the media type for the media being built.
         */
        public Builder setType(MediaType t)
        {
            type = t;
            return this;
        }

        /**
         * Adds a payload type to the media being built.
         */
        public Builder addPayloadType(PayloadTypePacketExtension pt)
        {
            payloadTypes.add(pt);
            return this;
        }

        /**
         * Adds an RTP header extension to the media being built.
         */
        public Builder addRtpHdrExt(RTPHdrExtPacketExtension ext)
        {
            rtpHeaderExtensions.add(ext);
            return this;
        }

        /* TODO: add something to set values from higher-level Jingle structures. */

        private Builder()
        {
        }

        @Contract(" -> new")
        public @NotNull Media build()
        {
            return new Media(this);
        }
    }

    public static class Provider extends DefaultPacketExtensionProvider<Media>
    {

        /**
         * Creates a new packet provider for MediaSource packet extensions.
         */
        public Provider()
        {
            super(Media.class);
        }

        @Override
        public Media parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
        {
            Media m = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            String type = m.getAttributeAsString(TYPE_ATTR_NAME);
            if (type == null)
            {
                throw new SmackParsingException.RequiredAttributeMissingException(TYPE_ATTR_NAME);
            }
            try
            {
                MediaType.parseString(type);
            }
            catch (IllegalArgumentException e)
            {
                throw new SmackParsingException(TYPE_ATTR_NAME + ":" + e.getMessage());
            }
            return m;
        }
    }
}
