package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import com.vermeg.ApplicationManager.entities.VirtualMachine;

import java.util.List;

public interface AppUpdaterConfigService {
    AppUpdaterConfig getAppUpdaterConfig(String name);

    AppUpdaterConfig create(AppUpdaterConfig appUpdaterConfig);

    AppUpdaterConfig getAppUpdaterConfigByName(String name);


    List<AppUpdaterConfig> getAllAppUpdaterConfigs();

    void deleteAppUpdaterConfig(String name);
}
