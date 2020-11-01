package me.about.widget.excel.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.about.widget.excel.ExcelColumn;
import me.about.widget.excel.ExcelDataFormatter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:34
 * @Description:
 */
public class XLSXWriter {
    public static XLSXWriter builder() {
        return new XLSXWriter();
    }

    public <T> void toStream(List<T> data, OutputStream out) throws Exception {
        ExcelDataFormatter edf = new ExcelDataFormatter();
        try(Workbook workbook = writeToWorkBook(data, edf)) {
            workbook.write(out);
        }
    }

    public <T> Workbook writeToWorkBook(List<T> input, ExcelDataFormatter edf) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
//        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet();

        //head style 部分
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        headStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setBold(true);
        headStyle.setFont(font);

        Row row = sheet.createRow(0);

        //head 部分
        CreationHelper createHelper = workbook.getCreationHelper();

        if (input == null || input.isEmpty()) return workbook;

        Field[] fields = input.get(0).getClass().getDeclaredFields();// 取类字段集合

        int headColumnIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            sheet.setColumnWidth(headColumnIndex, ann.width() * 256);
            Cell cell = row.createCell(headColumnIndex);
            cell.setCellStyle(headStyle);
            cell.setCellValue(ann.name());
            headColumnIndex++;
        }

        //data 部分
        CellStyle cs = workbook.createCellStyle();
        int rowIndex = 1;
        // 行
        for (T t : input) {
            row = sheet.createRow(rowIndex);
            int columnIndex = 0;
            // 列
            for (Field field : fields) {
                field.setAccessible(true);
                ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                if (ann == null) {
                    continue;
                }
                // 列数据
                Object o = field.get(t);// 反射取值
                if (o == null) {
                    columnIndex++;// *****跳到下一列
                    continue;
                }
                Cell cell = row.createCell(columnIndex);
                // 处理数据类型
                if (o instanceof Date) {
                    cs.setDataFormat(createHelper.createDataFormat().getFormat(ann == null ? "yyyy-MM-dd HH:mm:ss" : ann.format()));
                    cell.setCellStyle(cs);
                    cell.setCellValue((Date) o);
                } else if (o instanceof Double || o instanceof Float) {
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellStyle(cs);
                    cell.setCellValue((Double) o);
                } else if (o instanceof Boolean) {
                    Boolean bool = (Boolean) o;
                    if (edf == null) {
                        cell.setCellValue(bool);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(bool);
                        } else {
                            cell.setCellValue(map.get(bool.toString().toLowerCase()));
                        }
                    }
                } else if (o instanceof Integer) {
                    Integer intValue = (Integer) o;
                    if (edf == null) {
                        cell.setCellValue(intValue);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(intValue);
                        } else {
                            cell.setCellValue(map.get(intValue.toString()));
                        }
                    }
                } else if (o instanceof BigDecimal) {
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellStyle(cs);
                    cell.setCellValue(((BigDecimal) o).doubleValue());
                } else {
                    cell.setCellValue(o.toString());
                }
                columnIndex++;
            }
            rowIndex++;
        }
        return workbook;
    }
}
