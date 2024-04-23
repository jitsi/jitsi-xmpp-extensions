/*
 * Copyright @ 2024 - present 8x8, Inc.
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
package org.jitsi.xmpp

import org.jitsi.utils.logging2.createLogger
import org.jitsi.xmpp.stringprep.JitsiXmppStringprep
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.parsing.ExceptionLoggingCallback
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Proxy
import org.jxmpp.JxmppContext
import org.jxmpp.jid.impl.JidCreate

object Smack {
    val logger = createLogger()

    fun initialize() {
        logger.info("Setting XML parsing limits.")
        System.setProperty("jdk.xml.entityExpansionLimit", "0")
        System.setProperty("jdk.xml.maxOccurLimit", "0")
        System.setProperty("jdk.xml.elementAttributeLimit", "524288")
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0")
        System.setProperty("jdk.xml.maxXMLNameLimit", "524288")
        System.setProperty("jdk.xml.entityReplacementLimit", "0")

        // Force XmppStringPrepUtil to load before we override the context, otherwise it gets reverted.
        // https://github.com/igniterealtime/jxmpp/pull/44
        JidCreate.from("example")
        logger.info("Using JitsiXmppStringprep.")
        JxmppContext.setDefaultXmppStringprep(JitsiXmppStringprep.INSTANCE)

        // if there is a parsing error, do not break the connection to the server(the default behaviour) as we need
        // it for the other conferences.
        SmackConfiguration.setDefaultParsingExceptionCallback(ExceptionLoggingCallback())
        Socks5Proxy.setLocalSocks5ProxyEnabled(false)
    }
}
