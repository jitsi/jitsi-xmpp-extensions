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
package org.jitsi.xmpp.extensions.coin;

import org.jitsi.xmpp.extensions.*;

/**
 * Conference media packet extension.
 *
 * @author Sebastien Vincent
 */
public class ConferenceMediaPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that conference media belongs to.
     */
    public static final String NAMESPACE = CoinIQ.NAMESPACE;

    /**
     * The name of the element that contains the conference media.
     */
    public static final String ELEMENT = "available-media";

    /**
     * Constructor.
     */
    public ConferenceMediaPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }
}
