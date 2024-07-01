package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String command ;
    Boolean runAsRoot ;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    AppUpdaterConfig appUpdaterConfigBefore;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    AppUpdaterConfig appUpdaterConfigAfter;

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
}
