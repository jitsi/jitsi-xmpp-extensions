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
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.util.*;
import org.xmlpull.v1.*;

public class JvbApiIqProviderTest extends TestCase
{
    private final JvbApiIqProvider jvbApiIqProvider = new JvbApiIqProvider();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        ProviderManager.addIQProvider(
            JvbApiIq.ELEMENT_NAME,
            JvbApiIq.NAMESPACE,
            jvbApiIqProvider
        );
    }

    public void testSimple() throws Exception
    {
        String xml = "" +
            "<jvb-api xmlns='http://jitsi.org/protocol/colibri/v2'/>";

        XmlPullParser parser = PacketParserUtils.getParserFor(xml);
        JvbApiIq parsed = jvbApiIqProvider.parse(parser);
        assertTrue(parsed.toXML().toString().contains(xml));
    }

    public void testWithJson() throws Exception
    {
        String xml = "" +
            "<jvb-api xmlns='http://jitsi.org/protocol/colibri/v2'><json>{}</json></jvb-api>";
        XmlPullParser parser = PacketParserUtils.getParserFor(xml);
        JvbApiIq parsed = jvbApiIqProvider.parse(parser);
        assertTrue(parsed.toXML().toString().contains(xml));
        assertNotNull(parsed.getJsonContent());
        assertEquals("{}", parsed.getJsonContent());
    }
}