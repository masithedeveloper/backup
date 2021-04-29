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
package com.barclays.absa.banking.buy.ui.airtime.buyOnceOff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOffConfirmation;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.databinding.PurchaseOverviewActivityBinding;
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccountBalanceUpdateHelper;
import com.barclays.absa.utils.SureCheckUtils;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.utils.extensions.StringExtensions;

public class OnceOffAirtimeConfirmActivity extends ConnectivityMonitorActivity implements View.OnClickListener {
    private AirtimeOnceOffConfirmation airtimeConfirmationObject;
    private PurchaseOverviewActivityBinding binding;
    private AirtimeOnceOffConfirmation airtimeOnceOffConfirmation;

    private final SureCheckDelegate buyAirtimeSureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            buyAirtimeOnceOff();
        }

        @Override
        public void onSureCheckRejected() {
            launchSureCheckRejectedResultScreen();
            dismissProgressDialog();
        }

        @Override
        public void onSureCheckFailed() {
            launchSureCheckFailedResultScreen();
            dismissProgressDialog();
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(OnceOffAirtimeConfirmActivity.this);
            dismissProgressDialog();
        }
    };

    private final ExtendedResponseListener<AirtimeOnceOffConfirmation> buyAirtimeResponseListener = new ExtendedResponseListener<AirtimeOnceOffConfirmation>(this) {
        @Override
        public void onSuccess(final AirtimeOnceOffConfirmation airtimeOnceOffConfirmationResponse) {
            buyAirtimeSureCheckDelegate.processSureCheck(OnceOffAirtimeConfirmActivity.this, airtimeOnceOffConfirmationResponse, () -> {
                OnceOffAirtimeConfirmActivity.this.airtimeOnceOffConfirmation = airtimeOnceOffConfirmationResponse;
                launchResultScreen();
                dismissProgressDialog();
            });
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            dismissProgressDialog();
            if (failureResponse != null) {
                String error = TextUtils.isEmpty(failureResponse.getErrorMessage()) ?
                        getString(R.string.generic_error) : failureResponse.getErrorMessage();
                launchFailureResultScreen(error);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.purchase_overview_activity, null, false);
        setContentView(binding.getRoot());

        binding.prepaidBeneficiaryLabelView.setVisibility(View.GONE);
        buyAirtimeResponseListener.setView(this);
        airtimeConfirmationObject = (AirtimeOnceOffConfirmation) getIntent().getSerializableExtra(RESULT);
        setUpToolbar();
        mScreenName = BMBConstants.PURCHASE_OVERVIEW_CONST;
        mSiteSection = BMBConstants.PREPAID_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_OVERVIEW_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
        binding.prepaidPurchaseConfirmButton.setOnClickListener(this);
        if (airtimeConfirmationObject != null) {
            onPopulateView(airtimeConfirmationObject);
        }
    }

    private void setUpToolbar() {
        setToolBarBack(getString(R.string.purchase_overview), (v) -> finish());
    }

    private void onPopulateView(AirtimeOnceOffConfirmation airtimeConfirmationObject) {
        String account = TextFormatUtils.formatAccountNumberAndDescription(airtimeConfirmationObject.getDescription(), airtimeConfirmationObject.getFromAccount());
        binding.prepaidFromAccountLabelView.setContentText(account);
        binding.prepaidNetworkProviderLabelView.setContentText(airtimeConfirmationObject.getInstitutionCode());
        binding.prepaidMobileNumberLabelView.setContentText(StringExtensions.toFormattedCellphoneNumber(airtimeConfirmationObject.getCellNumber()));
        binding.prepaidTypeLabelView.setContentText(airtimeConfirmationObject.getRechargeType());
        binding.prepaidPurchaseAmountLabelView.setContentText(airtimeConfirmationObject.getAmount() != null ? airtimeConfirmationObject.getAmount().toString() : "");
    }

    private void launchResultScreen() {
        if (SureCheckUtils.isResponseSuccessSureCheck(airtimeOnceOffConfirmation, this)) {
            if (airtimeOnceOffConfirmation != null) {
                String onceOffAirtimeConfirmationTransactionMessage = airtimeOnceOffConfirmation.getTransactionMessage();
                if (AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(onceOffAirtimeConfirmationTransactionMessage)) {
                    Intent prepaidIntent = new Intent(this, DualAuthPaymentPendingActivity.class);
                    prepaidIntent.putExtra(TRANSACTION_TYPE, TRANSACTION_TYPE_PREPAID);
                    startActivity(prepaidIntent);
                } else {
                    IntentFactory.IntentBuilder intentBuilder;
                    if (BMBConstants.CONST_SUCCESS.equalsIgnoreCase(airtimeOnceOffConfirmation.getTransactionStatus())) {
                        mScreenName = BMBConstants.PURCHASE_SUCCESSFUL_CONST;
                        mSiteSection = BMBConstants.PREPAID_CONST;
                        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_SUCCESSFUL_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
                        intentBuilder = IntentFactoryGenericResult.getSuccessfulResultBuilder(this);
                        intentBuilder.setGenericResultTopButton(R.string.another_purchase, v -> new AccountBalanceUpdateHelper(this).refreshHomeScreenAccountsAndBalances(new AccountRefreshInterface() {
                            @Override
                            public void onSuccess() {
                                navigateToPrepaidAirtimeHub();
                            }

                            @Override
                            public void onFailure() {
                                navigateToPrepaidAirtimeHub();
                            }
                        }));
                        intentBuilder.setGenericResultSubMessage(getString(R.string.successful_airtime_purchase_message,
                                StringExtensions.toRandAmount(airtimeOnceOffConfirmation.getAmount().getAmount()),
                                airtimeConfirmationObject.getInstitutionCode(),
                                airtimeOnceOffConfirmation.getRechargeType(),
                                StringExtensions.toFormattedCellphoneNumber(airtimeConfirmationObject.getCellNumber())));
                        intentBuilder.setGenericResultHeaderMessage(R.string.purchase_success);
                        intentBuilder.setGenericResultDoneButton(this);
                    } else {
                        mScreenName = BMBConstants.PURCHASE_UNSUCCESSFUL_CONST;
                        mSiteSection = BMBConstants.PREPAID_CONST;
                        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_UNSUCCESSFUL_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
                        intentBuilder = IntentFactoryGenericResult.getFailureResultBuilder(this);
                        intentBuilder.setGenericResultHeaderMessage(R.string.purchase_unsuccessful);
                        intentBuilder.setGenericResultSubMessage(airtimeOnceOffConfirmation.getTransactionMessage());
                        intentBuilder.setGenericResultHomeButton(this);
                    }
                    Intent onceOffPrepaidResultIntent = intentBuilder.build();
                    startActivity(onceOffPrepaidResultIntent);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CODE_SUCCESS_SURE_CHECK) {
            buyAirtimeOnceOff();
        } else if (resultCode == RESULT_CODE_REJECTED_SURE_CHECK) {
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.surecheck_error_unabletocomplete));
        }
    }

    @Override
    public void onClick(View v) {
        if (getAppCacheService().isInNoPrimaryDeviceState()) {
            getAppCacheService().setReturnToScreen(OnceOffAirtimeConfirmActivity.class);
            showNoPrimaryDeviceScreen();
        } else {
            buyAirtimeOnceOff();
        }
    }

    private void buyAirtimeOnceOff() {
        new PrepaidInteractor().onceOffAirtimeResult(airtimeConfirmationObject.getTxnReferenceNumber(), buyAirtimeResponseListener);
    }

    private void launchSureCheckFailedResultScreen() {
        Intent intent = buildResultIntent();
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed);
        startActivity(intent);
    }

    private void launchSureCheckRejectedResultScreen() {
        Intent intent = buildResultIntent();
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.another_purchase);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        startActivity(intent);
    }

    private Intent buildResultIntent() {
        GenericResultActivity.topOnClickListener = v -> {
            preventDoubleClick(v);
            Intent intent = new Intent(OnceOffAirtimeConfirmActivity.this, BuyPrepaidActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME);
            intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);

            startActivity(intent);
        };
        GenericResultActivity.bottomOnClickListener = v -> {
            preventDoubleClick(v);
            loadAccountsAndGoHome();
        };

        Intent intent = new Intent(OnceOffAirtimeConfirmActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.another_purchase);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        return intent;
    }

    private void launchFailureResultScreen(String message) {
        IntentFactory.IntentBuilder intentBuilder = IntentFactory.getFailureResultScreenBuilder(this, R.string.purchase_unsuccessful, message);
        intentBuilder.setGenericResultHomeButton(this);
        Intent onceOffPrepaidResultIntent = intentBuilder.build();
        startActivity(onceOffPrepaidResultIntent);

    }

    private void navigateToPrepaidAirtimeHub() {
        final Intent intent = new Intent(OnceOffAirtimeConfirmActivity.this, BuyPrepaidActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
