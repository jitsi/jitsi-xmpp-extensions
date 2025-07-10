/*
 * Copyright @ 2025 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.jitsimeet

class MuteIq : AbstractMuteIq(NAMESPACE) {
    companion object {
        const val NAMESPACE = "http://jitsi.org/jitmeet/audio"
    }
}
class MuteIqProvider : AbstractMuteIqProvider<MuteIq>(MuteIq.NAMESPACE) {
    override fun createMuteIq() = MuteIq()
}

class MuteVideoIq : AbstractMuteIq(NAMESPACE) {
    companion object {
        const val NAMESPACE = "http://jitsi.org/jitmeet/video"
    }
}
class MuteVideoIqProvider : AbstractMuteIqProvider<MuteVideoIq>(MuteVideoIq.NAMESPACE) {
    override fun createMuteIq() = MuteVideoIq()
}

class MuteDesktopIq : AbstractMuteIq(NAMESPACE) {
    companion object {
        const val NAMESPACE = "http://jitsi.org/jitmeet/desktop"
    }
}
class MuteDesktopIqProvider : AbstractMuteIqProvider<MuteDesktopIq>(MuteDesktopIq.NAMESPACE) {
    override fun createMuteIq() = MuteDesktopIq()
}
