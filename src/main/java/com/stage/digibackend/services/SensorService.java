package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SensorService implements ISensorService {


        @Autowired
        SensorRepository sensorRepository;

    @Override
    public String addSensor(Sensor sensor) {
        if (sensorRepository.findSensorBySensorName(sensor.getSensorName()) != null) {
            return "Sensor name already exists!";
        }
        if (sensor.getUnit() != null)
        {
            sensor.setSymboleUnite(sensor.getUnit().getSymbol());
        }

        return "Sensor added successfully:\n" + sensorRepository.save(sensor);
    }

    @Override
    public String addSensors(List<Sensor> sensorList) {
        for (Sensor sensor : sensorList) {
            if (sensorRepository.findSensorBySensorName(sensor.getSensorName()) != null) {
                return "The Sensor with name " + sensor.getSensorName() + " already exists!";
            }
            if (sensor.getUnit() != null)
            {
                sensor.setSymboleUnite(sensor.getUnit().getSymbol());
            }
        }
        return "Sensors added successfully:\n" + sensorRepository.saveAll(sensorList);
    }


    @Override
        public String updateSensor(Sensor sensor,String idSensor) {
            if (sensorRepository.findSensorBySensorName(sensor.getSensorName()) != null) {
                return "Sensor name already exists!";
            }
            if (!sensorRepository.existsById(idSensor)) {
                return "Sensor does not exist!";
            }

            sensor.setSensorId(idSensor);
            Sensor existingSensor = sensorRepository.findById(idSensor).get();

            if (sensor.getSensorName() != existingSensor.getSensorName() && sensor.getSensorName() != null ){
                existingSensor.setSensorName(sensor.getSensorName() );
            }

        if (sensor.getRangeMin() != existingSensor.getRangeMin() && sensor.getRangeMin() != null ){
            existingSensor.setRangeMin(sensor.getRangeMin() );
        }

        if (sensor.getRangeMax() != existingSensor.getRangeMax() && sensor.getRangeMax()  != null){
            existingSensor.setRangeMax(sensor.getRangeMax() );
        }


        if (sensor.getUnit() != existingSensor.getUnit() && sensor.getUnit() != null){
            existingSensor.setUnit(sensor.getUnit() );
            existingSensor.setSymboleUnite(sensor.getUnit().getSymbol());
        }

        return "Sensor updated successfully:\n" + sensorRepository.save(existingSensor);
        }

        @Override
        public String deleteSensor(String sensorId) {
            if (!sensorRepository.existsById(sensorId)) {
                return "Sensor does not exist!";
            }
            sensorRepository.deleteById(sensorId);
            return "Sensor deleted successfully!";
        }

        @Override
        public Sensor getSensor(String sensorId) {
            //Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
            return sensorRepository.findById(sensorId).get() ;

        }


        @Override
        public String getAllSensors(List<String> sensorIds) {
            StringBuilder result = new StringBuilder();
            for (String id : sensorIds) {
                Optional<Sensor> sensorOptional = sensorRepository.findById(id);
                if (sensorOptional.isPresent()) {
                    Sensor sensor = sensorOptional.get();
                    result.append(sensor.toString()).append("\n");
                } else {
                    result.append("Sensor ").append(id).append(" does not exist!\n");
                }
            }
            return result.toString();
        }

        @Override
        public List<Sensor> getAllSensors() {
            List<Sensor> sensors = sensorRepository.findAll();
            return sensors;
        }

        @Autowired
        DeviceRepository deviceRepository ;
    @Override
    public List<String> getAllSensorsDevice(String d) {
        Device device = deviceRepository.findById(d).get() ;
        List<String> sensorList = new ArrayList() ;
        for(String sIds : device.getSensorList())
        {
            sensorList.add(sIds) ;
        }
        return sensorList ;
    }


}






