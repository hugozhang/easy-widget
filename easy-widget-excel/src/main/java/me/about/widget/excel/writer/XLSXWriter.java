package me.about.widget.excel.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import me.about.widget.excel.*;
import me.about.widget.excel.annotation.ExcelColumn;
import me.about.widget.excel.annotation.ExcelCellMerge;
import me.about.widget.excel.annotation.ExcelMeta;
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
        Sheet sheet = workbook.createSheet();

        CreationHelper createHelper = workbook.getCreationHelper();
        if (input == null || input.isEmpty()) return workbook;
        Class<?> clazz = input.get(0).getClass();

        // 元信息
        ExcelMeta excelMeta = clazz.getAnnotation(ExcelMeta.class);
        if (excelMeta == null) {
            throw new RuntimeException("Class " + clazz.getName() + " has not found annotation @me.about.widget.excel.ExcelMeta");
        }

        // 需合并的列值
        Map<String,String> cellTextMap = new HashMap<>();

        // 合并head处理
        ExcelCellMerge[] excelCellMerges = excelMeta.mergeCells();
        for (int i = 0; i < excelCellMerges.length ; i ++) {
            ExcelCellMerge cellMerge = excelCellMerges[i];
            int[] mergeCells = cellMerge.coordinate();
            cellTextMap.put(mergeCells[0] + "" + mergeCells[2],cellMerge.text());
        }

        //head style 部分
        CellStyle headCellStyle = workbook.createCellStyle();

        headCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        headCellStyle.setBorderBottom(BorderStyle.THIN);
        headCellStyle.setBorderLeft(BorderStyle.THIN);
        headCellStyle.setBorderTop(BorderStyle.THIN);
        headCellStyle.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        font.setBold(true);
        headCellStyle.setFont(font);

        //行号
        int rowIndex;

        int headIndex = excelMeta.headEndIndex();

        //head body 部分
        Field[] fields = clazz.getDeclaredFields();// 取类字段集合
        int colLen = fields.length;
        //行
        for (int i = 0 ; i <= headIndex ; i ++) {
            Row row0 = sheet.createRow(i);
            //列
            for (int j = 0 ; j < colLen ; j ++) {
                sheet.setColumnWidth(j, 30 * 256);
                Cell cell = row0.createCell(j);
                cell.setCellStyle(headCellStyle);
                cell.setCellValue("");
                //合并单元格后显示值
                if (cellTextMap.containsKey(i + "" + j)) {
                    cell.setCellValue(cellTextMap.get(i + "" + j));
                }
            }
        }

        Row row0 = sheet.getRow(headIndex);  //确定起始行

        for (int i = 0 ; i < colLen ; i ++) {
            Field field = fields[i];
            field.setAccessible(true);
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            sheet.setColumnWidth(i, ann.width() * 256);
            Cell cell = row0.getCell(i);
            cell.setCellStyle(headCellStyle);
            cell.setCellValue(ann.name());
        }

        // data 部分
        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);

        rowIndex = headIndex + 1; //确定起始行

        // 数据行
        for (T t : input) {
            Row row = sheet.createRow(rowIndex);
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
                cell.setCellStyle(dataCellStyle);
                // 处理数据类型
                if (o instanceof Date) {
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(ann == null ? "yyyy-MM-dd HH:mm:ss" : ann.format()));
                    cell.setCellValue((Date) o);
                } else if (o instanceof Double || o instanceof Float) {
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
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
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellValue(((BigDecimal) o).doubleValue());
                } else {
                    cell.setCellValue(o.toString());
                }

                //单元格合并  显示值
                if (cellTextMap.containsKey(rowIndex + "" + columnIndex)) {
                    cell.setCellValue(cellTextMap.get(rowIndex + "" + columnIndex));
                }

                //自定义格式化
                Class<? extends CellFormatter> customerFormat = ann.customerFormat();
                if (!customerFormat.equals(DefaultCellFormatter.class) && o != null) {
                    //单元格格式化
                    CellFormatter cellFormatter = Creator.of(customerFormat);
                    cell.setCellValue(cellFormatter.format(o,ann.suffix()));
                }

                columnIndex++;
            }
            rowIndex++;
        }

        // 合并单元格处理
        for (int i = 0; i < excelCellMerges.length ; i ++) {
            ExcelCellMerge cellMerge = excelCellMerges[i];
            int[] mergeCells = cellMerge.coordinate();
            sheet.addMergedRegion(new CellRangeAddress(mergeCells[0],mergeCells[1],mergeCells[2],mergeCells[3]));
        }
        return workbook;
    }
}
