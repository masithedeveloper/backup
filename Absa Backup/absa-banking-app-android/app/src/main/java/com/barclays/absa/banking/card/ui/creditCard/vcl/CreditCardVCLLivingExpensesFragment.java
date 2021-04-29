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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.IncomeAndExpensesResponse;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.CreditCardVclExpensesFragmentBinding;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.math.BigDecimal;

import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class CreditCardVCLLivingExpensesFragment extends BaseVCLFragment<CreditCardVclExpensesFragmentBinding> implements View.OnClickListener {

    private VCLParcelableModel vclDataModel;

    public CreditCardVCLLivingExpensesFragment() {
    }

    public static CreditCardVCLLivingExpensesFragment newInstance() {
        return new CreditCardVCLLivingExpensesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_vcl_expenses_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Living Expenses");

        getParentActivity().setOnBackPressedListener(new BaseBackPressedListener(getParentActivity()) {
            @Override
            public void doBack() {
                if (isAllFieldsValid()) {
                    updateVCLDataFromViews();
                }
            }
        });

        getParentActivity().setToolbarText(getString(R.string.vcl_income_verification_toolbar_title), true);
        getParentActivity().hideProgressIndicator();
        binding.continueButton.setOnClickListener(this);
        populateViews();
    }

    private void populateViews() {
        vclDataModel = getParentActivity().getVCLDataModel();

        if (vclDataModel != null && vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses() != null) {
            String totalMonthlyLivingExpense = StringExtensions.toDoubleString(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses());
            binding.totalLivingItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(totalMonthlyLivingExpense != null ? totalMonthlyLivingExpense : BASE_VALUE));
        }

        if (vclDataModel != null && vclDataModel.getIncomeAndExpense() != null) {
            IncomeAndExpensesResponse incomeAndExpenses = vclDataModel.getIncomeAndExpense();
            String groceries = StringExtensions.toDoubleString(vclDataModel.getGroceriesExpense() != null ? vclDataModel.getGroceriesExpense() : BASE_VALUE);
            String cellphone = StringExtensions.toDoubleString(incomeAndExpenses.getTelephoneExpense() != null ? incomeAndExpenses.getTelephoneExpense() : BASE_VALUE);
            String security = StringExtensions.toDoubleString(incomeAndExpenses.getSecurityExpense() != null ? incomeAndExpenses.getSecurityExpense() : BASE_VALUE);
            String insurance = StringExtensions.toDoubleString(incomeAndExpenses.getInsuranceExpense() != null ? incomeAndExpenses.getInsuranceExpense() : BASE_VALUE);
            String rateAndTax = StringExtensions.toDoubleString(vclDataModel.getMunicipalLevies() != null ? vclDataModel.getMunicipalLevies() : BASE_VALUE);
            String domestic = StringExtensions.toDoubleString(vclDataModel.getDomesticWorkerExpense() != null ? vclDataModel.getDomesticWorkerExpense() : BASE_VALUE);
            String entertainment = StringExtensions.toDoubleString(vclDataModel.getEntertainmentExpense() != null ? vclDataModel.getEntertainmentExpense() : BASE_VALUE);
            String education = StringExtensions.toDoubleString(vclDataModel.getEducationExpense() != null ? vclDataModel.getEducationExpense() : BASE_VALUE);
            String transport = StringExtensions.toDoubleString(vclDataModel.getTransportExpense() != null ? vclDataModel.getTransportExpense() : BASE_VALUE);
            String other = StringExtensions.toDoubleString(vclDataModel.getOtherLivingExpense() != null ? vclDataModel.getOtherLivingExpense() : BASE_VALUE);

            binding.groceriesInputView.setSelectedValue(groceries != null ? groceries : BASE_VALUE);
            binding.cellphoneInputView.setSelectedValue(cellphone != null ? cellphone : BASE_VALUE);
            binding.securityInputView.setSelectedValue(security != null ? security : BASE_VALUE);
            binding.insuraneInputView.setSelectedValue(insurance != null ? insurance : BASE_VALUE);
            binding.ratesAndTaxInputView.setSelectedValue(rateAndTax != null ? rateAndTax : BASE_VALUE);
            binding.domesticWorkerInputView.setSelectedValue(domestic != null ? domestic : BASE_VALUE);
            binding.entertainmentInputView.setSelectedValue(entertainment != null ? entertainment : BASE_VALUE);
            binding.educationInputView.setSelectedValue(education != null ? education : BASE_VALUE);
            binding.transportInputView.setSelectedValue(transport != null ? transport : BASE_VALUE);
            binding.otherInputView.setSelectedValue(other != null ? other : BASE_VALUE);
        } else {
            setBaseValue();
        }
        setupTextWatchers();
    }

    private void setupTextWatchers() {
        binding.groceriesInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.groceriesInputView, MAX_LIMIT_IMCOME));
        binding.cellphoneInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.cellphoneInputView, MIN_LIMIT_INCOME));
        binding.securityInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.securityInputView, MIN_LIMIT_INCOME));
        binding.insuraneInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.insuraneInputView, MAX_LIMIT_IMCOME));
        binding.ratesAndTaxInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.ratesAndTaxInputView, MIN_LIMIT_INCOME));
        binding.domesticWorkerInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.domesticWorkerInputView, MIN_LIMIT_INCOME));
        binding.entertainmentInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.entertainmentInputView, MIN_LIMIT_INCOME));
        binding.educationInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.educationInputView, MAX_LIMIT_IMCOME));
        binding.transportInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.transportInputView, MAX_LIMIT_IMCOME));
        binding.otherInputView.getEditText().setOnFocusChangeListener(new CreditCardVCLLivingExpensesFragment.OnFocusedChangedListener(binding.otherInputView, MAX_LIMIT_IMCOME));

        binding.groceriesInputView.addValueViewTextWatcher(new TextChangeListener(binding.groceriesInputView, MAX_VALUE_LIMIT));
        binding.cellphoneInputView.addValueViewTextWatcher(new TextChangeListener(binding.cellphoneInputView, MIN_VALUE_LIMIT));
        binding.securityInputView.addValueViewTextWatcher(new TextChangeListener(binding.securityInputView, MIN_VALUE_LIMIT));
        binding.insuraneInputView.addValueViewTextWatcher(new TextChangeListener(binding.insuraneInputView, MAX_VALUE_LIMIT));
        binding.ratesAndTaxInputView.addValueViewTextWatcher(new TextChangeListener(binding.ratesAndTaxInputView, MIN_VALUE_LIMIT));
        binding.domesticWorkerInputView.addValueViewTextWatcher(new TextChangeListener(binding.domesticWorkerInputView, MIN_VALUE_LIMIT));
        binding.entertainmentInputView.addValueViewTextWatcher(new TextChangeListener(binding.entertainmentInputView, MIN_VALUE_LIMIT));
        binding.educationInputView.addValueViewTextWatcher(new TextChangeListener(binding.educationInputView, MAX_VALUE_LIMIT));
        binding.transportInputView.addValueViewTextWatcher(new TextChangeListener(binding.transportInputView, MAX_VALUE_LIMIT));
        binding.otherInputView.addValueViewTextWatcher(new TextChangeListener(binding.otherInputView, MAX_VALUE_LIMIT));
    }

    private void setBaseValue() {
        binding.totalLivingItemView.setLineItemViewContent(BASE_VALUE);
        binding.groceriesInputView.setSelectedValue(BASE_VALUE);
        binding.cellphoneInputView.setSelectedValue(BASE_VALUE);
        binding.securityInputView.setSelectedValue(BASE_VALUE);
        binding.insuraneInputView.setSelectedValue(BASE_VALUE);
        binding.ratesAndTaxInputView.setSelectedValue(BASE_VALUE);
        binding.domesticWorkerInputView.setSelectedValue(BASE_VALUE);
        binding.entertainmentInputView.setSelectedValue(BASE_VALUE);
        binding.educationInputView.setSelectedValue(BASE_VALUE);
        binding.transportInputView.setSelectedValue(BASE_VALUE);
        binding.otherInputView.setSelectedValue(BASE_VALUE);
    }

    private boolean hasOneFieldChanged() {
        IncomeAndExpensesResponse incomeAndExpenses;
        if (vclDataModel != null && vclDataModel.getIncomeAndExpense() != null) {
            incomeAndExpenses = vclDataModel.getIncomeAndExpense();
        } else {
            incomeAndExpenses = null;
        }

        return !binding.groceriesInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getGroceriesExpense() : BASE_VALUE)
                || !binding.cellphoneInputView.getSelectedValueUnmasked().equalsIgnoreCase(incomeAndExpenses != null ? incomeAndExpenses.getTelephoneExpense() : BASE_VALUE)
                || !binding.securityInputView.getSelectedValueUnmasked().equalsIgnoreCase(incomeAndExpenses != null ? incomeAndExpenses.getSecurityExpense() : BASE_VALUE)
                || !binding.insuraneInputView.getSelectedValueUnmasked().equalsIgnoreCase(incomeAndExpenses != null ? incomeAndExpenses.getInsuranceExpense() : BASE_VALUE)
                || !binding.ratesAndTaxInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getMunicipalLevies() : BASE_VALUE)
                || !binding.domesticWorkerInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getDomesticWorkerExpense() : BASE_VALUE)
                || !binding.entertainmentInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getEntertainmentExpense() : BASE_VALUE)
                || !binding.educationInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getEducationExpense() : BASE_VALUE)
                || !binding.transportInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getTransportExpense() : BASE_VALUE)
                || !binding.otherInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModel != null ? vclDataModel.getOtherLivingExpense() : BASE_VALUE);
    }

    private boolean isAllFieldsValid() {
        return !binding.groceriesInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.cellphoneInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.securityInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.insuraneInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.ratesAndTaxInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.domesticWorkerInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.entertainmentInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.educationInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.transportInputView.getSelectedValueUnmasked().isEmpty()
                && !binding.otherInputView.getSelectedValueUnmasked().isEmpty()
                && hasOneFieldChanged();
    }

    public void updateVCLDataFromViews() {
        vclDataModel = getParentActivity().getVCLDataModel();

        if (vclDataModel != null && vclDataModel.getIncomeAndExpense() != null) {
            IncomeAndExpensesResponse incomeAndExpenses = vclDataModel.getIncomeAndExpense();

            String groceries = binding.groceriesInputView.getSelectedValueUnmasked();
            String cellphone = binding.cellphoneInputView.getSelectedValueUnmasked();
            String security = binding.securityInputView.getSelectedValueUnmasked();
            String insurance = binding.insuraneInputView.getSelectedValueUnmasked();
            String rateAndTax = binding.ratesAndTaxInputView.getSelectedValueUnmasked();
            String domestic = binding.domesticWorkerInputView.getSelectedValueUnmasked();
            String entertainment = binding.entertainmentInputView.getSelectedValueUnmasked();
            String education = binding.educationInputView.getSelectedValueUnmasked();
            String transport = binding.transportInputView.getSelectedValueUnmasked();
            String other = binding.otherInputView.getSelectedValueUnmasked();

            vclDataModel.setGroceriesExpense(groceries);
            incomeAndExpenses.setTelephoneExpense(cellphone);
            incomeAndExpenses.setSecurityExpense(security);
            incomeAndExpenses.setInsuranceExpense(insurance);
            if (vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null) {
                vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().setTotalMonhtlyLivingExpenses(calculateNewTotalFixedExpense().toString());
            }
            vclDataModel.setIncomeAndExpense(incomeAndExpenses);
            vclDataModel.setMunicipalLevies(rateAndTax);
            vclDataModel.setDomesticWorkerExpense(domestic);
            vclDataModel.setEntertainmentExpense(entertainment);
            vclDataModel.setEducationExpense(education);
            vclDataModel.setTransportExpense(transport);
            vclDataModel.setOtherLivingExpense(other);
            getParentActivity().updateVCLDataModel(vclDataModel);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                updateVCLDataFromViews();
                getParentActivity().navigateToPreviousScreen();
                break;
        }
    }

    class TextChangeListener implements TextWatcher {
        private NormalInputView inputView;
        private double maxInputValue;

        TextChangeListener(NormalInputView inputView, double maxInputValue) {
            this.inputView = inputView;
            this.maxInputValue = maxInputValue;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            inputView.showError(false);
            String inputString = inputView.getSelectedValueUnmasked();
            double inputValue;
            if (inputString.isEmpty()) {
                inputValue = 0.00;
            } else {
                inputValue = Double.parseDouble(inputString);
            }

            if (inputValue > maxInputValue) {
                inputView.setError(getString(R.string.vcl_income_max_limit_error, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(maxInputValue))));
            } else {
                inputView.showError(false);
                binding.totalLivingItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(calculateNewTotalFixedExpense().toString()));
            }
            binding.continueButton.setEnabled(isAllFieldsValid());
        }
    }

    class OnFocusedChangedListener implements View.OnFocusChangeListener {
        private NormalInputView inputView;
        private String defaultValue;

        OnFocusedChangedListener(NormalInputView inputView, String defaultValue) {
            this.inputView = inputView;
            this.defaultValue = defaultValue;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            String inputAmount = inputView.getSelectedValueUnmasked();
            String formattedInputValue;
            if (inputAmount.isEmpty()) {
                formattedInputValue = "0";
            } else if (inputView.getErrorTextView().getVisibility() == View.VISIBLE) {
                formattedInputValue = defaultValue;
            } else {
                formattedInputValue = inputAmount;
            }
            inputView.setSelectedValue(StringExtensions.toDoubleString(formattedInputValue));
        }
    }

    private BigDecimal calculateNewTotalFixedExpense() {
        BigDecimal total;
        BigDecimal groceries = BigDecimal.valueOf(Double.parseDouble(!binding.groceriesInputView.getSelectedValueUnmasked().isEmpty() ? binding.groceriesInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal telephone = BigDecimal.valueOf(Double.parseDouble(!binding.cellphoneInputView.getSelectedValueUnmasked().isEmpty() ? binding.cellphoneInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal security = BigDecimal.valueOf(Double.parseDouble(!binding.securityInputView.getSelectedValueUnmasked().isEmpty() ? binding.securityInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal insurance = BigDecimal.valueOf(Double.parseDouble(!binding.insuraneInputView.getSelectedValueUnmasked().isEmpty() ? binding.insuraneInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal municipal = BigDecimal.valueOf(Double.parseDouble(!binding.ratesAndTaxInputView.getSelectedValueUnmasked().isEmpty() ? binding.ratesAndTaxInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal domestic = BigDecimal.valueOf(Double.parseDouble(!binding.domesticWorkerInputView.getSelectedValueUnmasked().isEmpty() ? binding.domesticWorkerInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal entertainment = BigDecimal.valueOf(Double.parseDouble(!binding.entertainmentInputView.getSelectedValueUnmasked().isEmpty() ? binding.entertainmentInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal education = BigDecimal.valueOf(Double.parseDouble(!binding.educationInputView.getSelectedValueUnmasked().isEmpty() ? binding.educationInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal transport = BigDecimal.valueOf(Double.parseDouble(!binding.transportInputView.getSelectedValueUnmasked().isEmpty() ? binding.transportInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal other = BigDecimal.valueOf(Double.parseDouble(!binding.otherInputView.getSelectedValueUnmasked().isEmpty() ? binding.otherInputView.getSelectedValueUnmasked() : BASE_VALUE));
        total = groceries.add(telephone).add(security).add(insurance).add(municipal).add(domestic).add(education).add(entertainment).add(transport).add(other);
        return total;
    }
}
