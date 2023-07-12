package com.stage.digibackend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CsvData {
    private String historyId;
    private String date;
    private String action;
    private String deviceId;
    private String deviceName;
    private String macAddress;
    private String sensorId;
    private String sensorName;
    private String sensorUnit;
    private double data;
    private double total;
}
