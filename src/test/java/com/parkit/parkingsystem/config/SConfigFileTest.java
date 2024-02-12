package com.parkit.parkingsystem.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SConfigFileTest {
    @Test
    void fileExist() {
        assertDoesNotThrow(() -> new SConfigFile());
    }

    @Test
    void loadCustomFile() {
        assertDoesNotThrow(() -> new SConfigFile("config.ini"));
    }

    @Test
    void fileButNotExist() {
        assertThrows(IOException.class, () -> new SConfigFile("0xDEADBEEF.ini"));
    }

    @Test
    void loadPropreties() throws IOException {
        final var hostname = "0xDEADBEEF.com";
        final var username = "username";
        final var password = "password";
        final var stream = new ByteArrayInputStream(MessageFormat.format("""
            hostname={0}\n
            username={1}\n
            password={2}""", hostname, username, password).getBytes())
        ;
        var configFile = new SConfigFile(stream);
        assertEquals(configFile.getHostname(), hostname);
        assertEquals(configFile.getUsername(), username);
        assertEquals(configFile.getPassword(), password);
    }
}
