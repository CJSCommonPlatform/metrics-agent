package io.undertow.server;

public class HttpServerExchange {

    private String requestPath;

    public HttpServerExchange(final String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestPath() {
        return requestPath;
    }
}