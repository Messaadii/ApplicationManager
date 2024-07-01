package com.vermeg.ApplicationManager.services.impl;

import com.vermeg.ApplicationManager.repositories.AppUpdaterConfigRepository;
import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import com.vermeg.ApplicationManager.services.AppUpdaterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AppUpdaterConfigServiceImpl implements AppUpdaterConfigService{

    private AppUpdaterConfigRepository appUpdaterConfigServiceRepository;

    @Autowired
    public AppUpdaterConfigServiceImpl(AppUpdaterConfigRepository appUpdaterConfigServiceRepository) {
        this.appUpdaterConfigServiceRepository = appUpdaterConfigServiceRepository;
    }
    @Override
    public AppUpdaterConfig getAppUpdaterConfig(String name) {
        return appUpdaterConfigServiceRepository.getAppUpdaterConfigByName(name);
    }

    @Override
    public AppUpdaterConfig create(AppUpdaterConfig appUpdaterConfig) {
        return appUpdaterConfigServiceRepository.save(appUpdaterConfig);
    }

    @Override
    public AppUpdaterConfig getAppUpdaterConfigByName(String name) {
        return appUpdaterConfigServiceRepository.findByName(name);
    }
}
