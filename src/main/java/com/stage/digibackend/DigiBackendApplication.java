package com.stage.digibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class DigiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigiBackendApplication.class, args);
    }

}
