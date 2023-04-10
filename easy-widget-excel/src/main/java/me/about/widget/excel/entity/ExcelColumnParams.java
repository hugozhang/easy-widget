package me.about.widget.excel.entity;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * 列配置
 *
 * @author: hugo.zxh
 * @date: 2022/01/14 16:22
 * @description:
 */
@Data
public class ExcelColumnParams {

    /**
     * 列名
     */
    private String name;

    /**
     * 列宽
     */
    private int width;

    /**
     * 分组 双行表头
     */
    private String groupName;

    /**
     * 相同group name 按序号排序
     */
    private Integer order;

    /**
     *  bean field
     */
    private Field field;

}
