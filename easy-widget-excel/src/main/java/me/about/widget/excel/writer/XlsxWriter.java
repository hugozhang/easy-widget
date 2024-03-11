package me.about.widget.excel.writer;

import me.about.widget.excel.Creator;
import me.about.widget.excel.ExcelDataFormatter;
import me.about.widget.excel.annotation.ExcelCellFormat;
import me.about.widget.excel.annotation.ExcelColumn;
import me.about.widget.excel.entity.ExcelCellMergeParams;
import me.about.widget.excel.entity.ExcelColumnParams;
import me.about.widget.excel.util.Constants;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  XLSX 写
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:34
 * @description:
 */
public class XlsxWriter {

    private Class inputClass;

    private XlsxWriter(Class inputClass) {
        this.inputClass = inputClass;
    }


    public static XlsxWriter build(Class inputClass) {
        return new XlsxWriter(inputClass);
    }

    public <T> void toOutputStream(List<T> data, OutputStream out) throws Exception {
        ExcelDataFormatter edf = new ExcelDataFormatter();
        try(Workbook workbook = writeToWorkBook(data, edf)) {
            workbook.write(out);
        }
    }

    public <T> Workbook writeToWorkBook(List<T> data, ExcelDataFormatter edf) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CreationHelper createHelper = workbook.getCreationHelper();
        if (data == null || data.isEmpty()) {
            return workbook;
        }
        CellStyle headCellStyle = buildHeadCellStyle(workbook);

        List<ExcelColumnParams> columnParamsList = new ArrayList<>();

        //1、处理表头
        for (Field field : inputClass.getDeclaredFields()) {
            field.setAccessible(true);
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            ExcelColumnParams columnParams = new ExcelColumnParams();
            columnParams.setName(ann.name());
            columnParams.setWidth(ann.width());
            columnParams.setGroupName(ann.groupName());
            columnParams.setOrder(ann.order());
            columnParams.setField(field);
            columnParamsList.add(columnParams);
        }

        //2、先按组再按order排序
        List<ExcelColumnParams> columnParamsSortList = columnParamsList.stream()
                .sorted(Comparator.comparing(ExcelColumnParams::getGroupName).thenComparing(ExcelColumnParams::getOrder))
                .collect(Collectors.toList());

        //3、确定有没有分组，以及后续怎么合并单元格内容
        Map<String, List<ExcelColumnParams>> columnParamsListGroup = columnParamsSortList.stream().collect(Collectors.groupingBy(ExcelColumnParams::getGroupName));
        //减掉默认分组
        int groupSize = columnParamsListGroup.size() - 1;
        Map<String, ExcelCellMergeParams> mergeCellMap = new HashMap<>(16);
        List<ExcelCellMergeParams> mergeCellParamList = new ArrayList<>();
        //列的移动
        int curColIdx = 0;

        //大于一个分组的情况才执行如下操作
        if (groupSize != 0) {
            for (ExcelColumnParams columnParams:columnParamsSortList) {
                if (Constants.DEFAULT_GROUP.equals(columnParams.getGroupName())) {
                    mergeCellMap.put(columnParams.getName(),new ExcelCellMergeParams(0,1,curColIdx,curColIdx));
                } else {
                    //相同的组要合并  窗口移动
                    if(mergeCellMap.containsKey(columnParams.getGroupName())) {
                        ExcelCellMergeParams mergeCellParams = mergeCellMap.get(columnParams.getGroupName());
                        mergeCellParams.setStartCol(mergeCellParams.getStartCol());
                        mergeCellParams.setEndCol(curColIdx);
                    } else {
                        mergeCellMap.put(columnParams.getGroupName(),new ExcelCellMergeParams(0,0,curColIdx,curColIdx));
                    }
                }
                curColIdx++;
            }
        }

        if (!mergeCellMap.isEmpty()) {
            //要合并的窗口保存下来
            for (Map.Entry<String, ExcelCellMergeParams> stringExcelCellMergeParamsEntry : mergeCellMap.entrySet()) {
                mergeCellParamList.add(stringExcelCellMergeParamsEntry.getValue());
            }
        }

        //4、构建表头行
        int headRow = groupSize == 0 ? 0 : groupSize - 1;
        for (int i = 0 ; i <= headRow ; i ++) {
            Row row0 = sheet.createRow(i);
            row0.setHeightInPoints(32);
            // 5、构建表头列
            int j = 0;
            for (ExcelColumnParams columnParams : columnParamsSortList) {
                sheet.setColumnWidth(j, columnParams.getWidth() * 256);
                Cell cell = row0.createCell(j++);
                cell.setCellStyle(headCellStyle);
                if(Constants.DEFAULT_GROUP.equals(columnParams.getGroupName())) {
                    cell.setCellValue(columnParams.getName());
                } else {
                    String cellValue = i == 0 ? columnParams.getGroupName() : columnParams.getName();
                    cell.setCellValue(cellValue);
                }
            }
        }

        //6、表头行的合并
        for (ExcelCellMergeParams mergeCellParams : mergeCellParamList) {
            sheet.addMergedRegion(new CellRangeAddress(mergeCellParams.getStartRow(),mergeCellParams.getEndRow(),mergeCellParams.getStartCol(),mergeCellParams.getEndCol()));
        }

