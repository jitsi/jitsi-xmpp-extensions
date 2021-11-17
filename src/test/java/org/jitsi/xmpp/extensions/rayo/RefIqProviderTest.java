/*
 * Copyright @ 2015 - Present 8x8, Inc.
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

import static org.junit.jupiter.api.Assertions.*;

import org.jitsi.xmpp.extensions.*;
import org.junit.jupiter.api.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.stringprep.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

/**
 * Tests parsing of RefIQs.
 *
 * @author Pawel Domas
 */
public class RefIqProviderTest
{
    @Test
    public void testParseRef()
        throws Exception
    {
        String uri = "someUri@fsjdo-54.trh56.4";
        String iqXml = getRefIqXML(uri);

        RayoIqProvider provider = new RayoIqProvider();
        RefIq dialIq
            = (RefIq) IQUtils.parse(iqXml, provider);

        assertEquals(uri, dialIq.getUri());

        assertNotNull(IQUtils.parse(getRefIqXML("someUri"), provider));

        assertNull(IQUtils.parse(getRefIqXML(""), provider));

        assertNull(IQUtils.parse(getRefIqXML(null), provider));
    }

    private String getRefIqXML(String uri) throws XmppStringprepException
    {
        RefIq iq = RefIq.create(uri);
        iq.setFrom(JidCreate.from("from@example.org"));
        iq.setTo(JidCreate.from("to@example.org"));
        return iq.toXML().toString();
    }

    @Test
    public void testRefToString()
    {
        String uri = "from23dfsr";

        RefIq refIq = RefIq.create(uri);

        String id = refIq.getStanzaId();
        String type = refIq.getType().toString();

        Diff diff = DiffBuilder.compare(String.format(
                "<iq id=\"%s\" type=\"%s\" xmlns=\"jabber:client\">" +
                    "<ref xmlns='urn:xmpp:rayo:1'" +
                    " uri='%s' />" +
                    "</iq>",
                id, type, uri))
            .withTest(refIq.toXML().toString())
            .checkForIdentical()
            .build();
        Assertions.assertFalse(diff.hasDifferences(), diff.toString());
    }
}
