package com.phx.ei.core.service.impl;

import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.dto.request.RegisterRequest;
import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.JwtUtils;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.repository.UserRepository;
import com.phx.ei.profile.entity.CompanyProfile;
import com.phx.ei.profile.entity.IndividualProfile;
import com.phx.ei.profile.repository.CompanyProfileRepository;
import com.phx.ei.profile.repository.IndividualProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IndividualProfileRepository individualProfileRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(any())).thenReturn("encoded");
        lenient().when(jwtUtils.generateToken(any())).thenReturn("token");
    }

    @Test
    void registerIndividual_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ind-user");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");
        request.setIdentifierType(IdentifierType.EMAIL);
        request.setRole(Role.INDIVIDUAL);
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setDob(LocalDate.now().minusYears(20).toString());
        request.setGender("Female");
        request.setEmail("jane@example.com");
        request.setPhone("+94712345678");
        request.setAddress("Colombo");
        request.setNicFront("front");
        request.setNicBack("back");
        request.setHasDriversLicense(false);
        request.setProfession("Designer");
        request.setPreferredCategories("Design");
        request.setPreferredSectors("Tech");
        request.setSkills("Figma, UI/UX");

        when(userRepository.findByUsername("ind-user")).thenReturn(Optional.empty());

        String token = authService.register(request);

        assertEquals("token", token);
        verify(userRepository).save(any(User.class));
        verify(individualProfileRepository).save(any(IndividualProfile.class));
        verify(companyProfileRepository, never()).save(any());

        ArgumentCaptor<IndividualProfile> profileCaptor = ArgumentCaptor.forClass(IndividualProfile.class);
        verify(individualProfileRepository).save(profileCaptor.capture());
        IndividualProfile profile = profileCaptor.getValue();
        assertEquals("Jane", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals(LocalDate.parse(request.getDob()), profile.getDob());
        assertEquals("Female", profile.getGender());
        assertEquals("jane@example.com", profile.getEmail());
        assertEquals("+94712345678", profile.getPhone());
        assertEquals("Colombo", profile.getAddress());
        assertEquals("front", profile.getNicFront());
        assertEquals("back", profile.getNicBack());
        assertFalse(profile.isHasDriversLicense());
        assertEquals("Designer", profile.getProfession());
        assertEquals("Design", profile.getPreferredCategories());
        assertEquals("Tech", profile.getPreferredSectors());
        assertEquals("Figma, UI/UX", profile.getSkills());
    }

    @Test
    void registerCompany_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("comp-user");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");
        request.setIdentifierType(IdentifierType.EMAIL);
        request.setRole(Role.COMPANY);
        request.setCompanyName("Acme Ltd");
        request.setContactPerson("Alex");
        request.setContactEmail("alex@acme.com");
        request.setContactPhone("+94771234567");
        request.setAddress("Kandy");
        request.setBio("We build things");
        request.setSector("Construction");
        request.setLegalDocs(List.of("doc1", "doc2"));

        when(userRepository.findByUsername("comp-user")).thenReturn(Optional.empty());

        String token = authService.register(request);

        assertEquals("token", token);
        verify(userRepository).save(any(User.class));
        verify(companyProfileRepository).save(any(CompanyProfile.class));
        verify(individualProfileRepository, never()).save(any());

        ArgumentCaptor<CompanyProfile> profileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(companyProfileRepository).save(profileCaptor.capture());
        CompanyProfile profile = profileCaptor.getValue();
        assertEquals("Acme Ltd", profile.getCompanyName());
        assertEquals("Alex", profile.getContactPerson());
        assertEquals("alex@acme.com", profile.getContactEmail());
        assertEquals("+94771234567", profile.getPhone());
        assertEquals("Kandy", profile.getAddress());
        assertEquals("We build things", profile.getBio());
        assertEquals("Construction", profile.getSector());
        assertEquals(2, profile.getLegalDocs().size());
    }

    @Test
    void registerIndividual_UnderAgeRejected() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("underage");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");
        request.setIdentifierType(IdentifierType.EMAIL);
        request.setRole(Role.INDIVIDUAL);
        request.setFirstName("Kid");
        request.setLastName("Young");
        request.setDob(LocalDate.now().minusYears(17).toString());
        request.setGender("Male");
        request.setEmail("kid@example.com");
        request.setPhone("+94712345678");
        request.setNicFront("front");
        request.setNicBack("back");
        request.setProfession("Student");
        request.setPreferredCategories("Education");
        request.setSkills("Math");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
