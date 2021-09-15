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
package org.jitsi.xmpp.extensions.inputevt;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Implements an <tt>IQProvider</tt> which parses incoming <tt>InputEvtIQ</tt>s.
 *
 * @author Sebastien Vincent
 */
public class InputEvtIQProvider
    extends IQProvider<InputEvtIQ>
{
    /**
     * Parse the Input IQ sub-document and returns the corresponding
     * <tt>InputEvtIQ</tt>.
     *
     * @param parser XML parser
     * @return <tt>InputEvtIQ</tt>
     * @throws Exception if something goes wrong during parsing
     */
    @Override
    public InputEvtIQ parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        InputEvtIQ inputEvtIQ = new InputEvtIQ();
        InputEvtAction action
            = InputEvtAction.parseString(
                    parser.getAttributeValue("", InputEvtIQ.ACTION_ATTR_NAME));

        inputEvtIQ.setAction(action);

        boolean done = false;

        while (!done)
        {
            switch (parser.next())
            {
            case START_ELEMENT:
                // <remote-control>
                if (RemoteControlExtensionProvider.ELEMENT_REMOTE_CONTROL
                        .equals(parser.getName()))
                {
                    RemoteControlExtensionProvider provider
                        = new RemoteControlExtensionProvider();
                    RemoteControlExtension item
                        = (RemoteControlExtension)
                            provider.parse(parser);

                    inputEvtIQ.addRemoteControl(item);
                }
                break;

            case END_ELEMENT:
                if (InputEvtIQ.ELEMENT_NAME.equals(parser.getName()))
                    done = true;
                break;
            }
        }

        return inputEvtIQ;
    }
}
