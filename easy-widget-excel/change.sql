drop table POP_HIS_PERMA;
create table POP_HIS_PERMA (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
PERMA_POP                  FLOAT8        not null,
constraint PK_POP_HIS_PERMA primary key (id)
);
comment on table POP_HIS_PERMA is '常住人口历史数据';
comment on column POP_HIS_PERMA.ADMDVS is '医保区划';
comment on column POP_HIS_PERMA.STT_YEAR is '统计年份';
comment on column POP_HIS_PERMA.AGE_SEC is '年龄段';
comment on column POP_HIS_PERMA.PERMA_POP is '常住人口数';


drop table POP_HIS_INSU;
create table POP_HIS_INSU (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
INSU_PSNCNT                  FLOAT8        not null,
constraint PK_POP_HIS_INSU primary key (id)
);
comment on table POP_HIS_INSU is '参保人口历史数据';
comment on column POP_HIS_INSU.ADMDVS is '医保区划';
comment on column POP_HIS_INSU.STT_YEAR is '统计年份';
comment on column POP_HIS_INSU.AGE_SEC is '年龄段';
comment on column POP_HIS_INSU.INSUTYPE is '险种类型';
comment on column POP_HIS_INSU.PSN_TYPE is '人员类别';
comment on column POP_HIS_INSU.RESD_NATU is '户口性质';
comment on column POP_HIS_INSU.INSU_PSNCNT is '参保人数';


drop table POP_HIS_STT;
create table POP_HIS_STT (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
FLOW_POP                  FLOAT8        not null,
MATN_RATE                  FLOAT8        not null,
DIE_RATE                  FLOAT8        not null,
constraint PK_POP_HIS_STT primary key (id)
);
comment on table POP_HIS_STT is '人口统计历史数据';
comment on column POP_HIS_STT.ADMDVS is '医保区划';
comment on column POP_HIS_STT.STT_YEAR is '统计年份';
comment on column POP_HIS_STT.FLOW_POP is '流动人口数';
comment on column POP_HIS_STT.MATN_RATE is '生育率';
comment on column POP_HIS_STT.DIE_RATE is '死亡率';


drop table POP_HIS_FLOW;
create table POP_HIS_FLOW (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
ADMDVS_SOUC                  VARCHAR(45)        not null,
NET_FLOW_PSNCNT                  FLOAT8        not null,
constraint PK_POP_HIS_FLOW primary key (id)
);
comment on table POP_HIS_FLOW is '人口流动历史数据';
comment on column POP_HIS_FLOW.ADMDVS is '医保区划';
comment on column POP_HIS_FLOW.STT_YEAR is '统计年份';
comment on column POP_HIS_FLOW.ADMDVS_SOUC is '迁移源';
comment on column POP_HIS_FLOW.NET_FLOW_PSNCNT is '净流入人数';


drop table POP_FORT_PERMA;
create table POP_FORT_PERMA (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
PERMA_POP                  FLOAT8        not null,
constraint PK_POP_FORT_PERMA primary key (id)
);
comment on table POP_FORT_PERMA is '常住人口预测结果';
comment on column POP_FORT_PERMA.ADMDVS is '医保区划';
comment on column POP_FORT_PERMA.STT_YEAR is '统计年份';
comment on column POP_FORT_PERMA.AGE_SEC is '年龄段';
comment on column POP_FORT_PERMA.PERMA_POP is '常住人口数';


drop table POP_FORT_INSU;
create table POP_FORT_INSU (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
INSU_PSNCNT                  FLOAT8        not null,
constraint PK_POP_FORT_INSU primary key (id)
);
comment on table POP_FORT_INSU is '参保人口预测结果';
comment on column POP_FORT_INSU.SCHM_ID is '方案ID';
comment on column POP_FORT_INSU.ADMDVS is '医保区划';
comment on column POP_FORT_INSU.STT_YEAR is '统计年份';
comment on column POP_FORT_INSU.AGE_SEC is '年龄段';
comment on column POP_FORT_INSU.INSUTYPE is '险种类型';
comment on column POP_FORT_INSU.PSN_TYPE is '人员类别';
comment on column POP_FORT_INSU.RESD_NATU is '户口性质';
comment on column POP_FORT_INSU.INSU_PSNCNT is '参保人数';


drop table POP_FORT_STT;
create table POP_FORT_STT (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
FLOW_POP                  FLOAT8        not null,
MATN_RATE                  FLOAT8        not null,
DIE_RATE                  FLOAT8        not null,
constraint PK_POP_FORT_STT primary key (id)
);
comment on table POP_FORT_STT is '人口统计预测结果';
comment on column POP_FORT_STT.ADMDVS is '医保区划';
comment on column POP_FORT_STT.STT_YEAR is '统计年份';
comment on column POP_FORT_STT.FLOW_POP is '流动人口数';
comment on column POP_FORT_STT.MATN_RATE is '生育率';
comment on column POP_FORT_STT.DIE_RATE is '死亡率';


drop table POP_FORT_FLOW;
create table POP_FORT_FLOW (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
ADMDVS_SOUC                  VARCHAR(45)        not null,
NET_FLOW_PSNCNT                  FLOAT8        not null,
constraint PK_POP_FORT_FLOW primary key (id)
);
comment on table POP_FORT_FLOW is '人口流动预测结果';
comment on column POP_FORT_FLOW.ADMDVS is '医保区划';
comment on column POP_FORT_FLOW.STT_YEAR is '统计年份';
comment on column POP_FORT_FLOW.ADMDVS_SOUC is '迁移源';
comment on column POP_FORT_FLOW.NET_FLOW_PSNCNT is '净流入人数';


