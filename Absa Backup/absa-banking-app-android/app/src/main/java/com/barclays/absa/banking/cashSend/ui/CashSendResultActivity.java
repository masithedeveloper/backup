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
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffResult;
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusViewModel;
import com.barclays.absa.banking.databinding.ActivityCashSendResultBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import styleguide.utils.extensions.StringExtensions;

public class CashSendResultActivity extends BaseActivity {

    private ActivityCashSendResultBinding binding;
    private String atmAccessPin;
    private String cashSendAmount;
    private CashSendBeneficiaryResult cashSendBeneficiaryResult;
    private CashSendOnceOffResult onceoffCashSendOnceOffResult;
    private AddBeneficiaryCashSendConfirmationObject addBeneficiarySuccessObject;
    private CashSendPlusViewModel cashSendPlusViewModel;
    private boolean isCashSendPlus = false;
    private String customerName;

    private ExtendedResponseListener<BeneficiaryDetailObject> beneficiaryDetailExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailObject>() {
        @Override
        public void onSuccess(final BeneficiaryDetailObject successResponse) {
            dismissProgressDialog();
            Intent intent = new Intent(CashSendResultActivity.this, CashSendBeneficiaryActivity.class);
            intent.putExtra(RESULT, successResponse);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_cash_send_result, null, false);
        setContentView(binding.getRoot());
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        beneficiaryDetailExtendedResponseListener.setView(this);
        initViews();
        onPopulateView();
        customerName = CustomerProfileObject.getInstance().getCustomerName();
    }

    private void initViews() {
        Intent sourceIntent = getIntent();
        if (sourceIntent.hasExtra(BMBConstants.ATM_ACCESS_PIN_KEY)) {
            String atmPin = sourceIntent.getStringExtra(BMBConstants.ATM_ACCESS_PIN_KEY);
            this.cashSendAmount = sourceIntent.getStringExtra(BMBConstants.CASHSEND_AMOUNT);
            isCashSendPlus = sourceIntent.getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);
            if (isCashSendPlus) {
                cashSendPlusViewModel = new ViewModelProvider(this).get(CashSendPlusViewModel.class);
            }

            if (!TextUtils.isEmpty(atmPin)) {
                this.atmAccessPin = atmPin;
            }
        }

        binding.goBackHomeButton.setOnClickListener(v -> {
            if (isCashSendPlus) {
                if (cashSendPlusViewModel.getCheckCashSendPlusRegistrationStatusResponse().getValue() == null) {
                    cashSendPlusViewModel.sendCheckCashSendPlusRegistration();

                    cashSendPlusViewModel.getFailureResponse().observe(this, transactionResponse -> continueWithDoneAction());

                    cashSendPlusViewModel.getCheckCashSendPlusRegistrationStatusResponse().observe(this, checkCashSendPlusRegistrationStatusResponse -> {
                        getAppCacheService().setCashSendPlusRegistrationStatus(checkCashSendPlusRegistrationStatusResponse);
                        continueWithDoneAction();
                    });
                } else {
                    getAppCacheService().setCashSendPlusRegistrationStatus(cashSendPlusViewModel.getCheckCashSendPlusRegistrationStatusResponse().getValue());
                    continueWithDoneAction();
                }
            } else {
                continueWithDoneAction();
            }
        });

