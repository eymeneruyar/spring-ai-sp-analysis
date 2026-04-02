package com.bank.spmodernization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableAccess {
    private String tableName;
    private String accessType; // READ / WRITE
    private String sourceType; // SELECT / INSERT / UPDATE / DELETE
}
