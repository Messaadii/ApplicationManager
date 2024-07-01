package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class ApplicationFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String path ;

    @Lob // This annotation specifies that the attribute will be stored as a large object (BLOB or CLOB) in the database
    @Column(columnDefinition = "BLOB")
    byte[] newValue ;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    AppUpdaterConfig appUpdaterConfig;

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
