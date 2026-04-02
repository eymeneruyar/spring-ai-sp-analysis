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
public class ProcedureMetadata {
    private String procedureName;
    private List<String> parameters;
    private List<String> tables;
    private List<String> operations;
    private List<SqlTableReference> tableReferences;
    private List<String> cursors;
    private List<String> exceptionHandlers;
    private List<String> transactionStatements;
    private List<String> calledProcedures;
    private List<String> calledFunctions;
    private List<TableAccess> tableAccesses;
}
