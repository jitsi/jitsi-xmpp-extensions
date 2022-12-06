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
package org.jitsi.xmpp.extensions.jitsimeet;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.junit.jupiter.api.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.stringprep.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

/**
 * Tests for {@link ConferenceIqProvider}.
 *
 * @author Pawel Domas
 */
public class ConferenceIqProviderTest
{
    private final XmlEnvironment jabberClientNs =
        new XmlEnvironment("jabber:client");

    @Test
    public void testParseConferenceIq()
        throws Exception
    {
        // ConferenceIq
        String iqXml =
            "<iq to='t' from='f' type='set'>" +
                "<conference xmlns='http://jitsi.org/protocol/focus'" +
                " room='someroom@example.com' ready='true'" +
                ">" +
                "<property xmlns='http://jitsi.org/protocol/focus' " +
                "name='name1' value='value1'/>" +
                "<property name='name2' value='value2'/>" +
                "</conference>" +
                "</iq>";

        ConferenceIqProvider provider = new ConferenceIqProvider();
        ConferenceIq conference
            = IQUtils.parse(iqXml, provider);

        Assertions.assertEquals("someroom@example.com",
            conference.getRoom().toString());
        Assertions.assertEquals(true, conference.isReady());

        List<ConferenceIq.Property> properties = conference.getProperties();
        Assertions.assertEquals(2, properties.size());

        ConferenceIq.Property property1 = properties.get(0);
        Assertions.assertEquals("name1", property1.getName());
        Assertions.assertEquals("value1", property1.getValue());

        ConferenceIq.Property property2 = properties.get(1);
        Assertions.assertEquals("name2", property2.getName());
        Assertions.assertEquals("value2", property2.getValue());
    }

    @Test
    public void testParseConferenceIqWithWrongRoom()
    {
        // ConferenceIq
        String iqXml =
            "<iq to='t' from='f' type='set'>" +
                "<conference xmlns='http://jitsi.org/protocol/focus'" +
                " room='somename@email.com@example.com' ready='true'>" +
                "</conference>" +
                "</iq>";

        // we expect that an exception will be thrown
        Exception resultException = null;
        try
        {
            ConferenceIqProvider provider = new ConferenceIqProvider();
            IQUtils.parse(iqXml, provider);
        }
        catch (Exception e)
        {
            resultException = e;
        }

        Assertions.assertNotNull(resultException);

        // we expect XmppStringprepException
        Assertions.assertEquals(XmppStringprepException.class,
            resultException.getClass());
    }

    @Test
    public void testParseLoginUrlIq()
        throws Exception
    {
        String originalUrl = "somesdf23454$%12!://";
        String encodedUrl = URLEncoder.encode(originalUrl, "UTF8");

        // AuthUrlIq
        String authUrlIqXml = "<iq to='to1' from='from3' type='result'>" +
            "<login-url xmlns='http://jitsi.org/protocol/focus'" +
            " url='" + encodedUrl
            + "' room='someroom1234@example.com' />" +
            "</iq>";

        LoginUrlIq authUrlIq
            = IQUtils.parse(authUrlIqXml, new LoginUrlIqProvider());

        Assertions.assertNotNull(authUrlIq);
        Assertions.assertEquals("to1", authUrlIq.getTo().toString());
        Assertions.assertEquals("from3", authUrlIq.getFrom().toString());
        Assertions.assertEquals(IQ.Type.result, authUrlIq.getType());
        Assertions.assertEquals(originalUrl, authUrlIq.getUrl());
        Assertions.assertEquals("someroom1234@example.com",
            authUrlIq.getRoom().toString());
    }

    @Test
    public void testConferenceIqToXml()
        throws IOException
    {
        ConferenceIq conferenceIq = new ConferenceIq();

        conferenceIq.setStanzaId("123xyz");
        conferenceIq.setTo(JidCreate.from("toJid@example.com"));
        conferenceIq.setFrom(JidCreate.from("fromJid@example.com"));

        conferenceIq.setRoom(JidCreate.entityBareFrom("testroom1234@example.com"));
        conferenceIq.setVnode("v1");
        conferenceIq.setReady(false);
        conferenceIq.addProperty(
            new ConferenceIq.Property("prop1", "some1"));
        conferenceIq.addProperty(
            new ConferenceIq.Property("name2", "xyz2"));

        Diff diff = DiffBuilder.compare(
                "<iq to='tojid@example.com' from='fromjid@example.com' id='123xyz' type='get'>" +
                    "<conference xmlns='http://jitsi.org/protocol/focus' " +
                        "room='testroom1234@example.com' ready='false' vnode='v1'>" +
                        "<property name='prop1' value='some1'/>" +
                        "<property name='name2' value='xyz2'/>" +
                    "</conference>" +
                "</iq>")
            .withTest(conferenceIq.toXML(jabberClientNs).toString())
            .checkForIdentical()
            .build();
        Assertions.assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void testLoginUrlIqToXml()
        throws UnsupportedEncodingException, XmppStringprepException
    {
        LoginUrlIq authUrlIQ = new LoginUrlIq();

        authUrlIQ.setStanzaId("1df:234sadf");
        authUrlIQ.setTo(JidCreate.from("to657@example.com"));
        authUrlIQ.setFrom(JidCreate.from("23from2134#@1"));
        authUrlIQ.setType(IQ.Type.result);

        authUrlIQ.setUrl("url://dsf78645!!@3fsd&");
        authUrlIQ.setRoom(JidCreate.entityBareFrom("room@sdaf.dsf.dsf"));

        String encodedUrl = URLEncoder.encode(authUrlIQ.getUrl(), "UTF8");

        Assertions.assertEquals("<iq to='to657@example.com' " +
            "from='23from2134#@1' id='1df:234sadf' " +
            "type='result'>" +
            "<login-url " +
            "xmlns='http://jitsi.org/protocol/focus' " +
            "url='" + encodedUrl + "' " +
            "room='room@sdaf.dsf.dsf'" +
            "/>" +
            "</iq>", authUrlIQ.toXML(jabberClientNs).toString());
    }
}
