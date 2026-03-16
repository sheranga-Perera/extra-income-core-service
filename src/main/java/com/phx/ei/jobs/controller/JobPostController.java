package com.phx.ei.jobs.controller;

import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.service.CurrentUserService;
import com.phx.ei.jobs.dto.JobPostRequest;
import com.phx.ei.jobs.dto.JobPostResponse;
import com.phx.ei.jobs.entity.JobPost;
import com.phx.ei.jobs.entity.JobStatus;
import com.phx.ei.jobs.repository.JobPostRepository;
import com.phx.ei.profile.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CurrentUserService currentUserService;
    private final CompanyProfileRepository companyProfileRepository;

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
        post.setStatus(JobStatus.OPEN);

        JobPost saved = jobPostRepository.save(post);
        log.info("Job post created: jobId={}, companyUserId={}", saved.getId(), user.getId());
        return ResponseEntity.ok(toResponse(saved));
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
        List<JobPost> posts;
        if (query != null && !query.isBlank()) {
            posts = jobPostRepository.searchByQuery(query.trim(), JobStatus.OPEN);
        } else {
            posts = jobPostRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN);
        }

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

        List<JobPostResponse> response = stream.map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJob(@PathVariable UUID id) {
        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (post.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        return ResponseEntity.ok(toResponse(post));
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

    private JobPostResponse toResponse(JobPost post) {
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
                post.getStatus(),
                post.getCreatedAt()
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
