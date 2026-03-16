package com.phx.ei.jobs.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobPostRequest {
    private String title;
    private String description;
    private String category;
    private String sector;
    private String location;
    private Integer hoursPerWeek;
    private BigDecimal hourlyRate;
    private String contractType;
    private String contractDuration;
}
