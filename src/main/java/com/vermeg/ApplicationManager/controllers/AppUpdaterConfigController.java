package com.vermeg.ApplicationManager.controllers;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import com.vermeg.ApplicationManager.entities.UpdateResult;
import com.vermeg.ApplicationManager.entities.UpdateStatus;
import com.vermeg.ApplicationManager.helpers.EarDeployer;
import com.vermeg.ApplicationManager.services.AppUpdaterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/appUpdaterConfig")
public class AppUpdaterConfigController {

    private AppUpdaterConfigService appUpdaterConfigService;

    @Autowired
    public AppUpdaterConfigController(AppUpdaterConfigService appUpdaterConfigService) {
        this.appUpdaterConfigService = appUpdaterConfigService;
    }

    @GetMapping("/get/{name}")
    public AppUpdaterConfig getAppUpdaterConfig(@PathVariable String name) {
        return appUpdaterConfigService.getAppUpdaterConfig( name );
    }

    @PostMapping("/save")
    public AppUpdaterConfig updateAppUpdaterConfig(@RequestBody AppUpdaterConfig appUpdaterConfig) {
        return appUpdaterConfigService.create( appUpdaterConfig );
    }

    @GetMapping("/deploy/{name}")
    public UpdateResult getAppUpdaterConfigService(@PathVariable String name) {
        AppUpdaterConfig appUpdaterConfig = appUpdaterConfigService.getAppUpdaterConfigByName(name);
        EarDeployer earDeployer = null;
        try {
            earDeployer = new EarDeployer(appUpdaterConfig);
            earDeployer.deploy();
        } catch (Exception e) {
            earDeployer.getUpdateResult().appendLog(e.getMessage());
            earDeployer.getUpdateResult().setStatus(UpdateStatus.FAILED);
        }
        return earDeployer.getUpdateResult();
    }
}
