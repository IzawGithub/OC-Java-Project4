package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

class SConfigFile {
    private String hostname;

    public String getHostname() {
        return hostname;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    // -- Ctors --

    public SConfigFile(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        Properties prop = new Properties();
        prop.load(stream);
        setConfig(prop);
    }
    public SConfigFile(String path) throws IOException {
        this(loadConfigFile(path));
    }

    public SConfigFile() throws IOException {
        this(loadConfigFile(PATH_TO_CONFIG));
    }

    // Private

    private static final Logger logger = LoggerFactory.getLogger(SConfigFile.class);

    private static final String PATH_TO_CONFIG = "config.ini";

    private static FileInputStream loadConfigFile(String path) throws IOException
    {
        try {
            return new FileInputStream(path);
        } catch (IOException ex) {
            logger.error("Error with the database configuration file", ex);
            throw ex;
        }
    }
    private void setConfig(Properties configFile) {
        Objects.requireNonNull(configFile);
        hostname = configFile.getProperty("hostname");
        username = configFile.getProperty("username");
        password = configFile.getProperty("password");
    }
}
