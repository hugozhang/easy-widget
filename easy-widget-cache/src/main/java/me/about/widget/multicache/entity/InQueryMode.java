package me.about.widget.multicache.entity;

import lombok.Data;

/**
 * in查询模式结构休
 *
 * @author: hugo.zxh
 * @date: 2022/07/08 16:14
 * @description:
 */

@Data
public class InQueryMode {

    /**
     * 附加缓存key
     */
    private String fieldName;

    /**
     * 参数名字  需要指定 -parameters 才能拿到真实的参数名字
     */
    private String parameterName;


    /**
     * 参数类型
     */
    private Class parameterType;


    /**
     * 参数索引
     */
    private Integer parameterIndex;


}
