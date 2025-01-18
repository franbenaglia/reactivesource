package com.fab.reactivesource.source.model;

//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;

//import lombok.Data;

//@Document(collection = "climateData")
//@Data
@SuppressWarnings("unused")
public class ClimateData {

    // @Id
    private String id;

    private double temperature;
    private double humidity;

}
