package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

@Data
public class Zd {

    @ExcelColumn(name = "字典类型代码")
    private String ZD_LX;

    @ExcelColumn(name = "字典类型名称")
    private String ZD_LX_MC;

    @ExcelColumn(name = "字典名称")
    private String ZD_MC;

    @ExcelColumn(name = "字典值")
    private String ZD_Z;

    private Long pid;


}
