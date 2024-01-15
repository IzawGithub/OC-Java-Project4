package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.text.MessageFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {
    final String testVehicleRegNumber = "0xDEADBEEF";

    @Mock
    InputReaderUtil mockReaderUtil;
    @Mock
    ParkingSpotDAO mockSpotDAO;
    @Mock
    TicketDAO mockTicketDAO;

    @InjectMocks
    ParkingService testParkingService;

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicle(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getNbTicket(testVehicleRegNumber)).thenReturn(1);
        when(mockSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(mockTicketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getInfoLogs()).hasSizeGreaterThanOrEqualTo(3).contains("Generated Ticket and saved in DB");
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicleNoDiscount(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getNbTicket(testVehicleRegNumber)).thenReturn(0);
        when(mockSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(mockTicketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getInfoLogs()).hasSizeGreaterThanOrEqualTo(3).contains("Generated Ticket and saved in DB");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, 4, 1337, Integer.MAX_VALUE, Integer.MIN_VALUE })
    void processIncomingVehicleNotAnEnumValue(Integer param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains(MessageFormat.format("Error: input ''{0}'' is not a valid vehicle type", param.toString()));
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicleParkingNull(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(null);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getDebugLogs()).hasSizeGreaterThanOrEqualTo(2).contains("ParkingSpot is null");
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicleParkingFull(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(0);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSize(1)
                .contains("Error: ID of the parking spot is less than or equal to 0. Parking might be full");
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicleRegNumberNull(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getDebugLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Vehicle registration number is null");
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void processIncomingVehicleDiscountTicketDoesNotExist(EVehicleType param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(mockSpotDAO.getNextAvailableSlot(param)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getNbTicket(testVehicleRegNumber)).thenReturn(1);
        when(mockTicketDAO.getTicketExitTimeNull(anyString())).thenReturn(true);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1).contains(MessageFormat.format(
                "Error: tried to save vehicle registration number ''{0}'' for a vehicle that is already inside the car park",
                testVehicleRegNumber));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false })
    @NullSource
    void processIncomingVehicleUpdateParkingFailed(Boolean param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(EVehicleType.CAR.getValue());
        when(mockSpotDAO.getNextAvailableSlot(EVehicleType.CAR)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getNbTicket(testVehicleRegNumber)).thenReturn(0);
        when(mockSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(param);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Error: Could not update the parking spot succesfully");
    }

    @ParameterizedTest
    @ValueSource(booleans = { false })
    @NullSource
    void processIncomingVehicleSaveTicketFailed(Boolean param) {
        when(mockReaderUtil.readUserInputAsInt()).thenReturn(EVehicleType.CAR.getValue());
        when(mockSpotDAO.getNextAvailableSlot(EVehicleType.CAR)).thenReturn(1);
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getNbTicket(testVehicleRegNumber)).thenReturn(0);
        when(mockSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(mockTicketDAO.saveTicket(any(Ticket.class))).thenReturn(param);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processIncomingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Error: could not save the ticket to the parking succesfully");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void processExitingVehicle(Integer param) {
        var mockTicket = mock(Ticket.class);
        when(mockTicket.getPrice()).thenReturn(1337d);

        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getTicket(anyString())).thenReturn(mockTicket);
        when(mockTicketDAO.getNbTicket(anyString())).thenReturn(param);

        try (MockedStatic<FareCalculatorService> mockFare = Mockito.mockStatic(FareCalculatorService.class)) {
            mockFare.when(() -> FareCalculatorService.calculateFare(mockTicket, param == 1 ? true : false))
                    .thenReturn(mockTicket);
            when(mockTicketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            var mockSpot = mock(ParkingSpot.class);
            when(mockTicket.getParkingSpot()).thenReturn(mockSpot);

            var logCaptor = LogCaptor.forClass(testParkingService.getClass());
            testParkingService.processExitingVehicle();

            assertThat(logCaptor.getInfoLogs()).hasSizeGreaterThanOrEqualTo(2)
                    .contains(MessageFormat.format("Please pay the parking fare: ''{0}''€",
                            Double.valueOf(mockTicket.getPrice()).toString()));
        }
    }

    @ParameterizedTest
    @NullSource
    void processExitingVehicleVehicleRegNumberNull(String param) {
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(param);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processExitingVehicle();

        assertThat(logCaptor.getDebugLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Vehicle registration number is null");
    }

    @Test
    void processExitingVehicleTicketNull() {
        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getTicket(anyString())).thenReturn(null);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processExitingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Error: The registration number given does not match with a vehicle in the car park");
    }

    @Test
    void processExitingVehicleVehicleDidntLeft() {
        var mockTicket = mock(Ticket.class);

        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getTicket(anyString())).thenReturn(mockTicket);

        var mockDate = mock(Date.class);
        when(mockTicket.getOutTime()).thenReturn(mockDate);

        var logCaptor = LogCaptor.forClass(testParkingService.getClass());
        testParkingService.processExitingVehicle();

        assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                .contains("Error: cannot exit a vehicle that already left the carpark");
    }

    @Test
    void processExitingVehicleFareNull() {
        var mockTicket = mock(Ticket.class);

        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getTicket(anyString())).thenReturn(mockTicket);
        when(mockTicketDAO.getNbTicket(anyString())).thenReturn(0);

        try (MockedStatic<FareCalculatorService> mockFare = Mockito.mockStatic(FareCalculatorService.class)) {
            mockFare.when(() -> FareCalculatorService.calculateFare(mockTicket, true)).thenReturn(null);

            var logCaptor = LogCaptor.forClass(testParkingService.getClass());
            testParkingService.processExitingVehicle();

            assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                    .contains("Error: calculation of the exit ticket price returned Not a Number");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false })
    @NullSource
    void processExitingVehicleUpdateTicketFailed(Boolean param) {
        var mockTicket = mock(Ticket.class);

        when(mockReaderUtil.readVehicleRegistrationNumber()).thenReturn(testVehicleRegNumber);
        when(mockTicketDAO.getTicket(anyString())).thenReturn(mockTicket);
        when(mockTicketDAO.getNbTicket(anyString())).thenReturn(1);

        try (MockedStatic<FareCalculatorService> mockFare = Mockito.mockStatic(FareCalculatorService.class)) {
            mockFare.when(() -> FareCalculatorService.calculateFare(mockTicket, true)).thenReturn(mockTicket);
            when(mockTicketDAO.updateTicket(any(Ticket.class))).thenReturn(param);

            var logCaptor = LogCaptor.forClass(testParkingService.getClass());
            testParkingService.processExitingVehicle();

            assertThat(logCaptor.getErrorLogs()).hasSizeGreaterThanOrEqualTo(1)
                    .contains("Error: unable to update ticket information");
        }
    }
}
