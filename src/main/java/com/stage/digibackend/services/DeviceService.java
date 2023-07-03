package com.stage.digibackend.services;

import com.stage.digibackend.Collections.Device;
import com.stage.digibackend.Collections.User;
import com.stage.digibackend.dto.OtpStatus;
import com.stage.digibackend.dto.deviceResponse;
import com.stage.digibackend.repository.DeviceRepository;
import com.stage.digibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DeviceService implements IDeviceService {
    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public String addDevice(Device device) {
        System.out.println(deviceRepository.findBymacAdress(device.getMacAdress()));
        if(deviceRepository.findBymacAdress(device.getMacAdress())==null) {
            return deviceRepository.save(device).getDeviceId();
        }
        return "Error a device exists with address mac";
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Device getDeviceById(String deviceId) {
        System.out.println("User ID"+deviceId);
        System.out.println(deviceRepository.findById(deviceId).get());
        return deviceRepository.findById(deviceId).get();
    }

    @Override
    public Device getDeviceByMacAdd(String add_mac) {
        return deviceRepository.findBymacAdress(add_mac);
    }

    @Override
    public deviceResponse updateDevice(String deviceId, Device deviceRequest) {
        deviceResponse response=null;
        //get the document from db with the specific id
        Device existingDevice= deviceRepository.findById(deviceId).get();
        if(existingDevice!=null)
        {
            existingDevice.setDescription(deviceRequest.getDescription());
            existingDevice.setActive(deviceRequest.getActive());
            existingDevice.setSensorList(deviceRequest.getSensorList());
            deviceRepository.save(existingDevice);
           response=new deviceResponse(OtpStatus.SUCCED,existingDevice) ;
        }
        else {
            response=new deviceResponse(OtpStatus.FAILED,existingDevice) ;
        }
        return response;
    }

    @Override
    public String deleteDevice(String deviceId) {
         deviceRepository.deleteById(deviceId);
        return deviceId +"   Device deleted succesully";
    }

    @Override
    public String affectDeviceToAdmin(String deviceId,String adminId) {
        Device existingDevice= deviceRepository.findById(deviceId).get();
        if(existingDevice.getIdAdmin()==null)
        {
            existingDevice.setIdAdmin(adminId);
            deviceRepository.save(existingDevice);
            return "Device" +deviceId+ "affected succesufully to admin "+adminId;
        }

return "Device already affected";
    }
}
