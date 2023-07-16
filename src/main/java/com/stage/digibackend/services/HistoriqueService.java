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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> groupHistoriqueDataBySensorAndDate(String deviceId,int offset,int pagesize) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        System.out.println("*****************");
        List<Historique> historiqueList = historiqueRepository.findAllByDataSensorDevice(device,PageRequest.of(offset,pagesize));

        Map<String, List<Historique>> sensorDataMap = new HashMap<>();

        for (Historique historique : historiqueList) {
            DataSensor dataSensor = historique.getDataSensor();
            Sensor sensor = dataSensor.getSensor();

            String sensorId = sensor.getSensorName();
            Double dataValue = dataSensor.getData();
            String date = historique.getDate().toString();

            // Group data by Sensor and Date

            String unite = sensor.getUnit().getSymbol();
            String key = sensorId+"_"+unite;
            sensorDataMap.computeIfAbsent(key, k -> new ArrayList<>()).add(historique);
        }

        return sensorDataMap.entrySet().stream()
                .map(entry -> {
                    String sensorId = entry.getKey();
                    List<Historique> historiques = entry.getValue();
                    List<Map<String, Object>> series = historiques.stream()
                            .map(historique -> {
                                Double value = historique.getDataSensor().getData();
                                String date = historique.getDate().toString();
                                Map<String, Object> seriesEntry = new HashMap<>();
                                seriesEntry.put("value", value );
                                seriesEntry.put("name", date);
                                return seriesEntry;
                            })
                            .collect(Collectors.toList());

                    Map<String, Object> sensorData = new HashMap<>();
                    sensorData.put("name", sensorId);
                    sensorData.put("series", series);
                    return sensorData;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> groupHistoriqueDataBySensorAndDate1(String deviceId, LocalDate startDate, LocalDate endDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        System.out.println("*****************");
        List<Historique> historiqueList = historiqueRepository.findAllByDataSensorDeviceAndDateBetween(device, startDate, endDate);

        Map<String, List<Historique>> sensorDataMap = new HashMap<>();

        for (Historique historique : historiqueList) {
            DataSensor dataSensor = historique.getDataSensor();
            Sensor sensor = dataSensor.getSensor();

            String sensorId = sensor.getSensorName();
            Double dataValue = dataSensor.getData();
            String date = historique.getDate().toString();

            // Group data by Sensor and Date

            String unite = sensor.getUnit().getSymbol();
            String key = sensorId + "_" + unite;
            sensorDataMap.computeIfAbsent(key, k -> new ArrayList<>()).add(historique);
        }

        return sensorDataMap.entrySet().stream()
                .map(entry -> {
                    String sensorId = entry.getKey();
                    List<Historique> historiques = entry.getValue();
                    List<Map<String, Object>> series = historiques.stream()
                            .map(historique -> {
                                Double value = historique.getDataSensor().getData();
                                String date = historique.getDate().toString();
                                Map<String, Object> seriesEntry = new HashMap<>();
                                seriesEntry.put("value", value);
                                seriesEntry.put("name", date);
                                return seriesEntry;
                            })
                            .collect(Collectors.toList());

                    Map<String, Object> sensorData = new HashMap<>();
                    sensorData.put("name", sensorId);
                    sensorData.put("series", series);
                    return sensorData;
                })
                .collect(Collectors.toList());
    }


}
