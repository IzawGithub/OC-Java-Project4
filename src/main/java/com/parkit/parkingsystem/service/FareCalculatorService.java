package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    // Public

    public static Ticket calculateFare(Ticket ticket, boolean discountRecurringUser) {
        Objects.requireNonNull(ticket);
        Ticket result = ticket;

        if (result.getOutTime().before(result.getInTime())) {
            var error = MessageFormat.format("Out time provided is incorrect: ''{0}''", result.getOutTime());
            logger.error(error);
            return null;
        }

        long inMinutes = TimeUnit.MILLISECONDS.toMinutes(result.getInTime().getTime());
        long outMinutes = TimeUnit.MILLISECONDS.toMinutes(result.getOutTime().getTime());

        double hourDuration = (double) (outMinutes - inMinutes) / TimeUnit.HOURS.toMinutes(1);

        if (isFreeParking(hourDuration)) {
            result.setPrice(0);
            return result;
        }
        Double price = hourDuration * result.getParkingSpot().getVehicleType().getFare();
        if (discountRecurringUser) {
            logger.debug("Price before discount: {}", roundToCents(price));
            price = price * DISCOUNT;
        }
        // Rounding
        price = roundToCents(price);
        result.setPrice(price);
        return result;
    }

    public static Ticket calculateFare(Ticket ticket) {
        return calculateFare(ticket, false);
    }

    // -- Delete ctor --

    private FareCalculatorService() {
    }

    // Package

    static double roundToCents(double price)
    {
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    // Private

    // -- Interface --

    private static final Logger logger = LoggerFactory.getLogger(FareCalculatorService.class);

    // -- Vars --

    private static final double DISCOUNT = 0.95;

    private static final int MINUTES_FREE_PARKING = 30;

    // -- Private functions --

    private static boolean isFreeParking(double hourDuration) {
        return hourDuration < ((double)MINUTES_FREE_PARKING / TimeUnit.HOURS.toMinutes(1));
    }

}
