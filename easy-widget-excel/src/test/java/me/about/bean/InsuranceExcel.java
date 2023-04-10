package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/09/08 16:40
 * @Description:
 */
@Data
public class InsuranceExcel {

    @ExcelColumn(name = "* 地区")
    private String admdvsName;

    @ExcelColumn(name = "* 财产保险（1-12月）")
    private BigDecimal assetInsurInc;

    @ExcelColumn(name = "* 寿险（1-12月）")
    private BigDecimal lifeInsurInc;

    @ExcelColumn(name = "* 意外险（1-12月）")
    private BigDecimal healthInsurInc;

    @ExcelColumn(name = "* 健康险（1-12月）")
    private BigDecimal accInsurInc;

    @ExcelColumn(name = "* 1月份")
    private BigDecimal m1;

    @ExcelColumn(name = "* 2月份")
    private BigDecimal m2;

    @ExcelColumn(name = "* 3月份")
    private BigDecimal m3;

    @ExcelColumn(name = "* 4月份")
    private BigDecimal m4;

    @ExcelColumn(name = "* 5月份")
    private BigDecimal m5;

    @ExcelColumn(name = "* 6月份")
    private BigDecimal m6;

    @ExcelColumn(name = "* 7月份")
    private BigDecimal m7;

    @ExcelColumn(name = "* 8月份")
    private BigDecimal m8;

    @ExcelColumn(name = "* 9月份")
    private BigDecimal m9;

    @ExcelColumn(name = "* 10月份")
    private BigDecimal m10;

    @ExcelColumn(name = "* 11月份")
    private BigDecimal m11;

    @ExcelColumn(name = "* 12月份")
    private BigDecimal m12;
}
