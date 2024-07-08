package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.UpdateResult;
import com.vermeg.ApplicationManager.services.UpdateResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/update-result")
public class UpdateResultController {
    UpdateResultService updateResultService;

    @Autowired
    public UpdateResultController(UpdateResultService updateResultService) {
        this.updateResultService = updateResultService;
    }

    @GetMapping("/getAll")
    public List<UpdateResult> getAllUpdateResults() {
        return updateResultService.getAllUpdateResults();
    }

    @PostMapping("/save")
    public UpdateResult save(@RequestBody UpdateResult updateResult) {
        return updateResultService.create(updateResult);
    }
}
