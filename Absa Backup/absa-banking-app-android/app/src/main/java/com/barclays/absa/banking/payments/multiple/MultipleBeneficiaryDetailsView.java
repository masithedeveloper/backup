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
package com.barclays.absa.banking.payments.multiple;

import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryTransactionDetails;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;

public interface MultipleBeneficiaryDetailsView extends BaseView {
    void doNavigation(ValidateMultipleBeneficiariesPayment successResponse);

    void navigateToPaymentDetails(BeneficiaryTransactionDetails beneficiaryDetailObject);

    void navigateToCutOffScreen();

    void navigateToChooseAccountScreen();

    void openFromAccountChooserActivity();

    void showUpdateAmountsDialog();

    void showInsufficientFundsDialog();

    void deselectBeneficiary(BeneficiaryObject beneficiaryObject, double totalAmount);

    void showAgreementError(int errorMessage);

    void updateClientAgreementData(ClientAgreementDetails clientAgreementDetails);
}
