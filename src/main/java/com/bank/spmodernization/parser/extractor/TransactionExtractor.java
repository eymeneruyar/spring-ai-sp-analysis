package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TransactionExtractor {

    private static final Pattern TRANSACTION_PATTERN = Pattern.compile(
            "\\b(commit|rollback|savepoint\\s+[a-zA-Z0-9_]+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public List<String> extractTransactionStatements(String content) {
        List<String> statements = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return statements;
        }

        Matcher matcher = TRANSACTION_PATTERN.matcher(content);

        while (matcher.find()) {
            String statement = matcher.group(1);
            if (statement != null && !statement.isBlank()) {
                statements.add(statement.trim().replaceAll("\\s+", " ").toUpperCase());
            }
        }

        return statements.stream().distinct().toList();
    }

}
