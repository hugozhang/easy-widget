package me.about.widget.excel.writer;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/25 15:23
 * @Description:
 */
public class CustomerCellFormatter implements CellFormatter {

    @Override
    public void format(Cell cell,String fieldName, Object value, String payload) {

        if (value == null || !NumberUtils.isCreatable(value.toString())) {
            return ;
        }

        BigDecimal v =  value instanceof BigDecimal ? (BigDecimal)value : new BigDecimal(value + "");

        BigDecimal absVal = v.abs();

        BigDecimal y10 = new BigDecimal(1000000000 + "");
        BigDecimal w10 = new BigDecimal(100000 + "");

        BigDecimal y = new BigDecimal(100000000 + "");
        BigDecimal w = new BigDecimal(10000 + "");

        if (absVal.compareTo(y10) >= 0) {
            BigDecimal divide = v.divide(y,2,BigDecimal.ROUND_HALF_UP);
            cell.setCellValue(divide.toString() + "亿" + payload);
        } else if (absVal.compareTo(w10) >= 0) {
            BigDecimal divide = v.divide(w,2,BigDecimal.ROUND_HALF_UP);
            cell.setCellValue(divide.toString() + "万" + payload);
        } else if (absVal.compareTo(BigDecimal.ONE) >= 0 && absVal.compareTo(w10) < 0) {
            cell.setCellValue(v.toString() + "" + payload);
        } else {
            cell.setCellValue(value.toString() + payload);
        }
    }
}
