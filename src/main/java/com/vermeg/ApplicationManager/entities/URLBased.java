package com.vermeg.ApplicationManager.entities;

import jakarta.persistence.*;

import java.io.IOException;

@Entity
@DiscriminatorValue("URLBased")
public class URLBased extends Resource{
    @Column(name = "url_")
    private String url;

    @Override
    public String getEarCommand(String destinationPath) throws IOException {
        destinationPath.substring(0, destinationPath.lastIndexOf("/") );
        return "wget -P " + destinationPath + " " + commandUrl() + " \n";
    }

    @Override
    public String getFileName() {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public String getUrl() {
        return url;
    }

    public String commandUrl() throws IOException {
        return getUrl();
    }

    public void setUrl(String url) {
        this.url = url;
    }

}