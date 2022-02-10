/*
 * Copyright @ 2022 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.colibri2;

import org.jetbrains.annotations.*;
import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.xml.*;

import javax.xml.namespace.*;
import java.io.*;

public class Colibri2Error
        extends AbstractPacketExtension
{
    /**
     * The XML element name of the {@link Colibri2Error} element.
     */
    public static final String ELEMENT = "error";

    /**
     * The XML namespace of the {@link Colibri2Error} element
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    public static final String REASON_ATTR_NAME = "reason";

    public Colibri2Error(Reason reason)
    {
        super(NAMESPACE, ELEMENT);
        setAttribute(REASON_ATTR_NAME, reason.toString().toLowerCase());
    }

    public Colibri2Error()
    {
        this(Reason.UNSPECIFIED);
    }

    @NotNull
    public Reason getReason()
    {
        return Reason.parseString(getAttributeAsString(REASON_ATTR_NAME));
    }

    protected static class Provider extends DefaultPacketExtensionProvider<Colibri2Error>
    {
        public Provider()
        {
            super(Colibri2Error.class);
        }


        @Override
        public Colibri2Error parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
                throws XmlPullParserException, IOException, SmackParsingException
        {
            Colibri2Error colibri2Error = super.parse(parser, depth, xmlEnvironment);

            /* Validate parameters */
            try
            {
                Reason.parseString(colibri2Error.getAttributeAsString(REASON_ATTR_NAME));
            }
            catch (IllegalArgumentException e)
            {
                throw new SmackParsingException(
                        "Invalid value for the '" + REASON_ATTR_NAME + "' attribute: "
                                + colibri2Error.getAttributeAsString(REASON_ATTR_NAME));
            }

            return colibri2Error;
        }
    }

    public enum Reason
    {
        CONFERENCE_NOT_FOUND,
        CONFERENCE_ALREADY_EXISTS,
        UNSPECIFIED;

        @NotNull
        public static Reason parseString(String s)
                throws IllegalArgumentException
        {
            if (s == null)
            {
                return UNSPECIFIED;
            }

            return Reason.valueOf(s.toUpperCase());
        }
    }
}
