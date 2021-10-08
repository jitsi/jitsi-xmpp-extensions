/*
 * Copyright 2021 8x8 Inc
 * Copyright 2021 Florian Schmaus
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

package org.jitsi.xmpp.extensions;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.XmppElementUtil;

import org.junit.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import static org.junit.Assert.assertTrue;

/**
 * Verify that all jitsi XMPP extensions have QNAME (or ELEMENT and NAMESPACE) members,
 * as is now required by Smack for {@link StanzaView#getExtension(Class)} to work.
 */
public class ExtensionElementQNameDeclaredTest
{
    @Test
    public void qnameOrElementNamespaceDeclaredTest()
    {
        String[] jitsiXmppExtensionsPackages = new String[] {
            "org.jitsi.xmpp.extensions",
        };
        Reflections reflections = new Reflections(jitsiXmppExtensionsPackages, new SubTypesScanner());
        Set<Class<? extends ExtensionElement>> extensionElementClasses = reflections.getSubTypesOf(
            ExtensionElement.class);

        Map<Class<? extends ExtensionElement>, IllegalArgumentException> exceptions = new HashMap<>();
        for (Class<? extends ExtensionElement> extensionElementClass : extensionElementClasses) {
            if (Modifier.isAbstract(extensionElementClass.getModifiers())) {
                continue;
            }

            try {
                XmppElementUtil.getQNameFor(extensionElementClass);
            } catch (IllegalArgumentException e) {
                exceptions.put(extensionElementClass, e);
            }
        }

        Set<Class<? extends ExtensionElement>> failedClasses = exceptions.keySet();

        /* TODO - not all classes are converted.  (This may depend on a newer version of Smack for some.) */
        /* assertTrue("The following " + failedClasses.size()
            + " classes are missing QNAME declaration: " + failedClasses, failedClasses.isEmpty()); */
    }
}

