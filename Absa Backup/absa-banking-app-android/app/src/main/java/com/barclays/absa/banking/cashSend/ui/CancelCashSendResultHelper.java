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

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CancelCashSendResponse;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendUnredeemedAccounts;
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.cashSend.services.CashSendService;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.utils.AnalyticsUtil;

import static com.barclays.absa.banking.cashSend.ui.CashSendActivity.IS_CASH_SEND_PLUS;
import static com.barclays.absa.banking.framework.app.BMBConstants.RESULT;

class CancelCashSendResultHelper {

    void showResultScreen(final BaseActivity activity, boolean isCashSendPlus, CancelCashSendResponse cancelCashSendResponse) {
        IntentFactory.IntentBuilder builder = IntentFactoryGenericResult.getResultBuilder(activity)
                .setGenericResultDoneButton(activity, v -> {
                    ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).showProgressDialog();
                    navigateToPreviousCashSendScreen(activity, isCashSendPlus);
                });

        boolean isSuccess = BMBConstants.CONST_SUCCESS.equalsIgnoreCase(cancelCashSendResponse.getTransactionStatus());
        boolean authorisationNotRequired = BMBConstants.CONST_SUCCESS.equalsIgnoreCase(cancelCashSendResponse.getTransactionMessage());
        boolean isAuthorisationRequired = BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(cancelCashSendResponse.getTransactionMessage());
        boolean hasWarning = BMBConstants.CONST_WARNING.equalsIgnoreCase(cancelCashSendResponse.getTransactionStatus());

        if ((isSuccess || hasWarning) && authorisationNotRequired) {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedCancelSuccess_ScreenDisplayed");
            builder.setGenericResultIconToSuccessful();
            builder.setGenericResultHeaderMessage(R.string.cancel_cashsend_success_msg);
        } else if ((isSuccess || hasWarning) && isAuthorisationRequired) {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedCancelAuthorisationOutStanding_ScreenDisplayed");
            builder.setGenericResultIconToSuccessful();
            builder.setGenericResultHeaderMessage(R.string.unredeemed_cash_send_pending_authorisation);
        } else {
            AnalyticsUtil.INSTANCE.tagCashSend("UnredeemedCancelFailure_ScreenDisplayed");
            builder.setGenericResultIconToFailure();
            builder.setGenericResultHeaderMessage(R.string.cancel_cashsend_fail_msg);
            builder.setGenericResultSubMessage(cancelCashSendResponse.getErrorMessage());
        }
        activity.startActivity(builder.build());
    }

    private void navigateToPreviousCashSendScreen(BaseActivity activity, boolean isCashSendPlus) {
        CashSendService cashSendService = new CashSendInteractor();

        ExtendedResponseListener<CashSendUnredeemedAccounts> viewUnredeemedExtendedResponseListener = new ExtendedResponseListener<CashSendUnredeemedAccounts>() {
            @Override
            public void onSuccess(final CashSendUnredeemedAccounts cashSendUnredeemedAccounts) {
                ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).dismissProgressDialog();
                if (cashSendUnredeemedAccounts != null) {
                    Intent intent = new Intent(activity, UnredeemedTransactionActivity.class);
                    intent.putExtra(IS_CASH_SEND_PLUS, isCashSendPlus);
                    intent.putExtra(RESULT, cashSendUnredeemedAccounts);
                    activity.startActivity(intent);
                } else {
                    Intent cashSendIntent = new Intent(activity, CashSendActivity.class);
                    cashSendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
                    cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
                    activity.startActivity(cashSendIntent);
                }
            }
        };

        viewUnredeemedExtendedResponseListener.setView(activity);
        cashSendService.fetchUnredeemedCashSendList(isCashSendPlus, viewUnredeemedExtendedResponseListener);
    }
}