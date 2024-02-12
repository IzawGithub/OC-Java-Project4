package com.parkit.parkingsystem.service;

import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.altindag.log.LogCaptor;

import com.parkit.parkingsystem.config.AbstractDatabase;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
class InteractiveShellTest {
    @Mock
    AbstractDatabase mockDb;
    @Mock
    ParkingSpotDAO mockSpotDAO;
    @Mock
    TicketDAO mockTicketDAO;
    @Mock
    ParkingService mockParkingService;
    @Mock
    InputReaderUtil mockReaderUitl;

    @InjectMocks
    InteractiveShell interactiveShell;

    @ParameterizedTest
    @ValueSource(strings = { "Case '1': Processing incoming vehicle", "Case '2': Processing exiting vehicle",
            "Case '3': Exiting" })
    void menuUserInputSupported(String expected) {
        var mockReturnValue = Character.getNumericValue(expected.charAt(expected.indexOf("'") + 1));
        when(mockReaderUitl.readUserInputAsInt()).thenReturn(mockReturnValue);
        var logCaptor = LogCaptor.forClass(interactiveShell.getClass());

        interactiveShell.mainLoop();

        assertThat(logCaptor.getDebugLogs()).hasSize(1).contains(expected);
    }
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 4, 1337, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void menuUserInputUnsupported(Integer unsupported)
    {
        when(mockReaderUitl.readUserInputAsInt()).thenReturn(unsupported);
        var logCaptor = LogCaptor.forClass(interactiveShell.getClass());

        interactiveShell.mainLoop();

        var expected = "Unsupported option. Please enter a number corresponding to the provided menu";
        assertThat(logCaptor.getWarnLogs()).hasSize(1).contains(expected);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MILLISECONDS)
    void loadInterfaceCanExit()
    {
        when(mockReaderUitl.readUserInputAsInt()).thenReturn(3);
        interactiveShell.loadInterface();
        assertTrue(true);
    }
}
