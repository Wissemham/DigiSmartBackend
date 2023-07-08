package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.DataSensorRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataSensorService implements IDataSensorService {

    @Autowired
    DeviceRepository deviceRepository ;
    @Autowired
    SensorRepository sensorRepository ;
    @Autowired
    DataSensorRepository dataSensorRepository ;
    @Autowired
    IhistoriqueService ihistoriqueService ;

    @Override
    public DataSensor affecteSensorDevice(String idSensor, String idDevice) {
        Device device = deviceRepository.findById(idDevice).get();
        Sensor sensor = sensorRepository.findById(idSensor).get();

        DataSensor dataSensor = new DataSensor();
        dataSensor.setSensor(sensor);
        dataSensor.setDevice(device);

         return dataSensor ;
    }

    @Override
    public DataSensor loadDataInSensorDevice(String idSensor, String idDevice, Double time, Double data) {
        Device device = deviceRepository.findById(idDevice).get();
        Sensor sensor = sensorRepository.findById(idSensor).get();

        DataSensor dataSensor = dataSensorRepository.findDataSensorByDeviceAndSensor(device,sensor);
        dataSensor.setData(data);
        dataSensor.setTime(time);

        dataSensorRepository.save(dataSensor);

        String action = "Add new value ' "+data+" ' to Sensor ' "+sensor.getSensorName()+" ' In Device ' "+device.getDeviceCode() ;
        Historique historique = new Historique() ;
        historique.setAction(action);
        historique.setDate(LocalDateTime.now());
        historique.setDataSensor(dataSensor);

        dataSensorRepository.save(dataSensor) ;
        ihistoriqueService.addHistorique(historique);

        return dataSensor ;

    }
}
