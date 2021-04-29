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
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.databinding.OverdraftSummaryFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftStepsActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftSummaryFragment extends OverdraftBaseFragment<OverdraftSummaryFragmentBinding> implements View.OnClickListener {

    private static final String OVERDRAFT_QOUTE = "overdraftQuote";
    private OverdraftResponse overdraftResponse;
    private OverdraftQuoteSummary overdraftQuoteSummaryResponse;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    public static OverdraftSummaryFragment newInstance(OverdraftQuoteSummary overdraftQuoteSummary) {
        OverdraftSummaryFragment fragment = new OverdraftSummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OVERDRAFT_QOUTE, overdraftQuoteSummary);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_summary_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        getParentActivity().setToolBarWithNoBackButton(getString(R.string.overdraft_summary));

        if (getActivity() != null) {
            ((OverdraftStepsActivity) getActivity()).hideBackButton();
        }
        if (getArguments() != null) {
            overdraftQuoteSummaryResponse = (OverdraftQuoteSummary) getArguments().getSerializable(OVERDRAFT_QOUTE);
        }
        overdraftResponse = overdraftCacheService.getOverdraftResponse();
        binding.nextButton.setOnClickListener(this);
        initViews();
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_QuotationScreen_ScreenDisplayed");
    }

    private void initViews() {
        OverdraftSetup overdraftSetup = overdraftCacheService.getOverdraftSetup();
        if (overdraftQuoteSummaryResponse != null) {
            String overdraftAmount = (overdraftQuoteSummaryResponse.getOverdraftCreditLimit() != null) ? getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getOverdraftCreditLimit()) :
                    getString(R.string.overdraft_currency, overdraftSetup.getOverdraftAmount());
            binding.creditLimit.setContentText(overdraftAmount);
            binding.annualRate.setContentText(getString(R.string.percentage, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getAnnualInterestRate())));
            binding.fullContractualRate.setContentText(getString(R.string.percentage, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getContractualAnnualInterestRate())));
            binding.discountContractualRate.setContentText(getString(R.string.percentage, TextFormatUtils.formatBasicAmount(overdraftQuoteSummaryResponse.getDiscountContractualAnnualInterestRate())));
            binding.initiationFee.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getInitiationFee()));
            binding.monthlyFee.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getMonthlyServiceFee()));
            if (overdraftResponse.getCppAmount() != null && !overdraftResponse.getCppAmount().isEmpty() && "0.0".equals(overdraftResponse.getCppAmount())) {
                binding.creditProtection.setVisibility(View.VISIBLE);
                binding.creditProtection.setContentText(getString(R.string.overdraft_currency, overdraftQuoteSummaryResponse.getCreditLifeInsurance() + " pm"));
            }
        }
    }

    @Override
    public void onClick(View view) {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_QuotationScreen_NextButtonClicked");
        show(OverdraftIllustrativeCostFragment.newInstance(overdraftQuoteSummaryResponse));
    }
}
