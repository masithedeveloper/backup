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

package com.barclays.absa.banking.rewards.ui.redemptions.donations;

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.payments.services.RewardsRedemptionInteractor;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.rewards.ui.redemptions.RedeemRewardsContract;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class DonateToCharityConfirmationPresenter implements RedeemRewardsContract.DonateToCharityConfirmationPresenter {
    private RedeemRewardsContract.DonateToCharityConfirmationView view;
    private WeakReference<RedeemRewardsContract.DonateToCharityConfirmationView> weakReference;
    private RewardsRedemptionInteractor rewardsInteractor;
    private SureCheckDelegate rewardsSureCheckDeligate;

    private ExtendedResponseListener<RewardsRedeemResult> rewardsRedeemResponseListener = new ExtendedResponseListener<RewardsRedeemResult>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final RewardsRedeemResult successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                rewardsSureCheckDeligate.processSureCheck(view.getBaseActivity(), successResponse, () -> view.launchSuccessScreen(successResponse));
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            view.launchFailureScreen();
        }
    };

    private final ExtendedResponseListener<RewardsRedeemConfirmation> rewardsRedeemConfirmationResponseListener = new ExtendedResponseListener<RewardsRedeemConfirmation>() {
        @Override
        public void onSuccess(final RewardsRedeemConfirmation rewardsRedeemConfirmation) {
            view = weakReference.get();
            if (view != null) {
                rewardsInteractor.redeemRewardsResult(rewardsRedeemConfirmation.getTxnReferenceID(), rewardsRedeemResponseListener);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
            }
        }
    };

    DonateToCharityConfirmationPresenter(RedeemRewardsContract.DonateToCharityConfirmationView view, SureCheckDelegate sureCheckDelegate) {
        weakReference = new WeakReference<>(view);
        rewardsInteractor = new RewardsRedemptionInteractor();
        rewardsSureCheckDeligate = sureCheckDelegate;
        rewardsRedeemResponseListener.setView(view);
        rewardsRedeemConfirmationResponseListener.setView(view);
    }

    @Override
    public void redeemRewardsRequest(@NotNull RewardsRedeemConfirmation rewardsRedeemConfirmation) {
        rewardsInteractor.redeemRewardsConfirmation(rewardsRedeemConfirmation, rewardsRedeemConfirmationResponseListener);
    }
}
