/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2015-Present 8x8, Inc.
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
package org.jitsi.xmpp.extensions.rayo;

import org.jivesoftware.smack.packet.*;

/**
 * Base class for all Ray IQs. Takes care of <header /> extension handling as
 * well as other functions shared by all IQs.
 */
public abstract class RayoIq
    extends IQ
{
    /**
     * Creates new instance of <tt>RayoIq</tt>.
     *
     * @param elementName the name of XML element that will be used.
     */
    protected RayoIq(String elementName)
    {
        super(elementName, RayoIqProvider.NAMESPACE);
    }

    /**
     * Creates new instance of this class as a copy from <tt>original</tt>.
     *
     * @param original the class to copy the data from.
     */
    protected RayoIq(RayoIq original)
    {
        super(original);
    }

    /**
     * Returns value of the header extension with given <tt>name</tt> (if any).
     *
     * @param name the name of header extension which value we want to
     *             retrieve.
     * @return value of header extension with given <tt>name</tt> if it exists
     * or <tt>null</tt> otherwise.
     */
    public String getHeader(String name)
    {
        HeaderExtension header = findHeader(name);

        return header != null ? header.getValue() : null;
    }

    private HeaderExtension findHeader(String name)
    {
        for (ExtensionElement ext : getExtensions())
        {
            if (ext instanceof HeaderExtension)
            {
                HeaderExtension header = (HeaderExtension) ext;

                if (name.equals(header.getName()))
                {
                    return header;
                }
            }
        }
        return null;
    }

    /**
     * Adds 'header' extension to this Rayo IQ with given name and value
     * attributes.
     *
     * @param name  the attribute name of the 'header' extension to be added.
     * @param value the 'value' attribute of the 'header' extension that will be
     *              added to this IQ.
     */
    public void setHeader(String name, String value)
    {
        HeaderExtension headerExt = findHeader(name);

        if (headerExt == null)
        {
            headerExt = new HeaderExtension();

            headerExt.setName(name);

            addExtension(headerExt);
        }

        headerExt.setValue(value);
    }
}
