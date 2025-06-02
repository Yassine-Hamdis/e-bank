package com.ebanking.service.impl;

import com.ebanking.dto.response.CurrencyDTO;
import com.ebanking.service.BinanceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of BinanceService for cryptocurrency data integration
 */
@Service
public class BinanceServiceImpl implements BinanceService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceServiceImpl.class);
    
    private static final String BINANCE_API_BASE_URL = "https://api.binance.com/api/v3";
    private static final String TICKER_PRICE_ENDPOINT = "/ticker/price";
    private static final String TICKER_24HR_ENDPOINT = "/ticker/24hr";
    
    // Popular cryptocurrencies to fetch by default
    private static final List<String> POPULAR_SYMBOLS = Arrays.asList(
        "BTCUSDT", "ETHUSDT", "BNBUSDT", "ADAUSDT", "XRPUSDT", 
        "SOLUSDT", "DOTUSDT", "DOGEUSDT", "AVAXUSDT", "MATICUSDT"
    );

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<CurrencyDTO> fetchPopularCryptocurrencies() {
        logger.info("Fetching popular cryptocurrencies from Binance");
        
        List<CurrencyDTO> currencies = new ArrayList<>();
        
        for (String symbol : POPULAR_SYMBOLS) {
            try {
                CurrencyDTO currency = fetchCryptocurrencyTicker(symbol);
                if (currency != null) {
                    currencies.add(currency);
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch data for symbol: {}, error: {}", symbol, e.getMessage());
            }
        }
        
        logger.info("Successfully fetched {} popular cryptocurrencies", currencies.size());
        return currencies;
    }

    @Override
    public CurrencyDTO fetchCryptocurrencyPrice(String symbol) {
        logger.debug("Fetching price for symbol: {}", symbol);
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = BINANCE_API_BASE_URL + TICKER_PRICE_ENDPOINT + "?symbol=" + symbol;
            HttpGet request = new HttpGet(url);
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    CurrencyDTO currency = new CurrencyDTO();
                    currency.setSymbol(extractBaseSymbol(jsonNode.get("symbol").asText()));
                    currency.setName(getDisplayName(currency.getSymbol()));
                    currency.setCurrentPrice(new BigDecimal(jsonNode.get("price").asText()));
                    currency.setLastUpdated(LocalDateTime.now());
                    currency.setIsActive(true);
                    currency.setIsManual(false);
                    
                    return currency;
                } else {
                    logger.error("Binance API error for symbol {}: {} - {}", 
                               symbol, response.getStatusLine().getStatusCode(), responseBody);
                    throw new RuntimeException("Failed to fetch price from Binance: " + responseBody);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching cryptocurrency price for symbol: {}", symbol, e);
            throw new RuntimeException("Failed to fetch cryptocurrency price: " + e.getMessage(), e);
        }
    }

    @Override
    public CurrencyDTO fetchCryptocurrencyTicker(String symbol) {
        logger.debug("Fetching ticker data for symbol: {}", symbol);
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = BINANCE_API_BASE_URL + TICKER_24HR_ENDPOINT + "?symbol=" + symbol;
            HttpGet request = new HttpGet(url);
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    CurrencyDTO currency = new CurrencyDTO();
                    currency.setSymbol(extractBaseSymbol(jsonNode.get("symbol").asText()));
                    currency.setName(getDisplayName(currency.getSymbol()));
                    currency.setCurrentPrice(new BigDecimal(jsonNode.get("lastPrice").asText()));
                    currency.setPriceChange24h(new BigDecimal(jsonNode.get("priceChangePercent").asText()));
                    currency.setVolume24h(new BigDecimal(jsonNode.get("volume").asText()));
                    currency.setLastUpdated(LocalDateTime.now());
                    currency.setIsActive(true);
                    currency.setIsManual(false);
                    
                    return currency;
                } else {
                    logger.error("Binance API error for symbol {}: {} - {}", 
                               symbol, response.getStatusLine().getStatusCode(), responseBody);
                    throw new RuntimeException("Failed to fetch ticker from Binance: " + responseBody);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching cryptocurrency ticker for symbol: {}", symbol, e);
            throw new RuntimeException("Failed to fetch cryptocurrency ticker: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isApiAvailable() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = BINANCE_API_BASE_URL + "/ping";
            HttpGet request = new HttpGet(url);
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getStatusLine().getStatusCode() == 200;
            }
        } catch (Exception e) {
            logger.warn("Binance API is not available: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getSupportedSymbols() {
        return new ArrayList<>(POPULAR_SYMBOLS);
    }

    @Override
    public BigDecimal getMadToUsdRate() {
        logger.debug("Fetching MAD to USD exchange rate");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Using a free forex API to get MAD/USD rate
            // Alternative: could use Binance if they have MAD pairs, or other forex APIs
            String url = "https://api.exchangerate-api.com/v4/latest/MAD";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode rates = jsonNode.get("rates");

                    if (rates != null && rates.has("USD")) {
                        BigDecimal madToUsdRate = new BigDecimal(rates.get("USD").asText());
                        logger.info("Fetched MAD to USD rate: {}", madToUsdRate);
                        return madToUsdRate;
                    } else {
                        throw new RuntimeException("USD rate not found in response");
                    }
                } else {
                    logger.error("Exchange rate API error: {} - {}",
                               response.getStatusLine().getStatusCode(), responseBody);
                    throw new RuntimeException("Failed to fetch MAD/USD rate: " + responseBody);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch live MAD/USD rate, using fallback rate: {}", e.getMessage());
            // Fallback to approximate rate (1 MAD ≈ 0.10 USD as of 2024)
            return new BigDecimal("0.10");
        }
    }

    /**
     * Extract base symbol from trading pair (e.g., "BTCUSDT" -> "BTC")
     */
    private String extractBaseSymbol(String tradingPair) {
        if (tradingPair.endsWith("USDT")) {
            return tradingPair.substring(0, tradingPair.length() - 4);
        } else if (tradingPair.endsWith("BTC") || tradingPair.endsWith("ETH") || tradingPair.endsWith("BNB")) {
            return tradingPair.substring(0, tradingPair.length() - 3);
        }
        return tradingPair;
    }

    /**
     * Get display name for cryptocurrency symbol
     */
    private String getDisplayName(String symbol) {
        switch (symbol.toUpperCase()) {
            case "BTC": return "Bitcoin";
            case "ETH": return "Ethereum";
            case "BNB": return "Binance Coin";
            case "ADA": return "Cardano";
            case "XRP": return "Ripple";
            case "SOL": return "Solana";
            case "DOT": return "Polkadot";
            case "DOGE": return "Dogecoin";
            case "AVAX": return "Avalanche";
            case "MATIC": return "Polygon";
            default: return symbol.toUpperCase();
        }
    }
}
