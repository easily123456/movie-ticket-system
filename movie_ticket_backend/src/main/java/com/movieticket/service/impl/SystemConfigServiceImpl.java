package com.movieticket.service.impl;

import com.movieticket.entity.SystemConfig;
import com.movieticket.repository.SystemConfigRepository;
import com.movieticket.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    @Override
    public SystemConfig createConfig(SystemConfig config) {
        if (systemConfigRepository.existsByConfigKey(config.getConfigKey())) {
            throw new RuntimeException("配置键已存在");
        }
        return systemConfigRepository.save(config);
    }

    @Override
    public SystemConfig updateConfig(SystemConfig config) {
        SystemConfig existingConfig = systemConfigRepository.findById(config.getId())
                .orElseThrow(() -> new RuntimeException("配置不存在"));

        if (!existingConfig.getConfigKey().equals(config.getConfigKey()) &&
                systemConfigRepository.existsByConfigKey(config.getConfigKey())) {
            throw new RuntimeException("配置键已存在");
        }

        existingConfig.setConfigKey(config.getConfigKey());
        existingConfig.setConfigValue(config.getConfigValue());
        existingConfig.setDescription(config.getDescription());

        return systemConfigRepository.save(existingConfig);
    }

    @Override
    public void deleteConfig(Long id) {
        SystemConfig config = systemConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置不存在"));
        systemConfigRepository.delete(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfig> getConfigById(Long id) {
        return systemConfigRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfig> getConfigByKey(String key) {
        return systemConfigRepository.findByConfigKey(key);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public String getConfigValue(String key) {
        return systemConfigRepository.findByConfigKey(key)  // 返回 Optional<SystemConfig>
                .map(SystemConfig::getConfigValue)      // 提取 configValue
                .orElse(null);                          // 处理空值
    }

    @Override
    @Transactional(readOnly = true)
    public String getConfigValue(String key, String defaultValue) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByKey(String key) {
        return systemConfigRepository.existsByConfigKey(key);
    }
}