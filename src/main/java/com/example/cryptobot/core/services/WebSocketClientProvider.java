package com.example.cryptobot.core.services;

import com.example.cryptobot.core.CurrencyProcessor;
import com.example.cryptobot.core.WebSocketConnector;
import com.example.cryptobot.core.model.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Locale;

@Service
public class WebSocketClientProvider {
    private final ObjectMapper objectMapper;
    private final CurrencyProcessor currencyProcessor;

    public WebSocketClientProvider(ObjectMapper objectMapper,
                                   CurrencyProcessor currencyProcessor) {
        this.objectMapper = objectMapper;
        this.currencyProcessor = currencyProcessor;
    }

    public WebSocketConnector provide(String currency) {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        Disposable disposable = client.execute(
                URI.create("wss://stream.binance.com:9443/ws/" + currency.toLowerCase(Locale.ROOT) + "@miniTicker"),
                session -> session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .map(value -> {
                            try {
                                return objectMapper.readValue(value, Currency.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return new Currency("", BigDecimal.ONE);
                        })
                        .handle((v, sink) -> {
                            sink.next(v);
                            currencyProcessor.process(v);
                        })
                        .then()
        ).subscribe();
        return new WebSocketConnector(client, currency, disposable);
    }
}