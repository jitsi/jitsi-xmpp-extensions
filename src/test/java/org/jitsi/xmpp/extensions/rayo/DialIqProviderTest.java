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
import org.jivesoftware.smack.packet.*;
import org.junit.jupiter.api.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.stringprep.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

/**
 * Tests DialIQ parsing.
 *
 * @author Pawel Domas
 */
public class DialIqProviderTest
{
    @Test
    public void testParseDial()
        throws Exception
    {
        String src = "somesource";
        String dst = "somedestination";
        String iqXml = getDialIqXML(dst, src);

        RayoIqProvider provider = new RayoIqProvider();
        DialIq dialIq
            = (DialIq) IQUtils.parse(iqXml, provider);

        assertEquals(src, dialIq.getSource());
        assertEquals(dst, dialIq.getDestination());

        Assertions.assertNotNull(
            IQUtils.parse(getDialIqXML("to", ""), provider));

        // "to" attribute is mandatory for SIP gateway
        // "from" is optional(might be used to select source SIP account)
        // otherwise default one will be used.
        Assertions.assertNull(
            IQUtils.parse(getDialIqXML("", "from"), provider));

        Assertions.assertNull(IQUtils.parse(getDialIqXML("", ""), provider));

        Assertions.assertNull(
            IQUtils.parse(getDialIqXML(null, null), provider));
    }

    private String getDialIqXML(String to, String from)
        throws XmppStringprepException
    {
        DialIq iq = DialIq.create(to, from);
        iq.setFrom(JidCreate.from("from@example.com"));
        iq.setTo(JidCreate.from("to@example.org"));
        return iq.toXML().toString();
    }

    @Test
    public void testDialToString()
    {
        String src = "from23dfsr";
        String dst = "to123213";

        DialIq dialIq = DialIq.create(dst, src);

        String id = dialIq.getStanzaId();
        String type = dialIq.getType().toString();

        Diff diff = DiffBuilder.compare(
                String.format(
                    "<iq id=\"%s\" type=\"%s\" xmlns=\"jabber:client\">" +
                        "<dial xmlns='urn:xmpp:rayo:1'" +
                        " from='%s' to='%s' />" +
                        "</iq>",
                    id, type, src, dst))
            .withTest(dialIq.toXML().toString())
            .checkForIdentical()
            .build();
        Assertions.assertFalse(diff.hasDifferences(), diff.toString());

        dialIq.setHeader("h1", "v1");

        diff = DiffBuilder.compare(
                String.format(
                    "<iq id=\"%s\" type=\"%s\" xmlns=\"jabber:client\">" +
                        "<dial xmlns='urn:xmpp:rayo:1' from='%s' to='%s' >" +
                        "<header  name='h1' value='v1'/>" +
                        "</dial>" +
                        "</iq>",
                    id, type, src, dst))
            .withTest(dialIq.toXML().toString())
            .checkForIdentical()
            .build();
        Assertions.assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void testParseHeaders()
        throws Exception
    {
        String dialIqXml =
            "<iq id='123' type='set' from='fromJid' to='toJid' >" +
                "<dial xmlns='urn:xmpp:rayo:1' from='source' to='dest'>" +
                "<header name='h1' value='v1' />" +
                "<header name='h2' value='v2' />" +
                "</dial>" +
                "</iq>";

        DialIq iq
            = (DialIq) IQUtils.parse(
            dialIqXml, new RayoIqProvider());

        // IQ
        assertEquals("123", iq.getStanzaId());
        assertEquals(IQ.Type.set, iq.getType());
        assertEquals("fromjid", iq.getFrom().toString());
        assertEquals("tojid", iq.getTo().toString());
        // Dial
        assertEquals("source", iq.getSource());
        assertEquals("dest", iq.getDestination());
        // Header
        assertEquals("v1", iq.getHeader("h1"));
        assertEquals("v2", iq.getHeader("h2"));
    }
}
