package me.about.widget.routing.sqlparse;

import me.about.widget.routing.RoutingContext;
import me.about.widget.routing.sqlparse.model.SqlParseResult;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Calcite 解析sql
 *
 * @author: hugo.zxh
 * @date: 2023/10/26 10:59
 */
public class CalciteSqlParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalciteSqlParse.class);

    private static void parseFromNode(SqlNode sqlNode, SqlParseResult result, String operate) {
        if (sqlNode == null) {
            return;
        }
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                result.addTableOperator(sqlIdentifier.getSimple(),operate);
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
                SqlNode selectNode = sqlBasicCall.getOperandList().get(0);
                parseSqlNode(selectNode, result,operate);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) sqlNode;
                SqlNode left = sqlJoin.getLeft();
                parseFromNode(left, result,"join left table ");
                SqlNode right = sqlJoin.getRight();
                parseFromNode(right, result,"join right table");
                break;
            case SELECT:
                parseSqlNode(sqlNode, result,operate);
                break;
            default:
                break;
        }
    }

    private static void parseConditionNode(SqlNode sqlNode, SqlParseResult result) {
        if (sqlNode == null) {
            return;
        }
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
                LOGGER.info("[sql condition] field: " + field + ", value: " + value);
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

    private static void parseSqlNode(SqlNode sqlNode, SqlParseResult result, String operate) {
        if (sqlNode == null) {
            return;
        }
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case IDENTIFIER:
                parseFromNode(sqlNode, result,operate);
                break;
            case SELECT:
                SqlSelect select = (SqlSelect) sqlNode;
                parseFromNode(select.getFrom(), result,"select");
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
                if (insert.getSource() instanceof SqlSelect) {
                    parseSqlNode(insert.getSource(),result,"select");
                } else {
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
                                LOGGER.warn("[insert column] column : {},value : {} not support!",column,index);
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

    private static void handlerOrderBy(SqlNode sqlNode, SqlParseResult result) {
        SqlOrderBy sqlOrderBy = (SqlOrderBy) sqlNode;
        SqlNode query = sqlOrderBy.query;
        parseSqlNode(query, result,"select");
    }

    public static SqlParseResult parse(String sql) {
        SqlParseResult result = new SqlParseResult();
        SqlParser.Config myConfig = SqlParser.config()
                .withLex(Lex.MYSQL_ANSI)
                .withQuoting(Quoting.BACK_TICK)
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
//        String sql = "SELECT (select zd_z from s_sys_zd_tbl where zd_lx='DBL' ) as dbl, (select zd_z from s_sys_zd_tbl where zd_lx='GBL' ) as gbl FROM s_sys_zd_tbl group by dbl,gbl";
//        String sql = "SELECT * FROM STORY a left join (select * from a where dd.medins_no = '1' and g.bb_1 = '2' ) b on a.id=b.id where medins_no='c' and b=1 order by a desc";
//        String sql = "select * from a left join b on a.id = b.id where a.id =1 ";
//        String sql = "insert into a(a,b,c) select * from c where c=1";
        String sql = "insert into a(a,b,c) values('1',2,'3'),(1,2,3)";
//        String sql = "update b set a =1 where c = 1";
//        String sql = "delete from a where id = 2";
//        String sql = "select * from a where medins_no = '123qq' and age=2";
//        String sql = "select * from a where id = 1 or age=2";
//        String sql = "select name, `value` from t_cast_ddl";
//        String sql = "SELECT t.ID,t.TASK_NAME,t.CRON, t.REMARK,t.STATUS,t.JOB_NAME, u.YH_MC AS \"CREATE_ID\", t.MODIFY_ID,t.MODIFY_AT,t.DELE_FLG FROM T_TASK t LEFT JOIN S_SYS_YH_TBL u ON t.MODIFY_ID=u.YH_ID WHERE t.DELE_FLG='0'\n";
//        String sql = "DELETE users, orders FROM users JOIN orders ON users.user_id = orders.user_id WHERE users.username = 'John'";
//        String sql = "DELETE orders FROM orders JOIN users ON orders.user_id = users.user_id WHERE users.username = 'John'";
        SqlParseResult result = parse(sql.replaceAll("\"",""));
        System.out.println(result);

    }

}
