package com.bank.spmodernization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlTableReference {
    private String tableName;
    private String accessType; // READ / WRITE
    private String sourceStatementType; // SELECT / INSERT / UPDATE / DELETE
}
