package com.phx.ei.core.service.impl;

import com.phx.ei.common.dto.request.LoginRequest;
import com.phx.ei.common.dto.request.RegisterRequest;
import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.JwtUtils;
import com.phx.ei.common.security.Role;
import com.phx.ei.core.repository.UserRepository;
import com.phx.ei.core.service.AuthService;
import com.phx.ei.profile.entity.CompanyProfile;
import com.phx.ei.profile.entity.IndividualProfile;
import com.phx.ei.profile.repository.CompanyProfileRepository;
import com.phx.ei.profile.repository.IndividualProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final IndividualProfileRepository individualProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;

    /**
     * 
     */
    @Override
    @Transactional
    public String register(RegisterRequest request){
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()
                || request.getIdentifierType() == null || request.getRole() == null) {
            log.warn("Registration rejected due to missing fields: username={}, identifierType={}, role={}",
                    request.getUsername(), request.getIdentifierType(), request.getRole());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing registration fields");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Registration rejected due to password mismatch: username={}", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (request.getRole() == Role.INDIVIDUAL) {
            validateIndividualRegistration(request);
        } else if (request.getRole() == Role.COMPANY) {
            validateCompanyRegistration(request);
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration rejected due to existing username: username={}", request.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email or phone already exists.");
        }

        User user = new User(UUID.randomUUID(), request.getUsername(),
                passwordEncoder.encode(request.getPassword()), request.getIdentifierType(), request.getRole());
        userRepository.save(user);

        if (request.getRole() == Role.INDIVIDUAL) {
            IndividualProfile profile = new IndividualProfile();
            profile.setId(UUID.randomUUID());
            profile.setUser(user);
            profile.setFirstName(request.getFirstName().trim());
            profile.setLastName(request.getLastName().trim());
            profile.setDob(parseDob(request.getDob()));
            profile.setGender(request.getGender());
            profile.setEmail(request.getEmail());
            profile.setPhone(request.getPhone());
            profile.setAddress(request.getAddress());
            profile.setNicFront(request.getNicFront());
            profile.setNicBack(request.getNicBack());
            profile.setHasDriversLicense(Boolean.TRUE.equals(request.getHasDriversLicense()));
            profile.setDriversLicenseType(request.getDriversLicenseType());
            profile.setProfession(request.getProfession());
            profile.setPreferredCategories(request.getPreferredCategories());
            profile.setPreferredSectors(request.getPreferredSectors());
            profile.setSkills(request.getSkills());
            profile.setFullName(request.getFirstName().trim() + " " + request.getLastName().trim());
            profile.setLocation(isBlank(request.getAddress()) ? "" : request.getAddress());
            profile.setBio("");
            individualProfileRepository.save(profile);
        } else if (request.getRole() == Role.COMPANY) {
            CompanyProfile profile = new CompanyProfile();
            profile.setId(UUID.randomUUID());
            profile.setUser(user);
            profile.setCompanyName(request.getCompanyName());
            profile.setRegistrationNumber("");
            profile.setContactPerson(request.getContactPerson());
            profile.setContactEmail(request.getContactEmail());
            profile.setPhone(request.getContactPhone());
            profile.setAddress(request.getAddress());
            profile.setWebsite("");
            profile.setBio(request.getBio());
            profile.setSector(request.getSector());
            profile.setLegalDocs(request.getLegalDocs() == null ? List.of() : request.getLegalDocs());
            companyProfileRepository.save(profile);
        }

        log.info("Registration completed: userId={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());
        return jwtUtils.generateToken(user.getUsername());
    }

    /**
     * 
     */
    @Override
    public String login(LoginRequest request){
       User user = userRepository.findByUsername(request.getUsername())
               .orElseThrow(() -> {
                   log.warn("Login failed: username not found: username={}", request.getUsername());
                   return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
               });

       if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
           log.warn("Login failed: password mismatch: username={}", request.getUsername());
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
       }

        log.info("Login completed: userId={}, username={}", user.getId(), user.getUsername());
        return jwtUtils.generateToken(user.getUsername());
    }

    private void validateIndividualRegistration(RegisterRequest request) {
        if (isBlank(request.getFirstName())
                || isBlank(request.getLastName())
                || isBlank(request.getDob())
                || isBlank(request.getGender())
                || isBlank(request.getEmail())
                || isBlank(request.getPhone())
                || isBlank(request.getNicFront())
                || isBlank(request.getNicBack())
                || isBlank(request.getProfession())
                || isBlank(request.getPreferredCategories())
                || isBlank(request.getSkills())) {
            log.warn("Individual registration rejected due to missing fields: username={}", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing individual registration fields");
        }

        if (Boolean.TRUE.equals(request.getHasDriversLicense()) && isBlank(request.getDriversLicenseType())) {
            log.warn("Individual registration rejected due to missing license type: username={}", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing drivers license type");
        }

        parseDob(request.getDob());
    }

    private void validateCompanyRegistration(RegisterRequest request) {
        if (isBlank(request.getCompanyName())
                || isBlank(request.getContactPerson())
                || isBlank(request.getContactEmail())
                || isBlank(request.getContactPhone())
                || isBlank(request.getAddress())
                || isBlank(request.getSector())
                || request.getLegalDocs() == null
                || request.getLegalDocs().isEmpty()) {
            log.warn("Company registration rejected due to missing fields: username={}", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing company registration fields");
        }
    }

    private LocalDate parseDob(String dob) {
        try {
            LocalDate date = LocalDate.parse(dob);
            if (date.isAfter(LocalDate.now().minusYears(18))) {
                log.warn("Registration rejected due to age requirement: dob={}", dob);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least 18 years old");
            }
            return date;
        } catch (DateTimeParseException ex) {
            log.warn("Registration rejected due to invalid date of birth: dob={}", dob);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date of birth");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
