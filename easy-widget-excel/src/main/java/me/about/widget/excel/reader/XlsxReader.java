package me.about.widget.excel.reader;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.excel.CellDataType;
import me.about.widget.excel.Creator;
import me.about.widget.excel.annotation.ExcelColumn;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * XLSX 读
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:06
 * @description:
 */

@Slf4j
public class XlsxReader {

    private OPCPackage pkg;

    private XSSFReader xssfReader;

    private StylesTable stylesTable;

    private SharedStringsTable sharedStringsTable;

    private int skipRow = 1;

    private Class outputClass;

    private XlsxReader(Class outputClass) {
        this.outputClass = outputClass;
    }

    public static XlsxReader build(Class outputClass) {
        return new XlsxReader(outputClass);
    }

    public XlsxReader open(InputStream in) throws Exception {
        this.pkg = OPCPackage.open(in);
        this.xssfReader = new XSSFReader(pkg);
        this.stylesTable = xssfReader.getStylesTable();
        this.sharedStringsTable = xssfReader.getSharedStringsTable();
        return this;
    }

    public XlsxReader skipRow(int skipRow) {
        this.skipRow = skipRow;
        return this;
    }

    public <T> List<T> sheetParser(Integer sheetIndex) throws Exception{
        List<T> rows = new ArrayList();
        XMLReader parser = XMLReaderFactory.createXMLReader();
        InputStream sheet = xssfReader.getSheet("rId" + (sheetIndex + 1));
        ContentHandler handler = new SheetHandler(stylesTable, sharedStringsTable,sheetIndex + "", skipRow, rows, outputClass);
        parser.setContentHandler(handler);
        InputSource sheetSource = new InputSource(sheet);
        parser.parse(sheetSource);
        sheet.close();
        pkg.close();
        return rows;
    }

