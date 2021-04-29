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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.CommonUtils;

import java.util.ArrayList;

import styleguide.content.BeneficiaryListItem;
import styleguide.forms.validation.ValidationExtensions;
import styleguide.utils.extensions.StringExtensions;

@Deprecated
public class CashSendBeneficiaryActivity extends CashSendBaseActivity {
    private BeneficiaryDetailObject beneficiaryDetailObject;
    private Bundle cashSendBundle;
    private boolean isFromAddCashSendFlow = false;
    private boolean isCashSendPlus = false;

    private ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendBeneficiaryConfirmationResponseListener = new ExtendedResponseListener<CashSendBeneficiaryConfirmation>(this) {
        @Override
        public void onSuccess(final CashSendBeneficiaryConfirmation sendBenCashSendConfirmation) {
            Intent cashSendOverviewActivityIntent = new Intent(CashSendBeneficiaryActivity.this, CashSendOverViewActivity.class);
            cashSendOverviewActivityIntent.putExtra(AppConstants.RESULT, sendBenCashSendConfirmation);
            cashSendOverviewActivityIntent.putExtra(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus);
            cashSendOverviewActivityIntent.putExtras(cashSendBundle);
            cashSendOverviewActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cashSendOverviewActivityIntent);
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashsend_to_beneficiary_activity);
        clientAgreementDetailsResponseListener.setView(this);
        updateClientAgreementDetailsResponseListener.setView(this);
        cashSendBeneficiaryConfirmationResponseListener.setView(this);
        pinEncryptionExtendedResponseListener.setView(this);

        mScreenName = CASHSEND_DETAILS_CONST;
        mSiteSection = CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(CASHSEND_DETAILS_CONST, CASHSEND_CONST,
                TRUE_CONST);
        isFromAddCashSendFlow = getIntent().getBooleanExtra(AppConstants.IS_FROM_ADD_CASHSEND, false);
        isCashSendPlus = getIntent().getBooleanExtra(CashSendActivity.IS_CASH_SEND_PLUS, false);

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

