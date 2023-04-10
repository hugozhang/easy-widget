package me.about.bean;

import me.about.widget.excel.reader.XlsxReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/08/23 16:23
 * @Description:
 */
public class ReadDimDiseInfoB {

    @Test
    public void reader() throws Exception {
        StringBuilder s = new StringBuilder();

        for (int i = 0;i < 10;i++) {
            s.append("'C0" + i);
            s.append("'");
            s.append(",");
        }
        for (int i = 10;i < 76;i++) {
            s.append("'C" + i);
            s.append("'");
            s.append(",");
        }
        s.deleteCharAt(s.length() - 1);
//        System.out.println(s.toString());


        List<DimDiseInfoB> rows = XlsxReader.build(DimDiseInfoB.class).skipRow(1).open(new FileInputStream("疾病数据整理.xlsx")).sheetsParser();
        StringBuilder buffer = new StringBuilder();
        String insertSQL = "INSERT INTO dim_dise_info_b(" +
                "disecode, disename, dise_type, initi, icd10_codg, codg_std, codg_val_defn, " +
                "defn_souc, epid_flag, cpr_codg,cpr_name)";

        for (DimDiseInfoB dimDiseInfoB : rows) {
            buffer.append(insertSQL);
            buffer.append(" VALUES (");
            Field[] declaredFields = dimDiseInfoB.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object value = field.get(dimDiseInfoB);
                if (value != null) {
                    buffer.append("'");
                    buffer.append(value);
                    buffer.append("'");
                } else {
                    buffer.append("''");
                }
                buffer.append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append(" ); \n");
        }
        System.out.println(buffer);

//        INSERT INTO dim_dise_info_b(disecode, disename, dise_type, initi, icd10_codg, codg_std, codg_val_defn, defn_souc, epid_flag, cpr_codg, cpr_name, rid, crter_id, crte_time, modier, updt_time)
//        VALUES ('110608', '帕金森综合症110050110608', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '863', 'jcxxopr', '2019-07-23 00:00:00.0', 'jcxxopr', '2019-07-23 00:00:00.0');


//        FileWriter write = new FileWriter("D:/change.sql", false);
//        write.write(buffer.toString());
//        write.close();
    }

}
