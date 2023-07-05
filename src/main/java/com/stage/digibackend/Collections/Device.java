package com.stage.digibackend.Collections;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import java.util.List;
@Document(collection = "devices")
@AllArgsConstructor
@Data
@Builder
public class Device {
    @Id
    private String deviceId ;
    @NotBlank
    private String macAdress ;
    @NotBlank
    private String Description ;
    private List<String> sensorList ;
    private Boolean active;
    private String idAdmin;
    private String idClient;
    private String location;
    private String deviceCode;


}
