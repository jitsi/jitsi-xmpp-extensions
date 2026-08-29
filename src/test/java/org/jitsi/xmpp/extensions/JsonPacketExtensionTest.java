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

package org.jitsi.xmpp.extensions;

import junit.framework.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.util.*;
import org.xmlpull.v1.*;

public class JsonPacketExtensionTest
    extends TestCase
{

    public void testSimple()
    {
        JsonPacketExtension ext = new JsonPacketExtension("{}");

        assertEquals(JsonPacketExtension.ELEMENT_NAME, ext.getElementName());
        assertEquals("<json>{}</json>", ext.toXML());
        assertEquals("{}", ext.getJsonBody());
    }

    /**
     * Verify that if the JSON includes special characters (i.e. '<' or '>'),
     * it still works correctly
     */
    public void testJsonWithSpecialCharacters()
    {
        String json = "" +
        "{" +
            "foo: a < b," +
            "bar: [1, 2, 3]," +
            "baz: '<=>'" +
        "}";
        JsonPacketExtension ext = new JsonPacketExtension(json);
        assertEquals("<json>{foo: a &lt; b,bar: [1, 2, 3],baz: &apos;&lt;=&gt;&apos;}</json>", ext.toXML());
        assertEquals("{foo: a < b,bar: [1, 2, 3],baz: '<=>'}", ext.getJsonBody());
    }

    public void testParse() throws Exception
    {
        String json = "" +
            "{" +
            "foo: a < b," +
            "bar: [1, 2, 3]," +
            "baz: '<=>'" +
            "}";
        JsonPacketExtension ext = new JsonPacketExtension(json);
        String xml = ext.toXML();

        ExtensionElementProvider<JsonPacketExtension> jsonProvider =
            new DefaultPacketExtensionProvider<>(JsonPacketExtension.class);
        XmlPullParser parser = PacketParserUtils.getParserFor(xml);
        JsonPacketExtension parsed = jsonProvider.parse(parser);

        assertEquals("{foo: a < b,bar: [1, 2, 3],baz: '<=>'}", parsed.getJsonBody());
        System.out.println(parsed.getJsonBody());
    }
}