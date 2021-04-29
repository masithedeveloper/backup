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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ATMAccessPINConfirmObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.CancelCashSendResponse;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.ResendWithdrawalSMSObject;
import com.barclays.absa.banking.boundary.model.TransactionUnredeem;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.databinding.CashSendTransactionHistoryDetailsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.shared.widget.CustomEditText;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AccountBalanceUpdateHelper;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import styleguide.utils.extensions.StringExtensions;

public class UnredeemedDetailsActivity extends BaseActivity implements CustomEditText.HandleDismissingKeyboard, UnredeemPinChangeDialogFragment.OnPinChangedListener {

    private TransactionUnredeem transactionUnredeemObject;
    private String fromAccountText, fromAccountNo;
    private String mModifiedATMPIN;
    private final CashSendInteractor cashSendInteractor = new CashSendInteractor();
    private CashSendTransactionHistoryDetailsActivityBinding binding;
    private boolean isCashSendPlus = false;

    private final ExtendedResponseListener<ATMAccessPINConfirmObject> atmCashSendExtendedResponseListener = new ExtendedResponseListener<ATMAccessPINConfirmObject>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(ATMAccessPINConfirmObject successResponse) {
            dismissProgressDialog();
            String SELF_UNREDEEMED = "Self";
            if (!(TextUtils.isEmpty(transactionUnredeemObject.getBeneficiaryName()) && SELF_UNREDEEMED.equals(transactionUnredeemObject.getBeneficiaryNickname()))) {
                binding.unredeemDetailChangeAtmPin.setVisibility(View.VISIBLE);
            }
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedChangePinSuccess_NoticeDisplayed");
            Toast.makeText(UnredeemedDetailsActivity.this, getString(R.string.atm_updated_successfully), Toast.LENGTH_SHORT).show();
            binding.unredeemDetailPin.setContentText(mModifiedATMPIN);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedChangePinFailure_NoticeDisplayed");
            dismissProgressDialog();
            onFailureResponse();
        }
    };

    private final ExtendedResponseListener<PINObject> encryptPinExtendedResponseListener = new ExtendedResponseListener<PINObject>() {
        @Override
        public void onSuccess(final PINObject successResponse) {
            requestPinChange(successResponse);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            dismissProgressDialog();
            onFailureResponse();
        }
    };

    private final ExtendedResponseListener<CancelCashSendResponse> cancelTransactionExtendedResponseNumber = new ExtendedResponseListener<CancelCashSendResponse>() {
        @Override
        public void onSuccess(final CancelCashSendResponse cancelCashSendResponse) {
            new AccountBalanceUpdateHelper(UnredeemedDetailsActivity.this).refreshHomeScreenAccountsAndBalances(new AccountRefreshInterface() {
                @Override
                public void onSuccess() {
                    showCancelCashSendResultScreen(cancelCashSendResponse);
                }

                @Override
                public void onFailure() {
                    showCancelCashSendResultScreen(cancelCashSendResponse);
                }

                private void showCancelCashSendResultScreen(CancelCashSendResponse cancelCashSendResponse) {
                    dismissProgressDialog();
                    new CancelCashSendResultHelper().showResultScreen(UnredeemedDetailsActivity.this, isCashSendPlus, cancelCashSendResponse);
                }
            });
        }
    };

    private final ExtendedResponseListener<ResendWithdrawalSMSObject> smsExtendedResponseListener = new ExtendedResponseListener<ResendWithdrawalSMSObject>() {
        @Override
        public void onSuccess(final ResendWithdrawalSMSObject successResponse) {
            dismissProgressDialog();
            final String resendSMSMsg = successResponse.getMsg();
            final String resendSMSStatus = StringExtensions.toSentenceCase(getString(R.string.status_success));
            showSMSDialog(resendSMSMsg, resendSMSStatus);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        atmCashSendExtendedResponseListener.setView(this);
        encryptPinExtendedResponseListener.setView(this);
        cancelTransactionExtendedResponseNumber.setView(this);
        smsExtendedResponseListener.setView(this);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.cash_send_transaction_history_details_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.transaction_details);

        mScreenName = BMBConstants.CASHSEND_UNREDEEMED_TRANSACTION_DETAILS_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_UNREDEEMED_TRANSACTION_DETAILS_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.get("UnredeemTransaction") != null) {
            transactionUnredeemObject = (TransactionUnredeem) extras.getSerializable("UnredeemTransaction");
            fromAccountText = extras.getString("actNumToDisplay");
            fromAccountNo = extras.getString("actNum");
            isCashSendPlus = extras.getBoolean("isCashSendPlus", false);
        }

        initViews();
        onPopulateView();
        setupTalkBack();
    }

    private void setupTalkBack() {
        final String beneficiaryName = binding.unredeemBeneficiaryInfo.getContentTextViewValue();
        final String mobileNumber = binding.unredeemDetailCashSendNumber.getContentTextViewValue();
        final String cashsendAmount = AccessibilityUtils.getTalkBackRandValueFromString(binding.unredeemDetailCashSendAmount.getContentTextViewValue());
        final String[] accountInfo = AccessibilityUtils.splitAccountNumberFromName(binding.unredeemDetailAccountNumber.getContentTextViewValue());
        final String accountName = accountInfo[0];
        final String accountNumber = accountInfo[1];
        final String cashSendReference = binding.unredeemDetailReference.getContentTextViewValue();
        final String cashsendDate = binding.unredeemDetailDateSent.getContentTextViewValue();

        binding.unredeemBeneficiaryInfo.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_beneficiary_name, beneficiaryName));
        binding.unredeemBeneficiaryInfo.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_beneficiary_name, beneficiaryName));
        binding.unredeemDetailCashSendNumber.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_mobile_number, mobileNumber));
        binding.unredeemDetailCashSendNumber.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_mobile_number, mobileNumber));
        binding.unredeemDetailCashSendAmount.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_amount_sent, cashsendAmount));
        binding.unredeemDetailCashSendAmount.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_amount_sent, cashsendAmount));
        binding.unredeemDetailAccountNumber.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_account_number, accountName, accountNumber));
        binding.unredeemDetailAccountNumber.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_account_number, accountName, accountNumber));
        binding.unredeemDetailReference.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_reference_number, cashSendReference));
        binding.unredeemDetailReference.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_reference_number, cashSendReference));
        binding.unredeemDetailDateSent.getLabelTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_date, cashsendDate));
        binding.unredeemDetailDateSent.getContentTextView().setContentDescription(getString(R.string.talkback_unredeemed_cashsends_transaction_date, cashsendDate));
        binding.unredeemDetailChangeAtmPin.setContentDescription(getString(R.string.talkback_unredeemed_cashsend_change_atm_pin));
        binding.unredeemDetailCancelCashSend.setContentDescription(getString(R.string.talkback_unredeemed_cashsend_cancel));
        binding.unredeemDetailResendAtmPin.setContentDescription(getString(R.string.talkback_unredeemed_cashsend_resend_withdraw_sms));
    }

    private void initViews() {
        if (isCashSendPlus) {
            binding.unredeemDetailChangeAtmPin.setVisibility(View.GONE);
            binding.unredeemDetailResendAtmPin.setVisibility(View.GONE);
        }

        binding.unredeemDetailCancelCashSend.setOnClickListener(view -> {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedTransactionDetails_CancelClicked");
            displayCancelCashSendAlert();
        });
        binding.unredeemDetailResendAtmPin.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedTransactionDetails_ResendSmsClicked");
            displayResendWithdrawalAlert();
        });
        binding.unredeemDetailChangeAtmPin.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedTransactionDetails_ChangePinClicked");
            displayPinChangeDialog();
        });
    }

    private void onPopulateView() {
        if (transactionUnredeemObject != null) {
            binding.unredeemBeneficiaryInfo.setContentText(String.format("%s %s", transactionUnredeemObject.getBeneficiaryNickname(), transactionUnredeemObject.getBeneficiarySurname() != null ? transactionUnredeemObject.getBeneficiarySurname() : ""));
            binding.unredeemDetailCashSendNumber.setContentText(StringExtensions.toFormattedCellphoneNumber(transactionUnredeemObject.getCellphoneNumber()));
            binding.unredeemDetailAccountNumber.setContentText(String.format("%s (%s)", fromAccountText, StringExtensions.toFormattedAccountNumber(fromAccountNo)));
            final Amount amount = transactionUnredeemObject.getAmount();
            binding.unredeemDetailCashSendAmount.setContentText(amount != null ? amount.toString() : "R 0.00");
            binding.unredeemDetailReference.setContentText(transactionUnredeemObject.getStatementTransactionDescription1());

            SimpleDateFormat dateFormatWithSlash = new SimpleDateFormat("MM/dd/yyyy", BMBApplication.getApplicationLocale());
            final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", BMBApplication.getApplicationLocale());
            String formattedDate = transactionUnredeemObject.getTransactionDateTime();
            if (formattedDate != null) {
                try {
                    formattedDate = DateUtils.getFormattedDate(this.transactionUnredeemObject.getTransactionDateTime(), dateFormatWithSlash, displayFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                binding.unredeemDetailDateSent.setContentText(formattedDate);
            } else {
                binding.unredeemDetailDateSent.setContentText(getString(R.string.unredeemed_transaction_date_not_available));
            }
            binding.unredeemDetailPin.setContentText("******");
        }
        dismissProgressDialog();
    }

    @Override
    public void dismissKeyboard() {
        BMBLogger.d("Unredeemed", "KeyBoard is showing");
    }

    private void requestPinChange(PINObject pinObj) {
        if (transactionUnredeemObject != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ID, transactionUnredeemObject.getBeneficiaryId());
            bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ATM_PIN_CASHSEND, pinObj.getAccessPin());
            bundle.putString(BMBConstants.SERVICE_FROM_ACCOUNT, fromAccountNo);
            bundle.putString(BMBConstants.SERVICE_REF_NO, transactionUnredeemObject.getTransactionReferenceNumber());
            bundle.putString(BMBConstants.SERVICE_BENEFICIARY_BEN_CEL_NO_CASHSEND, transactionUnredeemObject.getCellphoneNumber());
            bundle.putString(BMBConstants.SERVICE_BENEFICIARY_NAME, transactionUnredeemObject.getBeneficiaryName());
            bundle.putString(BMBConstants.SERVICE_BENEFICIARY_SUR_NAME, transactionUnredeemObject.getBeneficiarySurname());
            bundle.putString(BMBConstants.SERVICE_BEN_SHORT_NAME, transactionUnredeemObject.getBeneficiaryNickname());
            bundle.putString(BMBConstants.SERVICE_BAL, transactionUnredeemObject.getAmountString());
            bundle.putString(BMBConstants.SERVICE_MY_REFERENCE, transactionUnredeemObject.getStatementTransactionDescription1());
            bundle.putString(BMBConstants.SERVICE_UNIQUE_EFT, this.transactionUnredeemObject.getUniqueEFT());
            if (transactionUnredeemObject.getTransactionDateTime() != null) {
                try {
                    String date = DateUtils.getDate(transactionUnredeemObject.getTransactionDateTime().trim(), DateUtils.SLASHED_DATE_PATTERN).toString();
                    bundle.putString(BMBConstants.SERVICE_DT, date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cashSendInteractor.updateATMAccessPin(isCashSendPlus, pinObj, bundle, atmCashSendExtendedResponseListener);
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    public void onFailureResponse() {
        if (null != mModifiedATMPIN && mModifiedATMPIN.length() > 0) {
            binding.unredeemDetailPin.setContentText(mModifiedATMPIN);
        } else {
            binding.unredeemDetailPin.setContentText("******");
        }
    }

    /**
     * Show sms dialog.
     *
     * @param resendSMSMsg the resend sms msg
     */
    private void showSMSDialog(String resendSMSMsg, String resendSMSStatus) {
        AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedResendSmsSuccess_NoticeDisplayed");
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(resendSMSStatus)
                .message(resendSMSMsg)
                .build());
    }

    private void displayResendWithdrawalAlert() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.resend_cashsend_withrawal_title))
                .message(getString(R.string.resend_cashsend_withdrawal_msg))
                .positiveDismissListener((dialog, which) -> requestWithdrawalSms()));
    }

    private void displayCancelCashSendAlert() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.cancel_cashsend))
                .positiveDismissListener((dialog, which) -> cashSendInteractor.requestToCancelCashSendTransaction(isCashSendPlus, transactionUnredeemObject, cancelTransactionExtendedResponseNumber)));
    }

    private void requestWithdrawalSms() {
        cashSendInteractor.requestWithdrawalSms(isCashSendPlus, fromAccountNo, transactionUnredeemObject, smsExtendedResponseListener);
    }

    private void displayPinChangeDialog() {
        FragmentManager manager = getSupportFragmentManager();
        UnredeemPinChangeDialogFragment fragment = UnredeemPinChangeDialogFragment.newInstance("Change Access Pin", false);
        fragment.show(manager, "change_pin");
    }

    @Override
    public void onPinChanged(String newPin, boolean sendSMS) {
        cashSendInteractor.requestCashSendPinEncryption(newPin, encryptPinExtendedResponseListener);
        mModifiedATMPIN = newPin;
        if (sendSMS) {
            requestWithdrawalSms();
        }
    }
}
