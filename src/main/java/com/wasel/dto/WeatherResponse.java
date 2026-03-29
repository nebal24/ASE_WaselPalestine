package com.wasel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String condition;
    private String description;
    private Double temperature;
    private Double windSpeed;
    private Integer humidity;
}