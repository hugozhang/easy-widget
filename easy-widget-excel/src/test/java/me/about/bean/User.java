package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;
import me.about.widget.excel.annotation.ExcelCellMerge;
import me.about.widget.excel.annotation.ExcelMeta;

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
@ExcelMeta(headEndIndex = 1, mergeCells = {
        @ExcelCellMerge(coordinate = {0,0,0,1}, text = "合并列单元格1"),
        @ExcelCellMerge(coordinate = {0,0,2,3}, text = "合并列单元格2"),
        @ExcelCellMerge(coordinate = {0,1,4,5}, text = "合并列单元格3"),
        @ExcelCellMerge(coordinate = {2,4,0,0}, text = "就诊人次"),
        @ExcelCellMerge(coordinate = {5,6,0,0}, text = "就诊人次2")
})
public class User {

    @ExcelColumn(name = "年龄")
    private int age;

    @ExcelColumn(name = "姓名")
    private String username;

    @ExcelColumn(name = "公司")
    private String company;

    @ExcelColumn(name = "地址")
    private String address;

    @ExcelColumn(name = "生日")
    private Date birthday;

    @ExcelColumn(name = "薪水")
    private BigDecimal salary;
}

