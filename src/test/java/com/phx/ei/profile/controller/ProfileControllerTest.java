package com.phx.ei.profile.controller;

import com.phx.ei.common.constant.IdentifierType;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private IndividualProfileRepository individualProfileRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void getIndividualProfile_ReturnsExtendedFields() {
        User user = new User(UUID.randomUUID(), "ind", "pw", IdentifierType.EMAIL, Role.INDIVIDUAL);
        IndividualProfile profile = new IndividualProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        profile.setFullName("Jane Doe");
        profile.setPhone("+94712345678");
        profile.setLocation("Colombo");
        profile.setBio("Bio");
        profile.setFirstName("Jane");
        profile.setLastName("Doe");
        profile.setDob(LocalDate.of(1990, 1, 1));
        profile.setGender("Female");
        profile.setEmail("jane@example.com");
        profile.setAddress("Colombo");
        profile.setNicFront("front");
        profile.setNicBack("back");
        profile.setHasDriversLicense(true);
        profile.setDriversLicenseType("LIGHT");
        profile.setProfession("Designer");
        profile.setPreferredCategories("Design");
        profile.setPreferredSectors("Tech");
        profile.setSkills("Figma");

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(individualProfileRepository.findByUserId(user.getId())).thenReturn(Optional.of(profile));

        ResponseEntity<IndividualProfileResponse> response = profileController.getIndividualProfile();

        assertEquals("Jane", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("1990-01-01", response.getBody().getDob());
        assertEquals("Female", response.getBody().getGender());
        assertEquals("jane@example.com", response.getBody().getEmail());
        assertEquals("front", response.getBody().getNicFront());
        assertEquals("back", response.getBody().getNicBack());
        assertTrue(response.getBody().getHasDriversLicense());
        assertEquals("LIGHT", response.getBody().getDriversLicenseType());
        assertEquals("Designer", response.getBody().getProfession());
        assertEquals("Design", response.getBody().getPreferredCategories());
        assertEquals("Tech", response.getBody().getPreferredSectors());
        assertEquals("Figma", response.getBody().getSkills());
    }

    @Test
    void getCompanyProfile_ReturnsExtendedFields() {
        User user = new User(UUID.randomUUID(), "comp", "pw", IdentifierType.EMAIL, Role.COMPANY);
        CompanyProfile profile = new CompanyProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        profile.setCompanyName("Acme");
        profile.setRegistrationNumber("REG-1");
        profile.setContactPerson("Alex");
        profile.setContactEmail("alex@acme.com");
        profile.setPhone("+94771234567");
        profile.setAddress("Kandy");
        profile.setWebsite("https://acme.com");
        profile.setBio("Bio");
        profile.setSector("Construction");
        profile.setLegalDocs(List.of("doc1"));

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(companyProfileRepository.findByUserId(user.getId())).thenReturn(Optional.of(profile));

        ResponseEntity<CompanyProfileResponse> response = profileController.getCompanyProfile();

        assertEquals("Acme", response.getBody().getCompanyName());
        assertEquals("Alex", response.getBody().getContactPerson());
        assertEquals("Bio", response.getBody().getBio());
        assertEquals("Construction", response.getBody().getSector());
        assertEquals(1, response.getBody().getLegalDocs().size());
    }

    @Test
    void upsertIndividualProfile_SetsExtendedFields() {
        User user = new User(UUID.randomUUID(), "ind", "pw", IdentifierType.EMAIL, Role.INDIVIDUAL);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(individualProfileRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        IndividualProfileRequest request = new IndividualProfileRequest();
        request.setFullName("Jane Doe");
        request.setPhone("+94712345678");
        request.setLocation("Colombo");
        request.setBio("Bio");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setDob("1990-01-01");
        request.setGender("Female");
        request.setEmail("jane@example.com");
        request.setAddress("Colombo");
        request.setNicFront("front");
        request.setNicBack("back");
        request.setHasDriversLicense(true);
        request.setDriversLicenseType("LIGHT");
        request.setProfession("Designer");
        request.setPreferredCategories("Design");
        request.setPreferredSectors("Tech");
        request.setSkills("Figma");

        when(individualProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        profileController.upsertIndividualProfile(request);

        ArgumentCaptor<IndividualProfile> captor = ArgumentCaptor.forClass(IndividualProfile.class);
        verify(individualProfileRepository).save(captor.capture());
        IndividualProfile saved = captor.getValue();
        assertEquals("Jane", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), saved.getDob());
        assertEquals("Female", saved.getGender());
        assertEquals("jane@example.com", saved.getEmail());
        assertEquals("front", saved.getNicFront());
        assertEquals("back", saved.getNicBack());
        assertTrue(saved.isHasDriversLicense());
        assertEquals("LIGHT", saved.getDriversLicenseType());
        assertEquals("Designer", saved.getProfession());
        assertEquals("Design", saved.getPreferredCategories());
        assertEquals("Tech", saved.getPreferredSectors());
        assertEquals("Figma", saved.getSkills());
    }

    @Test
    void upsertCompanyProfile_SetsExtendedFields() {
        User user = new User(UUID.randomUUID(), "comp", "pw", IdentifierType.EMAIL, Role.COMPANY);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(companyProfileRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        CompanyProfileRequest request = new CompanyProfileRequest();
        request.setCompanyName("Acme");
        request.setRegistrationNumber("REG-1");
        request.setContactPerson("Alex");
        request.setContactEmail("alex@acme.com");
        request.setPhone("+94771234567");
        request.setAddress("Kandy");
        request.setWebsite("https://acme.com");
        request.setBio("Bio");
        request.setSector("Construction");
        request.setLegalDocs(List.of("doc1", "doc2"));

        when(companyProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        profileController.upsertCompanyProfile(request);

        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(companyProfileRepository).save(captor.capture());
        CompanyProfile saved = captor.getValue();
        assertEquals("Acme", saved.getCompanyName());
        assertEquals("REG-1", saved.getRegistrationNumber());
        assertEquals("Alex", saved.getContactPerson());
        assertEquals("alex@acme.com", saved.getContactEmail());
        assertEquals("Kandy", saved.getAddress());
        assertEquals("Bio", saved.getBio());
        assertEquals("Construction", saved.getSector());
        assertEquals(2, saved.getLegalDocs().size());
    }
}
