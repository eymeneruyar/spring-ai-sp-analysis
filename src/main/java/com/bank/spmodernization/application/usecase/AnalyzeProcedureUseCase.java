package com.bank.spmodernization.application.usecase;

import com.bank.spmodernization.analysis.SqlTableAnalyzer;
import com.bank.spmodernization.analysis.StructuralAnalyzer;
import com.bank.spmodernization.application.service.ProcedureFileService;
import com.bank.spmodernization.application.service.ProcedureSegmentationService;
import com.bank.spmodernization.application.service.ProcedureValidationService;
import com.bank.spmodernization.domain.model.AnalysisReport;
import com.bank.spmodernization.domain.model.ProcedureMetadata;
import com.bank.spmodernization.domain.model.ProcedureSegment;
import com.bank.spmodernization.domain.model.SqlTableReference;
import com.bank.spmodernization.domain.model.TableAccess;
import com.bank.spmodernization.parser.extractor.CursorExtractor;
import com.bank.spmodernization.parser.extractor.ExceptionBlockExtractor;
import com.bank.spmodernization.parser.extractor.FunctionCallExtractor;
import com.bank.spmodernization.parser.extractor.ParameterExtractor;
import com.bank.spmodernization.parser.extractor.ProcedureCallExtractor;
import com.bank.spmodernization.parser.extractor.SqlStatementExtractor;
import com.bank.spmodernization.parser.extractor.TableAccessExtractor;
import com.bank.spmodernization.parser.extractor.TransactionExtractor;
import com.bank.spmodernization.parser.metadata.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnalyzeProcedureUseCase {

    private static final String UNKNOWN = "UNKNOWN";

    private final ProcedureFileService procedureFileService;
    private final ProcedureValidationService procedureValidationService;
    private final ProcedureSegmentationService procedureSegmentationService;

    private final ParameterExtractor parameterExtractor;
    private final CursorExtractor cursorExtractor;
    private final ExceptionBlockExtractor exceptionBlockExtractor;
    private final TransactionExtractor transactionExtractor;
    private final ProcedureCallExtractor procedureCallExtractor;
    private final FunctionCallExtractor functionCallExtractor;
    private final SqlStatementExtractor sqlStatementExtractor;
    private final TableAccessExtractor tableAccessExtractor;

    private final StructuralAnalyzer structuralAnalyzer;
    private final SqlTableAnalyzer sqlTableAnalyzer;

    public AnalysisReport analyze(MultipartFile file) {
        FileMetadata fileMetadata = procedureFileService.read(file);
        String content = fileMetadata.getContent();

        procedureValidationService.validateProcedureContent(content);

        List<String> sqlStatements = sqlStatementExtractor.extractSqlStatements(content);
        List<SqlTableReference> tableReferences = extractTableReferences(sqlStatements);
        List<TableAccess> tableAccesses = tableAccessExtractor.extractTableAccesses(tableReferences);
        List<ProcedureSegment> segments = procedureSegmentationService.segment(content);
        List<String> warnings = procedureValidationService.buildWarnings(content);

        ProcedureMetadata metadata = buildProcedureMetadata(content, tableReferences, tableAccesses);

        return AnalysisReport.builder()
                .fileName(fileMetadata.getFileName())
                .metadata(metadata)
                .segments(segments)
                .findings(buildFindings(metadata, segments, sqlStatements, content))
                .warnings(warnings)
                .build();
    }

    private ProcedureMetadata buildProcedureMetadata(String content,
                                                     List<SqlTableReference> tableReferences,
                                                     List<TableAccess> tableAccesses) {

        List<String> tablesFromSqlParser = tableReferences.stream()
                .map(SqlTableReference::getTableName)
                .filter(this::hasText)
                .map(String::trim)
                .distinct()
                .toList();

        List<String> structuralTables = structuralAnalyzer.extractTables(content);
        List<String> cursors = cursorExtractor.extractCursorNames(content);
        List<String> exceptionHandlers = exceptionBlockExtractor.extractExceptionHandlers(content);
        List<String> transactionStatements = transactionExtractor.extractTransactionStatements(content);
        List<String> calledProcedures = procedureCallExtractor.extractProcedureCalls(content);
        List<String> calledFunctions = functionCallExtractor.extractFunctionCalls(content);

        Set<String> mergedTables = new LinkedHashSet<>();
        mergedTables.addAll(structuralTables);
        mergedTables.addAll(tablesFromSqlParser);

        return ProcedureMetadata.builder()
                .procedureName(extractProcedureName(content))
                .parameters(parameterExtractor.extractParameters(content))
                .tables(new ArrayList<>(mergedTables))
                .operations(structuralAnalyzer.extractOperations(content))
                .tableReferences(tableReferences)
                .cursors(cursors)
                .exceptionHandlers(exceptionHandlers)
                .transactionStatements(transactionStatements)
                .calledProcedures(calledProcedures)
                .calledFunctions(calledFunctions)
                .tableAccesses(tableAccesses)
                .build();
    }

    private List<SqlTableReference> extractTableReferences(List<String> sqlStatements) {
        return sqlStatements.stream()
                .flatMap(sql -> sqlTableAnalyzer.extractTableReferences(sql).stream())
                .toList();
    }

    private List<String> buildFindings(ProcedureMetadata metadata,
                                       List<ProcedureSegment> segments,
                                       List<String> sqlStatements,
                                       String content) {

        List<String> findings = new ArrayList<>();
        findings.add("Dosya başarıyla analiz edildi");
        findings.add("Procedure/Function adı: " + metadata.getProcedureName());
        findings.add("Parametre sayısı: " + safeSize(metadata.getParameters()));
        findings.add("Cursor sayısı: " + safeSize(metadata.getCursors()));
        findings.add("Exception handler sayısı: " + safeSize(metadata.getExceptionHandlers()));
        findings.add("Transaction statement sayısı: " + safeSize(metadata.getTransactionStatements()));
        findings.add("Procedure call sayısı: " + safeSize(metadata.getCalledProcedures()));
        findings.add("Function call sayısı: " + safeSize(metadata.getCalledFunctions()));
        findings.add("Segment sayısı: " + safeSize(segments));
        findings.add("SQL statement sayısı: " + safeSize(sqlStatements));
        findings.add("Tespit edilen tablo sayısı: " + safeSize(metadata.getTables()));
        findings.add("Tespit edilen table access sayısı: " + safeSize(metadata.getTableAccesses()));
        findings.add("Tespit edilen operasyonlar: " + metadata.getOperations());

        long readCount = metadata.getTableReferences() == null
                ? 0
                : metadata.getTableReferences().stream()
                .filter(ref -> "READ".equalsIgnoreCase(ref.getAccessType()))
                .count();

        long writeCount = metadata.getTableReferences() == null
                ? 0
                : metadata.getTableReferences().stream()
                .filter(ref -> "WRITE".equalsIgnoreCase(ref.getAccessType()))
                .count();

        findings.add("READ referans sayısı: " + readCount);
        findings.add("WRITE referans sayısı: " + writeCount);

        if (exceptionBlockExtractor.hasExceptionBlock(content)) {
            findings.add("Exception bloğu tespit edildi");
        }

        return findings;
    }

    private int safeSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String extractProcedureName(String content) {
        if (!hasText(content)) {
            return UNKNOWN;
        }

        String normalized = content.replaceAll("\\s+", " ").trim();
        String lower = normalized.toLowerCase(Locale.ROOT);

        String procedureKeyword = "procedure ";
        int procedureIndex = lower.indexOf(procedureKeyword);
        if (procedureIndex >= 0) {
            String afterProcedure = normalized.substring(procedureIndex + procedureKeyword.length()).trim();
            return extractObjectName(afterProcedure);
        }

        String functionKeyword = "function ";
        int functionIndex = lower.indexOf(functionKeyword);
        if (functionIndex >= 0) {
            String afterFunction = normalized.substring(functionIndex + functionKeyword.length()).trim();
            return extractObjectName(afterFunction);
        }

        return UNKNOWN;
    }

    private String extractObjectName(String text) {
        if (!hasText(text)) {
            return UNKNOWN;
        }

        String candidate = text.split("[\\s(]")[0].trim();

        if (candidate.isBlank()) {
            return UNKNOWN;
        }

        if (candidate.contains(".")) {
            String[] parts = candidate.split("\\.");
            return parts[parts.length - 1];
        }

        return candidate;
    }
}
