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
package com.barclays.absa.banking.beneficiaries.ui;

import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.data.ResponseObject;

interface BeneficiaryDetailsView extends BaseView {
    void navigateToPaymentBeneficiaryDetail();

    void navigateToCashSendBeneficiaryDetail(BeneficiaryDetailObject successResponse);

    void navigateToAirtimeDetailView(ResponseObject successResponse);

    void navigateToElectricityBeneficiaryDetail();

    void setTransactionList(BeneficiaryDetailObject successResponse);

    void navigateBackToBeneficiaryList(BeneficiaryListObject successResponse);

    void navigateToBeneficiaryTransactionItem(ViewTransactionDetails successResponse);

    void setBeneficiaryImage(String beneficiaryImage);
}
