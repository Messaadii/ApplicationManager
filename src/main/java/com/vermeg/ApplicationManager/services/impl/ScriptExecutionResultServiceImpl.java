package com.vermeg.ApplicationManager.services.impl;

import com.vermeg.ApplicationManager.entities.Database;
import com.vermeg.ApplicationManager.entities.ExecutedQuery;
import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;
import com.vermeg.ApplicationManager.repositories.DatabaseRepository;
import com.vermeg.ApplicationManager.repositories.ScriptExecutionResultRepository;
import com.vermeg.ApplicationManager.services.ScriptExecutionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class ScriptExecutionResultServiceImpl implements ScriptExecutionResultService {
    @Autowired
    private ScriptExecutionResultRepository resultRepository;
    @Transactional
    @Override
    public void deleteById(Long resultId) {
        resultRepository.deleteById(resultId);
    }
}
