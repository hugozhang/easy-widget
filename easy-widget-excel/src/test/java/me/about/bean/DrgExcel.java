package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @auther: hugo.zxh
 * @date: 2022/07/13 11:08
 * @description:
 */
@Data
public class DrgExcel {

    @ExcelColumn(name = "医保分组编码")
    private String drg;

    @ExcelColumn(name = "NL")
    private BigDecimal nl;

    @ExcelColumn(name = "BZYZSNL")
    private BigDecimal bzyzsnl;

    @ExcelColumn(name = "XB")
    private String xb;

    @ExcelColumn(name = "映射2.0诊断代码")
    private String mainZd;

    @ExcelColumn(name = "映射2.0手术代码")
    private String mainSs;

    @ExcelColumn(name = "ys_jbbm1")
    private String zd1;

    @ExcelColumn(name = "ys_jbbm2")
    private String zd2;

    @ExcelColumn(name = "ys_jbbm3")
    private String zd3;

    @ExcelColumn(name = "ys_jbbm4")
    private String zd4;

    @ExcelColumn(name = "ys_jbbm5")
    private String zd5;

    @ExcelColumn(name = "ys_jbbm6")
    private String zd6;

    @ExcelColumn(name = "ys_jbbm7")
    private String zd7;

    @ExcelColumn(name = "ys_jbbm8")
    private String zd8;

    @ExcelColumn(name = "ys_jbbm9")
    private String zd9;

    @ExcelColumn(name = "ys_jbbm10")
    private String zd10;

    @ExcelColumn(name = "ys_jbbm11")
    private String zd11;

    @ExcelColumn(name = "ys_jbbm12")
    private String zd12;

    @ExcelColumn(name = "ys_jbbm13")
    private String zd13;

    @ExcelColumn(name = "ys_jbbm14")
    private String zd14;

    @ExcelColumn(name = "ys_jbbm15")
    private String zd15;

    @ExcelColumn(name = "ys_jbbm16")
    private String zd16;

    @ExcelColumn(name = "ys_jbbm17")
    private String zd17;

    @ExcelColumn(name = "ys_jbbm18")
    private String zd18;

    @ExcelColumn(name = "ys_jbbm19")
    private String zd19;

    @ExcelColumn(name = "ys_jbbm20")
    private String zd20;

    @ExcelColumn(name = "ys_xmbm1")
    private String ss1;

    @ExcelColumn(name = "ys_xmbm2")
    private String ss2;

    @ExcelColumn(name = "ys_xmbm3")
    private String ss3;

    @ExcelColumn(name = "ys_xmbm4")
    private String ss4;

    @ExcelColumn(name = "ys_xmbm5")
    private String ss5;

    @ExcelColumn(name = "ys_xmbm6")
    private String ss6;

    @ExcelColumn(name = "ys_xmbm7")
    private String ss7;

    @ExcelColumn(name = "ys_xmbm8")
    private String ss8;

    @ExcelColumn(name = "ys_xmbm9")
    private String ss9;
}
