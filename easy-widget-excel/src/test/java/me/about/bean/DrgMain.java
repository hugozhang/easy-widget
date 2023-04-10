package me.about.bean;

import me.about.widget.excel.reader.XlsxReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @auther: hugo.zxh
 * @date: 2022/07/13 11:05
 * @description:
 */
public class DrgMain {


    @Test
    public void reader2() throws Exception {

        List<DrgGroupReq> list = new ArrayList<>();
        List<DrgExcel> rows = XlsxReader.build(DrgExcel.class).skipRow(1)
                .open(new FileInputStream("遂宁2022年7月13重新跑数据结果对比(3).xlsx")).sheetParser(1);
//        List<User> rows = XLSXReader.build().skipRow(2)
//                .open(new FileInputStream("筹资增长率商业险影响分析导入模板.xlsx")).sheetsParser(User.class);
        StringBuilder buffer = new StringBuilder();
        int j = 0;
        for (DrgExcel excel : rows) {
            j++;
            DrgGroupReq req = new DrgGroupReq();
            req.setJbdm(excel.getMainZd());
            req.setSsjcz(excel.getMainSs());
            List<String> otherJbdmList = req.getOtherJbdmList();
            List<String> otherSsjczList = req.getOtherSsjczList();
            for (int i = 1; i < 21; i++) {
                Field field = excel.getClass().getDeclaredField("zd" + i);
                field.setAccessible(true);
                Object o = field.get(excel);
                if (o != null) {
                    otherJbdmList.add(o.toString());
                }
            }

            for (int i = 1; i < 10; i++) {
                Field fieldss = excel.getClass().getDeclaredField("ss" + i);
                fieldss.setAccessible(true);
                Object s = fieldss.get(excel);
                if (s != null) {
                    otherSsjczList.add(s.toString());
                }
            }


//        FileWriter write = new FileWriter("D:/change.sql", false);
//        write.write(buffer.toString());
//        write.close();
        }
    }
}
