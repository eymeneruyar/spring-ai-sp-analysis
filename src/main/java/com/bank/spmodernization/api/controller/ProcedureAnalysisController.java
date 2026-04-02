package com.bank.spmodernization.api.controller;

import com.bank.spmodernization.application.usecase.AnalyzeProcedureUseCase;
import com.bank.spmodernization.domain.model.AnalysisReport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/procedures")
@RequiredArgsConstructor
public class ProcedureAnalysisController {

    private final AnalyzeProcedureUseCase analyzeProcedureUseCase;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisReport analyze(@RequestPart("file") MultipartFile file) {
        return analyzeProcedureUseCase.analyze(file);
    }

}
