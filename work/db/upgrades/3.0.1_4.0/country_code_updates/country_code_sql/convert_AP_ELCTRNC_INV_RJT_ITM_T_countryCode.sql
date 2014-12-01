--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
-- 
-- Copyright 2005-2014 The Kuali Foundation
-- 
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

-- This script will migrate a column in a table from the former Rice country 
-- codes which were based on FIPS 10-4 (with some minor differences) to the new
-- Rice country codes which are based on ISO 3166-1.  This script may not 
-- properly execute on columns with a primary key or unique constraint as some
-- of the former codes have merged (i.e. - all US Minor Outlying Islands have 
-- been unified under a single code, UM).  This script also does not take any 
-- action on codes that are not part of the list of former Rice country codes.
--
-- Table Name: 	AP_ELCTRNC_INV_RJT_ITM_T
-- Column Name: INV_REF_ITM_CNTRY_CD
--
-- In order to avoid collisions between the former codes and the new codes, the 
-- codes are first changed to an interim, unique code.  Once that change is 
-- complete, they are changed to the new, correct code.
--
-- Change to temporary code
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A0' where INV_REF_ITM_CNTRY_CD='AA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A1' where INV_REF_ITM_CNTRY_CD='AC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A3' where INV_REF_ITM_CNTRY_CD='AG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A4' where INV_REF_ITM_CNTRY_CD='AJ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A7' where INV_REF_ITM_CNTRY_CD='AN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='A9' where INV_REF_ITM_CNTRY_CD='AQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B1' where INV_REF_ITM_CNTRY_CD='AS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B2' where INV_REF_ITM_CNTRY_CD='AT';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B3' where INV_REF_ITM_CNTRY_CD='AU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B4' where INV_REF_ITM_CNTRY_CD='AV';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B5' where INV_REF_ITM_CNTRY_CD='AY';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B6' where INV_REF_ITM_CNTRY_CD='BA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B8' where INV_REF_ITM_CNTRY_CD='BC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='B9' where INV_REF_ITM_CNTRY_CD='BD';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C1' where INV_REF_ITM_CNTRY_CD='BF';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C2' where INV_REF_ITM_CNTRY_CD='BG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C3' where INV_REF_ITM_CNTRY_CD='BH';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C4' where INV_REF_ITM_CNTRY_CD='BK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C5' where INV_REF_ITM_CNTRY_CD='BL';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C6' where INV_REF_ITM_CNTRY_CD='BM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C7' where INV_REF_ITM_CNTRY_CD='BN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C8' where INV_REF_ITM_CNTRY_CD='BO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='C9' where INV_REF_ITM_CNTRY_CD='BP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D0' where INV_REF_ITM_CNTRY_CD='BQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D2' where INV_REF_ITM_CNTRY_CD='BS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D4' where INV_REF_ITM_CNTRY_CD='BU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D6' where INV_REF_ITM_CNTRY_CD='BX';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D7' where INV_REF_ITM_CNTRY_CD='BY';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='D9' where INV_REF_ITM_CNTRY_CD='CB';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E0' where INV_REF_ITM_CNTRY_CD='CD';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E1' where INV_REF_ITM_CNTRY_CD='CE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E2' where INV_REF_ITM_CNTRY_CD='CF';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E3' where INV_REF_ITM_CNTRY_CD='CG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E4' where INV_REF_ITM_CNTRY_CD='CH';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E5' where INV_REF_ITM_CNTRY_CD='CI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E6' where INV_REF_ITM_CNTRY_CD='CJ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E7' where INV_REF_ITM_CNTRY_CD='CK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='E9' where INV_REF_ITM_CNTRY_CD='CN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F1' where INV_REF_ITM_CNTRY_CD='CQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F2' where INV_REF_ITM_CNTRY_CD='CR';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F3' where INV_REF_ITM_CNTRY_CD='CS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F4' where INV_REF_ITM_CNTRY_CD='CT';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F7' where INV_REF_ITM_CNTRY_CD='CW';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='F9' where INV_REF_ITM_CNTRY_CD='CZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G0' where INV_REF_ITM_CNTRY_CD='DA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G2' where INV_REF_ITM_CNTRY_CD='DO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G3' where INV_REF_ITM_CNTRY_CD='DR';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G6' where INV_REF_ITM_CNTRY_CD='EI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G7' where INV_REF_ITM_CNTRY_CD='EK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='G8' where INV_REF_ITM_CNTRY_CD='EN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='H0' where INV_REF_ITM_CNTRY_CD='ES';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='H2' where INV_REF_ITM_CNTRY_CD='EU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='H3' where INV_REF_ITM_CNTRY_CD='EZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='H4' where INV_REF_ITM_CNTRY_CD='FA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='H5' where INV_REF_ITM_CNTRY_CD='FG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I0' where INV_REF_ITM_CNTRY_CD='FP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I1' where INV_REF_ITM_CNTRY_CD='FQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I3' where INV_REF_ITM_CNTRY_CD='FS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I4' where INV_REF_ITM_CNTRY_CD='GA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I5' where INV_REF_ITM_CNTRY_CD='GB';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I6' where INV_REF_ITM_CNTRY_CD='GE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='I7' where INV_REF_ITM_CNTRY_CD='GG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J0' where INV_REF_ITM_CNTRY_CD='GJ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J1' where INV_REF_ITM_CNTRY_CD='GK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J3' where INV_REF_ITM_CNTRY_CD='GM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J4' where INV_REF_ITM_CNTRY_CD='GO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J6' where INV_REF_ITM_CNTRY_CD='GQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='J9' where INV_REF_ITM_CNTRY_CD='GV';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='K1' where INV_REF_ITM_CNTRY_CD='GZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='K2' where INV_REF_ITM_CNTRY_CD='HA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='K5' where INV_REF_ITM_CNTRY_CD='HO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='K6' where INV_REF_ITM_CNTRY_CD='HQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='K9' where INV_REF_ITM_CNTRY_CD='IC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='L4' where INV_REF_ITM_CNTRY_CD='IP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='L6' where INV_REF_ITM_CNTRY_CD='IS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='L8' where INV_REF_ITM_CNTRY_CD='IV';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='L9' where INV_REF_ITM_CNTRY_CD='IY';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='M0' where INV_REF_ITM_CNTRY_CD='IZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='M1' where INV_REF_ITM_CNTRY_CD='JA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='M4' where INV_REF_ITM_CNTRY_CD='JN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='M6' where INV_REF_ITM_CNTRY_CD='JQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='M7' where INV_REF_ITM_CNTRY_CD='JU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N0' where INV_REF_ITM_CNTRY_CD='KN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N1' where INV_REF_ITM_CNTRY_CD='KQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N2' where INV_REF_ITM_CNTRY_CD='KR';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N3' where INV_REF_ITM_CNTRY_CD='KS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N4' where INV_REF_ITM_CNTRY_CD='KT';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N5' where INV_REF_ITM_CNTRY_CD='KU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N8' where INV_REF_ITM_CNTRY_CD='LE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='N9' where INV_REF_ITM_CNTRY_CD='LG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O0' where INV_REF_ITM_CNTRY_CD='LH';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O1' where INV_REF_ITM_CNTRY_CD='LI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O2' where INV_REF_ITM_CNTRY_CD='LO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O3' where INV_REF_ITM_CNTRY_CD='LQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O4' where INV_REF_ITM_CNTRY_CD='LS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O5' where INV_REF_ITM_CNTRY_CD='LT';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O8' where INV_REF_ITM_CNTRY_CD='MA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='O9' where INV_REF_ITM_CNTRY_CD='MB';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P0' where INV_REF_ITM_CNTRY_CD='MC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P2' where INV_REF_ITM_CNTRY_CD='MF';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P3' where INV_REF_ITM_CNTRY_CD='MG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P4' where INV_REF_ITM_CNTRY_CD='MH';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P5' where INV_REF_ITM_CNTRY_CD='MI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P8' where INV_REF_ITM_CNTRY_CD='MN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='P9' where INV_REF_ITM_CNTRY_CD='MO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Q0' where INV_REF_ITM_CNTRY_CD='MP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Q1' where INV_REF_ITM_CNTRY_CD='MQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Q4' where INV_REF_ITM_CNTRY_CD='MU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Q6' where INV_REF_ITM_CNTRY_CD='MW';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='R0' where INV_REF_ITM_CNTRY_CD='NA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='R2' where INV_REF_ITM_CNTRY_CD='NE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='R4' where INV_REF_ITM_CNTRY_CD='NG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='R5' where INV_REF_ITM_CNTRY_CD='NH';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='R6' where INV_REF_ITM_CNTRY_CD='NI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S1' where INV_REF_ITM_CNTRY_CD='NS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S2' where INV_REF_ITM_CNTRY_CD='NU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S4' where INV_REF_ITM_CNTRY_CD='OC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S5' where INV_REF_ITM_CNTRY_CD='PA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S6' where INV_REF_ITM_CNTRY_CD='PC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S8' where INV_REF_ITM_CNTRY_CD='PF';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='S9' where INV_REF_ITM_CNTRY_CD='PG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T2' where INV_REF_ITM_CNTRY_CD='PM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T3' where INV_REF_ITM_CNTRY_CD='PO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T4' where INV_REF_ITM_CNTRY_CD='PP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T5' where INV_REF_ITM_CNTRY_CD='PS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T6' where INV_REF_ITM_CNTRY_CD='PU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='T9' where INV_REF_ITM_CNTRY_CD='RM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U1' where INV_REF_ITM_CNTRY_CD='RP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U2' where INV_REF_ITM_CNTRY_CD='RQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U3' where INV_REF_ITM_CNTRY_CD='RS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U6' where INV_REF_ITM_CNTRY_CD='SB';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U7' where INV_REF_ITM_CNTRY_CD='SC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U8' where INV_REF_ITM_CNTRY_CD='SE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='U9' where INV_REF_ITM_CNTRY_CD='SF';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='V0' where INV_REF_ITM_CNTRY_CD='SG';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='V5' where INV_REF_ITM_CNTRY_CD='SN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='V7' where INV_REF_ITM_CNTRY_CD='SP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='V8' where INV_REF_ITM_CNTRY_CD='SR';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='V9' where INV_REF_ITM_CNTRY_CD='ST';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W0' where INV_REF_ITM_CNTRY_CD='SU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W1' where INV_REF_ITM_CNTRY_CD='SV';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W2' where INV_REF_ITM_CNTRY_CD='SW';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W4' where INV_REF_ITM_CNTRY_CD='SZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W5' where INV_REF_ITM_CNTRY_CD='TC';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W6' where INV_REF_ITM_CNTRY_CD='TD';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W7' where INV_REF_ITM_CNTRY_CD='TE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='W9' where INV_REF_ITM_CNTRY_CD='TI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X0' where INV_REF_ITM_CNTRY_CD='TK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X1' where INV_REF_ITM_CNTRY_CD='TL';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X2' where INV_REF_ITM_CNTRY_CD='TN';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X3' where INV_REF_ITM_CNTRY_CD='TO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X4' where INV_REF_ITM_CNTRY_CD='TP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X5' where INV_REF_ITM_CNTRY_CD='TS';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X6' where INV_REF_ITM_CNTRY_CD='TU';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='X9' where INV_REF_ITM_CNTRY_CD='TX';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Y2' where INV_REF_ITM_CNTRY_CD='UK';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Y3' where INV_REF_ITM_CNTRY_CD='UP';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Y4' where INV_REF_ITM_CNTRY_CD='UR';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z0' where INV_REF_ITM_CNTRY_CD='VI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z1' where INV_REF_ITM_CNTRY_CD='VM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z2' where INV_REF_ITM_CNTRY_CD='VQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z3' where INV_REF_ITM_CNTRY_CD='VT';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z4' where INV_REF_ITM_CNTRY_CD='WA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z5' where INV_REF_ITM_CNTRY_CD='WE';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z7' where INV_REF_ITM_CNTRY_CD='WI';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='Z8' where INV_REF_ITM_CNTRY_CD='WQ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='00' where INV_REF_ITM_CNTRY_CD='WZ';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='01' where INV_REF_ITM_CNTRY_CD='YM';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='02' where INV_REF_ITM_CNTRY_CD='YO';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='03' where INV_REF_ITM_CNTRY_CD='ZA';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='04' where INV_REF_ITM_CNTRY_CD='ZI';
-- Change to final code
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AW' where INV_REF_ITM_CNTRY_CD='A0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AG' where INV_REF_ITM_CNTRY_CD='A1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DZ' where INV_REF_ITM_CNTRY_CD='A3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AZ' where INV_REF_ITM_CNTRY_CD='A4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AD' where INV_REF_ITM_CNTRY_CD='A7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AS' where INV_REF_ITM_CNTRY_CD='A9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AU' where INV_REF_ITM_CNTRY_CD='B1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AU' where INV_REF_ITM_CNTRY_CD='B2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AT' where INV_REF_ITM_CNTRY_CD='B3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AI' where INV_REF_ITM_CNTRY_CD='B4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AQ' where INV_REF_ITM_CNTRY_CD='B5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BH' where INV_REF_ITM_CNTRY_CD='B6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BW' where INV_REF_ITM_CNTRY_CD='B8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BM' where INV_REF_ITM_CNTRY_CD='B9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BS' where INV_REF_ITM_CNTRY_CD='C1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BD' where INV_REF_ITM_CNTRY_CD='C2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BZ' where INV_REF_ITM_CNTRY_CD='C3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BA' where INV_REF_ITM_CNTRY_CD='C4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BO' where INV_REF_ITM_CNTRY_CD='C5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MM' where INV_REF_ITM_CNTRY_CD='C6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BJ' where INV_REF_ITM_CNTRY_CD='C7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BY' where INV_REF_ITM_CNTRY_CD='C8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SB' where INV_REF_ITM_CNTRY_CD='C9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='D0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='D2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BG' where INV_REF_ITM_CNTRY_CD='D4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BN' where INV_REF_ITM_CNTRY_CD='D6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='BI' where INV_REF_ITM_CNTRY_CD='D7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KH' where INV_REF_ITM_CNTRY_CD='D9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TD' where INV_REF_ITM_CNTRY_CD='E0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LK' where INV_REF_ITM_CNTRY_CD='E1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CG' where INV_REF_ITM_CNTRY_CD='E2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CD' where INV_REF_ITM_CNTRY_CD='E3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CN' where INV_REF_ITM_CNTRY_CD='E4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CL' where INV_REF_ITM_CNTRY_CD='E5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KY' where INV_REF_ITM_CNTRY_CD='E6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CC' where INV_REF_ITM_CNTRY_CD='E7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KM' where INV_REF_ITM_CNTRY_CD='E9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MP' where INV_REF_ITM_CNTRY_CD='F1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AU' where INV_REF_ITM_CNTRY_CD='F2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CR' where INV_REF_ITM_CNTRY_CD='F3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CF' where INV_REF_ITM_CNTRY_CD='F4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CK' where INV_REF_ITM_CNTRY_CD='F7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CS' where INV_REF_ITM_CNTRY_CD='F9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DK' where INV_REF_ITM_CNTRY_CD='G0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DM' where INV_REF_ITM_CNTRY_CD='G2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DO' where INV_REF_ITM_CNTRY_CD='G3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='IE' where INV_REF_ITM_CNTRY_CD='G6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GQ' where INV_REF_ITM_CNTRY_CD='G7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='EE' where INV_REF_ITM_CNTRY_CD='G8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SV' where INV_REF_ITM_CNTRY_CD='H0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='H2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CZ' where INV_REF_ITM_CNTRY_CD='H3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='FK' where INV_REF_ITM_CNTRY_CD='H4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GF' where INV_REF_ITM_CNTRY_CD='H5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PF' where INV_REF_ITM_CNTRY_CD='I0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='I1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='I3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GM' where INV_REF_ITM_CNTRY_CD='I4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GA' where INV_REF_ITM_CNTRY_CD='I5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DE' where INV_REF_ITM_CNTRY_CD='I6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GE' where INV_REF_ITM_CNTRY_CD='I7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GD' where INV_REF_ITM_CNTRY_CD='J0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GG' where INV_REF_ITM_CNTRY_CD='J1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='DE' where INV_REF_ITM_CNTRY_CD='J3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='J4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GU' where INV_REF_ITM_CNTRY_CD='J6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GN' where INV_REF_ITM_CNTRY_CD='J9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PS' where INV_REF_ITM_CNTRY_CD='K1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='HT' where INV_REF_ITM_CNTRY_CD='K2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='HN' where INV_REF_ITM_CNTRY_CD='K5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='K6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='IS' where INV_REF_ITM_CNTRY_CD='K9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='FR' where INV_REF_ITM_CNTRY_CD='L4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='IL' where INV_REF_ITM_CNTRY_CD='L6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CI' where INV_REF_ITM_CNTRY_CD='L8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NT' where INV_REF_ITM_CNTRY_CD='L9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='IQ' where INV_REF_ITM_CNTRY_CD='M0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='JP' where INV_REF_ITM_CNTRY_CD='M1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NO' where INV_REF_ITM_CNTRY_CD='M4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='M6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='M7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KP' where INV_REF_ITM_CNTRY_CD='N0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='N1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KI' where INV_REF_ITM_CNTRY_CD='N2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KR' where INV_REF_ITM_CNTRY_CD='N3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CX' where INV_REF_ITM_CNTRY_CD='N4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KW' where INV_REF_ITM_CNTRY_CD='N5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LB' where INV_REF_ITM_CNTRY_CD='N8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LV' where INV_REF_ITM_CNTRY_CD='N9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LT' where INV_REF_ITM_CNTRY_CD='O0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LR' where INV_REF_ITM_CNTRY_CD='O1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SK' where INV_REF_ITM_CNTRY_CD='O2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='O3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LI' where INV_REF_ITM_CNTRY_CD='O4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LS' where INV_REF_ITM_CNTRY_CD='O5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MG' where INV_REF_ITM_CNTRY_CD='O8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MQ' where INV_REF_ITM_CNTRY_CD='O9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MO' where INV_REF_ITM_CNTRY_CD='P0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='YT' where INV_REF_ITM_CNTRY_CD='P2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MN' where INV_REF_ITM_CNTRY_CD='P3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MS' where INV_REF_ITM_CNTRY_CD='P4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MW' where INV_REF_ITM_CNTRY_CD='P5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MC' where INV_REF_ITM_CNTRY_CD='P8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MA' where INV_REF_ITM_CNTRY_CD='P9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MU' where INV_REF_ITM_CNTRY_CD='Q0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='Q1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='OM' where INV_REF_ITM_CNTRY_CD='Q4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ME' where INV_REF_ITM_CNTRY_CD='Q6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AN' where INV_REF_ITM_CNTRY_CD='R0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NU' where INV_REF_ITM_CNTRY_CD='R2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NE' where INV_REF_ITM_CNTRY_CD='R4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='VU' where INV_REF_ITM_CNTRY_CD='R5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NG' where INV_REF_ITM_CNTRY_CD='R6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SR' where INV_REF_ITM_CNTRY_CD='S1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NI' where INV_REF_ITM_CNTRY_CD='S2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ZZ' where INV_REF_ITM_CNTRY_CD='S4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PY' where INV_REF_ITM_CNTRY_CD='S5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PN' where INV_REF_ITM_CNTRY_CD='S6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='XP' where INV_REF_ITM_CNTRY_CD='S8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='XS' where INV_REF_ITM_CNTRY_CD='S9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PA' where INV_REF_ITM_CNTRY_CD='T2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PT' where INV_REF_ITM_CNTRY_CD='T3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PG' where INV_REF_ITM_CNTRY_CD='T4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PW' where INV_REF_ITM_CNTRY_CD='T5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GW' where INV_REF_ITM_CNTRY_CD='T6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='MH' where INV_REF_ITM_CNTRY_CD='T9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PH' where INV_REF_ITM_CNTRY_CD='U1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PR' where INV_REF_ITM_CNTRY_CD='U2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='RU' where INV_REF_ITM_CNTRY_CD='U3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PM' where INV_REF_ITM_CNTRY_CD='U6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='KN' where INV_REF_ITM_CNTRY_CD='U7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SC' where INV_REF_ITM_CNTRY_CD='U8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ZA' where INV_REF_ITM_CNTRY_CD='U9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SN' where INV_REF_ITM_CNTRY_CD='V0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SG' where INV_REF_ITM_CNTRY_CD='V5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ES' where INV_REF_ITM_CNTRY_CD='V7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='RS' where INV_REF_ITM_CNTRY_CD='V8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='LC' where INV_REF_ITM_CNTRY_CD='V9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SD' where INV_REF_ITM_CNTRY_CD='W0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SJ' where INV_REF_ITM_CNTRY_CD='W1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SE' where INV_REF_ITM_CNTRY_CD='W2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='CH' where INV_REF_ITM_CNTRY_CD='W4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='AE' where INV_REF_ITM_CNTRY_CD='W5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TT' where INV_REF_ITM_CNTRY_CD='W6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TF' where INV_REF_ITM_CNTRY_CD='W7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TJ' where INV_REF_ITM_CNTRY_CD='W9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TC' where INV_REF_ITM_CNTRY_CD='X0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TK' where INV_REF_ITM_CNTRY_CD='X1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TO' where INV_REF_ITM_CNTRY_CD='X2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TG' where INV_REF_ITM_CNTRY_CD='X3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ST' where INV_REF_ITM_CNTRY_CD='X4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TN' where INV_REF_ITM_CNTRY_CD='X5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TR' where INV_REF_ITM_CNTRY_CD='X6';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='TM' where INV_REF_ITM_CNTRY_CD='X9';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='GB' where INV_REF_ITM_CNTRY_CD='Y2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UA' where INV_REF_ITM_CNTRY_CD='Y3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SU' where INV_REF_ITM_CNTRY_CD='Y4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='VG' where INV_REF_ITM_CNTRY_CD='Z0';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='VN' where INV_REF_ITM_CNTRY_CD='Z1';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='VI' where INV_REF_ITM_CNTRY_CD='Z2';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='VA' where INV_REF_ITM_CNTRY_CD='Z3';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='NA' where INV_REF_ITM_CNTRY_CD='Z4';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='PS' where INV_REF_ITM_CNTRY_CD='Z5';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='EH' where INV_REF_ITM_CNTRY_CD='Z7';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='UM' where INV_REF_ITM_CNTRY_CD='Z8';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='SZ' where INV_REF_ITM_CNTRY_CD='00';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='YE' where INV_REF_ITM_CNTRY_CD='01';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='YU' where INV_REF_ITM_CNTRY_CD='02';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ZM' where INV_REF_ITM_CNTRY_CD='03';
UPDATE AP_ELCTRNC_INV_RJT_ITM_T SET INV_REF_ITM_CNTRY_CD='ZW' where INV_REF_ITM_CNTRY_CD='04';

