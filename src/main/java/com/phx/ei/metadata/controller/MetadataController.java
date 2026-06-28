package com.phx.ei.metadata.controller;

import com.phx.ei.metadata.repository.CompanySectorRepository;
import com.phx.ei.metadata.repository.JobContractTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final CompanySectorRepository companySectorRepository;
    private final JobContractTypeRepository jobContractTypeRepository;

    @GetMapping("/company-sectors")
    public List<String> getCompanySectors() {
        return companySectorRepository.findByDeletedOrderByDisplayOrderAscNameAsc(0).stream()
                .map(sector -> sector.getName())
                .toList();
    }

    @GetMapping("/job-contract-types")
    public List<String> getJobContractTypes() {
        return jobContractTypeRepository.findByDeletedOrderByDisplayOrderAscNameAsc(0).stream()
                .map(contractType -> contractType.getName())
                .toList();
    }
}
