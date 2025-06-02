package com.ebanking.repository;

import com.ebanking.entity.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for GlobalSettings entity
 */
@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {

    /**
     * Find setting by key
     * @param settingKey the setting key
     * @return Optional containing the setting if found
     */
    Optional<GlobalSettings> findBySettingKey(String settingKey);

    /**
     * Find all active settings
     * @return list of active settings
     */
    List<GlobalSettings> findByIsActiveTrue();

    /**
     * Find settings by type
     * @param settingType the setting type
     * @return list of settings with the specified type
     */
    List<GlobalSettings> findBySettingType(String settingType);

    /**
     * Find active settings by type
     * @param settingType the setting type
     * @return list of active settings with the specified type
     */
    @Query("SELECT gs FROM GlobalSettings gs WHERE gs.settingType = :settingType AND gs.isActive = true")
    List<GlobalSettings> findActiveBySettingType(@Param("settingType") String settingType);

    /**
     * Check if setting key exists
     * @param settingKey the setting key
     * @return true if exists, false otherwise
     */
    boolean existsBySettingKey(String settingKey);

    /**
     * Count active settings
     * @return number of active settings
     */
    @Query("SELECT COUNT(gs) FROM GlobalSettings gs WHERE gs.isActive = true")
    long countActiveSettings();
}
