package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.services.ScriptExecutionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("script-execution-result")
public class ScriptExecutionResultController {

    @Autowired
    ScriptExecutionResultService scriptExecutionResultService;

    @DeleteMapping("/delete-by-id/{resultId}")
    public ResponseEntity<String> deleteScriptExecutionResult(@PathVariable Long resultId) {
        scriptExecutionResultService.deleteById(resultId);
        return ResponseEntity.ok(" deleted successfully.");
    }
}
