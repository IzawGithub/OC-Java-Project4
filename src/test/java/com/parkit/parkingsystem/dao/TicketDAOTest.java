package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseTestConfig;
import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {
    final String testVehicleRegNumber = "0xDEADBEEF";

    @Mock
    ParkingSpot mockParkingSpot;
    @Mock
    Date mockDate;
    @Mock
    Ticket mockTicket;

    @Mock
    DataBaseTestConfig mockDb;
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockStatement;

    TicketDAO testDAO;

    @BeforeEach
    void setUpPerTest() {
        when(mockDb.getConnection()).thenReturn(mockConnection);

        testDAO = new TicketDAO(mockDb);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void getTicket(boolean param) throws SQLException {
        var mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(param);

        if (param) {
            when(mockResultSet.getInt(anyInt())).thenReturn(1337);
            when(mockResultSet.getString(anyInt())).thenReturn(EVehicleType.BIKE.name());
            assertNotNull(testDAO.getTicket(testVehicleRegNumber));
        } else {
            assertNull(testDAO.getTicket(testVehicleRegNumber));
        }
    }

    @Test
    void getTicketDBError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.getTicket(testVehicleRegNumber));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error fetching the ticket");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void getTicketExitTimeNull(boolean param) throws SQLException {
        var mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(param);

        assertEquals(param, testDAO.getTicketExitTimeNull(testVehicleRegNumber));
    }

    @Test
    void getTicketExitTimeNullDBError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.getTicketExitTimeNull(testVehicleRegNumber));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error fetching ticket with null time");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void getNbTicket(boolean param) throws SQLException {
        var mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(param);

        if (param) {
            when(mockResultSet.getInt(anyInt())).thenReturn(1337);
            when(mockResultSet.getString(anyInt())).thenReturn(EVehicleType.BIKE.name());
            assertEquals(1, testDAO.getNbTicket(testVehicleRegNumber));
        } else {
            assertEquals(0, testDAO.getNbTicket(testVehicleRegNumber));
        }
    }

    @Test
    void getNbTicketDBError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertEquals(0, testDAO.getNbTicket(testVehicleRegNumber));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error fetching the ticket");
    }

    @Test
    void saveTicket() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        when(mockTicket.getParkingSpot()).thenReturn(mockParkingSpot);
        when(mockTicket.getVehicleRegNumber()).thenReturn("0xDEADBEEF");
        when(mockTicket.getPrice()).thenReturn(1337d);
        when(mockTicket.getInTime()).thenReturn(mockDate);
        when(mockTicket.getOutTime()).thenReturn(mockDate);

        assertEquals(true, testDAO.saveTicket(mockTicket));
    }

    @Test
    void saveTicketNoTimestamp() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        when(mockTicket.getParkingSpot()).thenReturn(mockParkingSpot);
        when(mockTicket.getVehicleRegNumber()).thenReturn("0xDEADBEEF");
        when(mockTicket.getPrice()).thenReturn(1337d);
        when(mockTicket.getInTime()).thenReturn(mockDate);
        when(mockTicket.getOutTime()).thenReturn(null);

        assertEquals(true, testDAO.saveTicket(mockTicket));
    }

    @Test
    void saveTicketDBError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.saveTicket(mockTicket));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error saving the ticket");
    }

    @Test
    void updateTicketNoTimestamp() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        when(mockTicket.getId()).thenReturn(1337);
        when(mockTicket.getPrice()).thenReturn(1337d);
        when(mockTicket.getOutTime()).thenReturn(mockDate);

        assertEquals(true, testDAO.updateTicket(mockTicket));
    }

    @Test
    void updateTicketDBError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var logCaptor = LogCaptor.forClass(testDAO.getClass());
        assertNull(testDAO.updateTicket(mockTicket));
        assertThat(logCaptor.getErrorLogs()).hasSize(1).contains("Database: error updating the ticket information");
    }
}
