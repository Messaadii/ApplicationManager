package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "command")

public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    @JsonIgnore
    private Long id;
    @Column(name = "command_")
    private String command ;
    @Column(name = "runAsRoot_")
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

    public void setId(Long id) {
        this.id = id;
    }

    public Virgit pushtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

}
