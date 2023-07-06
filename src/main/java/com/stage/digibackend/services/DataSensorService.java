package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.DataSensorRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSensorService implements IDataSensorService {

    @Autowired
    DeviceRepository deviceRepository ;
    @Autowired
    SensorRepository sensorRepository ;
    @Autowired
    DataSensorRepository dataSensorRepository ;

    @Override
    public DataSensor affecteSensorDevice(String idSensor, String idDevice) {
        Device device = deviceRepository.findById(idDevice).get();
        Sensor sensor = sensorRepository.findById(idSensor).get();
        DataSensor sensorDevice = new DataSensor();
        sensorDevice.setSensor(sensor);
        sensorDevice.setDevice(device);
        return dataSensorRepository.save(sensorDevice) ;
    }
}
