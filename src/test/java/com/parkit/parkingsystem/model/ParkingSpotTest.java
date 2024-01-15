package com.parkit.parkingsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.EVehicleType;

class ParkingSpotTest {
    ParkingSpot testSpot;

    int testId = 1337;
    EVehicleType testParkingType = EVehicleType.CAR;
    boolean testAvailable = true;

    @BeforeEach
    void setUpPerTest() {
        testSpot = new ParkingSpot(testId, testParkingType, testAvailable);
    }

    @Test
    void testGetId() {
        assertEquals(testId, testSpot.getId());
    }

    @Test
    void testSetId() {
        int expected = 0xDEADBEEF;
        testSpot.setId(expected);
        assertEquals(expected, testSpot.getId());
    }

    @Test
    void testGetParkingType() {
        assertEquals(testParkingType, testSpot.getVehicleType());
    }

    @Test
    void testSetParkingType() {
        var expected = EVehicleType.BIKE;
        testSpot.setVehicleType(expected);
        assertEquals(expected, testSpot.getVehicleType());
    }

    @Test
    void testIsAvailable() {
        assertEquals(testAvailable, testSpot.isAvailable());
    }

    @Test
    void testSetAvaialble() {
        boolean expected = false;
        testSpot.setAvailable(expected);
        assertEquals(expected, testSpot.isAvailable());
    }

    @Test
    void testEquals() {
        var differentParkingSpot = new ParkingSpot(testId, EVehicleType.CAR, false);
        var actual = testSpot.equals(differentParkingSpot);
        assertTrue(actual);
    }

    @Test
    void testEqualsNot() {
        var differentParkingSpot = new ParkingSpot(0xDEADBEEF, EVehicleType.CAR, false);
        var actual = testSpot.equals(differentParkingSpot);
        assertFalse(actual);
    }

    @Test
    void testEqualsNull() {
        ParkingSpot differentParkingSpot = null;
        var actual = testSpot.equals(differentParkingSpot);
        assertFalse(actual);
    }

    @Test
    void testEqualsDifferentObject() {
        Object differentObject = 0xDEADBEEF;
        var actual = testSpot.equals(differentObject);
        assertFalse(actual);
    }

    @Test
    void testHashCode() {
        assertEquals(testId, testSpot.hashCode());
    }
}
