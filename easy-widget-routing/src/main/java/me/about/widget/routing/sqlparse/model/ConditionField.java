package me.about.widget.routing.sqlparse.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *  where 字段名 and 字段值
 *
 * @author: hugo.zxh
 * @date: 2023/10/26 10:48
 */
@Data
@AllArgsConstructor
public class ConditionField {

    private String field;

    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionField that = (ConditionField) o;
        return Objects.equal(field, that.field) &&
                Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(field, value);
    }
}
