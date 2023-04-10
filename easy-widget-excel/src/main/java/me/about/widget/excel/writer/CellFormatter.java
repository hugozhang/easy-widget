package me.about.widget.excel.writer;


/**
 * 单元格格式化
 *
 * @author: hugo.zxh
 * @date: 2021/01/25 15:01
 * @description:
 */
public interface CellFormatter {

    /**
     * 单元格格式化
     * @param value
     * @return
     */
    String format(Object value);

}
