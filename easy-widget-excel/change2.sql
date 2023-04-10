create table if not exists item_amt_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
ym          VARCHAR(6)        not null,
inscp_amt          numeric(20,4) DEFAULT 0.0000        not null,
hi_paymtd          VARCHAR(3)        not null,
medfee_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
psn_cnt          INT        not null,
fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
constraint PK_item_amt_d primary key (id)
);
comment on table item_amt_d is '按项目付费相关信息';
comment on column item_amt_d.admdvs is '区划';
comment on column item_amt_d.ym is '年份';
comment on column item_amt_d.inscp_amt is '医保覆盖费用';
comment on column item_amt_d.hi_paymtd is '付费方式
1 按项目
2 单病种
3 按病种分值
4 基本诊断相关分组（DRG）
5 按床日
6 按人次
9 其他
';
comment on column item_amt_d.medfee_sumamt is '医疗费用';
comment on column item_amt_d.psn_cnt is '项目付费人次';
comment on column item_amt_d.fund_pay_sumamt is '基金支付金额';


create table if not exists det_item_fee_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
ym          VARCHAR(6)        not null,
hi_paymtd          VARCHAR(3)        not null,
med_chrgitm_type          numeric(20,4) DEFAULT 0.0000        not null,
det_item_fee_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
constraint PK_det_item_fee_d primary key (id)
);
comment on table det_item_fee_d is '按项目付费费用构成';
comment on column det_item_fee_d.admdvs is '区划';
comment on column det_item_fee_d.ym is '年份';
comment on column det_item_fee_d.hi_paymtd is '付费方式
1 按项目
2 单病种
3 按病种分值
4 基本诊断相关分组（DRG）
5 按床日
6 按人次
9 其他
';
comment on column det_item_fee_d.med_chrgitm_type is '医疗收费项目类别';
comment on column det_item_fee_d.det_item_fee_sumamt is '明细项目费用总额';


create table if not exists drug_purc_used_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
ym          VARCHAR(6)        not null,
drug_genname          VARCHAR(50)        not null,
purc_retn_cnt          INT        not null,
psn_cnt          INT        not null,
constraint PK_drug_purc_used_d primary key (id)
);
comment on table drug_purc_used_d is '药品目录采购信息表';
comment on column drug_purc_used_d.admdvs is '区划';
comment on column drug_purc_used_d.ym is '年份';
comment on column drug_purc_used_d.drug_genname is '药品通用名';
comment on column drug_purc_used_d.purc_retn_cnt is '采购数量';
comment on column drug_purc_used_d.psn_cnt is '使用人数';


create table if not exists fct_his_mcs (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
generic_name          VARCHAR(255)        not null,
med_list_codg          VARCHAR(255)        not null,
mcs_name          VARCHAR(500)        not null,
highval_mcs_flag          VARCHAR(3)        not null,
mdtrt_way          VARCHAR(4)        not null,
medinslv          VARCHAR(4)        not null,
mdtrt_cnt          numeric(20,4) DEFAULT 0.0000        not null,
self_pay          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay          numeric(20,4) DEFAULT 0.0000        not null,
med_fee          numeric(20,4) DEFAULT 0.0000        not null,
usage_cnt          numeric(20,4) DEFAULT 0.0000        not null,
unit_price          numeric(20,4) DEFAULT 0.0000        not null,
constraint PK_fct_his_mcs primary key (id)
);
comment on table fct_his_mcs is '耗材使用历史统计表';
comment on column fct_his_mcs.admdvs is '医保区划';
comment on column fct_his_mcs.stt_year is '统计年份';
comment on column fct_his_mcs.generic_name is '耗材通用名';
comment on column fct_his_mcs.med_list_codg is '医疗目录编码';
comment on column fct_his_mcs.mcs_name is '耗材名称';
comment on column fct_his_mcs.highval_mcs_flag is '高值耗材标志 1 高值，0 低值';
comment on column fct_his_mcs.mdtrt_way is '就诊类型 ：1 门诊，2 住院 ，4 门慢门特';
comment on column fct_his_mcs.medinslv is '医疗机构等级:1 一级，2 二级，3 三级';
comment on column fct_his_mcs.mdtrt_cnt is '就诊人次';
comment on column fct_his_mcs.self_pay is '自付比例';
comment on column fct_his_mcs.fund_pay is '基金支出总额';
comment on column fct_his_mcs.med_fee is '医疗费用总额';
comment on column fct_his_mcs.usage_cnt is '使用数量';
comment on column fct_his_mcs.unit_price is '耗材单价';


