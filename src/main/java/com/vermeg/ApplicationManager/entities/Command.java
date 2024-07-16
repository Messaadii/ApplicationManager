package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "command")

public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String command ;
    private Boolean runAsRoot ;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private VirtualMachine virtualMachine;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private AppUpdaterConfig appUpdaterConfigBefore;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private AppUpdaterConfig appUpdaterConfigAfter;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getRunAsRoot() {
        return runAsRoot;
    }

    public void setRunAsRoot(Boolean runAsRoot) {
        this.runAsRoot = runAsRoot;
    }

    public AppUpdaterConfig getAppUpdaterConfigBefore() {
        return appUpdaterConfigBefore;
    }

    public void setAppUpdaterConfigBefore(AppUpdaterConfig appUpdaterConfigBefore) {
        this.appUpdaterConfigBefore = appUpdaterConfigBefore;
    }

    public AppUpdaterConfig getAppUpdaterConfigAfter() {
        return appUpdaterConfigAfter;
    }

    public void setAppUpdaterConfigAfter(AppUpdaterConfig appUpdaterConfigAfter) {
        this.appUpdaterConfigAfter = appUpdaterConfigAfter;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

}
