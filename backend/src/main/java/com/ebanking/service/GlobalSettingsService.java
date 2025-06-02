package com.ebanking.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for global settings operations
 */
public interface GlobalSettingsService {

    /**
     * Get all global settings as a map
     * @return map of setting key to setting value
     */
    Map<String, Object> getAllSettings();

    /**
     * Get setting value by key
     * @param key the setting key
     * @return setting value as string
     */
    String getSettingValue(String key);

    /**
     * Get setting value as BigDecimal
     * @param key the setting key
     * @return setting value as BigDecimal
     */
    BigDecimal getSettingValueAsDecimal(String key);

    /**
     * Get setting value as Integer
     * @param key the setting key
     * @return setting value as Integer
     */
    Integer getSettingValueAsInteger(String key);

    /**
     * Get setting value as Boolean
     * @param key the setting key
     * @return setting value as Boolean
     */
    Boolean getSettingValueAsBoolean(String key);

    /**
     * Update setting value
     * @param key the setting key
     * @param value the new value
     */
    void updateSetting(String key, String value);

    /**
     * Create or update setting
     * @param key the setting key
     * @param value the setting value
     * @param type the setting type
     * @param description the setting description
     */
    void createOrUpdateSetting(String key, String value, String type, String description);

    /**
     * Initialize default settings if they don't exist
     */
    void initializeDefaultSettings();
}