drop table POP_FORT_FAC;
create table POP_FORT_FAC (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
INSU_RATE                  FLOAT8        not null,
constraint PK_POP_FORT_FAC primary key (id)
);
comment on table POP_FORT_FAC is '人口因子标准预测';
comment on column POP_FORT_FAC.SCHM_ID is '方案ID';
comment on column POP_FORT_FAC.ADMDVS is '医保区划';
comment on column POP_FORT_FAC.STT_YEAR is '统计年份';
comment on column POP_FORT_FAC.INSU_RATE is '参保率';


drop table FAC_HIS_STT;
create table FAC_HIS_STT (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PERPSN_GDP                  FLOAT8        not null,
INSU_RATE                  FLOAT8        not null,
SOCA_AVESAL                  FLOAT8        not null,
STAF_CLCT_STD                  FLOAT8        not null,
FLXEMPE_CLCT_STD                  FLOAT8        not null,
RSDT_CLCT_STD                  FLOAT8        not null,
STU_CLCT_STD                  FLOAT8        not null,
PERPSN_MED_PAY                  FLOAT8        not null,
STAF_PAY_STD                  FLOAT8        not null,
RETR_PAY_STD                  FLOAT8        not null,
FLXEMPE_PAY_STD                  FLOAT8        not null,
RSDT_PAY_STD                  FLOAT8        not null,
STU_PAY_STD                  FLOAT8        not null,
constraint PK_FAC_HIS_STT primary key (id)
);
comment on table FAC_HIS_STT is '影响因子历史数据';
comment on column FAC_HIS_STT.ADMDVS is '医保区划';
comment on column FAC_HIS_STT.STT_YEAR is '统计年份';
comment on column FAC_HIS_STT.PERPSN_GDP is '人均GDP';
comment on column FAC_HIS_STT.INSU_RATE is '参保率';
comment on column FAC_HIS_STT.SOCA_AVESAL is '社会平均工资';
comment on column FAC_HIS_STT.STAF_CLCT_STD is '职工筹资标准';
comment on column FAC_HIS_STT.FLXEMPE_CLCT_STD is '灵活筹资标准';
comment on column FAC_HIS_STT.RSDT_CLCT_STD is '居民筹资标准';
comment on column FAC_HIS_STT.STU_CLCT_STD is '学生筹资标准';
comment on column FAC_HIS_STT.PERPSN_MED_PAY is '人均医疗支出';
comment on column FAC_HIS_STT.STAF_PAY_STD is '在职支出标准';
comment on column FAC_HIS_STT.RETR_PAY_STD is '退休支出标准';
comment on column FAC_HIS_STT.FLXEMPE_PAY_STD is '灵活支出标准';
comment on column FAC_HIS_STT.RSDT_PAY_STD is '居民支出标准';
comment on column FAC_HIS_STT.STU_PAY_STD is '学生支出标准';


drop table FAC_FORT_USER;
create table FAC_FORT_USER (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PERPSN_GDP                  FLOAT8        not null,
INSU_RATE                  FLOAT8        not null,
SOCA_AVESAL                  FLOAT8        not null,
STAF_CLCT_STD                  FLOAT8        not null,
FLXEMPE_CLCT_STD                  FLOAT8        not null,
RSDT_CLCT_STD                  FLOAT8        not null,
STU_CLCT_STD                  FLOAT8        not null,
PERPSN_MED_PAY                  FLOAT8        not null,
STAF_PAY_STD                  FLOAT8        not null,
RETR_PAY_STD                  FLOAT8        not null,
FLXEMPE_PAY_STD                  FLOAT8        not null,
RSDT_PAY_STD                  FLOAT8        not null,
STU_PAY_STD                  FLOAT8        not null,
constraint PK_FAC_FORT_USER primary key (id)
);
comment on table FAC_FORT_USER is '影响因子用户定义';
comment on column FAC_FORT_USER.SCHM_ID is '方案ID';
comment on column FAC_FORT_USER.ADMDVS is '医保区划';
comment on column FAC_FORT_USER.STT_YEAR is '统计年份';
comment on column FAC_FORT_USER.PERPSN_GDP is '人均GDP';
comment on column FAC_FORT_USER.INSU_RATE is '参保率';
comment on column FAC_FORT_USER.SOCA_AVESAL is '社会平均工资';
comment on column FAC_FORT_USER.STAF_CLCT_STD is '职工筹资标准';
comment on column FAC_FORT_USER.FLXEMPE_CLCT_STD is '灵活筹资标准';
comment on column FAC_FORT_USER.RSDT_CLCT_STD is '居民筹资标准';
comment on column FAC_FORT_USER.STU_CLCT_STD is '学生筹资标准';
comment on column FAC_FORT_USER.PERPSN_MED_PAY is '人均医疗支出';
comment on column FAC_FORT_USER.STAF_PAY_STD is '在职支出标准';
comment on column FAC_FORT_USER.RETR_PAY_STD is '退休支出标准';
comment on column FAC_FORT_USER.FLXEMPE_PAY_STD is '灵活支出标准';
comment on column FAC_FORT_USER.RSDT_PAY_STD is '居民支出标准';
comment on column FAC_FORT_USER.STU_PAY_STD is '学生支出标准';


