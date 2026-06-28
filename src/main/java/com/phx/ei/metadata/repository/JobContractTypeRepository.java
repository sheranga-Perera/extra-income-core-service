package com.phx.ei.metadata.repository;

import com.phx.ei.metadata.entity.JobContractType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobContractTypeRepository extends JpaRepository<JobContractType, Long> {
    List<JobContractType> findByDeletedOrderByDisplayOrderAscNameAsc(Integer deleted);
}
