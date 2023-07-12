package com.stage.digibackend.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.repository.DataSensorRepository;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.HistoriqueRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoriqueService implements IhistoriqueService {
    @Autowired
    HistoriqueRepository historiqueRepository;
    @Autowired
    DeviceRepository deviceRepository ;
    @Autowired
    SensorRepository sensorRepository ;
    @Autowired
    DataSensorRepository dataSensorRepository ;

    public HistoriqueService(HistoriqueRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    @Override
    public String addHistorique(Historique historique) {
        historiqueRepository.save(historique);
        return "Historique ajouté avec succès.";
    }

    @Override
    public List<Historique> getHistorique() {
        return historiqueRepository.findAll();
    }

    @Override
    public String deleteHistorique(String historiqueId) {
        historiqueRepository.deleteById(historiqueId);
        return "Historique supprimé avec succès.";
    }

    @Override
    public List<Historique> findHistoriqueByDevice(String idDevice) {
        Device device = deviceRepository.findById(idDevice).get();
        return historiqueRepository.findAllByDataSensorDevice(device);
    }

    @Override
    public List<Historique> findHistoriqueByDeviceAndSensor(String idDevice, String idSensor) {
        Device device = deviceRepository.findById(idDevice).get();
        Sensor sensor = sensorRepository.findById(idSensor).get() ;
        return historiqueRepository.findAllByDataSensorDeviceAndDataSensorSensor(device,sensor);
    }

    @Override
    public byte[] generateDataSensorHistoriquePdf(String dataSensorId) throws IOException {
        // Get the data sensor and its historique data
        DataSensor dataSensor = dataSensorRepository.findById(dataSensorId)
                .orElseThrow(() -> new IllegalArgumentException("Data sensor not found"));

        List<Historique> historiqueList = findHistoriqueByDeviceAndSensor(
                dataSensor.getDevice().getDeviceId(),
                dataSensor.getSensor().getSensorId());

        // Get the device and its sensors
        Device device = dataSensor.getDevice();
        Sensor sensor = dataSensor.getSensor();

        // Create a new PDF document
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);

        // Create a new page
        Document document = new Document(pdf);

        // Page content
        document.add(new Paragraph("Historique for Data Sensor: " + dataSensorId).setBold());
        document.add(new Paragraph("\n"));

        // Add device information
        document.add(new Paragraph("Device Information:").setBold());
        document.add(new Paragraph("Device ID: " + device.getDeviceId()));
        document.add(new Paragraph("MAC Address: " + device.getMacAdress()));
        document.add(new Paragraph("Name: " + device.getNom()));
        document.add(new Paragraph("Description: " + device.getDescription()));
        document.add(new Paragraph("Latitude: " + device.getLat()));
        document.add(new Paragraph("Longitude: " + device.getLng()));
        document.add(new Paragraph("\n"));

        // Add sensor information
        document.add(new Paragraph("Sensor Information:").setBold());
        document.add(new Paragraph("Sensor ID: " + sensor.getSensorId()));
        document.add(new Paragraph("Sensor Name: " + sensor.getSensorName()));
        document.add(new Paragraph("Range Min: " + sensor.getRangeMin()));
        document.add(new Paragraph("Range Max: " + sensor.getRangeMax()));
        document.add(new Paragraph("Unit: " + sensor.getUnit()));
        document.add(new Paragraph("Unit Symbol: " + sensor.getSymboleUnite()));
        document.add(new Paragraph("Signal: " + sensor.getSignal()));
        document.add(new Paragraph("Coefficient a: " + sensor.getA()));
        document.add(new Paragraph("Coefficient b: " + sensor.getB()));
        document.add(new Paragraph("\n"));

        // Add historique data
        document.add(new Paragraph("Historique Data:").setBold());
        for (Historique historique : historiqueList) {
            float remainingHeight = document.getPdfDocument().getDefaultPageSize().getHeight() - document.getRenderer().getCurrentArea().getBBox().getY();
            if (remainingHeight < 50) {
                // Create a new page if remaining space is not sufficient
                document.add(new AreaBreak());
            }

            document.add(new Paragraph("Date: " + historique.getDate()).setBold());
            document.add(new Paragraph("Action: " + historique.getAction()));

            // Add more data sensor information as needed
            document.add(new Paragraph("Latest Update: " + historique.getDataSensor().getLatestUpdate()));
            document.add(new Paragraph("Growth Status: " + historique.getDataSensor().getGrowthStatus()));
            document.add(new Paragraph("Data: " + historique.getDataSensor().getData()));
            document.add(new Paragraph("Total: " + historique.getDataSensor().getTotal()));

            document.add(new Paragraph("\n"));
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] generateDeviceHistoriquePdf(String deviceId, LocalDate startDate, LocalDate endDate) throws IOException {

        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("start date cannot be after end date.");
        }

        if (startDateTime.plusMonths(3).isBefore(endDateTime)) {
            throw new IllegalArgumentException("maximum allowed period is 3 months.");
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        List<Historique> historiqueList = findHistoriqueByDevice(deviceId);

        historiqueList = filterHistoriqueByDateRange(historiqueList, startDate, endDate);


        // Create a new PDF document
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);

        // Create a new page
        Document document = new Document(pdf);

        // Page content
        document.add(new Paragraph("Device historic: " + device.getDeviceCode()).setBold());
        document.add(new Paragraph("\n"));

        // Add device information
        document.add(new Paragraph("Device Information:").setBold());
        document.add(new Paragraph("Device ID: " + device.getDeviceId()));
        document.add(new Paragraph("MAC Address: " + device.getMacAdress()));
        document.add(new Paragraph("Name: " + device.getNom()));
        document.add(new Paragraph("Description: " + device.getDescription()));
        document.add(new Paragraph("Latitude: " + device.getLat()));
        document.add(new Paragraph("Longitude: " + device.getLng()));
        document.add(new Paragraph("\n"));

        for (String s : device.getSensorList()) {
            Sensor sensor = sensorRepository.findById(s)
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found with id : "+s));
            // Add sensor information
            document.add(new Paragraph("Sensor Information:").setBold());
            document.add(new Paragraph("Sensor ID: " + sensor.getSensorId()));
            document.add(new Paragraph("Sensor Name: " + sensor.getSensorName()));
            document.add(new Paragraph("Range Min: " + sensor.getRangeMin()));
            document.add(new Paragraph("Range Max: " + sensor.getRangeMax()));
            document.add(new Paragraph("Unit: " + sensor.getUnit()));
            document.add(new Paragraph("Unit Symbol: " + sensor.getSymboleUnite()));
            document.add(new Paragraph("Signal: " + sensor.getSignal()));
            document.add(new Paragraph("Coefficient a: " + sensor.getA()));
            document.add(new Paragraph("Coefficient b: " + sensor.getB()));
            document.add(new Paragraph("\n"));

            // Add historique data
            document.add(new Paragraph("Sensor data historique").setBold());
            for (Historique historique : historiqueList) {
                if(historique.getDataSensor().getSensor().equals(sensor)) {
                    float remainingHeight = document.getPdfDocument().getDefaultPageSize().getHeight() - document.getRenderer().getCurrentArea().getBBox().getY();
                    if (remainingHeight < 50) {
                        // Create a new page if remaining space is not sufficient
                        document.add(new AreaBreak());
                    }

                    document.add(new Paragraph("Date: " + historique.getDate()).setBold());
                    document.add(new Paragraph("Action: " + historique.getAction()));

                    // Add more data sensor information as needed
                    document.add(new Paragraph("Latest Update: " + historique.getDataSensor().getLatestUpdate()));
                    document.add(new Paragraph("Growth Status: " + historique.getDataSensor().getGrowthStatus()));
                    document.add(new Paragraph("Data: " + historique.getDataSensor().getData()));
                    document.add(new Paragraph("Total: " + historique.getDataSensor().getTotal()));

                    document.add(new Paragraph("\n"));
                }
            }
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private List<Historique> filterHistoriqueByDateRange(List<Historique> historiqueList, LocalDate startDate, LocalDate endDate) {
        List<Historique> filteredList = new ArrayList<>();

        for (Historique historique : historiqueList) {
            LocalDate historiqueDate = historique.getDate().toLocalDate();
            if (historiqueDate.isEqual(startDate) || historiqueDate.isEqual(endDate) || (historiqueDate.isAfter(startDate) && historiqueDate.isBefore(endDate))) {
                filteredList.add(historique);
            }
        }
        return filteredList;
    }

    @Override
    public Page<Historique> getHistorique(Pageable pageable) {
        return historiqueRepository.findAll(pageable);
    }

    @Override
    public Page<Historique> findHistoriqueByDevicePagebale(String idDevice, Pageable pageable) {

        return historiqueRepository.findAllByDataSensorDevice(idDevice,pageable);

    }


}
