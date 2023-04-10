drop table MDTRT_D_ETL;
create table if not exists MDTRT_D_ETL(
id bigint comment "自增ID",
mdtrt_id string comment "就诊ID",
mdtrt_time timestamp comment "就诊时间",
mdtrt_year int comment "年份",
psn_no string comment "人员ID",
conti_clct_month int comment "连续缴费月数",
acc_clct_month int comment "累计缴费月数",
med_type int comment "就诊类型",
hosp_lv int comment "机构等级",
inscp_amt decimal(20,4) comment "政策内费用",
acct_pay decimal(20,4) comment "个人账户支付金额"
)
comment "数据中台就诊信息表抽取"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH_RS_TMP;
create table if not exists MDTRT_D_ETL_SCH_RS_TMP(
id bigint comment "ID",
mdtrt_id string comment "就诊ID",
mdtrt_year int comment "年份",
mdtrt_time timestamp comment "就诊时间",
psn_no string comment "人员ID",
med_type int comment "就诊类型",
hosp_lv int comment "机构等级",
inscp_amt decimal(20,4) comment "政策内费用",
acct_pay decimal(20,4) comment "个人账户支付金额",
med_seq int comment "就诊序号",
acc_inscp_amt decimal(20,4) comment "就诊累计政策内费用",
excl_dedc_inscp_amt decimal(20,4) comment "排除起付后政策内费用",
basic_fund_pay decimal(20,4) comment "基本医保统筹基金支出"
)
comment "方案试算结果临时"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH_RS;
create table if not exists MDTRT_D_ETL_SCH_RS(
id bigint comment "ID",
mdtrt_id string comment "就诊ID",
mdtrt_year int comment "年份",
mdtrt_time timestamp comment "就诊时间",
psn_no string comment "人员ID",
med_type int comment "就诊类型",
hosp_lv int comment "机构等级",
inscp_amt decimal(20,4) comment "政策内费用",
acct_pay decimal(20,4) comment "个人账户支付金额",
med_seq int comment "就诊序号",
acc_inscp_amt decimal(20,4) comment "就诊累计政策内费用",
excl_dedc_inscp_amt decimal(20,4) comment "排除起付后政策内费用",
basic_fund_pay decimal(20,4) comment "基本医保统筹基金支出"
)
comment "方案试算结果表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH_SUM1;
create table if not exists MDTRT_D_ETL_SCH_SUM1(
id bigint comment "ID",
mdtrt_year int comment "年份",
psn_no string comment "人员ID",
med_type int comment "就诊类型",
basic_fund_pay decimal(20,4) comment "基本医保统筹基金支出",
inscp_amt decimal(20,4) comment "政策内费用",
acct_pay decimal(20,4) comment "个人账户支付金额",
excl_upper_fund_pay decimal(20,4) comment "排除封顶统筹支付",
create_at timestamp comment "创建时间",
create_by string comment "创建人"
)
comment "方案试算汇总表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH_SUM2;
create table if not exists MDTRT_D_ETL_SCH_SUM2(
id bigint comment "ID",
mdtrt_year  int comment "年份",
psn_no string comment "人员ID",
basic_fund_pay decimal(20,4) comment "基本医保统筹基金支出",
upper_owner_pay decimal(20,4) comment "封顶线上自付",
owner_pay decimal(20,4) comment "总自付金额",
acct_pay decimal(20,4) comment "个账支付金额",
create_at timestamp comment "创建时间",
create_by string comment "创建人"
)
comment "方案试算汇总表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH_SUM3;
create table if not exists MDTRT_D_ETL_SCH_SUM3(
id bigint comment "ID",
mdtrt_year  int comment "年份",
fund_pay decimal(20,4) comment "基金支付金额",
create_at timestamp comment "创建时间",
create_by string comment "创建人"
)
comment "方案试算汇总表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_SCH;
create table if not exists MDTRT_D_ETL_SCH(
id bigint comment "ID",
page_id string comment "页面ID",
sch_name string comment "方案名字",
is_std int comment "是否标准",
sch_conf string comment "方案内容",
owner_at string comment "方案归属人",
is_delete int comment "是否删除",
is_share int comment "是否共享(省级下发的)",
create_at timestamp comment "创建时间",
create_by string comment "创建人",
update_at timestamp comment "更新时间",
update_by string comment "更新人"
)
comment "基本医疗保障方案表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table MDTRT_D_ETL_PROV_SCH;
create table if not exists MDTRT_D_ETL_PROV_SCH(
id bigint comment "ID",
page_id string comment "页面ID",
sch_name string comment "省级方案名字",
link_sch_ids string comment "关联市级方案主键ID(英文逗号分隔开)",
owner_at string comment "方案归属人",
is_std int comment "是否标准",
is_delete int comment "是否删除",
create_at timestamp comment "创建时间",
create_by string comment "创建人",
update_at timestamp comment "更新时间",
update_by string comment "更新人"
)
comment "基本医疗保障方案表"
partitioned by (sch_id string,psn_type int,admdvs string)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


drop table POL_DRUG_CON_INFO;
create table if not exists POL_DRUG_CON_INFO(
admdvs DATE comment "医保区划 市级",
stt_mon DATE comment "统计月份",
medinslv string comment "医疗机构等级",
medins_code string comment "医疗机构代码",
medins_name string comment "医疗机构名称",
list_type string comment "项目类型 1：药品  2：耗材",
hilist_code string comment "医保目录编码",
hilist_name string comment "医保目录名称",
medfee_sumamt decimal(20,4) comment "医疗总费用",
fund_pay decimal(20,4) comment "医保基金支出",
psn_part_amt decimal(20,4) comment "个人负担金额"
)
comment "药品耗材分析表"
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;


