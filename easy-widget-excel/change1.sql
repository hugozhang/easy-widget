create table PAY_HIS_DIST (
id                SERIAL         not null,
ADMDVS                  VARCHAR(45)        null,
STT_YEAR                  VARCHAR(45)        null,
PRCT                  VARCHAR(45)        null,
MDTRT_PSNTIME                  FLOAT8        null,
AVG_FUND_PAY                  FLOAT8        null,
constraint PK_PAY_HIS_DIST primary key (id)
);
comment on table PAY_HIS_DIST is '支出分布历史数据';
comment on column PAY_HIS_DIST.ADMDVS is '医保区划';
comment on column PAY_HIS_DIST.STT_YEAR is '统计年份';
comment on column PAY_HIS_DIST.PRCT is '分位数';
comment on column PAY_HIS_DIST.MDTRT_PSNTIME is '就诊人次';
comment on column PAY_HIS_DIST.AVG_FUND_PAY is '次均基金支出';


