package com.phx.ei.profile.controller;

import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.service.CurrentUserService;
import com.phx.ei.profile.dto.CompanyProfileRequest;
import com.phx.ei.profile.dto.CompanyProfileResponse;
import com.phx.ei.profile.dto.IndividualProfileRequest;
import com.phx.ei.profile.dto.IndividualProfileResponse;
import com.phx.ei.profile.entity.CompanyProfile;
import com.phx.ei.profile.entity.IndividualProfile;
import com.phx.ei.profile.repository.CompanyProfileRepository;
import com.phx.ei.profile.repository.IndividualProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final IndividualProfileRepository individualProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @GetMapping("/individual/me")
    public ResponseEntity<IndividualProfileResponse> getIndividualProfile() {
        log.info("Individual profile fetch request received");
        User user = requireRole(Role.INDIVIDUAL);
        IndividualProfile profile = individualProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        log.info("Individual profile fetched: userId={}, profileId={}", user.getId(), profile.getId());
        return ResponseEntity.ok(toIndividualResponse(profile));
    }

    @PutMapping("/individual")
    public ResponseEntity<IndividualProfileResponse> upsertIndividualProfile(@RequestBody IndividualProfileRequest request) {
        log.info("Individual profile upsert request received");
        User user = requireRole(Role.INDIVIDUAL);
        Optional<IndividualProfile> existingProfile = individualProfileRepository.findByUserId(user.getId());
        boolean isCreate = existingProfile.isEmpty();
        IndividualProfile profile = existingProfile
                .orElse(new IndividualProfile(UUID.randomUUID(), user, "", "", "", null));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());

        IndividualProfile saved = individualProfileRepository.save(profile);
        log.info("Individual profile upserted: userId={}, profileId={}, created={}",
                user.getId(), saved.getId(), isCreate);
        return ResponseEntity.ok(toIndividualResponse(saved));
    }

    @GetMapping("/company/me")
    public ResponseEntity<CompanyProfileResponse> getCompanyProfile() {
        log.info("Company profile fetch request received");
        User user = requireRole(Role.COMPANY);
        CompanyProfile profile = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        log.info("Company profile fetched: userId={}, profileId={}", user.getId(), profile.getId());
        return ResponseEntity.ok(toCompanyResponse(profile));
    }

    @PutMapping("/company")
    public ResponseEntity<CompanyProfileResponse> upsertCompanyProfile(@RequestBody CompanyProfileRequest request) {
        log.info("Company profile upsert request received");
        User user = requireRole(Role.COMPANY);
        Optional<CompanyProfile> existingProfile = companyProfileRepository.findByUserId(user.getId());
        boolean isCreate = existingProfile.isEmpty();
        CompanyProfile profile = existingProfile
                .orElse(new CompanyProfile(UUID.randomUUID(), user, "", "", "", "", "", "", null));

        profile.setCompanyName(request.getCompanyName());
        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setContactPerson(request.getContactPerson());
        profile.setContactEmail(request.getContactEmail());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setWebsite(request.getWebsite());

        CompanyProfile saved = companyProfileRepository.save(profile);
        log.info("Company profile upserted: userId={}, profileId={}, created={}",
                user.getId(), saved.getId(), isCreate);
        return ResponseEntity.ok(toCompanyResponse(saved));
    }

    private User requireRole(Role role) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != role) {
            log.warn("Role check failed: userId={}, username={}, actualRole={}, requiredRole={}",
                    user.getId(), user.getUsername(), user.getRole(), role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
        log.debug("Role check passed: userId={}, role={}", user.getId(), role);
        return user;
    }

    private IndividualProfileResponse toIndividualResponse(IndividualProfile profile) {
        return new IndividualProfileResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getLocation(),
                profile.getBio()
        );
    }

    private CompanyProfileResponse toCompanyResponse(CompanyProfile profile) {
        return new CompanyProfileResponse(
                profile.getId(),
                profile.getCompanyName(),
                profile.getRegistrationNumber(),
                profile.getContactPerson(),
                profile.getContactEmail(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getWebsite()
        );
    }
}
