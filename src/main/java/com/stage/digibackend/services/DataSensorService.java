package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.Enumeration.GrowthStatus;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.DataSensorRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

         return dataSensorRepository.save(dataSensor) ;
    }

    @Override
    public DataSensor loadDataInSensorDevice(String idSensor, String idDevice, Double data, GrowthStatus growthStatus , LocalDateTime latestUpdate) {
        String action = "";

        Device device = deviceRepository.findById(idDevice)
                .orElseThrow(() -> new NoSuchElementException("Device not found with ID: " + idDevice));

        Sensor sensor = sensorRepository.findById(idSensor)
                .orElseThrow(() -> new NoSuchElementException("Sensor not found with ID: " + idSensor));


        DataSensor dataSensor = dataSensorRepository.findDataSensorByDeviceAndSensor(device, sensor);
        if (dataSensor.getLatestUpdate() != null && !latestUpdate.toLocalDate().isEqual(dataSensor.getLatestUpdate().toLocalDate())) {
            dataSensor.setTotal(0.0);
            dataSensor.setData(0.0);
        }
        dataSensor.setData(data);
        //LocalDateTime.now()
        dataSensor.setLatestUpdate(latestUpdate);
        dataSensor.setGrowthStatus(growthStatus);

        switch (growthStatus) {
            case POSITIVE:
                if (dataSensor.getTotal() != null) {
                    dataSensor.setTotal(dataSensor.getTotal() + data);
                } else {
                    dataSensor.setTotal(data);
                }
                action = "increase in value";
                break;
            case NEGATIVE:
                if (dataSensor.getTotal() != null) {
                    dataSensor.setTotal(dataSensor.getTotal() - data);
                } else {
                    dataSensor.setTotal(-data);
                }
                action = "decrease in value";
                break;
            case NEUTRAL:
                action = "stagnation in value";
                break;
        }

        dataSensorRepository.save(dataSensor);

        Historique historique = new Historique();
        historique.setAction(action);
        historique.setDate(LocalDateTime.now());
        historique.setDataSensor(dataSensor);

        ihistoriqueService.addHistorique(historique);

        return dataSensor;
    }

}
