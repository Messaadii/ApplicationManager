package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.Database;
import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;

public interface DatabaseService {
    Database save(Database database);
    Database update(String id, Database database);
    ScriptExecutionResult executeScript(String databaseName, String filePath);
    void delete(String id);
    Database findById(String id);

}
