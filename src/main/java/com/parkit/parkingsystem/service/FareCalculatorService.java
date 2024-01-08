package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    public void calculateFare(Ticket ticket){
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inMinutes = TimeUnit.MILLISECONDS.toMinutes(ticket.getInTime().getTime());
        long outMinutes = TimeUnit.MILLISECONDS.toMinutes(ticket.getOutTime().getTime());

        double hourDuration = (double) (outMinutes - inMinutes) / TimeUnit.HOURS.toMinutes(1);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(hourDuration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(hourDuration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
