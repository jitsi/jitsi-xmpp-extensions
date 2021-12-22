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
     * The XML namespace of the {@link Sources} element.
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * Construct Sources.  Needs to be public for DefaultPacketExtensionProvider to work.
     */
    public Sources()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Construct sources from a builder - used by Builder#build().
     */
    private Sources(Builder b)
    {
        super(NAMESPACE, ELEMENT);

        for (MediaSource ms: b.mediaSources)
        {
            addChildExtension(ms);
        }
    }

    /**
     * Get the media sources.
     */
    public @NotNull List<MediaSource> getMediaSources()
    {
        return getChildExtensionsOfType(MediaSource.class);
    }

    /**
     * Get a builder for Sources objects.
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
         * The <tt>media-source</tt> elements included in this <tt>sources</tt>.
         */
        private final List<MediaSource> mediaSources
            = new ArrayList<>();

        /**
         * Add  a payload type to the media being built.
         */
        public Builder addMediaSource(MediaSource pt)
        {
            mediaSources.add(pt);
            return this;
        }


        /* TODO: add something to set values from higher-level Jingle structures. */

        private Builder()
        {
        }

        @Contract(" -> new")
        public @NotNull Sources build()
        {
            return new Sources(this);
        }
    }

}
