package org.apache.activemq.artemis.core.server;

import org.apache.activemq.artemis.api.core.SimpleString;

public class ServerMessage {
    private final long messageID;
    private final SimpleString address;

    public ServerMessage(final long messageID, final SimpleString address) {
        this.messageID = messageID;
        this.address = address;
    }

    public long getMessageID() {
        return messageID;
    }

    public SimpleString getAddress() {
        return address;
    }
}