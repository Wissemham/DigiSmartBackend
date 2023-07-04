package com.stage.digibackend.repository;

import com.stage.digibackend.Collections.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository  extends MongoRepository<Sensor, String> {

    Sensor findSensorBySensorName(String sensorName) ;
    Sensor findSensorBySensorId (String sensorId) ;
}
