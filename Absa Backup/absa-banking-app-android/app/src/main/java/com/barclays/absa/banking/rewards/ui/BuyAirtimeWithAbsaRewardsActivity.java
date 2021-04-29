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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionAirtime;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionVoucher;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.services.RewardsRedemptionInteractor;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.RewardsViewModel;
import com.barclays.absa.banking.rewards.ui.rewardsHub.State;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccountBalanceUpdateHelper;
import com.barclays.absa.utils.SureCheckUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import styleguide.forms.SelectorList;

public class BuyAirtimeWithAbsaRewardsActivity extends BaseActivity implements BuyAirtimeWithAbsaRewardsView {
    private RedeemRewards rewardsRedeem;
    private List<RewardsRedemptionVoucher> voucherList;
    private int selectedVoucherIndex;
    private RewardsRedemptionAirtime selectedNetworkOperator;
    private String selectedProvider;
    private String selectedVoucherDetailsName;
    private String enterCellphoneNumber;
    private RewardsRedeemConfirmation rewardsRedeemConfirmationResponse;
    private RewardsRedeemResult redeemRewardsResult;
    private final Handler handler = new Handler();
    private long startTimeMillis;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    private final ExtendedResponseListener<RewardsRedeemResult> redeemRewardsResponseListener = new ExtendedResponseListener<RewardsRedeemResult>(this) {
        @Override
        public void onSuccess(final RewardsRedeemResult successResponse) {
            dismissProgressDialog();
            cashSendSureCheckDelegate.processSureCheck(BuyAirtimeWithAbsaRewardsActivity.this, successResponse, () -> {
                redeemRewardsResult = successResponse;
                launchResultScreen();
            });
        }
    };

