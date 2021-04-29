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
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.card.ui.CardCache;
import com.barclays.absa.banking.databinding.CreditCardVclMonthlyFixedFragmentBinding;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.math.BigDecimal;

import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class CreditCardVCLMonthlyFixedInstallmentFragment extends BaseVCLFragment<CreditCardVclMonthlyFixedFragmentBinding>
        implements View.OnClickListener {

    private VCLParcelableModel vclDataModel;
    private VCLParcelableModel vclDataModelCacheContent;

    public CreditCardVCLMonthlyFixedInstallmentFragment() {
    }

    public static CreditCardVCLMonthlyFixedInstallmentFragment newInstance() {
        return new CreditCardVCLMonthlyFixedInstallmentFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_vcl_monthly_fixed_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Monthly fixed installment");
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
        vclDataModelCacheContent = CardCache.getInstance().retrieveVCLParcelableModel();
        vclDataModel = getParentActivity().getVCLDataModel();

        if (vclDataModel != null) {
            binding.totalExpenseItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(vclDataModel.getTotalFixedAmount()));
            binding.loansInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getLoansFixedAmount()));
            binding.creditCardInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getCreditCardFixedAmount()));
            binding.assetInstallmentInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getAssetFinanceFixedAmount()));
            binding.retailAccountsInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getRetailAccountsFixedAmount()));
            binding.bondInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getBondFixedAmount()));
            binding.otherExpenseInputView.setSelectedValue(StringExtensions.toDoubleString(vclDataModel.getOtherFixedAmount()));
        }
        addOnTextWatchers();
    }

    private void addOnTextWatchers() {
        binding.bondInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.bondInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getBondFixedAmount() : BASE_VALUE));
        binding.assetInstallmentInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.assetInstallmentInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getAssetFinanceFixedAmount() : BASE_VALUE));
        binding.loansInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.loansInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getLoansFixedAmount() : BASE_VALUE));
        binding.creditCardInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.creditCardInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getCreditCardFixedAmount() : BASE_VALUE));
        binding.retailAccountsInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.retailAccountsInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getRetailAccountsFixedAmount() : BASE_VALUE));
        binding.otherExpenseInputView.getEditText().setOnFocusChangeListener(new OnFocusedChangedListener(binding.otherExpenseInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getOtherFixedAmount() : BASE_VALUE));


        binding.bondInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getBondFixedAmount() : BASE_VALUE, binding.bondInputView));
        binding.assetInstallmentInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getAssetFinanceFixedAmount() : BASE_VALUE, binding.assetInstallmentInputView));
        binding.loansInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getLoansFixedAmount() : BASE_VALUE, binding.loansInputView));
        binding.creditCardInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getCreditCardFixedAmount() : BASE_VALUE, binding.creditCardInputView));
        binding.retailAccountsInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getRetailAccountsFixedAmount() : BASE_VALUE, binding.retailAccountsInputView));
        binding.otherExpenseInputView.addValueViewTextWatcher(new TextChangeListener(vclDataModelCacheContent != null ? vclDataModelCacheContent.getOtherFixedAmount() : BASE_VALUE, binding.otherExpenseInputView));
    }

    private boolean validateView(NormalInputView normalInputView, String minValue) {
        String baseString = normalInputView.getSelectedValueUnmasked();
        double baseValue = 0.00;
        if (!baseString.isEmpty()) {
            try {
                baseValue = Double.parseDouble(StringExtensions.removeSpaces(baseString).replace(",", "").replace("-",""));
            } catch (NumberFormatException e) {
                new MonitoringInteractor().logCaughtExceptionEvent(e);
            }
        }
        return !baseString.isEmpty() && baseValue >= Double.parseDouble(minValue);
    }

    private boolean hasOneViewValueChanged() {
        return !binding.bondInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getBondFixedAmount() : BASE_VALUE)
                || !binding.assetInstallmentInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getAssetFinanceFixedAmount() : BASE_VALUE)
                || !binding.loansInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getLoansFixedAmount() : BASE_VALUE)
                || !binding.creditCardInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getCreditCardFixedAmount() : BASE_VALUE)
                || !binding.retailAccountsInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getRetailAccountsFixedAmount() : BASE_VALUE)
                || !binding.otherExpenseInputView.getSelectedValueUnmasked().equalsIgnoreCase(vclDataModelCacheContent != null ? vclDataModelCacheContent.getOtherFixedAmount() : BASE_VALUE);
    }

    private boolean isAllFieldsValid() {
        return validateView(binding.bondInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getBondFixedAmount() : BASE_VALUE)
                && validateView(binding.assetInstallmentInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getAssetFinanceFixedAmount() : BASE_VALUE)
                && validateView(binding.loansInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getLoansFixedAmount() : BASE_VALUE)
                && validateView(binding.creditCardInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getCreditCardFixedAmount() : BASE_VALUE)
                && validateView(binding.retailAccountsInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getRetailAccountsFixedAmount() : BASE_VALUE)
                && validateView(binding.otherExpenseInputView, vclDataModelCacheContent != null ? vclDataModelCacheContent.getOtherFixedAmount() : BASE_VALUE)
                && hasOneViewValueChanged();
    }

    public void updateVCLDataFromViews() {
        String loansExpense = binding.loansInputView.getSelectedValueUnmasked();
        String creditCardExpense = binding.creditCardInputView.getSelectedValueUnmasked();
        String assetExpense = binding.assetInstallmentInputView.getSelectedValueUnmasked();
        String retailExpense = binding.retailAccountsInputView.getSelectedValueUnmasked();
        String bondExpense = binding.bondInputView.getSelectedValueUnmasked();
        String otherExpense = binding.otherExpenseInputView.getSelectedValueUnmasked();

        vclDataModel.setLoansFixedAmount(loansExpense);
        vclDataModel.setCreditCardFixedAmount(creditCardExpense);
        vclDataModel.setAssetFinanceFixedAmount(assetExpense);
        vclDataModel.setRetailAccountsFixedAmount(retailExpense);
        vclDataModel.setBondFixedAmount(bondExpense);
        vclDataModel.setOtherFixedAmount(otherExpense);

        getParentActivity().updateVCLDataModel(vclDataModel);
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
        private final double minInputValue;

        TextChangeListener(String minValue, NormalInputView inputView) {
            this.inputView = inputView;
            minInputValue = Double.parseDouble(minValue);
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
                inputValue = Double.parseDouble(inputString.replace("-", ""));
            }

            if (!inputString.isEmpty() && inputValue < minInputValue) {
                inputView.setError(getString(R.string.vcl_income_can_not_be_less, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(minInputValue))));
            } else {
                inputView.showError(false);
                binding.totalExpenseItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(calculateNewTotalFixedExpense().toString()));
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
            inputView.showError(false);
        }
    }

    private BigDecimal calculateNewTotalFixedExpense() {
        BigDecimal total;
        BigDecimal bond = BigDecimal.valueOf(Double.parseDouble(!binding.bondInputView.getSelectedValueUnmasked().isEmpty() ? binding.bondInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal asset = BigDecimal.valueOf(Double.parseDouble(!binding.assetInstallmentInputView.getSelectedValueUnmasked().isEmpty() ? binding.assetInstallmentInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal creditCard = BigDecimal.valueOf(Double.parseDouble(!binding.creditCardInputView.getSelectedValueUnmasked().isEmpty() ? binding.creditCardInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal loan = BigDecimal.valueOf(Double.parseDouble(!binding.loansInputView.getSelectedValueUnmasked().isEmpty() ? binding.loansInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal retail = BigDecimal.valueOf(Double.parseDouble(!binding.retailAccountsInputView.getSelectedValueUnmasked().isEmpty() ? binding.retailAccountsInputView.getSelectedValueUnmasked() : BASE_VALUE));
        BigDecimal other = BigDecimal.valueOf(Double.parseDouble(!binding.otherExpenseInputView.getSelectedValueUnmasked().isEmpty() ? binding.otherExpenseInputView.getSelectedValueUnmasked() : BASE_VALUE));
        total = bond.add(asset).add(creditCard).add(loan).add(retail).add(other);
        vclDataModel.setTotalFixedAmount(String.valueOf(total));
        return total;
    }
}
