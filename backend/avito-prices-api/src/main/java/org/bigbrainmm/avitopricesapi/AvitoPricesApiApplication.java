package org.bigbrainmm.avitopricesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class AvitoPricesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvitoPricesApiApplication.class, args);
    }

}
