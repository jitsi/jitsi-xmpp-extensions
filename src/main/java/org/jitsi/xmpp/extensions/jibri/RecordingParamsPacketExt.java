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
 * Included in a start request to ask Jibri for a recording which is not the default one, for example a recording of
 * two participants side by side at a given resolution.
 *
 * The parameters describe what the recording must look like, not how Jibri makes it. Jibri decides which screen
 * resolution, which layout and which client options it uses to satisfy them. Jibri also decides which values it
 * accepts: it refuses a request it cannot serve with a bad-request response, while it is still idle.
 *
 * Every parameter is optional. A Jicofo release which does not know a parameter does not send it, and a request
 * without this extension asks for the default recording.
 *
 * Example:
 * <pre>{@code
 * <jibri xmlns='http://jitsi.org/protocol/jibri' action='start' recording_mode='file'>
 *   <recording-params xmlns='http://jitsi.org/protocol/jibri'
 *       tile-resolution='1280x720' tile-count='2' max-full-resolution-participants='3'/>
 * </jibri>
 * }</pre>
 */
public class RecordingParamsPacketExt
    extends AbstractPacketExtension
{
    /**
     * The namespace of this packet extension.
     */
    public static final String NAMESPACE = JibriIq.NAMESPACE;

    /**
     * XML element name of this packet extension.
     */
    public static final String ELEMENT = "recording-params";

    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The name of the attribute which holds the resolution of one tile, in the "WIDTHxHEIGHT" format.
     */
    public static final String TILE_RESOLUTION_ATTR_NAME = "tile-resolution";

    /**
     * The name of the attribute which holds the number of tiles.
     */
    public static final String TILE_COUNT_ATTR_NAME = "tile-count";

    /**
     * The name of the attribute which holds the value for the maxFullResolutionParticipants client option.
     */
    public static final String MAX_FULL_RESOLUTION_PARTICIPANTS_ATTR_NAME = "max-full-resolution-participants";

    public RecordingParamsPacketExt()
    {
        super(NAMESPACE, ELEMENT);
    }

    static public void registerExtensionProvider()
    {
        ProviderManager.addExtensionProvider(
                ELEMENT,
                NAMESPACE,
                new DefaultPacketExtensionProvider<>(RecordingParamsPacketExt.class)
        );
    }

    /**
     * @return the requested resolution of one tile in the "WIDTHxHEIGHT" format, or {@code null} if the request does
     * not ask for a specific tile resolution.
     */
    public String getTileResolution()
    {
        return getAttributeAsString(TILE_RESOLUTION_ATTR_NAME);
    }

    /**
     * Sets the requested resolution of one tile. The format is "WIDTHxHEIGHT", for example "1280x720".
     */
    public void setTileResolution(String tileResolution)
    {
        setAttribute(TILE_RESOLUTION_ATTR_NAME, tileResolution);
    }

    /**
     * @return the requested number of tiles, or {@code null} if the request does not ask for a specific number.
     * @throws NumberFormatException if the attribute is present but is not a number. The caller is expected to
     * refuse the request, because the sender asked for something which cannot be served.
     */
    public Integer getTileCount()
    {
        String value = getAttributeAsString(TILE_COUNT_ATTR_NAME);

        return value == null ? null : Integer.valueOf(value);
    }

    public void setTileCount(Integer tileCount)
    {
        setAttribute(TILE_COUNT_ATTR_NAME, tileCount);
    }

    /**
     * @return the requested value for the maxFullResolutionParticipants client option, or {@code null} if the
     * request does not ask for a specific value.
     * @throws NumberFormatException if the attribute is present but is not a number. The caller is expected to
     * refuse the request, because the sender asked for something which cannot be served.
     */
    public Integer getMaxFullResolutionParticipants()
    {
        String value = getAttributeAsString(MAX_FULL_RESOLUTION_PARTICIPANTS_ATTR_NAME);

        return value == null ? null : Integer.valueOf(value);
    }

    public void setMaxFullResolutionParticipants(Integer maxFullResolutionParticipants)
    {
        setAttribute(MAX_FULL_RESOLUTION_PARTICIPANTS_ATTR_NAME, maxFullResolutionParticipants);
    }
}
