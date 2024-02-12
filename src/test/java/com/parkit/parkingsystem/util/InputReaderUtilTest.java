package com.parkit.parkingsystem.util;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InputReaderUtilTest {
    InputReaderUtil inputReaderUtil;

    @Mock
    Scanner mockScanner;

    @Test
    void readUserInput() {
        final String expected = "0xDEADBEEF";
        when(mockScanner.nextLine()).thenReturn(expected);

        inputReaderUtil = new InputReaderUtil(mockScanner);
        assertEquals(expected, inputReaderUtil.readUserInput());
    }

    @Test
    void readUserInputAsInt() {
        final String expected = "1337";
        when(mockScanner.nextLine()).thenReturn(expected);

        inputReaderUtil = new InputReaderUtil(mockScanner);
        assertEquals(Integer.valueOf(expected), inputReaderUtil.readUserInputAsInt());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "0xDEADBEEF" })
    void readUserInputAsIntButNotAnInt(String param) {
        when(mockScanner.nextLine()).thenReturn(param);

        inputReaderUtil = new InputReaderUtil(mockScanner);
        assertNull(inputReaderUtil.readUserInputAsInt());
    }

    @Test
    void readVehicleRegistrationNumber() {
        final String expected = "0xDEADBEEF";
        when(mockScanner.nextLine()).thenReturn(expected);

        inputReaderUtil = new InputReaderUtil(mockScanner);
        assertEquals(expected, inputReaderUtil.readVehicleRegistrationNumber());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void readVehicleRegistrationNumberButNotANumber(String param) {
        when(mockScanner.nextLine()).thenReturn(param);

        inputReaderUtil = new InputReaderUtil(mockScanner);
        assertNull(inputReaderUtil.readVehicleRegistrationNumber());
    }

}
