package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelCellFormat;
import me.about.widget.excel.annotation.ExcelColumn;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 0:00
 * @Description:
 */
@Data
public class User {

    @ExcelColumn(name = "年龄")
    private int age;

    @ExcelColumn(name = "姓名",groupName = "YY",cellMerge = true)
    private String username;

    @ExcelColumn(name = "公司")
    private String company;

    @ExcelColumn(name = "地址",groupName = "YY")
    private String address;

    @ExcelColumn(name = "生日",groupName = "ZZ")
    private Date birthday;

    @ExcelColumn(name = "薪水",groupName = "ZZ", cellFormat = @ExcelCellFormat(payload = "元"))
    private BigDecimal salary;
}

