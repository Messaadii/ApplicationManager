package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@DiscriminatorValue("VirtualMachineResource")
public class VirtualMachineResource extends Resource {
    @Column(name = "earPath_")
    private String earPath;
    @Column(name = "tempPath_")
    private String tempPath;
    @Column(name = "backupFolderPath_")
    private String backupFolderPath;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "deployOn" )
    private AppUpdaterConfig deployOnAUC;
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

    public AppUpdaterConfig getDeployOnAUC() {
        return deployOnAUC;
    }

    public void setDeployOnAUC(AppUpdaterConfig deployOnAUC) {
        this.deployOnAUC = deployOnAUC;
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