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

package com.barclays.absa.banking.rewards.ui;

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;

import styleguide.forms.SelectorList;

public interface BuyAirtimeWithAbsaRewardsView {
    void navigateToPrepaidRewardsPurchaseFragment();
    void navigateToPrepaidRewardsPurchaseOverviewFragment();
    void setSelectedProvider(String selectedProvider);
    void updateVoucherDetails();
    SelectorList<PrepaidVoucherWrapper> getProviderVoucherList();
    void setVoucherDetailsName(String selectedVoucherDetailsName);
    void updateSelectedVoucherIndex();
    void setEnterCellphoneNumber(String enterCellphoneNumber);
    void buyAirtimeWithAbsaRewards();
    RewardsRedeemConfirmation getRewardsRedeemConfirmationResponse();
    void redeemRewards(RewardsRedeemConfirmation rewardsRedeemConfirmation);
    void showNoPrimaryDeviceScreen();
}