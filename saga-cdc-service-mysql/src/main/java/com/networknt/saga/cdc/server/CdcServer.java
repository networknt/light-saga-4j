package com.networknt.saga.cdc.server;

import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * CdcServer handle
 */
public class CdcServer implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.path()
                .addPrefixPath("/", exchange -> exchange.getResponseSender().send("OK!")
                );
    }
}
