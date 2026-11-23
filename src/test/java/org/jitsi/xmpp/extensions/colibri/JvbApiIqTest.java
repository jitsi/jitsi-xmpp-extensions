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

import junit.framework.*;
import org.jitsi.xmpp.extensions.*;

public class JvbApiIqTest extends TestCase
{

    public void testSimple()
    {
        JvbApiIq iq = new JvbApiIq();
        assertTrue(iq.toXML().toString().contains("<jvb-api xmlns='http://jitsi.org/protocol/colibri/v2'/>"));
    }

    public void testJsonChild()
    {
        JvbApiIq iq = new JvbApiIq();
        JsonPacketExtension json = new JsonPacketExtension("{}");
        iq.addExtension(json);
        assertTrue(iq.toXML().toString().contains("<jvb-api xmlns='http://jitsi.org/protocol/colibri/v2'><json>{}</json></jvb-api>"));
    }
}