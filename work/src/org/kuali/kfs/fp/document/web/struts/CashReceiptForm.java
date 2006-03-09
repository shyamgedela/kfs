/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.LabelValueBean;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.xml.sax.DocumentHandler;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the action form for Cash Receipts.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class CashReceiptForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;
    private Check newCheck;

    private KualiDecimal checkTotal;

    private String checkEntryMode;
    private List checkEntryModes;

    private List baselineChecks;

    /**
     * Constructs a CashReceiptForm.java.
     */
    public CashReceiptForm() {
        super();
        setDocument(new CashReceiptDocument());
        setNewCheck(new CheckBase());

        checkEntryModes = new ArrayList();
        checkEntryModes.add(new LabelValueBean("Individual Checks/Batches", CashReceiptDocument.CHECK_ENTRY_DETAIL));
        checkEntryModes.add(new LabelValueBean("Total Only", CashReceiptDocument.CHECK_ENTRY_TOTAL));

        setCheckEntryMode(getCashReceiptDocument().getCheckEntryMode());
        
        baselineChecks = new ArrayList();
    }
    
    /**
     * @return CashReceiptDocument
     */
    public CashReceiptDocument getCashReceiptDocument() {
        return (CashReceiptDocument) getDocument();
    }

    /**
     * @return Check
     */
    public Check getNewCheck() {
        return newCheck;
    }

    /**
     * @param newCheck
     */
    public void setNewCheck(Check newCheck) {
        this.newCheck = newCheck;
    }

    /**
     * @param checkTotal
     */
    public void setCheckTotal(KualiDecimal checkTotal) {
        this.checkTotal = checkTotal;
    }

    /**
     * @return KualiDecimal
     */
    public KualiDecimal getCheckTotal() {
        return checkTotal;
    }

    /**
     * @return List of LabelValueBeans representing all available check entry modes
     */
    public List getCheckEntryModes() {
        return checkEntryModes;
    }

    /**
     * @return String
     */
    public String getCheckEntryMode() {
        return checkEntryMode;
    }

    /**
     * @param checkEntryMode
     */
    public void setCheckEntryMode(String checkEntryMode) {
        this.checkEntryMode = checkEntryMode;
    }

    /**
     * @return boolean
     */
    public boolean isCheckEntryDetailMode() {
        return CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(getCheckEntryMode());
    }
    
    /**
     * @return current List of baseline checks for use in update detection
     */
    public List getBaselineChecks() {
        return baselineChecks;
    }

    /**
     * Sets the current List of baseline checks to the given List
     * 
     * @param baselineChecks
     */
    public void setBaselineChecks(List baselineChecks) {
        this.baselineChecks = baselineChecks;
    }

    /**
     * @param index
     * @return true if a baselineCheck with the given index exists
     */
    public boolean hasBaselineCheck(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineChecks.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty Checks as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @return baseline Check at the given index
     */
    public Check getBaselineCheck(int index) {
        while (baselineChecks.size() <= index) {
            baselineChecks.add(new CheckBase());
        }
        return (Check) baselineChecks.get(index);
    }

    /**
     * Gets the financialDocumentStatusMessage attribute. 
     * 
     * @return Returns the financialDocumentStatusMessage.
     */
    public String getFinancialDocumentStatusMessage() {
        String financialDocumentStatusMessage = "";
        CashReceiptDocument crd = getCashReceiptDocument();
        String financialDocumentStatusCode = crd.getDocumentHeader().getFinancialDocumentStatusCode();
        if(financialDocumentStatusCode.equals(Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED)) {
            financialDocumentStatusMessage = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.CashReceipt.MSG_VERIFIED_BUT_NOT_AWAITING_DEPOSIT);
        } else if(financialDocumentStatusCode.equals(Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_DEPOSITED)) {
            // get at the CR header so that we can get at the Cash Mgmt doc nbr
            HashMap primaryKeys = new HashMap();
            primaryKeys.put(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, crd.getFinancialDocumentNumber());
            CashReceiptHeader crh = (CashReceiptHeader) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(CashReceiptHeader.class, primaryKeys);
            if(!crh.getDepositCashReceiptControl().isEmpty()) {
                DepositCashReceiptControl dpcrc = (DepositCashReceiptControl) crh.getDepositCashReceiptControl().get(0);  //retrieve any in the list
                Deposit dp = dpcrc.getDeposit();
                String cmdFinancialDocNbr = dp.getCashManagementDocument().getFinancialDocumentNumber();
                
                String loadCMDocUrl = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.CashManagement.URL_LOAD_DOCUMENT_CASH_MGMT);
                loadCMDocUrl = StringUtils.replace(loadCMDocUrl, "{0}", cmdFinancialDocNbr);
                    
                financialDocumentStatusMessage = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.CashReceipt.MSG_VERIFIED_AND_AWAITING_DEPOSIT);
                financialDocumentStatusMessage = StringUtils.replace(financialDocumentStatusMessage, "{0}", loadCMDocUrl);
            }
        } else if(financialDocumentStatusCode.equals(Constants.DOCUMENT_STATUS_CD_APPROVED_PROCESSED)) {
            // get at the CR header so that we can get at the Cash Mgmt doc nbr
            HashMap primaryKeys = new HashMap();
            primaryKeys.put(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, crd.getFinancialDocumentNumber());
            CashReceiptHeader crh = (CashReceiptHeader) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(CashReceiptHeader.class, primaryKeys);
            if(!crh.getDepositCashReceiptControl().isEmpty()) {
                DepositCashReceiptControl dpcrc = (DepositCashReceiptControl) crh.getDepositCashReceiptControl().get(0);  //retrieve any in the list
                Deposit dp = dpcrc.getDeposit();
                String cmdFinancialDocNbr = dp.getCashManagementDocument().getFinancialDocumentNumber();
                
                String loadCMDocUrl = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.CashManagement.URL_LOAD_DOCUMENT_CASH_MGMT);
                loadCMDocUrl = StringUtils.replace(loadCMDocUrl, "{0}", cmdFinancialDocNbr);
            
                financialDocumentStatusMessage = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.CashReceipt.MSG_VERIFIED_AND_DEPOSITED);
                financialDocumentStatusMessage = StringUtils.replace(financialDocumentStatusMessage, "{0}", loadCMDocUrl);
            }
        }
        return financialDocumentStatusMessage;
    }
}