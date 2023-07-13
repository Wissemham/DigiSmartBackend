package com.stage.digibackend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
    private double rangeMin;
    private double rangeMax;
    private Boolean signal;
    private double coefficientA;
    private double coefficientB;
    private String latestUpdate;
    private String growthStatus;
    private double data;
    private double total;
}
