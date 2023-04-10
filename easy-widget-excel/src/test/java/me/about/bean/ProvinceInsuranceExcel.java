package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/09/08 19:30
 * @Description:
 */

@Data
public class ProvinceInsuranceExcel {

    @ExcelColumn(name = "* 项目")
    private String project;

    @ExcelColumn(name = "* 本年累计")
    private BigDecimal  summary;

}