        accountSelectorRoundedInputView = findViewById(R.id.accountSelectorView);
        accountSelectorRoundedInputView.setHint(R.string.select_account_hint);
        getAccounts();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BeneficiaryDetailObject detailObject = (BeneficiaryDetailObject) extras.getSerializable(RESULT);
            if (detailObject == null) {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog();
            } else {
                onPopulateView(detailObject);
            }
        }
        setupToolBar();
        setupTalkBack();
    }

    @Override
    public void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String minValue = "R 50";
            final String maxValue = "R 3000";
            amountInputView.setHintText(null);
            myReferenceInputView.setHintText(null);
            amountInputView.requestFocus();
            myReferenceInputView.setContentDescription(getString(R.string.talkback_cashsend_sn_reference));
            amountInputView.setContentDescription(getString(R.string.talkback_cashsend_enter_amount));
            availableTextView.setContentDescription(AccessibilityUtils.getTalkBackRandValueFromString(availableTextView.getText().toString()));
            accessPinInputView.setContentDescription(getString(R.string.talkback_cashsend_atm_pin));
            accountSelectorRoundedInputView.setEditTextContentDescription(getString(R.string.talkback_cashsend_account_used_for_cashsend, accountSelectorRoundedInputView.getSelectedValue()));
            TextView sendRangeTextView = findViewById(R.id.sendRangeTextView);
            sendRangeTextView.setContentDescription(getString(R.string.talkback_cashsend_min_max, AccessibilityUtils.getTalkBackRandValueFromString(minValue), AccessibilityUtils.getTalkBackRandValueFromString(maxValue)));
        }
    }

    private void setupToolBar() {
        setToolBarBack(getString(R.string.cashsend));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && isFromAddCashSendFlow) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void onPopulateView(BeneficiaryDetailObject detailObject) {
        this.beneficiaryDetailObject = detailObject;

        btnCashSend = findViewById(R.id.btnCashSend);
        btnCashSend.setOnClickListener(view -> {
            if (validateCashSendBeneficiary()) {
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(btnCashSend.getWindowToken(), 0);
                }
            }
        });

        clientAgreement = ClientAgreementHelper.fetchClientType(this);
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        } else {
            beneficiariesInteractor.fetchClientAgreementDetails(clientAgreementDetailsResponseListener);
        }

        String beneficiaryName = beneficiaryDetailObject.getBeneficiaryName() + " " + beneficiaryDetailObject.getBeneficiarySurName();
        myReferenceInputView.setText(beneficiaryDetailObject.getBenReference());
        String lastPaymentDetail = null;
        final ArrayList<TransactionObject> transactions = beneficiaryDetailObject.getTransactions();
        if (transactions != null && !transactions.isEmpty()) {
            Amount lastTransactionAmount = transactions.get(0).getAmount();
            String lastTransactionDate = transactions.get(0).getDate();
            if (lastTransactionAmount != null) {
                lastPaymentDetail = getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), getString(R.string.paid), lastTransactionDate);
            }
        }
        beneficiaryView.setBeneficiary(new BeneficiaryListItem(beneficiaryName, StringExtensions.toFormattedCellphoneNumber(beneficiaryDetailObject.getActNo()), lastPaymentDetail));

        if (beneficiaryDetailObject.getHasImage()) {
            AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(this);
            AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(beneficiaryDetailObject.getImageName());
            if (null != addBeneficiaryObject) {
                byte[] imageBytes = addBeneficiaryObject.getImageData();
                if (imageBytes != null) {
                    Bitmap benBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    beneficiaryView.setImage(benBitmap);
                }
            }
        }

        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            PayBeneficiaryPaymentObject paymentObject = new PayBeneficiaryPaymentObject();
            AbsaCacheManager.getInstance().getModelForAccounts(paymentObject, ApplicationFlowType.CASH_SEND_REBUILD);
            if (paymentObject.getFromAccounts() != null && paymentObject.getFromAccounts().size() > 0) {
                fromAccount = paymentObject.getFromAccounts().get(0);
                setLastSelectedIndex(0);
                setFromAccount();
            }
        } else {
            setLastSelectedIndex(-1);
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

        btnCashSend.setOnClickListener(view -> {
            if (validateCashSendBeneficiary()) {
                InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(btnCashSend.getWindowToken(), 0);
                }
                if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
                    cashSendInteractor.requestCashSendPinEncryption(cashSendBeneficiaryConfirmation.getAccessPin(), pinEncryptionExtendedResponseListener);
                } else {
                    beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementDetailsResponseListener);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(this, true, termsAndConditionsCheckBoxView, clientAgreement);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFromAddCashSendFlow) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.cancel_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item && isFromAddCashSendFlow) {
            if (CommonUtils.isManageBeneficiaryPage()) {
                mScreenName = CASHSEND_DETAILS_CONST;
                mSiteSection = MANAGE_CASHSEND_BENEFICIARIES_CONST;
                AnalyticsUtils.getInstance().trackCancelButton(CASHSEND_DETAILS_CONST,
                        MANAGE_CASHSEND_BENEFICIARIES_CONST);
                Intent intent = new Intent(this, BeneficiaryLandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
                intent.putExtra(PASS_BENEFICAIRY_TYPE, PASS_CASHSEND);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, CashSendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
                intent.putExtra(PASS_BENEFICAIRY_TYPE, PASS_CASHSEND);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void requestCashSendConfirmation(PINObject pinObj) {
        // Sanity check
        if (pinObj != null) {
            cashSendBundle = new Bundle();
            cashSendBundle.putString(ATM_ACCESS_PIN_KEY, accessPinInputView.getText().trim());
            if (beneficiaryDetailObject != null) {
                cashSendBundle.putString(BENEFICIARY_IMG_DATA, beneficiaryDetailObject.getImageName());
            }
            boolean termsAccepted = termsAndConditionsCheckBoxView.isChecked();
            if (beneficiaryDetailObject == null) {
                cashSendInteractor.requestCashSendConfirmation(cashSendBeneficiaryConfirmation, pinObj, termsAccepted, isCashSendPlus, cashSendBeneficiaryConfirmationResponseListener);
            } else {
                cashSendInteractor.requestCashSendExistingBeneficiaryConfirmation(beneficiaryDetailObject, cashSendBeneficiaryConfirmation, pinObj, termsAccepted, isCashSendPlus, cashSendBeneficiaryConfirmationResponseListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFromAddCashSendFlow) {
            finish();
        } else {
            // code for handling cancel event
            mScreenName = CASHSEND_DETAILS_CONST;
            mSiteSection = MANAGE_CASHSEND_BENEFICIARIES_CONST;
            AnalyticsUtils.getInstance().trackCancelButton(CASHSEND_DETAILS_CONST,
                    MANAGE_CASHSEND_BENEFICIARIES_CONST);
            Intent in = new Intent(this, BeneficiaryLandingActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            in.putExtra(PASS_BENEFICAIRY_TYPE, PASS_CASHSEND);
            startActivity(in);
        }
    }
}