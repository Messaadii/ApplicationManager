package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "virtual_machine")

public class VirtualMachine {
    @Id
    @Column(name = "name_")
    private String name;

    @Column(name = "host_")
    private String host;
    @Column(name = "user_")
    private String user;
    @OneToMany(mappedBy = "virtualMachine",cascade = CascadeType.ALL)
    private List<Command> commands;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_")
    private String password;
    @Column(name = "port_")
    private int port;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "virtualMachine")
    private List<VirtualMachineResource> virtualMachineResources;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public List<VirtualMachineResource> getVirtualMachineResources() {
        return virtualMachineResources;
    }

    public void setVirtualMachineResources(List<VirtualMachineResource> virtualMachineResources) {
        this.virtualMachineResources = virtualMachineResources;
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

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        for(Command command : commands){
            command.setVirtualMachine(this);
        }
        this.commands = commands;
    }




    }

