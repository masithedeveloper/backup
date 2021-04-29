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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary;
import com.barclays.absa.banking.databinding.OverdraftIllustrativeCostFragmentBinding;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftIllustrativeCostFragment extends OverdraftBaseFragment<OverdraftIllustrativeCostFragmentBinding>
        implements View.OnClickListener {

    private static final String OVERDRAFT_QUOTE = "overdraftQuote";
    private OverdraftQuoteSummary overdraftQuoteSummaryResponse;

    public static OverdraftIllustrativeCostFragment newInstance(OverdraftQuoteSummary overdraftQuoteSummary) {
        OverdraftIllustrativeCostFragment fragment = new OverdraftIllustrativeCostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OVERDRAFT_QUOTE, overdraftQuoteSummary);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_illustrative_cost_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setToolBar(getString(R.string.overdraft_illustrative_cost), v -> getParentActivity().onBackPressed());

        if (getArguments() != null) {
            overdraftQuoteSummaryResponse = (OverdraftQuoteSummary) getArguments().getSerializable(OVERDRAFT_QUOTE);
        }
        initViews();
        binding.nextButton.setOnClickListener(this);
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IllustrativeCostScreen_ScreenDisplayed");
    }

    private void initViews() {
        if (overdraftQuoteSummaryResponse != null) {
            binding.creditLimit.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getCreditLimit()));
            binding.totalPayment.setContentText(getString(R.string.overdraft_currency, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getTotalOverdraftCost())));
            binding.overdraftCost.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getTotalOverdraftCost()));
            binding.overdraftAmount.setContentText(getString(R.string.overdraft_currency, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getTotalOverdraftCost())));
            binding.aggregatedInterest.setContentText(getString(R.string.overdraft_currency, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getAggregratedInterest())));
            binding.initiationFee.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getInitiationFee()));
            binding.monthlyFee.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getAggregatedMonthlyServiceFee()));
            binding.creditProtection.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getAggregatedCreditProtection()));
            binding.creditCostMultiple.setContentText(overdraftQuoteSummaryResponse.getCreditCostMultiple());
        }
    }

    @Override
    public void onClick(View view) {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IllustrativeCostScreen_NextButtonClicked");
        show(OverdraftTermsAndConditionsFragment.newInstance());
    }
}
