package me.about.widget.excel.writer;

import me.about.widget.excel.writer.CellFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
    public String format(Object value,String payload) {

        if (value == null || !NumberUtils.isDigits(value.toString())) {
            return value.toString();
        }

        BigDecimal v =  value instanceof BigDecimal ? (BigDecimal)value : new BigDecimal(value + "");
        BigDecimal y10 = new BigDecimal(1000000000 + "");
        BigDecimal w10 = new BigDecimal(100000 + "");
        if (v.compareTo(y10) >= 0) {
            BigDecimal divide = v.divide(y10,2,BigDecimal.ROUND_HALF_UP);
            return divide.toString() + "亿" + payload;
        } else if (v.compareTo(w10) >= 0) {
            BigDecimal divide = v.divide(w10,2,BigDecimal.ROUND_HALF_UP);
            return divide.toString() + "万" + payload;
        } else if (v.compareTo(BigDecimal.ONE) >= 0 && v.compareTo(new BigDecimal(10000 + "")) < 0) {
            return v.toString() + "" + payload;
        }
        return value.toString();
    }

}
