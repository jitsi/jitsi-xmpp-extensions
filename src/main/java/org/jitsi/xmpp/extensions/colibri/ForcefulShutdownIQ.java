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
package org.jitsi.xmpp.extensions.colibri;

import org.jivesoftware.smack.packet.*;

/**
 * The IQ used to trigger the forceful shutdown mode of the videobridge which
 * receives the stanza (given that source JID is authorized to do so).
 *
 * @author Pawel Domas
 */
public class ForcefulShutdownIQ
    extends IQ
{
    /**
     * XML namespace name for shutdown IQs.
     */
    final static public String NAMESPACE = ColibriConferenceIQ.NAMESPACE;

    /**
     * Force shutdown IQ element name.
     */
    final static public String ELEMENT = "force-shutdown";

    public ForcefulShutdownIQ()
    {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQChildElementXmlStringBuilder buf)
    {
        buf.setEmptyElement();
        return buf;
    }
}
