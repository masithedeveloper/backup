/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.framework.BaseView;

interface CreditCardVCLIncomeSourceView extends BaseView {
    void navigateToBankListLayout(BankDetails successResponse);

    void navigateToBranchList(BankBranches successResponse);

    void navigateToAccountTypeSelector();

    void navigateToAdjustCreditLimitScreen();
}