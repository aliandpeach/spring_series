package com.yk.demo.configuration;

import com.yk.demo.model.Car;
import com.yk.demo.model.Moto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:spring/bean2.xml")
public class AppConfigImport {

    @Bean("moto5")
    @Autowired
    public Moto createMoto(@Qualifier("car9") Car car) {
        Moto moto = new Moto();
        moto.setCar4(car);
        return moto;
    }
}
