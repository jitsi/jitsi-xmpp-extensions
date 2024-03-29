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

import org.jitsi.xmpp.extensions.*;

import org.jivesoftware.smack.packet.*;
import org.jxmpp.jid.*;

import java.util.*;

/**
 * The packet extension added to Jicofo MUC presence to broadcast current
 * recording status to all conference participants.
 *
 * Status meaning:
 * <tt>{@link JibriIq.Status#UNDEFINED}</tt> - recording not available
 * <tt>{@link JibriIq.Status#OFF}</tt> - recording stopped(available to start)
 * <tt>{@link JibriIq.Status#PENDING}</tt> - starting recording
 * <tt>{@link JibriIq.Status#ON}</tt> - recording in progress
 */
public class RecordingStatus
    extends AbstractPacketExtension
{
    /**
     * The namespace of this packet extension.
     */
    public static final String NAMESPACE = JibriIq.NAMESPACE;

    /**
     * XML element name of this packet extension.
     */
    public static final String ELEMENT = "jibri-recording-status";

    /**
     * The name of XML attribute which holds the recording status.
     */
    private static final String STATUS_ATTRIBUTE = "status";

    /**
     * The name of the argument that contains the "initiator" jid.
     */
    public static final String INITIATOR_ATTR_NAME = "initiator";

    /**
     * The full JID of the entity that has initiated the recording flow.
     */
    private Jid initiator;

    public RecordingStatus()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Returns the value of current recording status stored in it's attribute.
     * @return one of {@link JibriIq.Status}
     */
    public JibriIq.Status getStatus()
    {
        String statusAttr = getAttributeAsString(STATUS_ATTRIBUTE);

        return JibriIq.Status.parse(statusAttr);
    }

    /**
     * Sets new value for the recording status.
     * @param status one of {@link JibriIq.Status}
     */
    public void setStatus(JibriIq.Status status)
    {
        setAttribute(STATUS_ATTRIBUTE, status);
    }

    /**
     * Returns the session ID stored in this element
     * @return the session ID
     */
    public String getSessionId()
    {
        return getAttributeAsString(JibriIq.SESSION_ID_ATTR_NAME);
    }

    /**
     * Set the session ID for this recording status element
     * @param sessionId the session ID
     */
    public void setSessionId(String sessionId)
    {
        setAttribute(JibriIq.SESSION_ID_ATTR_NAME, sessionId);
    }

    public JibriIq.RecordingMode getRecordingMode()
    {
        String recordingMode = getAttributeAsString(JibriIq.RECORDING_MODE_ATTR_NAME);
        return JibriIq.RecordingMode.parse(recordingMode);
    }

    public void setRecordingMode(JibriIq.RecordingMode recordingMode)
    {
        setAttribute(JibriIq.RECORDING_MODE_ATTR_NAME, recordingMode.toString());
    }

    /**
     * Get the failure reason in this status, or UNDEFINED if there isn't one
     * @return the failure reason
     */
    public JibriIq.FailureReason getFailureReason()
    {
        String failureReasonStr = getAttributeAsString(JibriIq.FAILURE_REASON_ATTR_NAME);
        return JibriIq.FailureReason.parse(failureReasonStr);
    }

    /**
     * Set the failure reason in this status
     * @param failureReason the failure reason
     */
    public void setFailureReason(JibriIq.FailureReason failureReason)
    {
        if (failureReason != null)
        {
            setAttribute(JibriIq.FAILURE_REASON_ATTR_NAME, failureReason.toString());
        }
    }

    /**
     * Returns <tt>StanzaError</tt> associated with current
     * {@link RecordingStatus}.
     */
    public StanzaError getError()
    {
        StanzaErrorPE errorPe = getErrorPE();
        return errorPe != null ? errorPe.getError() : null;
    }

    /**
     * Gets <tt>{@link StanzaErrorPE}</tt> from the list of child packet
     * extensions.
     * @return {@link StanzaErrorPE} or <tt>null</tt> if not found.
     */
    private StanzaErrorPE getErrorPE()
    {
        List<? extends ExtensionElement> errorPe
            = getChildExtensionsOfType(StanzaErrorPE.class);

        return (StanzaErrorPE) (!errorPe.isEmpty() ? errorPe.get(0) : null);
    }

    /**
     * Sets <tt>StanzaError</tt> on this <tt>RecordingStatus</tt>.
     * @param error <tt>StanzaError</tt> to add error details to this
     * <tt>RecordingStatus</tt> instance or <tt>null</tt> to have it removed.
     */
    public void setError(StanzaError error)
    {
        if (error != null)
        {
            // Wrap and add StanzaError as packet extension
            StanzaErrorPE errorPe = getErrorPE();
            if (errorPe == null)
            {
                errorPe = new StanzaErrorPE(error);
                addChildExtension(errorPe);
            }
            errorPe.setError(error);
        }
        else
        {
            // Remove error PE
            getChildExtensions().remove(getErrorPE());
        }
    }


    /**
     * Sets the full JID of the entity that has initiated the recording flow.
     *
     * @param initiator the full JID of the initiator.
     */
    public void setInitiator(Jid initiator)
    {
        setAttribute(INITIATOR_ATTR_NAME, initiator);

        this.initiator = initiator;
    }

    /**
     * Returns the full JID of the entity that has initiated the recording flow.
     *
     * @return the full JID of the initiator.
     */
    public Jid getInitiator()
    {
        return initiator;
    }
}
