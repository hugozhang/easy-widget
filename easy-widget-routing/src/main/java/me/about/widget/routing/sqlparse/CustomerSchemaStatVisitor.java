package me.about.widget.routing.sqlparse;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/31 12:48
 */
public class CustomerSchemaStatVisitor extends MySqlSchemaStatVisitor {

    private final List<List<Object>> values = Lists.newArrayList();

    public List<List<Object>> getValues() {
        return values;
    }

    @Override
    public boolean visit(SQLInsertStatement.ValuesClause x) {
        List<Object> row = Lists.newArrayList();
        for (SQLExpr sqlExpr : x.getValues()) {
            if (sqlExpr instanceof SQLValuableExpr) {
                SQLValuableExpr valuableExpr = (SQLValuableExpr) sqlExpr;
                row.add(valuableExpr.getValue());
            }
        }
        values.add(row);
        return true;
    }

}
