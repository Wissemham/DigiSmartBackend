package com.stage.digibackend.services;

import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.Enumeration.GrowthStatus;

public interface IDataSensorService {

    DataSensor affecteSensorDevice (String idSensor, String idDevice);

    DataSensor loadDataInSensorDevice (String idSensor, String idDevice, Double data, GrowthStatus growthStatus);
}
