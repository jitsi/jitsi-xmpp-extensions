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
package org.jitsi.xmpp.extensions.jibri;

import org.jitsi.xmpp.extensions.AbstractPacketExtension;
import org.jitsi.xmpp.extensions.DefaultPacketExtensionProvider;
import org.jitsi.utils.StringUtils;
import org.jivesoftware.smack.provider.ProviderManager;

/**
 * Status extension included in MUC presence by Jibri to indicate it's status.
 * One of:
 * <li>idle</li> - the instance is idle and can be used for recording
 * <li>busy</li> - the instance is currently recording or doing something very
 *                 important and should not be disturbed
 *
 *
 */
public class JibriBusyStatusPacketExt
    extends AbstractPacketExtension
{
    /**
     * The namespace of this packet extension.
     */
    public static final String NAMESPACE = JibriIq.NAMESPACE;

    /**
     * XML element name of this packet extension.
     */
    public static final String ELEMENT_NAME = "busy-status";

    private static final String STATUS_ATTRIBUTE = "status";

    /**
     * Creates new instance of <tt>VideoMutedExtension</tt>.
     */
    public JibriBusyStatusPacketExt()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    static public void registerExtensionProvider()
    {
        ProviderManager.addExtensionProvider(
                ELEMENT_NAME,
                NAMESPACE,
                new DefaultPacketExtensionProvider<>(JibriBusyStatusPacketExt.class)
        );
    }

    public BusyStatus getStatus()
    {
        return BusyStatus.parse(getAttributeAsString(STATUS_ATTRIBUTE));
    }

    public void setStatus(BusyStatus status)
    {
        setAttribute(STATUS_ATTRIBUTE, String.valueOf(status));
    }

    public enum BusyStatus
    {
        IDLE("idle"),
        BUSY("busy"),
        UNDEFINED("undefined");

        private String name;

        BusyStatus(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }

        /**
         * Parses <tt>Status</tt> from given string.
         *
         * @param status the string representation of <tt>Status</tt>.
         *
         * @return <tt>Status</tt> value for given string or
         *         {@link #UNDEFINED} if given string does not
         *         reflect any of valid values.
         */
        public static BusyStatus parse(String status)
        {
            if (StringUtils.isNullOrEmpty(status))
                return UNDEFINED;

            try
            {
                return BusyStatus.valueOf(status.toUpperCase());
            }
            catch(IllegalArgumentException e)
            {
                return UNDEFINED;
            }
        }
    }
}