drop table FAC_FORT_STD;
create table FAC_FORT_STD (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PERPSN_GDP                  FLOAT8        not null,
INSU_RATE                  FLOAT8        not null,
SOCA_AVESAL                  FLOAT8        not null,
STAF_CLCT_STD                  FLOAT8        not null,
FLXEMPE_CLCT_STD                  FLOAT8        not null,
RSDT_CLCT_STD                  FLOAT8        not null,
STU_CLCT_STD                  FLOAT8        not null,
PERPSN_MED_PAY                  FLOAT8        not null,
STAF_PAY_STD                  FLOAT8        not null,
RETR_PAY_STD                  FLOAT8        not null,
FLXEMPE_PAY_STD                  FLOAT8        not null,
RSDT_PAY_STD                  FLOAT8        not null,
STU_PAY_STD                  FLOAT8        not null,
constraint PK_FAC_FORT_STD primary key (id)
);
comment on table FAC_FORT_STD is '影响因子标准预测';
comment on column FAC_FORT_STD.SCHM_ID is '方案ID';
comment on column FAC_FORT_STD.ADMDVS is '医保区划';
comment on column FAC_FORT_STD.STT_YEAR is '统计年份';
comment on column FAC_FORT_STD.PERPSN_GDP is '人均GDP';
comment on column FAC_FORT_STD.INSU_RATE is '参保率';
comment on column FAC_FORT_STD.SOCA_AVESAL is '社会平均工资';
comment on column FAC_FORT_STD.STAF_CLCT_STD is '职工筹资标准';
comment on column FAC_FORT_STD.FLXEMPE_CLCT_STD is '灵活筹资标准';
comment on column FAC_FORT_STD.RSDT_CLCT_STD is '居民筹资标准';
comment on column FAC_FORT_STD.STU_CLCT_STD is '学生筹资标准';
comment on column FAC_FORT_STD.PERPSN_MED_PAY is '人均医疗支出';
comment on column FAC_FORT_STD.STAF_PAY_STD is '在职支出标准';
comment on column FAC_FORT_STD.RETR_PAY_STD is '退休支出标准';
comment on column FAC_FORT_STD.FLXEMPE_PAY_STD is '灵活支出标准';
comment on column FAC_FORT_STD.RSDT_PAY_STD is '居民支出标准';
comment on column FAC_FORT_STD.STU_PAY_STD is '学生支出标准';


drop table FAC_FORT_ADJ;
create table FAC_FORT_ADJ (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
INSU_RATE                  FLOAT8        not null,
SOCA_AVESAL                  FLOAT8        not null,
STAF_CLCT_STD                  FLOAT8        not null,
FLXEMPE_CLCT_STD                  FLOAT8        not null,
RSDT_CLCT_STD                  FLOAT8        not null,
STU_CLCT_STD                  FLOAT8        not null,
PERPSN_MED_PAY                  FLOAT8        not null,
STAF_PAY_STD                  FLOAT8        not null,
RETR_PAY_STD                  FLOAT8        not null,
FLXEMPE_PAY_STD                  FLOAT8        not null,
RSDT_PAY_STD                  FLOAT8        not null,
STU_PAY_STD                  FLOAT8        not null,
constraint PK_FAC_FORT_ADJ primary key (id)
);
comment on table FAC_FORT_ADJ is '影响因子预测结果';
comment on column FAC_FORT_ADJ.SCHM_ID is '方案ID';
comment on column FAC_FORT_ADJ.ADMDVS is '医保区划';
comment on column FAC_FORT_ADJ.STT_YEAR is '统计年份';
comment on column FAC_FORT_ADJ.INSU_RATE is '参保率';
comment on column FAC_FORT_ADJ.SOCA_AVESAL is '社会平均工资';
comment on column FAC_FORT_ADJ.STAF_CLCT_STD is '职工筹资标准';
comment on column FAC_FORT_ADJ.FLXEMPE_CLCT_STD is '灵活筹资标准';
comment on column FAC_FORT_ADJ.RSDT_CLCT_STD is '居民筹资标准';
comment on column FAC_FORT_ADJ.STU_CLCT_STD is '学生筹资标准';
comment on column FAC_FORT_ADJ.PERPSN_MED_PAY is '人均医疗支出';
comment on column FAC_FORT_ADJ.STAF_PAY_STD is '在职支出标准';
comment on column FAC_FORT_ADJ.RETR_PAY_STD is '退休支出标准';
comment on column FAC_FORT_ADJ.FLXEMPE_PAY_STD is '灵活支出标准';
comment on column FAC_FORT_ADJ.RSDT_PAY_STD is '居民支出标准';
comment on column FAC_FORT_ADJ.STU_PAY_STD is '学生支出标准';


drop table INC_HIS_BASE;
create table INC_HIS_BASE (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
CLCT_PSNTIME                  FLOAT8        not null,
AVG_CLCTSTD                  FLOAT8        not null,
AVG_CLCT_AMT                  FLOAT8        not null,
constraint PK_INC_HIS_BASE primary key (id)
);
comment on table INC_HIS_BASE is '基金收入历史数据';
comment on column INC_HIS_BASE.ADMDVS is '医保区划';
comment on column INC_HIS_BASE.STT_YEAR is '统计年份';
comment on column INC_HIS_BASE.AGE_SEC is '年龄段';
comment on column INC_HIS_BASE.INSUTYPE is '险种类型';
comment on column INC_HIS_BASE.PSN_TYPE is '人员类别';
comment on column INC_HIS_BASE.RESD_NATU is '户口性质';
comment on column INC_HIS_BASE.CLCT_PSNTIME is '缴费人次';
comment on column INC_HIS_BASE.AVG_CLCTSTD is '次均缴费基数';
comment on column INC_HIS_BASE.AVG_CLCT_AMT is '次均缴费金额';


