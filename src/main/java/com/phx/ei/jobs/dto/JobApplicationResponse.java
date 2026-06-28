package com.phx.ei.jobs.dto;

import com.phx.ei.jobs.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JobApplicationResponse {
    private UUID id;
    private UUID jobId;
    private JobApplicationStatus status;
    private LocalDateTime createdAt;
    private boolean cvUploaded;
}
