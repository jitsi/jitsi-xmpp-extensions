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
package org.jitsi.xmpp.extensions.rayo;

import org.jivesoftware.smack.packet.*;

/**
 * The 'dial' IQ used to initiate new outgoing call session in Rayo protocol.
 */
public class DialIq
    extends RayoIq
{
    /**
     * The name of XML element for this IQ.
     */
    public static final String ELEMENT = "dial";

    /**
     * XML namespace of this IQ.
     */
    public static final String NAMESPACE = RayoIqProvider.NAMESPACE;

    /**
     * The name of source URI/address attribute. Referred as "source" to avoid
     * confusion with "getFrom" and "setFrom" in {@link IQ} class.
     */
    public static final String SRC_ATTR_NAME = "from";

    /**
     * The name of destination URI/address attribute. Referred as "source" to
     * avoid confusion with "getFrom" and "setFrom" in {@link IQ} class.
     */
    public static final String DST_ATTR_NAME = "to";

    /**
     * Source URI/address.
     */
    private String source;

    /**
     * Destination URI/address.
     */
    private String destination;

    /**
     * Creates new instance of <tt>DialIq</tt>.
     */
    public DialIq()
    {
        super(DialIq.ELEMENT);
    }

    /**
     * Creates a new instance of this class as a copy from
     * <tt>original</tt>.
     *
     * @param original the class to copy the data from.
     */
    public DialIq(DialIq original)
    {
        // copies: id, to, from, extensions, error, type
        super(original);
        source = original.source;
        destination = original.destination;
    }

    /**
     * Creates new <tt>DialIq</tt> for given source and destination addresses.
     *
     * @param to   the destination address/call URI to be used.
     * @param from the source address that will be set on new <tt>DialIq</tt>
     *             instance.
     * @return new <tt>DialIq</tt> parameterized with given source and
     * destination addresses.
     */
    public static DialIq create(String to, String from)
    {
        DialIq dialIq = new DialIq();

        dialIq.setSource(from);

        dialIq.setDestination(to);

        return dialIq;
    }

    /**
     * Return source address value set on this <tt>DialIq</tt>.
     *
     * @return source address value of this <tt>DialIq</tt>.
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Sets new source address value on this <tt>DialIq</tt>.
     *
     * @param source the new source address value to be set.
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * Returns destination address/call URI associated with this instance.
     *
     * @return destination address/call URI associated with this instance.
     */
    public String getDestination()
    {
        return destination;
    }

    /**
     * Sets new destination address/call URI on this <tt>DialIq</tt>.
     *
     * @param destination the new destination address/call URI to set.
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQChildElementXmlStringBuilder xml)
    {
        xml.optAttribute(SRC_ATTR_NAME, source)
            .optAttribute(DST_ATTR_NAME, destination);
        xml.setEmptyElement();
        return xml;
    }
}