    public <T> List<T> sheetsParser() throws Exception{
        List<T> rows = new ArrayList();
        XMLReader parser = XMLReaderFactory.createXMLReader();
        XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (iterator.hasNext()) {
            InputStream sheet = iterator.next();
            ContentHandler handler = new SheetHandler(stylesTable, sharedStringsTable,iterator.getSheetName(), skipRow, rows, outputClass);
            parser.setContentHandler(handler);
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        pkg.close();
        return rows;
    }


    public <T> void sheetParser(SheetDone sheetDone) throws Exception {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (iterator.hasNext()) {
            InputStream sheet = iterator.next();
            List<T> rows = new ArrayList();
            ContentHandler handler = new SheetHandler(stylesTable, sharedStringsTable,iterator.getSheetName(), skipRow, rows, outputClass);
            parser.setContentHandler(handler);
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            sheetDone.ok(iterator.getSheetName(),rows);
        }
        pkg.close();
    }

    /***
     * sheet parse done event
     * @param <T>
     */
    public interface SheetDone<T> {
        /**
         * 解析完回调
         * @param sheetName
         * @param rows
         * @throws Exception
         */
        void ok(String sheetName,List<T> rows) throws Exception;
    }

    class SheetHandler<T> extends DefaultHandler {

        /** Table with styles */
        private StylesTable stylesTable;
        /** Table with unique strings */
        private SharedStringsTable sharedStringsTable;

        private String sheetName;

        private int headerRowIndex;
        private Class<T> clazz;
        private T currentRow;
        private List<T> rows;
        private String columnName;
        private Map<String, Field> fieldMapping = new HashMap<String, Field>();
        private Map<String, String> titleMapping = new HashMap<String, String>();
        private int rowNumber;// row
        private int cellNumber;// column

        // row is empty?
        private boolean rIsEmpty = true;
        // Set when V start element is seen
        private boolean vIsOpen;
        // Set when cell start element is seen;
        // used when cell close element is seen.
        private CellDataType nextDataType;
        // Used to format numeric cell values.
        private short formatIndex;
        private String formatString;
        private DataFormatter formatter;
        // Gathers characters as they are seen.
        private StringBuffer value;

        private SheetHandler(StylesTable stylesTable,
                             SharedStringsTable sharedStringsTable,
                             String sheetName,
                             int headerRowIndex,
                             List<T> rows,
                             Class<T> clazz) {
            this.stylesTable = stylesTable;
            this.sharedStringsTable = sharedStringsTable;
            this.sheetName = sheetName;
            this.clazz = clazz;
            this.rows = rows;
            this.headerRowIndex = headerRowIndex;

            this.value = new StringBuffer();
            this.nextDataType = CellDataType.NUMBER;
            this.formatter = new DataFormatter();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                if (ann != null) {
                    fieldMapping.put(ann.name(), field);
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {

            if (isTextTag(localName)) {
                vIsOpen = true;
                // Clear contents cache
                value.setLength(0);
            } else if ("c".equals(localName)) {
                // c => cell
                cellNumber++;
                String r = attributes.getValue("r");
                StringBuffer column = new StringBuffer();
                for (int c = 0, len = r.length(); c < len; ++c) {
                    if (Character.isLetter(r.charAt(c))) {
                        column.append(r.charAt(c));
                    }
                }
                this.columnName = column.toString();
                // Set up defaults.
                this.nextDataType = CellDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)) {
                    nextDataType = CellDataType.BOOLEAN;
                } else if ("e".equals(cellType)) {
                    nextDataType = CellDataType.ERROR;
                } else if ("inlineStr".equals(cellType)) {
                    nextDataType = CellDataType.INLINE_STRING;
                } else if ("s".equals(cellType)) {
                    nextDataType = CellDataType.SST_STRING;
                } else if ("str".equals(cellType)) {
                    nextDataType = CellDataType.FORMULA;
                } else {
                    // Number, but almost certainly with a special style or format
                    XSSFCellStyle style = null;
                    if (stylesTable != null) {
                        if (cellStyleStr != null) {
                            int styleIndex = Integer.parseInt(cellStyleStr);
                            style = stylesTable.getStyleAt(styleIndex);
                        } else if (stylesTable.getNumCellStyles() > 0) {
                            style = stylesTable.getStyleAt(0);
                        }
                    }
                    if (style != null) {
                        this.formatIndex = style.getDataFormat();
                        this.formatString = style.getDataFormatString();
                        if (this.formatString == null) {
                            this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                        }
                    }
                }
            } else if ("row".equals(localName)) {
                rowNumber++;
                cellNumber = 0;
                rIsEmpty = true;
                if (rowNumber > this.headerRowIndex) {
                    currentRow = Creator.of(this.clazz);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {

            if (uri != null && !uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
                return;
            }
            String thisStr = null;
            // v => contents of a cell
            if (isTextTag(localName)) {
                vIsOpen = false;
                switch (nextDataType) {
                    case BOOLEAN:
                        char first = value.charAt(0);
                        thisStr = first == '0' ? "FALSE" : "TRUE";
                        break;
                    case ERROR:
                        thisStr = "ERROR:" + value.toString();
                        break;
                    case FORMULA:
                        String fv = value.toString();
                        if (this.formatString != null) {
                            try {
                                // Try to use the value as a formatt】able number
                                double d = Double.parseDouble(fv);
                                thisStr = formatter.formatRawCellContents(d, this.formatIndex, this.formatString);
                            } catch (NumberFormatException e) {
                                log.error(e.getMessage(),e);
                                thisStr = fv;
                            }
                        } else {
                            thisStr = fv;
                        }
                        break;
                    case INLINE_STRING:
                        XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                        thisStr = rtsi.toString();
                        break;
                    case SST_STRING:
                        String sstIndex = value.toString();
                        try {
                            int idx = Integer.parseInt(sstIndex);
                            XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                            thisStr = rtss.toString();
                        } catch (NumberFormatException e) {
                            log.error(e.getMessage(),e);
                        }
                        break;
                    case NUMBER:
                        String n = value.toString().trim();
                        if (DateUtil.isADateFormat(this.formatIndex, this.formatString)) {
                            thisStr = n;
                        } else if (this.formatString != null) {
                            thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex,
                                    this.formatString);
                        } else {
                            thisStr = n;
                        }
                        break;
                    default:
                        thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                        break;
                }

                if (rowNumber == this.headerRowIndex) {
                    titleMapping.put(this.columnName, thisStr);
                }
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
                if (rowNumber > this.headerRowIndex) {
                    checkColumn(fieldMapping, titleMapping);
                    Field field = fieldMapping.get(titleMapping.get(this.columnName));
                    if (field == null) {
                        return;
                    }
                    ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                    if (ann == null) {
                        return;
                    }
                    Class<?> clz = field.getType();
                    try {
                        field.setAccessible(true);
                        if (int.class.equals(clz) || Integer.class.equals(clz)) {
                            checkValueType(titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toInt("".equals(thisStr) ? null : thisStr));
                        } else if (long.class.equals(clz) || Long.class.equals(clz)) {
                            checkValueType(titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toLong(thisStr));
                        } else if (float.class.equals(clz) || Float.class.equals(clz)) {
                            checkValueType(titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toFloat(thisStr));
                        } else if (double.class.equals(clz) || Double.class.equals(clz)) {
                            checkValueType(titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toDouble(thisStr));
                        } else if (boolean.class.equals(clz) || Boolean.class.equals(clz)) {
                            field.set(currentRow, thisStr);
                        } else if (Date.class.equals(clz)) {
                            if (thisStr == null) {
                                return;
                            }
                            Date javaDate = DateUtil.getJavaDate(Double.parseDouble(thisStr));
                            field.set(currentRow, javaDate);
                        } else if (BigDecimal.class.equals(clz)) {
                            checkValueType(titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, BigDecimal.valueOf(Double.parseDouble(thisStr)));
                        } else {
                            field.set(currentRow, thisStr);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                    }
                }
            } else if ("row".equals(qName)) {
                if (rowNumber > this.headerRowIndex && !rIsEmpty) {
                    this.rows.add(currentRow);
                }
            }
        }

        private boolean isTextTag(String name) {
            if ("v".equals(name) || "t".equals(name)) {
                // Easy, normal v text tag
                // 列有值，行肯定不为空
                rIsEmpty = false;
                return true;
            }
            if ("inlineStr".equals(name)) {
                // Easy inline string
                return true;
            }
            return false;
        }

        private void checkValueType(String columnName, String value) {
            if (!NumberUtils.isCreatable(value) && value != null && !"".equals(value)) {
                throw new RuntimeException("(标签页：" + sheetName + "，行：" + rowNumber + "，列：" + cellNumber + ")列名为'"
                        + columnName + "'，值" + value + "，不能转为数值类型");
            }
        }

        private void checkColumn(Map<String, Field> fieldMapping, Map<String, String> titleMapping) {
            if (titleMapping == null || titleMapping.isEmpty()) {
                throw new RuntimeException("标签页：" + sheetName + "，Excel文件标题栏错误，请检查下");
            }
            if (fieldMapping == null || fieldMapping.isEmpty()) {
                throw new RuntimeException("标签页：" + sheetName + "，没有读到需要的数据列，请检查下");
            }
            Iterator<Map.Entry<String, Field>> it = fieldMapping.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Field> entry = it.next();
                if (!titleMapping.containsValue(entry.getKey())) {
                    throw new RuntimeException("标签页：" + sheetName + "，列名为'" + entry.getKey() + "'不存在，请检查下");
                }
            }
        }

        /**
         * Captures characters only if a suitable element is open.
         * Originally was just "v"; extended for inlineStr also.
         */
        @Override
        public void characters(char[] ch, int start, int length) {
            if (vIsOpen) {
                value.append(ch, start, length);
            }
        }

    }
}
