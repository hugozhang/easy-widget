import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import me.about.widget.routing.RoutingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 13:48
 */
public class DruidSqlParseTest {

    public static void main(String[] args) {

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

//        String sql = "SELECT (select zd_z from s_sys_zd_tbl where zd_lx='DBL' ) as dbl, (select zd_z from s_sys_zd_tbl where zd_lx='GBL' ) as gbl FROM s_sys_zd_tbl group by dbl,gbl";
//        String sql = "SELECT * FROM STORY a left join (select * from a where dd.medins_no = '1' and g.bb_1 = '2' ) b on a.id=b.id where medins_no='c' and b=1 order by a desc";
//        String sql = "select * from a left join b on a.id = b.id where a.id =1 ";
//        String sql = "insert into a(a,b,c) select * from c where c=1";
        String sql = "insert into a(a,b,c) values('1',2,'3')";
//        String sql = "update b set a =1 where c = 1";
//        String sql = "delete from a where id = 2";
//        String sql = "select * from a where medins_no = '123qq' and age=2";
//        String sql = "select * from a where id = 1 or age=2";
//        String sql = "select name, `value` from t_cast_ddl";
//        String sql = "SELECT t.ID,t.TASK_NAME,t.CRON, t.REMARK,t.STATUS,t.JOB_NAME, u.YH_MC AS \"CREATE_ID\", t.MODIFY_ID,t.MODIFY_AT,t.DELE_FLG FROM T_TASK t LEFT JOIN S_SYS_YH_TBL u ON t.MODIFY_ID=u.YH_ID WHERE t.DELE_FLG='0'\n";
//        String sql = "DELETE users, orders FROM users JOIN orders ON users.user_id = orders.user_id WHERE users.username = 'John'";
//        String sql = "DELETE orders FROM orders JOIN users ON orders.user_id = users.user_id WHERE users.username = 'John'";
//        String sql = "DELETE A  FROM TableA AS A LEFT OUTER JOIN TableB As B ON A.Id = B.TabaleAId WHERE B.Column IS NULL";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        System.out.println(statementList);
        for (SQLStatement sqlStatement : statementList) {
            if (sqlStatement instanceof SQLInsertStatement) {
                SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) sqlStatement;
                if (!sqlInsertStatement.getValuesList().isEmpty()) {
                    int i = 0;
                    Pair<Integer,String> pair = null;
                    for (SQLExpr sqlExpr : sqlInsertStatement.getColumns()) {
                        if (sqlExpr instanceof SQLIdentifierExpr) {
                            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
                            String name = sqlIdentifierExpr.getName();
                            if (RoutingContext.getShardingColumns().contains(name)) {
                                pair = Pair.of(i,sqlIdentifierExpr.getLowerName());
                            }
                        }
                        i++;
                    }

                    if (pair != null) {
                        for (SQLInsertStatement.ValuesClause valuesClause : sqlInsertStatement.getValuesList()) {
                            SQLExpr sqlExpr = valuesClause.getValues().get(pair.getLeft());
                            if (sqlExpr instanceof SQLCharExpr) {
                                SQLCharExpr sqlCharExpr = (SQLCharExpr) sqlExpr;
                                String text = sqlCharExpr.getText();
                            }
                        }
                    }
                }
                // insert into select

            }
            if (sqlStatement instanceof SQLUpdateStatement) {
                System.out.println("SQLUpdateStatement");
                SQLUpdateStatement sqlUpdateStatement = (SQLUpdateStatement) sqlStatement;
                sqlUpdateStatement.getWhere();
            }
            if (sqlStatement instanceof SQLDeleteStatement) {
                System.out.println("SQLDeleteStatement");
                SQLDeleteStatement sqlDeleteStatement = (SQLDeleteStatement) sqlStatement;
                sqlDeleteStatement.getWhere();
            }
            if (sqlStatement instanceof SQLSelectStatement) {
                System.out.println("SQLSelectStatement");
                SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
                SQLSelectQuery query = sqlSelectStatement.getSelect().getQuery();
                if (query instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                    queryBlock.getFrom();
                }
            }
        }

    }

}
