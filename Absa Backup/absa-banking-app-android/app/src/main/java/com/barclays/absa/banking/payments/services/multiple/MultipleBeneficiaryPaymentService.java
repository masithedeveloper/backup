/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.payments.services.multiple;

import com.barclays.absa.banking.boundary.model.BeneficiaryTransactionDetails;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentsBeneficiaryList;
import com.barclays.absa.banking.payments.services.multiple.dto.PaymentResult;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;

import java.util.Map;

public interface MultipleBeneficiaryPaymentService {
    String OP0528_MULTIPLE_BENEFICIARIES_PAYMENT_RESULT = "OP0528";
    String ANALYTICS_CHANNEL_NAME = "Multiple payments";

    void getBeneficiaryDetails(String beneficiaryId, String beneficiaryType, ExtendedResponseListener<BeneficiaryTransactionDetails> beneficiaryDetailsResponseListener);
    void multipleBeneficiaryPerformPayment(String transactionReferenceId, ExtendedResponseListener<PaymentResult> multipleBeneficiaryPerformPaymentResponseListener);
    void validateMultipleBeneficiaryPayment(MultiplePaymentsBeneficiaryList beneficiaryDetails, Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap, ExtendedResponseListener<ValidateMultipleBeneficiariesPayment> multipleBeneficiaryPaymentResponseListener);
    void clientAgreementDetails(ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsResponseListener);
}