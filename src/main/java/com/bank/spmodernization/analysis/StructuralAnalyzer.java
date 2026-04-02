package com.bank.spmodernization.analysis;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StructuralAnalyzer {

    public List<String> extractTables(String spText) {
        List<String> tables = new ArrayList<>();
        String lower = spText.toLowerCase();

        if (lower.contains("from")) {
            String[] parts = lower.split("from");

            for (int i = 1; i < parts.length; i++) {
                String table = parts[i].trim().split("[\\s;]+")[0];
                if (!table.isBlank()) {
                    tables.add(table);
                }
            }
        }

        return tables.stream().distinct().toList();
    }

    public List<String> extractOperations(String spText) {
        List<String> ops = new ArrayList<>();
        String lower = spText.toLowerCase();

        if (lower.contains("select")) {
            ops.add("SELECT");
        }
        if (lower.contains("insert")) {
            ops.add("INSERT");
        }
        if (lower.contains("update")) {
            ops.add("UPDATE");
        }
        if (lower.contains("delete")) {
            ops.add("DELETE");
        }

        return ops;
    }

}
