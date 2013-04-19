/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import static org.kuali.kfs.module.tem.TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.DisbursementVoucherPaymentMethods;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.beans.BeanUtils;

public abstract class TEMReimbursementDocument extends TravelDocumentBase {

    private String paymentMethod = DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE;

    @Column(name = "PAYMENT_METHOD", nullable = true, length = 15)
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();

        //clear expenses
        setActualExpenses(new ArrayList<ActualExpense>());
        setPerDiemExpenses(new ArrayList<PerDiemExpense>());

        //default dates if null
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        if (getTripBegin() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        }
        if (getTripEnd() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            setTripEnd(new Timestamp(calendar.getTimeInMillis()));
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);

        //modify for any of the reimbursement doc to use the payment option instead of default
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(getPaymentMethod());
    }

    /**
     * change to generate the GLPE on ACTUAL EXPENSE based on Reimbursable minus Advance and its only ONE entry to
     * DV clearning account
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#generateDocumentGeneralLedgerPendingEntries
     * (org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - start");

        boolean success = true;
        boolean doGenerate = true;

        success = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);

        KualiDecimal reimbursableToTraveler = getTravelReimbursementService().getReimbursableToTraveler(this);

        //if there is any at all reimbursable to the traveler, there will be a DV generated later, so we need to funnel through
        // the travel clearing
        if (reimbursableToTraveler.isPositive()){
            //get a source accounting line for DV clearing details
            SourceAccountingLine sourceDetail = getTravelDisbursementService().getTravelClearingGLPESourceDetail();
            sourceDetail.setAmount(reimbursableToTraveler);
            TemSourceAccountingLine temSouceDetail = new TemSourceAccountingLine();
            BeanUtils.copyProperties(sourceDetail, temSouceDetail);

            success &= super.generateGeneralLedgerPendingEntries(temSouceDetail, sequenceHelper);
        }

        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - end");

        return success;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        //if the GLPE is an entry for the Travel Clearing Account, the explicit entry should be a credit
        SourceAccountingLine sourceDetail = getTravelDisbursementService().getTravelClearingGLPESourceDetail();
        if (postable.getChartOfAccountsCode().equals(sourceDetail.getChartOfAccountsCode()) &&
                postable.getAccountNumber().equals(sourceDetail.getAccountNumber()) && postable.getFinancialObjectCode().equals(sourceDetail.getFinancialObjectCode())){
            explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReimbursableTotal()
     */
    @Override
    public KualiDecimal getReimbursableTotal() {
        KualiDecimal eligible = getEligibleAmount();
        final KualiDecimal expenseLimit = getExpenseLimit();

        if (expenseLimit != null && expenseLimit.doubleValue() > 0) {
            return eligible.isGreaterThan(expenseLimit) ? expenseLimit : eligible;
        }

        return eligible;
    }

    /**
     * Get eligible amount
     *
     * @return
     */
    public KualiDecimal getEligibleAmount(){
        return getApprovedAmount().subtract(getCTSTotal()).subtract(getCorporateCardTotal());
    }

    /**
     * Total amount to be paid to Vendor
     *
     * @return
     */
    public KualiDecimal getTotalPaidAmountToVendor() {
        KualiDecimal totalPaidAmountToVendor = KualiDecimal.ZERO;
        List<Document> relatedDisbursementList = getTravelDocumentService().getDocumentsRelatedTo(this, DISBURSEMENT_VOUCHER_DOCTYPE);
        for (Document document : relatedDisbursementList) {
            if (document instanceof DisbursementVoucherDocument) {
                DisbursementVoucherDocument dv = (DisbursementVoucherDocument) document;
                if (dv.getDocumentHeader().getWorkflowDocument().isFinal()) {
                    totalPaidAmountToVendor = totalPaidAmountToVendor.add(dv.getDisbVchrCheckTotalAmount());
                }
            }
        }
        return totalPaidAmountToVendor;
    }

    /**
     * Total requested to be paid
     *
     * @return
     */
    public KualiDecimal getTotalPaidAmountToRequests() {
        KualiDecimal totalPaidAmountToRequests = KualiDecimal.ZERO;

        List<Document> relatedRequisitionDocuments = getTravelDocumentService().getDocumentsRelatedTo(this,
                TemConstants.REQUISITION_DOCTYPE);

        try {
            for (Document document : relatedRequisitionDocuments) {
                PurApRelatedViews relatedviews = ((RequisitionDocument) document).getRelatedViews();
                if (relatedviews != null && relatedviews.getRelatedPaymentRequestViews() != null && relatedviews.getRelatedPaymentRequestViews().size() > 0) {
                    List<PaymentRequestView> preqViews = relatedviews.getRelatedPaymentRequestViews();
                    for (PaymentRequestView preqView : preqViews) {
                        PaymentRequestDocument preqDocument;

                        preqDocument = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(preqView.getDocumentNumber());
                        if (preqDocument.getDocumentHeader().getWorkflowDocument().isFinal()) {
                            totalPaidAmountToRequests = totalPaidAmountToRequests.add(preqDocument.getVendorInvoiceAmount());
                        }
                    }
                }
            }
        } catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return totalPaidAmountToRequests;
    }

    /**
     *
     * @return
     */
    public KualiDecimal getReimbursableGrandTotal() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;
        grandTotal = getApprovedAmount().add(getTotalPaidAmountToVendor()).add(getTotalPaidAmountToRequests());

        if (KualiDecimal.ZERO.isGreaterThan(grandTotal)) {
            return KualiDecimal.ZERO;
        }

        return grandTotal;
    }

    /**
     *
     * @return
     */
    public boolean requiresTravelerApprovalRouting() {
        String initiator = getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String travelerID = getTraveler().getPrincipalId();

        boolean routeToTraveler = travelerID != null && !initiator.equals(travelerID);
        return routeToTraveler;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPerDiemAdjustment()
     */
    @Override
    public KualiDecimal getPerDiemAdjustment() {
        //do not use per diem adjustment
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPerDiemAdjustment(org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void setPerDiemAdjustment(KualiDecimal perDiemAdjustment) {
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getExpenseTypeCode()
     */
    @Override
    public String getExpenseTypeCode() {
        return TemConstants.ACTUAL_EXPENSE;
    }
}