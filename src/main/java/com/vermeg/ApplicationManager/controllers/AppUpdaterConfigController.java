package com.vermeg.ApplicationManager.controllers;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import com.vermeg.ApplicationManager.entities.UpdateResult;
import com.vermeg.ApplicationManager.entities.UpdateStatus;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.helpers.EarDeployer;
import com.vermeg.ApplicationManager.services.AppUpdaterConfigService;
import com.vermeg.ApplicationManager.services.UpdateResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/app-updater-config")
@CrossOrigin
public class AppUpdaterConfigController {

    private AppUpdaterConfigService appUpdaterConfigService;

    private UpdateResultController updateResultController ;

    @Autowired
    public AppUpdaterConfigController(AppUpdaterConfigService appUpdaterConfigService, UpdateResultController updateResultController) {
        this.appUpdaterConfigService = appUpdaterConfigService;
        this.updateResultController = updateResultController;
    }

    @GetMapping("/get/{name}")
    public AppUpdaterConfig getAppUpdaterConfig(@PathVariable String name) {
        return appUpdaterConfigService.getAppUpdaterConfig( name );
    }

    @PostMapping("/save")
    public AppUpdaterConfig updateAppUpdaterConfig(@RequestBody AppUpdaterConfig appUpdaterConfig) {
        return appUpdaterConfigService.create( appUpdaterConfig );
    }

    @GetMapping("/getAll")
    public List<AppUpdaterConfig> getAllAppUpdaterConfigs() {
        return appUpdaterConfigService.getAllAppUpdaterConfigs();
    }

    @PutMapping("/update/{name}")
    public AppUpdaterConfig updateAppUpdaterConfig(@PathVariable String name, @RequestBody AppUpdaterConfig appUpdaterConfig) {
        return appUpdaterConfigService.create( appUpdaterConfig );
    }

    @DeleteMapping("/delete/{name}")
    public void deleteAppUpdaterConfig(@PathVariable String name) {
        appUpdaterConfigService.deleteAppUpdaterConfig(name);
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
        this.updateResultController.save(earDeployer.getUpdateResult());
        return earDeployer.getUpdateResult();
    }
}
