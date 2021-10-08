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


import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Provider handles parsing of {@link ConferenceIq} and {@link LoginUrlIq}
 * stanzas and converting objects back to their XML representation.
 *
 * @author Pawel Domas
 */
public class LogoutIqProvider
    extends IQProvider<LogoutIq>
{
    /**
     * Creates new instance of <tt>ConferenceIqProvider</tt>.
     */
    public LogoutIqProvider()
    {
        //<logout>
        ProviderManager.addIQProvider(
            LogoutIq.ELEMENT, LogoutIq.NAMESPACE, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogoutIq parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!ConferenceIq.NAMESPACE.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();
        LogoutIq logoutIq;
        if (LogoutIq.ELEMENT.endsWith(rootElement))
        {
            logoutIq = new LogoutIq();

            String sessionId = parser.getAttributeValue(
                    "", LogoutIq.SESSION_ID_ATTR);

            if (StringUtils.isNotEmpty(sessionId))
            {
                logoutIq.setSessionId(sessionId);
            }

            String logoutUrl = parser.getAttributeValue(
                    "", LogoutIq.LOGOUT_URL_ATTR);

            if (StringUtils.isNotEmpty(logoutUrl))
            {
                logoutIq.setLogoutUrl(logoutUrl);
            }
        }
        else
        {
            return null;
        }

        return logoutIq;
    }
}
