package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProcedureCallExtractor {

    private static final Pattern PROCEDURE_CALL_PATTERN = Pattern.compile(
            "\\b(?:([a-zA-Z0-9_]+)\\.)?([a-zA-Z0-9_]+)\\s*\\(",
            Pattern.CASE_INSENSITIVE
    );

    private static final Set<String> EXCLUDED_KEYWORDS = Set.of(
            "select", "insert", "update", "delete", "values",
            "count", "sum", "min", "max", "avg", "substr",
            "nvl", "decode", "to_char", "to_date", "case"
    );

    public List<String> extractProcedureCalls(String content) {
        List<String> procedureCalls = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return procedureCalls;
        }

        Matcher matcher = PROCEDURE_CALL_PATTERN.matcher(content);

        while (matcher.find()) {
            String owner = matcher.group(1);
            String name = matcher.group(2);

            if (name == null || name.isBlank()) {
                continue;
            }

            String lowerName = name.toLowerCase();
            if (EXCLUDED_KEYWORDS.contains(lowerName)) {
                continue;
            }

            String fullName = owner != null && !owner.isBlank()
                    ? owner + "." + name
                    : name;

            procedureCalls.add(fullName.trim());
        }

        return procedureCalls.stream().distinct().toList();
    }
}
