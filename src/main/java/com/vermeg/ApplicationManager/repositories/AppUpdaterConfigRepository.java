package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.AppUpdaterConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUpdaterConfigRepository extends JpaRepository<AppUpdaterConfig, Long>{
    AppUpdaterConfig getAppUpdaterConfigByName(String name);

    AppUpdaterConfig findByName(String name);
}
