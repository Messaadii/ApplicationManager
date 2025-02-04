package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.vermeg.ApplicationManager.helpers.EarDeployer;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.List;

@Entity
public class AppUpdaterConfig {
    @Id
    @Column(name = "name_")
    private String name ;
    @OneToMany(mappedBy = "appUpdaterConfigBefore",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Command> beforeUpdateCommands ;

    @OneToMany(mappedBy = "appUpdaterConfigAfter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Command> afterUpdateCommands ;

    @OneToMany(mappedBy = "appUpdaterConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationFile> applicationFiles;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Resource toBeDeployed;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VirtualMachineResource deployOn;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "appUpdaterConfig", cascade = CascadeType.REMOVE)
    private List<UpdateResult> updateResults;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApplicationFile> getApplicationFiles() {
        return applicationFiles;
    }

    public void setApplicationFiles(List<ApplicationFile> applicationFiles) {
        for(ApplicationFile applicationFile : applicationFiles){
            applicationFile.setAppUpdaterConfig(this);
        }
        this.applicationFiles = applicationFiles;
    }

    public Resource getToBeDeployed() {
        return toBeDeployed;
    }

    public void setToBeDeployed(Resource toBeDeployed) {
        toBeDeployed.setToBeDeployedAUC(this);
        this.toBeDeployed = toBeDeployed;
    }

    public VirtualMachineResource getDeployOn() {
        return deployOn;
    }

    public void setDeployOn(VirtualMachineResource deployOn) {
        this.deployOn = deployOn;
    }

    public List<Command> getBeforeUpdateCommands() {
        return beforeUpdateCommands;
    }

    public void setBeforeUpdateCommands(List<Command> beforeUpdateCommands) {
        for(Command command : beforeUpdateCommands){
            command.setAppUpdaterConfigBefore(this);
        }
        this.beforeUpdateCommands = beforeUpdateCommands;
    }

    public List<Command> getAfterUpdateCommands() {
        return afterUpdateCommands;
    }

    public void setAfterUpdateCommands(List<Command> afterUpdateCommands) {
        for(Command command : afterUpdateCommands){
            command.setAppUpdaterConfigAfter(this);
        }
        this.afterUpdateCommands = afterUpdateCommands;
    }

    public List<UpdateResult> getUpdateResults() {
        return updateResults;
    }

    public void setUpdateResults(List<UpdateResult> updateResults) {
        for (UpdateResult updateResult : updateResults){
            updateResult.setAppUpdaterConfig(this);
        }
        this.updateResults = updateResults;
    }
}
