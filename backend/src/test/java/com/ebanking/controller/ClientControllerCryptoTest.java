package com.ebanking.controller;

import com.ebanking.dto.request.CryptoBuyRequest;
import com.ebanking.dto.request.CryptoSellRequest;
import com.ebanking.dto.request.WalletUpdateRequest;
import com.ebanking.dto.response.TransactionDTO;
import com.ebanking.entity.Client;
import com.ebanking.service.ClientService;
import com.ebanking.service.impl.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ClientController cryptocurrency endpoints
 */
@ExtendWith(MockitoExtension.class)
public class ClientControllerCryptoTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDetailsServiceImpl.UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
        objectMapper = new ObjectMapper();

        // Create a mock Client entity
        Client mockClient = new Client();
        mockClient.setId(1L);
        mockClient.setUsername("testclient");
        mockClient.setEmail("test@example.com");
        mockClient.setPassword("password");
        mockClient.setRole("ROLE_CLIENT");

        // Mock security context
        userPrincipal = new UserDetailsServiceImpl.UserPrincipal(mockClient);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCryptoWallet_Success() throws Exception {
        // Arrange
        Map<String, Object> walletData = new HashMap<>();
        walletData.put("clientId", 1L);
        walletData.put("btcBalance", new BigDecimal("0.5"));
        walletData.put("ethBalance", new BigDecimal("2.0"));
        walletData.put("totalValueMAD", new BigDecimal("50000.00"));

        when(clientService.getCryptoWallet(1L)).thenReturn(walletData);

        // Act & Assert
        mockMvc.perform(get("/api/client/crypto/wallet")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.btcBalance").value(0.5))
                .andExpect(jsonPath("$.ethBalance").value(2.0))
                .andExpect(jsonPath("$.totalValueMAD").value(50000.00));

        verify(clientService).getCryptoWallet(1L);
    }

    @Test
    void testGetCryptoRates_Success() throws Exception {
        // Arrange
        Map<String, Object> rates = new HashMap<>();
        rates.put("BTC", new BigDecimal("45000.00"));
        rates.put("ETH", new BigDecimal("3000.00"));
        rates.put("USDT", new BigDecimal("10.50"));
        rates.put("timestamp", System.currentTimeMillis());

        when(clientService.getCryptoRates()).thenReturn(rates);

        // Act & Assert
        mockMvc.perform(get("/api/client/crypto/rates")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.BTC").value(45000.00))
                .andExpect(jsonPath("$.ETH").value(3000.00))
                .andExpect(jsonPath("$.USDT").value(10.50));

        verify(clientService).getCryptoRates();
    }

    @Test
    void testBuyCryptocurrency_Success() throws Exception {
        // Arrange
        CryptoBuyRequest request = new CryptoBuyRequest();
        request.setCryptoType("BTC");
        request.setAmount(new BigDecimal("1000.00"));
        request.setExchangeRate(new BigDecimal("45000.00"));
        request.setWalletAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        request.setDescription("Test BTC purchase");

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId("TXN001");
        transactionDTO.setAmount(new BigDecimal("1000.00"));
        transactionDTO.setType("CRYPTO_BUY");
        transactionDTO.setStatus("PENDING");

        when(clientService.buyCryptocurrency(any(CryptoBuyRequest.class), eq(1L)))
                .thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/client/crypto/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN001"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.type").value("CRYPTO_BUY"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(clientService).buyCryptocurrency(any(CryptoBuyRequest.class), eq(1L));
    }

    @Test
    void testSellCryptocurrency_Success() throws Exception {
        // Arrange
        CryptoSellRequest request = new CryptoSellRequest();
        request.setCryptoType("BTC");
        request.setCryptoAmount(new BigDecimal("0.02"));
        request.setExchangeRate(new BigDecimal("45000.00"));
        request.setWalletAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        request.setDescription("Test BTC sale");

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId("TXN002");
        transactionDTO.setAmount(new BigDecimal("900.00"));
        transactionDTO.setType("CRYPTO_SELL");
        transactionDTO.setStatus("PENDING");

        when(clientService.sellCryptocurrency(any(CryptoSellRequest.class), eq(1L)))
                .thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/client/crypto/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN002"))
                .andExpect(jsonPath("$.amount").value(900.00))
                .andExpect(jsonPath("$.type").value("CRYPTO_SELL"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(clientService).sellCryptocurrency(any(CryptoSellRequest.class), eq(1L));
    }

    @Test
    void testGetCryptoTransactions_Success() throws Exception {
        // Arrange
        List<TransactionDTO> transactions = Arrays.asList(
                createTransactionDTO("TXN001", "CRYPTO_BUY", new BigDecimal("1000.00")),
                createTransactionDTO("TXN002", "CRYPTO_SELL", new BigDecimal("900.00"))
        );

        when(clientService.getCryptoTransactionHistory(1L)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/client/crypto/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value("TXN001"))
                .andExpect(jsonPath("$[0].type").value("CRYPTO_BUY"))
                .andExpect(jsonPath("$[1].transactionId").value("TXN002"))
                .andExpect(jsonPath("$[1].type").value("CRYPTO_SELL"));

        verify(clientService).getCryptoTransactionHistory(1L);
    }

    @Test
    void testUpdateWalletAddress_Success() throws Exception {
        // Arrange
        WalletUpdateRequest request = new WalletUpdateRequest();
        request.setNewWalletAddress("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");

        Map<String, Object> updatedWallet = new HashMap<>();
        updatedWallet.put("clientId", 1L);
        updatedWallet.put("btcWalletAddress", "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
        updatedWallet.put("message", "Wallet address updated successfully");

        when(clientService.updateWalletAddress(any(WalletUpdateRequest.class), eq(1L)))
                .thenReturn(updatedWallet);

        // Act & Assert
        mockMvc.perform(put("/api/client/crypto/wallet/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.btcWalletAddress").value("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"))
                .andExpect(jsonPath("$.message").value("Wallet address updated successfully"));

        verify(clientService).updateWalletAddress(any(WalletUpdateRequest.class), eq(1L));
    }

    private TransactionDTO createTransactionDTO(String id, String type, BigDecimal amount) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(id);
        dto.setType(type);
        dto.setAmount(amount);
        dto.setStatus("COMPLETED");
        dto.setDate(LocalDateTime.now());
        return dto;
    }
}
