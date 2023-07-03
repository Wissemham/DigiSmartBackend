package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Sensor;

import java.util.List;

public interface ISensorService {

    String addSensor(Sensor sensor);
    String addSensors(List<Sensor> sensorList);
    String updateSensor(Sensor sensor,String idSensor);
    String deleteSensor(String sensorId);
    String getSensor(String sensorId);
    String getAllSensors( List<String> sensorIds);
List<Sensor> getAllSensors();
}
