package com.stage.digibackend.Collections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dataSensor")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DataSensor {

    @Id
    private String dataSensorId ;
    @DBRef
    private Sensor sensor ;
    @DBRef
    private Device device ;

    private Double time ;

    private Double data ;




}
