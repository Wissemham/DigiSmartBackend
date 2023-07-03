package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.User;
import com.stage.digibackend.dto.deviceResponse;

import java.util.List;

public interface IDeviceService {
    String addDevice(Device device);
    List<Device> getAllDevices();
    Device getDeviceById(String deviceId);
    Device getDeviceByMacAdd(String add_mac);
    deviceResponse updateDevice(String deviceId, Device deviceRequest);
    String deleteDevice(String deviceId);
    String affectDeviceToAdmin(String deviceId,String adminId);

}
