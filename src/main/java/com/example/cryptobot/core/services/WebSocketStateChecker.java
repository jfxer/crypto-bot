package com.example.cryptobot.core.services;

import com.example.cryptobot.core.WebSocketConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebSocketStateChecker {
    private final List<WebSocketConnector> webSocketClients;
    private final WebSocketClientProvider webSocketClientProvider;

    public WebSocketStateChecker(List<WebSocketConnector> webSocketClients,
                                 WebSocketClientProvider webSocketClientProvider) {
        this.webSocketClients = webSocketClients;
        this.webSocketClientProvider = webSocketClientProvider;
    }

    @Scheduled(fixedDelay = 1000*60*60) //each hour
    public void check(){
        List<WebSocketConnector> forRestart = webSocketClients.stream().filter(WebSocketConnector::isDisposed).collect(Collectors.toList());
        webSocketClients.removeAll(forRestart);
        forRestart.forEach(webSocketConnector -> {
            webSocketClients.add(webSocketClientProvider.provide(webSocketConnector.getCurrency()));
        });
    }
}
