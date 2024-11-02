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

import org.jitsi.xmpp.extensions.AbstractPacketExtension;

/**
 * Implements <tt>AbstractPacketExtension</tt> for the <tt>raw-key-fingerprint</tt>
 * element equivalent to the SDP attribute defined in draft-lennox-sdp-raw-key-fingerprints.
 */
public class DtlsRawKeyFingerprintPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The XML name of the <tt>raw-key-fingerprint</tt> element.
     */
    public static final String ELEMENT = "raw-key-fingerprint";

    /**
     * The XML name of the <tt>raw-key-fingerprint</tt> element's attribute which
     * specifies the hash function utilized to calculate the fingerprint.
     */
    private static final String HASH_ATTR_NAME = "hash";

    /**
     * The XML namespace of the <tt>fingerprint</tt> element defined by
     * XEP-0320: Use of DTLS-SRTP in Jingle Sessions.
     */
    public static final String NAMESPACE = "urn:xmpp:jingle:apps:dtls:0";

    /** Initializes a new <tt>DtlsFingerprintPacketExtension</tt> instance. */
    public DtlsRawKeyFingerprintPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Gets the fingerprint carried/represented by this instance.
     *
     * @return the fingerprint carried/represented by this instance
     */
    public String getFingerprint()
    {
        return getText();
    }

    /**
     * Gets the hash function utilized to calculate the fingerprint
     * carried/represented by this instance.
     *
     * @return the hash function utilized to calculate the fingerprint
     * carried/represented by this instance
     */
    public String getHash()
    {
        return getAttributeAsString(HASH_ATTR_NAME);
    }

    /**
     * Sets the fingerprint to be carried/represented by this instance.
     *
     * @param fingerprint the fingerprint to be carried/represented by this
     * instance
     */
    public void setFingerprint(String fingerprint)
    {
        setText(fingerprint);
    }

    /**
     * Sets the hash function utilized to calculate the fingerprint
     * carried/represented by this instance.
     *
     * @param hash the hash function utilized to calculate the fingerprint
     * carried/represented by this instance
     */
    public void setHash(String hash)
    {
        setAttribute(HASH_ATTR_NAME, hash);
    }
}
