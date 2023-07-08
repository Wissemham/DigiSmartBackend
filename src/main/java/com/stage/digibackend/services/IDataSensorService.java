package com.stage.digibackend.services;

import com.stage.digibackend.Collections.DataSensor;

public interface IDataSensorService {

    DataSensor affecteSensorDevice (String idSensor, String idDevice);

    DataSensor loadDataInSensorDevice (String idSensor, String idDevice, Double time, Double data);
}
