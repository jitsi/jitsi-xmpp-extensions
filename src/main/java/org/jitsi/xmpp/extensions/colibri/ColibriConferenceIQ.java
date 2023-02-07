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

/**
 * Implements the Jitsi Videobridge <tt>conference</tt> IQ within the
 * COnferencing with LIghtweight BRIdging.
 *
 * @author Lyubomir Marinov
 * @author Boris Grozev
 * @author George Politis
 */
public class ColibriConferenceIQ
{
    /**
     * The XML element name of the Jitsi Videobridge <tt>conference</tt> IQ.
     */
    public static final String ELEMENT = "conference";

    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>conference</tt> IQ.
     */
    public static final String NAMESPACE = "http://jitsi.org/protocol/colibri";

}