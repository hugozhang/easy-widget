

INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2141, '产妇的主要诊断或其他诊断编码中含有Z37.0/Z37.2/Z37.3/Z37.5/Z37.6编码时,必须填写新生儿出生体重或多新生儿出生体重');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('NWB_BIR_WT,DIAG_CODE', '', 2141, 0, '0', '2', '【新生儿出生体重】产妇的主要诊断或其他诊断编码中含有Z37.0/Z37.2/Z37.3/Z37.5/Z37.6编码时,必须填写新生儿出生体重或多新生儿出生体重', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2141', 'NWB_BIR_WT,DIAG_CODE', '0', '002', '新生儿出生体重', '2', '【新生儿出生体重】产妇的主要诊断或其他诊断编码中含有Z37.0/Z37.2/Z37.3/Z37.5/Z37.6编码时,必须填写新生儿出生体重或多新生儿出生体重');


INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2142, '当性别为男性时,门（急）诊诊断编码不能出现女性诊断编码');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('GEND,OTP_WM_DIAG_DISE_CODE', '门（急）诊诊断编码', 2142, 0, '0', '2', '【门（急）诊诊断编码】当性别为男性时,门（急）诊诊断编码不能出现女性诊断编码', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2142', 'GEND,OTP_WM_DIAG_DISE_CODE', '0', '002', '门（急）诊诊断编码', '2', '【门（急）诊诊断编码】当性别为男性时,门（急）诊诊断编码不能出现女性诊断编码');



INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2143, '当性别为女性时,门（急）诊诊断编码不能出现男性诊断编码');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('GEND,OTP_WM_DIAG_DISE_CODE', '门（急）诊诊断编码', 2143, 0, '0', '2', '【门（急）诊诊断编码】当性别为女性时,门（急）诊诊断编码不能出现男性诊断编码', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2143', 'GEND,OTP_WM_DIAG_DISE_CODE', '0', '002', '门（急）诊诊断编码', '2', '【门（急）诊诊断编码】当性别为女性时,门（急）诊诊断编码不能出现男性诊断编码');


INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2144, '门（急）诊诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('OTP_WM_DIAG_DISE_CODE', '门（急）诊诊断编码范围', 2144, 0, '0', '2', '【门（急）诊诊断编码范围】门（急）诊诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2144', 'OTP_WM_DIAG_DISE_CODE', '0', '002', '门（急）诊诊断编码范围', '2', '【门（急）诊诊断编码范围】门（急）诊诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码');



INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2145, '门（急）诊（中医）诊断名称不为空时，门（急）诊（中医）诊断名称未按照中医诊断目录里的标准填写');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('OTP_TCM_DIAG', '门（急）诊（中医）诊断名称', 2145, 0, '0', '2', '【门（急）诊（中医）诊断名称】门（急）诊（中医）诊断名称不为空时，门（急）诊（中医）诊断名称未按照中医诊断目录里的标准填写', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2145', 'OTP_TCM_DIAG', '0', '002', '门（急）诊（中医）诊断名称', '2', '【门（急）诊（中医）诊断名称】门（急）诊（中医）诊断名称不为空时，门（急）诊（中医）诊断名称未按照中医诊断目录里的标准填写');



INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2146, '门（急）诊（中医）诊断代码不为空时，门（急）诊（中医）诊断代码未按照中医诊断目录里的标准填写');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('OTP_TCM_DIAG_DISE_CODE', '门（急）诊（中医）诊断代码', 2146, 0, '0', '2', '【门（急）诊（中医）诊断代码】门（急）诊（中医）诊断代码不为空时，门（急）诊（中医）诊断代码未按照中医诊断目录里的标准填写', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2146', 'OTP_TCM_DIAG_DISE_CODE', '0', '002', '门（急）诊（中医）诊断代码', '2', '【门（急）诊（中医）诊断代码】门（急）诊（中医）诊断代码不为空时，门（急）诊（中医）诊断代码未按照中医诊断目录里的标准填写');


INSERT INTO `t_jsqd_rule_tbl`(`gzid`, `pcgz`) VALUES (2147, '主要诊断和其他诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码');

INSERT INTO `t_jsqd_project_rule_tbl`(`zd`, `xmmc`, `gzid`, `kfbz`, `gzzt`, `cwlx`, `cwxx`, `glzd`, `bafzxybs`, `ycfz`, `dipycfz`)
VALUES ('DIAG_CODE', '主要诊断和其他诊断', 2147, 0, '0', '2', '【主要诊断和其他诊断】主要诊断和其他诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码', NULL, NULL, NULL, NULL);
INSERT INTO `t_jsqd_rule_scene_tbl`(`gzid`, `zd`, `gzzt`, `cjbm`, `xmmc`, `cwlx`, `cwxx`)
VALUES ('2147', 'DIAG_CODE', '0', '002', '主要诊断和其他诊断', '2', '【主要诊断和其他诊断】主要诊断和其他诊断及编码范围应填写A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码');