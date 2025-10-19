package com.movieticket.service;

import com.movieticket.entity.SystemConfig;
import java.util.List;
import java.util.Optional;

public interface SystemConfigService {
    SystemConfig createConfig(SystemConfig config);
    SystemConfig updateConfig(SystemConfig config);
    void deleteConfig(Long id);
    Optional<SystemConfig> getConfigById(Long id);
    Optional<SystemConfig> getConfigByKey(String key);
    List<SystemConfig> getAllConfigs();
    String getConfigValue(String key);
    String getConfigValue(String key, String defaultValue);
    boolean existsByKey(String key);
}