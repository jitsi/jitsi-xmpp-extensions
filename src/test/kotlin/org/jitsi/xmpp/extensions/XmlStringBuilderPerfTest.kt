/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2024-Present 8x8, Inc.
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
package org.jitsi.xmpp.extensions

import io.kotest.core.spec.style.ShouldSpec
import org.jitsi.utils.logging2.createLogger
import org.jivesoftware.smack.util.Supplier
import org.jivesoftware.smack.util.XmlStringBuilder

/**
 * A test for the performance of XmlStringBuilder serialization. See https://github.com/igniterealtime/Smack/pull/569
 */
class XmlStringBuilderPerfTest : ShouldSpec() {
    val countOuter: Int = 500
    val countInner: Int = 50
    val logger = createLogger()

    init {
        xcontext("XmlStringBuilder.toString() performance") {
            test1()
            test2()
            test3()
        }
    }

    private fun test1() {
        logger.info("Test 1")
        val parent = XmlStringBuilder()
        val child = XmlStringBuilder()
        val child2 = XmlStringBuilder()

        for (i in 1 until countOuter) {
            val cs = XmlStringBuilder()
            for (j in 0 until countInner) {
                cs.append("abc")
            }
            child2.append(cs as CharSequence)
        }

        child.append(child2 as CharSequence)
        parent.append(child as CharSequence)

        time("test1: parent") { "len=" + parent.toString().length }
        time("test1: child") { "len=" + child.toString().length }
        time("test1: child2") { "len=" + child2.toString().length }
    }

    private fun test2() {
        logger.info("Test 2: evaluate children first")
        val parent = XmlStringBuilder()
        val child = XmlStringBuilder()
        val child2 = XmlStringBuilder()

        for (i in 1 until countOuter) {
            val cs = XmlStringBuilder()
            for (j in 0 until countInner) {
                cs.append("abc")
            }
            child2.append(cs as CharSequence)
        }

        child.append(child2 as CharSequence)
        parent.append(child as CharSequence)

        time("test2: child2") { "len=" + child2.toString().length }
        time("test2: child") { "len=" + child.toString().length }
        time("test2: parent") { "len=" + parent.toString().length }
    }

    private fun test3() {
        logger.info("Test 3: use append(XmlStringBuilder)")
        val parent = XmlStringBuilder()
        val child = XmlStringBuilder()
        val child2 = XmlStringBuilder()

        for (i in 1 until countOuter) {
            val cs = XmlStringBuilder()
            for (j in 0 until countInner) {
                cs.append("abc")
            }
            child2.append(cs)
        }

        child.append(child2)
        parent.append(child)

        time("test3: parent") { "len=" + parent.toString().length }
        time("test3: child") { "len=" + child.toString().length }
        time("test3: child2") { "len=" + child2.toString().length }
    }

    fun time(name: String, block: Supplier<String>) {
        val start = System.currentTimeMillis()
        val result = block.get()
        val end = System.currentTimeMillis()

        logger.info(name + " took " + (end - start) + "ms: " + result)
    }
}
