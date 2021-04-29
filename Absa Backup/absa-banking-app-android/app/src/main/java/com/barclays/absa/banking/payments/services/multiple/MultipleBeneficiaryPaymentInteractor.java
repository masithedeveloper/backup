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

import com.barclays.absa.banking.beneficiaries.services.dto.BeneficiaryTransactionDetailsRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.ClientAgreementDetailsRequest;
import com.barclays.absa.banking.boundary.model.BeneficiaryTransactionDetails;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentResultRequest;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentsBeneficiaryList;
import com.barclays.absa.banking.payments.services.multiple.dto.PaymentResult;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultiplePaymentsRequest;

import java.util.Map;

public class MultipleBeneficiaryPaymentInteractor extends AbstractInteractor implements MultipleBeneficiaryPaymentService {
    @Override
    public void getBeneficiaryDetails(String transactionReferenceId, String beneficiaryType, ExtendedResponseListener<BeneficiaryTransactionDetails> beneficiaryDetailsResponseListener) {
        BeneficiaryTransactionDetailsRequest<BeneficiaryTransactionDetails> beneficiaryDetailsRequest = new BeneficiaryTransactionDetailsRequest<>(transactionReferenceId, beneficiaryType, beneficiaryDetailsResponseListener);
        submitRequest(beneficiaryDetailsRequest);
    }

    @Override
    public void multipleBeneficiaryPerformPayment(String beneficiaryId, ExtendedResponseListener<PaymentResult> multipleBeneficiaryPerformPaymentResponseListener) {
        MultiplePaymentResultRequest<PaymentResult> multiplePaymentResultRequest = new MultiplePaymentResultRequest<>(beneficiaryId, multipleBeneficiaryPerformPaymentResponseListener);
        submitRequest(multiplePaymentResultRequest);
    }

    @Override
    public void validateMultipleBeneficiaryPayment(MultiplePaymentsBeneficiaryList beneficiaryDetails, Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap, ExtendedResponseListener<ValidateMultipleBeneficiariesPayment> multipleBeneficiaryPaymentResponseListener) {
        ValidateMultiplePaymentsRequest<ValidateMultipleBeneficiariesPayment> validateMultiplePaymentRequest = new ValidateMultiplePaymentsRequest<>(beneficiaryDetails, beneficiaryPaymentConfirmationObjectMap, multipleBeneficiaryPaymentResponseListener);
        submitRequest(validateMultiplePaymentRequest);
    }

    @Override
    public void clientAgreementDetails(ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsResponseListener) {
        ClientAgreementDetailsRequest<ClientAgreementDetails> clientAgreementDetailsRequest = new ClientAgreementDetailsRequest<>(clientAgreementDetailsResponseListener);
        submitRequest(clientAgreementDetailsRequest);
    }
}