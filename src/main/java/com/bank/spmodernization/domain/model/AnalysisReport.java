package com.bank.spmodernization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisReport {
    private String fileName;
    private ProcedureMetadata metadata;
    private List<ProcedureSegment> segments;
    private List<String> findings;
    private List<String> warnings;
}
