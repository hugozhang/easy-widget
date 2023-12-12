package me.about.widget.mybatis.plugin.page.model;

import lombok.Data;

import java.util.List;

/**
 * 分页对象
 *
 * @author: hugo.zxh
 * @date: 2023/11/06 10:37
 */
@Data
public class PageResult<T> {

    /**
     * 结果  总记录数
     */
    private long total;

    /**
     * 结果  总页数
     */
    private int totalPage;

    /**
     * 结果  记录数
     */
    private List<T> rows;
}
