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

import org.jitsi.utils.*;
import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;
import org.jitsi.xmpp.extensions.jingle.*;

import javax.xml.namespace.*;
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
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference-modify</tt> IQ.
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
        super.setAttribute(TYPE_ATTR_NAME, b.type.toString());

        super.setAttribute(ID_NAME, b.id);

        for (SourcePacketExtension s: b.sources) {
            super.addChildExtension(s);
        }

        for (SourceGroupPacketExtension sg: b.ssrcGroups) {
            super.addChildExtension(sg);
        }
    }

    /**
     * Get the ID of this source.
     */
    public String getId()
    {
        return super.getAttributeAsString(ID_NAME);
    }

    /**
     * Get the media type of this source.
     */
    public MediaType getType()
    {
        /* TODO: handle invalid media types at XML parse time?  This will throw at get-time. */
        return MediaType.parseString(super.getAttributeAsString(TYPE_ATTR_NAME));
    }

    /**
     * Get the sources of this media source.
     */
    public List<SourcePacketExtension> getSources()
    {
        return super.getChildExtensionsOfType(SourcePacketExtension.class);
    }

    /**
     * Get the RTP header extensions of this media.
     */
    public List<SourceGroupPacketExtension> getSsrcGroups()
    {
        return super.getChildExtensionsOfType(SourceGroupPacketExtension.class);
    }

    /**
     * Get a builder for MediaSource objects.
     */
    public static Builder getBuilder()
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
         * Sets the media type for the media source being built.
         */
        public void setType(MediaType t)
        {
            type = t;
        }

        /**
         * Sets the ID for the media source being built.
         */
        public void setId(String id)
        {
            this.id = id;
        }

        /**
         * The <tt>source</tt> elements defined by XEP-0339: Source-Specific
         * Media Attributes associated with this <tt>media-source</tt>.
         */
        private final List<SourcePacketExtension> sources
            = new ArrayList<>();

        /**
         * The <tt>ssrc-group</tt> elements defined by XEP-0339: Source-Specific
         * Media Attributes associated with this <tt>media-source</tt>.
         */
        private final List<SourceGroupPacketExtension> ssrcGroups
            = new ArrayList<>();

        /**
         * Adds a payload type to the media being built.
         */
        public void addSource(SourcePacketExtension pt)
        {
            sources.add(pt);
        }

        /**
         * Adds an RTP header extension to the media being built.
         */
        public void addSsrcGroup(SourceGroupPacketExtension ext)
        {
            ssrcGroups.add(ext);
        }

        /* TODO: add something to set values from higher-level Jingle structures. */

        private Builder()
        {
        }

        public MediaSource build()
        {
            return new MediaSource(this);
        }
    }
}
