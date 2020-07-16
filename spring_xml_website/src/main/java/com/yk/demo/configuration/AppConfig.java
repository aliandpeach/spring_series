package com.yk.demo.configuration;

import com.yk.demo.model.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean("car8")
    public Car createCar() {
        return new Car("", "", 1605);
    }
}
