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
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffConfirmation;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffResult;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.databinding.CashsendOverviewActivityBinding;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.SureCheckUtils;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.utils.extensions.StringExtensions;

@Deprecated
public class CashSendOnceOffConfirmActivity extends ConnectivityMonitorActivity {
    private CashsendOverviewActivityBinding binding;
    CashSendOnceOffResult cashSendOnceOffResult;
    private CashSendOnceOffConfirmation cashSendOnceOffConfirmation;
    private String ATMPin;
    private String beneficiaryName;
    private String cashSendAmount;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isCashSendPlus = false;
    private final CashSendInteractor cashSendInteractor = new CashSendInteractor();
    private final SureCheckDelegate cashSendSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            handler.postDelayed(() -> requestCashSendOnceOffResult(), 250);
        }

        @Override
        public void onSureCheckFailed() {
            launchSureCheckFailedResultScreen("");
        }

        @Override
        public void onSureCheckFailed(String errorMessage) {
            launchSureCheckFailedResultScreen(errorMessage);
        }
    };

    private final ExtendedResponseListener<CashSendOnceOffResult> cashSendResponseListener = new ExtendedResponseListener<CashSendOnceOffResult>() {
        @Override
        public void onSuccess(final CashSendOnceOffResult successResponse) {
            cashSendSureCheckDelegate.processSureCheck(CashSendOnceOffConfirmActivity.this, successResponse, () -> {
                cashSendOnceOffResult = successResponse;
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    launchTransactionResultScreen();
                } else {
                    cashSendSureCheckDelegate.onSureCheckFailed(successResponse.getMessage());
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.cashsend_overview_activity, null, false);
        setContentView(binding.getRoot());

        cashSendResponseListener.setView(this);
        mScreenName = BMBConstants.CASHSEND_OVERVIEW_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_OVERVIEW_CONST, BMBConstants.CASHSEND_CONST,
                BMBConstants.TRUE_CONST);

        ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra(AppConstants.RESULT);
        this.cashSendOnceOffConfirmation = (CashSendOnceOffConfirmation) (responseObject);

        if (this.getIntent().getExtras() != null) {
            ATMPin = this.getIntent().getExtras().getString(ATM_PIN_KEY);
            isCashSendPlus = getIntent().getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);
        }

        setToolBarBack(R.string.confirm_cash_send);
        onPopulateView();
        setupTalkBack();
    }

    private void setupTalkBack() {
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

    private void onPopulateView() {
        String accountType = cashSendOnceOffConfirmation.getAccountType();
        String accountNumber = StringExtensions.toFormattedAccountNumber(cashSendOnceOffConfirmation.getFromAccountNumber());
        this.cashSendAmount = cashSendOnceOffConfirmation.getAmount().toString();
        binding.cashSendAccountNumber.setContentText(String.format("%s (%s)", accountType, accountNumber));

        beneficiaryName = this.cashSendOnceOffConfirmation.getFirstName() + " " + this.cashSendOnceOffConfirmation.getSurname();
        binding.cashSendBeneficiary.setContentText(beneficiaryName);

        binding.cashSendMobileNumber.setContentText(StringExtensions.toFormattedCellphoneNumber(cashSendOnceOffConfirmation.getCellNumber()));
        binding.cashSendAmount.setContentText(TextFormatUtils.formatCashSendAmount(cashSendOnceOffConfirmation.getAmount()));
        binding.cashSendReference.setContentText(cashSendOnceOffConfirmation.getMyReference());
        binding.cashSendPin.setContentText(ATMPin);

        binding.cashSendConfirmButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            if (getAppCacheService().isInNoPrimaryDeviceState()) {
                getAppCacheService().setReturnToScreen(CashSendOnceOffConfirmActivity.class);
                showNoPrimaryDeviceScreen();
            } else {
                requestCashSendOnceOffResult();
            }
        });
    }

    private void requestCashSendOnceOffResult() {
        getAppCacheService().setSureCheckDelegate(cashSendSureCheckDelegate);
        cashSendInteractor.performOnceOffCashSend(cashSendOnceOffConfirmation.getTxnReference(), cashSendResponseListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_SUCCESS_SURE_CHECK) {
            requestCashSendOnceOffResult();
        } else if (resultCode == RESULT_CODE_REJECTED_SURE_CHECK) {
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.surecheck_error_unabletocomplete));
        }
    }

    private void launchTransactionResultScreen() {
        if (SureCheckUtils.isResponseSuccessSureCheck(cashSendOnceOffResult, this)) {
            if (cashSendOnceOffResult != null) {
                String onceOffCashSendResultMessage = cashSendOnceOffResult.getMessage();
                if (!TextUtils.isEmpty(onceOffCashSendResultMessage) && AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(onceOffCashSendResultMessage)) {
                    AnalyticsUtil.INSTANCE.tagCashSend("OnceOffAuthorisationPending_ScreenDisplayed");
                    Intent airtimeIntent = new Intent(this, DualAuthPaymentPendingActivity.class);
                    airtimeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    airtimeIntent.putExtra(TRANSACTION_TYPE, TRANSACTION_TYPE_CASH_SEND);
                    startActivity(airtimeIntent);
                } else {
                    Intent cashSendResultIntent = new Intent(getApplicationContext(), CashSendResultActivity.class);
                    cashSendResultIntent.putExtra(CASHSEND_AMOUNT, cashSendAmount);
                    cashSendResultIntent.putExtra(ATM_ACCESS_PIN_KEY, ATMPin);
                    cashSendResultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    cashSendResultIntent.putExtra(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus);
                    cashSendResultIntent.putExtra(AppConstants.RESULT, cashSendOnceOffResult);
                    startActivity(cashSendResultIntent);
                }
            } else {
                launchSureCheckFailedResultScreen(getString(R.string.generic_error));
            }
        } else {
            launchSureCheckFailedResultScreen(getString(R.string.generic_error));
        }
    }

    private Intent buildResultIntent(String message) {
        GenericResultActivity.topOnClickListener = v -> {
            Intent cashSendIntent = new Intent(CashSendOnceOffConfirmActivity.this, CashSendActivity.class);
            cashSendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
            startActivity(cashSendIntent);
        };

        GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();

        Intent intent = new Intent(CashSendOnceOffConfirmActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        if (!TextUtils.isEmpty(message)) {
            intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, message);
        }
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.btn_make_another_cash_send);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        return intent;
    }

    private void launchSureCheckFailedResultScreen(String message) {
        if (message != null && message.contains("Payment limit exceeded")) {
            message = (getString(R.string.cashsend_more_than_available_limit));
        }

        Intent intent = buildResultIntent(message);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed);
        startActivity(intent);
    }
}
