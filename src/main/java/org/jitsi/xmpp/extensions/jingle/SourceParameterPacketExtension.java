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
package org.jitsi.xmpp.extensions.jingle;

import org.jitsi.xmpp.extensions.*;
import org.jitsi.xmpp.extensions.colibri.*;

/**
 * Represents the <tt>parameter</tt> elements described in XEP-0167.
 *
 * @author Emil Ivov
 */
public class SourceParameterPacketExtension extends AbstractPacketExtension
{
    /**
     * The name of the "parameter" element.
     */
    public static final String ELEMENT = "parameter";

    /**
     * XML namespace of this extension.
     */
    public static final String NAMESPACE = SourcePacketExtension.NAMESPACE;

    /**
     * The name of the <tt>name</tt> parameter in the <tt>parameter</tt>
     * element.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * The name of the <tt>value</tt> parameter in the <tt>parameter</tt>
     * element.
     */
    public static final String VALUE_ATTR_NAME = "value";

    /**
     * Creates a new {@link SourceParameterPacketExtension} instance.
     */
    public SourceParameterPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Creates a new {@link SourceParameterPacketExtension} instance and
     * sets the given name and value.
     */
    public SourceParameterPacketExtension(String name, String value)
    {
        super(NAMESPACE, ELEMENT);

        setName(name);
        setValue(value);
    }

    /**
     * Sets the name of the format parameter we are representing here.
     *
     * @param name the name of the format parameter we are representing here.
     */
    public void setName(String name)
    {
        super.setAttribute(NAME_ATTR_NAME, name);
    }

    /**
     * Returns the name of the format parameter we are representing here.
     *
     * @return the name of the format parameter we are representing here.
     */
    public String getName()
    {
        return super.getAttributeAsString(NAME_ATTR_NAME);
    }

    /**
     * Sets that value of the format parameter we are representing here.
     *
     * @param value the value of the format parameter we are representing here.
     */
    public void setValue(String value)
    {
        super.setAttribute(VALUE_ATTR_NAME, value);
    }

    /**
     * Returns the value of the format parameter we are representing here.
     *
     * @return the value of the format parameter we are representing here.
     */
    public String getValue()
    {
        return super.getAttributeAsString(VALUE_ATTR_NAME);
    }
}
