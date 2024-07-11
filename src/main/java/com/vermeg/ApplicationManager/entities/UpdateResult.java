package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
public class UpdateResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    @JsonIgnore
    private Integer id;
    @Column(name = "executionDate_")
    private Date executionDate;

    @Lob
    @Column(name = "log_")
    private byte [] log;
    @Column(name = "status_")
    private UpdateStatus status;

    @ManyToOne
    private AppUpdaterConfig appUpdaterConfig;

    public UpdateResult(Date executionDate , UpdateStatus status) {
        this.executionDate = executionDate;
        this.status = status;
        log = new byte[0];
    }

    public UpdateResult() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getLog() {
        return new String(log);
    }

    public void setLog(String log) {
        this.log = log.getBytes();
    }

    public AppUpdaterConfig getAppUpdaterConfig() {
        return appUpdaterConfig;
    }

    public void setAppUpdaterConfig(AppUpdaterConfig appUpdaterConfig) {
        this.appUpdaterConfig = appUpdaterConfig;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateStatus status) {
        this.status = status;
    }

    public void appendLog(String line){
     if(line.trim().isEmpty()){
            return;
        }
        setLog(getLog() + line + "\n");
    }

    public void breakLine() {
        setLog(getLog() + "\n");
    }

    public void appendLog(String line,String type){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss:SSS");
        String formattedDate = now.format(formatter);

        if(line.trim().isEmpty()){
            return;
        }
        setLog(getLog() + formattedDate + " | " + type + ": " + line + "\n");
    }
}
