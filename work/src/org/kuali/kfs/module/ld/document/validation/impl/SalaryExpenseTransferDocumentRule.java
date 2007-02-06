/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.rules;

import static org.kuali.Constants.BALANCE_TYPE_ACTUAL;
import static org.kuali.Constants.BLANK_SPACE;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_OBJECT_CODE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBase;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.SalaryExpenseTransferAccountingLine;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 * 
 * 
 */
public class SalaryExpenseTransferDocumentRule extends TransactionalDocumentRuleBase {

    public SalaryExpenseTransferDocumentRule() {
    }   
    
    protected boolean AddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
    }
    
    /** Account must be valid.
      * Object code must be valid.
      * Object code must be a labor object code.
             Object code must exist in the ld_labor_obj_t table.
             The field finobj_frngslry_cd for the object code in the ld_labor_obj_t table must have a value of "S".
      * Sub-account, if specified, must be valid for account.
      * Sub-object, if specified, must be valid for account and object code.
      * Enforce the A21-report-related business rules for the "SAVE" action.
      * Position must be valid for fiscal year. FIS enforces this by a direct lookup of the PeopleSoft HRMS position data table. Kuali cannot do this. (See issue 12.)
      * Amount must not be zero. 
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {

        // Retrieve the Fringe or Salary Code for the object code in the ld_labor_obj_t table. 
        // It must have a value of "S".
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        Map fieldValues = new HashMap();
        fieldValues.put("financialObjectCode", accountingLine.getFinancialObjectCode().toString());
        ArrayList laborObjects = (ArrayList) SpringServiceLocator.getBusinessObjectService().findMatching(LaborObject.class, fieldValues);
        if (laborObjects.size() == 0) {
            reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }
        LaborObject laborObject = (LaborObject) laborObjects.get(0);    
        String FringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();

        if (!FringeOrSalaryCode.equals("S")) {
            LOG.info("FringeOrSalaryCode not equal S");
              reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.FRINGE_OR_SALARY_CODE_MISSING_ERROR, accountingLine.getAccountNumber());
            return false;
        }            
            
        if (accountingLine.isSourceAccountingLine()) {
            System.out.println("** Source **");
        }
        else if (accountingLine.isTargetAccountingLine()) {
            System.out.println("** Target **");
        }
        else {
            System.out.println("** Other **");
        }
        
        // Save the employee ID in all accounting related lines
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument)transactionalDocument;
        SalaryExpenseTransferAccountingLine salaryExpenseTransferAccountingLine = (SalaryExpenseTransferAccountingLine)accountingLine;
        salaryExpenseTransferAccountingLine.setEmplid(salaryExpenseTransferDocument.getEmplid()); 
        
        return true;
    }
    
    /**
     * This document specific routing business rule check calls the check that makes sure that the budget year is consistent for all
     * accounting lines.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        SalaryExpenseTransferDocument setDoc = (SalaryExpenseTransferDocument) document;

        List sourceLines = new ArrayList();
        List targetLines = new ArrayList();

        //set source and target accounting lines
        sourceLines.addAll(setDoc.getSourceAccountingLines());
        targetLines.addAll(setDoc.getTargetAccountingLines());

        //check to ensure totals of accounting lines in source and target sections match
        if (isValid) {
            isValid = isAccountingLineTotalsMatch(sourceLines, targetLines);            
        }

        //check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        if (isValid) {
            isValid = isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);
        }
        
        return isValid;        
    }

    /**
     * 
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    private boolean isAccountingLineTotalsMatch(List sourceLines, List targetLines){
        
        boolean isValid = true;
        
        AccountingLine line = null; 
        
        // totals for the from and to lines.
        KualiDecimal sourceLinesAmount = new KualiDecimal(0);
        KualiDecimal targetLinesAmount = new KualiDecimal(0);

        //sum source lines
        for (Iterator i = sourceLines.iterator(); i.hasNext();) {
            line = (SalaryExpenseTransferAccountingLine) i.next();            
            sourceLinesAmount = sourceLinesAmount.add(line.getAmount());            
        }

        //sum target lines
        for (Iterator i = targetLines.iterator(); i.hasNext();) {
            line = (SalaryExpenseTransferAccountingLine) i.next();            
            targetLinesAmount = targetLinesAmount.add(line.getAmount());            
        }
        
        //if totals don't match, then add error message
        if (sourceLinesAmount.compareTo(targetLinesAmount) != 0) {
            isValid = false;
            reportError(PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.Labor.ACCOUNTING_LINE_TOTALS_MISMATCH_ERROR);            
        }

        return isValid;        
    }
    
    /**
     * 
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    private boolean isAccountingLineTotalsMatchByPayFYAndPayPeriod(List sourceLines, List targetLines){
        
        boolean isValid = true;
                
        Map sourceLinesMap = new HashMap();
        Map targetLinesMap = new HashMap();                       

        //sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        sourceLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(sourceLines);

        //sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        targetLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(targetLines);
        
        //if totals don't match by PayFY+PayPeriod categories, then add error message
        if ( compareAccountingLineTotalsByPayFYAndPayPeriod(sourceLinesMap, targetLinesMap) == false ) {
            isValid = false;
            reportError(PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.Labor.ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR);            
        }

        return isValid;        
    }

    private String createPayFYPeriodKey(Integer payFiscalYear, String payPeriodCode){
    
        StringBuffer payFYPeriodKey = new StringBuffer();
        
        payFYPeriodKey.append(payFiscalYear);
        payFYPeriodKey.append(payPeriodCode);
        
        return payFYPeriodKey.toString();
    }
    
    private Map sumAccountingLineAmountsByPayFYAndPayPeriod(List accountingLines){
        
        SalaryExpenseTransferAccountingLine line = null; 
        KualiDecimal linesAmount = new KualiDecimal(0);
        Map linesMap = new HashMap();
        String payFYPeriodKey = null;
        
        //go through source lines adding amounts to appropriate place in map
        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            //initialize
            line = (SalaryExpenseTransferAccountingLine) i.next();
            linesAmount = new KualiDecimal(0);
            
            //create hash key
            payFYPeriodKey = createPayFYPeriodKey(
                    line.getPayrollEndDateFiscalYear(), line.getPayrollEndDateFiscalPeriodCode()); 
            
            //if entry exists, pull from hash
            if ( linesMap.containsKey(payFYPeriodKey) ){
                linesAmount = (KualiDecimal)linesMap.get(payFYPeriodKey);                
            }
                        
            //update and store
            linesAmount = linesAmount.add(line.getAmount());            
            linesMap.put(payFYPeriodKey, linesAmount);            
        }
        
        return linesMap;        
    }
    
    private boolean compareAccountingLineTotalsByPayFYAndPayPeriod(Map sourceLinesMap, Map targetLinesMap){
    
        boolean isValid = true;
        Map.Entry entry = null;
        String currentKey = null;
        KualiDecimal sourceLinesAmount = new KualiDecimal(0);
        KualiDecimal targetLinesAmount = new KualiDecimal(0);

        
        //Loop through source lines comparing against target lines
        for(Iterator i=sourceLinesMap.entrySet().iterator(); i.hasNext() && isValid;){
            //initialize
            entry = (Map.Entry)i.next();
            currentKey = (String)entry.getKey();
            sourceLinesAmount = (KualiDecimal)entry.getValue();
            
            if( targetLinesMap.containsKey( currentKey ) ){
                targetLinesAmount = (KualiDecimal)targetLinesMap.get(currentKey);

                if ( sourceLinesAmount.compareTo(targetLinesAmount) != 0 ) {
                    isValid = false;                
                }

            }else{
                isValid = false;                
            }            
        }
        
        /* Now loop through target lines comparing against source lines.
         * This finds missing entries from either direction (source or target)
         */
        for(Iterator i=targetLinesMap.entrySet().iterator(); i.hasNext() && isValid;){
            //initialize
            entry = (Map.Entry)i.next();
            currentKey = (String)entry.getKey();
            targetLinesAmount = (KualiDecimal)entry.getValue();
            
            if( sourceLinesMap.containsKey( currentKey ) ){
                sourceLinesAmount = (KualiDecimal)sourceLinesMap.get(currentKey);

                if ( targetLinesAmount.compareTo(sourceLinesAmount) != 0 ) {
                    isValid = false;                                
                }
                
            }else{
                isValid = false;                
            }            
        }
        
        
        return isValid;    
    }
    
    /**
     * Overriding hook into generate general ledger pending entries, but calling a method
     * to generate labor ledger pending entries.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
        //return processGenerateLaborLedgerPendingEntries(transactionalDocument, accountingLine, sequenceHelper);
    }

    /**
     * This method is the starting point for creating labor ledger pending entries.
     *  
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @return
     */
    public boolean processGenerateLaborLedgerPendingEntries(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper){
        boolean success = true;
    
        PendingLedgerEntry originalEntry = new PendingLedgerEntry();
        success &= processOriginalLaborLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLine, originalEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        PendingLedgerEntry offsetEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(originalEntry);
        
        return true;
    }
        
    protected boolean processOriginalLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry originalEntry) {        
        
        boolean success = true;
        
        // populate the entry
        populateOriginalLaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, originalEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeOriginalLaborLedgerPendingEntry(transactionalDocument, accountingLine, originalEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(originalEntry);

        return success;
    }

    protected boolean customizeOriginalLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry originalEntry) {
        return true;
    }

 
    protected void populateOriginalLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry originalEntry) {        

        //TODO: Need to find out why there number of fields on the spec does not match with the number of fields
        // specified in the PendingLedgerEntry class.
        originalEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule classes
        originalEntry.setUniversityFiscalYear(transactionalDocument.getPostingYear());
        originalEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        originalEntry.setAccountNumber(accountingLine.getAccountNumber());
        originalEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        originalEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        originalEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        originalEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        ObjectCode objectCode = accountingLine.getObjectCode();
        if (ObjectUtils.isNull(objectCode)) {
            accountingLine.refreshReferenceObject("objectCode");
        }
        originalEntry.setFinancialObjectTypeCode(accountingLine.getObjectCode().getFinancialObjectTypeCode());
        originalEntry.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(transactionalDocument.getClass()));
        originalEntry.setFinancialSystemOriginationCode(SpringServiceLocator.getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode());
        originalEntry.setDocumentNumber(accountingLine.getDocumentNumber());
        originalEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        originalEntry.setTransactionLedgerEntryDescription(getEntryValue(accountingLine.getFinancialDocumentLineDescription(), transactionalDocument.getDocumentHeader().getFinancialDocumentDescription()));
        originalEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        originalEntry.setTransactionDebitCreditCode(isDebit(transactionalDocument, accountingLine) ? Constants.GL_DEBIT_CODE : Constants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        originalEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        originalEntry.setOrganizationDocumentNumber(transactionalDocument.getDocumentHeader().getOrganizationDocumentNumber());
        originalEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        originalEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());      
        originalEntry.setPositionNumber( ((SalaryExpenseTransferAccountingLine)accountingLine).getPositionNumber() );
        originalEntry.setEmplid( ((SalaryExpenseTransferAccountingLine)accountingLine).getEmplid() );
        originalEntry.setPayrollEndDateFiscalYear( ((SalaryExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        originalEntry.setPayrollEndDateFiscalPeriodCode( ((SalaryExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        originalEntry.setTransactionTotalHours( ((SalaryExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        
        //originalEntry.setVersionNumber(new Long(1));
        
        //TODO: This field is a Date in GL but a Timestamp in LL
        //originalEntry.setTransactionEntryProcessedTimestamp(new java.sql.Timestamp(transactionTimestamp.getTime()));
        //TODO: check why sufficient funds fin obj code doesn't exist for LL
        //originalEntry.setAcctSufficientFundsFinObjCd(SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(accountingLine.getObjectCode(), accountingLine.getAccount().getAccountSufficientFundsCode()));
        //originalEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        //originalEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        //originalEntry.setReferenceFinancialSystemOriginationCode(getEntryValue(accountingLine.getReferenceOriginCode(), BLANK_SPACE));
        //originalEntry.setReferenceFinancialDocumentNumber(getEntryValue(accountingLine.getReferenceNumber(), BLANK_SPACE));
        //originalEntry.setReferenceFinancialDocumentTypeCode(getEntryValue(accountingLine.getReferenceTypeCode(), BLANK_SPACE));

        //TODO: offset indicator doesn't exist in LLPE, but offset code does exist but not in GLPE
        //originalEntry.setTransactionEntryOffsetIndicator(false);
        
        // TODO wait for core budget year data structures to be put in place
        // originalEntry.setBudgetYear(accountingLine.getBudgetYear());
        // originalEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);

    }
    
    protected boolean processA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry a21Entry) {        

        boolean success = true;
        
        // populate the entry
        populateA21LaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, a21Entry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21LaborLedgerPendingEntry(transactionalDocument, accountingLine, a21Entry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(a21Entry);

        return success;
    }

    protected boolean customizeA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry a21Entry) {
        return true;
    }

    protected void populateA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry a21Entry) {        
    }

    protected boolean processA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry a21RevEntry) {        

        boolean success = true;
        
        // populate the entry
        populateA21RevLaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, a21RevEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21RevLaborLedgerPendingEntry(transactionalDocument, accountingLine, a21RevEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(a21RevEntry);
        
        return success;
    }

    protected boolean customizeA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry a21RevEntry) {
        return true;
    }

    protected void populateA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry a21RevEntry) {        
    }

    protected boolean processBenefitLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitEntry) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitLaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, benefitEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitLaborLedgerPendingEntry(transactionalDocument, accountingLine, benefitEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(benefitEntry);

        return success;
    }

    protected boolean customizeBenefitLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry benefitEntry) {
        return true;
    }

    protected void populateBenefitLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitEntry) {        
    }

    protected boolean processBenefitA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitA21Entry) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitA21LaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, benefitA21Entry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21LaborLedgerPendingEntry(transactionalDocument, accountingLine, benefitA21Entry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(benefitA21Entry);

        return success;
    }

    protected boolean customizeBenefitA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry benefitA21Entry) {
        return true;
    }

    protected void populateBenefitA21LaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitA21Entry) {        
    }

    protected boolean processBenefitA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitA21RevEntry) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitA21RevLaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, benefitA21RevEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21RevLaborLedgerPendingEntry(transactionalDocument, accountingLine, benefitA21RevEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(benefitA21RevEntry);

        return success;
    }

    protected boolean customizeBenefitA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry benefitA21RevEntry) {
        return true;
    }

    protected void populateBenefitA21RevLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitA21RevEntry) {        
    }

    protected boolean processBenefitClearingLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitClearingEntry) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitClearingLaborLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, benefitClearingEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitClearingLaborLedgerPendingEntry(transactionalDocument, accountingLine, benefitClearingEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)transactionalDocument).getLaborLedgerPendingEntries().add(benefitClearingEntry);

        return success;
    }

    protected boolean customizeBenefitClearingLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry benefitClearingEntry) {
        return true;
    }

    protected void populateBenefitClearingLaborLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitClearingEntry) {        
    }

    /**
     * This is responsible for properly negating the sign on an accounting line's amount when its associated document is an error
     * correction.
     * 
     * @param transactionalDocument
     * @param accountingLine
     */
    private final void handleDocumentErrorCorrection(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // If the document corrects another document, make sure the accounting line has the correct sign.
        if ((null == transactionalDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isNegative()) || (null != transactionalDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isPositive())) {
            accountingLine.setAmount(accountingLine.getAmount().multiply(new KualiDecimal(1)));
        }
    }

    /**
     * Applies the given flexible offset account to the given offset entry. Does nothing if flexibleOffsetAccount is null or its COA
     * and account number are the same as the offset entry's.
     * 
     * @param flexibleOffsetAccount may be null
     * @param offsetEntry may be modified
     */
    private static void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, PendingLedgerEntry offsetEntry) {
        if (flexibleOffsetAccount == null) {
            return; // They are not required and may also be disabled.
        }
        String flexCoa = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();
        String flexAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        if (flexCoa.equals(offsetEntry.getChartOfAccountsCode()) && flexAccountNumber.equals(offsetEntry.getAccountNumber())) {
            return; // no change, so leave sub-account as is
        }
        if (ObjectUtils.isNull(flexibleOffsetAccount.getFinancialOffsetAccount())) {
            throw new ReferentialIntegrityException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
        }
        offsetEntry.setChartOfAccountsCode(flexCoa);
        offsetEntry.setAccountNumber(flexAccountNumber);
        // COA and account number are part of the sub-account's key, so the original sub-account would be invalid.
        offsetEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
    }
    

    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
        }
        else {
            throw new IllegalStateException(IsDebitUtils.isInvalidLineTypeIllegalArgumentExceptionMessage);
        }

        return isDebit;
    }

    public boolean isCredit(AccountingLine accountingLine, TransactionalDocument transactionalDocument) {
        return false;
    }
 
    /**
     * util class that contains common algorithms for determining debit amounts
     * 
     * 
     */
    protected static class IsDebitUtils {
        protected static final String isDebitCalculationIllegalStateExceptionMessage = "an invalid debit/credit check state was detected";
        protected static final String isErrorCorrectionIllegalStateExceptionMessage = "invalid (error correction) document not allowed";
        protected static final String isInvalidLineTypeIllegalArgumentExceptionMessage = "invalid accounting line type";

        /**
         * 
         * @param debitCreditCode
         * @return true if debitCreditCode equals the the debit constant
         */
        static boolean isDebitCode(String debitCreditCode) {
            return StringUtils.equals(Constants.GL_DEBIT_CODE, debitCreditCode);
        }

        /**
         * <ol>
         * <li>object type is included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount > 0)
         * <li> (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount < 0)
         * <li> (isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> document isErrorCorrection
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * 
         * @param rule
         * @param transactionalDocument
         * @param accountingLine
         * @return boolean
         * 
         */
        static boolean isDebitConsideringType(TransactionalDocumentRuleBase rule, TransactionalDocument transactionalDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();

            // income/liability
            if (rule.isIncomeOrLiability(accountingLine)) {
                isDebit = !isPositiveAmount;
            }
            // expense/asset
            else {
                if (rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>object type is not included in determining if a line is debit or credit.
         * <li>accounting line section (source/target) is not included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> none
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability || isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount <= 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * @param rule
         * @param transactionalDocument
         * @param accountingLine
         * @return boolean
         */
        static boolean isDebitConsideringNothingPositiveOnly(TransactionalDocumentRuleBase rule, TransactionalDocument transactionalDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(transactionalDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // isDebit if income/liability/expense/asset and line amount is positive
                if (isPositiveAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = true;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // error correction
            else {
                boolean isNegativeAmount = amount.isNegative();
                // isDebit if income/liability/expense/asset and line amount is negative
                if (isNegativeAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = false;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }

            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) type is included in determining if a line is debit or credit.
         * <li> zero line amounts are never allowed
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * 
         * @param rule
         * @param transactionalDocument
         * @param accountingLine
         * @return boolean
         * 
         */
        static boolean isDebitConsideringSection(TransactionalDocumentRuleBase rule, TransactionalDocument transactionalDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();
            // source line
            if (accountingLine.isSourceAccountingLine()) {
                // income/liability/expense/asset
                if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = !isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // target line
            else {
                if (accountingLine.isTargetAccountingLine()) {
                    if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                        isDebit = isPositiveAmount;
                    }
                    else {
                        throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                    }
                }
                else {
                    throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) and object type is included in determining if a line is debit or credit.
         * <li> negative line amounts are <b>Only</b> allowed during error correction
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isErrorCorrection && isSourceLine && (isIncome || isLiability) && (lineAmount < 0)
         * <li> isErrorCorrection && isTargetLine && (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isErrorCorrection && (isExpense || isAsset) && (lineAmount < 0)
         * <li> isErrorCorrection && (isIncome || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> !isErrorCorrection && !(lineAmount > 0)
         * <li> isErrorCorrection && !(lineAmount < 0)
         * </ol>
         * 
         * @param rule
         * @param transactionalDocument
         * @param accountingLine
         * @return boolean
         */
        static boolean isDebitConsideringSectionAndTypePositiveOnly(TransactionalDocumentRuleBase rule, TransactionalDocument transactionalDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(transactionalDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // only allow amount >0
                if (!isPositiveAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isIncomeOrLiability(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isExpenseOrAsset(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }
            // error correction document
            else {
                boolean isNegativeAmount = amount.isNegative();
                if (!isNegativeAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isExpenseOrAsset(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isIncomeOrLiability(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }

            return isDebit;
        }

        /**
         * throws an <code>IllegalStateException</code> if the document is an error correction. otherwise does nothing
         * 
         * @param rule
         * @param transactionalDocument
         */
        static void disallowErrorCorrectionDocumentCheck(TransactionalDocumentRuleBase rule, TransactionalDocument transactionalDocument) {
            if (isErrorCorrection(transactionalDocument)) {
                throw new IllegalStateException(isErrorCorrectionIllegalStateExceptionMessage);
            }
        }
        
        /**
         * Convience method for determine if a document is an error correction document.
         * 
         * @param transactionalDocument
         * @return true if document is an error correct
         */
        static boolean isErrorCorrection(TransactionalDocument transactionalDocument) {
            boolean isErrorCorrection = false;

            String correctsDocumentId = transactionalDocument.getDocumentHeader().getFinancialDocumentInErrorNumber();
            if (StringUtils.isNotBlank(correctsDocumentId)) {
                isErrorCorrection = true;
            }

            return isErrorCorrection;
        }

    }

}