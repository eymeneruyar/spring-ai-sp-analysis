package com.bank.spmodernization.parser.extractor;

import com.bank.spmodernization.domain.model.SqlTableReference;
import com.bank.spmodernization.domain.model.TableAccess;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TableAccessExtractor {

    public List<TableAccess> extractTableAccesses(List<SqlTableReference> references) {
        List<TableAccess> accesses = new ArrayList<>();

        if (references == null || references.isEmpty()) {
            return accesses;
        }

        for (SqlTableReference reference : references) {
            if (reference == null || reference.getTableName() == null || reference.getTableName().isBlank()) {
                continue;
            }

            accesses.add(TableAccess.builder()
                    .tableName(reference.getTableName())
                    .accessType(reference.getAccessType())
                    .sourceType(reference.getSourceStatementType())
                    .build());
        }

        return accesses.stream().distinct().toList();
    }
}
