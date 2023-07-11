package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Historique;

import java.util.List;

public interface IhistoriqueService {
    String addHistorique(Historique historique);
    List<Historique> getHistorique();
    String deleteHistorique(String historiqueId);
    List<Historique> findHistoriqueByDevice(String idDevice) ;
    List<Historique> findHistoriqueByDeviceAndSensor(String idDevice, String idSensor) ;
}
