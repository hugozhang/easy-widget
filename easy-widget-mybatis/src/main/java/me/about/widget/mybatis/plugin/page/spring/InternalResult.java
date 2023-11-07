package me.about.widget.mybatis.plugin.page.spring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 0:15
 */
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
