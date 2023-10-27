package me.about.widget.routing.sqlparse;

import me.about.widget.routing.RoutingContext;
import me.about.widget.routing.sqlparse.model.CalciteSqlParseResult;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Calcite 解析sql
 *
 * @author: hugo.zxh
 * @date: 2023/10/26 10:59
 */
public class CalciteSqlParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalciteSqlParse.class);

    private static void parseFromNode(SqlNode from, CalciteSqlParseResult result,String operate){
        SqlKind kind = from.getKind();
        switch (kind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier) from;
                result.addTableOperator(sqlIdentifier.getSimple(),operate);
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) from;
                SqlNode selectNode = sqlBasicCall.getOperandList().get(0);
                parseSqlNode(selectNode, result,operate);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) from;
                SqlNode left = sqlJoin.getLeft();
                parseFromNode(left, result,"join");
                SqlNode right = sqlJoin.getRight();
                parseFromNode(right, result,"join");
                break;
            case SELECT:
                parseSqlNode(from, result,operate);
                break;
            default:
                break;
        }
    }

    private static void parseConditionNode(SqlNode sqlNode,CalciteSqlParseResult result) {
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case AND:
            case OR:
                ((SqlBasicCall) sqlNode).getOperandList().forEach(node -> {
                    parseConditionNode(node,result);
                });
                break;
            case EQUALS:
                SqlBasicCall basicCall = (SqlBasicCall) sqlNode;
                SqlIdentifier identifier = (SqlIdentifier) basicCall.getOperandList().get(0);
                SqlLiteral literal = (SqlLiteral) basicCall.getOperandList().get(1);
                // a.b 取 b
                String field = identifier.names.get(identifier.names.size() - 1);
                String value = literal.toValue();
                LOGGER.info("[sql where parse] field: " + field + ", value: " + value);
                System.out.println("[sql where parse] field: " + field + ", value: " + value);
                if (RoutingContext.getShardingColumns().contains(field)) {
                    result.addConditionField(field, value);
                }
                break;
            case NOT_EQUALS:
                System.out.println(sqlNode);
                break;
            default:
                break;
        }
    }

    private static void parseSqlNode(SqlNode sqlNode, CalciteSqlParseResult result,String operate) {
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case IDENTIFIER:
                parseFromNode(sqlNode, result,operate);
                break;
            case SELECT:
                SqlSelect select = (SqlSelect) sqlNode;
                parseFromNode(select.getFrom(), result,"from");
                parseConditionNode(select.getWhere(),result);
                break;
            case INSERT:
                SqlInsert insert = (SqlInsert) sqlNode;
                // 解析Table
                parseSqlNode(insert.getTargetTable(),result,"insert");
                // 找到分库字段
                Map<String,Integer> shardingColumn = new HashMap<>(1);
                AtomicInteger shardingColumnIndex = new AtomicInteger();
                // 解析 values
                insert.getTargetColumnList().forEach(columnSqlNode -> {
                    SqlIdentifier sqlIdentifier = (SqlIdentifier)columnSqlNode;
                    String column = sqlIdentifier.getSimple();
                    if(RoutingContext.getShardingColumns().contains(column)) {
                        shardingColumn.put(column,shardingColumnIndex.get());
                    }
                    shardingColumnIndex.getAndIncrement();
                });
                if (!shardingColumn.isEmpty()) {
                    ((SqlBasicCall) insert.getSource()).getOperandList().forEach(innerSqlNode -> {
                        SqlBasicCall sqlBasicCall = (SqlBasicCall) innerSqlNode;
                        Iterator<Map.Entry<String, Integer>> it = shardingColumn.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Integer> next = it.next();
                            String column = next.getKey();
                            Integer index = next.getValue();
                            SqlNode valueNode = sqlBasicCall.getOperandList().get(index);
                            if (valueNode instanceof SqlCharStringLiteral) {
                                SqlCharStringLiteral stringLiteral = (SqlCharStringLiteral) valueNode;
                                result.addConditionField(column,stringLiteral.toValue());
                            } else {
                                LOGGER.warn("column : {},value : {} not support!",column,index);
                            }
                        }
                    });
                }
                break;
            case DELETE:
                SqlDelete delete = (SqlDelete) sqlNode;
                parseSqlNode(delete.getTargetTable(),result,"delete");
                parseConditionNode(delete.getCondition(),result);
                break;
            case UPDATE:
                SqlUpdate update = (SqlUpdate) sqlNode;
                parseSqlNode(update.getTargetTable(),result,"update");
                parseConditionNode(update.getCondition(),result);
                break;
            case UNION:
                ((SqlBasicCall) sqlNode).getOperandList().forEach(node -> {
                    parseSqlNode(node, result,"union");
                });
                break;
            case ORDER_BY:
                handlerOrderBy(sqlNode, result);
                break;
            default:
                break;
        }
    }

    private static void handlerOrderBy(SqlNode node, CalciteSqlParseResult result) {
        SqlOrderBy sqlOrderBy = (SqlOrderBy) node;
        SqlNode query = sqlOrderBy.query;
        parseSqlNode(query, result,"");
    }

    public static CalciteSqlParseResult parse(String sql) {
        CalciteSqlParseResult result = new CalciteSqlParseResult();
        SqlParser.Config myConfig = SqlParser.config()
                .withQuotedCasing(Casing.TO_LOWER)
                .withUnquotedCasing(Casing.TO_LOWER);
        SqlParser parser = SqlParser.create(sql,myConfig);
        try {
            SqlNode sqlNode = parser.parseQuery();
            parseSqlNode(sqlNode, result,"");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return result;
    }

    public static void main(String[] args) {
//        String sql = "SELECT * FROM STORY a left join (select * from a where dd.medins_no = '1' and g.bb_1 = '2' ) b on a.id=b.id where medins_no='c' and b=1 order by a desc";
//        String sql = "select * from a left join b on a.id = b.id where a.id =1";
//        String sql = "insert into a select * from c where c=1";
//        String sql = "insert into a(a,b,c) values('1',2,'3')";
//          String sql = "update b set a =1 where c = 1";
//        String sql = "delete from a where id = 2";
        String sql = "select * from a where medins_no = '123qq' and age=2";
//        String sql = "select * from a where id = 1 or age=2";

        CalciteSqlParseResult result = parse(sql);
        System.out.println(result);

    }

}
