package com.parkit.parkingsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.EVehicleType;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Date;

class TicketTest {
    Ticket test;

    int testId = 1337;
    ParkingSpot testParkingSpot = new ParkingSpot(testId, EVehicleType.CAR, false);
    String testVehicleRegNumber = "1337";
    double testPrice = 133.7;
    long testDate = LocalDate.ofEpochDay(1337).toEpochDay();

    @BeforeEach
    void setUpPerTest() {
        test = new Ticket();
        test.setId(testId);
        test.setParkingSpot(testParkingSpot);
        test.setVehicleRegNumber(testVehicleRegNumber);
        test.setPrice(testPrice);
        var date = new Date();
        date.setTime(testDate);
        test.setInTime(date);
        test.setOutTime(date);
    }

    @Test
    void testGetId() {
        assertEquals(testId, test.getId());
    }

    @Test
    void testSetId() {
        int expected = 0xDEADBEEF;
        test.setId(expected);
        assertEquals(expected, test.getId());
    }

    @Test
    void testGetParkingSpot() {
        assertEquals(testParkingSpot, test.getParkingSpot());
    }

    @Test
    void testSetParkingSpot() {
        var expected = new ParkingSpot(0xDEADBEEF, EVehicleType.BIKE, true);
        test.setParkingSpot(expected);
        assertEquals(expected, test.getParkingSpot());
    }

    @Test
    void testGetVehicleRegNumber() {
        assertEquals(testVehicleRegNumber, test.getVehicleRegNumber());
    }

    @Test
    void testSetVehicleRegNumber() {
        String expected = "0xDEADBEEF";
        test.setVehicleRegNumber(expected);
        assertEquals(expected, test.getVehicleRegNumber());
    }

    @Test
    void testGetPrice() {
        assertEquals(testPrice, test.getPrice());
    }

    @Test
    void testSetPrice() {
        double expected = 0xDEADBEEF;
        test.setPrice(expected);
        assertEquals(expected, test.getPrice());
    }

    @Test
    void testGetInTime() {
        assertEquals(testDate, test.getInTime().getTime());
    }

    @Test
    void testSetInTime() {
        long expected = 0xDEADBEEF;
        var date = new Date();
        date.setTime(expected);
        test.setInTime(date);
        assertEquals(expected, test.getInTime().getTime());
    }

    @Test
    void testGetOutTime() {
        assertEquals(testDate, test.getOutTime().getTime());
    }

    @Test
    void testSetOutTime() {
        long expected = 0xDEADBEEF;
        var date = new Date();
        date.setTime(expected);
        test.setOutTime(date);
        assertEquals(expected, test.getOutTime().getTime());
    }
}
