package com.bank.spmodernization.application.service;

import com.bank.spmodernization.common.exception.InvalidProcedureFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class ProcedureValidationService {

    public void validateProcedureContent(String content) {

        if (content == null || content.isBlank()) {
            throw new InvalidProcedureFileException("Dosya içeriği boş olamaz");
        }

        String lower = content.toLowerCase(Locale.ROOT);

        boolean looksLikePlSql =
                lower.contains("procedure")
                        || lower.contains("function")
                        || lower.contains("package")
                        || lower.contains("begin");

        if (!looksLikePlSql) {
            throw new InvalidProcedureFileException("Dosya içeriği procedure/function/package yapısı içermiyor");
        }
    }

    public List<String> buildWarnings(String content) {
        List<String> warnings = new ArrayList<>();
        String lower = content.toLowerCase(Locale.ROOT);

        if (content.length() > 50000) {
            warnings.add("Dosya oldukça büyük, analiz parçalara bölünerek yapılmalı");
        }

        if (lower.contains("execute immediate")) {
            warnings.add("Dynamic SQL tespit edildi");
        }

        if (lower.contains("commit")) {
            warnings.add("Commit ifadesi tespit edildi");
        }

        if (lower.contains("rollback")) {
            warnings.add("Rollback ifadesi tespit edildi");
        }

        if (lower.contains("exception")) {
            warnings.add("Exception bloğu tespit edildi");
        }

        return warnings;
    }

}
