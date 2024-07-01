package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;

public interface AppUpdaterConfigService {
    AppUpdaterConfig getAppUpdaterConfig(String name);

    AppUpdaterConfig create(AppUpdaterConfig appUpdaterConfig);

    AppUpdaterConfig getAppUpdaterConfigByName(String name);
}
