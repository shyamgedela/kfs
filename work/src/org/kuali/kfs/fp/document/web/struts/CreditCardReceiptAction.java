/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.fp.document.validation.impl.CreditCardReceiptDocumentRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This is the action class for the CreditCardReceiptDocument.
 */
public class CreditCardReceiptAction extends CapitalAccountingLinesActionBase {
    /**
     * Adds handling for credit card receipt amount updates.
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CreditCardReceiptForm ccrForm = (CreditCardReceiptForm) form;

        if (ccrForm.hasDocumentId()) {
            CreditCardReceiptDocument ccrDoc = ccrForm.getCreditCardReceiptDocument();

            ccrDoc.setTotalCreditCardAmount(ccrDoc.calculateCreditCardReceiptTotal()); // recalc b/c changes to the amounts could
            // have happened

            //set bank
            ccrDoc.setBank(SpringContext.getBean(BankService.class).getByPrimaryId(ccrDoc.getCreditCardReceiptBankCode()));
        }

        // proceed as usual
        return super.execute(mapping, form, request, response);
    }

    /**
     * Adds a CreditCardDetail instance created from the current "new creditCardReceipt" line to the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCreditCardReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CreditCardReceiptForm ccrForm = (CreditCardReceiptForm) form;
        CreditCardReceiptDocument ccrDoc = ccrForm.getCreditCardReceiptDocument();

        CreditCardDetail newCreditCardReceipt = ccrForm.getNewCreditCardReceipt();
        ccrDoc.prepareNewCreditCardReceipt(newCreditCardReceipt);

        // creditCardReceipt business rules
        boolean rulePassed = validateNewCreditCardReceipt(newCreditCardReceipt);
        if (rulePassed) {
            // add creditCardReceipt
            ccrDoc.addCreditCardReceipt(newCreditCardReceipt);

            // clear the used creditCardReceipt
            ccrForm.setNewCreditCardReceipt(new CreditCardDetail());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected creditCardReceipt (line) from the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteCreditCardReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CreditCardReceiptForm ccrForm = (CreditCardReceiptForm) form;
        CreditCardReceiptDocument ccrDoc = ccrForm.getCreditCardReceiptDocument();

        int deleteIndex = getLineToDelete(request);
        // delete creditCardReceipt
        ccrDoc.removeCreditCardReceipt(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method validates a new credit card receipt detail record.
     *
     * @param creditCardReceipt
     * @return boolean
     */
    protected boolean validateNewCreditCardReceipt(CreditCardDetail creditCardReceipt) {
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT);
        boolean isValid = CreditCardReceiptDocumentRuleUtil.validateCreditCardReceipt(creditCardReceipt);
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT);
        return isValid;
    }

    /**
     * Do initialization for a new credit card receipt
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CreditCardReceiptDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }
}
