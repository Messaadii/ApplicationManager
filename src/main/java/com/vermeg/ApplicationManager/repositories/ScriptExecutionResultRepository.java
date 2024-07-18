package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptExecutionResultRepository extends JpaRepository<ScriptExecutionResult, Long> {

}
