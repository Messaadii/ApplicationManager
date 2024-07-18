package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.Database;
import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;
import com.vermeg.ApplicationManager.services.DatabaseService;
import com.vermeg.ApplicationManager.services.ScriptExecutionResultService;
import com.vermeg.ApplicationManager.services.impl.DatabaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    private DatabaseService baseDonneService;

    @PostMapping("/create")
    public ResponseEntity<?> createBaseDonne(@RequestBody Database baseDonne) {
        try {
            Database createdBaseDonne = baseDonneService.save(baseDonne);
            return new ResponseEntity<>(createdBaseDonne, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/execute-script")
    public ScriptExecutionResult executeSqlFile(@RequestParam String databaseName, @RequestParam String filePath) {
        ScriptExecutionResult result = baseDonneService.executeScript(databaseName, filePath);
        return result;
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDatabase(@PathVariable String aleas, @RequestBody Database database) {
        try {
            Database updatedDatabase = baseDonneService.update(aleas, database);
            return new ResponseEntity<>(updatedDatabase, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDatabase(@PathVariable String aleas) {
        try {
            baseDonneService.delete(aleas);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
