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
package com.barclays.absa.banking.overdraft.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.databinding.OverdraftIncomeExpenseFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftIncomeAndExpensePresenter;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftSummaryFragment;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import styleguide.content.SecondaryContentAndLabelView;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftIncomeAndExpenseFragment extends OverdraftBaseFragment<OverdraftIncomeExpenseFragmentBinding> implements OverdraftContracts.IncomeAndExpenseView, View.OnClickListener {

    private OverdraftQuoteDetailsObject overdraftQuoteDetailsObject;
    private OverdraftIncomeAndExpensePresenter presenter;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    public static OverdraftIncomeAndExpenseFragment newInstance() {
        return new OverdraftIncomeAndExpenseFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_income_expense_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getString(R.string.income_and_expenses), v -> getParentActivity().onBackPressed());

        presenter = new OverdraftIncomeAndExpensePresenter(new WeakReference<>(this));
        overdraftQuoteDetailsObject = overdraftCacheService.getOverdraftQuoteDetails();
        if (overdraftQuoteDetailsObject != null) {
            setText(binding.grossMonthlyIncomeView, overdraftQuoteDetailsObject.getTotalMonthlyGrossIncome());
            setText(binding.netMonthlyIncomeView, overdraftQuoteDetailsObject.getTotalMonthlyNetIncome());
            setText(binding.livingExpensesView, overdraftQuoteDetailsObject.getTotalMonthlyLivingExpenses());
            setText(binding.fixedDebtView, overdraftQuoteDetailsObject.getCustomerBureauCommitments());
            setText(binding.maintenanceExpenseView, overdraftQuoteDetailsObject.getCustomerMaintenanceExpenses());
            setText(binding.disposableIncomeView, overdraftQuoteDetailsObject.getTotalMonthlyDisposableIncome());
        }
        binding.confirmButton.setOnClickListener(this);
        binding.thisIsIncorrectButton.setOnClickListener(this);
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_ScreenDisplayed ");
    }

    void setText(SecondaryContentAndLabelView view, String amount) {
        String amountText = TextFormatUtils.formatBasicAmountAsRand(TextFormatUtils.formatBasicAmount(amount));
        view.setContentText(amountText);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OverdraftStepsActivity) getActivity()).hideSteps();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_ConfirmButtonClicked");
                presenter.onNextButtonClicked(overdraftQuoteDetailsObject);
                break;
            case R.id.thisIsIncorrectButton:
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_ThisIsIncorrectButtonClicked");
                startActivity(IntentFactoryOverdraft.getUnableToContinueScreen(getActivity(), R.string.unable_to_continue,
                        R.string.overdraft_unable_to_update_income_and_expenses));
                break;
        }
    }

    @Override
    public void navigateToQuoteSummaryScreen(@NotNull OverdraftResponse overdraftResponse, @NotNull OverdraftQuoteSummary overdraftQuoteSummaryResponse) {
        show(OverdraftSummaryFragment.newInstance(overdraftQuoteSummaryResponse));
    }

    @Override
    public void navigateToFailureScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_UnableToContinueErrorScreenDisplayed");
        startActivity(IntentFactoryOverdraft.getFailureResultScreen(getActivity(), R.string.error, R.string.generic_error));
    }

    @Override
    public void navigateToPolicyDeclineFailureScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_PolicyDeclineFailureScreenDisplayed");
        startActivity(IntentFactoryOverdraft.getPolicyDeclinedResultScreen(getParentActivity()));
    }

    @Override
    public void navigateToReferralScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_ReferralScreenDisplayed");
        startActivity(IntentFactoryOverdraft.getReferralResultScreen(getParentActivity()));
    }

    @Override
    public void navigateToQuoteWaitingScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_QuoteWaitingScreenDisplayed");
        startActivity(IntentFactoryOverdraft.getQuoteWaitingResultScreen(getParentActivity(), getString(R.string.overdraft_quote_waiting_description)));
    }
}
