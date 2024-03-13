package org.bigbrainmm.avitopricesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Точка входа в приложение
 */
@SpringBootApplication
public class AvitoPricesApiApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AvitoPricesApiApplication.class, args);
    }

}
