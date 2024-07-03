/*
 * Copyright @ 2023 - present 8x8, Inc.
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
package org.jitsi.xmpp.util

import java.io.StringReader
import java.io.StringWriter
import javax.xml.XMLConstants
import javax.xml.transform.Templates
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class RedactColibriIp {
    companion object {
        private val redactXslt =
            """
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:j="jabber:client"
                xmlns:c="jitsi:colibri2"
                xmlns:i="urn:xmpp:jingle:transports:ice-udp:1"
                >
  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template name="redactIp">
    <xsl:choose>
      <!-- Unspecified address or loopback -->
      <xsl:when test='. = "0.0.0.0" or . = "::" or . = "::1" or starts-with(., "127.")'>
        <xsl:value-of select='.'/>
      </xsl:when>
      <!-- Globally-routable IPv6 -->
      <xsl:when test='contains(., ":") and (starts-with(., "2") or starts-with(., "3"))'>
        <xsl:value-of select='"2xxx::xxx"'/>
      </xsl:when>
      <!-- Other IPv6 -->
      <xsl:when test='contains(., ":")'>
        <xsl:value-of select='concat(substring-before(., ":"), "::xxx")'/>
      </xsl:when>
      <!-- IPv4 -->
      <xsl:when test='contains(., ".")'>
        <xsl:value-of select='"xx.xx.xx.xx"'/>
      </xsl:when>
      <!-- Unrecognized format -->
      <xsl:otherwise>
        <xsl:value-of select='.'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match='j:iq/c:conference-modify/c:endpoint/c:transport/i:transport/i:candidate[not(@type) or @type != "relay"]/@ip'>
    <xsl:attribute name="ip">
      <xsl:call-template name="redactIp"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match='j:iq/c:conference-modify/c:endpoint/c:transport/i:transport/i:candidate/@rel-addr'>
    <xsl:attribute name="rel-addr">
      <xsl:call-template name="redactIp"/>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
            """

        private val factory: TransformerFactory by lazy {
            TransformerFactory.newInstance().also {
                it.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            }
        }
        private val templates: Templates by lazy {
            factory.newTemplates(
                StreamSource(StringReader(redactXslt))
            )
        }

        fun redact(input: String): String {
            val source = StreamSource(StringReader(input))
            val writer = StringWriter()
            val result = StreamResult(writer)
            val transformer = templates.newTransformer()
            transformer.transform(source, result)
            return writer.toString()
        }
    }
}
