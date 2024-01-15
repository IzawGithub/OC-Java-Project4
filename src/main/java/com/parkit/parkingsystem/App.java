package com.parkit.parkingsystem;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        logger.info("Initializing Parking System");
        new InteractiveShell().loadInterface();
    }
}
