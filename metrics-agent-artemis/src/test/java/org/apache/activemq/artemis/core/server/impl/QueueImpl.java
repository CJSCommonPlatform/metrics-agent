package org.apache.activemq.artemis.core.server.impl;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.server.Consumer;
import org.apache.activemq.artemis.core.server.MessageReference;

public class QueueImpl {


    private SimpleString name;

    public QueueImpl(final SimpleString name) {

        this.name = name;
    }

    public void addTail(final MessageReference ref, final boolean direct) {

    }

    public void proceedDeliver(final Consumer consumer, final MessageReference reference) {

    }

}
