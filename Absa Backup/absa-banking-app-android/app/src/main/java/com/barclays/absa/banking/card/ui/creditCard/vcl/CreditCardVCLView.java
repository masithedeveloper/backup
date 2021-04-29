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

import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.framework.BaseView;

interface CreditCardVCLView extends BaseView {

    void setToolbarText(String toolbarText, boolean showCancelToolbar);

    void navigateToNextFragment(Fragment fragment);

    void setCurrentProgress(int stepNumber);

    void hideProgressIndicator();

    void hideKeyBoard();

    void updateVCLDataModel(VCLParcelableModel vclDataModel);

    VCLParcelableModel getVCLDataModel();

    void navigateToPreviousScreen();

    void creditLimitIncreaseRejected();

    void navigateToVCLAcceptedScreen();

    void navigateToVCLPendingScreen();

    void navigateToVCLDeclineScreen();

    void hideToolbar();

    void setOnBackPressedListener(BaseBackPressedListener onBackPressedListener);

    void navigateToFailureScreen();

    void navigateToInvalidAccountScreen();
}
