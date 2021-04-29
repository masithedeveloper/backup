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

import android.os.Bundle;
import android.os.Handler;

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.payments.services.RewardsRedemptionInteractor;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RedeemPointsPresenter {

    private RewardsRedemptionInteractor rewardsInteractor;
    private WeakReference<RedeemShoppingPointsView> RedeemShoppingPointsViewWeakReference;
    private RewardsRedeemConfirmation redeemConfirmation;
    private long startTimeMillis;
    private Handler handler = new Handler();
    private String redemptionType = "ConvertToPoints";
    private SureCheckDelegate sureCheckDelegate;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public RedeemPointsPresenter(RedeemShoppingPointsView shoppingPointsView) {
        rewardsInteractor = new RewardsRedemptionInteractor();
        RedeemShoppingPointsViewWeakReference = new WeakReference<>(shoppingPointsView);
    }

    private void initSureCheckDelegate(BaseActivity baseActivity) {
        sureCheckDelegate = new SureCheckDelegate(baseActivity) {
            @Override
            public void onSureCheckProcessed() {
                RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
                if (shoppingPointsView != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (redeemConfirmation != null) {
                                if (appCacheService.isInNoPrimaryDeviceState()) {
                                    shoppingPointsView.showNoPrimaryDeviceScreen();
                                } else {
                                    startTimeMillis = System.currentTimeMillis();
                                    rewardsInteractor.redeemRewardsResult(redeemConfirmation.getTxnReferenceID(), redeemResultExtendedResponseListener);
                                }
                            }
                        }
                    }, 250);
                }
            }

            @Override
            public void onSureCheckFailed() {
                super.onSureCheckFailed();
                RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
                if (shoppingPointsView != null) {
                    shoppingPointsView.launchSureCheckFailedResultScreen();
                }
            }

            @Override
            public void onSureCheckCancelled() {
                super.onSureCheckCancelled();
                RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
                if (shoppingPointsView != null) {
                    shoppingPointsView.launchSureCheckFailedResultScreen();
                }
            }
        };
    }

    private ExtendedResponseListener<RewardsRedeemResult> redeemResultExtendedResponseListener = new ExtendedResponseListener<RewardsRedeemResult>() {
        @Override
        public void onSuccess(final RewardsRedeemResult successResponse) {
            recordMonitoringEvent();
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                initSureCheckDelegate((BaseActivity) shoppingPointsView);
                shoppingPointsView.dismissProgressDialog();
                sureCheckDelegate.processSureCheck((BaseActivity) shoppingPointsView, successResponse, shoppingPointsView::navigateToRedeemPointsSuccessScreen);
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                shoppingPointsView.dismissProgressDialog();
                shoppingPointsView.launchSureCheckFailedResultScreen();
            }
        }
    };

    private void recordMonitoringEvent() {
        long endTimeMillis = System.currentTimeMillis();
        long elapsedTime = endTimeMillis - startTimeMillis;
        Map<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME, elapsedTime);
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_REDEMPTION_TYPE, redemptionType);
        new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_REWARDS_REDEMPTION, eventData);
    }

    private final ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener = new ExtendedResponseListener<RedeemRewards>() {
        @Override
        public void onSuccess(final RedeemRewards redeemRewards) {
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                shoppingPointsView.dismissProgressDialog();
                shoppingPointsView.navigateToRedeemShoppingPointsInputFragment(redeemRewards);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                shoppingPointsView.dismissProgressDialog();
            }
        }
    };

    private final ExtendedResponseListener<RewardsRedeemConfirmation> rewardsRedeemConfirmationResponseListener = new ExtendedResponseListener<RewardsRedeemConfirmation>() {
        @Override
        public void onSuccess(final RewardsRedeemConfirmation rewardsRedeemConfirmation) {
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                shoppingPointsView.dismissProgressDialog();
                redeemConfirmation = rewardsRedeemConfirmation;
                redeemRewards();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
            if (shoppingPointsView != null) {
                shoppingPointsView.dismissProgressDialog();
                shoppingPointsView.navigateToRedeemPointsFailureScreen();
            }
        }
    };

    public void pullRewards() {
        RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
        if (shoppingPointsView != null) {
            shoppingPointsView.showProgressDialog();
        }
        rewardsInteractor.pullRewards(redeemRewardsResponseListener);
    }

    public void convertRewards(Bundle bundle) {
        RedeemShoppingPointsView shoppingPointsView = RedeemShoppingPointsViewWeakReference.get();
        if (shoppingPointsView != null) {
            shoppingPointsView.showProgressDialog();
        }
        rewardsInteractor.convertRewards(bundle, rewardsRedeemConfirmationResponseListener);
    }

    private void redeemRewards() {
        rewardsInteractor.redeemRewardsResult(redeemConfirmation.getTxnReferenceID(), redeemResultExtendedResponseListener);
    }
}