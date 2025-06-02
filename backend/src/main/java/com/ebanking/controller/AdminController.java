package com.ebanking.controller;

import com.ebanking.dto.request.BankAgentCreateRequest;
import com.ebanking.dto.request.CurrencyCreateRequest;
import com.ebanking.dto.response.BankAgentDTO;
import com.ebanking.dto.response.CurrencyDTO;
import com.ebanking.entity.UserStatus;
import com.ebanking.service.AdminService;
import com.ebanking.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin operations
 */
@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private CurrencyService currencyService;

    /**
     * Create a new bank agent
     *
     * @param request the bank agent creation request
     * @return ResponseEntity containing the created bank agent information
     */
    @PostMapping("/agents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBankAgent(@Valid @RequestBody BankAgentCreateRequest request) {
        logger.info("Creating bank agent with username: {}", request.getUsername());

        try {
            BankAgentDTO createdAgent = adminService.createBankAgent(request);
            logger.info("Bank agent created successfully with ID: {}", createdAgent.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAgent);

        } catch (Exception e) {
            logger.error("Failed to create bank agent: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bank agent creation failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get all bank agents
     *
     * @return ResponseEntity containing list of all bank agents
     */
    @GetMapping("/agents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllBankAgents() {
        logger.debug("Fetching all bank agents");

        try {
            List<BankAgentDTO> agents = adminService.getAllBankAgents();
            logger.debug("Retrieved {} bank agents", agents.size());
            return ResponseEntity.ok(agents);

        } catch (Exception e) {
            logger.error("Failed to fetch bank agents: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch bank agents");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get bank agent by ID
     *
     * @param id the bank agent ID
     * @return ResponseEntity containing the bank agent information
     */
    @GetMapping("/agents/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBankAgentById(@PathVariable Long id) {
        logger.debug("Fetching bank agent by ID: {}", id);

        try {
            BankAgentDTO agent = adminService.getBankAgentById(id);
            return ResponseEntity.ok(agent);

        } catch (Exception e) {
            logger.error("Failed to fetch bank agent by ID {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bank agent not found");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Get bank agent by employee ID
     *
     * @param employeeId the employee ID
     * @return ResponseEntity containing the bank agent information
     */
    @GetMapping("/agents/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBankAgentByEmployeeId(@PathVariable String employeeId) {
        logger.debug("Fetching bank agent by employee ID: {}", employeeId);

        try {
            BankAgentDTO agent = adminService.getBankAgentByEmployeeId(employeeId);
            return ResponseEntity.ok(agent);

        } catch (Exception e) {
            logger.error("Failed to fetch bank agent by employee ID {}: {}", employeeId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bank agent not found");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Get bank agents by branch
     *
     * @param branch the branch name
     * @return ResponseEntity containing list of bank agents in the branch
     */
    @GetMapping("/agents/branch/{branch}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBankAgentsByBranch(@PathVariable String branch) {
        logger.debug("Fetching bank agents by branch: {}", branch);

        try {
            List<BankAgentDTO> agents = adminService.getBankAgentsByBranch(branch);
            logger.debug("Retrieved {} bank agents for branch: {}", agents.size(), branch);
            return ResponseEntity.ok(agents);

        } catch (Exception e) {
            logger.error("Failed to fetch bank agents by branch {}: {}", branch, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch bank agents");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update bank agent status
     *
     * @param id the bank agent ID
     * @param status the new status
     * @return ResponseEntity containing the updated bank agent information
     */
    @PutMapping("/agents/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBankAgentStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        logger.info("Updating bank agent status for ID: {} to {}", id, status);

        try {
            BankAgentDTO updatedAgent = adminService.updateBankAgentStatus(id, status);
            logger.info("Bank agent status updated successfully for ID: {}", id);
            return ResponseEntity.ok(updatedAgent);

        } catch (Exception e) {
            logger.error("Failed to update bank agent status for ID {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update bank agent status");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete bank agent
     *
     * @param id the bank agent ID
     * @return ResponseEntity indicating deletion status
     */
    @DeleteMapping("/agents/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBankAgent(@PathVariable Long id) {
        logger.info("Deleting bank agent with ID: {}", id);

        try {
            adminService.deleteBankAgent(id);
            logger.info("Bank agent deleted successfully with ID: {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bank agent deleted successfully");
            response.put("id", id);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to delete bank agent with ID {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete bank agent");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get bank agent statistics
     *
     * @return ResponseEntity containing bank agent statistics
     */
    @GetMapping("/agents/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBankAgentStatistics() {
        logger.debug("Fetching bank agent statistics");

        try {
            AdminService.BankAgentStatistics statistics = adminService.getBankAgentStatistics();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Failed to fetch bank agent statistics: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ==================== CURRENCY MANAGEMENT ENDPOINTS ====================

    /**
     * Get all supported currencies with current Binance rates
     *
     * @return ResponseEntity containing list of all currencies
     */
    @GetMapping("/currencies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCurrencies() {
        logger.debug("Fetching all supported currencies");

        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            logger.debug("Retrieved {} currencies", currencies.size());

            Map<String, Object> response = new HashMap<>();
            response.put("currencies", currencies);
            response.put("total", currencies.size());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to fetch currencies: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch currencies");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Add or update a currency manually
     *
     * @param request the currency creation/update request
     * @return ResponseEntity containing the created/updated currency information
     */
    @PostMapping("/currencies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createOrUpdateCurrency(@Valid @RequestBody CurrencyCreateRequest request) {
        logger.info("Creating or updating currency: {}", request.getSymbol());

        try {
            CurrencyDTO currency = currencyService.createOrUpdateCurrency(request);
            logger.info("Currency created/updated successfully: {}", currency.getSymbol());

            Map<String, Object> response = new HashMap<>();
            response.put("currency", currency);
            response.put("message", "Currency created/updated successfully");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Failed to create/update currency: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Currency creation/update failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get currency by symbol
     *
     * @param symbol the currency symbol
     * @return ResponseEntity containing the currency information
     */
    @GetMapping("/currencies/{symbol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCurrencyBySymbol(@PathVariable String symbol) {
        logger.debug("Fetching currency by symbol: {}", symbol);

        try {
            CurrencyDTO currency = currencyService.getCurrencyBySymbol(symbol);
            return ResponseEntity.ok(currency);

        } catch (Exception e) {
            logger.error("Failed to fetch currency by symbol {}: {}", symbol, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Currency not found");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Update currency status (active/inactive)
     *
     * @param symbol the currency symbol
     * @param isActive the new status
     * @return ResponseEntity containing the updated currency information
     */
    @PutMapping("/currencies/{symbol}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCurrencyStatus(@PathVariable String symbol, @RequestParam Boolean isActive) {
        logger.info("Updating currency status for symbol: {} to {}", symbol, isActive);

        try {
            CurrencyDTO updatedCurrency = currencyService.updateCurrencyStatus(symbol, isActive);
            logger.info("Currency status updated successfully for symbol: {}", symbol);

            Map<String, Object> response = new HashMap<>();
            response.put("currency", updatedCurrency);
            response.put("message", "Currency status updated successfully");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to update currency status for symbol {}: {}", symbol, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update currency status");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete a currency (only manually added currencies can be deleted)
     *
     * @param symbol the currency symbol
     * @return ResponseEntity indicating deletion status
     */
    @DeleteMapping("/currencies/{symbol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCurrency(@PathVariable String symbol) {
        logger.info("Deleting currency with symbol: {}", symbol);

        try {
            currencyService.deleteCurrency(symbol);
            logger.info("Currency deleted successfully: {}", symbol);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Currency deleted successfully");
            response.put("symbol", symbol);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to delete currency with symbol {}: {}", symbol, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete currency");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Refresh currencies from Binance API
     *
     * @return ResponseEntity containing the refreshed currencies
     */
    @PostMapping("/currencies/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refreshBinanceCurrencies() {
        logger.info("Refreshing currencies from Binance API");

        try {
            List<CurrencyDTO> refreshedCurrencies = currencyService.refreshBinanceCurrencies();
            logger.info("Successfully refreshed {} currencies from Binance", refreshedCurrencies.size());

            Map<String, Object> response = new HashMap<>();
            response.put("currencies", refreshedCurrencies);
            response.put("refreshed", refreshedCurrencies.size());
            response.put("message", "Currencies refreshed successfully from Binance");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to refresh currencies from Binance: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to refresh currencies");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get currency statistics
     *
     * @return ResponseEntity containing currency statistics
     */
    @GetMapping("/currencies/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCurrencyStatistics() {
        logger.debug("Fetching currency statistics");

        try {
            CurrencyService.CurrencyStatistics statistics = currencyService.getCurrencyStatistics();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Failed to fetch currency statistics: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch currency statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
