package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.services.IhistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historique")
@CrossOrigin(origins = "*")
public class HistoriqueController {
   IhistoriqueService historiqueService ;

    public HistoriqueController(IhistoriqueService historiqueService) {
        this.historiqueService = historiqueService;
    }

    @PostMapping("/addHistorique")
    public String addHistorique(@RequestBody Historique historique) {
        String result = historiqueService.addHistorique(historique);
        return result;
    }

    @GetMapping("/getHistorique")
    public List<Historique> getHistorique() {
        List<Historique> historiqueList = historiqueService.getHistorique();
        return historiqueList;
    }

    @DeleteMapping("/deleteHistorique/{historiqueId}")
    public String deleteHistorique(@PathVariable String historiqueId) {
        String result = historiqueService.deleteHistorique(historiqueId);
        return result;
    }

    @GetMapping("/findHistoriqueByDevice/{idDevice}")
    public List<Historique> findHistoriqueByDevice(@PathVariable String idDevice) {
        return historiqueService.findHistoriqueByDevice(idDevice);
    }

    @GetMapping("/findHistoriqueByDeviceAndSensor/{idDevice}/{idSensor}")
    public List<Historique> findHistoriqueByDeviceAndSensor(@PathVariable String idDevice, @PathVariable String idSensor) {
        return historiqueService.findHistoriqueByDeviceAndSensor(idDevice,idSensor);
    }
}
