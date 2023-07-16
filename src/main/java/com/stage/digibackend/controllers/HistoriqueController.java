package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.services.IhistoriqueService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/generateDataSensorHistoriquePdf/{dataSensorId}")
    public ResponseEntity<ByteArrayResource> generateDataSensorHistoriquePdf(@PathVariable String dataSensorId) throws IOException {

        byte[] pdfBytes = historiqueService.generateDataSensorHistoriquePdf(dataSensorId);

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=historique.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }


    @GetMapping("/device/{deviceId}/{offset}/{pagesize}")
    public List<Map<String, Object>> groupHistoriqueDataBySensorAndDate(@PathVariable String deviceId,@PathVariable int offset,@PathVariable int pagesize) {
        return historiqueService.groupHistoriqueDataBySensorAndDate(deviceId , offset, pagesize);
    }



    @GetMapping("/device1/{deviceId}/{debut}/{fin}")
    public List<Map<String, Object>> groupHistoriqueDataBySensorAndDate1(@PathVariable String deviceId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate debut, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin) {
        return historiqueService.groupHistoriqueDataBySensorAndDate1(deviceId , debut, fin);
    }

}
