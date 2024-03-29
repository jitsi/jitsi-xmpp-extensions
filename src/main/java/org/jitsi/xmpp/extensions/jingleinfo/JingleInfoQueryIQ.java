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
package org.jitsi.xmpp.extensions.jingleinfo;

import org.jivesoftware.smack.packet.*;

/**
 * The <tt>JingleInfoQueryIQ</tt> is used to discover STUN and relay server via
 * the Google's Jingle Server Discovery extension.
 *
 * @author Sebastien Vincent
 */
public class JingleInfoQueryIQ
    extends IQ
{
    /**
     * The namespace.
     */
    public static final String NAMESPACE = "google:jingleinfo";

    /**
     * The element name.
     */
    public static final String ELEMENT = "query";

    public JingleInfoQueryIQ()
    {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * Returns the sub-element XML section of the IQ packet, or null if
     * there isn't one. Packet extensions must be included, if any are defined.
     *
     * @return the child element section of the IQ XML.
     */
    @Override
    protected IQ.IQChildElementXmlStringBuilder getIQChildElementBuilder(IQ.IQChildElementXmlStringBuilder bld)
    {
        bld.setEmptyElement();
        return bld;
    }
}
