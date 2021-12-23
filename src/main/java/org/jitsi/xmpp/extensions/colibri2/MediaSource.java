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
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;
import java.util.*;

/**
 * A Colibri2 media-source element
 */
public class MediaSource
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 MediaSource element.
     */
    public static final String ELEMENT = "media-source";

    /**
     * The XML namespace of the Colibri2 MediaSource element.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the <tt>id</tt> attribute.
     */
    public static final String ID_NAME = "id";

    /**
     * The name of the <tt>type</tt> attribute.
     */
    public static final String TYPE_ATTR_NAME = "type";

    /**
     * Construct a MediaSource.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public MediaSource()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct a source from a builder - used by Builder#build().
     */
    private MediaSource(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        if (b.type == null)
        {
            throw new IllegalArgumentException("Source media type must be set");
        }
        setAttribute(TYPE_ATTR_NAME, b.type.toString());

        setAttribute(ID_NAME, b.id);

        for (SourcePacketExtension s: b.sources)
        {
            addChildExtension(s);
        }

        for (SourceGroupPacketExtension sg: b.ssrcGroups)
        {
            addChildExtension(sg);
        }
    }

    /**
     * Get the ID of this source.
     */
    public @Nullable String getId()
    {
        return getAttributeAsString(ID_NAME);
    }

    /**
     * Get the media type of this source.
     */
    public @NotNull MediaType getType()
    {
        return MediaType.parseString(getAttributeAsString(TYPE_ATTR_NAME));
    }

    /**
     * Get the sources of this media source.
     */
    public @NotNull List<SourcePacketExtension> getSources()
    {
        return getChildExtensionsOfType(SourcePacketExtension.class);
    }

    /**
     * Get the RTP header extensions of this media.
     */
    public @NotNull List<SourceGroupPacketExtension> getSsrcGroups()
    {
        return getChildExtensionsOfType(SourceGroupPacketExtension.class);
    }

    /**
     * Get a builder for MediaSource objects.
     */
    @Contract(" -> new")
    public static @NotNull Builder getBuilder()
    {
        return new Builder();
    }

    /**
     * Builder for MediaSource objects.
     */
    public static final class Builder
    {
        /**
         * The media type of the source object being built.
         */
        MediaType type = null;

        /**
         * The ID of the media source being built.
         */
        String id = null;

        /**
         * The <tt>source</tt> elements defined by XEP-0339: Source-Specific
         * Media Attributes associated with this <tt>media-source</tt>.
         */
        private final List<SourcePacketExtension> sources = new ArrayList<>();

        /**
         * The <tt>ssrc-group</tt> elements defined by XEP-0339: Source-Specific
         * Media Attributes associated with this <tt>media-source</tt>.
         */
        private final List<SourceGroupPacketExtension> ssrcGroups = new ArrayList<>();

        /**
         * Sets the media type for the media source being built.
         */
        public Builder setType(MediaType t)
        {
            type = t;
            return this;
        }

        /**
         * Sets the ID for the media source being built.
         */
        public Builder setId(String id)
        {
            this.id = id;
            return this;
        }

        /**
         * Adds a payload type to the media being built.
         */
        public Builder addSource(SourcePacketExtension pt)
        {
            sources.add(pt);
            return this;
        }

        /**
         * Adds an RTP header extension to the media being built.
         */
        public Builder addSsrcGroup(SourceGroupPacketExtension ext)
        {
            ssrcGroups.add(ext);
            return this;
        }

        /* TODO: add something to set values from higher-level Jingle structures. */

        private Builder()
        {
        }

        @Contract(" -> new")
        public @NotNull MediaSource build()
        {
            return new MediaSource(this);
        }
    }

    public static class Provider extends DefaultPacketExtensionProvider<MediaSource>
    {

        /**
         * Creates a new packet provider for MediaSource packet extensions.
         */
        public Provider()
        {
            super(MediaSource.class);
        }

        @Override
        public MediaSource parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
        {
            MediaSource ms = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            String type = ms.getAttributeAsString(TYPE_ATTR_NAME);
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
            return ms;
        }
    }
}
