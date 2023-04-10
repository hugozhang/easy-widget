package me.about.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  诊断入参
 *
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2022/03/28 11:22
 * @Description:
 */
@Data
public class DrgGroupReq {

    /**
     * DRG版本
     */
    private String bbh = "100";

    /**
     * 权重版本  默认是1
     */
    private String mr;

    /**
     * 年龄
     */
    private BigDecimal nl;

    /**
     * 年龄不足 1 周岁-年龄月
     */
    private BigDecimal bzyzsnl;

    /**
     * 新生儿出生体重
     */
    private BigDecimal xsecstz;

    /**
     * 性别
     */
    private String xb;

    /**
     * 离院方式
     */
    private String lyfs;

    /**
     * 住院天数
     */
    private BigDecimal sjzyts;

    /**
     * 入院主诊断
     */
    private String jbdm;

    /**
     * 入院其他诊断
     */
    private List<String> otherJbdmList = new ArrayList<>();

    /**
     * 主手术
     */
    private String ssjcz;

    /**
     * 其他手术
     */
    private List<String> otherSsjczList = new ArrayList<>();

}
