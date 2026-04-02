package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SqlStatementExtractor {

    public List<String> extractSqlStatements(String content) {
        List<String> statements = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return statements;
        }

        String[] parts = content.split(";");

        for (String part : parts) {
            String statement = part.trim();
            String lower = statement.toLowerCase();

            if (lower.startsWith("select")
                    || lower.startsWith("insert")
                    || lower.startsWith("update")
                    || lower.startsWith("delete")) {
                statements.add(statement);
            }
        }

        return statements;
    }
}
