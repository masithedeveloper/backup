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
package com.barclays.absa.banking.buy.ui.airtime.existing;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiaryConfirmation;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.databinding.PurchaseOverviewActivityBinding;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.transactions.PrepaidAirtimeResultActivity;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.SureCheckUtils;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.utils.extensions.StringExtensions;

public class PurchaseOverview extends ConnectivityMonitorActivity implements View.OnClickListener {
    private PurchaseOverviewActivityBinding binding;
    private AirtimeBuyBeneficiaryConfirmation beneficiaryAirtimeConfirmObject;
    private AirtimeBuyBeneficiaryConfirmation airtimeBuyBeneficiaryConfirmation;
    private String amount, provider, prepaidType, beneficiaryPhoneNumber;

    private final ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> buyAirtimeResponseListener = new ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation>() {
        @Override
        public void onSuccess(final AirtimeBuyBeneficiaryConfirmation successResponse) {
            buyAirtimeSureCheckDelegate.processSureCheck(PurchaseOverview.this, successResponse, () -> {
                airtimeBuyBeneficiaryConfirmation = successResponse;
                launchResultScreen();
                dismissProgressDialog();
            });
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if ("Payments/Prepaid/Validation/ExistingBundleError".equalsIgnoreCase(failureResponse.getResponseCode())) {
                GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();
                Intent intent = new Intent(PurchaseOverview.this, GenericResultActivity.class);
                intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_unsuccessful_white_large);
                intent.putExtra(GenericResultActivity.IS_FAILURE, true);
                intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
                intent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.existing_bundle_error_message);

                intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
                startActivity(intent);
            } else {
                super.onFailure(failureResponse);
            }
        }
    };

    private final SureCheckDelegate buyAirtimeSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            buyAirtimeForBeneficiary();
        }

        @Override
        public void onSureCheckFailed() {
            launchSureCheckFailedResultScreen();
            dismissProgressDialog();
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(PurchaseOverview.this);
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.purchase_overview_activity, null, false);
        setContentView(binding.getRoot());

        binding.prepaidBeneficiaryLabelView.setVisibility(View.VISIBLE);
        buyAirtimeResponseListener.setView(this);
        beneficiaryAirtimeConfirmObject = (AirtimeBuyBeneficiaryConfirmation) getIntent().getSerializableExtra(RESULT);
        if (beneficiaryAirtimeConfirmObject != null) {
            amount = beneficiaryAirtimeConfirmObject.getAmount().getAmount();
            provider = beneficiaryAirtimeConfirmObject.getNetworkProvider();
            prepaidType = beneficiaryAirtimeConfirmObject.getRechargeType();
            beneficiaryPhoneNumber = beneficiaryAirtimeConfirmObject.getCellNumber();
            setUpToolbar();
            mScreenName = BMBConstants.PURCHASE_OVERVIEW_CONST;
            mSiteSection = BMBConstants.PREPAID_CONST;
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_OVERVIEW_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
            onPopulateView();
            binding.prepaidPurchaseConfirmButton.setOnClickListener(this);
        } else {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
        }
        setupTalkBack();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String beneficiaryName = binding.prepaidBeneficiaryLabelView.getContentTextViewValue();
            final String mobileNumber = binding.prepaidMobileNumberLabelView.getContentTextViewValue();
            final String networkProvider = binding.prepaidNetworkProviderLabelView.getContentTextViewValue();
            final String purchaseAmount = AccessibilityUtils.getTalkBackRandValueFromString(binding.prepaidPurchaseAmountLabelView.getContentTextViewValue());
            final String prepaidType = binding.prepaidTypeLabelView.getContentTextViewValue();
            final String fromAccount = binding.prepaidFromAccountLabelView.getContentTextViewValue();

            binding.prepaidBeneficiaryLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_beneficiary_name, beneficiaryName));
            binding.prepaidMobileNumberLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_beneficiary_number, mobileNumber));
            binding.prepaidNetworkProviderLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_beneficiary_network, networkProvider));
            binding.prepaidPurchaseAmountLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_beneficiary_purchase_amount, purchaseAmount));
            binding.prepaidTypeLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_beneficiary_prepaid_type, prepaidType));
            binding.prepaidFromAccountLabelView.getContentTextView().setContentDescription(getString(R.string.talkback_prepaid_overview_from_account, fromAccount));
            binding.prepaidPurchaseConfirmButton.setContentDescription(getString(R.string.talkback_prepaid_overview_confirm_purchase));
        }
    }

    private void onPopulateView() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("Image_Name")) {
            mScreenName = BMBConstants.PREPAID_PURCHASE_OVERVIEW_CONST;
            mSiteSection = BMBConstants.PREPAID_CONST;
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PREPAID_PURCHASE_OVERVIEW_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
        }
        if (beneficiaryAirtimeConfirmObject != null) {
            String account = TextFormatUtils.formatAccountNumberAndDescription(beneficiaryAirtimeConfirmObject.getDescription(), beneficiaryAirtimeConfirmObject.getFromAccountNumber());
            binding.prepaidFromAccountLabelView.setContentText(account);
            binding.prepaidBeneficiaryLabelView.setContentText(beneficiaryAirtimeConfirmObject.getBeneficiaryName());
            binding.prepaidNetworkProviderLabelView.setContentText(beneficiaryAirtimeConfirmObject.getNetworkProvider());
            binding.prepaidMobileNumberLabelView.setContentText(StringExtensions.toFormattedCellphoneNumber(beneficiaryAirtimeConfirmObject.getCellNumber()));
            binding.prepaidTypeLabelView.setContentText(beneficiaryAirtimeConfirmObject.getRechargeType());
            binding.prepaidPurchaseAmountLabelView.setContentText(TextFormatUtils.formatBasicAmount(beneficiaryAirtimeConfirmObject.getAmount()));
        }
    }

    private void setUpToolbar() {
        setToolBarBack(getString(R.string.purchase_overview), (v) -> finish());
    }

    private void launchResultScreen() {
        if (SureCheckUtils.isResponseSuccessSureCheck(airtimeBuyBeneficiaryConfirmation, this)) {
            if (airtimeBuyBeneficiaryConfirmation != null) {
                String airtimeBuyBeneficiaryConfirmationMessage = airtimeBuyBeneficiaryConfirmation.getTransactionMessage();
                if (!TextUtils.isEmpty(airtimeBuyBeneficiaryConfirmationMessage) && airtimeBuyBeneficiaryConfirmationMessage.equalsIgnoreCase(AUTHORISATION_OUTSTANDING_TRANSACTION)) {
                    Intent airtimeIntent = new Intent(this, DualAuthPaymentPendingActivity.class);
                    airtimeIntent.putExtra(TRANSACTION_TYPE, TRANSACTION_TYPE_PREPAID);
                    startActivity(airtimeIntent);
                } else {
                    Intent prepaidResultIntent = new Intent(PurchaseOverview.this, PrepaidAirtimeResultActivity.class);
                    prepaidResultIntent.putExtra(AppConstants.RESULT, airtimeBuyBeneficiaryConfirmation);
                    prepaidResultIntent.putExtra(FROM_ACTIVITY, this.getIntent().getStringExtra("FromActivity"));
                    prepaidResultIntent.putExtra(PREPAID_AMOUNT, amount);
                    prepaidResultIntent.putExtra(PROVIDER, provider);
                    prepaidResultIntent.putExtra(PREPAID_TYPE, prepaidType);
                    prepaidResultIntent.putExtra(BENEFICIARY_PHONE_NUMBER, beneficiaryPhoneNumber);
                    startActivity(prepaidResultIntent);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        if (v.getId() == R.id.prepaidPurchaseConfirmButton) {
            preventDoubleClick(binding.prepaidPurchaseConfirmButton);
            if (getAppCacheService().isInNoPrimaryDeviceState()) {
                getAppCacheService().setReturnToScreen(PurchaseOverview.class);
                showNoPrimaryDeviceScreen();
            } else {
                buyAirtimeForBeneficiary();
            }
        }
    }

    private void buyAirtimeForBeneficiary() {
        new PrepaidInteractor().buyAirtimeResult(beneficiaryAirtimeConfirmObject.getTxnReferenceNumber(), buyAirtimeResponseListener);
    }

    private void launchSureCheckFailedResultScreen() {
        Intent intent = buildFailureResultIntent();
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
        startActivity(intent);
    }

    private Intent buildFailureResultIntent() {
        GenericResultActivity.bottomOnClickListener = v -> {
            preventDoubleClick(v);
            loadAccountsAndGoHome();
        };
        Intent intent = new Intent(PurchaseOverview.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        return intent;
    }
}
