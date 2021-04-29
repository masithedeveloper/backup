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
package com.barclays.absa.banking.payments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryUtils;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.databinding.ConfirmPaymentActivityBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.TxnAmt;
import com.barclays.absa.banking.payments.services.dto.PaymentConfirmationResponse;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import styleguide.utils.extensions.StringExtensions;

public class PaymentConfirmationActivity extends ConnectivityMonitorActivity implements View.OnClickListener {

    private ConfirmPaymentActivityBinding binding;
    private PayBeneficiaryPaymentConfirmationObject payBeneficiaryPaymentConfirmationObject;
    private OnceOffPaymentConfirmationResponse onceOffPaymentConfirmationResponse;
    private PaymentsInteractor payBeneficiaryInteractor;
    private String USER_TYPE;
    private PaymentConfirmationResponse paymentConfirmationResponse;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String beneficiaryAccountNumber;
    private String bankName, branchCode, accountType;
    private ResponseObject responseObject;
    private boolean isOnceOff;

    private SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            handler.postDelayed(() -> {
                if (payBeneficiaryInteractor == null) {
                    payBeneficiaryInteractor = new PaymentsInteractor();
                }
                if (isOnceOff && onceOffPaymentConfirmationResponse != null) {
                    payBeneficiaryInteractor.performOnceOffPayment(onceOffPaymentConfirmationResponse.getTxnRef(), paymentResponseListener);
                } else if (payBeneficiaryPaymentConfirmationObject != null) {
                    payBeneficiaryInteractor.performPayment(payBeneficiaryPaymentConfirmationObject.getTransactionRefNo(), paymentResponseListener);
                }
            }, 250);
        }

        @Override
        public void onSureCheckFailed() {
            launchSureCheckFailedResultScreen();
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(PaymentConfirmationActivity.this);
        }
    };

    private ExtendedResponseListener<PaymentConfirmationResponse> paymentResponseListener = new ExtendedResponseListener<PaymentConfirmationResponse>() {

        @Override
        public void onSuccess(final PaymentConfirmationResponse successResponse) {
            dismissProgressDialog();
            if (successResponse != null) {
                paymentConfirmationResponse = successResponse;
                sureCheckDelegate.processSureCheck(PaymentConfirmationActivity.this, successResponse, () -> {
                    if (SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                        launchResultScreen();
                    } else if ((FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus()))) {
                        if (!TextUtils.isEmpty(successResponse.getErrorMessage())) {
                            launchFailureResultScreen(successResponse.getErrorMessage());
                        } else {
                            launchFailureResultScreen(successResponse.getMsg());
                        }
                    }
                });
            } else {
                launchFailureResultScreen(getString(R.string.generic_error));
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            if (failureResponse != null) {
                if (CONN_TIME_OUT.equals(failureResponse.getErrorMessage())) {
                    launchFailureResultScreen(R.string.payments_timeout_title, getString(R.string.payments_timeout_message));
                } else {
                    String error = TextUtils.isEmpty(failureResponse.getErrorMessage()) ? getString(R.string.generic_error) : failureResponse.getErrorMessage();
                    launchFailureResultScreen(error);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.confirm_payment_activity, null, false);
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(RESULT)) {
                responseObject = (ResponseObject) getIntent().getExtras().getSerializable(RESULT);
            }
            if (extras.containsKey(BEN_TYPE)) {
                USER_TYPE = getIntent().getStringExtra(BEN_TYPE);
            }
            if (extras.containsKey(SERVICE_BENEFICIARY_ACCOUNT_NUMBER)) {
                beneficiaryAccountNumber = StringExtensions.toFormattedAccountNumber(getIntent().getExtras().getString(SERVICE_BENEFICIARY_ACCOUNT_NUMBER));
            }
            if (extras.containsKey(PaymentsConstants.BANK_NAME)) {
                bankName = getIntent().getExtras().getString(PaymentsConstants.BANK_NAME);
            }
            if (extras.containsKey(PaymentsConstants.BRANCH_CODE)) {
                branchCode = StringExtensions.insertSpaceAtIncrements(getIntent().getExtras().getString(PaymentsConstants.BRANCH_CODE), 3);
            }
            if (extras.containsKey(PaymentsConstants.ACCOUNT_TYPE)) {
                accountType = getIntent().getExtras().getString(PaymentsConstants.ACCOUNT_TYPE);
            }
        }
        setToolBarBack(getString(R.string.confirm_payment), expression -> onBackPressed());
        payBeneficiaryInteractor = new PaymentsInteractor();
        binding.payButton.setOnClickListener(this);
        paymentResponseListener.setView(this);
        if (responseObject instanceof PayBeneficiaryPaymentConfirmationObject) {
            payBeneficiaryPaymentConfirmationObject = (PayBeneficiaryPaymentConfirmationObject) responseObject;
            populateData();
        } else if (responseObject instanceof OnceOffPaymentConfirmationResponse) {
            isOnceOff = true;
            onceOffPaymentConfirmationResponse = (OnceOffPaymentConfirmationResponse) responseObject;
            populateOnceOffData();
        }
        setupTalkback();
    }

    private void setupTalkback() {
        String amountBeingPayed = AccessibilityUtils.getTalkBackRandValueFromString(binding.amountView.getTitle());
        binding.beneficiaryNameView.setContentDescription(getString(R.string.talkback_payment_confirmation_beneficiary_name, binding.beneficiaryNameView.getTitle()));
        binding.accountNumberView.setContentDescription(getString(R.string.talkback_payment_confirm_account_number, binding.accountNumberView.getContentTextView().getText().toString()));
        binding.bankView.setContentDescription(getString(R.string.talkback_payment_confirmation_bank_name, binding.bankView.getContentTextView().getText().toString()));
        binding.accountTypeView.setContentDescription(getString(R.string.talkback_payment_confirmation_their_account_type, binding.accountTypeView.getContentTextView().getText().toString()));
        binding.amountView.setContentDescription(getString(R.string.talkback_payment_confirmation_amount_being_payed, amountBeingPayed));
        binding.myNotificationView.setContentDescription(getString(R.string.talkback_payment_confirmation_my_notification, binding.myNotificationView.getContentTextViewValue()));
        binding.theirNotificationView.setContentDescription(getString(R.string.talkback_payment_confirmation_their_notification, binding.theirNotificationView.getContentTextViewValue()));
        binding.branchView.setContentDescription(getString(R.string.talkback_payment_confirmation_branch_name, binding.branchView.getContentTextView().getText().toString()));
        binding.paymentDateView.setContentDescription(getString(R.string.talkback_payment_confirmation_date_chosen, binding.paymentDateView.getContentTextViewValue()));
        binding.paymentTypeView.setContentDescription(getString(R.string.talkback_payment_confirmation_payment_type, binding.paymentTypeView.getContentTextViewValue()));
    }

    private void payABillView() {
        binding.accountTypeView.setLineItemViewContent(StringExtensions.toTitleCase(BILL));
        binding.bankView.setVisibility(View.GONE);
        binding.branchView.setVisibility(View.GONE);
        binding.theirNotificationView.setVisibility(View.GONE);
        binding.theirReferenceView.setVisibility(View.GONE);
    }

    private void populateData() {
        if (BUSINESS_USER.equalsIgnoreCase(USER_TYPE)) {
            binding.beneficiaryNameView.setTitle(payBeneficiaryPaymentConfirmationObject.getInstitutionName());
            binding.accountTypeView.setLineItemViewContent(StringExtensions.toTitleCase(BILL));
            payABillView();
        } else {
            binding.beneficiaryNameView.setTitle(payBeneficiaryPaymentConfirmationObject.getBeneficiaryName());
            binding.theirReferenceView.setContentText(payBeneficiaryPaymentConfirmationObject.getBeneficiaryReference());
        }

        if (!TextUtils.isEmpty(beneficiaryAccountNumber) && !BILL.equalsIgnoreCase(BeneficiaryUtils.getBeneficiaryType(payBeneficiaryPaymentConfirmationObject.getBeneficiaryType()))) {
            binding.accountNumberView.setLineItemViewContent(beneficiaryAccountNumber);
        } else {
            binding.accountNumberView.setLineItemViewContent(payBeneficiaryPaymentConfirmationObject.getAcctAtInst());
            beneficiaryAccountNumber = payBeneficiaryPaymentConfirmationObject.getAcctAtInst();
        }

        if (TextUtils.isEmpty(accountType)) {
            binding.accountTypeView.setVisibility(View.GONE);
        } else {
            binding.accountTypeView.setVisibility(View.VISIBLE);
            if ("Cheque account".equals(accountType) || "currentAccount".equalsIgnoreCase(accountType)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.current_account));
            } else if ("Savings account".equals(accountType)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.savings_account));
            } else {
                binding.accountTypeView.setVisibility(View.GONE);
            }
        }
        binding.branchView.setLineItemViewContent(branchCode);

        if (TextUtils.isEmpty(bankName)) {
            binding.bankView.setVisibility(View.GONE);
        } else {
            binding.bankView.setLineItemViewContent(bankName);
        }

        String formattedAmount = TextFormatUtils.formatBasicAmount(payBeneficiaryPaymentConfirmationObject.getTransactionAmount());
        binding.amountView.setTitle(formattedAmount);
        binding.fromAccountView.setContentText(StringExtensions.toFormattedAccountNumber(payBeneficiaryPaymentConfirmationObject.getFromAccountNumber()));

        binding.myReferenceView.setContentText(payBeneficiaryPaymentConfirmationObject.getMyReference());
        if (checkMethodDetails(payBeneficiaryPaymentConfirmationObject.getBeneficiaryMethodDetails())) {
            binding.theirNotificationView.setContentText(payBeneficiaryPaymentConfirmationObject.getBeneficiaryMethodDetails());
        } else {
            binding.theirNotificationView.setContentText(getString(R.string.notification_none));
        }
        if (checkMethodDetails(payBeneficiaryPaymentConfirmationObject.getMyMethodDetails())) {
            binding.myNotificationView.setContentText(payBeneficiaryPaymentConfirmationObject.getMyMethodDetails());
        } else {
            binding.myNotificationView.setContentText(getString(R.string.notification_none));
            if (BILL.equalsIgnoreCase(BeneficiaryUtils.getBeneficiaryType(payBeneficiaryPaymentConfirmationObject.getBeneficiaryType()))) {
                mScreenName = PAYMENT_INSTITUTION_OVERVIEW_CONST;
                mSiteSection = CONFIRM_CONST;
                trackCustomScreenView(PAYMENT_INSTITUTION_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
            } else {
                mScreenName = PAYMENT_OVERVIEW_CONST;
                mSiteSection = CONFIRM_CONST;
                trackCustomScreenView(PAYMENT_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
            }
        }

        if (ON.equalsIgnoreCase(payBeneficiaryPaymentConfirmationObject.getImmediatePay())) {
            binding.paymentDateView.setVisibility(View.GONE);
            binding.paymentTypeView.setContentText(getString(R.string.iip));
        } else if (NOW.equalsIgnoreCase(payBeneficiaryPaymentConfirmationObject.getPaymentDate())) {
            binding.paymentTypeView.setContentText(getString(R.string.normal));
            binding.paymentDateView.setVisibility(View.GONE);
        } else {
            binding.paymentTypeView.setContentText(getString(R.string.future_dated_payment));
            binding.paymentDateView.setContentText(displayFutureDate());
            mScreenName = PAYMENT_FUTURE_DATED_OVERVIEW_CONST;
            mSiteSection = CONFIRM_CONST;
            trackCustomScreenView(PAYMENT_FUTURE_DATED_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
        }
    }

    private void populateOnceOffData() {
        if (BILL.equalsIgnoreCase(BeneficiaryUtils.getBeneficiaryType(onceOffPaymentConfirmationResponse.getBeneficiaryType()))) {
            binding.beneficiaryNameView.setTitle(onceOffPaymentConfirmationResponse.getInstitutionName());
            payABillView();
        } else {
            binding.beneficiaryNameView.setTitle(onceOffPaymentConfirmationResponse.getBeneficiaryName());
            String description = onceOffPaymentConfirmationResponse.getDescription();
            if ("Cheque account".equals(description) || "currentAccount".equalsIgnoreCase(description)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.current_account));
            } else if ("Savings account".equals(description)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.savings_account));
            } else if ("Cheque account".equals(accountType) || "currentAccount".equalsIgnoreCase(accountType)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.current_account));
            } else if ("Savings account".equals(accountType)) {
                binding.accountTypeView.setLineItemViewContent(getString(R.string.savings_account));
            } else {
                binding.accountTypeView.setLineItemViewContent(description);
            }

            if (checkMethodDetails(onceOffPaymentConfirmationResponse.getBeneficiaryMethodDetails())) {
                binding.theirNotificationView.setContentText(onceOffPaymentConfirmationResponse.getBeneficiaryMethodDetails());
            } else {
                binding.theirNotificationView.setContentText(getString(R.string.notification_none));
            }
            binding.theirReferenceView.setContentText(onceOffPaymentConfirmationResponse.getBeneficiaryReference());
        }
        binding.accountNumberView.setLineItemViewContent(onceOffPaymentConfirmationResponse.getAccountNumber());

        binding.bankView.setLineItemViewContent(onceOffPaymentConfirmationResponse.getBankName());
        binding.branchView.setLineItemViewContent(onceOffPaymentConfirmationResponse.getBranchCode());

        if (TextUtils.isEmpty(onceOffPaymentConfirmationResponse.getBankName()) && "absa".equalsIgnoreCase(onceOffPaymentConfirmationResponse.getBeneficiaryType())) {
            binding.bankView.setLineItemViewContent(onceOffPaymentConfirmationResponse.getBeneficiaryType());
            binding.branchView.setLineItemViewContent("632005");
        }

        String formattedAmount = TextFormatUtils.formatBasicAmount(onceOffPaymentConfirmationResponse.getTransactionAmount());
        binding.amountView.setTitle(formattedAmount);
        binding.fromAccountView.setContentText(StringExtensions.toFormattedAccountNumber(onceOffPaymentConfirmationResponse.getFromAccountNumber()));
        binding.myReferenceView.setContentText(onceOffPaymentConfirmationResponse.getMyReference());
        if (checkMethodDetails(onceOffPaymentConfirmationResponse.getMyMethodDetails())) {
            binding.myNotificationView.setContentText(onceOffPaymentConfirmationResponse.getMyMethodDetails());
        } else {
            binding.myNotificationView.setContentText(getString(R.string.notification_none));
        }
        if (NOW.equalsIgnoreCase(onceOffPaymentConfirmationResponse.getPaymentDate())) {
            if (ON.equalsIgnoreCase(onceOffPaymentConfirmationResponse.getImmediatePay())) {
                binding.paymentTypeView.setContentText(getString(R.string.iip));
            } else {
                binding.paymentTypeView.setContentText(getString(R.string.normal));
            }
            binding.paymentDateView.setVisibility(View.GONE);
            if (BILL.equalsIgnoreCase(BeneficiaryUtils.getBeneficiaryType(onceOffPaymentConfirmationResponse.getBeneficiaryType()))) {
                mScreenName = PAYMENT_INSTITUTION_OVERVIEW_CONST;
                mSiteSection = CONFIRM_CONST;
                trackCustomScreenView(PAYMENT_INSTITUTION_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
            } else {
                mScreenName = PAYMENT_OVERVIEW_CONST;
                mSiteSection = CONFIRM_CONST;
                trackCustomScreenView(PAYMENT_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
            }
        } else {
            binding.paymentTypeView.setContentText(getString(R.string.future_dated_payment));
            binding.paymentDateView.setContentText(displayFutureDate());
            mScreenName = PAYMENT_FUTURE_DATED_OVERVIEW_CONST;
            mSiteSection = CONFIRM_CONST;
            trackCustomScreenView(PAYMENT_FUTURE_DATED_OVERVIEW_CONST, CONFIRM_CONST, TRUE_CONST);
        }
    }

    private String displayFutureDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "EEE MMM dd hh:mm:ss 'GMT+02:00' yyyy", BMBApplication.getApplicationLocale());
        Date newDate = null;
        try {
            String futureDate;
            if (isOnceOff) {
                futureDate = onceOffPaymentConfirmationResponse.getFutureDate();
            } else {
                futureDate = payBeneficiaryPaymentConfirmationObject.getFutureDate();
            }
            if (futureDate != null) {
                newDate = simpleDateFormat.parse(futureDate);
            } else {
                showMessageError(getString(R.string.technical_difficulties_try_shortly));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", BMBApplication.getApplicationLocale());
        if (newDate != null) {
            return displayFormat.format(newDate);
        }
        return displayFormat.format(new Date());
    }

    private boolean checkMethodDetails(String methodDetails) {
        boolean isMethodContainsData;
        isMethodContainsData = methodDetails != null && !"null".equalsIgnoreCase(methodDetails) && !"".equalsIgnoreCase(methodDetails);
        return isMethodContainsData;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.payButton) {
            preventDoubleClick(binding.payButton);
            showProgressDialog();
            if (getAppCacheService().isInNoPrimaryDeviceState()) {
                getAppCacheService().setReturnToScreen(PaymentConfirmationActivity.class);
                showNoPrimaryDeviceScreen();
            } else {
                if (isOnceOff) {
                    payBeneficiaryInteractor.performOnceOffPayment(onceOffPaymentConfirmationResponse.getTxnRef(), paymentResponseListener);
                } else {
                    payBeneficiaryInteractor.performPayment(payBeneficiaryPaymentConfirmationObject.getTransactionRefNo(), paymentResponseListener);
                }
            }
        }
    }

    private void launchSureCheckFailedResultScreen() {
        startActivity(IntentFactory.getPaymentTransactionFailedResultScreen(this));
    }

    private void launchFailureResultScreen(String message) {
        launchFailureResultScreen(R.string.payment_error_title, message);
    }

    private void launchFailureResultScreen(int title, String message) {
        mScreenName = PAYMENT_UNSUCCESSFUL_CONST;
        mSiteSection = CONFIRM_CONST;
        trackCustomScreenView(PAYMENT_UNSUCCESSFUL_CONST, CONFIRM_CONST, TRUE_CONST);
        Intent intent = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(title)
                .setGenericResultSubMessage(message)
                .setGenericResultDoneButton(this, v -> loadAccountsAndGoHome())
                .build();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void launchResultScreen() {
        if (paymentConfirmationResponse != null) {
            String payBeneficiaryConfirmationMessage = paymentConfirmationResponse.getMsg();
            if (!TextUtils.isEmpty(payBeneficiaryConfirmationMessage) && payBeneficiaryConfirmationMessage.contains(AUTHORISATION_OUTSTANDING_TRANSACTION)) {
                String subMessage = getString(R.string.auth_title);
                startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_pending, subMessage, R.string.done, false));
            } else {
                String successMessage;
                final TxnAmt transactionAmount = paymentConfirmationResponse.getTransactionAmount();
                if (BUSINESS_USER.equalsIgnoreCase(USER_TYPE)) {
                    if (transactionAmount != null) {
                        successMessage = getString(R.string.payment_success_message, transactionAmount.toString(), paymentConfirmationResponse.getInstitutionName(), beneficiaryAccountNumber);

                        if (OWN.equalsIgnoreCase(paymentConfirmationResponse.getPaymentDate())) {
                            startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_sched_success, successMessage, R.string.done, false));
                        } else {
                            startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_success_title, successMessage, R.string.done, true));
                        }
                    } else {
                        showGenericErrorMessage();
                    }
                } else if (OWN.equalsIgnoreCase(paymentConfirmationResponse.getPaymentDate()) && CONST_SUCCESS.equalsIgnoreCase(paymentConfirmationResponse.getTransactionStatus())) {
                    mScreenName = PAYMENT_SCHEDULE_SUCCESS_CONST;
                    mSiteSection = CONFIRM_CONST;
                    trackCustomScreenView(PAYMENT_SCHEDULE_SUCCESS_CONST, CONFIRM_CONST, TRUE_CONST);

                    if (BILL.equalsIgnoreCase(BeneficiaryUtils.getBeneficiaryType(paymentConfirmationResponse.getBeneficiaryType()))) {
                        successMessage = getString(R.string.payment_success_message, transactionAmount != null ? transactionAmount.toString() : "", paymentConfirmationResponse.getInstitutionName(), beneficiaryAccountNumber);
                    } else {
                        successMessage = getString(R.string.payment_success_message, transactionAmount != null ? transactionAmount.toString() : "", paymentConfirmationResponse.getBeneficiaryName(), beneficiaryAccountNumber);
                    }
                    startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_sched_success, successMessage, R.string.done, false));
                } else if (ON.equalsIgnoreCase(paymentConfirmationResponse.getImmediatePay()) && CONST_SUCCESS.equalsIgnoreCase(paymentConfirmationResponse.getTransactionStatus()) && paymentConfirmationResponse.getMsg() != null && !paymentConfirmationResponse.getMsg().isEmpty()) {
                    mScreenName = PAYMENT_IIP_DELAYED_SUCCESS_CONST;
                    mSiteSection = CONFIRM_CONST;
                    trackCustomScreenView(PAYMENT_IIP_DELAYED_SUCCESS_CONST, CONFIRM_CONST, TRUE_CONST);
                    successMessage = getString(R.string.iip_payment_successfully_scheduled_message, transactionAmount != null ? transactionAmount.toString() : "", paymentConfirmationResponse.getBeneficiaryName(), beneficiaryAccountNumber, paymentConfirmationResponse.getIipReferenceNumber());
                    startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_sched_success, successMessage, R.string.done, true));
                } else {
                    if (paymentConfirmationResponse.getImmediatePay() != null && !"".equalsIgnoreCase(paymentConfirmationResponse.getImmediatePay()) && !OFF.equalsIgnoreCase(paymentConfirmationResponse.getImmediatePay()) && paymentConfirmationResponse.getMsg() != null && paymentConfirmationResponse.getMsg().isEmpty()) {
                        mScreenName = PAYMENT_IIP_SUCCESS_CONST;
                        mSiteSection = CONFIRM_CONST;
                        trackCustomScreenView(PAYMENT_IIP_SUCCESS_CONST, CONFIRM_CONST, TRUE_CONST);
                    } else {
                        mScreenName = PAYMENT_NORMAL_CONST;
                        mSiteSection = CONFIRM_CONST;
                        trackCustomScreenView(PAYMENT_NORMAL_CONST, CONFIRM_CONST, TRUE_CONST);
                    }
                    if (transactionAmount != null) {
                        successMessage = getString(mScreenName.equals(PAYMENT_IIP_SUCCESS_CONST) ? R.string.iip_payment_successful_message : R.string.payment_success_message, transactionAmount.toString(), paymentConfirmationResponse.getBeneficiaryName() != null ? paymentConfirmationResponse.getBeneficiaryName() : paymentConfirmationResponse.getInstitutionName(), beneficiaryAccountNumber);
                        AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_PAYMENT);
                        startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(this, R.string.payment_success_title, successMessage, R.string.done, true));
                    } else {
                        showGenericErrorMessage();
                    }
                }
            }
        }
    }
}
