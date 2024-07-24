package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command, Long> {
}
