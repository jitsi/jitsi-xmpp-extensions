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
 * An 'rtcp-mux' extension.
 * @author Boris Grozev
 */
public class RtcpmuxPacketExtension
        extends AbstractPacketExtension
{
    /**
     * The name of the "encryption" element.
     */
    public static final String ELEMENT_NAME = "rtcp-mux";

    /**
     * Creates a new instance of <tt>RtcpmuxPacketExtension</tt>.
     */
    public RtcpmuxPacketExtension()
    {
        super(null, ELEMENT_NAME);
    }
}

