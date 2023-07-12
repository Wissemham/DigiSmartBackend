package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Historique;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IhistoriqueService {
    String addHistorique(Historique historique);
    List<Historique> getHistorique();
    String deleteHistorique(String historiqueId);

}
