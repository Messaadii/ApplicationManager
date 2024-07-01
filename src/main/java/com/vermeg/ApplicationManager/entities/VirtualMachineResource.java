package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@DiscriminatorValue("VirtualMachineResource")
public class VirtualMachineResource extends Resource {
    private String earPath;
    private String tempPath;
    private String backupFolderPath;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "deployOn" )
    private AppUpdaterConfig appUpdaterConfig;
    @ManyToOne
    private VirtualMachine virtualMachine;

    @Override
    public String getEarCommand(String destinationPath) {
        return "sshpass -p '" + virtualMachine.getPassword() + "' scp " + virtualMachine.getUser() + "@" + virtualMachine.getHost() + ":" + earPath + " " + destinationPath ;
    }

    @Override
    public String getFileName() {
        return earPath.substring(earPath.lastIndexOf('/') + 1);
    }


    public String getEarPath() {
        return earPath;
    }

    public void setEarPath(String earPath) {
        this.earPath = earPath;
    }

    public AppUpdaterConfig getAppUpdaterConfig() {
        return appUpdaterConfig;
    }

    public void setAppUpdaterConfig(AppUpdaterConfig appUpdaterConfig) {
        this.appUpdaterConfig = appUpdaterConfig;
    }

    public void setPath(String path) {
        this.earPath = earPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getBackupFolderPath() {
        return backupFolderPath;
    }

    public void setBackupFolderPath(String backupFolderPath) {
        this.backupFolderPath = backupFolderPath;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }
}