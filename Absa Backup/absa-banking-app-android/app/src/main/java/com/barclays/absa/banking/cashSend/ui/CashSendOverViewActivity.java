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
package com.barclays.absa.banking.cashSend.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryResult;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.databinding.CashsendOverviewActivityBinding;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.SureCheckUtils;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.utils.extensions.StringExtensions;

@Deprecated
public class CashSendOverViewActivity extends ConnectivityMonitorActivity {

    private CashsendOverviewActivityBinding binding;
    private boolean isCashSendToSelf;
    private boolean shouldUseResultStub;
    private String cashSendAmount;
    private boolean isCashSendPlus = false;
    private CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation;
    private CashSendBeneficiaryResult cashSendBeneficiaryResult;
    private final CashSendInteractor cashSendInteractor = new CashSendInteractor();

    private final ExtendedResponseListener<CashSendBeneficiaryResult> cashSendResponseListener = new ExtendedResponseListener<CashSendBeneficiaryResult>() {

        @Override
        public void onSuccess(final CashSendBeneficiaryResult successResponse) {
            cashSendBeneficiaryResult = successResponse;
            cashSendSureCheckDelegate.processSureCheck(CashSendOverViewActivity.this, successResponse, () -> launchResultScreen());
        }
    };

    private final SureCheckDelegate cashSendSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            shouldUseResultStub = true;
            requestSendBenCashSendResult();
        }

        @Override
        public void onSureCheckRejected() {
            setupFailureScreen(false);
        }

        @Override
        public void onSureCheckFailed() {
            setupFailureScreen(true);
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(CashSendOverViewActivity.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeviceProfilingInteractor().notifyTransaction();
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.cashsend_overview_activity, null, false);
        setContentView(binding.getRoot());

        isCashSendPlus = getIntent().getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);

        cashSendResponseListener.setView(this);
        cashSendBeneficiaryConfirmation = (CashSendBeneficiaryConfirmation) getIntent().getSerializableExtra(RESULT);

        setToolBarBack(getResources().getString(R.string.cash_send_confirm));

        mScreenName = BMBConstants.CASHSEND_OVERVIEW_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_OVERVIEW_CONST, BMBConstants.CASHSEND_CONST,
                BMBConstants.TRUE_CONST);

        onPopulateView(cashSendBeneficiaryConfirmation);
        binding.cashSendConfirmButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            if (getAppCacheService().isInNoPrimaryDeviceState()) {
                getAppCacheService().setReturnToScreen(CashSendOverViewActivity.class);
                showNoPrimaryDeviceScreen();
            } else {
                requestSendBenCashSendResult();
            }
        });

        setupTalkBack();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String cashSendBeneficiary = binding.cashSendBeneficiary.getContentTextViewValue();
            final String cashSendAmount = binding.cashSendAmount.getContentTextViewValue();
            final String cashSendMobileNumber = binding.cashSendMobileNumber.getContentTextViewValue();
            final String cashSendBeneficiaryReference = binding.cashSendReference.getContentTextViewValue();
            final String cashsendAccountUsed = binding.cashSendAccountNumber.getContentTextViewValue();
            final String cashSendAtmAccessPin = binding.cashSendPin.getContentTextViewValue();

            binding.cashSendBeneficiary.setContentDescription(getString(R.string.talkback_cashsend_overview_recipient, cashSendBeneficiary));
            binding.cashSendBeneficiary.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_recipient, cashSendBeneficiary));
            binding.cashSendBeneficiary.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_recipient, cashSendBeneficiary));
            binding.cashSendMobileNumber.setContentDescription(getString(R.string.talkback_cashsend_overview_mobile_Number, cashSendMobileNumber));
            binding.cashSendMobileNumber.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_mobile_Number, cashSendMobileNumber));
            binding.cashSendMobileNumber.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_mobile_Number, cashSendMobileNumber));
            binding.cashSendAccountNumber.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_account, cashsendAccountUsed));
            binding.cashSendAccountNumber.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_account, cashsendAccountUsed));
            binding.cashSendAccountNumber.setContentDescription(getString(R.string.talkback_cashsend_overview_account, cashsendAccountUsed));
            binding.cashSendAmount.setContentDescription(getString(R.string.talkback_cashsend_overview_amount_sending, AccessibilityUtils.getTalkBackRandValueFromString(cashSendAmount)));
            binding.cashSendAmount.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_amount_sending, AccessibilityUtils.getTalkBackRandValueFromString(cashSendAmount)));
            binding.cashSendAmount.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_amount_sending, AccessibilityUtils.getTalkBackRandValueFromString(cashSendAmount)));
            binding.cashSendReference.setContentDescription(getString(R.string.talkback_cashsend_overview_my_reference, cashSendBeneficiaryReference));
            binding.cashSendReference.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_my_reference, cashSendBeneficiaryReference));
            binding.cashSendReference.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_my_reference, cashSendBeneficiaryReference));
            binding.cashSendPin.setContentDescription(getString(R.string.talkback_cashsend_overview_my_atm_access_pin, AccessibilityUtils.getTalkbackPinNumberFromString(cashSendAtmAccessPin)));
            binding.cashSendPin.getContentTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_my_atm_access_pin, AccessibilityUtils.getTalkbackPinNumberFromString(cashSendAtmAccessPin)));
            binding.cashSendPin.getLabelTextView().setContentDescription(getString(R.string.talkback_cashsend_overview_my_atm_access_pin, AccessibilityUtils.getTalkbackPinNumberFromString(cashSendAtmAccessPin)));
            binding.cashSendConfirmButton.setContentDescription(getString(R.string.talkback_cashsend_overview_confirm_cashsend));
        }
    }

    private void onPopulateView(CashSendBeneficiaryConfirmation beneficiaryConfirmation) {
        isCashSendToSelf = this.getIntent().getBooleanExtra(IS_SELF, false);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(BENEFICIARY_IMG_DATA)) {
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PREPAID_PURCHASE_OVERVIEW_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
        }
        String accountType = beneficiaryConfirmation.getAccountType();
        String accountNumber = StringExtensions.toFormattedAccountNumber(beneficiaryConfirmation.getFromAccountNumber());
        binding.cashSendAccountNumber.setContentText(String.format("%s (%s)", accountType, accountNumber));

        if (isCashSendToSelf) {
            binding.cashSendBeneficiary.setContentText(CustomerProfileObject.getInstance().getCustomerName());
        } else {
            binding.cashSendBeneficiary.setContentText(beneficiaryConfirmation.getBeneficiaryName());
        }

        binding.cashSendMobileNumber.setContentText(StringExtensions.toFormattedCellphoneNumber(beneficiaryConfirmation.getCellNumber()));
        binding.cashSendAmount.setContentText(TextFormatUtils.formatCashSendAmount(beneficiaryConfirmation.getAmount()));
        this.cashSendAmount = beneficiaryConfirmation.getAmount().toString();
        binding.cashSendReference.setContentText(beneficiaryConfirmation.getMyReference());
        if (!this.getIntent().getStringExtra(ATM_ACCESS_PIN_KEY).equalsIgnoreCase(""))
            binding.cashSendPin.setContentText(this.getIntent().getStringExtra(ATM_ACCESS_PIN_KEY));
    }

    private void requestSendBenCashSendResult() {
        cashSendInteractor.performCashSendToBeneficiary(isCashSendPlus, cashSendBeneficiaryConfirmation.getTxnReference(), shouldUseResultStub, cashSendResponseListener);
    }

    private void launchResultScreen() {
        if (SureCheckUtils.isResponseSuccessSureCheck(cashSendBeneficiaryResult, this)) {
            if (cashSendBeneficiaryResult != null) {
                String cashSendBeneficiaryResultMessage = cashSendBeneficiaryResult.getMessage();
                if (cashSendBeneficiaryResultMessage != null && cashSendBeneficiaryResultMessage.contains("Payment limit exceeded")) {
                    cashSendBeneficiaryResult.setMessage(getString(R.string.cashsend_more_than_available_limit));
                }
                if (AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(cashSendBeneficiaryResultMessage)) {
                    if (isCashSendToSelf) {
                        AnalyticsUtil.INSTANCE.tagCashSend("SendCashToMyselfAuthorisationPending_ScreenDisplayed");
                    } else {
                        AnalyticsUtil.INSTANCE.tagCashSend("SendCashToBeneficiaryAuthorisationPending_ScreenDisplayed");
                    }
                    Intent cashSendIntent = new Intent(this, DualAuthPaymentPendingActivity.class);
                    cashSendIntent.putExtra(TRANSACTION_TYPE, TRANSACTION_TYPE_CASH_SEND);
                    startActivity(cashSendIntent);
                } else {
                    Intent cashSendResultIntent = new Intent(CashSendOverViewActivity.this, CashSendResultActivity.class);
                    cashSendResultIntent.putExtra(CASHSEND_AMOUNT, cashSendAmount);
                    cashSendResultIntent.putExtra(ATM_ACCESS_PIN_KEY, this.getIntent().getStringExtra(ATM_ACCESS_PIN_KEY));
                    cashSendResultIntent.putExtra(IS_SELF, isCashSendToSelf);
                    cashSendResultIntent.putExtra(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus);
                    cashSendResultIntent.putExtra(AppConstants.RESULT, cashSendBeneficiaryResult);
                    startActivity(cashSendResultIntent);
                }
            }
        }
    }

    private void setupFailureScreen(final boolean isSureCheckFailure) {
        GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();

        Intent intent = new Intent(CashSendOverViewActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);

        if (isSureCheckFailure) {
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed);
        } else {
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected);
        }
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        startActivity(intent);
    }
}

