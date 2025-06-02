package com.ebanking.dto.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO for currency creation/update request
 */
public class CurrencyCreateRequest {

    @NotBlank(message = "Currency symbol is required")
    @Size(min = 2, max = 10, message = "Currency symbol must be between 2 and 10 characters")
    private String symbol;

    @NotBlank(message = "Currency name is required")
    @Size(min = 2, max = 100, message = "Currency name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Current price must be greater than 0")
    private BigDecimal currentPrice;

    private BigDecimal priceChange24h;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private Boolean isActive = true;

    // Constructors
    public CurrencyCreateRequest() {}

    public CurrencyCreateRequest(String symbol, String name, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol != null ? symbol.toUpperCase() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getPriceChange24h() {
        return priceChange24h;
    }

    public void setPriceChange24h(BigDecimal priceChange24h) {
        this.priceChange24h = priceChange24h;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "CurrencyCreateRequest{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice=" + currentPrice +
                ", priceChange24h=" + priceChange24h +
                ", isActive=" + isActive +
                '}';
    }
}
