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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffConfirmation;
import com.barclays.absa.banking.buy.ui.airtime.AirtimeHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;

import styleguide.forms.NormalInputView;
import styleguide.forms.validation.ValidationExtensions;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

@Deprecated
public class CashSendOnceOffActivity extends CashSendBaseActivity {
    private final String NAME = "name";
    private final String SURNAME = "surname";
    private final String MOBILE = "mobile";
    private boolean isCashSendPlus = false;
    private final int SELECT_CONTACT_NO_REQUEST_CODE = 3;

    private NormalInputView nameInputView, surnameInputView, numberInputView;

    private CashSendOnceOffConfirmation onceOffCashSendConfirmationObj = new CashSendOnceOffConfirmation();

    private Uri contactUri;

    protected ExtendedResponseListener<CashSendOnceOffConfirmation> cashSendOnceOffConfirmationResponseListener = new ExtendedResponseListener<CashSendOnceOffConfirmation>() {
        @Override
        public void onSuccess(final CashSendOnceOffConfirmation successResponse) {
            Intent intent = new Intent(CashSendOnceOffActivity.this, CashSendOnceOffConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ATM_PIN_KEY, accessPinInputView.getText());
            bundle.putBoolean(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus);
            intent.putExtras(bundle);
            intent.putExtra(AppConstants.RESULT, successResponse);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeviceProfilingInteractor().notifyTransaction();
        setContentView(R.layout.cashsend_onceoff_activity);
        setToolBarBack(R.string.cashsend, true);
        isCashSendPlus = getIntent().getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);

        termsAndConditionsCheckBoxView = findViewById(R.id.termsAndConditionsCheckBoxView);

        nameInputView = findViewById(R.id.nameInputView);
        surnameInputView = findViewById(R.id.surnameInputView);
        numberInputView = findViewById(R.id.numberInputView);

        availableTextView = findViewById(R.id.availableTextView);
        amountInputView = findViewById(R.id.amountInputView);
        myReferenceInputView = findViewById(R.id.myReferenceInputView);
        accessPinInputView = findViewById(R.id.accessPinInputView);
        beneficiaryView = findViewById(R.id.beneficiaryView);

        CommonUtils.setInputFilter(this.nameInputView.getEditText());
        CommonUtils.setInputFilter(this.surnameInputView.getEditText());
        CommonUtils.setInputFilter(this.myReferenceInputView.getEditText());

        accountSelectorRoundedInputView = findViewById(R.id.accountSelectorView);
        accountSelectorRoundedInputView.setHint(R.string.select_account_hint);
        getAccounts();

        clientAgreementDetailsResponseListener.setView(this);
        updateClientAgreementDetailsResponseListener.setView(this);
        cashSendOnceOffConfirmationResponseListener.setView(this);
        pinEncryptionExtendedResponseListener.setView(this);

        LinearLayout parentLinearLayout = findViewById(R.id.parentLinearLayout);
        if (parentLinearLayout != null) {
            parentLinearLayout.requestFocus();
        }

        CommonUtils.setInputFilterForRestrictingSpecialCharacter(myReferenceInputView.getEditText());

        mScreenName = BMBConstants.CASHSEND_ONCE_OFF_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_ONCE_OFF_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST);
        populateView();

        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            PayBeneficiaryPaymentObject paymentObject = new PayBeneficiaryPaymentObject();
            AbsaCacheManager.getInstance().getModelForAccounts(paymentObject, ApplicationFlowType.CASH_SEND_REBUILD);
            if (paymentObject.getFromAccounts() != null && paymentObject.getFromAccounts().size() > 0) {
                fromAccount = paymentObject.getFromAccounts().get(0);
                setFromAccount();
            }
        }
        setupTalkBack();
    }

    @Override
    public void setupTalkBack() {
        super.setupTalkBack();
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            nameInputView.setContentDescription(getString(R.string.talkback_cashsend_onceoff_enter_name));
            nameInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_recipient_name));
            numberInputView.setIconImageViewDescription(getString(R.string.talkback_cashsend_choose_contact_from_phone));
            numberInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_send_mobile_number));
            numberInputView.setContentDescription(getString(R.string.talkback_cashsend_onceoff_send_mobile_number));
            surnameInputView.setContentDescription(getString(R.string.talkback_cashsend_onceoff_enter_recipient_surname));
            surnameInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_enter_recipient_surname));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveInstanceState(NAME, nameInputView.getText());
        saveInstanceState(SURNAME, surnameInputView.getText());
        saveInstanceState(MOBILE, numberInputView.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreInstanceState(NAME, nameInputView);
        restoreInstanceState(SURNAME, surnameInputView);
        restoreInstanceState(MOBILE, numberInputView);
    }

    private void populateView() {
        nameInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(nameInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, BMBConstants.THEIR_REFERENCE_DETIALS_REQUESTCODE, null));
        clientAgreement = ClientAgreementHelper.fetchClientType(this);
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        } else {
            beneficiariesInteractor.fetchClientAgreementDetails(clientAgreementDetailsResponseListener);
        }

        setUpComponentListeners();
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

        nameInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(nameInputView);
            }
        });

        surnameInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(surnameInputView);
            }
        });

        numberInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(numberInputView);
            }
        });

        amountInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidAmount();
            }
            return true;
        });

        nameInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidName();
            }
            return true;
        });

        surnameInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidSurname();
            }
            return true;
        });

        numberInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidPhoneNumber();
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

        ValidationExtensions.addRequiredValidationHidingTextWatcher(nameInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(surnameInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(accessPinInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(amountInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(myReferenceInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(numberInputView);

        numberInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(numberInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, SELECT_CONTACT_NO_REQUEST_CODE, null));

        findViewById(R.id.btnCashSend).setOnClickListener(v -> {
            preventDoubleClick(v);
            validationAndExecute();
        });
    }

    private void validationAndExecute() {
        if (validateCashSendBeneficiary()) {
            if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
                cashSendInteractor.requestCashSendPinEncryption(onceOffCashSendConfirmationObj.getAtmPin(), pinEncryptionExtendedResponseListener);
            } else {
                beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementDetailsResponseListener);
            }
        }
    }

    @Override
    protected boolean validateCashSendBeneficiary() {
        setUpAccount();

        if (!isValidAmount()) {
            return false;
        }

        if (!(isValidName() && isValidSurname() && isValidPhoneNumber())) {
            return false;
        }

        if (!isValidReference()) {
            return false;
        }

        if (!isValidATMPin()) {
            return false;
        }

        if (!termsAndConditionsCheckBoxView.isChecked()) {
            Toast.makeText(this, this.getString(R.string.plz_accept_conditions), Toast.LENGTH_SHORT).show();
            animate(termsAndConditionsCheckBoxView, R.anim.shake);
            return false;
        }

        return true;
    }

    private void setUpAccount() {
        onceOffCashSendConfirmationObj = new CashSendOnceOffConfirmation();

        if (fromAccount != null) {
            onceOffCashSendConfirmationObj.setFromAccountNumber(fromAccount.getAccountNumber());
            onceOffCashSendConfirmationObj.setAccountType(fromAccount.getDescription());
        }
    }

    protected boolean isValidReference() {
        if (ValidationUtils.validateInput(myReferenceInputView, this.getString(R.string.my_reference))) {
            onceOffCashSendConfirmationObj.setMyReference(myReferenceInputView.getText());
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidAmount() {
        setUpAccount();

        if (ValidationUtils.validateInput(amountInputView, this.getString(R.string.amount))) {
            double enteredAmount = Double.parseDouble(getAmountString());
            if (ValidationUtils.validateAmountInput(amountInputView, this.getString(R.string.amount), enteredAmount, 50.00, 3000.00)) {
                if (enteredAmount % 10 != 0) {
                    amountInputView.setError(getString(R.string.card_limit_mul_of_10_validation));
                    AccessibilityUtils.announceErrorFromTextWidget(amountInputView.getErrorTextView());
                    return false;
                } else if (fromAccount != null && fromAccount.getAvailableBalance() != null && enteredAmount > fromAccount.getAvailableBalance().getAmountDouble()) {
                    amountInputView.setError(String.format("%s %s", getString(R.string.balance_exceeded), getString(R.string.you_have_available, fromAccount.getAvailableBalanceFormated())));
                    AccessibilityUtils.announceErrorFromTextWidget(amountInputView.getErrorTextView());
                    return false;
                } else if (onceOffCashSendConfirmationObj != null) {
                    onceOffCashSendConfirmationObj.setAmount(new Amount(this.getResources().getString(R.string.currency), getAmountString()));
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        amountInputView.hideError();
        return true;
    }

    protected boolean isValidATMPin() {
        if (ValidationUtils.validateATMPin(this.accessPinInputView, this.getString(R.string.atm_pin_tv), 6)) {
            onceOffCashSendConfirmationObj.setAtmPin(this.accessPinInputView.getText());
            accessPinInputView.hideError();
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidPhoneNumber() {
        if (ValidationUtils.validatePhoneNumberInput(this.numberInputView.getText())) {
            onceOffCashSendConfirmationObj.setCellNumber(this.numberInputView.getText());
        } else {
            numberInputView.setError(getString(R.string.enter_valid_number));
            AccessibilityUtils.announceErrorFromTextWidget(numberInputView.getErrorTextView());
            return false;
        }
        return true;
    }

    private boolean isValidName() {
        if (ValidationUtils.validateInput(this.nameInputView, this.getString(R.string.firstName))) {
            onceOffCashSendConfirmationObj.setFirstName(this.nameInputView.getText());
        } else {
            return false;
        }
        return true;
    }

    private boolean isValidSurname() {
        if (ValidationUtils.validateInput(this.surnameInputView, this.getString(R.string.surname))) {
            onceOffCashSendConfirmationObj.setSurname(this.surnameInputView.getText());
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void requestCashSendConfirmation(PINObject pinObject) {
        cashSendInteractor.validateOnceOffCashSend(onceOffCashSendConfirmationObj, pinObject, termsAndConditionsCheckBoxView.isChecked(), isCashSendPlus, cashSendOnceOffConfirmationResponseListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_CONTACT_NO_REQUEST_CODE) {
                contactUri = data.getData();
                PermissionHelper.requestContactsReadPermission(this, this::readContact);
                numberInputView.clearError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.length > 0) {
            int permissionStatus = grantResults[0];
            switch (permissionStatus) {
                case PackageManager.PERMISSION_GRANTED:
                    if (contactUri == null) {
                        CommonUtils.pickPhoneNumber(numberInputView.getEditText(), SELECT_CONTACT_NO_REQUEST_CODE);
                    } else {
                        readContact();
                    }
                    break;
                case PackageManager.PERMISSION_DENIED:
                    PermissionHelper.requestContactsReadPermission(this, this::readContact);
                    break;
                default:
                    break;
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(this, contactUri);
        final PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            numberInputView.setText(phoneNumbers.getMobile());
            AirtimeHelper.validateMobileNumber(this, numberInputView);
        }
    }
}