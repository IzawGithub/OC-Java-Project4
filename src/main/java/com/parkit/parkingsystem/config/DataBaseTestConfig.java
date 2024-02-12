package com.parkit.parkingsystem.config;

import java.io.IOException;
import java.sql.Connection;

public class DataBaseTestConfig extends AbstractDatabase {
    public DataBaseTestConfig() throws IOException {
        super(new SConfigFile());
    }
    public DataBaseTestConfig(SConfigFile config) throws IOException {
        super(config);
    }

    @Override
    public Connection getConnection() {
        return super.getConnection("OC4Test");
    }
}
