package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.services.IDataSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dataSensor")
@CrossOrigin(origins = "*")
public class DataSensorController {

    @Autowired
    IDataSensorService iDataSensorService ;

    @GetMapping("/affecteSensorDevice/{idSensor}/{idDevice}")
    public DataSensor affecteSensorDevice (@PathVariable String idSensor, @PathVariable String idDevice){
        return iDataSensorService.affecteSensorDevice(idSensor,idDevice);
    }

    @PutMapping("loadDataInSensorDevice/{idSensor}/{idDevice}")
    public DataSensor loadDataInSensorDevice(@PathVariable String idSensor,
                                             @PathVariable String idDevice,
                                             @RequestBody DataSensor dataSensor) {
        return iDataSensorService.loadDataInSensorDevice(idSensor,idDevice,dataSensor.getTime(),dataSensor.getData());
    }
}
