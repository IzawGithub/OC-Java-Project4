package com.parkit.parkingsystem.model;

import java.util.Objects;

import com.parkit.parkingsystem.constants.EVehicleType;

public class ParkingSpot {
    private int number;
    private EVehicleType vehicleType;
    private boolean isAvailable;

    public ParkingSpot(int number, EVehicleType parkingType, boolean isAvailable) {
        Objects.requireNonNull(parkingType);
        this.number = number;
        this.vehicleType = parkingType;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return number;
    }

    public void setId(int number) {
        this.number = number;
    }

    public EVehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(EVehicleType parkingType) {
        this.vehicleType = parkingType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (Objects.isNull(o))
            return false;
        if (getClass() != o.getClass())
            return false;
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
