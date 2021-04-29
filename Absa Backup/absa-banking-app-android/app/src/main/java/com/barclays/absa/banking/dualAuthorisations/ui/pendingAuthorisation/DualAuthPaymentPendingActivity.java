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
package com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation;

import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsActivity;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DrawerOptions;

public class DualAuthPaymentPendingActivity extends DualAuthPendingActivity {

    @Override
    String getAuthTitle() {
        return String.format(getString(R.string.auth_title_payments_pending), transactionType);
    }

    @Override
    String getAuthExpiryMessage() {
        return getString(R.string.auth_expire_message);
    }

    @Override
    String getAuthContactMessage() {
        return getString(R.string.transaction_auth_contact_message);
    }

    @Override
    String getAuthPrimaryButtonText() {
        return String.format(getString(R.string.btn_make_another_payment), transactionType.toLowerCase());
    }

    @Override
    void onPrimaryButtonClicked() {
        switch (transactionType) {
            case TRANSACTION_TYPE_TRANSFER:
                launchTransferScreen();
                break;
            case TRANSACTION_TYPE_PAYMENT:
                if (CommonUtils.isManageBeneficiaryPage()) {
                    DrawerOptions.callPaymentManageBeneficiary(DualAuthPaymentPendingActivity.this);
                } else {
                    launchSelectBeneficiaryPaymentScreen();
                }
                break;
            case TRANSACTION_TYPE_PREPAID:
                if (CommonUtils.isManageBeneficiaryPage()) {
                    DrawerOptions.callPrepaidManageBeneficiary(DualAuthPaymentPendingActivity.this);
                } else {
                    launchSelectBeneficiaryPrepaidScreen();
                }
                break;
            case TRANSACTION_TYPE_CASH_SEND:
                if (CommonUtils.isManageBeneficiaryPage()) {
                    DrawerOptions.callCashSendManageBeneficiary(DualAuthPaymentPendingActivity.this);
                } else {
                    launchSelectBeneficiaryCashSendScreen();
                }
                break;
            default:
                break;
        }
    }

    private void launchSelectBeneficiaryPaymentScreen() {
        Intent paymentIntent = new Intent(DualAuthPaymentPendingActivity.this, PaymentsActivity.class);
        paymentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        paymentIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        paymentIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT);
        startActivity(paymentIntent);
    }

    private void launchTransferScreen() {
        Intent i = new Intent(DualAuthPaymentPendingActivity.this, TransferFundsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void launchSelectBeneficiaryPrepaidScreen() {
        Intent prepaidIntent = new Intent(DualAuthPaymentPendingActivity.this, BuyPrepaidActivity.class);
        prepaidIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        prepaidIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        prepaidIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME);
        startActivity(prepaidIntent);
    }

    private void launchSelectBeneficiaryCashSendScreen() {
        Intent cashSendIntent = new Intent(DualAuthPaymentPendingActivity.this, CashSendActivity.class);
        cashSendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        cashSendIntent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        cashSendIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);
        startActivity(cashSendIntent);
    }

}
