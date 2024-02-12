package com.parkit.parkingsystem.integration.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;

import com.parkit.parkingsystem.config.DataBaseTestConfig;

public class DataBasePrepareService {
    public static void setUpDatabaseEntries() throws IOException {
        var dataBaseTestConfig = new DataBaseTestConfig();
        try (Connection connection = dataBaseTestConfig.getConnection();) {
            final var parkingFile = new File(MessageFormat.format("{0}/resources/createParking.sql", System.getProperty("user.dir")));
            final var ticketFile = new File(MessageFormat.format("{0}/resources/createTicket.sql", System.getProperty("user.dir")));
            final var updateParkingFile = new File(MessageFormat.format("{0}/resources/insertParking.sql", System.getProperty("user.dir")));

            var batch = connection.createStatement();
            batch.addBatch(FileUtils.readFileToString(parkingFile, Charset.defaultCharset()));
            batch.addBatch(FileUtils.readFileToString(ticketFile, Charset.defaultCharset()));
            batch.addBatch(FileUtils.readFileToString(updateParkingFile, Charset.defaultCharset()));
            batch.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDataBaseEntries() throws IOException {
        var dataBaseTestConfig = new DataBaseTestConfig();
        try (Connection connection = dataBaseTestConfig.getConnection();) {
            final var sqlPath = MessageFormat.format("{0}/resources/delete.sql", System.getProperty("user.dir"));
            final var sqlFile = new File(sqlPath);
            connection.prepareStatement(FileUtils.readFileToString(sqlFile, Charset.defaultCharset())).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DataBasePrepareService() {
    }
}
