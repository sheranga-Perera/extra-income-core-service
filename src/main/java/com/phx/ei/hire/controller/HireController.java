package com.phx.ei.hire.controller;

import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.service.CurrentUserService;
import com.phx.ei.hire.dto.IndividualSearchResponse;
import com.phx.ei.profile.entity.IndividualProfile;
import com.phx.ei.profile.repository.IndividualProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hire")
@RequiredArgsConstructor
@Slf4j
public class HireController {

    private final CurrentUserService currentUserService;
    private final IndividualProfileRepository individualProfileRepository;

    @GetMapping("/individuals")
    public ResponseEntity<List<IndividualSearchResponse>> searchIndividuals(
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "profession", required = false) String profession
    ) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.COMPANY && user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can search individuals");
        }

        String locationFilter = normalize(location);
        String professionFilter = normalize(profession);
        List<String> skillTokens = parseTokens(skills);

        String firstSkill = skillTokens.isEmpty() ? null : skillTokens.get(0);
        List<IndividualProfile> profiles = individualProfileRepository.searchIndividuals(
                locationFilter,
                professionFilter,
                firstSkill
        );

        List<IndividualSearchResponse> response = profiles.stream()
                .filter(profile -> matchesSkills(profile.getSkills(), skillTokens))
                .map(this::toResponse)
                .toList();

        log.info("Hire search completed: userId={}, results={}", user.getId(), response.size());
        return ResponseEntity.ok(response);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private List<String> parseTokens(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        String[] parts = value.split(",");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            String token = part.trim().toLowerCase();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private boolean matchesSkills(String skills, List<String> tokens) {
        if (tokens.isEmpty()) {
            return true;
        }
        String skillsLower = skills == null ? "" : skills.toLowerCase();
        for (String token : tokens) {
            if (!skillsLower.contains(token)) {
                return false;
            }
        }
        return true;
    }

    private IndividualSearchResponse toResponse(IndividualProfile profile) {
        return new IndividualSearchResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getLocation(),
                profile.getProfession(),
                profile.getSkills(),
                profile.getPreferredCategories(),
                profile.getPreferredSectors(),
                profile.getBio(),
                profile.getPhone(),
                profile.getEmail()
        );
    }
}