create table if not exists mcs_schme_info (
id SERIAL NOT NULL,
scen_id          VARCHAR(40)        not null,
stt_year          VARCHAR(8)        not null,
adjus_mark          VARCHAR(3)        not null,
scen_nm          VARCHAR(40)        not null,
med_list_codg          VARCHAR(255)        not null,
hi_genname          VARCHAR(255)        not null,
mcs_name          VARCHAR(255)        not null,
prodentp_name          VARCHAR(100)        not null,
pacspec          VARCHAR(100)        not null,
pac_cnt          VARCHAR(50)        not null,
pacunt          VARCHAR(50)        not null,
min_useunt          VARCHAR(50)        not null,
used_cnt          numeric(20,4) DEFAULT 0.0000        not null,
mcs_type          VARCHAR(15)        not null,
selfpay_prop          numeric(20,4) DEFAULT 0.0000        not null,
lv3_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
lv2_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
lv1_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
constraint PK_mcs_schme_info primary key (id)
);
comment on table mcs_schme_info is '耗材方案信息表';
comment on column mcs_schme_info.scen_id is '方案ID';
comment on column mcs_schme_info.stt_year is '年度';
comment on column mcs_schme_info.adjus_mark is '调整情况 1 正常；2 调整；3 移除';
comment on column mcs_schme_info.scen_nm is '方案名称';
comment on column mcs_schme_info.med_list_codg is '医疗目录编码';
comment on column mcs_schme_info.hi_genname is '医保通用名';
comment on column mcs_schme_info.mcs_name is '耗材名称';
comment on column mcs_schme_info.prodentp_name is '生产企业名称';
comment on column mcs_schme_info.pacspec is '包装规格';
comment on column mcs_schme_info.pac_cnt is '包装数量';
comment on column mcs_schme_info.pacunt is '包装单位';
comment on column mcs_schme_info.min_useunt is '最小使用单位';
comment on column mcs_schme_info.used_cnt is '使用量';
comment on column mcs_schme_info.mcs_type is '耗材类型 1 高值耗材；0 低值耗材';
comment on column mcs_schme_info.selfpay_prop is '自付比例';
comment on column mcs_schme_info.lv3_pric_uplmt_amt is '三级医院限价';
comment on column mcs_schme_info.lv2_pric_uplmt_amt is '二级医院限价';
comment on column mcs_schme_info.lv1_pric_uplmt_amt is '一级医院限价';
comment on column mcs_schme_info.id is '唯一记录';
comment on column mcs_schme_info.admdvs is '区划';


create table if not exists mcs_schme_info_change (
id SERIAL NOT NULL,
scen_id          VARCHAR(40)        not null,
stt_year          VARCHAR(8)        not null,
adjus_mark          VARCHAR(3)        not null,
scen_nm          VARCHAR(40)        not null,
med_list_codg          VARCHAR(255)        not null,
hi_genname          VARCHAR(255)        not null,
mcs_name          VARCHAR(255)        not null,
prodentp_name          VARCHAR(100)        not null,
pacspec          VARCHAR(100)        not null,
pac_cnt          VARCHAR(50)        not null,
pacunt          VARCHAR(50)        not null,
min_useunt          VARCHAR(50)        not null,
used_cnt          numeric(20,4) DEFAULT 0.0000        not null,
mcs_type          VARCHAR(15)        not null,
selfpay_prop          numeric(20,4) DEFAULT 0.0000        not null,
lv3_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
lv2_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
lv1_pric_uplmt_amt          numeric(20,4) DEFAULT 0.0000        not null,
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_detail_id          INT        not null,
mcs_schme_info_id          INT        not null,
stt_type          VARCHAR(50)        not null,
constraint PK_mcs_schme_info_change primary key (id)
);
comment on table mcs_schme_info_change is '耗材方案变更详情表';
comment on column mcs_schme_info_change.scen_id is '方案ID';
comment on column mcs_schme_info_change.stt_year is '年度';
comment on column mcs_schme_info_change.adjus_mark is '调整情况 1 正常；2 调整；3 移除';
comment on column mcs_schme_info_change.scen_nm is '方案名称';
comment on column mcs_schme_info_change.med_list_codg is '医疗目录编码';
comment on column mcs_schme_info_change.hi_genname is '医保通用名';
comment on column mcs_schme_info_change.mcs_name is '耗材名称';
comment on column mcs_schme_info_change.prodentp_name is '生产企业名称';
comment on column mcs_schme_info_change.pacspec is '包装规格';
comment on column mcs_schme_info_change.pac_cnt is '包装数量';
comment on column mcs_schme_info_change.pacunt is '包装单位';
comment on column mcs_schme_info_change.min_useunt is '最小使用单位';
comment on column mcs_schme_info_change.used_cnt is '使用量';
comment on column mcs_schme_info_change.mcs_type is '耗材类型 1 高值耗材；0 低值耗材';
comment on column mcs_schme_info_change.selfpay_prop is '自付比例';
comment on column mcs_schme_info_change.lv3_pric_uplmt_amt is '三级医院限价';
comment on column mcs_schme_info_change.lv2_pric_uplmt_amt is '二级医院限价';
comment on column mcs_schme_info_change.lv1_pric_uplmt_amt is '一级医院限价';
comment on column mcs_schme_info_change.id is '唯一记录';
comment on column mcs_schme_info_change.admdvs is '区划';
comment on column mcs_schme_info_change.stt_detail_id is 'mcs_info_stt_d的主键id';
comment on column mcs_schme_info_change.mcs_schme_info_id is 'mcs_schme_info表的主键id;';
comment on column mcs_schme_info_change.stt_type is '分析类型 ;1 全部医保目录耗材;2 指定医保目录耗材';


