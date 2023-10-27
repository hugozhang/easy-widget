package me.about.widget.routing.sqlparse.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *   表名 and 表参与的操作 主要是关注更新 update,insert,delete
 *
 * @author: hugo.zxh
 * @date: 2023/10/26 10:48
 */

@Data
@AllArgsConstructor
public class TableOperator {

    private String tableName;

    private String operate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableOperator)) {
            return false;
        }
        TableOperator that = (TableOperator) o;
        return Objects.equal(getTableName(), that.getTableName()) &&
                Objects.equal(getOperate(), that.getOperate());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTableName(), getOperate());
    }
}
