package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseTestConfig;
import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {
    @Mock
    DataBaseTestConfig mockDb;
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockStatement;

    ParkingSpotDAO testDAO;

    @BeforeEach
    void setUpPerTest() {
        when(mockDb.getConnection()).thenReturn(mockConnection);
        testDAO = new ParkingSpotDAO(mockDb);
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void getNextAvailableSlot(EVehicleType param) throws SQLException {
        var mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        assertNotNull(testDAO.getNextAvailableSlot(param));

        when(mockResultSet.next()).thenReturn(false);
        assertNull(testDAO.getNextAvailableSlot(param));
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void getNextAvailableSlotDBError(EVehicleType param) throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.getNextAvailableSlot(param));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error fetching next available slot");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void updateParking(Integer param) throws SQLException {
        var mockParkingSpot = mock(ParkingSpot.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        when(mockParkingSpot.isAvailable()).thenReturn(true);
        when(mockParkingSpot.getId()).thenReturn(1337);

        when(mockStatement.executeUpdate()).thenReturn(param);

        var expected = param == 1 ? true : false;
        assertEquals(expected, testDAO.updateParking(mockParkingSpot));
    }

    @Test
    void updateParkingDBError() throws SQLException {
        var mockParkingSpot = mock(ParkingSpot.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.updateParking(mockParkingSpot));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error updating parking info");
    }
}