drop table INC_HIS_DIST;
create table INC_HIS_DIST (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
STAF_INC                  FLOAT8        not null,
STAF_PAY                  FLOAT8        not null,
constraint PK_INC_HIS_DIST primary key (id)
);
comment on table INC_HIS_DIST is '职工收入分布历史';
comment on column INC_HIS_DIST.ADMDVS is '医保区划';
comment on column INC_HIS_DIST.STT_YEAR is '统计年份';
comment on column INC_HIS_DIST.PRCT is '分位数';
comment on column INC_HIS_DIST.STAF_INC is '职工总工资收入';
comment on column INC_HIS_DIST.STAF_PAY is '职工总医疗支出';


drop table INC_HIS_CUM;
create table INC_HIS_CUM (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
FUND_CUM_PAY                  FLOAT8        not null,
FUND_CUM_INC                  FLOAT8        not null,
constraint PK_INC_HIS_CUM primary key (id)
);
comment on table INC_HIS_CUM is '基金累计收支历史';
comment on column INC_HIS_CUM.ADMDVS is '医保区划';
comment on column INC_HIS_CUM.STT_YEAR is '统计年份';
comment on column INC_HIS_CUM.FUND_CUM_PAY is '基金累计收入';
comment on column INC_HIS_CUM.FUND_CUM_INC is '基金累计支出';


drop table INC_FORT_BASE;
create table INC_FORT_BASE (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
CLCT_PSNTIME                  FLOAT8        not null,
AVG_CLCTSTD                  FLOAT8        not null,
AVG_CLCT_AMT                  FLOAT8        not null,
constraint PK_INC_FORT_BASE primary key (id)
);
comment on table INC_FORT_BASE is '基金收入预测结果';
comment on column INC_FORT_BASE.SCHM_ID is '方案ID';
comment on column INC_FORT_BASE.ADMDVS is '医保区划';
comment on column INC_FORT_BASE.STT_YEAR is '统计年份';
comment on column INC_FORT_BASE.AGE_SEC is '年龄段';
comment on column INC_FORT_BASE.INSUTYPE is '险种类型';
comment on column INC_FORT_BASE.PSN_TYPE is '人员类别';
comment on column INC_FORT_BASE.RESD_NATU is '户口性质';
comment on column INC_FORT_BASE.CLCT_PSNTIME is '缴费人次';
comment on column INC_FORT_BASE.AVG_CLCTSTD is '次均缴费基数';
comment on column INC_FORT_BASE.AVG_CLCT_AMT is '次均缴费金额';


drop table INC_FORT_DIST;
create table INC_FORT_DIST (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
STAF_INC                  FLOAT8        not null,
STAF_PAY                  FLOAT8        not null,
constraint PK_INC_FORT_DIST primary key (id)
);
comment on table INC_FORT_DIST is '职工收入分布预测';
comment on column INC_FORT_DIST.SCHM_ID is '方案ID';
comment on column INC_FORT_DIST.ADMDVS is '医保区划';
comment on column INC_FORT_DIST.STT_YEAR is '统计年份';
comment on column INC_FORT_DIST.PRCT is '分位数';
comment on column INC_FORT_DIST.STAF_INC is '职工总工资收入';
comment on column INC_FORT_DIST.STAF_PAY is '职工总医疗支出';


drop table INC_HIS_RICH;
create table INC_HIS_RICH (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
FAIR_LINE                  FLOAT8        not null,
CONC_CURVE                  FLOAT8        not null,
LORENZ_CURVE                  FLOAT8        not null,
constraint PK_INC_HIS_RICH primary key (id)
);
comment on table INC_HIS_RICH is '贫富差距分析历史';
comment on column INC_HIS_RICH.ADMDVS is '医保区划';
comment on column INC_HIS_RICH.STT_YEAR is '统计年份';
comment on column INC_HIS_RICH.PRCT is '分位数';
comment on column INC_HIS_RICH.FAIR_LINE is '绝对公平线';
comment on column INC_HIS_RICH.CONC_CURVE is '集中曲线';
comment on column INC_HIS_RICH.LORENZ_CURVE is '洛伦兹曲线';


drop table INC_FORT_RICH;
create table INC_FORT_RICH (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
FAIR_LINE                  FLOAT8        not null,
CONC_CURVE                  FLOAT8        not null,
LORENZ_CURVE                  FLOAT8        not null,
constraint PK_INC_FORT_RICH primary key (id)
);
comment on table INC_FORT_RICH is '贫富差距分析预测';
comment on column INC_FORT_RICH.SCHM_ID is '方案ID';
comment on column INC_FORT_RICH.ADMDVS is '医保区划';
comment on column INC_FORT_RICH.STT_YEAR is '统计年份';
comment on column INC_FORT_RICH.PRCT is '分位数';
comment on column INC_FORT_RICH.FAIR_LINE is '绝对公平线';
comment on column INC_FORT_RICH.CONC_CURVE is '集中曲线';
comment on column INC_FORT_RICH.LORENZ_CURVE is '洛伦兹曲线';


drop table INC_HIS_KAKW;
create table INC_HIS_KAKW (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
KAKWANI                  FLOAT8        not null,
constraint PK_INC_HIS_KAKW primary key (id)
);
comment on table INC_HIS_KAKW is '指数历史';
comment on column INC_HIS_KAKW.ADMDVS is '医保区划';
comment on column INC_HIS_KAKW.STT_YEAR is '统计年份';
comment on column INC_HIS_KAKW.KAKWANI is 'KAKWANI指数';


