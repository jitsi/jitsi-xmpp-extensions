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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.jxmpp.jid.impl.*;

import java.io.*;
import org.xmlunit.builder.*;
import org.xmlunit.diff.*;

/**
 * Playground for testing {@link MuteIq} parsing.
 *
 * @author Pawel Domas
 */
public class MuteIqProviderTest
{
    private final XmlEnvironment jabberClientNs = new XmlEnvironment("jabber:client");

    @Test
    public void testParseIq()
        throws Exception
    {
        String iqXml =
            "<iq to='t' from='f' type='set'>" +
                "<mute xmlns='http://jitsi.org/jitmeet/audio'" +
                     " jid='somejid' >" +
                "true" +
                "</mute>" +
                "</iq>";

        MuteIqProvider provider = new MuteIqProvider();
        MuteIq mute = IQUtils.parse(iqXml, provider);

        assertEquals("f", mute.getFrom().toString());
        assertEquals("t", mute.getTo().toString());

        assertEquals("somejid", mute.getJid().toString());

        assertEquals(true, mute.getMute());
    }

    @Test
    public void testToXml()
            throws IOException
    {
        MuteIq muteIq = new MuteIq();

        muteIq.setStanzaId("123xyz");
        muteIq.setTo(JidCreate.from("toJid"));
        muteIq.setFrom(JidCreate.from("fromJid"));

        muteIq.setJid(JidCreate.from("mucjid1234"));
        muteIq.setMute(true);

        Diff diff = DiffBuilder
            .compare("<iq to='tojid' from='fromjid' " +
                         "type='get' id='123xyz'>" +
                         "<mute " +
                         "xmlns='http://jitsi.org/jitmeet/audio' " +
                         "jid='mucjid1234'" +
                         ">true</mute>" +
                         "</iq>")
            .withTest(muteIq.toXML(jabberClientNs).toString())
            .checkForIdentical()
            .build();
        Assertions.assertFalse(diff.hasDifferences(), diff.toString());
    }
}
