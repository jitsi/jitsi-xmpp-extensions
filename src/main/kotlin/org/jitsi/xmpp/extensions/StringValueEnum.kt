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
package org.jitsi.xmpp.extensions

/**
 * An enum whose entries have a string value which is used on the wire.
 */
interface StringValueEnum {
    val value: String
}

/**
 * Find the entry of [T] whose [StringValueEnum.value] is [s], or return null if there is no such entry.
 */
inline fun <reified T> parseStringValue(s: String): T? where T : Enum<T>, T : StringValueEnum =
    enumValues<T>().find { it.value == s }
