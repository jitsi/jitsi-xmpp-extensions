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
package org.jitsi.xmpp.extensions.coin;

import org.jitsi.xmpp.extensions.*;

import org.jivesoftware.smack.util.*;

/**
 * User languages packet extension.
 *
 * @author Sebastien Vincent
 */
public class UserLanguagesPacketExtension
    extends AbstractPacketExtension
{
    /**
     * The namespace that user languages belongs to.
     */
    public static final String NAMESPACE = "";

    /**
     * The name of the element that contains the user languages data.
     */
    public static final String ELEMENT_NAME = "languages";

    /**
     * The name of the element that contains the media data.
     */
    public static final String ELEMENT_LANGUAGES = "stringvalues";

    /**
     * The list of languages separated by space.
     */
    private String languages = null;

    /**
     * Constructor.
     */
    public UserLanguagesPacketExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    /**
     * Set languages.
     *
     * @param languages list of languages
     */
    public void setLanguages(String languages)
    {
        this.languages = languages;
    }

    /**
     * Get languages.
     *
     * @return languages
     */
    public String getLanguages()
    {
        return languages;
    }

    /**
     * The child elements content.
     * @return the child elements content.
     */
    @Override
    public XmlStringBuilder getChildElementBuilder()
    {
        XmlStringBuilder xml = new XmlStringBuilder();

        xml.optElement(ELEMENT_LANGUAGES, languages);

        return xml;
    }
}
