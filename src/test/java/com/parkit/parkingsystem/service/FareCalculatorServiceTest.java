package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class FareCalculatorServiceTest {
    Ticket ticket;

    @BeforeEach
    void setUpPerTest() {
        ticket = new Ticket();
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void fareVehicle(EVehicleType vehicleType) {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, vehicleType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        var actual = FareCalculatorService.calculateFare(ticket);
        assertEquals(vehicleType.getFare(), actual.getPrice());
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void fareVehicleWithFutureInTime(EVehicleType vehicleType) {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, vehicleType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertNull(FareCalculatorService.calculateFare(ticket));
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void fareVehicleWithLessThanOneHourParkingTime(EVehicleType vehicleType) {
        Date inTime = new Date();
        // 45 minutes parking time should give 3/4th of the parking fare
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));

        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, vehicleType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        var actual = FareCalculatorService.calculateFare(ticket);
        assertEquals(FareCalculatorService.roundToCents(0.75 * vehicleType.getFare()), actual.getPrice());
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void fareVehicleWithLessThan30minutesParkingTime(EVehicleType vehicleType) {
        var inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15));
        var outTime = new Date();
        var parkingSpot = new ParkingSpot(1, vehicleType, false);

        ticket.setInTime(inTime).setOutTime(outTime).setParkingSpot(parkingSpot);
        var actual = FareCalculatorService.calculateFare(ticket);

        double expected = 0;
        assertEquals(expected, actual.getPrice());
    }

    @ParameterizedTest
    @EnumSource(EVehicleType.class)
    void fareVehicleWithDiscount(EVehicleType vehicleType) {
        var inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(60));
        var outTime = new Date();
        var parkingSpot = new ParkingSpot(1, vehicleType, false);

        ticket.setInTime(inTime).setOutTime(outTime).setParkingSpot(parkingSpot);
        var actual = FareCalculatorService.calculateFare(ticket, true);

        double expected = FareCalculatorService.roundToCents(vehicleType.getFare() * 0.95);
        assertEquals(expected, actual.getPrice());
    }
}
