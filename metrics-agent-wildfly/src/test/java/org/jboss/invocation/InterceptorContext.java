package org.jboss.invocation;


public class InterceptorContext {

    private Object[] parameters;


    public InterceptorContext(final Object... parameters) {
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
