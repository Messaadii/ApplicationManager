package com.vermeg.ApplicationManager.entities;

import jakarta.persistence.*;

import java.io.IOException;

@Entity
@DiscriminatorValue("URLBased")
public class URLBased extends Resource{
    private String url;

    @Override
    public String getEarCommand(String destinationPath) throws IOException {
        destinationPath.substring(0, destinationPath.lastIndexOf("/") );
        return "wget -P " + destinationPath + " " + getUrl() + " \n";
    }

    @Override
    public String getFileName() {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public String getUrl() throws IOException {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}