drop table INC_FORT_KAKW;
create table INC_FORT_KAKW (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
KAKWANI                  FLOAT8        not null,
constraint PK_INC_FORT_KAKW primary key (id)
);
comment on table INC_FORT_KAKW is 'KAKWANI指数预测';
comment on column INC_FORT_KAKW.SCHM_ID is '方案ID';
comment on column INC_FORT_KAKW.ADMDVS is '医保区划';
comment on column INC_FORT_KAKW.STT_YEAR is '统计年份';
comment on column INC_FORT_KAKW.KAKWANI is 'KAKWANI指数';


drop table PAY_HIS_BASE;
create table PAY_HIS_BASE (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
MEDINSLV                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_IPT_DAYS                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
AVG_HIFP_PAY                  FLOAT8        not null,
AVG_HIFMI_PAY                  FLOAT8        not null,
AVG_MAF_PAY                  FLOAT8        not null,
AVG_ACCT_PAY                  FLOAT8        not null,
AVG_PSN_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_BASE primary key (id)
);
comment on table PAY_HIS_BASE is '基金支出历史数据';
comment on column PAY_HIS_BASE.ADMDVS is '医保区划';
comment on column PAY_HIS_BASE.STT_YEAR is '统计年份';
comment on column PAY_HIS_BASE.AGE_SEC is '年龄段';
comment on column PAY_HIS_BASE.INSUTYPE is '险种类型';
comment on column PAY_HIS_BASE.PSN_TYPE is '人员类别';
comment on column PAY_HIS_BASE.RESD_NATU is '户口性质';
comment on column PAY_HIS_BASE.MEDINSLV is '医疗机构等级';
comment on column PAY_HIS_BASE.MED_TYPE is '医疗类别';
comment on column PAY_HIS_BASE.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_BASE.AVG_IPT_DAYS is '次均住院天数';
comment on column PAY_HIS_BASE.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_HIS_BASE.AVG_FUND_PAY is '次均基金支出';
comment on column PAY_HIS_BASE.AVG_HIFP_PAY is '次均基本统筹基金';
comment on column PAY_HIS_BASE.AVG_HIFMI_PAY is '次均大病医保基金';
comment on column PAY_HIS_BASE.AVG_MAF_PAY is '次均医疗救助基金';
comment on column PAY_HIS_BASE.AVG_ACCT_PAY is '次均个账支出';
comment on column PAY_HIS_BASE.AVG_PSN_PAY is '次均个人自付';


drop table PAY_HIS_RESU;
create table PAY_HIS_RESU (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
LV3_MEDINS_CNT                  FLOAT8        not null,
LV2_MEDINS_CNT                  FLOAT8        not null,
LV1_MEDINS_CNT                  FLOAT8        not null,
FIXPHAC_CNT                  FLOAT8        not null,
DR_PSNCNT                  FLOAT8        not null,
NURS_PSNCNT                  FLOAT8        not null,
BED_VAL                  FLOAT8        not null,
constraint PK_PAY_HIS_RESU primary key (id)
);
comment on table PAY_HIS_RESU is '医疗资源历史数据';
comment on column PAY_HIS_RESU.ADMDVS is '医保区划';
comment on column PAY_HIS_RESU.STT_YEAR is '统计年份';
comment on column PAY_HIS_RESU.LV3_MEDINS_CNT is '三级机构数';
comment on column PAY_HIS_RESU.LV2_MEDINS_CNT is '二级机构数';
comment on column PAY_HIS_RESU.LV1_MEDINS_CNT is '一级或无等级机构数';
comment on column PAY_HIS_RESU.FIXPHAC_CNT is '定点药店数';
comment on column PAY_HIS_RESU.DR_PSNCNT is '医师数';
comment on column PAY_HIS_RESU.NURS_PSNCNT is '护士数';
comment on column PAY_HIS_RESU.BED_VAL is '床位数';


drop table PAY_HIS_DIST;
create table PAY_HIS_DIST (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_DIST primary key (id)
);
comment on table PAY_HIS_DIST is '支出分布历史数据';
comment on column PAY_HIS_DIST.ADMDVS is '医保区划';
comment on column PAY_HIS_DIST.STT_YEAR is '统计年份';
comment on column PAY_HIS_DIST.PRCT is '分位数';
comment on column PAY_HIS_DIST.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_DIST.AVG_FUND_PAY is '次均基金支出';


drop table PAY_HIS_DIST_DETL;
create table PAY_HIS_DIST_DETL (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
CITY_ADMDVS                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
MEDINSLV                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_DIST_DETL primary key (id)
);
comment on table PAY_HIS_DIST_DETL is '支出分布明细历史';
comment on column PAY_HIS_DIST_DETL.ADMDVS is '医保区划';
comment on column PAY_HIS_DIST_DETL.STT_YEAR is '统计年份';
comment on column PAY_HIS_DIST_DETL.PRCT is '分位数';
comment on column PAY_HIS_DIST_DETL.CITY_ADMDVS is '市级医保区划';
comment on column PAY_HIS_DIST_DETL.PSN_TYPE is '人员类别';
comment on column PAY_HIS_DIST_DETL.MEDINSLV is '医疗机构等级';
comment on column PAY_HIS_DIST_DETL.MED_TYPE is '医疗类别';
comment on column PAY_HIS_DIST_DETL.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_DIST_DETL.AVG_FUND_PAY is '次均基金支出';


