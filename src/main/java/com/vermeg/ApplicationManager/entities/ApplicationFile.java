package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class ApplicationFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "id_")
    private Integer id;
    @Column(name = "path_")
    private String path ;
    @Column(name = "newValue_")
    private byte[] newValue ;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private AppUpdaterConfig appUpdaterConfig;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getNewValue() {
        return newValue;
    }

    public void setNewValue( byte[] newValue ) {
        this.newValue = newValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUpdaterConfig getAppUpdaterConfig() {
        return appUpdaterConfig;
    }

    public void setAppUpdaterConfig(AppUpdaterConfig appUpdaterConfig) {
        this.appUpdaterConfig = appUpdaterConfig;
    }
}
