package com.hedgerock.configs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class CentralizedConfigApp {

    public static void main(String[] args) {

        SpringApplication.run(CentralizedConfigApp.class, args);
    }

}
