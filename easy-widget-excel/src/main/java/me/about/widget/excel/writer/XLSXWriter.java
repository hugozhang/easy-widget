package me.about.widget.excel.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import me.about.widget.excel.*;
import me.about.widget.excel.annotation.ExcelCellFormat;
import me.about.widget.excel.annotation.ExcelColumn;
import me.about.widget.excel.annotation.ExcelCellMerge;
import me.about.widget.excel.annotation.ExcelMeta;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:34
 * @Description:
 */
public class XLSXWriter {

    private CellFormatter cellFormatter;

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
                newCellValue(cell,i,j,cellTextMap);
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
                Object fieldValue = field.get(t);// 反射取值
                if (fieldValue == null) {
                    columnIndex++;// *****跳到下一列
                    continue;
                }
                Cell cell = row.createCell(columnIndex);
                // 处理数据类型  org.apache.poi.ss.usermodel.BuiltinFormats
                if (fieldValue instanceof Date) {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    String excelFormatPattern = DateFormatConverter.convert(Locale.CHINA, ann.format());
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(excelFormatPattern));
                    cell.setCellValue((Date) fieldValue);
                    cell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Double || fieldValue instanceof Float) {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellValue((Double) fieldValue);
                    cell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Boolean) {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    Boolean bool = (Boolean) fieldValue;
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
                    cell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Integer) {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    Integer intValue = (Integer) fieldValue;
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
                    cell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof BigDecimal) {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellValue(((BigDecimal) fieldValue).doubleValue());
                    cell.setCellStyle(dataCellStyle);
                } else {
                    CellStyle dataCellStyle = getDataCellStyle(workbook);
                    cell.setCellValue(fieldValue.toString());
                    cell.setCellStyle(dataCellStyle);
                }

                //确定是否需要合并以及合并后显示的值
                newCellValue(cell,rowIndex,columnIndex,cellTextMap);

                //确定是否需要格式化
                if (cellFormatter == null) {
                    //自定义格式化
                    ExcelCellFormat cellFormat = ann.cellFormat();
                    this.cellFormatter = Creator.of(cellFormat.format());
                }

                //确定是否格式化
                this.cellFormatter.format(cell,field.getName(),fieldValue,ann.cellFormat().payload());

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

    private CellStyle getDataCellStyle(SXSSFWorkbook workbook) {
        // data 部分
        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        return dataCellStyle;
    }

    private void newCellValue(Cell cell, int rowIndex, int cellIndex, Map<String,String> cellTextMap) {
        //合并单元格后显示值
        if (cellTextMap.containsKey(rowIndex + "" + cellIndex)) {
            cell.setCellValue(cellTextMap.get(rowIndex + "" + cellIndex));
        }
    }
}
