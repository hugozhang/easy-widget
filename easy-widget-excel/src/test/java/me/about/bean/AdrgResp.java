package me.about.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2022/03/30 14:03
 * @Description:
 */
@Data
public class AdrgResp implements Serializable {

    /**
     * MDC编码
     */
    private String mdcbm;

    /**
     * MDC名称
     */
    private String mdcmc;

    /**
     * adrg 编码
     */
    private String adrgbm;

    /**
     * adrg 名称
     */
    private String adrgmc;

    /**
     * drg 编码
     */
    private String drgbm;

    /**
     * drg 名称
     */
    private String drgmc;

    /**
     * 描述
     */
    private String desc;

    /**
     * 问题标识
     */
    private String wtbs;

    /**
     * 权重
     */
    private BigDecimal weight;
}