        binding.shareCashSendButton.setOnClickListener(v -> requestForSharePIN(atmAccessPin));
    }

    private void continueWithDoneAction() {
        showProgressDialog();
        loadAccountsAndGoHome();
    }

    private void requestForSharePIN(String atmPIN) {
        AnalyticsUtils.getInstance().trackAirDropShare();
        String msgToSend;
        if (onceoffCashSendOnceOffResult != null) {
            if (onceoffCashSendOnceOffResult.getBeneficiaryName() != null && !onceoffCashSendOnceOffResult.getBeneficiaryName().equals("null")) {
                msgToSend = String.format(getString(R.string.share_atm_pin_message),
                        (onceoffCashSendOnceOffResult.getFirstName().concat(" ").concat(onceoffCashSendOnceOffResult.getSurname())),
                        onceoffCashSendOnceOffResult.getAmount(),
                        atmPIN,
                        customerName);
            } else {
                msgToSend = String.format(getString(R.string.share_atm_pin_message),
                        (onceoffCashSendOnceOffResult.getFirstName().concat(" ").concat(onceoffCashSendOnceOffResult.getSurname())),
                        onceoffCashSendOnceOffResult.getAmount(),
                        atmPIN,
                        customerName);
            }
        } else {
            // This would be the url you are wanting to share...
            msgToSend = String.format(getString(R.string.share_atm_pin_message),
                    (cashSendBeneficiaryResult.getBeneficiaryName().concat(" ").concat(cashSendBeneficiaryResult.getSurname())),
                    cashSendBeneficiaryResult.getAmount(),
                    atmPIN,
                    customerName);
        }
        // Set the content type for the intent (*/* means ALL)
        final String contentType = "text/plain";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP;
        shareIntent.setFlags(flags);
        shareIntent.setType(contentType);
        // We couldn't get the shortlink, just use the original URL
        shareIntent.putExtra(Intent.EXTRA_TEXT, msgToSend);
        // Set the intent on the share action provider
        startActivity(shareIntent);
        //Track share event
        AnalyticsUtils.getInstance().trackAirDropShare();
    }

    public void onPopulateView() {

        boolean isCashSendToSelf = getIntent().getExtras() != null && getIntent().getExtras().getBoolean(IS_SELF);
        ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra(AppConstants.RESULT);
        String phoneNumber;
        if (responseObject instanceof CashSendOnceOffResult) {
            onceoffCashSendOnceOffResult = (CashSendOnceOffResult) responseObject;
            this.customerName = String.format("%s %s", ((CashSendOnceOffResult) responseObject).getFirstName(), ((CashSendOnceOffResult) responseObject).getSurname());
            phoneNumber = StringExtensions.toFormattedCellphoneNumber(((CashSendOnceOffResult) responseObject).getCellNumber());
            this.cashSendAmount = ((CashSendOnceOffResult) responseObject).getAmount().toString();
            if (BMBConstants.CONST_SUCCESS.equalsIgnoreCase(onceoffCashSendOnceOffResult
                    .getTransactionStatus())) {
                AnalyticsUtil.INSTANCE.tagCashSend("OnceOffSuccess_ScreenDisplayed");
                binding.shareCashSendButton.setVisibility(View.VISIBLE);
                showResultAnimation(true);
                binding.resultSuccessHeader.setText(getString(R.string.cash_send_success));

                binding.resultSuccessContent.setText(getString(R.string.cash_send_success_text, customerName, cashSendAmount, phoneNumber));
                binding.resultSuccessPin.setText(getString(R.string.cash_send_atm_pin_text, atmAccessPin));
                binding.resultSuccessPin.setVisibility(View.VISIBLE);

                if (onceoffCashSendOnceOffResult.getMessage() != null && !onceoffCashSendOnceOffResult.getMessage().contains("success")) {
                    binding.resultSuccessContent.setVisibility(View.VISIBLE);
                    binding.resultSuccessPin.setVisibility(View.VISIBLE);
                }
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND);
                AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
            } else {
                AnalyticsUtil.INSTANCE.tagCashSend("OnceOffFailure_ScreenDisplayed");
                binding.shareCashSendButton.setVisibility(View.GONE);
                binding.resultSuccessHeader.setText(getString(R.string.cash_send_fail));
                binding.resultSuccessPin.setVisibility(View.GONE);
                showResultAnimation(true);
                binding.resultSuccessContent.setText(onceoffCashSendOnceOffResult.getMessage());
            }
        } else if (responseObject instanceof AddBeneficiaryCashSendConfirmationObject) {
            addBeneficiarySuccessObject = (AddBeneficiaryCashSendConfirmationObject) responseObject;

            if (BMBConstants.CONST_SUCCESS.equalsIgnoreCase(addBeneficiarySuccessObject
                    .getStatus())) {
                mScreenName = BMBConstants.CASHSEND_SUCCESSFUL_CONST;
                mSiteSection = BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST;
                AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_SUCCESSFUL_CONST, BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST,
                        BMBConstants.TRUE_CONST);
                binding.shareCashSendButton.setVisibility(View.GONE);
                showResultAnimation(true);
                binding.resultSuccessContent.setText(getString(R.string.add_new_prepaid_success_msg));
                binding.resultSuccessContent.setText(addBeneficiarySuccessObject.getMsg());
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND);
                AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
                AnalyticsUtil.INSTANCE.tagCashSend("CashSend_BeneficiaryAddSuccess_ScreenDisplayed");
            } else {
                mScreenName = BMBConstants.ADD_BENEFICIARY_UNSUCCESSFUL_CONST;
                mSiteSection = BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST;
                AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_UNSUCCESSFUL_CONST, BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST,
                        BMBConstants.TRUE_CONST);
                binding.shareCashSendButton.setVisibility(View.GONE);
                binding.resultSuccessHeader.setText(getString(R.string.unable_to_save_prepaid_ben));
                showResultAnimation(false);
                binding.resultSuccessContent.setText(addBeneficiarySuccessObject.getMsg());
                AnalyticsUtil.INSTANCE.tagCashSend("CashSend_BeneficiaryAddFailure_ScreenDisplayed");
            }
        } else {
            cashSendBeneficiaryResult = (CashSendBeneficiaryResult) responseObject;
            String firstName = ((CashSendBeneficiaryResult) responseObject).getFirstName();
            String surname = ((CashSendBeneficiaryResult) responseObject).getSurname();

            if (firstName == null && surname == null) {
                customerName = getString(R.string.yourself);
            } else {
                customerName = String.format("%s %s", firstName, surname);
                customerName = customerName.replaceAll("null", "");
            }

            phoneNumber = StringExtensions.toFormattedCellphoneNumber(((CashSendBeneficiaryResult) responseObject).getCellNumber());
            this.cashSendAmount = ((CashSendBeneficiaryResult) responseObject).getAmount().toString();

            if (BMBConstants.CONST_SUCCESS.equalsIgnoreCase(cashSendBeneficiaryResult.getTransactionStatus())) {
                if (isCashSendToSelf) {
                    AnalyticsUtil.INSTANCE.tagCashSend("SendCashToMyselfSuccess_ScreenDisplayed");
                    binding.shareCashSendButton.setVisibility(View.GONE);
                } else {
                    AnalyticsUtil.INSTANCE.tagCashSend("SendCashToBeneficiarySuccess_ScreenDisplayed");
                }
                binding.resultSuccessHeader.setText(getString(R.string.cash_send_success));
                binding.resultSuccessContent.setText(getString(R.string.cash_send_success_text, customerName, cashSendAmount, phoneNumber));
                binding.resultSuccessPin.setText(getString(R.string.cash_send_atm_pin_text, atmAccessPin));
                binding.resultSuccessPin.setVisibility(View.VISIBLE);
                showResultAnimation(true);
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND);
                AbsaCacheManager.getInstance().setAccountsCacheStatus(false);
            } else {
                if (isCashSendToSelf) {
                    AnalyticsUtil.INSTANCE.tagCashSend("SendCashToMyselfFailure_ScreenDisplayed");
                } else {
                    AnalyticsUtil.INSTANCE.tagCashSend("SendCashToBeneficiaryFailure_ScreenDisplayed");
                }
                binding.shareCashSendButton.setVisibility(View.GONE);
                binding.resultSuccessHeader.setText(getString(R.string.cash_send_fail));
                binding.resultSuccessPin.setVisibility(View.GONE);
                showResultAnimation(false);

                String regexPattern = "(\\d+)-([a-zA-Z. ]+)(\\([A-Z]\\d+\\))";
                Pattern pattern = Pattern.compile(regexPattern);

                Matcher matcher = pattern.matcher(cashSendBeneficiaryResult.getMessage());
                if (pattern.matcher(cashSendBeneficiaryResult.getMessage()).matches() && matcher.find()) {
                    binding.resultSuccessContent.setText(matcher.group(2));
                } else {
                    binding.resultSuccessContent.setText(cashSendBeneficiaryResult.getMessage());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void showResultAnimation(boolean result) {
        String animation = result ? ResultAnimations.paymentSuccess : ResultAnimations.generalFailure;
        binding.cashSendResultAnimationView.setAnimation(animation);
    }
}