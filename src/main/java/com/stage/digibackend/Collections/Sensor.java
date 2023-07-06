package com.stage.digibackend.Collections;

import com.stage.digibackend.Enumeration.EUnite;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.NotBlank;

@Document(collection = "sensors")
@AllArgsConstructor
@Data
@Builder
public class Sensor {

    @Id
    private String sensorId ;
    @NotBlank
    private String sensorName ;
    @NotBlank
    private Double rangeMin ;
    @NotBlank
    private Double rangeMax ;


    private EUnite unit ;
    private String symboleUnite ;


}
