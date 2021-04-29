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
package com.barclays.absa.banking.settings.ui;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit;
import com.barclays.absa.banking.boundary.model.ManageCardSaveAccount;
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.ManageCardSaveAccountRequest;
import com.barclays.absa.banking.card.ui.CardIntentFactory;
import com.barclays.absa.banking.databinding.ActivityLimitsConfirmationBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AnalyticsUtil;

import styleguide.content.Card;

import static com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity.IS_FROM_CARD_HUB;
import static com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.MANAGE_CARD_ITEM;

public class SettingsChangeCardLimitConfirmActivity extends ConnectivityMonitorActivity implements View.OnClickListener {

    public static final String CREDIT_CARD_INDICATOR = "CC";
    public static final String CREDIT_CARD_INDICATOR_TWO = "credit";
    private ActivityLimitsConfirmationBinding binding;
    private ManageCardConfirmLimit mCardConfirmObject;
    private ManageCardSaveAccount manageCardLimitsResponse;
    private ManageCardResponseObject.ManageCardItem manageCardItem;
    private boolean isFromCreditCardHub;

    private ExtendedResponseListener<ManageCardSaveAccount> manageCardLimitsResponseListener = new ExtendedResponseListener<ManageCardSaveAccount>() {

        @Override
        public void onSuccess(final ManageCardSaveAccount successResponse) {
            super.onSuccess();
            cardLimitsSureCheckDelegate.processSureCheck(SettingsChangeCardLimitConfirmActivity.this, successResponse,
                    () -> {
                        manageCardLimitsResponse = successResponse;
                        if (BMBConstants.FAILURE.equalsIgnoreCase(manageCardLimitsResponse.getTransactionStatus())) {
                            launchFailureScreen(manageCardLimitsResponse.getTransactionMessage());
                        } else {
                            launchSuccessScreen();
                        }
                    });
        }

    };
    private SureCheckDelegate cardLimitsSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            changeCardLimits();
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(SettingsChangeCardLimitConfirmActivity.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_limits_confirmation, null, false);
        setContentView(binding.getRoot());

        manageCardLimitsResponseListener.setView(this);

        manageCardItem = getIntent().getParcelableExtra(MANAGE_CARD_ITEM);
        isFromCreditCardHub = getIntent().getBooleanExtra(IS_FROM_CARD_HUB, false);
        mScreenName = MANAGE_PAYMENT_TRANSFER_LIMITS_CONFIRM;
        mSiteSection = SETTINGS_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(MANAGE_PAYMENT_TRANSFER_LIMITS_CONFIRM, SETTINGS_CONST, TRUE_CONST);
        initViews();
        setToolBarBack(getString(R.string.confirm_limit_change));
    }

    private void initViews() {
        binding.saveButton.setOnClickListener(this);

        mCardConfirmObject = (ManageCardConfirmLimit) getIntent().getSerializableExtra(AppConstants.RESULT);
        if (mCardConfirmObject != null) {
            populateData();
        } else {
            toastLong(R.string.generic_error);
        }
    }

    private void populateData() {
        if (mCardConfirmObject != null && mCardConfirmObject.getCardNumber() != null && mCardConfirmObject.getCardType() != null) {
            String cardType = CREDIT_CARD_INDICATOR.equalsIgnoreCase(mCardConfirmObject.getCardType()) || CREDIT_CARD_INDICATOR_TWO.equalsIgnoreCase(mCardConfirmObject.getCardType()) ? getString(R.string.credit_card) : getString(R.string.debit_card);
            Card cardItem = new Card(cardType, mCardConfirmObject.getCardNumber());
            binding.accountCardView.setCard(cardItem);
        }

        if (mCardConfirmObject != null && (!"credit".equalsIgnoreCase(mCardConfirmObject.getCardType()) && !CREDIT_CARD_INDICATOR.equalsIgnoreCase(mCardConfirmObject.getCardType()))) {
            binding.posCurrentLimit.setVisibility(View.VISIBLE);
            binding.posNewLimit.setVisibility(View.VISIBLE);

            if (mCardConfirmObject.getOldPOSLimit() != null && mCardConfirmObject.getOldPOSLimit().getAmount() != null) {
                binding.posCurrentLimit.setContentText(mCardConfirmObject.getOldPOSLimit().toString());
            }
            if (mCardConfirmObject.getNewPOSLimit() != null && mCardConfirmObject.getNewPOSLimit().getAmount() != null) {
                binding.posNewLimit.setContentText(mCardConfirmObject.getNewPOSLimit().toString());
            }
        }
        if (mCardConfirmObject != null && mCardConfirmObject.getOldATMLimit() != null && mCardConfirmObject.getOldATMLimit().getAmount() != null) {
            binding.atmCurrentLimit.setContentText(mCardConfirmObject.getOldATMLimit().toString());
        }
        if (mCardConfirmObject != null && mCardConfirmObject.getNewATMLimit() != null && mCardConfirmObject.getNewATMLimit().getAmount() != null) {
            binding.atmNewLimit.setContentText(mCardConfirmObject.getNewATMLimit().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                binding.saveButton.setEnabled(false);
                showProgressDialog();
                if (getAppCacheService().isInNoPrimaryDeviceState()) {
                    getAppCacheService().setReturnToScreen(SettingsChangeCardLimitConfirmActivity.class);
                    showNoPrimaryDeviceScreen();
                } else {
                    changeCardLimits();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.saveButton.setEnabled(true);
        dismissProgressDialog();
    }

    private void changeCardLimits() {
        if (mCardConfirmObject != null) {
            ManageCardSaveAccountRequest manageCardLimitsRequest = new ManageCardSaveAccountRequest(mCardConfirmObject.getTxnRefID(), manageCardLimitsResponseListener);
            ServiceClient serviceClient = new ServiceClient(manageCardLimitsRequest);
            serviceClient.submitRequest();
        } else {
            toastLong(R.string.generic_error);
        }
    }

    private void launchSuccessScreen() {
        AnalyticsUtil.INSTANCE.trackAction("Update card limits", (isFromCreditCardHub ? "CreditCardLimit" : "DebitCardLimit") + "_UpdateLimitsSuccessScreen_SuccessScreenDisplayed");
        startActivity(CardIntentFactory.showCardLimitSuccessResultScreen(this, "", manageCardItem, isFromCreditCardHub));
    }

    private void launchFailureScreen(String failureMessage) {
        startActivity(CardIntentFactory.showCardLimitFailedResultScreen(this, failureMessage, manageCardItem));
    }
}
