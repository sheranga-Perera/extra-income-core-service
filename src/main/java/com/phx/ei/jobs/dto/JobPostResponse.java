package com.phx.ei.jobs.dto;

import com.phx.ei.jobs.entity.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JobPostResponse {
    private UUID id;
    private String companyName;
    private String title;
    private String description;
    private String category;
    private String sector;
    private String location;
    private Integer hoursPerWeek;
    private BigDecimal hourlyRate;
    private String contractType;
    private String contractDuration;
    private JobStatus status;
    private LocalDateTime createdAt;
}
