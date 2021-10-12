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

import org.jitsi.xmpp.extensions.*;

import javax.xml.namespace.*;
import java.util.*;

/**
 * A colibri2 sources element
 */
public class Sources
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 Sources element.
     */
    public static final String ELEMENT = "sources";

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
     * Construct a MediaSource.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Sources()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct a source from a builder - used by Builder#build().
     */
    private Sources(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        for (MediaSource ms: b.mediaSources) {
            super.addChildExtension(ms);
        }
    }

    /**
     * Get the sources of this media source.
     */
    public List<MediaSource> getMediaSources()
    {
        return super.getChildExtensionsOfType(MediaSource.class);
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
         * The <tt>meida-source</tt> elements included in this <tt>sources</tt>.
         */
        private final List<MediaSource> mediaSources
            = new ArrayList<>();

        /**
         * Add  a payload type to the media being built.
         */
        public void addMediaSource(MediaSource pt)
        {
            mediaSources.add(pt);
        }


        /* TODO: add something to set values from higher-level Jingle structures. */

        private Builder()
        {
        }

        public Sources build()
        {
            return new Sources(this);
        }
    }

}
