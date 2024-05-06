package me.about.widget.mybatis.plugin.page.spring;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部使用
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 0:15
 */
@Getter
public class InternalResult<T> extends ArrayList<T> {

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

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
