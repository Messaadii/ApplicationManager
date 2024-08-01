package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.Database;
import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;
import com.vermeg.ApplicationManager.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    public ResponseEntity<ScriptExecutionResult> executeSqlFile(
            @RequestParam("databaseName") String databaseName,
            @RequestParam("file") MultipartFile file) {

        try {
            // Save file to a temporary location
            Path tempFilePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
            Files.write(tempFilePath, file.getBytes());

            // Call the service to execute the script
            ScriptExecutionResult result = baseDonneService.executeScript(databaseName, tempFilePath.toString());

            // Optionally delete the temp file after execution
            Files.deleteIfExists(tempFilePath);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDatabase(@PathVariable String id, @RequestBody Database database) {
        try {
            Database updatedDatabase = baseDonneService.update(id, database);
            return new ResponseEntity<>(updatedDatabase, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{alias}")
    public ResponseEntity<?> deleteDatabase(@PathVariable String alias) {
        try {
            baseDonneService.delete(alias);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Database>> getAllDatabases() {
        List<Database> databases = baseDonneService.findAll();

        return new ResponseEntity<>(databases, HttpStatus.OK);

    }
}
