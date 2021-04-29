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
import com.barclays.absa.banking.buy.services.airtime.dto.RedeemRewardsConfirmationRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.RedeemRewardsResultRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.rewards.services.dto.RedeemVoucherRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsRedeemConfirmRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsRedeemRequest;
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionInfo;

public class RewardsRedemptionInteractor extends AbstractInteractor implements RewardsRedemptionService {

    @Override
    public void pullRewards(ExtendedResponseListener<RedeemRewards> responseListener) {
        RewardsRedeemRequest<RedeemRewards> rewardsRedeemRequest = new RewardsRedeemRequest<>(responseListener);
        submitRequest(rewardsRedeemRequest);
    }

    @Override
    public void redeemRewardsResult(String transactionReferenceId, ExtendedResponseListener<RewardsRedeemResult> redeemRewardsResultResponseListener) {
        RedeemRewardsResultRequest<RewardsRedeemResult> request = new RedeemRewardsResultRequest<>(transactionReferenceId, redeemRewardsResultResponseListener);
        if (BuildConfigHelper.STUB) {
            String mockFile = firstCall ? "rewards/op0913_redeem_rewards_result.json" : "rewards/op0913_redeem_rewards_confirmation.json";
            submitRequest(request, mockFile);
        } else {
            submitRequest(request);
        }
    }

    @Override
    public void redeemRewardsResult(String transactionReferenceId, boolean isFirstCall, ExtendedResponseListener<RewardsRedeemResult> redeemRewardsResultResponseListener) {
        this.firstCall = isFirstCall;
        redeemRewardsResult(transactionReferenceId, redeemRewardsResultResponseListener);
    }

    @Override
    public void redeemVoucher(VoucherRedemptionInfo voucherRedemptionInfo, ExtendedResponseListener<RewardsRedeemConfirmation> redeemVoucherResponseListener){
        RedeemVoucherRequest<RewardsRedeemConfirmation> redeemVoucherRequest = new RedeemVoucherRequest<>(voucherRedemptionInfo,  redeemVoucherResponseListener);
        submitRequest(redeemVoucherRequest);
    }

    @Override
    public void redeemRewardsConfirmation(RewardsRedeemConfirmation rewardsRedeemConfirmation, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener) {
        RedeemRewardsConfirmationRequest<RewardsRedeemConfirmation> request = new RedeemRewardsConfirmationRequest<>(rewardsRedeemConfirmation, redeemRewardsConfirmResponseListener);
        submitRequest(request);
    }

    @Override
    public void redeemRewardsConfirmation(Bundle bundle, ExtendedResponseListener<RewardsRedeemConfirmation> redeemRewardsConfirmResponseListener) {
        RedeemRewardsConfirmationRequest<RewardsRedeemConfirmation> request = new RedeemRewardsConfirmationRequest<>(bundle, redeemRewardsConfirmResponseListener);
        submitRequest(request);
    }

    @Override
    public void convertRewards(Bundle bundle, ExtendedResponseListener<RewardsRedeemConfirmation> confirmRedeemRewardsResponseListener) {
        RewardsRedeemConfirmRequest<RewardsRedeemConfirmation> rewardsRedeemConfirmRequest = new RewardsRedeemConfirmRequest<>(confirmRedeemRewardsResponseListener, bundle);
        submitRequest(rewardsRedeemConfirmRequest);
    }
}
