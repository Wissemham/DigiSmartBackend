package com.stage.digibackend.controllers;

import com.stage.digibackend.Collections.Device;

import com.stage.digibackend.Collections.User;
import com.stage.digibackend.dto.deviceResponse;
import com.stage.digibackend.services.IDeviceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    @Autowired
    IDeviceService ideviceService;
    @PostMapping("/addDevice")
    public String addDevice(@RequestBody Device device)
    {
        return ideviceService.addDevice(device);
    }
    @GetMapping("/getAllDevices")
    public List<Device> getAllDevice() {
        return ideviceService.getAllDevices();
    }
    @GetMapping("getDeviceId/{deviceId}")
    public Device getDevice(@PathVariable String deviceId)
    {
        return ideviceService.getDeviceById(deviceId);
    }

    @GetMapping("getDeviceMac/{macadd}")
    public Device getDeviceByMac(@PathVariable String macadd)
    {
        return ideviceService.getDeviceByMacAdd(macadd);
    }
    @PutMapping("/updateDevice/{deviceId}")
    public deviceResponse updateDevice(@PathVariable String deviceId,@RequestBody Device device){return ideviceService.updateDevice(deviceId,device);}
@DeleteMapping("/deleteDevice/{deviceId}")
    public String deleteDevice(@PathVariable String deviceId)
{
    return ideviceService.deleteDevice(deviceId);
}

    @PutMapping("/affectToAdmin/{deviceId}")
    public String affectToAdmin(@PathVariable String deviceId, @RequestBody String adminId) {
        return ideviceService.affectDeviceToAdmin(deviceId,adminId);}

    }
