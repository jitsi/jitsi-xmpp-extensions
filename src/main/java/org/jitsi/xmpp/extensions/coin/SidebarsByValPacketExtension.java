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
package org.jitsi.xmpp.extensions.coin;

import org.jitsi.xmpp.extensions.*;

/**
 * Sidebars by val packet extension.
 *
 * @author Sebastien Vincent
 */
public class SidebarsByValPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that sidebars by val belongs to.
     */
    public static final String NAMESPACE = "";

    /**
     * The name of the element that contains the sidebars by val.
     */
    public static final String ELEMENT_NAME = "sidebars-by-val";

    /**
     * Constructor.
     */
    public SidebarsByValPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }
}
