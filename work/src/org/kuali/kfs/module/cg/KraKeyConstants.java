/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.kra;

import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.util.AuditError;

/**
 * Holds error key constants for KRA.
 * 
 * 
 */
public class KraKeyConstants {

    // KRA Audit Errors
    public static final String AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED = "audit.costShare.institution.distributed";
    public static final String AUDIT_COST_SHARE_3P_DISTRIBUTED = "audit.costShare.3p.distributed";
    public static final String AUDIT_MODULAR_CONSORTIUM = "audit.modular.consortium";
    public static final String AUDIT_PERSONNEL_STATUS = "audit.personnel.status";
    public static final String AUDIT_NONPERSONNEL_SUBCONTRACTOR_EXCESS_AMOUNT = "audit.nonpersonnel.subcontractorExceesAmount";
    public static final String AUDIT_PARAMETERS_NEGATIVE_IDC = "audit.parameters.negativeIdc";

    // Agency/Delivery Info
    public static final String AUDIT_MAIN_PAGE_AGENCY_REQUIRED = "audit.mainPage.agency.required";
    public static final String AUDIT_MAIN_PAGE_DUE_DATE_TYPE_REQUIRED = "audit.mainPage.due.date.type.required";
    public static final String AUDIT_MAIN_PAGE_COPIES_REQUIRED = "audit.mainPage.copies.required";
    public static final String AUDIT_MAIN_PAGE_ADDRESS_REQUIRED = "audit.mainPage.address.required";

    // Personnel and Units/Orgs
    public static final String AUDIT_MAIN_PAGE_PERSON_REQUIRED = "audit.mainPage.person.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_FA_REQUIRED = "audit.mainPage.person.fa.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_CREDIT_REQUIRED = "audit.mainPage.person.credit.required";
    public static final String AUDIT_MAIN_PAGE_PD_REQUIRED = "audit.mainPage.pd.required";
    public static final String AUDIT_MAIN_PAGE_ONLY_ONE_PD = "audit.mainPage.only.one.pd.required";
    public static final String AUDIT_MAIN_PAGE_ORG_FA_REQUIRED = "audit.mainPage.org.fa.required";
    public static final String AUDIT_MAIN_PAGE_ORG_CREDIT_REQUIRED = "audit.mainPage.org.credit.required";    
    public static final String AUDIT_MAIN_PAGE_TOTAL_CREDIT_PERCENT_NOT_100 = "audit.mainPage.total.credit.percent.not.100";
    public static final String AUDIT_MAIN_PAGE_TOTAL_FA_PERCENT_NOT_100 = "audit.mainPage.total.fa.percent.not.100";
    
    // Submission Details
    public static final String AUDIT_MAIN_PAGE_SUBMISSION_TYPE_REQUIRED = "audit.mainPage.submission.type.required";
    public static final String AUDIT_MAIN_PAGE_SUBMISSION_TYPE_FEDID_REQUIRED = "audit.mainPage.submission.type.fedid.required";
    public static final String AUDIT_MAIN_PAGE_PURPOSE_REQUIRED = "audit.mainPage.purpose.required";
    public static final String AUDIT_MAIN_PAGE_TITLE_REQUIRED = "audit.mainPage.title.required";
    public static final String AUDIT_MAIN_PAGE_ABSTRACT_REQUIRED = "audit.mainPage.abstract.required";
    
    // Amounts & Dates
    public static final String AUDIT_MAIN_PAGE_DIRECT_REQUIRED = "audit.mainPage.direct.required";
    public static final String AUDIT_MAIN_PAGE_INDIRECT_REQUIRED = "audit.mainPage.indirect.required";
    public static final String AUDIT_MAIN_PAGE_START_DATE_REQUIRED = "audit.mainPage.start.date.required";
    public static final String AUDIT_MAIN_PAGE_END_DATE_REQUIRED = "audit.mainPage.end.date.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_DIRECT_REQUIRED = "audit.mainPage.total.direct.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_INDIRECT_REQUIRED = "audit.mainPage.total.indirect.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_START_DATE_REQUIRED = "audit.mainPage.total.start.date.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_END_DATE_REQUIRED = "audit.mainPage.total.end.date.required";
    public static final String AUDIT_MAIN_PAGE_DIRECT_GREATER_TOTAL_DIRECT = "audit.mainPage.direct.greater.total.direct";
    public static final String AUDIT_MAIN_PAGE_INDIRECT_GREATER_TOTAL_INDIRECT = "audit.mainPage.indirect.greater.total.indirect";
    public static final String AUDIT_MAIN_PAGE_START_DATE_GREATER_TOTAL_START_DATE = "audit.mainPage.start.date.greater.total.start.date";
    public static final String AUDIT_MAIN_PAGE_END_DATE_LESS_TOTAL_END_DATE = "audit.mainPage.end.date.less.total.end.date";
    public static final String AUDIT_MAIN_PAGE_DIRECT_LESS_INDIRECT = "audit.mainPage.direct.less.indirect";
    public static final String AUDIT_MAIN_PAGE_TOTAL_DIRECT_LESS_TOTAL_INDIRECT = "audit.mainPage.total.direct.less.total.indirect";
    public static final String AUDIT_MAIN_PAGE_START_DATE_BEFORE_END_DATE = "audit.mainPage.start.date.before.end.date";
    public static final String AUDIT_MAIN_PAGE_TOTAL_START_DATE_BEFORE_TOTAL_END_DATE = "audit.mainPage.total.start.date.before.total.end.date";
    public static final String AUDIT_MAIN_PAGE_SUBCONTRACTOR_TOTAL_GREATER_DIRECT = "audit.mainPage.subcontractor.total.greater.direct";
    
    public static final String ERROR_INVALID_AMOUNT_POSITIVE_ONLY = "error.invalid.amount.positive.only";
    public static final String ERROR_ORG_ALREADY_EXISTS_ON_RF = "error.org.already.exists.on.rf";
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS_ON_RF = "error.account.already.exists.on.rf";
    public static final String ERROR_ORG_NOT_FOUND = "error.org.not.found";
    public static final String ERROR_SUBCONTRACTOR_NOT_FOUND = "error.subcontractor.not.found";
    public static final String ERROR_SUBCONTRACTOR_ALREADY_EXISTS_ON_RF = "error.subcontractor.already.exists.on.rf";
    public static final String ERROR_FRINGE_RATE_TOO_LARGE = "error.fringeRate.tooLarge";
    public static final String ERROR_KEYWORD_MISSING = "error.keyword.missing";
    public static final String ERROR_PERSON_NOT_NAMED = "error.person.not.named";
    
    // Routing Form Research Risks page errors
    public static final String ERROR_APPROVAL_DATE_REQUIRED = "error.approvalDate.required";
    public static final String ERROR_APPROVAL_DATE_REMOVE = "error.approvalDate.remove";
    public static final String ERROR_EXPIRATION_DATE_REMOVE = "error.expirationDate.remove";
    public static final String ERROR_EXEMPTION_NUMBER_REQUIRED = "error.exemptionNumber.required";
    public static final String ERROR_EXEMPTION_NUMBER_REMOVE = "error.exemptionNumber.remove";
    public static final String ERROR_HUMAN_SUBJECTS_APPROVAL_DATE_TOO_OLD = "error.humanSubjects.approvalDate.tooOld";
    public static final String ERROR_ANIMALS_APPROVAL_DATE_TOO_OLD = "error.animals.approvalDate.tooOld";
    public static final String ERROR_EXPIRATION_DATE_TOO_EARLY = "error.expiration.tooEarly";
}