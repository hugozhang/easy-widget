package me.about.bean;

import me.about.widget.excel.reader.XlsxReader;
import me.about.widget.excel.writer.XlsxWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("main");
    }

    private void genHiveSQL(String tableName, String tableComment,List<Column> rows) throws Exception {
        StringBuilder buffer = new StringBuilder();
        buffer.append("drop table " + tableName + ";\n");
        buffer.append("create table if not exists ");
        buffer.append(tableName);
        buffer.append("(\n");

        for (Column column : rows) {
            if (column.getColumnId().toLowerCase().equals("sch_id")
              || column.getColumnId().toLowerCase().equals("psn_type") || column.getColumnId().toLowerCase().equals("admdvs")) {
               continue;
            }
            buffer.append(column.getColumnId().toLowerCase() + " " + column.getColumnHiveType() + " comment \"" + column.getColumnName().trim() + "\",\n");
        }

        buffer.deleteCharAt(buffer.length() - 2);

        buffer.append(")\n");

        buffer.append("comment ");
        buffer.append("\"");
        buffer.append(tableComment);
        buffer.append("\"");
        buffer.append("\n");

        buffer.append("partitioned by (sch_id string,psn_type int,admdvs string)");
        buffer.append("\n");
        buffer.append("row format delimited");
        buffer.append("\n");
        buffer.append("fields terminated by ','");
        buffer.append("\n");
        buffer.append("lines terminated by '\\n'");
        buffer.append("\n");
        buffer.append("stored as textfile");
        buffer.append(";\n");

        FileWriter write = new FileWriter("hive-change.sql", true);
        write.write(buffer.toString() + "\n\n");
        write.close();
    }



    public void label_for_disease() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("create table if not exists ");
        buffer.append("label_for_disease");
        buffer.append("(\n");
        buffer.append("id SERIAL primary key NOT NULL,");
        buffer.append("\n");
        buffer.append("insu_admdvs varchar(6) NOT NULL,");
        buffer.append("\n");
        buffer.append("dept_feature_disease varchar(255) NOT NULL,");
        buffer.append("\n");
        buffer.append("psn_cnt int NOT NULL,");
        buffer.append("\n");
        buffer.append("biz_date date NOT NULL,");
        buffer.append("\n");
        buffer.append("create_time timestamp(6) NOT NULL");
        buffer.append(");\n");

        buffer.append("comment on table label_for_disease is '门诊特病人群';");
        buffer.append("\n");
        buffer.append("comment on column label_for_disease.dept_feature_disease is '门诊特病';");
        buffer.append("\n");
        buffer.append("comment on column label_for_disease.psn_cnt is '人数';");
        buffer.append("\n");
        buffer.append("comment on column label_for_disease.biz_date is '业务时间';");


        System.out.println(buffer.toString());
    }

    public void label_for_disease_x() {
        String[] attrs = new String[]{"gend","mrg_stas","hsreg_addr","insu_admdvs","insutype","educ","disease","dept_mdtrt_cnt","hosp_cnt","mdtrt_cnt","dept_avg_hifp_pay","dept_avg_medfee","hosp_avg_hifp_pay","hosp_avg_medfee","medfee_sumamt","dept_avg_psn_pay","hosp_avg_psn_pay","mdtrt_doctor_hobby","mdtrt_dept_hobby","mdtrt_hosp_hobby"};
        String[] mappings = new String[]{"性别","婚姻状况","户口所在地","参保地","参保类型","医疗救助对象","学历","疾病","门诊就诊次数","住院次数","就诊次数","门诊次均基金支出","门诊次均医疗费用","住院次均基金支出","住院次均医疗费用","医疗总费用","门诊次均个人负担","住院次均个人负担","就诊医生偏好","就诊科室偏好","就诊医院偏好"};

        for (int i = 0 ,len = attrs.length; i < len ; i ++) {
            String attr = attrs[i];
            String map = mappings[i];
            StringBuilder buffer = new StringBuilder();
            buffer.append("create table if not exists ");
            buffer.append("label_for_disease_"+attr);
            buffer.append("(\n");
            buffer.append("id SERIAL primary key NOT NULL,");
            buffer.append("\n");
            buffer.append("insu_admdvs varchar(6) NOT NULL,");
            buffer.append("\n");
            buffer.append("dept_feature_disease varchar(255) NOT NULL,");
            buffer.append("\n");
            buffer.append(attr + " varchar(255) NOT NULL,");
            buffer.append("\n");
            buffer.append("psn_cnt int NOT NULL,");
            buffer.append("\n");
            buffer.append("biz_date date NOT NULL,");
            buffer.append("\n");
            buffer.append("create_time timestamp(6) NOT NULL");
            buffer.append(");\n");

            buffer.append("comment on table label_for_mat_idet_" + attr + " is '门慢门特群体画像标签动态表-" + map +  "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_mat_idet_" + attr + "." + attr + " is '" + map + "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_disease_" +attr + ".psn_cnt is '人数';");
            buffer.append("\n");
            buffer.append("comment on column label_for_disease_" +attr + ".biz_date is '业务时间';");
            System.out.println(buffer.toString());
        }
    }


    public void label_for_mat_idet() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("create table if not exists ");
        buffer.append("label_for_mat_idet");
        buffer.append("(\n");
        buffer.append("id SERIAL primary key NOT NULL,");
        buffer.append("\n");
        buffer.append("insu_admdvs varchar(6) NOT NULL,");
        buffer.append("\n");
        buffer.append("mat_idet varchar(255) NOT NULL,");
        buffer.append("\n");
        buffer.append("psn_cnt int NOT NULL,");
        buffer.append("\n");
        buffer.append("biz_date date NOT NULL,");
        buffer.append("\n");
        buffer.append("create_time timestamp(6) NOT NULL");
        buffer.append(");\n");

        buffer.append("comment on table label_for_mat_idet is '医疗救助人群';");
        buffer.append("\n");
        buffer.append("comment on column label_for_mat_idet.mat_idet is '医疗救助';");
        buffer.append("\n");
        buffer.append("comment on column label_for_mat_idet.psn_cnt is '人数';");
        buffer.append("\n");
        buffer.append("comment on column label_for_mat_idet.biz_date is '业务时间';");

        System.out.println(buffer.toString());
    }

    public void label_for_mat_idet_x() {
        String[] attrs = new String[]{"gend","mrg_stas","hsreg_addr","insu_admdvs","insutype","educ","disease","dept_feature_disease","dept_mdtrt_cnt","hosp_cnt","mdtrt_cnt","dept_avg_hifp_pay","dept_avg_medfee","hosp_avg_hifp_pay","hosp_avg_medfee","medfee_sumamt","dept_avg_psn_pay","hosp_avg_psn_pay","mdtrt_doctor_hobby","mdtrt_dept_hobby","mdtrt_hosp_hobby"};
        String[] mappings = new String[]{"性别","婚姻状况","户口所在地","参保地","参保类型","学历","疾病","门慢门特疾病","门诊就诊次数","住院次数","就诊次数","门诊次均基金支出","门诊次均医疗费用","住院次均基金支出","住院次均医疗费用","医疗总费用","门诊次均个人负担","住院次均个人负担","就诊医生偏好","就诊科室偏好","就诊医院偏好"};

        for (int i = 0 ,len = attrs.length; i < len ; i ++) {
            String attr = attrs[i];
            String map = mappings[i];
            StringBuilder buffer = new StringBuilder();
            buffer.append("create table if not exists ");
            buffer.append("label_for_mat_idet_"+attr);
            buffer.append("(\n");
            buffer.append("id SERIAL primary key NOT NULL,");
            buffer.append("\n");
            buffer.append("insu_admdvs varchar(6) NOT NULL,");
            buffer.append("\n");
            buffer.append("mat_idet varchar(255) NOT NULL,");
            buffer.append("\n");
            buffer.append(attr + " varchar(255) NOT NULL,");
            buffer.append("\n");
            buffer.append("psn_cnt int NOT NULL,");
            buffer.append("\n");
            buffer.append("biz_date date NOT NULL,");
            buffer.append("\n");
            buffer.append("create_time timestamp(6) NOT NULL");
            buffer.append(");\n");

            buffer.append("comment on table label_for_mat_idet_" + attr + " is '救助群体画像标签动态表-" + map +  "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_mat_idet_" + attr + "." + attr + " is '" + map + "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_mat_idet_" + attr + ".psn_cnt is '人数';");
            buffer.append("\n");
            buffer.append("comment on column label_for_mat_idet_" + attr + ".biz_date is '业务时间';");
            System.out.println(buffer.toString());
        }
    }


    @Test
    public void label_for_psn() {
        label_for_disease();
        label_for_disease_x();

        label_for_medins();
        label_for_medins_x();

        label_for_mat_idet();
        label_for_mat_idet_x();
    }


    public void label_for_medins() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("create table if not exists ");
        buffer.append("label_for_medins");
        buffer.append("(\n");
        buffer.append("id SERIAL primary key NOT NULL,");
        buffer.append("\n");
        buffer.append("insu_admdvs varchar(6) NOT NULL,");
        buffer.append("\n");
        buffer.append("psn_time int NOT NULL,");
        buffer.append("\n");
        buffer.append("biz_date date NOT NULL,");
        buffer.append("\n");
        buffer.append("create_time timestamp(6) NOT NULL");
        buffer.append(");\n");

        buffer.append("comment on table label_for_medins is '医疗机构画像';");
        buffer.append("\n");
        buffer.append("comment on column label_for_medins.psn_time is '人次';");
        buffer.append("\n");
        buffer.append("comment on column label_for_medins.biz_date is '业务时间';");
        System.out.println(buffer.toString());

    }


    public void label_for_medins_x() {
        String[] attrs = new String[]{"medins_nature","medins_category","medins_lv","insu_admdvs","tcmherb_medins","tcmherb_proportion","tcmpat_proportion","tcmherb_treat_proportion","tcmherb_doctor_cnt","tcmherb_drug_cnt","tcmherb_dept_cnt","tcmpat_treat_proportion","avg_psn_pay","avg_dept_medfee","avg_hosp_medfee","avg_dept_hifp_pay","avg_hosp_hifp_pay","avg_medfee"};
        String[] mappings = new String[]{"医疗机构性质","医疗机构类别","医疗机构等级","医保区划","服务人次","中医服务机构","中药饮片结算占比","西药结算占比","中医治疗项目结算占比","中医师人数","中药师人数","中医类科室数","中医治疗项目结算占比","次均个人负担","门诊次均费用","住院次均费用","门诊次均基金支出","住院次均基金支出","次均医疗费用"};
        for (int i = 0 ,len = attrs.length; i < len ; i ++) {
            String attr = attrs[i];
            String map = mappings[i];

            StringBuilder buffer = new StringBuilder();
            buffer.append("create table if not exists ");
            buffer.append("label_for_medins_" + attr);
            buffer.append("(\n");
            buffer.append("id SERIAL primary key NOT NULL,");
            buffer.append("\n");
            buffer.append("insu_admdvs varchar(6) NOT NULL,");
            buffer.append("\n");
            buffer.append(attr + " varchar(255) NOT NULL,");
            buffer.append("\n");
            buffer.append("medins_cnt int NOT NULL,");
            buffer.append("\n");
            buffer.append("biz_date date NOT NULL,");
            buffer.append("\n");
            buffer.append("create_time timestamp(6) NOT NULL");
            buffer.append(");\n");


            buffer.append("comment on table label_for_mat_idet_" + attr + " is '机构画像标签动态表-" + map +  "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_medins_" + attr +"." + attr + " is '" + map + "';");
            buffer.append("\n");
            buffer.append("comment on column label_for_medins_" + attr +".medins_cnt is '机构数';");
            buffer.append("\n");
            buffer.append("comment on column label_for_medins_" + attr +".biz_date is '业务时间';");
            System.out.println(buffer.toString());
        }
    }

    @Test
    public void label_for_medins_a() {
        label_for_medins();
        label_for_medins_x();
    }




    @Test
    public void hiveSQL() throws Exception {
        XlsxReader.build(DimDiseInfoB.class).open(new FileInputStream("基本医疗保障分析.xlsx")).sheetParser((XlsxReader.SheetDone<Column>) (sheetName, rows) -> {
            String tableName = sheetName.split("-")[0];
            String tableComment = sheetName.split("-")[1];
            System.out.println(tableName);
            genHiveSQL(tableName,tableComment,rows);
        });
    }


    @Test
    public void postgresSQL1() throws Exception {

        XlsxReader.build(DimDiseInfoB.class).open(new FileInputStream("省立项2.xlsx")).sheetParser((XlsxReader.SheetDone<Column>) (sheetName, rows) -> {
            String tableName = sheetName.split("-")[0];
            String tableComment = sheetName.split("-")[1];
            System.out.println(tableName);
            genSQL(tableName,tableComment,rows);
        });

    }


    @Test
    public void postgresSQL2() throws Exception {

        XlsxReader.build(DimDiseInfoB.class).open(new FileInputStream("居民征缴.xlsx")).sheetParser((XlsxReader.SheetDone<Column>) (sheetName, rows) -> {
            String tableName = sheetName.split("-")[0];
            String tableComment = sheetName.split("-")[1];
            System.out.println(tableName);
            genSQL(tableName,tableComment,rows);
        });

    }


    @Test
    public void postgresSQL() throws Exception {


        //genSQL("PAY_HIS_DIST", "支出分布历史数据");

        XlsxReader.build(DimDiseInfoB.class).open(new FileInputStream("算法表2.xlsx")).sheetParser((XlsxReader.SheetDone<Column>) (sheetName, rows) -> {
            String tableName = sheetName.split("-")[0];
            String tableComment = sheetName.split("-")[1];
            System.out.println(tableName);
            genSQL(tableName,tableComment,rows);
        });


    }

    private void genSQL(String tableName, String tableComment,List<Column> rows) throws Exception {
        //List<Column> rows = XLSXReader.builder().skipRow(1).open(new FileInputStream("中文.xlsx")).parseArray(Column.class);

        StringBuilder buffer = new StringBuilder();

//        buffer.append("drop table if exists " + tableName + ";\n");
        buffer.append("create table if not exists " + tableName + " (\n");
        buffer.append("id SERIAL NOT NULL,\n");

        for (Column column : rows) {

            if (column.getColumnId().toLowerCase().equals("id")) {
                buffer.append("id SERIAL NOT NULL,\n");
            } else {
                buffer.append(column.getColumnId().toLowerCase());
                buffer.append("          ");
            }

            if (!column.getColumnId().toLowerCase().equals("id")) {
                if (column.getColumnType().toLowerCase().contains("string")) {
                    buffer.append("VARCHAR(45)");
                } else if (column.getColumnType().toLowerCase().contains("float")) {
                    buffer.append("float4 DEFAULT 0.00");
                }
                else if (column.getColumnType().toLowerCase().contains("decimal")) {
                    buffer.append("numeric(20,4) DEFAULT 0.0000");
                } else {
                    buffer.append(column.getColumnType().toUpperCase());
                }
                buffer.append("        " + "not null,\n");
            }
        }
        buffer.append("constraint PK_" + tableName + " primary key (id)\n");
        buffer.append(");\n");
        buffer.append("comment on table " + tableName + " is '" + tableComment + "';\n");
        for (Column column : rows) {
            buffer.append("comment on column " + tableName +"." + column.getColumnId() + " is '" + column.getColumnName() + "';\n");
        }

        FileWriter write = new FileWriter("change2.sql", true);
        write.write(buffer.toString() + "\n\n");
        write.close();
    }

    @Test
    public void reader() throws Exception {
        List<InsuranceExcel> rows = XlsxReader.build(InsuranceExcel.class).skipRow(2)
                .open(new FileInputStream("筹资增长率商业险影响分析导入模板.xlsx")).sheetParser(0);
//        List<User> rows = XLSXReader.build().skipRow(2)
//                .open(new FileInputStream("筹资增长率商业险影响分析导入模板.xlsx")).sheetsParser(User.class);
        StringBuilder buffer = new StringBuilder();
        for (InsuranceExcel excel : rows) {
            System.out.println(excel);
        }
        System.out.println(buffer);

//        FileWriter write = new FileWriter("D:/change.sql", false);
//        write.write(buffer.toString());
//        write.close();
    }


    @Test
    public void reader2() throws Exception {
        List<ProvinceInsuranceExcel> rows = XlsxReader.build(ProvinceInsuranceExcel.class).skipRow(3)
                .open(new FileInputStream("筹资增长率商业险影响分析导入模板.xlsx")).sheetParser(1);
//        List<User> rows = XLSXReader.build().skipRow(2)
//                .open(new FileInputStream("筹资增长率商业险影响分析导入模板.xlsx")).sheetsParser(User.class);
        StringBuilder buffer = new StringBuilder();
        for (ProvinceInsuranceExcel excel : rows) {
            System.out.println(excel);
        }
        System.out.println(buffer);

//        FileWriter write = new FileWriter("D:/change.sql", false);
//        write.write(buffer.toString());
//        write.close();
    }


    @Test
    public void writer() throws Exception {
        List<User> list = new ArrayList();

        for (int i = 0; i < 1000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A");
            u.setCompany("B"+i);
            u.setAddress("C" + i);
            u.setBirthday(new Date());
            if (i == 1) {
                 u.setSalary(new BigDecimal(10000000034.12345+""));
            } else if (i == 2) {
                u.setSalary(new BigDecimal(100056.8967+""));
            } else if (i == 3) {
                u.setSalary(new BigDecimal(-100000005464.12345+""));
            } else {
                u.setSalary(new BigDecimal(-1000464.12345+""));
            }
            list.add(u);
        }
        Date s = new Date();
        System.out.println(s);
        FileOutputStream out = new FileOutputStream("中文.xlsx");
        XlsxWriter.build(User.class).toOutputStream(list, out);
        Date e = new Date();
        System.out.println(e);
        System.out.println("耗时:" + (e.getTime() - s.getTime()) / 1000);
        out.close();
    }
}
