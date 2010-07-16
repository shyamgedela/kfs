create table END_TRAN_ARCHV_T (
	FDOC_NBR VARCHAR2(14) NOT NULL,
	FDOC_LN_NBR NUMBER(7) NOT NULL,
	FDOC_LN_TYP_CD VARCHAR2(1) NOT NULL,
	TRAN_DESC VARCHAR2(40),
	TRAN_TYP_CD VARCHAR2(5) NOT NULL,
	TRAN_SUB_TYP_CD VARCHAR2(1) NOT NULL,
	TRAN_SRC_TYP_CD VARCHAR2(1) NOT NULL,
	TRAN_KEMID VARCHAR2(10) NOT NULL,
	TRAN_ETRAN_CD VARCHAR2(9) NOT NULL,
	TRAN_LN_DESC VARCHAR2(40),
	TRAN_IP_IND_CD VARCHAR2(1) NOT NULL,
	TRAN_INC_CSH_AMT NUMBER(19,2) NOT NULL,
	TRAN_PRIN_CSH_AMT NUMBER(19,2) NOT NULL,
	TRAN_CORPUS_IND VARCHAR2(1) NOT NULL,
	TRAN_CORPUS_AMT NUMBER(19,2),
	TRAN_PSTD_DT DATE,	
	PRIMARY KEY (FDOC_NBR, FDOC_LN_TYP_CD, FDOC_LN_NBR)	
);

ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR1 FOREIGN KEY (FDOC_NBR)
	REFERENCES KRNS_DOC_HDR_T (DOC_HDR_ID);

ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR2 FOREIGN KEY (TRAN_SUB_TYP_CD)
	REFERENCES END_TRAN_SUB_TYP_T (TRAN_SUB_TYP_CD);
	
ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR3 FOREIGN KEY (TRAN_SRC_TYP_CD)
	REFERENCES END_TRAN_SRC_TYP_T (TRAN_SRC_TYP_CD);

ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR4 FOREIGN KEY (TRAN_KEMID)
	REFERENCES END_KEMID_T (KEMID);
	
ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR5 FOREIGN KEY (TRAN_ETRAN_CD)
	REFERENCES END_ETRAN_CD_T (ETRAN_CD);

ALTER TABLE END_TRAN_ARCHV_T ADD CONSTRAINT END_TRAN_ARCHV_TR6 FOREIGN KEY (TRAN_IP_IND_CD)
	REFERENCES END_IP_IND_T (IP_IND_CD);

create table END_TRAN_ARCHV_SEC_T (
	FDOC_NBR VARCHAR2(14) NOT NULL,
	FDOC_LN_NBR NUMBER(7) NOT NULL,
	FDOC_LN_TYP_CD VARCHAR2(1) NOT NULL,
	TRAN_SEC_ID VARCHAR2(9) NOT NULL,
	TRAN_SEC_REGIS_CD VARCHAR2(4) NOT NULL,
	TRAN_SEC_ETRAN_CD VARCHAR2(9),
	TRAN_SEC_UNITS NUMBER(16,4),
	TRAN_SEC_UNIT_VAL NUMBER(19,5),
	TRAN_SEC_COST NUMBER(19,2),
	TRAN_SEC_LT_GAIN_LOSS NUMBER(19,2),
	TRAN_SEC_ST_GAIN_LOSS NUMBER(19,2),
	PRIMARY KEY (FDOC_NBR, FDOC_LN_NBR, FDOC_LN_TYP_CD)

);

ALTER TABLE END_TRAN_ARCHV_SEC_T ADD CONSTRAINT END_TRAN_ARCHV_SEC_TR1 FOREIGN KEY (FDOC_NBR, FDOC_LN_TYP_CD, FDOC_LN_NBR)
	REFERENCES  END_TRAN_ARCHV_T(FDOC_NBR, FDOC_LN_TYP_CD, FDOC_LN_NBR);

ALTER TABLE END_TRAN_ARCHV_SEC_T ADD CONSTRAINT END_TRAN_ARCHV_SEC_TR2 FOREIGN KEY (TRAN_SEC_ID)
	REFERENCES END_SEC_T (SEC_ID);

ALTER TABLE END_TRAN_ARCHV_SEC_T ADD CONSTRAINT END_TRAN_ARCHV_SEC_TR3 FOREIGN KEY (TRAN_SEC_REGIS_CD)
	REFERENCES END_REGIS_CD_T (REGIS_CD);