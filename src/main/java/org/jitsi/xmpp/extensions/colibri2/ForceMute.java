/*
 * Copyright @ 2021 - present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.colibri2;

import org.jitsi.xmpp.extensions.*;

import javax.xml.namespace.*;

/**
 * A packet extension for "force-mute", i.e. signaling to the bridge that it should not accept audio and/or video
 * from a certain endpoint (because it is muted).
 */
public class ForceMute
        extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 {@link ForceMute} element.
     */
    public static final String ELEMENT = "force-mute";

    /**
     * The XML namespace of the Colibri2 {@link ForceMute} element
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the <tt>audio</tt> attribute.
     */
    public static final String AUDIO_ATTR_NAME = "audio";

    /**
     * Default value for the "audio" attribute.
     */
    public static final boolean AUDIO_DEFAULT = false;

    /**
     * The name of the <tt>video</tt> attribute.
     */
    public static final String VIDEO_ATTR_NAME = "video";

    /**
     * Default value for the "video" attribute.
     */
    public static final boolean VIDEO_DEFAULT = false;

    public ForceMute()
    {
        this(AUDIO_DEFAULT, VIDEO_DEFAULT);
    }

    public ForceMute(boolean audio, boolean video)
    {
        super(NAMESPACE, ELEMENT);

        if (audio != AUDIO_DEFAULT)
        {
            setAttribute(AUDIO_ATTR_NAME, audio);
        }
        if (video != VIDEO_DEFAULT)
        {
            setAttribute(VIDEO_ATTR_NAME, video);
        }
    }

    public boolean getAudio()
    {
        String s = getAttributeAsString(AUDIO_ATTR_NAME);
        return s == null ? AUDIO_DEFAULT : Boolean.parseBoolean(s);
    }

    public boolean getVideo()
    {
        String s = getAttributeAsString(VIDEO_ATTR_NAME);
        return s == null ? VIDEO_DEFAULT : Boolean.parseBoolean(s);
    }

    public static class Provider extends DefaultPacketExtensionProvider<ForceMute>
    {
        public Provider()
        {
            super(ForceMute.class);
        }
    }
}
