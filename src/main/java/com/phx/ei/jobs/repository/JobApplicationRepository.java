package com.phx.ei.jobs.repository;

import com.phx.ei.jobs.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    boolean existsByJobPost_IdAndIndividualUser_Id(UUID jobPostId, UUID individualUserId);

    List<JobApplication> findByJobPost_IdOrderByCreatedAtDesc(UUID jobPostId);

    List<JobApplication> findByIndividualUser_IdOrderByCreatedAtDesc(UUID individualUserId);
}
