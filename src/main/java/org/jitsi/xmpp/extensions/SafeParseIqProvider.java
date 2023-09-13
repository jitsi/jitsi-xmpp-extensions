/*
 * Copyright @ 2023 - present 8x8, Inc.
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

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Catch all unexpected exceptions while parsing and convert them to a {@link SmackParsingException}.
 *
 * Implementations should override either {@link #doParse(XmlPullParser)} or
 * {@link #doParse(XmlPullParser, int, IqData, XmlEnvironment)} if they need the extra parameters.
 */
public abstract class SafeParseIqProvider<I extends IQ> extends IqProvider<I>
{
    @Override
    public I parse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        try
        {
            return doParse(parser, initialDepth, iqData, xmlEnvironment);
        }
        catch (XmlPullParserException | IOException | SmackParsingException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new OtherSmackParsingException(e);
        }
    }

    /**
     * Default implementation which ignores all params except {@code parser}.
     * Override if the extra parameters are needed.
     */
    protected I doParse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment)
        throws Exception
    {
        return doParse(parser);
    }

    /**
     * Override this method if the extra parameters are not needed.
     */
    protected I doParse(XmlPullParser parser)
        throws Exception
    {
        throw new SmackParsingException("Not implemented");
    }

    static class OtherSmackParsingException extends SmackParsingException
    {
        OtherSmackParsingException(Exception e)
        {
            super(e);
        }
    }
}
