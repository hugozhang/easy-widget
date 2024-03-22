package me.about.bean;

import me.about.widget.excel.reader.XlsxReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;

public class ZDMain {

    @Test
    public void ntly() throws Exception {
        XlsxReader.build(Zd.class)
                .open(new FileInputStream("国籍.xlsx"))
                .sheetParser((XlsxReader.SheetDone<Zd>) (sheetName, rows) -> {
//            String tableName = sheetName.split("-")[0];
//            String tableComment = sheetName.split("-")[1];
//            System.out.println(tableName);
            genNtlySQL(rows);
        });
    }

    @Test
    public void dept() throws Exception {
        XlsxReader.build(Zd.class)
                .open(new FileInputStream("科室代码.xlsx"))
                .sheetParser((XlsxReader.SheetDone<Zd>) (sheetName, rows) -> {
//            String tableName = sheetName.split("-")[0];
//            String tableComment = sheetName.split("-")[1];
//            System.out.println(tableName);
                    genNtlySQL(rows);
                });
    }


    private void genNtlySQL(List<Zd> rows) {
         StringBuilder sb = new StringBuilder();
         sb.append("INSERT INTO `s_sys_zd_tbl`(`ZD_LX`, `ZD_LX_MC`, `ZD_SX`, `ZD_MC`, `ZD_Z`, `YS_Z`, `YS_BS`, `FL_BS`, `BZ`, `CR_SJ`, `CR_ID`, `XG_SJ`, `XG_ID`, `SC_BS`, `pid`) \n");
         int i = 0;
         for (Zd row : rows) {
             if (i == 0) {
                 sb.append("VALUES ('"+ row.getZD_LX() +"', '" + row.getZD_LX_MC() +"', '" + i +"', '" + row.getZD_MC() + "', '" + row.getZD_Z() + "', NULL, '0', '0', NULL, '2022-04-29 10:19:52', 'admin', '2022-05-02 13:47:32', 'admin', '0', 2491),\n");

             } else if (i == rows.size() -1 ) {
                 sb.append("('"+ row.getZD_LX() +"', '" + row.getZD_LX_MC() +"', '" + i +"', '" + row.getZD_MC() + "', '" + row.getZD_Z() + "', NULL, '0', '0', NULL, '2022-04-29 10:19:52', 'admin', '2022-05-02 13:47:32', 'admin', '0', 2491);\n");
             } else {
                 sb.append("('"+ row.getZD_LX() +"', '" + row.getZD_LX_MC() +"', '" + i +"', '" + row.getZD_MC() + "', '" + row.getZD_Z() + "', NULL, '0', '0', NULL, '2022-04-29 10:19:52', 'admin', '2022-05-02 13:47:32', 'admin', '0', 2491),\n");
             }
             i++;
         }

         System.out.println(sb.toString());
    }

}
