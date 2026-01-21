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

package org.jitsi.xmpp.extensions.colibri;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.provider.*;
import org.xmlpull.v1.*;

public class JvbApiIqProvider extends IQProvider<JvbApiIq>
{

    @Override
    public JvbApiIq parse(XmlPullParser parser, int initialDepth) throws Exception
    {
        if (!JvbApiIq.NAMESPACE.equals(parser.getNamespace()))
        {
            return null;
        }
        if (!JvbApiIq.ELEMENT_NAME.equals(parser.getName()))
        {
            return null;
        }
        JvbApiIq iq = new JvbApiIq();

        boolean done = false;
        int eventType = parser.next();
        if (eventType == XmlPullParser.START_TAG)
        {
            String name = parser.getName();
            if (name.equals(JsonPacketExtension.ELEMENT_NAME))
            {
                ExtensionElementProvider<JsonPacketExtension> jsonProvider =
                    new DefaultPacketExtensionProvider<>(JsonPacketExtension.class);
                JsonPacketExtension jsonPE = jsonProvider.parse(parser);
                iq.addExtension(jsonPE);
            }
        }




        return iq;
    }
}
