package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.dto.ExportDataRequest;
import com.stage.digibackend.services.IhistoriqueService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @GetMapping("/generateDeviceHistoriquePdf/{deviceId}")
    public ResponseEntity<ByteArrayResource> generateDeviceHistoriquePdf(@PathVariable String deviceId,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate ,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate endDate) throws IOException {

        byte[] pdfBytes = historiqueService.generateDeviceHistoriquePdf(deviceId,startDate,endDate);

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=device_historique.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);

    }

    @GetMapping("/loadMore/{idDevice}")
    public Page<Historique> getHistorique(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable String idDevice
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return historiqueService.getHistorique(pageable);
    }

    @GetMapping("/loadMoreHistoricDevice/{deviceId}")
    public Page<Historique> getHistoriqueByDevice(@PathVariable String deviceId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        return historiqueService.findHistoriqueByDevicePagebale(deviceId, pageable);

    }


    @GetMapping("/exportToCSV/{deviceId}/{startDate}/{endDate}")
    public String exportDataToCSV(@PathVariable String deviceId,
                                                  @PathVariable String startDate,
                                                  @PathVariable String endDate) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";


        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime startDateForm = LocalDateTime.parse(startDate, formatter);
            LocalDateTime enddateForm = LocalDateTime.parse(endDate, formatter);

            historiqueService.exportToCSV(deviceId, startDateForm, enddateForm);
            return "Data history exported to CSV file";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