drop table PAY_HIS_DIAG;
create table PAY_HIS_DIAG (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
GEND                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
NW_PATN_PSNCNT                  FLOAT8        not null,
MDTRT_PSNCNT                  FLOAT8        not null,
CUM_MDTRT_PSNCNT                  FLOAT8        not null,
DIE_PSNCNT                  FLOAT8        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_DIAG primary key (id)
);
comment on table PAY_HIS_DIAG is '疾病诊断历史数据';
comment on column PAY_HIS_DIAG.ADMDVS is '医保区划';
comment on column PAY_HIS_DIAG.STT_YEAR is '统计年份';
comment on column PAY_HIS_DIAG.GEND is '性别';
comment on column PAY_HIS_DIAG.AGE_SEC is '年龄段';
comment on column PAY_HIS_DIAG.INSUTYPE is '险种类型';
comment on column PAY_HIS_DIAG.PSN_TYPE is '人员类别';
comment on column PAY_HIS_DIAG.DIAG_CODE is '诊断代码';
comment on column PAY_HIS_DIAG.NW_PATN_PSNCNT is '新增病人数';
comment on column PAY_HIS_DIAG.MDTRT_PSNCNT is '就诊人数';
comment on column PAY_HIS_DIAG.CUM_MDTRT_PSNCNT is '累计就诊人数';
comment on column PAY_HIS_DIAG.DIE_PSNCNT is '死亡人数';
comment on column PAY_HIS_DIAG.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_DIAG.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_HIS_DIAG.AVG_FUND_PAY is '次均基金支出';


drop table PAY_HIS_DIAG_FEE;
create table PAY_HIS_DIAG_FEE (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
TCMHERB_FEE                  FLOAT8        not null,
TCMPAT_FEE                  FLOAT8        not null,
WMFEE                  FLOAT8        not null,
OPRN_FEE                  FLOAT8        not null,
BEDFEE                  FLOAT8        not null,
TRTFEE                  FLOAT8        not null,
EXAMFEE                  FLOAT8        not null,
OTH_FEE                  FLOAT8        not null,
constraint PK_PAY_HIS_DIAG_FEE primary key (id)
);
comment on table PAY_HIS_DIAG_FEE is '疾病费用历史数据';
comment on column PAY_HIS_DIAG_FEE.ADMDVS is '医保区划';
comment on column PAY_HIS_DIAG_FEE.STT_YEAR is '统计年份';
comment on column PAY_HIS_DIAG_FEE.DIAG_CODE is '诊断代码';
comment on column PAY_HIS_DIAG_FEE.TCMHERB_FEE is '中草药费';
comment on column PAY_HIS_DIAG_FEE.TCMPAT_FEE is '中成药费';
comment on column PAY_HIS_DIAG_FEE.WMFEE is '西药费';
comment on column PAY_HIS_DIAG_FEE.OPRN_FEE is '手术费';
comment on column PAY_HIS_DIAG_FEE.BEDFEE is '床位费';
comment on column PAY_HIS_DIAG_FEE.TRTFEE is '诊疗费';
comment on column PAY_HIS_DIAG_FEE.EXAMFEE is '检查费';
comment on column PAY_HIS_DIAG_FEE.OTH_FEE is '其他费';


drop table PAY_HIS_OUT_FLOW;
create table PAY_HIS_OUT_FLOW (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_OUT_FLOW primary key (id)
);
comment on table PAY_HIS_OUT_FLOW is '异地流向历史数据';
comment on column PAY_HIS_OUT_FLOW.INSU_ADMDVS is '参保医保区划';
comment on column PAY_HIS_OUT_FLOW.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_HIS_OUT_FLOW.STT_YEAR is '统计年份';
comment on column PAY_HIS_OUT_FLOW.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_OUT_FLOW.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_HIS_OUT_FLOW.AVG_FUND_PAY is '次均基金支出';


drop table PAY_HIS_OUT_DIAG;
create table PAY_HIS_OUT_DIAG (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_OUT_DIAG primary key (id)
);
comment on table PAY_HIS_OUT_DIAG is '异地诊断历史数据';
comment on column PAY_HIS_OUT_DIAG.INSU_ADMDVS is '参保医保区划';
comment on column PAY_HIS_OUT_DIAG.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_HIS_OUT_DIAG.STT_YEAR is '统计年份';
comment on column PAY_HIS_OUT_DIAG.DIAG_CODE is '诊断代码';
comment on column PAY_HIS_OUT_DIAG.MED_TYPE is '医疗类别';
comment on column PAY_HIS_OUT_DIAG.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_OUT_DIAG.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_HIS_OUT_DIAG.AVG_FUND_PAY is '次均基金支出';


