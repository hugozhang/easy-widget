package me.about.widget.routing.sqlparse.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * SqlParseResult  解析结果
 *
 * @author: hugo.zxh
 * @date: 2023/10/26 10:39
 */
@Data
public class SqlParseResult {

    private Set<TableOperator> tableOperators = new HashSet<>();

    private Set<ConditionField> conditionFields = new HashSet<>();

    public void addTableOperator(String tableName,String operate) {
        tableOperators.add(new TableOperator(tableName,operate));
    }

    public void addConditionField(String field,String value) {
        conditionFields.add(new ConditionField(field,value));
    }

    public Set<TableOperator> getTableOperators() {
        return tableOperators;
    }

    public Set<ConditionField> getConditionFields() {
        return conditionFields;
    }
}
