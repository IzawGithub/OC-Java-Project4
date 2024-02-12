package com.parkit.parkingsystem.config;

import java.io.IOException;
import java.sql.Connection;

public class DataBaseConfig extends AbstractDatabase {
    public DataBaseConfig() throws IOException {
        super(new SConfigFile());
    }

    @Override
    public Connection getConnection() {
        return getConnection("prod");
    }
}
