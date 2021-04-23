package com.involve.peopleflow.microservice.employee.config;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.ArangoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by olivertasevski on April 22, 2021
 */
@Configuration
@EnableArangoRepositories(basePackages = { "com.involve.peopleflow.microservice.employee" })
public class ArangoDBConfiguration implements ArangoConfiguration {

    @Value("${ARANGO_DB_HOST:localhost}")
    private String host;

    @Value("${ARANGO_DB_PORT:8529}")
    private int port;

    @Value("${ARANGO_DB_USERNAME:root}")
    private String username;

    @Value("${ARANGO_DB_PASSWORD:}")
    private String password;

    @Value("${ARANGO_DB_DATABASE_NAME:peopleflow-employees}")
    private String databaseName;

    @Override
    public ArangoDB.Builder arango() {
        System.out.println("Configuring arango with host: " + host + ", port: " + port + ", username: " + username);
        return new ArangoDB.Builder()
                .host(host, port)
                .useSsl(false)
                .user(username)
                .password(password);
    }

    @Override
    public String database() {
        return databaseName;
    }
}