create table if not exists mcs_info_stt_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_dt          TIMESTAMP        not null,
stt_status          INT        not null,
stt_type          VARCHAR(50)        not null,
stt_finsh_time          TIMESTAMP        not null,
del_flag          VARCHAR(3)        not null,
id SERIAL NOT NULL,
hi_genname          VARCHAR(255)        not null,
adjust          INT        not null,
remove          INT        not null,
scheme_id          VARCHAR(255)        not null,
constraint PK_mcs_info_stt_d primary key (id)
);
comment on table mcs_info_stt_d is '耗材变更分析主表';
comment on column mcs_info_stt_d.admdvs is '区划';
comment on column mcs_info_stt_d.stt_dt is '更变时间';
comment on column mcs_info_stt_d.stt_status is '分析进度';
comment on column mcs_info_stt_d.stt_type is '分析类型  1 全部医保目录耗材2 指定医保目录耗材';
comment on column mcs_info_stt_d.stt_finsh_time is '试算完成时间';
comment on column mcs_info_stt_d.del_flag is '删除标志；0 已删除；1 未删除';
comment on column mcs_info_stt_d.id is '唯一记录';
comment on column mcs_info_stt_d.hi_genname is '医保通用名';
comment on column mcs_info_stt_d.adjust is '调整数量';
comment on column mcs_info_stt_d.remove is '移除数量';
comment on column mcs_info_stt_d.scheme_id is '方案id';


create table if not exists mcs_info_rst_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
hi_genname          VARCHAR(255)        not null,
low_mcs_fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
high_mcs_fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
low_mcs_fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
high_mcs_fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
ipt_amount          numeric(20,4) DEFAULT 0.0000        not null,
opt_amount          numeric(20,4) DEFAULT 0.0000        not null,
mt_amount          numeric(20,4) DEFAULT 0.0000        not null,
ipt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
opt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
mt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
stt_detail_id          INT        not null,
id SERIAL NOT NULL,
scheme_id          VARCHAR(255)        not null,
constraint PK_mcs_info_rst_d primary key (id)
);
comment on table mcs_info_rst_d is '耗材变更分析结果表';
comment on column mcs_info_rst_d.admdvs is '地区';
comment on column mcs_info_rst_d.stt_year is '年份';
comment on column mcs_info_rst_d.hi_genname is '医保通用名';
comment on column mcs_info_rst_d.low_mcs_fund_pay_sumamt is '调整前低值耗材基金支出金额';
comment on column mcs_info_rst_d.high_mcs_fund_pay_sumamt is '调整前高值耗材基金支出金额';
comment on column mcs_info_rst_d.fund_pay_sumamt is '调整前基金支出总额';
comment on column mcs_info_rst_d.low_mcs_fund_pay_sumamt_adj is '调整后低值耗材基金支出金额i';
comment on column mcs_info_rst_d.high_mcs_fund_pay_sumamt_adj is '调整后高值耗材基金支出金额';
comment on column mcs_info_rst_d.fund_pay_sumamt_adj is '调整后基金支出总额';
comment on column mcs_info_rst_d.ipt_amount is '调整前门诊个人负担';
comment on column mcs_info_rst_d.opt_amount is '调整前住院个人负担';
comment on column mcs_info_rst_d.mt_amount is '调整前门慢门特个人负担';
comment on column mcs_info_rst_d.ipt_amount_adj is '调整后门诊个人负担';
comment on column mcs_info_rst_d.opt_amount_adj is '调整后住院个人负担';
comment on column mcs_info_rst_d.mt_amount_adj is '调整后门慢门特个人负担';
comment on column mcs_info_rst_d.stt_detail_id is 'mcs_info_stt_d的主键id';
comment on column mcs_info_rst_d.id is '唯一记录';
comment on column mcs_info_rst_d.scheme_id is '方案id';


