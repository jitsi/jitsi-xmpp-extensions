package org.jitsi.xmpp.extensions.colibri2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jivesoftware.smack.parsing.SmackParsingException
import org.jivesoftware.smack.util.PacketParserUtils

class ConferenceNotificationIQTest : ShouldSpec() {
    init {
        IqProviderUtils.registerProviders()
        context("Parsing valid XML") {
            val parser = PacketParserUtils.getParserFor(
                """
                <iq xmlns="jabber:client" id="id" type="get">
                  <conference-notification xmlns="jitsi:colibri2" meeting-id="meeting-123">
                    <notification xmlns="jitsi:colibri2" id="abcdabcd" type="ice_failed"/>
                    <notification xmlns="jitsi:colibri2" id="ws://localhost:8080/record/dssda" type="connect_failed" description="Connection failed due to network issues."/>
                  </conference-notification>
                </iq>
                """.trimIndent()
            )
            PacketParserUtils.parseIQ(parser).apply {
                shouldBeInstanceOf<ConferenceNotificationIQ>()
                meetingId shouldBe "meeting-123"
                extensions.size shouldBe 2
                notifications.size shouldBe 2
                notifications[0].id shouldBe "abcdabcd"
                notifications[0].type shouldBe Notification.Types.ICE_FAILED
                notifications[0].description shouldBe null
                notifications[1].id shouldBe "ws://localhost:8080/record/dssda"
                notifications[1].type shouldBe Notification.Types.CONNECT_FAILED
                notifications[1].description shouldBe "Connection failed due to network issues."
            }
        }
        context("Parsing invalid XML") {
            context("Missing meeting-id") {
                val parser = PacketParserUtils.getParserFor(
                    """
                    <iq xmlns="jabber:client" id="id" type="get">
                      <conference-notification xmlns="jitsi:colibri2">
                        <notification xmlns="jitsi:colibri2" id="abcdabcd" type="ice_failed"/>
                      </conference-notification>
                    </iq>
                    """.trimIndent()
                )
                shouldThrow<SmackParsingException> {
                    PacketParserUtils.parseIQ(parser)
                }
            }
            context("Invalid notification type") {
                val parser = PacketParserUtils.getParserFor(
                    """
                    <iq xmlns="jabber:client" id="id" type="get">
                      <conference-notification xmlns="jitsi:colibri2" meeting-id="meeting-123">
                        <notification xmlns="jitsi:colibri2" id="abcdabcd" type="invalid_type"/>
                      </conference-notification>
                    </iq>
                    """.trimIndent()
                )
                shouldThrow<SmackParsingException> {
                    PacketParserUtils.parseIQ(parser)
                }
            }
        }
    }
}
