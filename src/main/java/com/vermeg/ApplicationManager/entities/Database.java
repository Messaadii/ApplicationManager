package com.vermeg.ApplicationManager.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "`database`")
public class Database {
    @Id
    @Column(name = "alias_")
    private String alias;
    @Column(name = "port_")
    private int port;
    @Column(name = "host_")
    private String host;
    @Column(name = "name_")
    private String name;
    @Column(name = "user_")
    private String user;
    @Column(name = "password_")
    private String password;
    @Column(name = "type_")
    private String type;
    @OneToMany(mappedBy = "database")
    private List<ScriptExecutionResult> results;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ScriptExecutionResult> getResults() {
        return results;
    }

    public void setResults(List<ScriptExecutionResult> results) {
        this.results = results;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

