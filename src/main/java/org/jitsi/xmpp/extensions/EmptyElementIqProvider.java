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

import java.io.*;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;
import org.jivesoftware.smack.xml.XmlPullParser.*;

/**
 * Implements an {@link IqProvider} for empty elements.
 */
public abstract class EmptyElementIqProvider<T extends IQ>
    extends SafeParseIqProvider<T>
{
    private final String element;

    private final String namespace;

    protected EmptyElementIqProvider(String element, String namespace)
    {
        this.element = element;
        this.namespace = namespace;
    }

    protected abstract T createInstance();

    /**
     * Parses an IQ sub-document and creates an
     * <tt>org.jivesoftware.smack.packet.IQ</tt> instance.
     *
     * @param parser an <tt>XmlPullParser</tt> which specifies the IQ
     *               sub-document to be parsed into a new <tt>IQ</tt> instance
     * @return a new <tt>IQ</tt> instance parsed from the specified IQ
     * sub-document
     */
    @Override
    protected T doParse(XmlPullParser parser, int initialDepth,
        IqData data, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException
    {
        T iq;

        if (namespace.equals(parser.getNamespace())
            && element.equals(parser.getName()))
        {
            String rootElement = parser.getName();
            iq = createInstance();

            boolean done = false;
            while (!done)
            {
                if (parser.next() == Event.END_ELEMENT)
                {
                    String name = parser.getName();
                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                }
            }
        }
        else
        {
            iq = null;
        }

        return iq;
    }
}
