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

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.CreditCardVclIncreaseCreditLimitBinding;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class CreditCardVCLIncomeAndExpensesFragment extends BaseVCLFragment<CreditCardVclIncreaseCreditLimitBinding> implements View.OnClickListener {
    private VCLParcelableModel vclDataModel;

    public CreditCardVCLIncomeAndExpensesFragment() {
    }

    public static CreditCardVCLIncomeAndExpensesFragment newInstance() {
        return new CreditCardVCLIncomeAndExpensesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_vcl_increase_credit_limit;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentActivity().setOnBackPressedListener(new BaseBackPressedListener(getParentActivity()) {
            @Override
            public void doBack() {
                if (isAllFieldsValid()) {
                    updateVCLDataFromViews();
                }
            }
        });

        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Income and expenses");

        getParentActivity().setToolbarText(getString(R.string.vcl_income_verification_toolbar_title), true);
        getParentActivity().setCurrentProgress(1);
        binding.continueButton.setOnClickListener(this);
        binding.totalFixedDebtInstallmentInputView.setCustomOnClickListener((View v) -> {
            AnalyticsUtil.INSTANCE.trackAction("Fixed installments clicked");
            getParentActivity().navigateToNextFragment(CreditCardVCLMonthlyFixedInstallmentFragment.newInstance());
        });
        binding.totalMonthlyLivingExpenseInputView.setCustomOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Monthly living expenses clicked");
            getParentActivity().navigateToNextFragment(CreditCardVCLLivingExpensesFragment.newInstance());
        });
        vclDataModel = getParentActivity().getVCLDataModel();
        populateData();
    }

    private void populateData() {
        binding.totalMonthlyLivingExpenseInputView.setImageViewVisibility(View.VISIBLE);
        binding.totalFixedDebtInstallmentInputView.setImageViewVisibility(View.VISIBLE);

        if (vclDataModel != null && vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null) {
            String grossMonthlyIncome = StringExtensions.toDoubleString(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalGrossMonthlyIncome());
            String netMonthlyIncome = StringExtensions.toDoubleString(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalNetMonthlyIncome());
            String totalMonthlyLivingExpenses = StringExtensions.toDoubleString(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses());

            binding.grossMonthlyIncomeInputView.setSelectedValue(grossMonthlyIncome != null ? grossMonthlyIncome : BASE_VALUE);
            binding.netMonthlyIncomeInputView.setSelectedValue(netMonthlyIncome != null ? netMonthlyIncome : BASE_VALUE);
            new Handler(Looper.getMainLooper()).post(() -> binding.totalMonthlyLivingExpenseInputView.getEditText().setText(totalMonthlyLivingExpenses != null ? totalMonthlyLivingExpenses : BASE_VALUE));
        } else {
            setBaseValues();
        }
        if (vclDataModel != null) {
            String maintenanceValue = StringExtensions.toDoubleString(vclDataModel.getMaintenanceExpense());
            String fixedDebtInstallment = StringExtensions.toDoubleString(vclDataModel.getTotalFixedAmount());
            binding.maintenanceExpenseInputView.setSelectedValue(maintenanceValue != null ? maintenanceValue : BASE_VALUE);
            new Handler(Looper.getMainLooper()).post(() -> binding.totalFixedDebtInstallmentInputView.getEditText().setText(fixedDebtInstallment));
        } else {
            binding.maintenanceExpenseInputView.setSelectedValue(BASE_VALUE);
            binding.totalFixedDebtInstallmentInputView.setSelectedValue(BASE_VALUE);
        }

        binding.grossMonthlyIncomeInputView.addValueViewTextWatcher(new TextChangeListener(binding.grossMonthlyIncomeInputView));
        binding.netMonthlyIncomeInputView.addValueViewTextWatcher(new TextChangeListener(binding.netMonthlyIncomeInputView));
        binding.maintenanceExpenseInputView.addValueViewTextWatcher(new TextChangeListener(binding.maintenanceExpenseInputView));
    }

    private void setBaseValues() {
        binding.grossMonthlyIncomeInputView.setSelectedValue(BASE_VALUE);
        binding.netMonthlyIncomeInputView.setSelectedValue(BASE_VALUE);
        binding.totalMonthlyLivingExpenseInputView.setSelectedValue(BASE_VALUE);
    }

    private void showValidationIssues() {
        if (binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty()) {
            showNormalInputViewError(binding.grossMonthlyIncomeInputView, getString(R.string.manage_card_limit_empty_value_error));
        } else if (Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked()) > MAX_VALUE_LIMIT) {
            showNormalInputViewError(binding.grossMonthlyIncomeInputView, getString(R.string.vcl_income_max_limit_error, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(MAX_VALUE_LIMIT))));
        } else if (Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked()) == 0) {
            showNormalInputViewError(binding.grossMonthlyIncomeInputView, getString(R.string.vcl_error_not_zero));
        } else if (!binding.netMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()) > Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked())) {
            showNormalInputViewError(binding.netMonthlyIncomeInputView, getString(R.string.vcl_income_and_expenses_net_may_not_be_more_error));
        } else if (!binding.netMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()) == 0) {
            showNormalInputViewError(binding.netMonthlyIncomeInputView, getString(R.string.vcl_error_not_zero));
        } else if (!binding.netMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && binding.netMonthlyIncomeInputView.getSelectedValue().isEmpty()) {
            showNormalInputViewError(binding.netMonthlyIncomeInputView, getString(R.string.manage_card_limit_empty_value_error));
        } else if (!binding.netMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()) > MAX_VALUE_LIMIT) {
            showNormalInputViewError(binding.netMonthlyIncomeInputView, getString(R.string.vcl_income_max_limit_error, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(MAX_VALUE_LIMIT))));
        } else if (binding.maintenanceExpenseInputView.getSelectedValueUnmasked().isEmpty()) {
            showNormalInputViewError(binding.maintenanceExpenseInputView, getString(R.string.manage_card_limit_empty_value_error));
        }
    }


    private void showNormalInputViewError(NormalInputView normalInputView, String errorMessage) {
        ObjectAnimator.ofInt(binding.scrollView, "scrollY", normalInputView.getTop()).setDuration(FAST_SCROLL_TIME).start();
        normalInputView.setError(errorMessage);
        normalInputView.showError(true);
    }

    private boolean isAllFieldsValid() {
        return (!binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked()) < MAX_VALUE_LIMIT && Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked()) > 0)
                && (!binding.netMonthlyIncomeInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()) < MAX_VALUE_LIMIT && Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()) > 0)
                && (Double.parseDouble(binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked()) >= Double.parseDouble(binding.netMonthlyIncomeInputView.getSelectedValueUnmasked()))
                && !binding.maintenanceExpenseInputView.getSelectedValueUnmasked().isEmpty();
    }

    public void updateVCLDataFromViews() {
        if (getParentActivity() != null) {
            if (vclDataModel != null && vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null) {
                String totalMonthlyLivingExpenses = binding.totalMonthlyLivingExpenseInputView.getSelectedValueUnmasked();
                String grossMonthlyIncome = binding.grossMonthlyIncomeInputView.getSelectedValueUnmasked();
                String netMonthlyIncome = binding.netMonthlyIncomeInputView.getSelectedValueUnmasked();

                vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().setTotalMonhtlyLivingExpenses(totalMonthlyLivingExpenses);
                vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().setTotalGrossMonthlyIncome(grossMonthlyIncome);
                vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().setTotalNetMonthlyIncome(netMonthlyIncome);
            }
            if (vclDataModel != null && vclDataModel.getIncomeAndExpense() != null) {
                String fixedDebtInstallment = binding.totalFixedDebtInstallmentInputView.getSelectedValueUnmasked();
                String maintenanceValue = binding.maintenanceExpenseInputView.getSelectedValueUnmasked();

                vclDataModel.getIncomeAndExpense().setTotalMonthlyFixedDebtInstallment(fixedDebtInstallment);
                vclDataModel.setTotalFixedAmount(fixedDebtInstallment);
                vclDataModel.setMaintenanceExpense(maintenanceValue);
                getParentActivity().updateVCLDataModel(vclDataModel);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                if (isAllFieldsValid()) {
                    AnalyticsUtil.INSTANCE.trackAction("Continue clicked");
                    updateVCLDataFromViews();
                    getParentActivity().navigateToNextFragment(CreditCardVCLIncomeSourceFragment.newInstance());
                } else {
                    showValidationIssues();
                }
                break;
        }
    }

    class TextChangeListener implements TextWatcher {
        private NormalInputView normalInputView;

        TextChangeListener(NormalInputView normalInputView) {
            this.normalInputView = normalInputView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(normalInputView.getDescriptionTextView().getText().toString())) {
                normalInputView.showDescription(true);
            }
            normalInputView.showError(false);
            normalInputView.hideError();
        }
    }
}
