package com.vermeg.ApplicationManager.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ExecutedQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    private Long id;
    @Column(columnDefinition = "TEXT", name = "query_")
    private String query;
    @Column(columnDefinition = "TEXT", name = "executionResult_")
    private String executionResult;
    @ManyToOne
    @JsonBackReference
    private ScriptExecutionResult result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }

    public ScriptExecutionResult getResult() {
        return result;
    }

    public void setResult(ScriptExecutionResult result) {
        this.result = result;
    }
}
