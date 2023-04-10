package me.about.widget.excel.writer;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

/**
 * 默认实现类
 *
 * @author: hugo.zxh
 * @date: 2021/01/25 15:23
 * @description:
 */
public class CustomerCellFormatter implements CellFormatter {

    @Override
    public String format(Object value) {
        if (value != null && NumberUtils.isCreatable(value.toString())) {
            BigDecimal v = value instanceof BigDecimal ? (BigDecimal)value : new BigDecimal(value + "");
            BigDecimal absVal = v.abs();
            BigDecimal y10 = new BigDecimal("1000000000");
            BigDecimal w10 = new BigDecimal("100000");
            BigDecimal y = new BigDecimal("100000000");
            BigDecimal w = new BigDecimal("10000");
            BigDecimal divide;
            if (absVal.compareTo(y10) >= 0) {
                divide = v.divide(y, 2, 4);
                return divide.toString() + "亿";
            } else if (absVal.compareTo(w10) >= 0) {
                divide = v.divide(w, 2, 4);
                return divide.toString() + "万";
            } else {
                return absVal.compareTo(BigDecimal.ONE) >= 0 && absVal.compareTo(w10) < 0 ? v.toString() : value.toString();
            }
        } else {
            return value.toString();
        }
    }
}
