package com.parkit.parkingsystem.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Scanner;

public class InputReaderUtil {
    // Public

    public String readUserInput() {
        return scanner.nextLine();
    }

    public Integer readUserInputAsInt() {
        Integer result = null;
        try {
            result = Integer.parseInt(readUserInput());
        } catch (NumberFormatException e) {
            logger.error("User input to integer failed");
        }
        return result;
    }

    public String readVehicleRegistrationNumber() {
        String result = null;
        String vehicleRegNumber = scanner.nextLine();
        if (Objects.isNull(vehicleRegNumber) || vehicleRegNumber.trim().length() == 0) {
            logger.error("Error while reading vehicle registration number from Shell");
        } else {
            result = vehicleRegNumber;
        }
        return result;
    }

    // -- Ctors --

    public InputReaderUtil(Scanner scanner) {
        Objects.requireNonNull(scanner);
        this.scanner = scanner;
    }

    public InputReaderUtil() {
        this(new Scanner(System.in));
    }

    // Private

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final Scanner scanner;
}
