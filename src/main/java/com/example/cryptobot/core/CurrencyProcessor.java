package com.example.cryptobot.core;

import com.example.cryptobot.core.model.Currency;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyProcessor {
    Map<String, LinkedList<Currency>> currencyToModelMap = new HashMap<>();
    Map<String, Instant> notified = new HashMap<>();
    private final RestTemplate restTemplate;

    public CurrencyProcessor(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void process(Currency currency) {
        if (!currencyToModelMap.containsKey(currency.getCurrency())) {
            currencyToModelMap.put(currency.getCurrency(), new LinkedList<>());
        }
        synchronized (currency.getCurrency()) {
            LinkedList<Currency> currencies = currencyToModelMap.get(currency.getCurrency());
            currencies.add(currency);

            Duration delta = Duration.between(currencies.getFirst().getInstant(), currencies.getLast().getInstant());
            Instant newStart = currencies.getLast().getInstant().minus(Duration.ofMinutes(5));
            if (delta.getSeconds() > 60 * 5) {
                currencies.remove(currencies.stream().filter(curr -> curr.getInstant().isBefore(newStart)).collect(Collectors.toList()));
                //System.out.println("removed old data");
                //System.out.println(currencies);
            }
            Currency firstCurr = currencies.getFirst();
            Currency lastCurr = currencies.getLast();
            Double difference = lastCurr.getVolume().doubleValue() - firstCurr.getVolume().doubleValue();

            if (difference > 500000) {
                Instant now = Instant.now();
                //System.out.println("Bigger + " + lastCurr);
                if (!notified.containsKey(currency.getCurrency()) || notified.get(currency.getCurrency()).isBefore(Instant.now().minusSeconds(60 * 60 * 2))) {
                    sendNotification(currency.getCurrency(), difference.intValue());
                    notified.put(currency.getCurrency(), now);
                }
            }
        }
    }

    public String sendNotification(String currency, Integer difference) {
        String url = "https://api.telegram.org/bot" + "5733535824:AAGn1ZG0O-PH0ArfVeBe_Plzge2O9_WQJcE" + "/sendMessage?chat_id=" + "-1001644436046" + "&parse_mode=Markdown&text=" + "==========" + currency + "Minute=============\n" + "https://www.binance.com/ru/trade/" + currency.substring(0, currency.indexOf("USDT")) + "\\_USDT" + "\nup:    `" + difference + "$`";
        return this.restTemplate.getForObject(url, String.class);
    }
}
