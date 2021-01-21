package me.about.widget.excel.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.about.widget.excel.*;
import me.about.widget.excel.ExcelColumn;
import me.about.widget.excel.ExcelColumnMerge;
import me.about.widget.excel.ExcelMeta;
import me.about.widget.excel.ExcelRowMerge;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:34
 * @Description:
 */
public class XLSXWriter {

    private XLSXWriter() {}

    public static XLSXWriter build() {
        return new XLSXWriter();
    }

    public <T> void toOutputStream(List<T> data, OutputStream out) throws Exception {
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

        headStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        headStyle.setBorderBottom(BorderStyle.THIN); //下边框
        headStyle.setBorderLeft(BorderStyle.THIN);//左边框
        headStyle.setBorderTop(BorderStyle.THIN);//上边框
        headStyle.setBorderRight(BorderStyle.THIN);//右边框

        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        font.setBold(true);
        headStyle.setFont(font);

        // 标题行
        CreationHelper createHelper = workbook.getCreationHelper();
        if (input == null || input.isEmpty()) return workbook;
        Class<?> clazz = input.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();// 取类字段集合
        int colLen = fields.length;
        Row row0 = sheet.createRow(0);
        for (int i = 0 ; i < colLen ; i ++) {
            sheet.setColumnWidth(i, 30 * 256);
            Cell cell = row0.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue("");
        }

        // 元信息
        ExcelMeta excelMeta = clazz.getAnnotation(ExcelMeta.class);
        if (excelMeta == null) {
            throw new RuntimeException("Class " + clazz.getName() + " has not found annotation @me.about.widget.excel.ExcelMeta");
        }
        // 合并行
//        ExcelRowMerge[] excelRowMerges = excelMeta.mergeRows();
        // 合并列
        ExcelColumnMerge[] excelColumnMerges = excelMeta.mergeCols();

        for (int i = 0 ; i < excelColumnMerges.length ; i ++) {
            ExcelColumnMerge columnMerge = excelColumnMerges[i];
            int[] mergeCols = columnMerge.mergeCols();
            if (mergeCols[0] == 0) {
                sheet.addMergedRegion(new CellRangeAddress(mergeCols[0],mergeCols[1],mergeCols[2],mergeCols[3]));
                row0.getCell(mergeCols[2]).setCellValue(columnMerge.mergeColsText());
            }
        }

        Row row = sheet.createRow(excelColumnMerges.length == 0 ? 0 : 1);  //确定起始行

        for (int i = 0 ; i < colLen ; i ++) {
            Field field = fields[i];
            field.setAccessible(true);
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            sheet.setColumnWidth(i, ann.width() * 256);
            Cell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue(ann.name());
        }

        // data 部分
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        dataStyle.setBorderBottom(BorderStyle.THIN); //下边框
        dataStyle.setBorderLeft(BorderStyle.THIN);//左边框
        dataStyle.setBorderTop(BorderStyle.THIN);//上边框
        dataStyle.setBorderRight(BorderStyle.THIN);//右边框

        int rowIndex = excelColumnMerges.length == 0 ? 1 : 2; //确定起始行
        // 数据行
        for (T t : input) {
            row = sheet.createRow(rowIndex);
            int columnIndex = 0;

            for (int i = 0 ; i < excelColumnMerges.length ; i ++) {
                ExcelColumnMerge columnMerge = excelColumnMerges[i];
                int[] mergeCols = columnMerge.mergeCols();
                if (mergeCols[0] == rowIndex) {
                    sheet.addMergedRegion(new CellRangeAddress(mergeCols[0],mergeCols[1],mergeCols[2],mergeCols[3]));
                    row.createCell(mergeCols[2]).setCellValue(columnMerge.mergeColsText());
//                    row.getCell(mergeCols[2]).setCellValue(columnMerge.mergeColsText());
                }
            }

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
                cell.setCellStyle(dataStyle);
                // 处理数据类型
                if (o instanceof Date) {
                    dataStyle.setDataFormat(createHelper.createDataFormat().getFormat(ann == null ? "yyyy-MM-dd HH:mm:ss" : ann.format()));
                    cell.setCellValue((Date) o);
                } else if (o instanceof Double || o instanceof Float) {
                    dataStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
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
                    dataStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
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
