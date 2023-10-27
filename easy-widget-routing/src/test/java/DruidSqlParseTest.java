import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 13:48
 */
public class DruidSqlParseTest {

    public static void main(String[] args) {

        String sql = "SELECT\n" +
                "          a.id,\n" +
                "          a.`name`,\n" +
                "          a.age\n" +
                "        FROM\n" +
                "          `user` a\n" +
                "          LEFT JOIN order_info o\n" +
                "          ON a.id = o.user_id\n" +
                "        WHERE a.id = 1\n" +
                "          AND a.name = 'xxx'\n" +
                "          AND a.age = (SELECT age FROM age_table age WHERE age.user_id = 1)\n" +
                "        GROUP BY a.id,\n" +
                "          a.score\n" +
                "        HAVING MAX(a.id) > 1\n" +
                "        ORDER BY a.id DESC,\n" +
                "          a.score ASC\n" +
                "        LIMIT 3, 10;";

        List statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        System.out.println(statementList);
    }

}
