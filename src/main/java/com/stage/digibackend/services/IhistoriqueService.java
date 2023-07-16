package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Historique;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IhistoriqueService {
    String addHistorique(Historique historique);
    List<Historique> getHistorique();
    String deleteHistorique(String historiqueId);
    List<Historique> findHistoriqueByDevice(String idDevice) ;
    List<Historique> findHistoriqueByDeviceAndSensor(String idDevice, String idSensor) ;

    byte[] generateDataSensorHistoriquePdf(String dataSensorId) throws IOException;

    List<Map<String, Object>> groupHistoriqueDataBySensorAndDate(String deviceId,int offset,int pagesize);

    public List<Map<String, Object>> groupHistoriqueDataBySensorAndDate1(String deviceId, LocalDate startDate, LocalDate endDate);
}
