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
package com.barclays.absa.banking.presentation.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiaryConfirmation;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.utils.extensions.StringExtensions;

public class PrepaidAirtimeResultActivity extends BaseActivity {

    private Button mMakeAnotherPurchaseBtn, mHomeBtn;
    private TextView purchaseStatus, mErrorMessageTv;
    private LottieAnimationView mResultAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initViews();
        onPopulateView((ResponseObject) getIntent().getSerializableExtra(RESULT));
    }

    private void initViews() {
        mMakeAnotherPurchaseBtn = findViewById(R.id.make_another_payment_btn);
        mHomeBtn = findViewById(R.id.btn_home);
        purchaseStatus = findViewById(R.id.tv_payment_status);
        mResultAnimation = findViewById(R.id.iv_img_result);
        mErrorMessageTv = findViewById(R.id.tv_error_msg);
    }

    public void onPopulateView(ResponseObject rob) {
        final AirtimeBuyBeneficiaryConfirmation beneficiaryAirtimeConfirmObject = (AirtimeBuyBeneficiaryConfirmation) rob;
        if (beneficiaryAirtimeConfirmObject != null && BMBConstants.CONST_SUCCESS.equalsIgnoreCase(beneficiaryAirtimeConfirmObject.getTransactionStatus())) {
            mScreenName = BMBConstants.PURCHASE_SUCCESSFUL_CONST;
            mSiteSection = BMBConstants.PREPAID_CONST;
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_SUCCESSFUL_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
            if (CommonUtils.isManageBeneficiaryPage()) {
                mMakeAnotherPurchaseBtn.setText(getString(R.string.manage_beneficiary_title));
            } else {
                mMakeAnotherPurchaseBtn.setText(getString(R.string.another_purchase));
            }
            purchaseStatus.setText(getString(R.string.purchase_success));
            mResultAnimation.setAnimation("general_success.json");

            mErrorMessageTv.setVisibility(View.VISIBLE);
            String prepaidAmount = TextFormatUtils.formatBasicAmount(new Amount(getIntent().getStringExtra(PREPAID_AMOUNT)));
            mErrorMessageTv.setText(getString(R.string.successful_airtime_purchase_message, prepaidAmount,
                    getIntent().getStringExtra(PROVIDER),
                    getIntent().getStringExtra(PREPAID_TYPE),
                    StringExtensions.toFormattedCellphoneNumber(getIntent().getStringExtra(BENEFICIARY_PHONE_NUMBER))));
            // Beneficiary Caching Related
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_PREPAID);
        } else {
            mScreenName = BMBConstants.PURCHASE_UNSUCCESSFUL_CONST;
            mSiteSection = BMBConstants.PREPAID_CONST;
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PURCHASE_UNSUCCESSFUL_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
            mMakeAnotherPurchaseBtn.setVisibility(View.GONE);
            purchaseStatus.setText(getString(R.string.purchase_unsuccessful));
            mErrorMessageTv.setVisibility(View.VISIBLE);
            mResultAnimation.setAnimation("general_failure.json");
            mErrorMessageTv.setText(beneficiaryAirtimeConfirmObject != null ? beneficiaryAirtimeConfirmObject.getTransactionMessage() : "");
        }

        mHomeBtn.setOnClickListener(view -> loadAccountsAndGoHome());

        mMakeAnotherPurchaseBtn.setOnClickListener(view -> {
            Intent in = new Intent(PrepaidAirtimeResultActivity.this, BuyPrepaidActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            in.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME);
            startActivity(in);
        });
    }

    @Override
    public void onBackPressed() {
    }
}
