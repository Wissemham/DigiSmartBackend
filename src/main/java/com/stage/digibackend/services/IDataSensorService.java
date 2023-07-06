package com.stage.digibackend.services;

import com.stage.digibackend.Collections.DataSensor;

public interface IDataSensorService {

    DataSensor affecteSensorDevice (String idSensor, String idDevice);
}
