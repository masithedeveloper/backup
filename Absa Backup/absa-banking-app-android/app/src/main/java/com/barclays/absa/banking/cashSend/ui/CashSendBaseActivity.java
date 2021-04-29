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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.account.services.AccountService;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.presentation.shared.AccountsView;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnimationHelper;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.FilterAccountList;
import com.barclays.absa.utils.ImageUtils;
import com.barclays.absa.utils.ValidationUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import styleguide.content.BeneficiaryView;
import styleguide.forms.CheckBoxView;
import styleguide.forms.LargeInputView;
import styleguide.forms.NormalInputView;
import styleguide.forms.RoundedSelectorView;
import styleguide.forms.SelectorList;

public abstract class CashSendBaseActivity extends BaseActivity implements BMBConstants, AccountsView {
    protected final String FROM_ACCOUNT = "fromAccount";
    protected final String AMOUNT = "amount";
    protected final String ATM_PIN = "atm_pin";
    protected final String MY_REFERENCE = "MyReference";
    protected final String IS_CLIENT_AGREEMENT_ACCEPTED = "isClientAgreementAccepted";

    protected AccountObject fromAccount;
    protected Button btnCashSend;
    protected CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation = new CashSendBeneficiaryConfirmation();
    protected CheckBoxView termsAndConditionsCheckBoxView;

    protected String clientAgreement;

    protected AccountService accountService = new AccountInteractor();
    protected CashSendInteractor cashSendInteractor = new CashSendInteractor();
    protected BeneficiariesInteractor beneficiariesInteractor = new BeneficiariesInteractor();

    protected TextView availableTextView;
    protected BeneficiaryView beneficiaryView;
    protected LargeInputView amountInputView;
    protected NormalInputView myReferenceInputView, accessPinInputView;
    protected RoundedSelectorView<AccountObjectWrapper> accountSelectorRoundedInputView;

    private int lastSelectedIndex = 0;

    protected ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsResponseListener = new ExtendedResponseListener<ClientAgreementDetails>() {
        @Override
        public void onSuccess(final ClientAgreementDetails clientAgreementDetails) {
            if (ALPHABET_N.equalsIgnoreCase(clientAgreementDetails.getClientAgreementAccepted())) {
                ClientAgreementHelper.updatePersonalClientAgreementContainer(CashSendBaseActivity.this, false, termsAndConditionsCheckBoxView, clientAgreement);
            } else {
                ClientAgreementHelper.updatePersonalClientAgreementContainer(CashSendBaseActivity.this, true, termsAndConditionsCheckBoxView, clientAgreement);
            }
            dismissProgressDialog();
        }
    };

    protected ExtendedResponseListener<TransactionResponse> updateClientAgreementDetailsResponseListener = new ExtendedResponseListener<TransactionResponse>() {
        @Override
        public void onSuccess(final TransactionResponse transactionResponse) {
            if (SUCCESS.equalsIgnoreCase(transactionResponse.getTransactionStatus())) {
                getAbsaCacheService().setPersonalClientAgreementAccepted(true);
            }
            if (cashSendBeneficiaryConfirmation != null) {
                cashSendInteractor.requestCashSendPinEncryption(cashSendBeneficiaryConfirmation.getAccessPin(), pinEncryptionExtendedResponseListener);
                dismissProgressDialog();
            } else {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
            }
        }
    };

    protected ExtendedResponseListener<PINObject> pinEncryptionExtendedResponseListener = new ExtendedResponseListener<PINObject>() {
        @Override
        public void onSuccess(PINObject successResponse) {
            requestCashSendConfirmation(successResponse);
        }
    };

    @Override
    public void fillAccountsView(List<AccountObject> accounts) {
        PayBeneficiaryPaymentObject payBeneficiaryPaymentObject = new PayBeneficiaryPaymentObject();
        payBeneficiaryPaymentObject.setFromAccounts(accounts);
        AbsaCacheManager.getInstance().getModelForAccounts(payBeneficiaryPaymentObject, ApplicationFlowType.CASH_SEND_REBUILD);
        navigateToSelectAccount(accounts);
        dismissProgressDialog();
    }

    protected void navigateToSelectAccount(List<AccountObject> accountObjectList) {
        if (accountObjectList != null && !accountObjectList.isEmpty()) {
            SelectorList<AccountObjectWrapper> accounts = new SelectorList<>();
            for (AccountObject accountObject : accountObjectList) {
                accounts.add(new AccountObjectWrapper(accountObject));
            }
            accountSelectorRoundedInputView.setList(accounts, getString(R.string.select_account_toolbar_title));
            accountSelectorRoundedInputView.setItemSelectionInterface(index -> {
                lastSelectedIndex = index;
                fromAccount = accounts.get(index).getAccountObject();
                setFromAccount();
            });
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(FROM_ACCOUNT, fromAccount);
        outState.putString(AMOUNT, getAmountString());
        outState.putString(MY_REFERENCE, myReferenceInputView.getText());
        outState.putString(ATM_PIN, accessPinInputView.getText());
        saveInstanceState(IS_CLIENT_AGREEMENT_ACCEPTED, termsAndConditionsCheckBoxView.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle inState) {
        super.onRestoreInstanceState(inState);
        fromAccount = (AccountObject) inState.getSerializable(FROM_ACCOUNT);
        setFromAccount();
        restoreInstanceState(inState, AMOUNT, amountInputView);
        restoreInstanceState(inState, MY_REFERENCE, myReferenceInputView);
        restoreInstanceState(inState, ATM_PIN, accessPinInputView);
        restoreInstanceState(IS_CLIENT_AGREEMENT_ACCEPTED, termsAndConditionsCheckBoxView);
    }

    public void setLastSelectedIndex(int lastSelectedIndex) {
        this.lastSelectedIndex = lastSelectedIndex;
    }

    protected boolean setImage(@NonNull ImageView imageView, @NonNull String imageName) {
        AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(this);
        AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(imageName);
        if (addBeneficiaryObject != null) {
            byte[] oldImage = addBeneficiaryObject.getImageData();
            if (null != oldImage) {
                Bitmap profileBitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
                return ImageUtils.setImageFromBitmap(imageView, profileBitmap);
            }
        }
        return false;
    }

    protected void getAccounts() {
        PayBeneficiaryPaymentObject responseObj = new PayBeneficiaryPaymentObject();
        List<AccountObject> accountObjects = accountService.getFromAccountsFromCache(responseObj, ApplicationFlowType.CASH_SEND_REBUILD);
        accountObjects = FilterAccountList.getCashSendAccounts(accountObjects);
        if (accountObjects != null && !accountObjects.isEmpty()) {
            navigateToSelectAccount(accountObjects);
        }
    }

    protected void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(btnCashSend.getWindowToken(), 0);
        }
    }

