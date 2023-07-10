package com.stage.digibackend.repository;

import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataSensorRepository extends MongoRepository<DataSensor, String> {

    DataSensor findDataSensorByDeviceAndSensor(Device device, Sensor sensor);
}
