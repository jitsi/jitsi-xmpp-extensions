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
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jetbrains.annotations.*;
import org.jitsi.xmpp.extensions.*;

import org.jivesoftware.smack.xml.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;

public abstract class AbstractMuteIqProvider<T extends AbstractMuteIq>
    extends SafeParseIqProvider<T>
{
    @NotNull
    private final String namespace;

    public AbstractMuteIqProvider(@NotNull String namespace)
    {
        this.namespace = namespace;
    }

    abstract protected T createMuteIq();

    /**
     * {@inheritDoc}
     */
    @Override
    protected T doParse(XmlPullParser parser)
        throws Exception
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!this.namespace.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();

        T iq;

        if (AbstractMuteIq.ELEMENT.equals(rootElement))
        {
            iq = createMuteIq();
            String jidStr = parser.getAttributeValue("", AbstractMuteIq.JID_ATTR_NAME);
            if (jidStr != null)
            {
                Jid jid = JidCreate.from(jidStr);
                iq.setJid(jid);
            }

            String actorStr = parser.getAttributeValue("", AbstractMuteIq.ACTOR_ATTR_NAME);
            if (actorStr != null)
            {
                Jid actor = JidCreate.from(actorStr);
                iq.setActor(actor);
            }
        }
        else
        {
            return null;
        }

        boolean done = false;

        while (!done)
        {
            switch (parser.next())
            {
                case END_ELEMENT:
                {
                    String name = parser.getName();

                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                    break;
                }

                case TEXT_CHARACTERS:
                {
                    Boolean mute = Boolean.parseBoolean(parser.getText());
                    iq.setMute(mute);
                    break;
                }
            }
        }

        return iq;
    }
}
