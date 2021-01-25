package me.about.widget.excel.writer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/25 15:11
 * @Description:
 */
public class DefaultCellFormatter implements CellFormatter {
    @Override
    public String format(Object value,String suffix) {
        return value == null ? "" : value.toString();
    }
}
