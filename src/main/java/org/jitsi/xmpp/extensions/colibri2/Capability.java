package org.jitsi.xmpp.extensions.colibri2;

import org.jitsi.xmpp.extensions.AbstractPacketExtension;
import org.jitsi.xmpp.extensions.DefaultPacketExtensionProvider;

import javax.xml.namespace.QName;

/**
 * The list of Colibri2 Endpoint capabilities (supported features).
 */
public class Capability extends AbstractPacketExtension
{
    /**
     * The XML element name of the Colibri2 {@link Capability} element.
     */
    public static final String ELEMENT = "capability";

    /**
     * The XML namespace of the Colibri2 {@link ForceMute} element
     */
    public static final String NAMESPACE = ConferenceModifyIQ.NAMESPACE;

    /**
     * The qualified name of the element.
     */
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT);

    /**
     * The capability name attribute.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * A constant for the source name signaling support (required for the multi-stream mode).
     */
    public static final String CAP_SOURCE_NAME_SUPPORT = "source-names";

    /**
     * A constant for SSRC rewriting support
     */
    public static final String CAP_SSRC_REWRITING_SUPPORT = "ssrc-rewriting";

    /**
     * Creates an {@link Capability} instance.
     */
    public Capability()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Creates an {@link Capability} instance for given name.
     * @param capabilityName - the name of the capability.
     */
    public Capability(String capabilityName)
    {
        this();

        setName(capabilityName);
    }

    /**
     * @return the capability name.
     */
    public String getName()
    {
        return getAttributeAsString(NAME_ATTR_NAME);
    }

    /**
     * Sets the capability name.
     * @param capabilityName - the name to set.
     */
    public void setName(String capabilityName)
    {
        setAttribute(NAME_ATTR_NAME, capabilityName);
    }

    public static class Provider extends DefaultPacketExtensionProvider<Capability>
    {
        public Provider()
        {
            super(Capability.class);
        }
    }
}
