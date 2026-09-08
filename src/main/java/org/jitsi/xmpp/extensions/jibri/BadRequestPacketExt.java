/*
 * Copyright @ 2026 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.jibri;

import javax.xml.namespace.*;

import org.jitsi.xmpp.extensions.*;
import org.jivesoftware.smack.provider.*;

/**
 * Included by Jibri in a response to a start request to indicate that the request itself is invalid, for example
 * because a parameter has a bad value. No Jibri instance can serve such a request, so it must not be retried.
 *
 * The element text, when present, describes what is wrong with the request. It must not contain a credential such as
 * a stream key, because it is included in responses and logs.
 *
 * Jibri only sends this when the start request set {@link JibriIq#getSupportsBadRequest()}, so a Jicofo which does not
 * understand it never receives it.
 */
public class BadRequestPacketExt
    extends AbstractPacketExtension
{
    /**
     * The namespace of this packet extension.
     */
    public static final String NAMESPACE = JibriIq.NAMESPACE;

    /**
     * XML element name of this packet extension.
     */
    public static final String ELEMENT = "bad-request";

    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * Creates a new instance with no detail.
     */
    public BadRequestPacketExt()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Creates a new instance which describes what is wrong with the request.
     *
     * @param detail a description of the problem. It must not contain a credential.
     */
    public BadRequestPacketExt(String detail)
    {
        this();
        setDetail(detail);
    }

    static public void registerExtensionProvider()
    {
        ProviderManager.addExtensionProvider(
                ELEMENT,
                NAMESPACE,
                new DefaultPacketExtensionProvider<>(BadRequestPacketExt.class)
        );
    }

    /**
     * @return a description of what is wrong with the request, or {@code null} if none was given.
     */
    public String getDetail()
    {
        return getText();
    }

    /**
     * Sets a description of what is wrong with the request. It must not contain a credential.
     */
    public void setDetail(String detail)
    {
        setText(detail);
    }
}
