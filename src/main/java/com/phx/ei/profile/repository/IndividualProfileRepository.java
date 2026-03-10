package com.phx.ei.profile.repository;

import com.phx.ei.profile.entity.IndividualProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IndividualProfileRepository extends JpaRepository<IndividualProfile, UUID> {
    Optional<IndividualProfile> findByUserId(UUID userId);
}
