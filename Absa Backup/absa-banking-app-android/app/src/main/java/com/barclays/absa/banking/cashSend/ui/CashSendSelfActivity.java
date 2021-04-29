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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.ValidationUtils;
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper;

import styleguide.content.BeneficiaryListItem;
import styleguide.forms.validation.ValidationExtensions;
import styleguide.utils.extensions.StringExtensions;

public class CashSendSelfActivity extends CashSendBaseActivity {
    private Bundle cashSendToSelfBundle;
    private boolean isCashSendPlus = false;

    private ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendToSelfConfirmationResponseListener = new ExtendedResponseListener<CashSendBeneficiaryConfirmation>() {
        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(final CashSendBeneficiaryConfirmation sendBenCashSendConfirmation) {
            Intent cashSendOverviewRebuildActivityIntent = new Intent(CashSendSelfActivity.this, CashSendOverViewActivity.class);
            cashSendOverviewRebuildActivityIntent.putExtra(AppConstants.RESULT, sendBenCashSendConfirmation);
            cashSendOverviewRebuildActivityIntent.putExtras(cashSendToSelfBundle);
            cashSendOverviewRebuildActivityIntent.putExtra(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus);
            cashSendOverviewRebuildActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cashSendOverviewRebuildActivityIntent);
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashsend_to_self_activity);
        setToolBarBack(R.string.cashsend, true);
        isCashSendPlus = getIntent().getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        clientAgreementDetailsResponseListener.setView(this);
        updateClientAgreementDetailsResponseListener.setView(this);
        cashSendToSelfConfirmationResponseListener.setView(this);
        pinEncryptionExtendedResponseListener.setView(this);

        mScreenName = BMBConstants.CASHSEND_TO_MYSELF_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_TO_MYSELF_CONST,
                BMBConstants.CASHSEND_CONST,
                BMBConstants.TRUE_CONST);

        termsAndConditionsCheckBoxView = findViewById(R.id.termsAndConditionsCheckBoxView);

        availableTextView = findViewById(R.id.availableTextView);
        amountInputView = findViewById(R.id.amountInputView);
        myReferenceInputView = findViewById(R.id.myReferenceInputView);
        accessPinInputView = findViewById(R.id.accessPinInputView);
        beneficiaryView = findViewById(R.id.beneficiaryView);

        LinearLayout parentLinearLayout = findViewById(R.id.parentLinearLayout);
        if (parentLinearLayout != null) {
            parentLinearLayout.requestFocus();
        }

        btnCashSend = findViewById(R.id.btnCashSend);

        accountSelectorRoundedInputView = findViewById(R.id.accountSelectorView);
        accountSelectorRoundedInputView.setHint(R.string.select_account_hint);
        getAccounts();
        populateView();

        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            PayBeneficiaryPaymentObject paymentObject = new PayBeneficiaryPaymentObject();
            AbsaCacheManager.getInstance().getModelForAccounts(paymentObject, ApplicationFlowType.CASH_SEND_REBUILD);
            if (paymentObject.getFromAccounts() != null && paymentObject.getFromAccounts().size() > 0) {
                fromAccount = paymentObject.getFromAccounts().get(0);
                setLastSelectedIndex(0);
                setFromAccount();
            }
        }
        setupTalkBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        }
    }

    private void populateView() {
        UserProfile userProfile = ProfileManager.getInstance().getActiveUserProfile();
        if (userProfile != null) {
            String name = userProfile.getCustomerName();
            String number = StringExtensions.toFormattedCellphoneNumber(CustomerProfileObject.getInstance().getCellNumber());
            beneficiaryView.setBeneficiary(new BeneficiaryListItem(name, number, null));
            ProfileViewImageHelper profileViewImageHelper = new ProfileViewImageHelper(this);
            profileViewImageHelper.getImageBitmap();
            beneficiaryView.setImage(profileViewImageHelper.getImageBitmap());
        }

        clientAgreement = ClientAgreementHelper.fetchClientType(this);
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        } else {
            beneficiariesInteractor.fetchClientAgreementDetails(clientAgreementDetailsResponseListener);
        }

        setUpComponentListeners();

        myReferenceInputView.setText(CustomerProfileObject.getInstance().getCustomerName());
    }

    private void setUpComponentListeners() {
        amountInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(amountInputView);
            }
        });

        myReferenceInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                myReferenceInputView.getEditText().setSelection(0, myReferenceInputView.getEditText().length());
                scrollToTopOfView(myReferenceInputView);
            }
        });

        accessPinInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(accessPinInputView);
            }
        });

        amountInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidAmount();
            }
            return true;
        });

        myReferenceInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidReference();
            }
            return true;
        });

        accessPinInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return !isValidATMPin();
            }
            return true;
        });

        ValidationExtensions.addRequiredValidationHidingTextWatcher(accessPinInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(amountInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(myReferenceInputView);

        btnCashSend.setOnClickListener(v -> {
            if (validateCashSendBeneficiary()) {
                hideKeyBoard();
                if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
                    cashSendInteractor.requestCashSendPinEncryption(cashSendBeneficiaryConfirmation.getAccessPin(), pinEncryptionExtendedResponseListener);
                } else {
                    beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementDetailsResponseListener);
                }
            }
        });
    }

    @Override
    protected boolean validateCashSendBeneficiary() {
        if (ValidationUtils.validatePhoneNumberInput(CustomerProfileObject.getInstance().getCellNumber())) {
            return super.validateCashSendBeneficiary();
        } else {
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.cash_send_invalid_phone_number));
            return false;
        }
    }

    @Override
    protected void requestCashSendConfirmation(PINObject pinObject) {
        if (pinObject != null) {
            cashSendToSelfBundle = new Bundle();
            cashSendToSelfBundle.putBoolean(IS_SELF, true);
            cashSendToSelfBundle.putString(ATM_ACCESS_PIN_KEY, accessPinInputView.getText().trim());
            String cellNumber = CustomerProfileObject.getInstance().getCellNumber();
            boolean termsAccepted = termsAndConditionsCheckBoxView.isChecked();
            cashSendInteractor.validateCashSendToSelf(cashSendBeneficiaryConfirmation, pinObject, termsAccepted, cellNumber, isCashSendPlus, cashSendToSelfConfirmationResponseListener);
        }
    }
}