create table if not exists mcs_info_rst_d_std (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
hi_genname          VARCHAR(255)        not null,
low_mcs_fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
high_mcs_fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
low_mcs_fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
high_mcs_fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay_sumamt_adj          numeric(20,4) DEFAULT 0.0000        not null,
ipt_amount          numeric(20,4) DEFAULT 0.0000        not null,
opt_amount          numeric(20,4) DEFAULT 0.0000        not null,
mt_amount          numeric(20,4) DEFAULT 0.0000        not null,
ipt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
opt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
mt_amount_adj          numeric(20,4) DEFAULT 0.0000        not null,
id SERIAL NOT NULL,
scheme_id          VARCHAR(255)        not null,
constraint PK_mcs_info_rst_d_std primary key (id)
);
comment on table mcs_info_rst_d_std is '标准方案分析结果表';
comment on column mcs_info_rst_d_std.admdvs is '地区';
comment on column mcs_info_rst_d_std.stt_year is '年份';
comment on column mcs_info_rst_d_std.hi_genname is '医保通用名';
comment on column mcs_info_rst_d_std.low_mcs_fund_pay_sumamt is '调整前低值耗材基金支出金额';
comment on column mcs_info_rst_d_std.high_mcs_fund_pay_sumamt is '调整前高值耗材基金支出金额';
comment on column mcs_info_rst_d_std.fund_pay_sumamt is '调整前基金支出总额';
comment on column mcs_info_rst_d_std.low_mcs_fund_pay_sumamt_adj is '调整后低值耗材基金支出金额i';
comment on column mcs_info_rst_d_std.high_mcs_fund_pay_sumamt_adj is '调整后高值耗材基金支出金额';
comment on column mcs_info_rst_d_std.fund_pay_sumamt_adj is '调整后基金支出总额';
comment on column mcs_info_rst_d_std.ipt_amount is '调整前门诊个人负担';
comment on column mcs_info_rst_d_std.opt_amount is '调整前住院个人负担';
comment on column mcs_info_rst_d_std.mt_amount is '调整前门慢门特个人负担';
comment on column mcs_info_rst_d_std.ipt_amount_adj is '调整后门诊个人负担';
comment on column mcs_info_rst_d_std.opt_amount_adj is '调整后住院个人负担';
comment on column mcs_info_rst_d_std.mt_amount_adj is '调整后门慢门特个人负担';
comment on column mcs_info_rst_d_std.id is '唯一记录';
comment on column mcs_info_rst_d_std.scheme_id is '方案id';


create table if not exists mcs_info_medins_d (
id SERIAL NOT NULL,
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
med_list_codg          VARCHAR(255)        not null,
mcs_name          VARCHAR(255)        not null,
hosp_lv          INT        not null,
hosp_cnt          INT        not null,
drug_user_num          INT        not null,
fund_pay          numeric(20,4) DEFAULT 0.0000        not null,
psn_part_amt          numeric(20,4) DEFAULT 0.0000        not null,
first_low_med_ins          numeric(20,4) DEFAULT 0.0000        not null,
constraint PK_mcs_info_medins_d primary key (id)
);
comment on table mcs_info_medins_d is '耗材用量(医疗机构)';
comment on column mcs_info_medins_d.id is '唯一记录';
comment on column mcs_info_medins_d.admdvs is '区划';
comment on column mcs_info_medins_d.stt_year is '年份';
comment on column mcs_info_medins_d.med_list_codg is '医疗目录编码';
comment on column mcs_info_medins_d.mcs_name is '耗材名称';
comment on column mcs_info_medins_d.hosp_lv is '医疗机等级1  一级医疗机构2  二级医疗机构3  三级医疗机构';
comment on column mcs_info_medins_d.hosp_cnt is '医疗机构用量';
comment on column mcs_info_medins_d.drug_user_num is '使用人数';
comment on column mcs_info_medins_d.fund_pay is '医保基金支出';
comment on column mcs_info_medins_d.psn_part_amt is '个人负担金额';
comment on column mcs_info_medins_d.first_low_med_ins is '平均单价';


