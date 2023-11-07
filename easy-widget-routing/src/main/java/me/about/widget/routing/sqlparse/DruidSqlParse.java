package me.about.widget.routing.sqlparse;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import me.about.widget.routing.RoutingContext;
import me.about.widget.routing.sqlparse.model.SqlParseResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * SQL Parse
 *
 * @author: hugo.zxh
 * @date: 2023/10/31 10:49
 */
public class DruidSqlParse {

    public static SqlParseResult parse(String sql) {
        SqlParseResult sqlParseResult = new SqlParseResult();
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (SQLStatement sqlStatement : statementList) {
            CustomerSchemaStatVisitor visitor = new CustomerSchemaStatVisitor();
            sqlStatement.accept(visitor);
            visitor.getTables().entrySet().forEach(entry -> {
                sqlParseResult.addTableOperator(entry.getKey().getName(),entry.getValue().toString());
            });
            visitor.getConditions().forEach(condition -> {
                if (RoutingContext.getShardingColumns().contains(condition.getColumn().getName())
                        && condition.getOperator().equalsIgnoreCase("=")
                        && !condition.getValues().isEmpty()) {
                    sqlParseResult.addConditionField(condition.getColumn().getName(),condition.getValues().get(0).toString());
                }
            });
            // insert into values  计算列对应的值
            if (!visitor.getValues().isEmpty()) {
                Pair<Integer,String> pair = null;
                int i = 0;
                for (TableStat.Column column : visitor.getColumns()) {
                    if (RoutingContext.getShardingColumns().contains(column.getName())) {
                        pair =  Pair.of(i,column.getName());
                        break;
                    }
                    i++;
                }
                if (pair != null) {
                    for (List<Object> row : visitor.getValues()) {
                        Object o = row.get(pair.getLeft());
                        if (o instanceof String) {
                            sqlParseResult.addConditionField(pair.getRight(),o.toString());
                        }
                    }
                }
            }
        }
        return sqlParseResult;
    }

    public static void main(String[] args) {

//        String sql = "SELECT (select zd_z from s_sys_zd_tbl where zd_lx='DBL' ) as dbl, (select zd_z from s_sys_zd_tbl where zd_lx='GBL' ) as gbl FROM s_sys_zd_tbl group by dbl,gbl";
//        String sql = "SELECT * FROM STORY a left join (select * from a where dd.medins_no = '1' and g.bb_1 = '2' ) b on a.id=b.id where medins_no='c' and b=1 order by a desc";
//        String sql = "select * from a left join b on a.id = b.id where a.id =1 ";
//        String sql = "insert into a(a,b,c) select * from c where c=1";
//        String sql = "insert into a(a,b,c) values('1',2,'3')";
//        String sql = "insert into a(a,b,c) values('1',2,'3'),('2',2,3)";
//        String sql = "update b set a =1 where c = 1";
//        String sql = "delete from a where id = 2";
//        String sql = "select * from a where medins_no = '123qq' and age=2";
//        String sql = "select * from a where id = 1 or age=2";
//        String sql = "select name, `value` from t_cast_ddl";
//        String sql = "SELECT t.ID,t.TASK_NAME,t.CRON, t.REMARK,t.STATUS,t.JOB_NAME, u.YH_MC AS \"CREATE_ID\", t.MODIFY_ID,t.MODIFY_AT,t.DELE_FLG FROM T_TASK t LEFT JOIN S_SYS_YH_TBL u ON t.MODIFY_ID=u.YH_ID WHERE t.DELE_FLG='0'\n";
//        String sql = "DELETE users, orders FROM users JOIN orders ON users.user_id = orders.user_id WHERE users.username = 'John'";
//        String sql = "DELETE orders FROM orders JOIN users ON orders.user_id = users.user_id WHERE users.username = 'John'";
//        String sql = "UPDATE library l, book b SET l.book_count = l.book_count - 2, b.book_count = b.book_count + 2 WHERE l.id = b.book_id AND b.id = '1AG'";
//        String sql = "UPDATE A JOIN B ON A.id=B.order_id SET A.order_status=B.target_status AND A.address=B.target_address WHERE a = '123'";
//        SQLStatementParser parser = new MySqlStatementParser(sql);
//        SQLStatement sqlStatement = parser.parseStatement();
//        CustomerSchemaStatVisitor visitor = new CustomerSchemaStatVisitor();
//        sqlStatement.accept(visitor);
//        System.out.println(visitor.getColumns()); //[acct.name, acct.id, user.money]
//        System.out.println(visitor.getTables()); //{acct=Select, user=Select}
//        System.out.println(visitor.getConditions()); //[acct.id = 10]
//        System.out.println(visitor.getDbType());//mysql
//        System.out.println(visitor.getValues());
        String sql = "select dipzbm,dipzmc from T_DIP_FZTJ_TBL where 1=1 and dddsbm =  and dipzbm like CONCAT(CONCAT('%','40511_l/1__'),'%')";

        RoutingContext.addShardingColumn("a");
        RoutingContext.addShardingColumn("username");

        SqlParseResult parseResult = parse(sql);
        System.out.println(parseResult);
    }
}
