package com.parkit.parkingsystem.integration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.altindag.log.LogCaptor;

import com.parkit.parkingsystem.config.DataBaseTestConfig;
import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {
    String vehicleRegNumber = "1337";

    DataBaseTestConfig testDb;
    ParkingSpotDAO testSpotDAO;
    TicketDAO testTicketDAO;
    DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    @Mock
    InputReaderUtil inputReaderUtil;

    @BeforeEach
    void setUpPerTest() throws IOException {
        testDb = new DataBaseTestConfig();
        testSpotDAO = new ParkingSpotDAO(testDb);
        testTicketDAO = new TicketDAO(testDb);
    }

    @AfterEach
    void tearDownPerTest() throws IOException {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void testParkingAVehicle(EVehicleType param) {
        when(inputReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

        ParkingService parkingService = new ParkingService(inputReaderUtil, testSpotDAO, testTicketDAO);

        var logCaptor = LogCaptor.forClass(parkingService.getClass());
        parkingService.processIncomingVehicle();

        assertThat(logCaptor.getInfoLogs()).hasSizeGreaterThanOrEqualTo(3).contains("Generated Ticket and saved in DB");
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void testParkingVehicleExit(EVehicleType param) {
        when(inputReaderUtil.readUserInputAsInt()).thenReturn(param.getValue());
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        testParkingAVehicle(param);

        ParkingService parkingService = new ParkingService(inputReaderUtil, testSpotDAO, testTicketDAO);

        var logCaptor = LogCaptor.forClass(parkingService.getClass());
        parkingService.processExitingVehicle();

        assertThat(logCaptor.getInfoLogs()).hasSizeGreaterThanOrEqualTo(3).contains("Vehicle exit succesfull");
    }

}
