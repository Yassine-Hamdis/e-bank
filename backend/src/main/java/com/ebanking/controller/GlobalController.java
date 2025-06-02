package com.ebanking.controller;

import com.ebanking.service.GlobalSettingsService;
import com.ebanking.service.GlobalStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for global application statistics and settings
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class GlobalController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalController.class);

    @Autowired
    private GlobalStatsService globalStatsService;

    @Autowired
    private GlobalSettingsService globalSettingsService;

    /**
     * Get global application statistics
     * GET /api/stats/global
     *
     * @return ResponseEntity containing global statistics
     */
    @GetMapping("/stats/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getGlobalStatistics() {
        logger.debug("Fetching global application statistics");

        try {
            GlobalStatsService.GlobalApplicationStatistics statistics = globalStatsService.getGlobalStatistics();

            // Create response with additional metadata
            Map<String, Object> response = new HashMap<>();
            response.put("statistics", statistics);
            response.put("status", "success");
            response.put("message", "Global statistics retrieved successfully");
            response.put("timestamp", System.currentTimeMillis());

            logger.debug("Global statistics retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to fetch global statistics: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch global statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get global application settings
     * GET /api/settings/global
     *
     * @return ResponseEntity containing global settings
     */
    @GetMapping("/settings/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getGlobalSettings() {
        logger.debug("Fetching global application settings");

        try {
            Map<String, Object> settings = globalSettingsService.getAllSettings();

            // Create response with additional metadata
            Map<String, Object> response = new HashMap<>();
            response.put("settings", settings);
            response.put("status", "success");
            response.put("message", "Global settings retrieved successfully");
            response.put("timestamp", System.currentTimeMillis());

            logger.debug("Global settings retrieved successfully: {} settings", settings.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to fetch global settings: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch global settings");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update multiple global settings
     * PUT /api/settings/global
     *
     * @param requestBody the request body containing the settings to update
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping("/settings/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGlobalSettings(@RequestBody Map<String, Object> requestBody) {
        logger.info("Updating multiple global settings");

        try {
            Map<String, Object> updatedSettings = new HashMap<>();
            Map<String, String> errors = new HashMap<>();

            for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                try {
                    // Convert value to string for storage
                    String stringValue = value != null ? value.toString() : null;

                    if (stringValue == null) {
                        errors.put(key, "Value cannot be null");
                        continue;
                    }

                    globalSettingsService.updateSetting(key, stringValue);
                    updatedSettings.put(key, value);
                    logger.info("Updated setting: {} = {}", key, stringValue);

                } catch (Exception e) {
                    logger.error("Failed to update setting {}: {}", key, e.getMessage());
                    errors.put(key, e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", errors.isEmpty() ? "success" : "partial_success");
            response.put("message", errors.isEmpty() ?
                "All settings updated successfully" :
                "Some settings updated successfully, others failed");
            response.put("updatedSettings", updatedSettings);
            response.put("timestamp", System.currentTimeMillis());

            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

            logger.info("Global settings update completed. Updated: {}, Errors: {}",
                       updatedSettings.size(), errors.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to update global settings: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update settings");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update a specific global setting
     * PUT /api/settings/global/{key}
     *
     * @param key the setting key
     * @param requestBody the request body containing the new value
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping("/settings/global/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGlobalSetting(@PathVariable String key, @RequestBody Map<String, String> requestBody) {
        logger.info("Updating global setting: {}", key);

        try {
            String newValue = requestBody.get("value");
            if (newValue == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing value in request body");
                errorResponse.put("status", "error");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            globalSettingsService.updateSetting(key, newValue);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Setting updated successfully");
            response.put("key", key);
            response.put("value", newValue);
            response.put("timestamp", System.currentTimeMillis());

            logger.info("Global setting updated successfully: {} = {}", key, newValue);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to update global setting {}: {}", key, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update setting");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("key", key);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get a specific global setting
     * GET /api/settings/global/{key}
     *
     * @param key the setting key
     * @return ResponseEntity containing the setting value
     */
    @GetMapping("/settings/global/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getGlobalSetting(@PathVariable String key) {
        logger.debug("Fetching global setting: {}", key);

        try {
            String value = globalSettingsService.getSettingValue(key);

            if (value == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Setting not found");
                errorResponse.put("key", key);
                errorResponse.put("status", "error");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("key", key);
            response.put("value", value);
            response.put("status", "success");
            response.put("timestamp", System.currentTimeMillis());

            logger.debug("Global setting retrieved successfully: {} = {}", key, value);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to fetch global setting {}: {}", key, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch setting");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("key", key);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
