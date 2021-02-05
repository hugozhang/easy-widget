package me.about.widget.excel.writer;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/25 15:01
 * @Description:
 */
public interface CellFormatter {

    void format(Cell cell,String fieldName, Object value, String payload);

}
