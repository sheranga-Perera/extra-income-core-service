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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        IndividualProfile profile = existingProfile.orElseGet(() -> {
            IndividualProfile created = new IndividualProfile();
            created.setId(UUID.randomUUID());
            created.setUser(user);
            created.setFullName("");
            created.setPhone("");
            created.setLocation("");
            return created;
        });

        if (request.getProfilePicture() != null) {
            profile.setProfilePicture(request.getProfilePicture());
        }
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getDob() != null && !request.getDob().isBlank()) {
            profile.setDob(parseDob(request.getDob()));
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getNicFront() != null) {
            profile.setNicFront(request.getNicFront());
        }
        if (request.getNicBack() != null) {
            profile.setNicBack(request.getNicBack());
        }
        if (request.getHasDriversLicense() != null) {
            profile.setHasDriversLicense(request.getHasDriversLicense());
        }
        if (request.getDriversLicenseType() != null) {
            profile.setDriversLicenseType(request.getDriversLicenseType());
        }
        if (request.getProfession() != null) {
            profile.setProfession(request.getProfession());
        }
        if (request.getPreferredCategories() != null) {
            profile.setPreferredCategories(request.getPreferredCategories());
        }
        if (request.getPreferredSectors() != null) {
            profile.setPreferredSectors(request.getPreferredSectors());
        }
        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills());
        }

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
        CompanyProfile profile = existingProfile.orElseGet(() -> {
            CompanyProfile created = new CompanyProfile();
            created.setId(UUID.randomUUID());
            created.setUser(user);
            created.setCompanyName("");
            created.setRegistrationNumber("");
            created.setContactPerson("");
            created.setContactEmail("");
            created.setPhone("");
            created.setAddress("");
            return created;
        });

        if (request.getProfilePicture() != null) {
            profile.setProfilePicture(request.getProfilePicture());
        }
        if (request.getCompanyName() != null) {
            profile.setCompanyName(request.getCompanyName());
        }
        if (request.getRegistrationNumber() != null) {
            profile.setRegistrationNumber(request.getRegistrationNumber());
        }
        if (request.getContactPerson() != null) {
            profile.setContactPerson(request.getContactPerson());
        }
        if (request.getContactEmail() != null) {
            profile.setContactEmail(request.getContactEmail());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getSector() != null) {
            profile.setSector(request.getSector());
        }
        if (request.getLegalDocs() != null) {
            profile.setLegalDocs(request.getLegalDocs());
        }

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
                profile.getProfilePicture(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getLocation(),
                profile.getBio(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDob() == null ? null : profile.getDob().toString(),
                profile.getGender(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getNicFront(),
                profile.getNicBack(),
                profile.isHasDriversLicense(),
                profile.getDriversLicenseType(),
                profile.getProfession(),
                profile.getPreferredCategories(),
                profile.getPreferredSectors(),
                profile.getSkills()
        );
    }

    private CompanyProfileResponse toCompanyResponse(CompanyProfile profile) {
        return new CompanyProfileResponse(
                profile.getId(),
                profile.getProfilePicture(),
                profile.getCompanyName(),
                profile.getRegistrationNumber(),
                profile.getContactPerson(),
                profile.getContactEmail(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getWebsite(),
                profile.getBio(),
                profile.getSector(),
                profile.getLegalDocs()
        );
    }

    private LocalDate parseDob(String dob) {
        try {
            return LocalDate.parse(dob);
        } catch (DateTimeParseException ex) {
            log.warn("Profile update rejected due to invalid date of birth: dob={}", dob);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date of birth");
        }
    }
}
