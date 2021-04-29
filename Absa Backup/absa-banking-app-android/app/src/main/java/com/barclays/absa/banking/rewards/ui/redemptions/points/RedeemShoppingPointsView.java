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

package com.barclays.absa.banking.rewards.ui.redemptions.points;

import android.content.Intent;
import android.os.Bundle;

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.framework.BaseView;

public interface RedeemShoppingPointsView extends BaseView {
    void navigateToRewardsRedeemFragment();
    void navigateToRedeemShoppingPointsInputFragment(RedeemRewards redeemRewards);
    void navigateToRedeemPointsConfirmationFragment(RedeemPointsInputFields userInput);
    void navigateToRedeemPointsSuccessScreen();
    void navigateToRedeemPointsFailureScreen();
    void retrieveRewards();
    void convertRewards(Bundle bundle);
    void startActivity(Intent intent);
    Intent buildResultIntent(String message);
    void launchSureCheckFailedResultScreen();
    void showNoPrimaryDeviceScreen();
}
