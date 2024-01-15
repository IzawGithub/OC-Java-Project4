package com.parkit.parkingsystem.config;

import java.util.Objects;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class AbstractDatabase {
    // accessible pour les autres classes alors que non?
    Connection getConnection(String dbName) {
        Connection result = null;
        Objects.requireNonNull(dbName);
        try {
            result = DriverManager.getConnection(
                    MessageFormat.format("{0}/{1}", config.getHostname(), dbName),
                    config.getUsername(),
                    config.getPassword());
        } catch (SQLException e) {
            logger.error("Database: Connection failed", e);
        }
        return result;

    }

    public abstract Connection getConnection();

    // -- Ctors --

    protected AbstractDatabase(SConfigFile config) {
        Objects.requireNonNull(config);
        this.config = config;
    }

    // Private

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private SConfigFile config;
}