drop table PAY_HIS_OUT_DEPT;
create table PAY_HIS_OUT_DEPT (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DEPT_NAME                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_HIS_OUT_DEPT primary key (id)
);
comment on table PAY_HIS_OUT_DEPT is '异地科室历史数据';
comment on column PAY_HIS_OUT_DEPT.INSU_ADMDVS is '参保医保区划';
comment on column PAY_HIS_OUT_DEPT.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_HIS_OUT_DEPT.STT_YEAR is '统计年份';
comment on column PAY_HIS_OUT_DEPT.DEPT_NAME is '科室名称';
comment on column PAY_HIS_OUT_DEPT.MED_TYPE is '医疗类别';
comment on column PAY_HIS_OUT_DEPT.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_OUT_DEPT.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_HIS_OUT_DEPT.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_BASE;
create table PAY_FORT_BASE (
id     INT8     SERIAL         not null,
SCHM_ID                  VARCHAR(45)        not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
RESD_NATU                  VARCHAR(45)        not null,
MEDINSLV                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_IPT_DAYS                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
AVG_HIFP_PAY                  FLOAT8        not null,
AVG_HIFMI_PAY                  FLOAT8        not null,
AVG_MAF_PAY                  FLOAT8        not null,
AVG_ACCT_PAY                  FLOAT8        not null,
AVG_PSN_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_BASE primary key (id)
);
comment on table PAY_FORT_BASE is '基金支出预测结果';
comment on column PAY_FORT_BASE.SCHM_ID is '方案ID';
comment on column PAY_FORT_BASE.ADMDVS is '医保区划';
comment on column PAY_FORT_BASE.STT_YEAR is '统计年份';
comment on column PAY_FORT_BASE.AGE_SEC is '年龄段';
comment on column PAY_FORT_BASE.INSUTYPE is '险种类型';
comment on column PAY_FORT_BASE.PSN_TYPE is '人员类别';
comment on column PAY_FORT_BASE.RESD_NATU is '户口性质';
comment on column PAY_FORT_BASE.MEDINSLV is '医疗机构等级';
comment on column PAY_FORT_BASE.MED_TYPE is '医疗类别';
comment on column PAY_FORT_BASE.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_BASE.AVG_IPT_DAYS is '次均住院天数';
comment on column PAY_FORT_BASE.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_BASE.AVG_FUND_PAY is '次均基金支出';
comment on column PAY_FORT_BASE.AVG_HIFP_PAY is '次均基本统筹基金';
comment on column PAY_FORT_BASE.AVG_HIFMI_PAY is '次均大病医保基金';
comment on column PAY_FORT_BASE.AVG_MAF_PAY is '次均医疗救助基金';
comment on column PAY_FORT_BASE.AVG_ACCT_PAY is '次均个账支出';
comment on column PAY_FORT_BASE.AVG_PSN_PAY is '次均个人自付';


drop table PAY_FORT_RESU;
create table PAY_FORT_RESU (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
LV3_MEDINS_CNT                  FLOAT8        not null,
LV2_MEDINS_CNT                  FLOAT8        not null,
LV1_MEDINS_CNT                  FLOAT8        not null,
FIXPHAC_CNT                  FLOAT8        not null,
DR_PSNCNT                  FLOAT8        not null,
NURS_PSNCNT                  FLOAT8        not null,
BED_VAL                  FLOAT8        not null,
constraint PK_PAY_FORT_RESU primary key (id)
);
comment on table PAY_FORT_RESU is '医疗资源预测结果';
comment on column PAY_FORT_RESU.ADMDVS is '医保区划';
comment on column PAY_FORT_RESU.STT_YEAR is '统计年份';
comment on column PAY_FORT_RESU.LV3_MEDINS_CNT is '三级机构数';
comment on column PAY_FORT_RESU.LV2_MEDINS_CNT is '二级机构数';
comment on column PAY_FORT_RESU.LV1_MEDINS_CNT is '一级或无等级机构数';
comment on column PAY_FORT_RESU.FIXPHAC_CNT is '定点药店数';
comment on column PAY_FORT_RESU.DR_PSNCNT is '医师数';
comment on column PAY_FORT_RESU.NURS_PSNCNT is '护士数';
comment on column PAY_FORT_RESU.BED_VAL is '床位数';


