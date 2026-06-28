package com.phx.ei.jobs.controller;

import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.service.CurrentUserService;
import com.phx.ei.jobs.dto.AppliedJobResponse;
import com.phx.ei.jobs.dto.JobApplicationRequest;
import com.phx.ei.jobs.dto.JobApplicationResponse;
import com.phx.ei.jobs.dto.JobApplicantResponse;
import com.phx.ei.jobs.dto.JobPostRequest;
import com.phx.ei.jobs.dto.JobPostResponse;
import com.phx.ei.jobs.entity.CvRequirement;
import com.phx.ei.jobs.entity.JobApplication;
import com.phx.ei.jobs.entity.JobApplicationStatus;
import com.phx.ei.jobs.entity.JobPost;
import com.phx.ei.jobs.entity.JobStatus;
import com.phx.ei.jobs.repository.JobApplicationRepository;
import com.phx.ei.jobs.repository.JobPostRepository;
import com.phx.ei.profile.entity.IndividualProfile;
import com.phx.ei.profile.repository.CompanyProfileRepository;
import com.phx.ei.profile.repository.IndividualProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobPostController {

    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;
    private final CompanyProfileRepository companyProfileRepository;
    private final IndividualProfileRepository individualProfileRepository;

    @PostMapping
    public ResponseEntity<JobPostResponse> createJob(@RequestBody JobPostRequest request) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.COMPANY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can create job posts");
        }

        validateRequest(request);

        String companyName = companyProfileRepository.findByUserId(user.getId())
                .map(profile -> profile.getCompanyName())
                .filter(name -> name != null && !name.isBlank())
                .orElse(user.getUsername());

        JobPost post = new JobPost();
        post.setId(UUID.randomUUID());
        post.setCompanyUser(user);
        post.setCompanyName(companyName);
        post.setTitle(request.getTitle().trim());
        post.setDescription(request.getDescription().trim());
        post.setCategory(trimOrNull(request.getCategory()));
        post.setSector(trimOrNull(request.getSector()));
        post.setLocation(trimOrNull(request.getLocation()));
        post.setHoursPerWeek(request.getHoursPerWeek());
        post.setHourlyRate(request.getHourlyRate());
        post.setContractType(trimOrNull(request.getContractType()));
        post.setContractDuration(trimOrNull(request.getContractDuration()));
        post.setCvRequirement(request.getCvRequirement() == null
                ? CvRequirement.NOT_REQUIRED
                : request.getCvRequirement());
        post.setStatus(JobStatus.OPEN);

        JobPost saved = jobPostRepository.save(post);
        log.info("Job post created: jobId={}, companyUserId={}", saved.getId(), user.getId());
        return ResponseEntity.ok(toResponse(saved, user));
    }

    @GetMapping
    public ResponseEntity<List<JobPostResponse>> listJobs(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sector", required = false) String sector,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "contractType", required = false) String contractType,
            @RequestParam(value = "contractDuration", required = false) String contractDuration,
            @RequestParam(value = "minRate", required = false) BigDecimal minRate,
            @RequestParam(value = "maxRate", required = false) BigDecimal maxRate
    ) {
        User user = currentUserService.getCurrentUser();
        List<JobPost> posts = findVisibleJobs(user, query);

        Stream<JobPost> stream = posts.stream();
        if (category != null && !category.isBlank()) {
            String categoryFilter = category.trim().toLowerCase();
            stream = stream.filter(post -> safeLower(post.getCategory()).contains(categoryFilter));
        }
        if (sector != null && !sector.isBlank()) {
            String sectorFilter = sector.trim().toLowerCase();
            stream = stream.filter(post -> safeLower(post.getSector()).contains(sectorFilter));
        }
        if (location != null && !location.isBlank()) {
            String locationFilter = location.trim().toLowerCase();
            stream = stream.filter(post -> safeLower(post.getLocation()).contains(locationFilter));
        }
        if (contractType != null && !contractType.isBlank()) {
            String contractTypeFilter = contractType.trim().toLowerCase();
            stream = stream.filter(post -> safeLower(post.getContractType()).contains(contractTypeFilter));
        }
        if (contractDuration != null && !contractDuration.isBlank()) {
            String contractDurationFilter = contractDuration.trim().toLowerCase();
            stream = stream.filter(post -> safeLower(post.getContractDuration()).contains(contractDurationFilter));
        }
        if (minRate != null) {
            stream = stream.filter(post -> post.getHourlyRate() != null
                    && post.getHourlyRate().compareTo(minRate) >= 0);
        }
        if (maxRate != null) {
            stream = stream.filter(post -> post.getHourlyRate() != null
                    && post.getHourlyRate().compareTo(maxRate) <= 0);
        }

        List<JobPostResponse> response = stream.map(post -> toResponse(post, user)).toList();
        return ResponseEntity.ok(response);
    }

    private List<JobPost> findVisibleJobs(User user, String query) {
        String normalizedQuery = trimOrNull(query);
        if (user.getRole() == Role.COMPANY) {
            if (normalizedQuery != null && !normalizedQuery.isBlank()) {
                return jobPostRepository.searchByCompanyUserIdAndQuery(
                        user.getId(),
                        normalizedQuery,
                        JobStatus.OPEN
                );
            }
            return jobPostRepository.findByCompanyUserIdAndStatusOrderByCreatedAtDesc(
                    user.getId(),
                    JobStatus.OPEN
            );
        }

        if (normalizedQuery != null && !normalizedQuery.isBlank()) {
            return jobPostRepository.searchByQuery(normalizedQuery, JobStatus.OPEN);
        }
        return jobPostRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJob(@PathVariable UUID id) {
        User user = currentUserService.getCurrentUser();
        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (post.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        if (user.getRole() == Role.COMPANY && !post.getCompanyUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        return ResponseEntity.ok(toResponse(post, user));
    }

    @GetMapping("/applications/me")
    public ResponseEntity<List<AppliedJobResponse>> listMyApplications() {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.INDIVIDUAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only individuals can view applied jobs");
        }

        List<AppliedJobResponse> response = jobApplicationRepository
                .findByIndividualUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(application -> toAppliedJobResponse(application, user))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<List<JobApplicantResponse>> listJobApplicants(@PathVariable UUID id) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.COMPANY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can view job applicants");
        }

        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (!post.getCompanyUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        List<JobApplicantResponse> response = jobApplicationRepository
                .findByJobPost_IdOrderByCreatedAtDesc(post.getId())
                .stream()
                .map(this::toApplicantResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/applications")
    public ResponseEntity<JobApplicationResponse> applyForJob(
            @PathVariable UUID id,
            @RequestBody(required = false) JobApplicationRequest request
    ) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.INDIVIDUAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only individuals can apply for jobs");
        }

        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (post.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        if (jobApplicationRepository.existsByJobPost_IdAndIndividualUser_Id(post.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied for this job.");
        }

        String cvDocument = trimOrNull(request == null ? null : request.getCvDocument());
        if (post.getCvRequirement() == CvRequirement.REQUIRED && cvDocument == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CV is required to apply for this job.");
        }

        JobApplication application = new JobApplication();
        application.setId(UUID.randomUUID());
        application.setJobPost(post);
        application.setIndividualUser(user);
        application.setStatus(JobApplicationStatus.SUBMITTED);
        application.setCvDocument(post.getCvRequirement() == CvRequirement.NOT_REQUIRED ? null : cvDocument);

        try {
            JobApplication saved = jobApplicationRepository.saveAndFlush(application);
            log.info("Job application submitted: applicationId={}, jobId={}, individualUserId={}",
                    saved.getId(), post.getId(), user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toApplicationResponse(saved));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied for this job.");
        }
    }

    private void validateRequest(JobPostRequest request) {
        if (request == null
                || isBlank(request.getTitle())
                || isBlank(request.getDescription())
                || request.getHoursPerWeek() == null
                || request.getHourlyRate() == null
                || isBlank(request.getContractDuration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job post fields");
        }
    }

    private JobPostResponse toResponse(JobPost post, User user) {
        boolean applied = user.getRole() == Role.INDIVIDUAL
                && jobApplicationRepository.existsByJobPost_IdAndIndividualUser_Id(post.getId(), user.getId());
        return new JobPostResponse(
                post.getId(),
                post.getCompanyName(),
                post.getTitle(),
                post.getDescription(),
                post.getCategory(),
                post.getSector(),
                post.getLocation(),
                post.getHoursPerWeek(),
                post.getHourlyRate(),
                post.getContractType(),
                post.getContractDuration(),
                post.getCvRequirement(),
                post.getStatus(),
                post.getCreatedAt(),
                applied
        );
    }

    private JobApplicationResponse toApplicationResponse(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getJobPost().getId(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getCvDocument() != null && !application.getCvDocument().isBlank()
        );
    }

    private AppliedJobResponse toAppliedJobResponse(JobApplication application, User user) {
        return new AppliedJobResponse(
                application.getId(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getCvDocument() != null && !application.getCvDocument().isBlank(),
                toResponse(application.getJobPost(), user)
        );
    }

    private JobApplicantResponse toApplicantResponse(JobApplication application) {
        User applicant = application.getIndividualUser();
        IndividualProfile profile = individualProfileRepository.findByUserId(applicant.getId()).orElse(null);
        String fullName = profile == null ? applicant.getUsername() : profile.getFullName();
        String phone = profile == null ? null : profile.getPhone();
        String email = profile == null ? applicant.getUsername() : profile.getEmail();
        String location = profile == null ? null : profile.getLocation();
        String profession = profile == null ? null : profile.getProfession();
        String skills = profile == null ? null : profile.getSkills();
        String cvDocument = trimOrNull(application.getCvDocument());

        return new JobApplicantResponse(
                application.getId(),
                application.getJobPost().getId(),
                applicant.getId(),
                fullName,
                phone,
                email,
                location,
                profession,
                skills,
                application.getStatus(),
                application.getCreatedAt(),
                cvDocument != null,
                cvDocument
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
