package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, String> {
}
