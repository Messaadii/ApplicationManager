package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.io.IOException;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VirtualMachineResource.class, name = "VirtualMachineResource"),
        @JsonSubTypes.Type(value = URLBased.class, name = "URLBased"),
        @JsonSubTypes.Type(value = RegularExpressionBasedURL.class, name = "RegularExpressionBasedURL")
})
public abstract class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    @JsonIgnore
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "toBeDeployed")
    private AppUpdaterConfig toBeDeployedAUC;

    public abstract String getEarCommand(String destinationPath) throws IOException;

    public abstract String getFileName();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUpdaterConfig getToBeDeployedAUC() {
        return toBeDeployedAUC;
    }

    public void setToBeDeployedAUC(AppUpdaterConfig toBeDeployedAUC) {
        this.toBeDeployedAUC = toBeDeployedAUC;
    }
}
