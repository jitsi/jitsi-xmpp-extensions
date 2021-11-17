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
package org.jitsi.xmpp.extensions.health;

import org.jivesoftware.smack.packet.*;

/**
 * The health check IQ used to trigger health checks on the Jitsi Videobridge.
 *
 * @author Pawel Domas
 */
public class HealthCheckIQ
    extends IQ
{
    /**
     * Health check IQ element name.
     */
    public static final String ELEMENT = "healthcheck";

    /**
     * XML namespace name for health check IQs.
     */
    public static final String NAMESPACE =
        "http://jitsi.org/protocol/healthcheck";

    public HealthCheckIQ()
    {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQ.IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQ.IQChildElementXmlStringBuilder buf)
    {
        buf.setEmptyElement();
        return buf;
    }
}
