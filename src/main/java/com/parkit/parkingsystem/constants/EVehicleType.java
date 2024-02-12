package com.parkit.parkingsystem.constants;

public enum EVehicleType {
    CAR(1, 1.0),
    BIKE(2, 1.5);

    EVehicleType(int value, double fare) {
        this.eValue = value;
        this.eFare = fare;
    }

    private final int eValue;

    public int getValue() {
        return eValue;
    }

    private final double eFare;

    public double getFare() {
        return eFare;
    }
}
