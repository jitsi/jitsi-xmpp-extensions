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

/**
 * A representation of the <tt>extmap-allow-mixed</tt> element used in RTP
 * <tt>description</tt> elements.  Has no attributes or sub-elements.
 */
public class ExtmapAllowMixedPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The name of the "extmap-allow-mixed" element.
     */
    public static final String ELEMENT = "extmap-allow-mixed";

    /**
     * XML namespace for this extension.
     */
    public static final String NAMESPACE
        = RTPHdrExtPacketExtension.NAMESPACE;

    /**
     * Creates a new {@link ExtmapAllowMixedPacketExtension} instance.
     */
    public ExtmapAllowMixedPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }
}
