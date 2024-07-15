package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.UpdateResult;

import java.util.List;

public interface UpdateResultService {

    List<UpdateResult> getAllUpdateResults();

    UpdateResult create(UpdateResult updateResult);
}
