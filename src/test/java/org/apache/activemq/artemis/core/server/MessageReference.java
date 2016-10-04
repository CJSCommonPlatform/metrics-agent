package org.apache.activemq.artemis.core.server;

import org.apache.activemq.artemis.api.core.SimpleString;

public class MessageReference {
    private final ServerMessage message;

    public MessageReference(final ServerMessage message) {
        this.message = message;
    }

    public MessageReference(final long messageID, final SimpleString address) {
        this.message = new ServerMessage(messageID, address);
    }

    public ServerMessage getMessage() {
        return message;
    }
}
