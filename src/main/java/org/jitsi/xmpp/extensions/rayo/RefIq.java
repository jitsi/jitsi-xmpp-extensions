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
 * Rayo 'ref' IQ sent by the server as a reply to 'dial' request. Holds created
 * call's resource in 'uri' attribute.
 */
public class RefIq
    extends RayoIq
{
    /**
     * XML element name of <tt>RefIq</tt>.
     */
    public static final String ELEMENT = "ref";

    /**
     * XML namespace of this IQ.
     */
    public static final String NAMESPACE = RayoIqProvider.NAMESPACE;

    /**
     * Name of the URI attribute that stores call resource reference.
     */
    public static final String URI_ATTR_NAME = "uri";

    /**
     * Call resource/uri reference.
     */
    private String uri;

    /**
     * Creates new <tt>RefIq</tt>.
     */
    protected RefIq()
    {
        super(RefIq.ELEMENT);
    }

    /**
     * Creates new <tt>RefIq</tt> parametrized with given call <tt>uri</tt>.
     *
     * @param uri the call URI to be set on newly created <tt>RefIq</tt>.
     * @return new <tt>RefIq</tt> parametrized with given call <tt>uri</tt>.
     */
    public static RefIq create(String uri)
    {
        RefIq refIq = new RefIq();

        refIq.setUri(uri);

        return refIq;
    }

    /**
     * Creates result <tt>RefIq</tt> for given <tt>requestIq</tt> parametrized
     * with given call <tt>uri</tt>.
     *
     * @param requestIq the request IQ which 'from', 'to' and 'id' attributes
     *                  will be used for constructing result IQ.
     * @param uri       the call URI that will be included in newly created
     *                  <tt>RefIq</tt>.
     * @return result <tt>RefIq</tt> for given <tt>requestIq</tt> parametrized
     * with given call <tt>uri</tt>.
     */
    public static RefIq createResult(IQ requestIq, String uri)
    {
        RefIq refIq = create(uri);

        refIq.setType(Type.result);
        refIq.setStanzaId(requestIq.getStanzaId());
        refIq.setFrom(requestIq.getTo());
        refIq.setTo(requestIq.getFrom());

        return refIq;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
        IQChildElementXmlStringBuilder xml)
    {
        xml.optAttribute(URI_ATTR_NAME, uri);
        xml.setEmptyElement();
        return xml;
    }

    /**
     * Sets given call <tt>uri</tt> value on this instance.
     *
     * @param uri the call <tt>uri</tt> to be stored in this instance.
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    /**
     * Returns call URI held by this instance.
     *
     * @return the call URI held by this instance.
     */
    public String getUri()
    {
        return uri;
    }
}
