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
/*
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.document.service.impl;

import java.text.MessageFormat;
import java.util.List;

import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.dataaccess.AchAccountNumberDao;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupHistoryDao;
import org.kuali.kfs.pdp.document.service.PaymentMaintenanceService;
import org.kuali.kfs.pdp.exception.CancelPaymentException;
import org.kuali.kfs.pdp.exception.PdpException;
import org.kuali.kfs.pdp.service.EnvironmentService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.ReferenceService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author HSTAPLET
 */
@Transactional
public class PaymentMaintenanceServiceImpl implements PaymentMaintenanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
    private PaymentDetailDao paymentDetailDao;
    private AchAccountNumberDao achAccountNumberDao;
    private PaymentGroupHistoryDao paymentGroupHistoryDao;
    private ReferenceService referenceService;
    private PendingTransactionService glPendingTransactionService;
    private EnvironmentService environmentService;
    private MailService mailService;
    private ParameterService parameterService;
    private KualiCodeService kualiCodeService;
    private BankService bankService;
    
    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, UniversalUser user) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistoryDao.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        paymentGroup.setPaymentStatus((PaymentStatus) code);
        paymentGroupDao.save(paymentGroup);
        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, UniversalUser user, PaymentGroupHistory paymentGroupHistory) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistoryDao.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        if (paymentGroup.getPaymentStatus() != ((PaymentStatus) code)) {
            paymentGroup.setPaymentStatus((PaymentStatus) code);
        }
        paymentGroupDao.save(paymentGroup);

        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * cancelPendingPayment() This method cancels the pending payment of the given payment id if the following rules apply. -
     * Payment status must be: "open", "held", or "pending/ACH".
     * 
     * @param paymentGroupId (Integer) Primary key of the PaymentGroup that the Payment Detail to be canceled belongs to.
     * @param paymentDetailId (Integer) Primary key of the PaymentDetail that was actually canceled.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, UniversalUser user, SecurityRecord sr) throws PdpException {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelPendingPayment() Enter method to cancel pending payment with group id = " + paymentGroupId);
        LOG.debug("cancelPendingPayment() payment detail id being cancelled = " + paymentDetailId);
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelPendingPayment() Pending payment not found; throw exception.");
            throw new CancelPaymentException("Pending payment not found.");
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();
        if (!(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentStatus))) {
            LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; continue with cancel.");
            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (sr.isTaxHoldersRole() || sr.isSysAdminRole()) {
                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);
                    // set primary cancel indicator for EPIC to use
                    PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                    }
                    paymentDetailDao.save(pd);
                    sendCancelEmail(paymentGroup, note, user);
                    LOG.debug("cancelPendingPayment() Pending payment cancelled and mail was sent; exit method.");
                }
                else {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
                    throw new CancelPaymentException("Invalid status to cancel pending payment.");
                }
            }
            else if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus) || PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (sr.isCancelRole()) {
                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);
                    // set primary cancel indicator for EPIC to use
                    PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                        PaymentNoteText payNoteText = new PaymentNoteText();
                        payNoteText.setCustomerNoteLineNbr(pd.getNotes().size()+1);
                        payNoteText.setCustomerNoteText(note);
                        pd.addNote(payNoteText);
                    }
                    paymentDetailDao.save(pd);
                    LOG.debug("cancelPendingPayment() Pending payment cancelled; exit method.");
                }
                else {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
                    throw new CancelPaymentException("Invalid status to cancel pending payment.");
                }
            }
            else {
                LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; cannot cancel payment in this status");
                throw new CancelPaymentException("Invalid status to cancel pending payment.");
            }
        }
        else {
            LOG.debug("cancelPendingPayment() Pending payment group has already been cancelled; exit method.");
        }
    }// end cancelPendingPayment()

    /**
     * holdPendingPayment() This method holds pending payment of the given payment id if the following rules apply. - Payment status
     * must be: "open".
     * 
     * @param paymentGroupId (Integer) Primary key of the PaymentGroup that the Payment Detail to be held belongs to.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void holdPendingPayment(Integer paymentGroupId, String note, UniversalUser user) throws PdpException {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("holdPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("holdPendingPayment() Pending payment not found; throw exception.");
            throw new CancelPaymentException("Pending payment not found.");
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();
        if (!(PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus))) {
            if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus)) {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; continue with hold.");
                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.HELD_CD, PdpConstants.PaymentChangeCodes.HOLD_CHNG_CD, note, user);
                LOG.debug("holdPendingPayment() Pending payment was put on hold; exit method.");
            }
            else {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; cannot hold payment in this status");
                throw new CancelPaymentException("Invalid status to hold pending payment.");
            }
        }
        else {
            LOG.debug("holdPendingPayment() Pending payment group has already been held; exit method.");
        }

    }// end holdPendingPayment()

    /**
     * removeHoldPendingPayment() This method removes holds on pending payments of the given payment id if the following rules
     * apply. - Payment status must be: "held".
     * 
     * @param paymentGroupId (Integer) Primary key of the PaymentGroup that the Payment Detail to be un-held belongs to.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     * @param sr (SecurityRecord) User's rights
     */
    public void removeHoldPendingPayment(Integer paymentGroupId, String note, UniversalUser user, SecurityRecord sr) throws PdpException {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("removeHoldPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("removeHoldPendingPayment() Payment not found; throw exception.");
            throw new CancelPaymentException("Pending payment not found.");
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();
        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; continue with hold removal.");
            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (sr.isTaxHoldersRole() || sr.isSysAdminRole()) {
                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);
                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
                    throw new CancelPaymentException("Invalid status to hold pending payment.");
                }
            }
            else if (PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (sr.isHoldRole()) {
                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);
                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
                    throw new CancelPaymentException("Invalid status to hold pending payment.");
                }
            }
            else {
                LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; cannot remove hold on payment in this status");
                throw new CancelPaymentException("Invalid status to hold pending payment.");
            }
        }
        else {
            LOG.debug("removeHoldPendingPayment() Pending payment group has already been un-held; exit method.");
        }
    }// end removeHoldPendingPayment()

    public void changeImmediateFlag(Integer paymentGroupId, String note, UniversalUser user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("changeImmediateFlag() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);

        paymentGroupHistory.setOrigProcessImmediate(paymentGroup.getProcessImmediate());

        if (paymentGroup.getProcessImmediate().equals(new Boolean("True"))) {
            paymentGroup.setProcessImmediate(new Boolean("False"));
        }
        else {
            paymentGroup.setProcessImmediate(new Boolean("True"));
        }

        changeStatus(paymentGroup, paymentGroup.getPaymentStatus().getCode(), PdpConstants.PaymentChangeCodes.CHANGE_IMMEDIATE_CHNG_CD, note, user, paymentGroupHistory);
        LOG.debug("changeImmediateFlag() exit method.");
    }

    /**
     * cancelDisbursement() This method cancels all disbursements with the same disbursment number as that of the given payment id
     * if the following rules apply. - Payment status must be: "extr".
     * 
     * @param paymentGroupId (Integer) Primary key of the PaymentGroup that the Payment Detail to be cancelled belongs to.
     * @param paymentDetailId (Integer) Primary key of the PaymentDetail that was actually cancelled.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, UniversalUser user) throws PdpException {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelDisbursement() Disbursement not found; throw exception.");
            throw new CancelPaymentException("Disbursement not found.");
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();
        if (!(PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null)) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
                List<PaymentGroup> allDisbursementPaymentGroups = paymentGroupDao.getByDisbursementNumber(paymentGroup.getDisbursementNbr());

                for (PaymentGroup element : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();
                    if ((element.getDisbursementType() != null) && (element.getDisbursementType().getCode().equals("CHCK"))) {
                        pgh.setPmtCancelExtractStat(new Boolean("False"));
                    }
                    changeStatus(element, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, note, user, pgh);
                    glPendingTransactionService.generateCancellationGeneralLedgerPendingEntry(element);
                }
                // set primary cancel indicator for EPIC to use
                PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
                if (pd != null) {
                    pd.setPrimaryCancelledPayment(Boolean.TRUE);
                }
                paymentDetailDao.save(pd);

                LOG.debug("cancelDisbursement() Disbursement cancelled; exit method.");
            }
            else {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment in this status");
                throw new CancelPaymentException("Invalid disbursement to cancel.");
            }
        }
        else {
            LOG.debug("cancelDisbursement() Disbursement has already been cancelled; exit method.");
        }
    }// end cancelDisbursement()

    /**
     * cancelReissueDisbursement() This method cancels and re-opens all disbursements with the same disbursment number as that of
     * the given payment id if the following rules apply. - Payment status must be: "extr".
     * 
     * @param paymentGroupId (Integer) Primary key of the PaymentGroup that the Payment Detail to be canceled/reissued belongs to.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void cancelReissueDisbursement(Integer paymentGroupId, String note, UniversalUser user) throws PdpException {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelReissueDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
        PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelReissueDisbursement() Disbursement not found; throw exception.");
            throw new CancelPaymentException("Disbursement not found.");
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();
        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null)) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
                List<PaymentGroup> allDisbursementPaymentGroups = paymentGroupDao.getByDisbursementNumber(paymentGroup.getDisbursementNbr());

                for (PaymentGroup pg : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if ((pg.getDisbursementType() != null) && (pg.getDisbursementType().getCode().equals("CHCK"))) {
                        pgh.setPmtCancelExtractStat(new Boolean("False"));
                    }
                    pgh.setOrigProcessImmediate(pg.getProcessImmediate());
                    pgh.setOrigPmtSpecHandling(pg.getPymtSpecialHandling());
                    pgh.setBank(pg.getBank());
                    pgh.setOrigPaymentDate(pg.getPaymentDate());
                    pgh.setOrigDisburseDate(pg.getDisbursementDate());
                    pgh.setOrigAchBankRouteNbr(pg.getAchBankRoutingNbr());
                    pgh.setOrigDisburseNbr(pg.getDisbursementNbr());
                    pgh.setOrigAdviceEmail(pg.getAdviceEmailAddress());
                    pgh.setDisbursementType(pg.getDisbursementType());
                    pgh.setProcess(pg.getProcess());

                    glPendingTransactionService.generateReissueGeneralLedgerPendingEntry(pg);
                    LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");
                    AchAccountNumber achAccountNumber = pg.getAchAccountNumber();
                    if (achAccountNumber != null) {
                        achAccountNumberDao.delete(achAccountNumber);
                        pg.setAchAccountNumber(null);
                    }

                    // if bank functionality is not enabled or the group bank is inactive clear bank code
                    if (!bankService.isBankSpecificationEnabled() || !pg.getBank().isActive()) {
                        pg.setBank(null);
                    }
                    
                    pg.setDisbursementDate(null);
                    pg.setAchBankRoutingNbr(null);
                    pg.setAchAccountType(null);
                    pg.setPhysCampusProcessCd(null);
                    pg.setDisbursementNbr(null);
                    pg.setAdviceEmailAddress(null);
                    pg.setDisbursementType(null);
                    pg.setProcess(null);
                    changeStatus(pg, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT, note, user, pgh);
                }

                LOG.debug("cancelReissueDisbursement() Disbursement cancelled and reissued; exit method.");
            }
            else {
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");
                throw new CancelPaymentException("Invalid disbursement to cancel and reissue.");
            }
        }
        else {
            LOG.debug("cancelReissueDisbursement() Disbursement already cancelled and reissued; exit method.");
        }
    }// end cancelReissueDisbursement()

    public void sendCancelEmail(PaymentGroup paymentGroup, String note, UniversalUser user) {
        LOG.debug("sendCancelEmail() starting");
        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Cancelled Payment by Tax");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Cancelled Payment by Tax");
        }

        CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
        String toAddresses = cp.getAdviceReturnEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }
        // message.addToAddress(cp.getAdviceReturnEmailAddr());

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_CANCEL_EMAIL_LIST);
        String ccAddressList[] = ccAddresses.split(",");

        if (ccAddressList.length > 0) {
            for (int i = 0; i < ccAddressList.length; i++) {
                if (ccAddressList[i] != null) {
                    message.addCcAddress(ccAddressList[i].trim());
                }
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        StringBuffer body = new StringBuffer();
        //TODO: this if statement seems unnecessary
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");          
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");   
        }
        body.append(note + "\n\n");
        String taxEmail = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_GROUP_EMAIL_ADDRESS);
        if (GeneralUtilities.isStringEmpty(taxEmail)) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_2);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");   
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_3);
            body.append(MessageFormat.format(messageKey, new Object[]{taxEmail}) + " \n\n");   
        }
        //TODO: unnecessary if statement?
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");  
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");  
        }
        for (PaymentDetail pd : paymentGroup.getPaymentDetails()) {
            String payeeMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYEE_NAME);
            String netPaymentAccountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_NET_PAYMENT_AMOUNT);
            String sourceDocumentNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_SOURCE_DOCUMENT_NUMBER);
            String invoiceNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_INVOICE_NUMBER);
            String purchaseOrderNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PURCHASE_ORDER_NUMBER);
            String paymentDetailIdMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_DETAIL_ID);
            
            body.append(MessageFormat.format(payeeMessageKey, new Object[]{paymentGroup.getPayeeName()}) + " \n");  
            body.append(MessageFormat.format(netPaymentAccountMessageKey, new Object[]{pd.getNetPaymentAmount()}) + " \n");  
            body.append(MessageFormat.format(sourceDocumentNumberMessageKey, new Object[]{pd.getCustPaymentDocNbr()}) + " \n");  
            body.append(MessageFormat.format(invoiceNumberMessageKey, new Object[]{pd.getInvoiceNbr()}) + " \n");  
            body.append(MessageFormat.format(purchaseOrderNumberMessageKey, new Object[]{pd.getPurchaseOrderNbr()}) + " \n");  
            body.append(MessageFormat.format(paymentDetailIdMessageKey, new Object[]{pd.getId()}) + " \n");  

        }
        //TODO: unnecessary if statement?
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_INFORMATION_HEADER);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");  

        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_INFORMATION_HEADER);
            body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");  
        }

        String batchIdMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_ID);
        String chartMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_CHART);
        String organizationMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_ORGANIZATION);
        String subUnitMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_SUB_UNIT);
        String creationDateMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_CREATION_DATE);
        String paymentCountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_COUNT);
        String paymentTotalAmountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_TOTAL_AMOUNT);
        
        body.append(MessageFormat.format(batchIdMessageKey, new Object[]{paymentGroup.getBatch().getId()}) + " \n");  
        body.append(MessageFormat.format(chartMessageKey, new Object[]{cp.getChartCode()}) + " \n");  
        body.append(MessageFormat.format(organizationMessageKey, new Object[]{cp.getOrgCode()}) + " \n");  
        body.append(MessageFormat.format(subUnitMessageKey, new Object[]{cp.getSubUnitCode()}) + " \n");  
        body.append(MessageFormat.format(creationDateMessageKey, new Object[]{paymentGroup.getBatch().getCustomerFileCreateTimestamp()}) + " \n");  
        body.append(MessageFormat.format(paymentCountMessageKey, new Object[]{paymentGroup.getBatch().getPaymentCount()}) + " \n");  
        body.append(MessageFormat.format(paymentTotalAmountMessageKey, new Object[]{paymentGroup.getBatch().getPaymentTotalAmount()}) + " \n");  
        
        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setPaymentGroupDao(PaymentGroupDao dao) {
        paymentGroupDao = dao;
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setPaymentDetailDao(PaymentDetailDao dao) {
        paymentDetailDao = dao;
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setPaymentGroupHistoryDao(PaymentGroupHistoryDao dao) {
        paymentGroupHistoryDao = dao;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setReferenceService(ReferenceService service) {
        referenceService = service;
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setAchAccountNumberDao(AchAccountNumberDao dao) {
        achAccountNumberDao = dao;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setGlPendingTransactionService(PendingTransactionService service) {
        glPendingTransactionService = service;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setEnvironmentService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    /**
     * Sets the bankService attribute value.
     * 
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }
}
