package com.ebanking.service.impl;

import com.ebanking.entity.GlobalSettings;
import com.ebanking.repository.GlobalSettingsRepository;
import com.ebanking.service.GlobalSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of GlobalSettingsService for managing application settings
 */
@Service
@Transactional
public class GlobalSettingsServiceImpl implements GlobalSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalSettingsServiceImpl.class);

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @PostConstruct
    public void init() {
        initializeDefaultSettings();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllSettings() {
        logger.debug("Fetching all global settings");
        
        List<GlobalSettings> settings = globalSettingsRepository.findByIsActiveTrue();
        Map<String, Object> settingsMap = new HashMap<>();
        
        for (GlobalSettings setting : settings) {
            Object value = convertSettingValue(setting.getSettingValue(), setting.getSettingType());
            settingsMap.put(setting.getSettingKey(), value);
        }
        
        logger.debug("Retrieved {} global settings", settingsMap.size());
        return settingsMap;
    }

    @Override
    @Transactional(readOnly = true)
    public String getSettingValue(String key) {
        logger.debug("Fetching setting value for key: {}", key);
        
        Optional<GlobalSettings> setting = globalSettingsRepository.findBySettingKey(key);
        if (setting.isPresent() && setting.get().getIsActive()) {
            return setting.get().getSettingValue();
        }
        
        logger.warn("Setting not found or inactive for key: {}", key);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSettingValueAsDecimal(String key) {
        String value = getSettingValue(key);
        if (value != null) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                logger.error("Failed to convert setting value to BigDecimal for key: {}, value: {}", key, value);
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSettingValueAsInteger(String key) {
        String value = getSettingValue(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.error("Failed to convert setting value to Integer for key: {}, value: {}", key, value);
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean getSettingValueAsBoolean(String key) {
        String value = getSettingValue(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    @Override
    public void updateSetting(String key, String value) {
        logger.info("Updating setting: {} = {}", key, value);
        
        Optional<GlobalSettings> existingSetting = globalSettingsRepository.findBySettingKey(key);
        if (existingSetting.isPresent()) {
            GlobalSettings setting = existingSetting.get();
            setting.setSettingValue(value);
            globalSettingsRepository.save(setting);
            logger.info("Setting updated successfully: {}", key);
        } else {
            logger.error("Setting not found for key: {}", key);
            throw new RuntimeException("Setting not found: " + key);
        }
    }

    @Override
    public void createOrUpdateSetting(String key, String value, String type, String description) {
        logger.info("Creating or updating setting: {} = {} (type: {})", key, value, type);
        
        Optional<GlobalSettings> existingSetting = globalSettingsRepository.findBySettingKey(key);
        GlobalSettings setting;
        
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setSettingValue(value);
            setting.setSettingType(type);
            setting.setDescription(description);
        } else {
            setting = new GlobalSettings(key, value, type, description);
        }
        
        globalSettingsRepository.save(setting);
        logger.info("Setting saved successfully: {}", key);
    }

    @Override
    public void initializeDefaultSettings() {
        logger.info("Initializing default global settings");

        // Initialize maxClientAccountBalance
        if (!globalSettingsRepository.existsBySettingKey("maxClientAccountBalance")) {
            createOrUpdateSetting(
                "maxClientAccountBalance",
                "1000000.00",
                "DECIMAL",
                "Maximum balance allowed for client accounts"
            );
        }

        // Initialize maxDailyNewClients
        if (!globalSettingsRepository.existsBySettingKey("maxDailyNewClients")) {
            createOrUpdateSetting(
                "maxDailyNewClients",
                "50",
                "NUMBER",
                "Maximum number of new clients that can be registered per day"
            );
        }

        // Initialize feePercentage
        if (!globalSettingsRepository.existsBySettingKey("feePercentage")) {
            createOrUpdateSetting(
                "feePercentage",
                "1.5",
                "DECIMAL",
                "Global fee percentage applied to transfers and crypto purchases from main account"
            );
        }

        logger.info("Default global settings initialization completed");
    }

    /**
     * Convert setting value based on its type
     */
    private Object convertSettingValue(String value, String type) {
        if (value == null) return null;
        
        try {
            switch (type.toUpperCase()) {
                case "NUMBER":
                    return Integer.parseInt(value);
                case "DECIMAL":
                    return new BigDecimal(value);
                case "BOOLEAN":
                    return Boolean.parseBoolean(value);
                case "STRING":
                default:
                    return value;
            }
        } catch (Exception e) {
            logger.warn("Failed to convert setting value: {} (type: {}), returning as string", value, type);
            return value;
        }
    }
}
