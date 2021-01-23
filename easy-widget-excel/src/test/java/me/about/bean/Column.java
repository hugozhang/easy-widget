package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/10/04 16:57
 * @Description:
 */
@Data
public class Column {

    @ExcelColumn(name = "字段名")
    private String columnId;

    @ExcelColumn(name = "字段名称")
    private String columnName;

    @ExcelColumn(name = "类型")
    private String columnType;

//    @ExcelColumn(name = "Hive类型")
    private String columnHiveType;

}
