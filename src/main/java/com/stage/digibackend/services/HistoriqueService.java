package com.stage.digibackend.services;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.stage.digibackend.Collections.DataSensor;
import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.Historique;
import com.stage.digibackend.Collections.Sensor;
import com.stage.digibackend.dto.CsvData;
import com.stage.digibackend.repository.DataSensorRepository;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.HistoriqueRepository;
import com.stage.digibackend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


import java.time.LocalDateTime;
import java.time.LocalTime;


import java.util.ArrayList;

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
            if (historiqueDate.compareTo(startDate) >= 0 && historiqueDate.compareTo(endDate) <= 0) {
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

    @Override


    public void exportToCSV(String deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        if (startDate.plusMonths(3).isBefore(endDate)) {
            throw new IllegalArgumentException("Date range cannot exceed three months.");
        }

        List<Historique> deviceHist = findHistoriqueByDevice(deviceId);
        List<Historique> filteredHist = new ArrayList<>();
        for (Historique hist : deviceHist) {
            LocalDateTime histDate = hist.getDate();
            if (histDate.compareTo(startDate) >= 0 && histDate.compareTo(endDate) <= 0) {
                filteredHist.add(hist);
            }
        }

        String userHomeDir = System.getProperty("user.home");
        String downloadsDir = userHomeDir + File.separator + "Downloads";
        String csvFilePath = downloadsDir + File.separator + "historique.csv";
        System.out.println(csvFilePath);

        try (ICsvBeanWriter csvWriter = new CsvBeanWriter(new FileWriter(csvFilePath),
                CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
            String[] header = {"historyId", "date", "action", "deviceId", "deviceName", "macAddress",
                    "sensorId", "sensorName", "sensorUnit", "rangeMin", "rangeMax", "signal",
                    "coefficientA", "coefficientB", "latestUpdate", "growthStatus", "data", "total"};

            csvWriter.writeHeader(header);

            CellProcessor[] processors = new CellProcessor[]{
                    new Optional(), // historyId
                    new Optional(), // date
                    new Optional(), // action
                    new Optional(), // deviceId
                    new Optional(), // deviceName
                    new Optional(), // macAddress
                    new Optional(), // sensorId
                    new Optional(), // sensorName
                    new Optional(), // sensorUnit
                    new Optional(), // rangeMin
                    new Optional(), // rangeMax
                    new Optional(), // signal
                    new Optional(), // coefficientA
                    new Optional(), // coefficientB
                    new Optional(), // latestUpdate
                    new Optional(), // growthStatus
                    new Optional(), // data
                    new Optional() // total
            };

            for (Historique hist : filteredHist) {
                CsvData csvData = new CsvData();
                csvData.setHistoryId(String.valueOf(hist.getId()));
                csvData.setDate(hist.getDate().toString());
                csvData.setAction(hist.getAction());
                csvData.setDeviceId(hist.getDataSensor().getDevice().getDeviceId());
                csvData.setDeviceName(hist.getDataSensor().getDevice().getNom());
                csvData.setMacAddress(hist.getDataSensor().getDevice().getMacAdress());
                csvData.setSensorId(hist.getDataSensor().getSensor().getSensorId());
                csvData.setSensorName(hist.getDataSensor().getSensor().getSensorName());
                csvData.setSensorUnit(hist.getDataSensor().getSensor().getSymboleUnite());
                csvData.setRangeMin(hist.getDataSensor().getSensor().getRangeMin());
                csvData.setRangeMax(hist.getDataSensor().getSensor().getRangeMax());
                csvData.setSignal(hist.getDataSensor().getSensor().getSignal());
                csvData.setCoefficientA(hist.getDataSensor().getSensor().getA());
                csvData.setCoefficientB(hist.getDataSensor().getSensor().getB());
                csvData.setLatestUpdate(hist.getDataSensor().getLatestUpdate().toString());
                csvData.setGrowthStatus(hist.getDataSensor().getGrowthStatus().toString());
                csvData.setData(hist.getDataSensor().getData());
                csvData.setTotal(hist.getDataSensor().getTotal());

                csvWriter.write(csvData, header, processors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Historique> lastMonthHistorique(String idDevice) {

        LocalDateTime lastMonthDate = LocalDateTime.now().minusMonths(7);
        Device d = deviceRepository.findById(idDevice).get();
        return historiqueRepository.findHistoriqueForLastMonth(d,lastMonthDate);
    }


}
