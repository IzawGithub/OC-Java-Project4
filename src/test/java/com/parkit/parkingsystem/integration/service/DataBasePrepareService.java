package com.parkit.parkingsystem.integration.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.parkit.parkingsystem.config.DataBaseTestConfig;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig;

    public void clearDataBaseEntries() throws IOException {

        dataBaseTestConfig = new DataBaseTestConfig();
        try (Connection connection = dataBaseTestConfig.getConnection();) {
            // set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            // clear ticket entries
            connection.prepareStatement("truncate table ticket").execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
