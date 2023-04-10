package me.about.widget.multicache.entity;

import lombok.Data;

/**
 * 方法参数信息
 *
 * @author: hugo.zxh
 * @date: 2022/07/08 16:22
 * @description:
 */
@Data
public class MethodParameter {

    /**
     * 参数名
     */
    private String parameterName;

    /**
     * 参数值
     */
    private Object parameterValue;

    /**
     * 参数索引
     */
    private Integer parameterIndex;

}
