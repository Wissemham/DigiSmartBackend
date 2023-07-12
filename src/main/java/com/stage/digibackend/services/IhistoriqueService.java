package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Historique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IhistoriqueService {
    String addHistorique(Historique historique);
    List<Historique> getHistorique();
    String deleteHistorique(String historiqueId);
    List<Historique> findHistoriqueByDevice(String idDevice) ;
    List<Historique> findHistoriqueByDeviceAndSensor(String idDevice, String idSensor) ;

    byte[] generateDataSensorHistoriquePdf(String dataSensorId) throws IOException;

    byte[] generateDeviceHistoriquePdf(String deviceId, LocalDate startDate, LocalDate endDate) throws IOException;

    //List<Historique> getHistorique(int page, int pageSize);

    Page<Historique> getHistorique(Pageable pageable);

    Page<Historique> findHistoriqueByDevicePagebale(String idDevice, Pageable pageable);
}
