package com.vermeg.ApplicationManager.services.impl;

import com.vermeg.ApplicationManager.entities.Database;
import com.vermeg.ApplicationManager.entities.ExecutedQuery;
import com.vermeg.ApplicationManager.entities.ScriptExecutionResult;
import com.vermeg.ApplicationManager.repositories.DatabaseRepository;
import com.vermeg.ApplicationManager.repositories.ScriptExecutionResultRepository;
import com.vermeg.ApplicationManager.services.DatabaseService;
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
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private ScriptExecutionResultRepository resultRepository;
    @Autowired
    private DatabaseRepository databaseRepository;

    @Override
    @Transactional
    public Database save(Database database){
        return databaseRepository.save(database);
    }

    @Override
    @Transactional
    public ScriptExecutionResult executeScript(String databaseName, String filePath) {
        ScriptExecutionResult result = new ScriptExecutionResult();
        result.setPath(filePath);
        result.setExecutedQueries(new ArrayList<>());

        try {
            Optional<Database> opDb = databaseRepository.findById(databaseName);
            Database database = opDb.orElseThrow(() ->
                    new RuntimeException(
                            "Database not found for database name: " + databaseName + ". The database must exist."));
            result.setDatabase(database);

            JdbcTemplate dynamicJdbcTemplate = createDynamicJdbcTemplate(database);

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                StringBuilder queryBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    queryBuilder.append(line.trim());
                    if (line.trim().endsWith(";")) {
                        String query = queryBuilder.toString();
                        executeQuery(query, dynamicJdbcTemplate, result);
                        queryBuilder.setLength(0);
                    } else {
                        queryBuilder.append(" ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.addExecutedQueries(createQuery(null, "Failed to read SQL file: " + e.getMessage()));
            }

            result.setDate(new Date());

            result = resultRepository.save(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.getExecutedQueries().add(createQuery(null, "Failed to execute SQL file: " + e.getMessage()));
        }

        return result;
    }

    private JdbcTemplate createDynamicJdbcTemplate(Database database) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String url = "";

        switch (database.getType().toLowerCase()) {
            case "mysql":
                url = "jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + database.getName();
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case "postgresql":
                url = "jdbc:postgresql://" + database.getHost() + ":" + database.getPort() + "/" + database.getName();
                dataSource.setDriverClassName("org.postgresql.Driver");
                break;
            case "oracle":
                url = "jdbc:oracle:thin:@" + database.getHost() + ":" + database.getPort() + ":" + database.getName();
                dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
                break;
            case "sqlserver":
                url = "jdbc:sqlserver://" + database.getHost() + ":" + database.getPort() + ";databaseName=" + database.getName();
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + database.getType());
        }

        dataSource.setUrl(url);
        dataSource.setUsername(database.getUser());
        dataSource.setPassword(database.getPassword());

        return new JdbcTemplate(dataSource);
    }

    private void executeQuery(String query, JdbcTemplate jdbcTemplate, ScriptExecutionResult result) {
        try {
            jdbcTemplate.execute(query);
            result.addExecutedQueries(createQuery(query, "Success"));
        } catch (Exception e) {
            e.printStackTrace();
            result.addExecutedQueries(createQuery(query, "Failed to execute query: " + e.getMessage()));
        }
    }

    private ExecutedQuery createQuery(String query, String message) {
        ExecutedQuery executedQuery = new ExecutedQuery();
        executedQuery.setExecutionResult(message);
        executedQuery.setQuery(query);
        return executedQuery;
    }
    @Override
    @Transactional
    public void delete(String aleas) {
        databaseRepository.deleteById(aleas);
    }
    @Override
    @Transactional
    public Database update(String aleas, Database database) {
        Database existingDatabase = findById(aleas);
        existingDatabase.setName(database.getName());
        existingDatabase.setHost(database.getHost());
        existingDatabase.setPort(database.getPort());
        existingDatabase.setType(database.getType());
        existingDatabase.setUser(database.getUser());
        existingDatabase.setPassword(database.getPassword());
        return databaseRepository.save(existingDatabase);
    }
    @Override
    @Transactional(readOnly = true)
    public Database findById(String aleas) {
        return databaseRepository.findById(aleas).orElseThrow(() -> new RuntimeException("Database not found with id: " + aleas));
    }
}
