package com.phx.ei.jobs.dto;

import com.phx.ei.jobs.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AppliedJobResponse {
    private UUID applicationId;
    private JobApplicationStatus status;
    private LocalDateTime appliedAt;
    private boolean cvUploaded;
    private JobPostResponse job;
}
