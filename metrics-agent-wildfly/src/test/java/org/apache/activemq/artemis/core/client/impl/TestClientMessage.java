package org.apache.activemq.artemis.core.client.impl;

import org.apache.activemq.artemis.api.core.SimpleString;

public class TestClientMessage extends ClientMessageImpl {
    public TestClientMessage(final long messageID,final String address, final String cppNameProperty) {
        super();
        this.messageID = messageID;
        this.address = new SimpleString(address);
        this.putStringProperty("CPPNAME", cppNameProperty);
    }
}