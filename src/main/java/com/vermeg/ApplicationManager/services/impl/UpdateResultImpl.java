package com.vermeg.ApplicationManager.services.impl;

import com.vermeg.ApplicationManager.entities.UpdateResult;
import com.vermeg.ApplicationManager.repositories.UpdateResultRepository;
import com.vermeg.ApplicationManager.services.UpdateResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateResultImpl implements UpdateResultService {

    UpdateResultRepository updateResultRepository;
    @Autowired
    public UpdateResultImpl(UpdateResultRepository updateResultRepository) {
        this.updateResultRepository = updateResultRepository;
    }

    @Override
    public List<UpdateResult> getAllUpdateResults() {
        return updateResultRepository.findAll();
    }

    @Override
    public UpdateResult create(UpdateResult updateResult) {
        return updateResultRepository.save(updateResult);
    }
}