    protected void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String amountAvailable = AccessibilityUtils.getTalkBackRandValueFromString(availableTextView.getText().toString());
            final String minValue = "R 50";
            final String maxValue = "R 3000";
            amountInputView.getEditText().setHint(null);
            accessPinInputView.setHintText(null);
            myReferenceInputView.setHintText(null);
            amountInputView.requestFocus();
            myReferenceInputView.setContentDescription(getString(R.string.talkback_cashsend_sn_reference));
            myReferenceInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_my_reference));
            amountInputView.setContentDescription(getString(R.string.talkback_cashsend_enter_amount));
            amountInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_enter_amount));
            availableTextView.setContentDescription(amountAvailable);
            accessPinInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_atm_pin));
            accessPinInputView.setContentDescription(getString(R.string.talkback_cashsend_atm_pin));
            accountSelectorRoundedInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_account_used_for_cashsend, accountSelectorRoundedInputView.getEditText().getText().toString()));
            accountSelectorRoundedInputView.setContentDescription(getString(R.string.talkback_cashsend_overview_account, accountSelectorRoundedInputView.getEditText().getText().toString()));
        }
    }

    protected void setFromAccount() {
        if (fromAccount != null) {
            accountSelectorRoundedInputView.setSelectedValue(fromAccount.getAccountInformation());

            animate(accountSelectorRoundedInputView, R.anim.expand_horizontal);
            String availableBalance = String.format("%s %s", fromAccount.getAvailableBalance().toString(), getString(R.string.available).toLowerCase());

            if (availableTextView != null) {
                availableTextView.setText(availableBalance);
            }
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
        }
    }

    protected String getAmountString() {
        return amountInputView.getSelectedValueUnmasked();
    }

    protected boolean validateCashSendBeneficiary() {
        if (lastSelectedIndex < 0) {
            AnimationHelper.shakeShakeAnimate(accountSelectorRoundedInputView);
            return false;
        }

        setUpAccount();

        if (!isValidAmount()) {
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

    protected boolean isValidATMPin() {
        if (ValidationUtils.validateATMPin(this.accessPinInputView, this.getString(R.string.atm_pin_tv), 6)) {
            if (cashSendBeneficiaryConfirmation == null) {
                setUpAccount();
            }
            if (cashSendBeneficiaryConfirmation == null) {
                cashSendBeneficiaryConfirmation = new CashSendBeneficiaryConfirmation();
            }
            cashSendBeneficiaryConfirmation.setAccessPin(this.accessPinInputView.getText());
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidReference() {
        if (ValidationUtils.validateInput(myReferenceInputView, this.getString(R.string.my_reference))) {
            if (cashSendBeneficiaryConfirmation == null) {
                setUpAccount();
            }
            cashSendBeneficiaryConfirmation.setMyReference(myReferenceInputView.getText());
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidAmount() {
        setUpAccount();
        if (ValidationUtils.validateInput(amountInputView, this.getString(R.string.amount))) {
            Double enteredAmount = Double.parseDouble(getAmountString());
            if (ValidationUtils.validateAmountInput(amountInputView, this.getString(R.string.amount), enteredAmount, 50.00, 3000.00)) {
                if (enteredAmount % 10 != 0) {
                    amountInputView.setError(getString(R.string.card_limit_mul_of_10_validation));
                    AccessibilityUtils.announceErrorFromTextWidget(amountInputView.getErrorTextView());
                    return false;
                } else if (fromAccount != null && enteredAmount > fromAccount.getAvailableBalance().getAmountDouble()) {
                    amountInputView.setError(String.format("%s %s", getString(R.string.balance_exceeded), getString(R.string.you_have_available, fromAccount.getAvailableBalanceFormated())));
                    AccessibilityUtils.announceErrorFromTextWidget(amountInputView.getErrorTextView());
                    return false;
                } else if (cashSendBeneficiaryConfirmation != null) {
                    cashSendBeneficiaryConfirmation.setAmount(new Amount(this.getResources().getString(R.string.currency), getAmountString()));
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void setUpAccount() {
        cashSendBeneficiaryConfirmation = new CashSendBeneficiaryConfirmation();
        if (fromAccount != null) {
            cashSendBeneficiaryConfirmation.setFromAccountNumber(fromAccount.getAccountNumber());
            cashSendBeneficiaryConfirmation.setAccountType(fromAccount.getDescription());
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
        }
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = findViewById(R.id.scrollView);

        if (scrollView != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, (int) view.getY()));
        }
    }

    protected abstract void requestCashSendConfirmation(PINObject pinObject);
}