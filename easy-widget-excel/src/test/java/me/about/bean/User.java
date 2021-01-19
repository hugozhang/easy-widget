package me.about.bean;

import lombok.Data;
import me.about.widget.excel.ExcelColumn;
import me.about.widget.excel.ExcelColumnMerge;
import me.about.widget.excel.ExcelMeta;
import me.about.widget.excel.ExcelRowMerge;

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
@ExcelMeta(mergeCols = {
        @ExcelColumnMerge(mergeCols = {0,0,0,1},mergeColsText = "合并列单元格1"),
        @ExcelColumnMerge(mergeCols = {0,0,2,3},mergeColsText = "合并列单元格2"),
        @ExcelColumnMerge(mergeCols = {0,1,4,5},mergeColsText = "合并列单元格3")
},mergeRows = {
        @ExcelRowMerge(mergeRows = {2,5,0,0},mergeRowsText = "合并列单元格1")
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

