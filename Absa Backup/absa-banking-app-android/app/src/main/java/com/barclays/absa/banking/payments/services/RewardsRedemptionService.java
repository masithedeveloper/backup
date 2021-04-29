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
package com.barclays.absa.banking.payments.services;

import android.os.Bundle;

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionInfo;

public interface RewardsRedemptionService {
    String OP0912_REDEEM_REWARDS_CONFIRM = "OP0912";
    String OP0913_REDEEM_REWARDS_RESULT = "OP0913";

    void pullRewards(ExtendedResponseListener<RedeemRewards> responseListener);
    void redeemRewardsResult(String transactionReferenceId, ExtendedResponseListener<RewardsRedeemResult> redeemRewardsResultResponseListener);
    void redeemRewardsResult(String transactionReferenceId, boolean isFirstCall, ExtendedResponseListener<RewardsRedeemResult> redeemRewardsResultResponseListener);
    void redeemVoucher(VoucherRedemptionInfo voucherRedemptionInfo, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener);
    void redeemRewardsConfirmation(Bundle bundle, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener);
    void redeemRewardsConfirmation(RewardsRedeemConfirmation rewardsRedeemConfirmation, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener);
    void convertRewards(Bundle bundle, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener);
}