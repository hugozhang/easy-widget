package me.about.widget.mybatis.plugin.page.model;

import lombok.Data;

/**
 * 分页参数与业务参数
 *
 * @author: hugo.zxh
 * @date: 2023/11/06 17:57
 */

@Data
public class PageParam<T> {

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 页大小
     */
    private int pageSize;


    /**
     * 业务参数
     */
    private  T paramObj;

}
