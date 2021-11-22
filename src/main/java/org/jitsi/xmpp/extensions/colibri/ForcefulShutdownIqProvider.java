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

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.provider.*;

/**
 * Implements an {@link IqProvider} for the Jitsi Videobridge extension {@link
 * GracefulShutdownIQ}.
 *
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class ForcefulShutdownIqProvider
    extends EmptyElementIqProvider<ForcefulShutdownIQ>
{
    /**
     * Registers this provider with Smack.
     */
    public static ForcefulShutdownIqProvider registerIQProvider()
    {
        // ColibriStatsIQ
        ForcefulShutdownIqProvider iqProvider =
            new ForcefulShutdownIqProvider();
        ProviderManager.addIQProvider(
            ForcefulShutdownIQ.ELEMENT,
            ForcefulShutdownIQ.NAMESPACE,
            iqProvider);
        return iqProvider;
    }

    public ForcefulShutdownIqProvider()
    {
        super(ForcefulShutdownIQ.ELEMENT, ForcefulShutdownIQ.NAMESPACE);
    }

    @Override
    protected ForcefulShutdownIQ createInstance()
    {
        return new ForcefulShutdownIQ();
    }
}
