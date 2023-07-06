package com.stage.digibackend.repository;

import com.stage.digibackend.Collections.DataSensor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataSensorRepository extends MongoRepository<DataSensor, String> {
}
