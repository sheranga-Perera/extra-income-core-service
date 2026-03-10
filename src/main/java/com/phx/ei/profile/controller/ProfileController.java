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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final IndividualProfileRepository individualProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @GetMapping("/individual/me")
    public ResponseEntity<IndividualProfileResponse> getIndividualProfile() {
        User user = requireRole(Role.INDIVIDUAL);
        IndividualProfile profile = individualProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return ResponseEntity.ok(toIndividualResponse(profile));
    }

    @PutMapping("/individual")
    public ResponseEntity<IndividualProfileResponse> upsertIndividualProfile(@RequestBody IndividualProfileRequest request) {
        User user = requireRole(Role.INDIVIDUAL);
        IndividualProfile profile = individualProfileRepository.findByUserId(user.getId())
                .orElse(new IndividualProfile(UUID.randomUUID(), user, "", "", "", null));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());

        IndividualProfile saved = individualProfileRepository.save(profile);
        return ResponseEntity.ok(toIndividualResponse(saved));
    }

    @GetMapping("/company/me")
    public ResponseEntity<CompanyProfileResponse> getCompanyProfile() {
        User user = requireRole(Role.COMPANY);
        CompanyProfile profile = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return ResponseEntity.ok(toCompanyResponse(profile));
    }

    @PutMapping("/company")
    public ResponseEntity<CompanyProfileResponse> upsertCompanyProfile(@RequestBody CompanyProfileRequest request) {
        User user = requireRole(Role.COMPANY);
        CompanyProfile profile = companyProfileRepository.findByUserId(user.getId())
                .orElse(new CompanyProfile(UUID.randomUUID(), user, "", "", "", "", "", "", null));

        profile.setCompanyName(request.getCompanyName());
        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setContactPerson(request.getContactPerson());
        profile.setContactEmail(request.getContactEmail());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setWebsite(request.getWebsite());

        CompanyProfile saved = companyProfileRepository.save(profile);
        return ResponseEntity.ok(toCompanyResponse(saved));
    }

    private User requireRole(Role role) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
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
