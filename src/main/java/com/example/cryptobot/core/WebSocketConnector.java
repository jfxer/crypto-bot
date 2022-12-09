package com.example.cryptobot.core;


import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;

public class WebSocketConnector {
    private final WebSocketClient webSocketClient;
    private final String currency;
    private final Disposable disposable;

    public WebSocketConnector(WebSocketClient webSocketClient, String currency, Disposable disposable) {
        this.webSocketClient = webSocketClient;
        this.currency = currency;
        this.disposable = disposable;
    }

    public boolean isDisposed() {
        return disposable.isDisposed();
    }

    public String getCurrency() {
        return currency;
    }
}
