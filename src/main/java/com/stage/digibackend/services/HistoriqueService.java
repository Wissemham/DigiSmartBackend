package com.stage.digibackend.services;
import org.supercsv.cellprocessor.constraint.NotNull;
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
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    public void exportToCSV(String deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Historique> deviceHist = findHistoriqueByDevice(deviceId);
        List<Historique> filteredHist = new ArrayList<>();
        for (Historique hist : deviceHist) {
            LocalDateTime histDate = hist.getDate();

            if (histDate.compareTo(startDate) >= 0 && histDate.compareTo(endDate) <= 0) {
                filteredHist.add(hist);
            }
        }
        String userHomeDir = System.getProperty("user.home");
        String csvFilePath = userHomeDir + File.separator + "historique.csv";
System.out.println(csvFilePath);
        try (ICsvBeanWriter csvWriter = new CsvBeanWriter(new FileWriter(csvFilePath),
                CsvPreference.STANDARD_PREFERENCE)) {

            String[] header = {"historyId", "date", "action", "deviceId", "deviceName", "macAddress",
                    "sensorId", "sensorName", "sensorUnit", "data", "total"};

            csvWriter.writeHeader(header);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Historique hist : filteredHist) {
                System.out.println(hist);

                DataSensor dataSensor = hist.getDataSensor();
                CsvData csvData = new CsvData();
                csvData.setHistoryId(hist.getId());
                csvData.setDate(hist.getDate().toString());
                csvData.setAction(hist.getAction());
                csvData.setDeviceId(dataSensor.getDevice().getDeviceId());
                csvData.setDeviceName(dataSensor.getDevice().getNom());
                csvData.setMacAddress(dataSensor.getDevice().getMacAdress());
                csvData.setSensorId(dataSensor.getSensor().getSensorId());
                csvData.setSensorName(dataSensor.getSensor().getSensorName());
                csvData.setSensorUnit(dataSensor.getSensor().getSymboleUnite());
                csvData.setData(dataSensor.getData());
                csvData.setTotal(dataSensor.getTotal());

                CellProcessor[] processors = new CellProcessor[]{
                        new NotNull(), // historyId (mandatory field)
                        new NotNull(), // date
                        new NotNull(), // action (mandatory field)
                        new NotNull(), // deviceId (mandatory field)
                        new NotNull(), // deviceName (mandatory field)
                        new NotNull(), // macAddress (mandatory field)
                        new NotNull(), // sensorId (mandatory field)
                        new NotNull(), // sensorName (mandatory field)
                        new NotNull(), // sensorUnit (mandatory field)
                        null, // data
                        null // total
                };

                csvWriter.write(csvData, header, processors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
