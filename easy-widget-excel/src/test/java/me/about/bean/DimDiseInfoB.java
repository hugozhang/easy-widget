package me.about.bean;

import lombok.Data;
import me.about.widget.excel.annotation.ExcelColumn;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/08/23 15:36
 * @Description:
 */
@Data
public class DimDiseInfoB {

    @ExcelColumn(name = "疾病编码")
    private String disecode;

    @ExcelColumn(name = "疾病名称")
    private String disename;

    @ExcelColumn(name = "疾病类型")
    private String dise_type;

    @ExcelColumn(name = "首字母")
    private String initi;

    @ExcelColumn(name = "icd10编码")
    private String icd10_codg;

    @ExcelColumn(name = "编码标准")
    private String codg_std;

    @ExcelColumn(name = "编码取值定义")
    private String codg_val_defn;

    @ExcelColumn(name = "定义来源")
    private String defn_souc;

    @ExcelColumn(name = "流行病标志")
    private String epid_flag;

    @ExcelColumn(name = "章节编码")
    private String cpr_codg;

    @ExcelColumn(name = "章节名称")
    private String cpr_name;





}
