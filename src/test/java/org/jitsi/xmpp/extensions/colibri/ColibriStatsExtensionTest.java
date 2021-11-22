/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Copyright @ 2018 Atlassian Pty Ltd
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
package org.jitsi.xmpp.extensions.colibri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * @author Boris Grozev
 */
public class ColibriStatsExtensionTest
{
    /**
     * Test the constructor and adding a new stat.
     */
    @Test
    public void testAddStat()
    {
        ColibriStatsExtension stats = new ColibriStatsExtension();

        stats.addStat("name", "value");

        assertNotNull(stats.getStat("name"));
        assertEquals("name", stats.getStat("name").getName());
        assertEquals("value", stats.getStat("name").getValue());
        assertEquals("value", stats.getValueAsString("name"));
        assertNull(stats.getValueAsInt("name"));

        stats.addStat("int", 13);
        assertEquals(13, stats.getValueAsInt("int"));
        assertEquals("13", stats.getValueAsString("int"));

        assertNull(stats.getStat("somethingelse"));
        assertNull(stats.getValue("somethingelse"));

    }

    /**
     * Test cloning.
     */
    @Test
    public void testClone()
    {
        ColibriStatsExtension stats = new ColibriStatsExtension();
        stats.addStat("name", "value");

        ColibriStatsExtension clone = ColibriStatsExtension.clone(stats);

        assertNotNull(clone.getStat("name"));
        assertEquals("name", clone.getStat("name").getName());
        assertEquals("value", clone.getStat("name").getValue());

        ColibriStatsExtension.Stat cloneNameStat = clone.getStat("name");
        cloneNameStat.setValue("virtue");

        assertEquals("virtue", clone.getValue("name"));
        assertEquals("value", stats.getValue("name"));
    }
}
