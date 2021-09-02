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

import org.jivesoftware.smack.packet.*;

import java.util.*;

/**
 * Wraps Smack's <tt>StanzaError</tt> into <tt>ExtensionElement</tt>, so that it
 * can be easily inserted into {@link RecordingStatus}.
 */
public class StanzaErrorPE
    implements ExtensionElement
{
    /**
     * <tt>StanzaError</tt> wrapped into this <tt>StanzaErrorPE</tt>.
     */
    private StanzaError error;

    /**
     * Creates new instance of <tt>StanzaErrorPE</tt>.
     * @param stanzaError the instance of <tt>StanzaError</tt> that will be wrapped
     * by the newly created <tt>StanzaErrorPE</tt>.
     */
    public StanzaErrorPE(StanzaError stanzaError)
    {
        setError(stanzaError);
    }

    /**
     * Returns the underlying instance of <tt>StanzaError</tt>.
     */
    public StanzaError getError()
    {
        return error;
    }

    /**
     * Sets new instance of <tt>StanzaError</tt> to be wrapped by this
     * <tt>StanzaErrorPE</tt>.
     * @param error <tt>StanzaError</tt> that will be wrapped by this
     * <TT>StanzaErrorPE</TT>.
     */
    public void setError(StanzaError error)
    {
        Objects.requireNonNull(error, "error");

        this.error = error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getElementName()
    {
        return "error";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespace()
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toXML(XmlEnvironment enclosingNamespace)
    {
        return error.toXML().toString();
    }
}
