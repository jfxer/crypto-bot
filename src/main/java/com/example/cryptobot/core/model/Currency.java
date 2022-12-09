package com.example.cryptobot.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Currency {
    @JsonProperty("s")
    private final String currency;

    @JsonProperty("q")
    private final BigDecimal volume;

    private Instant instant;

    public Currency(String currency, BigDecimal volume) {
        this.currency = currency;
        this.volume = volume;
    }

    @JsonProperty("E")
    public void setInstant(String instant) {
        this.instant = Instant.ofEpochMilli(Long.parseLong(instant));
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                ", volume=" + volume +
                ", instant=" + instant +
                '}';
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(instant, currency.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant);
    }
}
