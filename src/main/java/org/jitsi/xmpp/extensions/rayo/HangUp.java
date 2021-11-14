/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2015-Present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.rayo;

import org.jxmpp.jid.*;

/**
 * Rayo hangup IQ is sent by the controlling agent to tell the server that call
 * whose resource is mentioned in IQ's 'to' attribute should be terminated.
 * Server immediately replies with result IQ which means that hangup operation
 * is now scheduled. After it is actually executed presence indication with
 * {@link EndExtension} is sent through the presence to confirm the operation.
 */
public class HangUp
    extends RayoIq
{
    /**
     * The name of 'hangup' element.
     */
    public static final String ELEMENT = "hangup";

    /**
     * XML namespace of this IQ.
     */
    public static final String NAMESPACE = RayoIqProvider.NAMESPACE;

    /**
     * Creates new instance of <tt>HangUp</tt> IQ.
     */
    protected HangUp()
    {
        super(ELEMENT);
    }

    /**
     * Creates new, parametrized instance of {@link HangUp} IQ.
     *
     * @param from source JID.
     * @param to   the destination address/call URI to be ended by this IQ.
     * @return new, parametrized instance of {@link HangUp} IQ.
     */
    public static HangUp create(Jid from, Jid to)
    {
        HangUp hangUp = new HangUp();
        hangUp.setFrom(from);
        hangUp.setTo(to);
        hangUp.setType(Type.set);

        return hangUp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQChildElementXmlStringBuilder xml)
    {
        xml.setEmptyElement();
        return xml;
    }
}
