package com.example.cryptobot.config;

import com.example.cryptobot.core.WebSocketConnector;
import com.example.cryptobot.core.services.WebSocketClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class WebSocketConfigurer {
    private final WebSocketClientProvider webSocketClientProvider;

    public WebSocketConfigurer(WebSocketClientProvider webSocketClientProvider) {
        this.webSocketClientProvider = webSocketClientProvider;
    }

    @Bean
    public List<WebSocketConnector> binanceTradesWS() {
        List<WebSocketConnector> webSocketClients = new LinkedList<>();
        Currencies.CURRENCIES.forEach(currency -> {
            webSocketClients.add(webSocketClientProvider.provide(currency));
        });
        return webSocketClients;
    }
}
