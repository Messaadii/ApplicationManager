package com.vermeg.ApplicationManager.controllers;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import com.vermeg.ApplicationManager.services.AppUpdaterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/appUpdaterConfig")
public class AppUpdaterConfigController {

    AppUpdaterConfigService appUpdaterConfigService;

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
    public void getAppUpdaterConfigService(@PathVariable String name) throws JSchException, IOException, SftpException {
        appUpdaterConfigService.getAppUpdaterConfigByName(name).deploy();
        System.out.println("i am here");
    }
}
