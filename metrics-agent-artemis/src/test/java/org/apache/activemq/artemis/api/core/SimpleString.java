package org.apache.activemq.artemis.api.core;


public class SimpleString {

    private final String string;

    public SimpleString(final String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