    private final SureCheckDelegate cashSendSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            handler.postDelayed(() -> {
                recordMonitoringEvent();
                redeemRewards();
            }, 250);
        }

        @Override
        public void onSureCheckRejected() {
            showFailureScreen(false);
        }

        @Override
        public void onSureCheckFailed() {
            showFailureScreen(true);
        }
    };

    private void showFailureScreen(boolean isFailureResult) {
        GenericResultActivity.topOnClickListener = v -> {
            Intent intent = new Intent(BuyAirtimeWithAbsaRewardsActivity.this, BuyPrepaidActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            intent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME);
            startActivity(intent);
        };
        GenericResultActivity.bottomOnClickListener = v -> navigateToHomeScreenWithoutReloadingAccounts();
        Intent intent = new Intent(BuyAirtimeWithAbsaRewardsActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_unsuccessful_white_large);
        if (isFailureResult) {
            intent.putExtra(GenericResultActivity.IS_ERROR, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed);
        } else {
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected);
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        }
        intent.putExtra(GenericResultActivity.IS_CALL_SUPPORT_GONE, true);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.another_purchase);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        startActivity(intent);
    }

    private void launchResultScreen() {
        if (SureCheckUtils.isResponseSuccessSureCheck(redeemRewardsResult, this)) {
            mSiteSection = BMBConstants.BUY_PREPAID_REWARDS_CHANNEL_BUY_CONSTANT;
            Intent genericResultScreen = new Intent(this, GenericResultActivity.class);
            if (BMBConstants.SUCCESS.equalsIgnoreCase(redeemRewardsResult.getRegistrationStatus()) || BMBConstants.SUCCESS.equalsIgnoreCase(redeemRewardsResult.getTransactionStatus())) {
                if (redeemRewardsResult.getRegistrationMessage() != null && !redeemRewardsResult.getRegistrationMessage().isEmpty() && !redeemRewardsResult.getRegistrationMessage().contains(BMBConstants.SUCCESS)) {
                    genericResultScreen.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, redeemRewardsResult.getRegistrationMessage());
                }
                GenericResultActivity.topOnClickListener = v -> navigateToMakeAnotherPurchaseScreen();
                GenericResultActivity.bottomOnClickListener = v -> loadAccountsClearingRewardsBalanceAndShowHomeScreen();

                genericResultScreen.putExtra(GenericResultActivity.IS_SUCCESS, true);
                genericResultScreen.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.purchase_success);
                genericResultScreen.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.another_purchase);
                genericResultScreen.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);

                mScreenName = BMBConstants.PURCHASE_SUCCESSFUL_CONSTANT;

            } else {
                GenericResultActivity.bottomOnClickListener = v -> navigateToHomeScreenWithoutReloadingAccounts();

                genericResultScreen.putExtra(GenericResultActivity.IS_FAILURE, true);
                genericResultScreen.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.purchase_unsuccessful);
                genericResultScreen.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, redeemRewardsResult.getRegistrationMessage());
                genericResultScreen.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);

                mScreenName = BMBConstants.PURCHASE_UNSUCCESSFUL_CONSTANT;
            }
            AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
            genericResultScreen.putExtra(AppConstants.RESULT, redeemRewardsResult);
            startActivity(genericResultScreen);
        }
    }

    public void navigateToMakeAnotherPurchaseScreen() {
        new AccountBalanceUpdateHelper(this).updateRewardsBalance(new AccountRefreshInterface() {
            @Override
            public void onSuccess() {
                RewardsViewModel rewardsViewModel = new ViewModelProvider(BuyAirtimeWithAbsaRewardsActivity.this).get(RewardsViewModel.class);
                rewardsViewModel.resetState();

                BaseActivity topMostActivity = (BaseActivity) BMBApplication.getInstance().getTopMostActivity();
                rewardsViewModel.getState().observe(topMostActivity, state -> {
                    if (state == State.QUEUE_COMPLETED) {
                        dismissProgressDialog();
                        Intent in = new Intent(topMostActivity, BuyAirtimeWithAbsaRewardsActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        finish();
                    } else if (state == State.FAILED) {
                        dismissProgressDialog();
                        loadAccountsAndGoHome();
                    }
                });

                rewardsViewModel.fetchRedeemRewards();
            }

            @Override
            public void onFailure() {
                dismissProgressDialog();
                loadAccountsAndGoHome();
            }
        });
    }

    private void recordMonitoringEvent() {
        long endTimeMillis = System.currentTimeMillis();
        long elapsedTime = endTimeMillis - startTimeMillis;
        Map<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME, elapsedTime);
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_REDEMPTION_TYPE, MonitoringService.MONITORING_EVENT_ATTRIBUTE_REDEMPTION_TYPE_PREPAID);
        new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_REWARDS_REDEMPTION, eventData);
    }

    private final ExtendedResponseListener<RewardsRedeemConfirmation> rewardsRedeemConfirmationResponseListener = new ExtendedResponseListener<RewardsRedeemConfirmation>() {

        @Override
        public void onSuccess(final RewardsRedeemConfirmation successResponse) {
            dismissProgressDialog();
            rewardsRedeemConfirmationResponse = successResponse;
            navigateToPrepaidRewardsPurchaseOverviewFragment();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_airtime_with_absa_rewards_activity);
        if (savedInstanceState == null) {
            navigateToPrepaidRewardsPurchaseFragment();
        }
        setUpToolbar();
        rewardsRedeemConfirmationResponseListener.setView(this);
        startTimeMillis = System.currentTimeMillis();

        if (rewardsCacheService.getRewardsRedemption() != null) {
            rewardsRedeem = rewardsCacheService.getRewardsRedemption();
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
        }

        mScreenName = BMBConstants.ABSA_REWARDS_BUY_AIRTIME_CONSTANT;
        mSiteSection = BMBConstants.BUY_PREPAID_REWARDS_CHANNEL_CONSTANT;
        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolBarBack(getString(R.string.prepaid_purchase_details), v -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.prepaid_purchase_details));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void navigateToPrepaidRewardsPurchaseFragment() {
        startFragment(PrepaidRewardsPurchaseFragment.newInstance(), true, AnimationType.FADE);
    }

    @Override
    public void navigateToPrepaidRewardsPurchaseOverviewFragment() {
        RewardsPurchaseDetailsObject purchaseDetailsObject = getRewardsConfirmationDetails();
        PrepaidRewardsPurchaseOverviewFragment prepaidRewardsPurchaseFragment = PrepaidRewardsPurchaseOverviewFragment.newInstance(purchaseDetailsObject, getRewardsRedeemConfirmationResponse());
        startFragment(prepaidRewardsPurchaseFragment, true, AnimationType.FADE);
    }

    @Override
    public SelectorList<PrepaidVoucherWrapper> getProviderVoucherList() {
        SelectorList<PrepaidVoucherWrapper> selectorVoucherList = new SelectorList<>();
        PrepaidVoucherWrapper prepaidVoucherWrapper;
        Amount amount;
        if (getVoucherList() != null) {
            for (int i = 0; i < getVoucherList().size(); i++) {
                prepaidVoucherWrapper = new PrepaidVoucherWrapper();
                amount = new Amount();
                amount.setAmount(getVoucherList().get(i).getAccrualCost().getAmount());
                amount.setCurrency(getVoucherList().get(i).getAccrualCost().getCurrency());
                prepaidVoucherWrapper.setProviderVoucher(getVoucherList().get(i).getName());
                prepaidVoucherWrapper.setVoucherDiscount(TextFormatUtils.formatBasicAmount(amount));
                selectorVoucherList.add(prepaidVoucherWrapper);
            }
        }
        return selectorVoucherList;
    }

    @Override
    public void setSelectedProvider(String selectedProvider) {
        this.selectedProvider = selectedProvider;
    }

    @Override
    public void setVoucherDetailsName(String selectedVoucherDetailsName) {
        this.selectedVoucherDetailsName = selectedVoucherDetailsName;
    }

    @Override
    public void updateVoucherDetails() {
        List<RewardsRedemptionAirtime> networkOperatorsList = rewardsRedeem != null ? rewardsRedeem.getNetworkList() : null;
        if (rewardsRedeem != null && rewardsRedeem.getNetworkList() != null)
            for (int i = 0; i < rewardsRedeem.getNetworkList().size(); i++) {
                if (networkOperatorsList != null) {
                    String currentProvider = networkOperatorsList.get(i).getNetworkType();
                    if (!TextUtils.isEmpty(selectedProvider) && selectedProvider.equals(currentProvider)) {
                        this.setSelectedNetworkOperator(networkOperatorsList.get(i));
                        break;
                    }
                }
            }
        if (getSelectedNetworkOperator() != null) {
            setVoucherList(getSelectedNetworkOperator().getVoucherDetails());
        }
    }

    @Override
    public void updateSelectedVoucherIndex() {
        String currentVoucherDetailsName;
        for (int i = 0; i < getVoucherList().size(); i++) {
            currentVoucherDetailsName = getVoucherList().get(i).getName();
            if (currentVoucherDetailsName.equals(selectedVoucherDetailsName)) {
                selectedVoucherIndex = i;
            }
        }
    }

    @Override
    public void setEnterCellphoneNumber(String enterCellphoneNumber) {
        this.enterCellphoneNumber = enterCellphoneNumber;
    }

    private RewardsPurchaseDetailsObject getRewardsConfirmationDetails() {
        RewardsPurchaseDetailsObject detailsObject = new RewardsPurchaseDetailsObject();
        detailsObject.setRedeemMethod(BMBConstants.REDEMPTION_CODE_AIRTIME);
        detailsObject.setRedeemID(getSelectedNetworkOperator().getVoucherDetails().get(selectedVoucherIndex).getId());
        detailsObject.setPhoneNumber(enterCellphoneNumber);
        detailsObject.setNetworkOperator(getSelectedNetworkOperator().getNetworkType());
        detailsObject.setRedemptionVoucherName(selectedVoucherDetailsName);
        detailsObject.setRedemptionAirtimeFaceValue(getSelectedNetworkOperator().getVoucherDetails().get(selectedVoucherIndex).getFaceValue().getAmount());
        return detailsObject;
    }

    @Override
    public void buyAirtimeWithAbsaRewards() {
        Bundle bundle = new Bundle();
        bundle.putString("REDEEM_METHOD", BMBConstants.REDEMPTION_CODE_AIRTIME);
        bundle.putString("REDEMPTION_ID", getSelectedNetworkOperator().getVoucherDetails().get(selectedVoucherIndex).getId());
        bundle.putString("PHONE_NUMBER", enterCellphoneNumber);
        bundle.putString("NETWORK_OPERATOR", getSelectedNetworkOperator().getNetworkType());
        bundle.putString("REDEMPTION_VOUCHER_NAME", selectedVoucherDetailsName);
        bundle.putString("REDEMPTION_AIRTIME_FACEVALUE", getSelectedNetworkOperator().getVoucherDetails().get(selectedVoucherIndex).getFaceValue().getAmount());
        bundle.putString("REDEMPTION_AIRTIME_ACTUALCOST", getSelectedNetworkOperator().getVoucherDetails().get(selectedVoucherIndex).getAccrualCost().getAmount());
        new RewardsRedemptionInteractor().redeemRewardsConfirmation(bundle, rewardsRedeemConfirmationResponseListener);
    }

    @Override
    public RewardsRedeemConfirmation getRewardsRedeemConfirmationResponse() {
        return rewardsRedeemConfirmationResponse;
    }

    public void redeemRewards() {
        if (rewardsRedeemConfirmationResponse != null) {
            new RewardsRedemptionInteractor().redeemRewardsResult(rewardsRedeemConfirmationResponse.getTxnReferenceID(), redeemRewardsResponseListener);
        } else {
            showFailureScreen(true);
        }
    }

    @Override
    public void redeemRewards(RewardsRedeemConfirmation rewardsRedeemConfirmation) {
        rewardsRedeemConfirmationResponse = rewardsRedeemConfirmation;
        redeemRewards();
    }

    @Override
    public void onBackPressed() {
        if (PrepaidRewardsPurchaseFragment.class.getName().equals(getCurrentFragmentName())) {
            finish();
            return;
        } else if (PrepaidRewardsPurchaseOverviewFragment.class.getName().equals(getCurrentFragmentName())) {
            removeFragments(1);
            return;
        }
        super.onBackPressed();
    }

    public RewardsRedemptionAirtime getSelectedNetworkOperator() {
        return selectedNetworkOperator;
    }

    public void setSelectedNetworkOperator(RewardsRedemptionAirtime selectedNetworkOperator) {
        this.selectedNetworkOperator = selectedNetworkOperator;
    }

    public List<RewardsRedemptionVoucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(List<RewardsRedemptionVoucher> voucherList) {
        this.voucherList = voucherList;
    }
}