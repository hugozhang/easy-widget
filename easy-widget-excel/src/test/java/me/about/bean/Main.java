package me.about.bean;

import me.about.widget.excel.reader.XLSXReader;
import me.about.widget.excel.writer.XLSXWriter;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 0:01
 * @Description:
 */
public class Main {


    @Test
    public void postgresSQL() throws Exception {


        //genSQL("PAY_HIS_DIST", "支出分布历史数据");

        XLSXReader.build().open(new FileInputStream("中文.xlsx")).sheetParser(Column.class, (XLSXReader.SheetDone<Column>) (sheetName, rows) -> {
            String tableName = sheetName.split("-")[0];
            String tableComment = sheetName.split("-")[1];
            System.out.println(tableName);
            genSQL(tableName,tableComment,rows);
        });


    }

    private void genSQL(String tableName, String tableComment,List<Column> rows) throws Exception {
        //List<Column> rows = XLSXReader.builder().skipRow(1).open(new FileInputStream("中文.xlsx")).parseArray(Column.class);
        StringBuilder buffer = new StringBuilder();
        buffer.append("drop table " + tableName +";\n");
        buffer.append("create table " + tableName + " (\n");
        buffer.append("id     INT8     SERIAL         not null,\n");

        for (Column column : rows) {
            buffer.append(column.getColumnId());
            buffer.append("                  ");
            if (column.getColumnType().contains("string")) {
                buffer.append("VARCHAR(45)");
            }
            if (column.getColumnType().contains("float")) {
                buffer.append("FLOAT8");
            }
            if (column.getColumnType().contains("int")) {
                buffer.append("INT8");
            }
            if (column.getColumnType().contains("decimal")) {
                buffer.append("numeric(20,4)");
            }
            buffer.append("        " + "not null,\n");
        }
        buffer.append("constraint PK_" + tableName + " primary key (id)\n");
        buffer.append(");\n");
        buffer.append("comment on table " + tableName + " is '" + tableComment + "';\n");
        for (Column column : rows) {
            buffer.append("comment on column " + tableName +"." + column.getColumnId() + " is '" + column.getColumnName() + "';\n");
        }

        FileWriter write = new FileWriter("change.sql", true);
        write.write(buffer.toString() + "\n\n");
        write.close();
    }

    @Test
    public void reader() throws Exception {
        List<User> rows = XLSXReader.build().skipRow(1).open(new FileInputStream("中文.xlsx")).sheetsParser(User.class);
        StringBuilder buffer = new StringBuilder();
        for (User user : rows) {
            buffer.append(user.getAddress());
        }
        System.out.println(buffer);

//        FileWriter write = new FileWriter("D:/change.sql", false);
//        write.write(buffer.toString());
//        write.close();
    }

    @Test
    public void writer() throws Exception {
        List<User> list = new ArrayList();

        for (int i = 0; i < 10000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A" + i);
            u.setCompany("B"+i);
            u.setAddress("C" + i);
            u.setBirthday(new Date());
            u.setSalary(new BigDecimal(23.45));
            list.add(u);
        }
        Date s = new Date();
        System.out.println(s);
        FileOutputStream out = new FileOutputStream("中文.xlsx");
        XLSXWriter.build().toOutputStream(list, out);
        Date e = new Date();
        System.out.println(e);
        System.out.println("耗时:" + (e.getTime() - s.getTime()) / 1000);
        out.close();
    }
}
