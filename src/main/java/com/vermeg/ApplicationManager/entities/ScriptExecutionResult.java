package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class ScriptExecutionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    private Long id;
    @Column(name = "path_")
    private String path;
    @Column(name = "date_")
    private Date date;
    @ManyToOne
    @JoinColumn(name = "database_alias")
    @JsonBackReference
    private Database database;
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ExecutedQuery> executedQueries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public List<ExecutedQuery> getExecutedQueries() {
        return executedQueries;
    }

    public void setExecutedQueries(List<ExecutedQuery> executedQueries) {
        this.executedQueries = executedQueries;
    }

    public void addExecutedQueries(ExecutedQuery executedQuery) {
        executedQuery.setResult(this);
        this.executedQueries.add(executedQuery);
    }
}
