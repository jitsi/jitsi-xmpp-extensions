package org.jitsi.xmpp.extensions.colibri2;

import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import static org.junit.jupiter.api.Assertions.*;

public class CapabilityTest
{
    @Test
    public void parsingTest()
            throws Exception
    {
        Capability.Provider provider = new Capability.Provider();

        Capability cap = provider.parse(PacketParserUtils.getParserFor(
                "<capability name='" + Capability.CAP_SOURCE_NAME_SUPPORT + "'/>"));

        assertEquals(Capability.CAP_SOURCE_NAME_SUPPORT, cap.getName());
    }

    @Test
    public void toXmlTest()
    {
        Diff diff1 = DiffBuilder.compare(
            "<capability xmlns='jitsi:colibri2' name='" + Capability.CAP_SOURCE_NAME_SUPPORT + "'/>").
                withTest(new Capability(Capability.CAP_SOURCE_NAME_SUPPORT).toXML().toString()).
                checkForIdentical().build();
        assertFalse(diff1.hasDifferences(), diff1.toString());
    }
}