        //数据起始行
        int curRowIndex = headRow + 1;

        //收集哪些列的哪些行数据需要合并
        Map<Object,ExcelCellMergeParams> dataCellMergeParamsMap = new HashMap<>(16);

        //6、造建数据行
        for (T t : data) {
            Row row = sheet.createRow(curRowIndex);
            int curColIndex = 0;
            // 列
            for (ExcelColumnParams columnParams:columnParamsSortList) {
                Field field = columnParams.getField();
                field.setAccessible(true);
                ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                if (ann == null) {
                    continue;
                }
                // 列数据 反射取值
                Object fieldValue = field.get(t);
                // 跳过无效数据
                if (fieldValue == null) {
                    curColIndex++;
                    continue;
                }
                Cell curCell = row.createCell(curColIndex);

                // 处理数据类型  org.apache.poi.ss.usermodel.BuiltinFormats
                if (fieldValue instanceof Date) {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    String excelFormatPattern = DateFormatConverter.convert(Locale.CHINA, ann.format());
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(excelFormatPattern));
                    curCell.setCellValue((Date) fieldValue);
                    curCell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Double || fieldValue instanceof Float) {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    curCell.setCellValue((Double) fieldValue);
                    curCell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Boolean) {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    Boolean bool = (Boolean) fieldValue;
                    if (edf == null) {
                        curCell.setCellValue(bool);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            curCell.setCellValue(bool);
                        } else {
                            curCell.setCellValue(map.get(bool.toString().toLowerCase()));
                        }
                    }
                    curCell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof Integer) {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    Integer intValue = (Integer) fieldValue;
                    if (edf == null) {
                        curCell.setCellValue(intValue);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            curCell.setCellValue(intValue);
                        } else {
                            curCell.setCellValue(map.get(intValue.toString()));
                        }
                    }
                    curCell.setCellStyle(dataCellStyle);
                } else if (fieldValue instanceof BigDecimal) {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    curCell.setCellValue(((BigDecimal) fieldValue).doubleValue());
                    curCell.setCellStyle(dataCellStyle);
                } else {
                    CellStyle dataCellStyle = buildDataCellStyle(workbook);
                    curCell.setCellValue(fieldValue.toString());
                    curCell.setCellStyle(dataCellStyle);
                }

                //此列的单元格数据相同需要合并 垂直合并
                if (ann.cellMerge()) {
                    //获取当前行的当前列的数据和上一行的当前列数据，通过上一行数据是否相同进行合并
                    Cell prevCell = sheet.getRow(curRowIndex - 1).getCell(curColIndex);
                    Object curData = getCellValue(curCell);
                    Object prevData = getCellValue(prevCell);
                    String key = ann.name() + curData;
                    if (curData.equals(prevData)) {
                        if(dataCellMergeParamsMap.containsKey(key)) {
                            ExcelCellMergeParams excelCellMergeParams = dataCellMergeParamsMap.get(key);
                            excelCellMergeParams.setEndRow(curRowIndex);
                        } else {
                            //要合并的单元格起始行要从上一行开始
                            dataCellMergeParamsMap.put(key,new ExcelCellMergeParams(curRowIndex - 1,curRowIndex,curColIndex,curColIndex));
                        }
                    }
                }

                //确定是否需要格式化
                ExcelCellFormat cellFormat = ann.cellFormat();
                if (!cellFormat.format().equals(CellFormatter.class)) {
                    //实例化自定义 格式化工具类
                    CellFormatter cellFormatter = Creator.of(cellFormat.format());
                    cellFormatter.format(fieldValue);
                }
                curColIndex++;
            }
            curRowIndex++;
        }

        List<ExcelCellMergeParams> dataCellMergeParamsList = new ArrayList<>();
        Iterator<Map.Entry<Object, ExcelCellMergeParams>> dataIt = dataCellMergeParamsMap.entrySet().iterator();
        while (dataIt.hasNext()) {
            dataCellMergeParamsList.add(dataIt.next().getValue());
        }
        //7、数据行的合并
        for (ExcelCellMergeParams mergeCellParams : dataCellMergeParamsList) {
            sheet.addMergedRegion(new CellRangeAddress(mergeCellParams.getStartRow(),mergeCellParams.getEndRow(),mergeCellParams.getStartCol(),mergeCellParams.getEndCol()));
        }

        return workbook;
    }


    private Object getCellValue(Cell cell) {
        CellType cellTypeEnum = cell.getCellTypeEnum();
        switch (cellTypeEnum) {
            case BOOLEAN:return cell.getBooleanCellValue();
            case NUMERIC:return cell.getNumericCellValue();
            default:return cell.getStringCellValue();
        }
    }

    private CellStyle buildHeadCellStyle(SXSSFWorkbook workbook) {
        //head style 部分
        CellStyle headCellStyle = workbook.createCellStyle();

        headCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
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
        return headCellStyle;
    }

    private CellStyle buildDataCellStyle(SXSSFWorkbook workbook) {
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
}