create table if not exists mcs_info_age_sec_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
med_list_codg          VARCHAR(255)        not null,
mcs_name          VARCHAR(255)        not null,
age_sec          INT        not null,
used_cnt          INT        not null,
id SERIAL NOT NULL,
constraint PK_mcs_info_age_sec_d primary key (id)
);
comment on table mcs_info_age_sec_d is '耗材用量(年龄段)';
comment on column mcs_info_age_sec_d.admdvs is '区划';
comment on column mcs_info_age_sec_d.stt_year is '年份';
comment on column mcs_info_age_sec_d.med_list_codg is '医疗目录编码';
comment on column mcs_info_age_sec_d.mcs_name is '耗材名称';
comment on column mcs_info_age_sec_d.age_sec is '年龄段
1 0~18周岁
2 18~35周岁
3 35~45周岁
4 45~60周岁
5 60周岁以上
';
comment on column mcs_info_age_sec_d.used_cnt is '使用量';
comment on column mcs_info_age_sec_d.id is '唯一记录';


create table if not exists mcs_info_psn_d (
id SERIAL NOT NULL,
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
med_list_codg          VARCHAR(255)        not null,
mcs_name          VARCHAR(255)        not null,
psn_type          INT        not null,
used_cnt          INT        not null,
constraint PK_mcs_info_psn_d primary key (id)
);
comment on table mcs_info_psn_d is '耗材用量(人员类别)';
comment on column mcs_info_psn_d.id is '唯一记录';
comment on column mcs_info_psn_d.admdvs is '区划';
comment on column mcs_info_psn_d.stt_year is '年份';
comment on column mcs_info_psn_d.med_list_codg is '医疗目录编码';
comment on column mcs_info_psn_d.mcs_name is '耗材名称';
comment on column mcs_info_psn_d.psn_type is '人员类别';
comment on column mcs_info_psn_d.used_cnt is '使用量';


create table if not exists servitem_pric_mnit_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
stt_year          VARCHAR(4)        not null,
servitem_type          VARCHAR(3)        not null,
cplt_val_cnt          INT        not null,
constraint PK_servitem_pric_mnit_d primary key (id)
);
comment on table servitem_pric_mnit_d is '医疗服务价格调整表';
comment on column servitem_pric_mnit_d.admdvs is '区划';
comment on column servitem_pric_mnit_d.stt_year is '年份';
comment on column servitem_pric_mnit_d.servitem_type is '服务项目类别
1001 综合医疗服务类
1002 医技诊疗类
1003 临床诊疗类
1004 中医及民族医诊疗类
';
comment on column servitem_pric_mnit_d.cplt_val_cnt is '调价完成数量';


create table if not exists setl_medfee_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
ym          VARCHAR(6)        not null,
fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
medfee_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
hifp_pay          numeric(20,4) DEFAULT 0.0000        not null,
psn_type          VARCHAR(4)        not null,
constraint PK_setl_medfee_d primary key (id)
);
comment on table setl_medfee_d is '结算表';
comment on column setl_medfee_d.admdvs is '区划';
comment on column setl_medfee_d.ym is '年月（yyyymm）';
comment on column setl_medfee_d.fund_pay_sumamt is '基金支出';
comment on column setl_medfee_d.medfee_sumamt is '医疗总费用';
comment on column setl_medfee_d.hifp_pay is '统筹基金支出';
comment on column setl_medfee_d.psn_type is '人员类别';


create table if not exists setl_mate_insur_d (
id SERIAL NOT NULL,
admdvs          VARCHAR(6)        not null,
ym          VARCHAR(4)        not null,
insu_type          VARCHAR(3)        not null,
hi_paymtd          VARCHAR(3)        not null,
medins          VARCHAR(50)        not null,
psn_type          VARCHAR(3)        not null,
medfee_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
fund_pay_sumamt          numeric(20,4) DEFAULT 0.0000        not null,
mdtrt_psn_cnt          INT        not null,
setl_psn_cnt          INT        not null,
constraint PK_setl_mate_insur_d primary key (id)
);
comment on table setl_mate_insur_d is '支付政策关联对比';
comment on column setl_mate_insur_d.admdvs is '区划';
comment on column setl_mate_insur_d.ym is '年份';
comment on column setl_mate_insur_d.insu_type is '险种
1 基本医疗保险
2 生育险
';
comment on column setl_mate_insur_d.hi_paymtd is '医保支付方式
1 按项目
2 单病种
3 按病种分值
4 基本诊断相关分组（DRG）
5 按床日
6 按人次
9 其他
';
comment on column setl_mate_insur_d.medins is '医疗机构';
comment on column setl_mate_insur_d.psn_type is '人员类别';
comment on column setl_mate_insur_d.medfee_sumamt is '医疗费用';
comment on column setl_mate_insur_d.fund_pay_sumamt is '基金支出金额';
comment on column setl_mate_insur_d.mdtrt_psn_cnt is '就诊人次';
comment on column setl_mate_insur_d.setl_psn_cnt is '结算人次';


