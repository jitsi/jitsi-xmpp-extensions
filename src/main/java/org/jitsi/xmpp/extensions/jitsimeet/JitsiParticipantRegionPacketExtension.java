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
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jetbrains.annotations.*;
import org.jitsi.xmpp.extensions.*;

/**
 * A packet extension which contains an id of a region.
 *
 * @author Boris Grozev
 */
public class JitsiParticipantRegionPacketExtension
    extends AbstractPacketExtension
{
    /**
     * XML element name of this packet extension.
     */
    public static final String ELEMENT = "jitsi_participant_region";

    /**
     * XML namespace of this packet extension.
     */
    public static final String NAMESPACE = "jabber:client";

    /**
     * Creates new instance of <tt>EtherpadPacketExt</tt>.
     */
    public JitsiParticipantRegionPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * @return the value of the "id" attribute.
     */
    public @Nullable String getRegionId()
    {
        return getText();
    }

    /**
     * Sets the value for the region in the id attribute.
     */
    public void setRegionId(String value)
    {
        setText(value);
    }
}
