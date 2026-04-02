package com.bank.spmodernization.analysis;

import com.bank.spmodernization.domain.model.SqlTableReference;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.schema.Table;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SqlTableAnalyzer {

    public List<SqlTableReference> extractTableReferences(String sql) {
        List<SqlTableReference> references = new ArrayList<>();

        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select select) {
                handleSelect(select, references);
            } else if (statement instanceof Insert insert) {
                handleInsert(insert, references);
            } else if (statement instanceof Update update) {
                handleUpdate(update, references);
            } else if (statement instanceof Delete delete) {
                handleDelete(delete, references);
            }

        } catch (Exception e) {
            // parse edilemeyen statement için sessiz geçiyoruz
        }

        return references;
    }

    private void handleSelect(Select select, List<SqlTableReference> references) {
        PlainSelect plainSelect = select.getPlainSelect();

        if (plainSelect.getFromItem() instanceof Table table) {
            references.add(buildReference(table.getName(), "READ", "SELECT"));
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Table joinTable) {
                    references.add(buildReference(joinTable.getName(), "READ", "SELECT"));
                }
            }
        }
    }

    private void handleInsert(Insert insert, List<SqlTableReference> references) {
        if (insert.getTable() != null) {
            references.add(buildReference(insert.getTable().getName(), "WRITE", "INSERT"));
        }
    }

    private void handleUpdate(Update update, List<SqlTableReference> references) {
        if (update.getTable() != null) {
            references.add(buildReference(update.getTable().getName(), "WRITE", "UPDATE"));
        }
    }

    private void handleDelete(Delete delete, List<SqlTableReference> references) {
        if (delete.getTable() != null) {
            references.add(buildReference(delete.getTable().getName(), "WRITE", "DELETE"));
        }
    }

    private SqlTableReference buildReference(String tableName, String accessType, String statementType) {
        return SqlTableReference.builder()
                .tableName(tableName)
                .accessType(accessType)
                .sourceStatementType(statementType)
                .build();
    }
}
