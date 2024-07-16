package com.vermeg.ApplicationManager.entities;

public class SSHCredentials {
    private String username;
    private String password;
    private String hostname;
    private int port;

    public SSHCredentials(String username, String password, String hostname, int port) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getHostname() { return hostname; }
    public int getPort() { return port; }
}
