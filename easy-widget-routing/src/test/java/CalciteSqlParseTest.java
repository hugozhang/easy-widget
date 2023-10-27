import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.util.SqlShuttle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Calcite SQL 解析
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 13:48
 */
public class CalciteSqlParseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalciteSqlParseTest.class);

    private static void parseWhereNode(SqlNode where) {
        SqlKind kind = where.getKind();
        switch (kind) {
            case AND:
            case OR:
                System.out.println(where);
                break;
            default:
                break;
        }
    }

    private static void parseFromNode(SqlNode from, List<String> tableNameList){
        SqlKind kind = from.getKind();
        switch (kind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier) from;
                tableNameList.add(sqlIdentifier.toString());
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) from;
                SqlNode selectNode = sqlBasicCall.getOperandList().get(0);
                parseSqlNode(selectNode, tableNameList);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) from;
                SqlNode left = sqlJoin.getLeft();
                parseFromNode(left, tableNameList);
                SqlNode right = sqlJoin.getRight();
                parseFromNode(right, tableNameList);
                break;
            case SELECT:
                parseSqlNode(from, tableNameList);
                break;
            default:
                break;
        }
    }

    private static void parseSqlNode(SqlNode sqlNode, List<String> tableNameList) {
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case IDENTIFIER:
                parseFromNode(sqlNode, tableNameList);
                break;
            case SELECT:
                SqlSelect select = (SqlSelect) sqlNode;
                parseFromNode(select.getFrom(), tableNameList);
                parseWhereNode(select.getWhere());
                break;
            case UNION:
                ((SqlBasicCall) sqlNode).getOperandList().forEach(node -> {
                    parseSqlNode(node, tableNameList);
                });
                break;
            case ORDER_BY:
                handlerOrderBy(sqlNode, tableNameList);
                break;
            default:
                break;
        }
    }

    private static void handlerOrderBy(SqlNode node, List<String> tableNameList) {
        SqlOrderBy sqlOrderBy = (SqlOrderBy) node;
        SqlNode query = sqlOrderBy.query;
        parseSqlNode(query, tableNameList);
    }



    public static Map<String, List<String>> parse(String sql) {
        Map<String, List<String>> shardingKeyMap = Maps.newHashMap();
        SqlParser.Config myConfig = SqlParser.config();
        SqlParser parser = SqlParser.create(sql,myConfig);
        try {
            SqlNode sqlNode = parser.parseQuery();

            List<String> tableNameList = new ArrayList<>();
            parseSqlNode(sqlNode, tableNameList);

            System.out.println(tableNameList);



            sqlNode.accept(new SqlShuttle() {
                @Override
                public SqlNode visit(SqlCall call) {
//                    parseSqlNode(call, tableNameList);

                    if (call.getKind() == SqlKind.SELECT) {
                        SqlSelect select = (SqlSelect)call;
                        SqlBasicCall where = (SqlBasicCall)select.getWhere();
                        if (where != null) {
                            SqlKind kind = where.getKind();
                            switch (kind) {
                                case AND:
                                case OR:
                                    for (SqlNode operand : where.getOperandList()) {
                                        SqlIdentifier identifier = (SqlIdentifier) ((SqlBasicCall) operand).getOperands()[0];
                                        SqlLiteral literal = (SqlLiteral) ((SqlBasicCall) operand).getOperands()[1];
                                        collectWhere(identifier, literal);
                                    }
                                    break;
                                case EQUALS:
                                case NOT_EQUALS:
                                    SqlIdentifier identifier = (SqlIdentifier) where.getOperandList().get(0);
                                    SqlLiteral literal = (SqlLiteral) where.getOperandList().get(1);
                                    collectWhere(identifier,literal);
                                    break;
                                default:
                                    LOGGER.info("where {},kind : {}",select.getWhere(),kind);
                                    break;
                            }
                        }
                    }
                    return super.visit(call);
                }


                private void collectWhere(SqlIdentifier identifier, SqlLiteral literal) {
                    String key;
                    int len = identifier.names.size();
                    if (len == 1) {
                        key = identifier.getSimple();
                    } else if(len > 1) {
                        key = identifier.names.get(len - 1);
                    } else {
                        return;
                    }
                    String value = literal.getValue().toString();
                    if(shardingKeyMap.containsKey(key)) {
                        List<String> values = shardingKeyMap.get(key);
                        values.add(value);
                    } else {
                        shardingKeyMap.put(key, Lists.newArrayList(value));
                    }
                    LOGGER.info("[sql where parse] Field: " + key + ", Value: " + value);
                }
            });

        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return shardingKeyMap;
    }


    public static void main(String[] args) {

        String sql1 = "update ";
        String sql = "SELECT * FROM STORY a left join (select * from a where dd.aa_1 = '1' and g.bb_1 = '2' ) b on a.id=b.id where a='c' and b=1 order by a desc";
//        String sql = "SELECT\n" +
//                "          a.id,\n" +
//                "          a.`name`,\n" +
//                "          a.age\n" +
//                "        FROM\n" +
//                "          `user` a\n" +
//                "          LEFT JOIN order_info o\n" +
//                "          ON a.id = o.user_id\n" +
//                "        WHERE a.id = 1\n" +
//                "          AND a.name = 'xxx'\n" +
//                "          AND a.age = (SELECT age FROM age_table age WHERE age.user_id = 1)\n" +
//                "        GROUP BY a.id,\n" +
//                "          a.score\n" +
//                "        HAVING MAX(a.id) > 1\n" +
//                "        ORDER BY a.id DESC,\n" +
//                "          a.score ASC\n" +
//                "        LIMIT 3, 10;";
        Map<String, List<String>> parse = parse(sql);
        System.out.println(parse);


    }
}
