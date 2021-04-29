/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.CreditCardVclQualifyActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

public class CreditCardVCLQualifyActivity extends BaseActivity {
    private CreditCardVclQualifyActivityBinding binding;
    private static final String VCL_DATA = "vcl_data";
    private VCLParcelableModel vclParcelableModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.credit_card_vcl_qualify_activity, null, false);
        setContentView(binding.getRoot());

        AnalyticsUtil.INSTANCE.trackAction("Credit card VCL", "Qualify overview");
        vclParcelableModel = getIntent().getParcelableExtra(VCL_DATA);
        onViewCreated();
    }

    public void onViewCreated() {
        updateVCLModel();
        binding.navigationLinearLayout.bringToFront();
        binding.applyNowButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Apply now");
            Intent intent = new Intent(CreditCardVCLQualifyActivity.this, CreditCardVCLBaseActivity.class);
            intent.putExtra(VCL_DATA, vclParcelableModel);
            intent.putExtra(CreditCardVCLBaseActivity.IS_FROM_EXPLORE, true);
            startActivity(intent);
        });
        binding.backImageView.setOnClickListener(v -> finish());
    }

    public void updateVCLModel() {
        String amount;
        if (vclParcelableModel != null && vclParcelableModel.getNewCreditLimitAmount() != null) {
            amount = TextFormatUtils.formatBasicAmountAsRand(vclParcelableModel.getNewCreditLimitAmount());
        } else {
            amount = TextFormatUtils.formatBasicAmountAsRand("0");
        }
        binding.titleTextView.setText(getString(R.string.vcl_offer_increase_limit, amount));
    }
}
