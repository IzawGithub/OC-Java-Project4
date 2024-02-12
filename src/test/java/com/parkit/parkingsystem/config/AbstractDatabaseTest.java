package com.parkit.parkingsystem.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.integration.service.DataBasePrepareService;

@ExtendWith(MockitoExtension.class)
class AbstractDatabaseTest {
    @Mock
    SConfigFile mockConfig;

    @BeforeEach
    void setUpPerTest() throws IOException {
        DataBasePrepareService.setUpDatabaseEntries();
    }

    @AfterEach
    void tearDownPerTest() throws IOException {
        DataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void dbNameExist() throws IOException {
        var db = new DataBaseTestConfig().getConnection();
        assertNotNull(db);
    }

    @ParameterizedTest
    @ValueSource(strings = { "0xDEADBEEF", "1337" })
    @EmptySource
    void dbNameDoesNotExist(String param) throws IOException {
        when(mockConfig.getHostname()).thenReturn(param);
        var db = new DataBaseTestConfig(mockConfig).getConnection();
        assertNull(db);
    }
}
