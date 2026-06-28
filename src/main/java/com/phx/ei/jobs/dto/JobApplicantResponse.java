package com.phx.ei.jobs.dto;

import com.phx.ei.jobs.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JobApplicantResponse {
    private UUID applicationId;
    private UUID jobId;
    private UUID individualUserId;
    private String fullName;
    private String phone;
    private String email;
    private String location;
    private String profession;
    private String skills;
    private JobApplicationStatus status;
    private LocalDateTime appliedAt;
    private boolean cvUploaded;
    private String cvDocument;
}