drop table PAY_FORT_DIST;
create table PAY_FORT_DIST (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_DIST primary key (id)
);
comment on table PAY_FORT_DIST is '支出分布预测结果';
comment on column PAY_FORT_DIST.ADMDVS is '医保区划';
comment on column PAY_FORT_DIST.STT_YEAR is '统计年份';
comment on column PAY_FORT_DIST.PRCT is '分位数';
comment on column PAY_FORT_DIST.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_DIST.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_DIST_DETL;
create table PAY_FORT_DIST_DETL (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
PRCT                  VARCHAR(45)        not null,
CITY_ADMDVS                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
MEDINSLV                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_DIST_DETL primary key (id)
);
comment on table PAY_FORT_DIST_DETL is '支出分布明细预测';
comment on column PAY_FORT_DIST_DETL.ADMDVS is '医保区划';
comment on column PAY_FORT_DIST_DETL.STT_YEAR is '统计年份';
comment on column PAY_FORT_DIST_DETL.PRCT is '分位数';
comment on column PAY_FORT_DIST_DETL.CITY_ADMDVS is '市级医保区划';
comment on column PAY_FORT_DIST_DETL.PSN_TYPE is '人员类别';
comment on column PAY_FORT_DIST_DETL.MEDINSLV is '医疗机构等级';
comment on column PAY_FORT_DIST_DETL.MED_TYPE is '医疗类别';
comment on column PAY_FORT_DIST_DETL.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_DIST_DETL.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_DIST_DETL.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_DIAG;
create table PAY_FORT_DIAG (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
GEND                  VARCHAR(45)        not null,
AGE_SEC                  VARCHAR(45)        not null,
INSUTYPE                  VARCHAR(45)        not null,
PSN_TYPE                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
NW_PATN_PSNCNT                  FLOAT8        not null,
MDTRT_PSNCNT                  FLOAT8        not null,
CUM_MDTRT_PSNCNT                  FLOAT8        not null,
DIE_PSNCNT                  FLOAT8        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_DIAG primary key (id)
);
comment on table PAY_FORT_DIAG is '疾病诊断预测结果';
comment on column PAY_FORT_DIAG.ADMDVS is '医保区划';
comment on column PAY_FORT_DIAG.STT_YEAR is '统计年份';
comment on column PAY_FORT_DIAG.GEND is '性别';
comment on column PAY_FORT_DIAG.AGE_SEC is '年龄段';
comment on column PAY_FORT_DIAG.INSUTYPE is '险种类型';
comment on column PAY_FORT_DIAG.PSN_TYPE is '人员类别';
comment on column PAY_FORT_DIAG.DIAG_CODE is '诊断代码';
comment on column PAY_FORT_DIAG.NW_PATN_PSNCNT is '新增病人数';
comment on column PAY_FORT_DIAG.MDTRT_PSNCNT is '就诊人数';
comment on column PAY_FORT_DIAG.CUM_MDTRT_PSNCNT is '累计就诊人数';
comment on column PAY_FORT_DIAG.DIE_PSNCNT is '死亡人数';
comment on column PAY_FORT_DIAG.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_DIAG.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_DIAG.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_DIAG_FEE;
create table PAY_FORT_DIAG_FEE (
id     INT8     SERIAL         not null,
ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
TCMHERB_FEE                  FLOAT8        not null,
TCMPAT_FEE                  FLOAT8        not null,
WMFEE                  FLOAT8        not null,
OPRN_FEE                  FLOAT8        not null,
BEDFEE                  FLOAT8        not null,
TRTFEE                  FLOAT8        not null,
EXAMFEE                  FLOAT8        not null,
OTH_FEE                  FLOAT8        not null,
constraint PK_PAY_FORT_DIAG_FEE primary key (id)
);
comment on table PAY_FORT_DIAG_FEE is '疾病费用预测结果';
comment on column PAY_FORT_DIAG_FEE.ADMDVS is '医保区划';
comment on column PAY_FORT_DIAG_FEE.STT_YEAR is '统计年份';
comment on column PAY_FORT_DIAG_FEE.DIAG_CODE is '诊断代码';
comment on column PAY_FORT_DIAG_FEE.TCMHERB_FEE is '中草药费';
comment on column PAY_FORT_DIAG_FEE.TCMPAT_FEE is '中成药费';
comment on column PAY_FORT_DIAG_FEE.WMFEE is '西药费';
comment on column PAY_FORT_DIAG_FEE.OPRN_FEE is '手术费';
comment on column PAY_FORT_DIAG_FEE.BEDFEE is '床位费';
comment on column PAY_FORT_DIAG_FEE.TRTFEE is '诊疗费';
comment on column PAY_FORT_DIAG_FEE.EXAMFEE is '检查费';
comment on column PAY_FORT_DIAG_FEE.OTH_FEE is '其他费';


drop table PAY_FORT_OUT_FLOW;
create table PAY_FORT_OUT_FLOW (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_OUT_FLOW primary key (id)
);
comment on table PAY_FORT_OUT_FLOW is '异地流向预测结果';
comment on column PAY_FORT_OUT_FLOW.INSU_ADMDVS is '参保医保区划';
comment on column PAY_FORT_OUT_FLOW.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_FORT_OUT_FLOW.STT_YEAR is '统计年份';
comment on column PAY_FORT_OUT_FLOW.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_OUT_FLOW.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_OUT_FLOW.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_OUT_DIAG;
create table PAY_FORT_OUT_DIAG (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DIAG_CODE                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_OUT_DIAG primary key (id)
);
comment on table PAY_FORT_OUT_DIAG is '异地诊断预测结果';
comment on column PAY_FORT_OUT_DIAG.INSU_ADMDVS is '参保医保区划';
comment on column PAY_FORT_OUT_DIAG.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_FORT_OUT_DIAG.STT_YEAR is '统计年份';
comment on column PAY_FORT_OUT_DIAG.DIAG_CODE is '诊断代码';
comment on column PAY_FORT_OUT_DIAG.MED_TYPE is '医疗类别';
comment on column PAY_FORT_OUT_DIAG.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_OUT_DIAG.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_OUT_DIAG.AVG_FUND_PAY is '次均基金支出';


drop table PAY_FORT_OUT_DEPT;
create table PAY_FORT_OUT_DEPT (
id     INT8     SERIAL         not null,
INSU_ADMDVS                  VARCHAR(45)        not null,
MDTRT_ADMDVS                  VARCHAR(45)        not null,
STT_YEAR                  VARCHAR(45)        not null,
DEPT_NAME                  VARCHAR(45)        not null,
MED_TYPE                  VARCHAR(45)        not null,
MDTRT_PSNTIME                  FLOAT8        not null,
AVG_MED_FEE                  FLOAT8        not null,
AVG_FUND_PAY                  FLOAT8        not null,
constraint PK_PAY_FORT_OUT_DEPT primary key (id)
);
comment on table PAY_FORT_OUT_DEPT is '异地科室预测结果';
comment on column PAY_FORT_OUT_DEPT.INSU_ADMDVS is '参保医保区划';
comment on column PAY_FORT_OUT_DEPT.MDTRT_ADMDVS is '就医医保区划';
comment on column PAY_FORT_OUT_DEPT.STT_YEAR is '统计年份';
comment on column PAY_FORT_OUT_DEPT.DEPT_NAME is '科室名称';
comment on column PAY_FORT_OUT_DEPT.MED_TYPE is '医疗类别';
comment on column PAY_FORT_OUT_DEPT.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_FORT_OUT_DEPT.AVG_MED_FEE is '次均医疗费用';
comment on column PAY_FORT_OUT_DEPT.AVG_FUND_PAY is '次均基金支出';


