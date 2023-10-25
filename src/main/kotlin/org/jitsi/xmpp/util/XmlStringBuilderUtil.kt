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
package org.jitsi.xmpp.util

import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.util.LazyStringBuilder
import org.jivesoftware.smack.util.XmlStringBuilder
import java.io.StringWriter

class XmlStringBuilderUtil {
    companion object {
        /**
         *  Avoid calling [XmlStringBuilder.toString] because it can be slow.
         *  TODO: remove once smack is fixed and updated: https://github.com/igniterealtime/Smack/pull/569
         */
        fun CharSequence.toStringOpt(): String = if (this is XmlStringBuilder) {
            StringWriter().apply {
                write(this, XmlEnvironment.EMPTY)
            }.toString()
        } else {
            toString()
        }

        @JvmStatic
        fun IQ.toStringOpt(): String = this.toXML().toStringOpt()

        @JvmStatic
        fun ExtensionElement.toStringOpt() = this.toXML().toStringOpt()

        /**
         *  Avoid using the generic [XmlStringBuilder.append] because it can be slow.
         *  TODO: remove once smack is fixed and updated: https://github.com/igniterealtime/Smack/pull/569
         */
        @JvmStatic
        fun XmlStringBuilder.append0(cs: CharSequence): XmlStringBuilder {
            when (cs) {
                is XmlStringBuilder -> {
                    append(cs)
                }

                is LazyStringBuilder -> {
                    append(cs)
                }

                else -> {
                    append(cs)
                }
            }
            return this
        }
    }
}
