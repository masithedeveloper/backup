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
import android.view.inputmethod.EditorInfo;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.FragmentIncreaseCreditCardLimitsBinding;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.Locale;

import styleguide.utils.extensions.StringExtensions;

public class CreditCardVCLAdjustLimitFragment extends BaseVCLFragment<FragmentIncreaseCreditCardLimitsBinding> {

    private final int MAX_INCREMENT_ROUND = 500;
    private final int MIN_INCREMENT_ROUND = 100;
    private final int INCREMENT_LIMIT = 50000;
    private VCLParcelableModel vclDataModel;
    private double minCreditLimitValue;
    private double maxCreditLimitValue;
    private double creditLimitDifference;
    private boolean isSelfChange;
    private CreditCardVCLView creditCardVCLView;

    public CreditCardVCLAdjustLimitFragment() {
    }

    public static CreditCardVCLAdjustLimitFragment newInstance() {
        return new CreditCardVCLAdjustLimitFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_increase_credit_card_limits;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Adjust credit limit");

        creditCardVCLView = (CreditCardVCLView) getActivity();
        vclDataModel = getParentActivity().getVCLDataModel();

        initViews();
        setupListeners();
        creditLimitDifference = maxCreditLimitValue - minCreditLimitValue;
    }

    private void initViews() {
        getParentActivity().setToolbarText(getString(R.string.vcl_income_verification_toolbar_title), true);
        getParentActivity().setCurrentProgress(3);

        binding.limitSlider.setStartText(TextFormatUtils.formatBasicAmountAsRand(getCurrentMinimumLimitSliderValue()));
        binding.limitSlider.setEndText(TextFormatUtils.formatBasicAmountAsRand(getCurrentMaximumLimitSliderValue()));
        binding.limitSlider.setMax(100);
        binding.limitSlider.setProgress(100);
        binding.currentLimitPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(getCurrentLimit()));
        binding.newLimitNormalInputView.setSelectedValue(String.format(Locale.ENGLISH, "%.0f", maxCreditLimitValue));
    }

    private int round(int number, int multiple) {
        int result = multiple;
        if (number % multiple != 0) {
            int division = (number / multiple) + 1;
            result = division * multiple;
        }
        return result;
    }

    private void setupListeners() {
        binding.limitSlider.getSeekbar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double creditLimitIncrement = ((double) progress / (double) 100) * creditLimitDifference;
                if (!binding.newLimitNormalInputView.isEnabled() && !isSelfChange) {
                    int incrementValue = (int) (minCreditLimitValue + creditLimitIncrement);
                    binding.newLimitNormalInputView.setSelectedValue(String.valueOf(round(incrementValue, incrementValue > INCREMENT_LIMIT ? MAX_INCREMENT_ROUND : MIN_INCREMENT_ROUND)));
                } else {
                    isSelfChange = false;
                }

                if (progress == 0) {
                    binding.newLimitNormalInputView.setSelectedValue(String.format(Locale.ENGLISH, "%.0f", minCreditLimitValue));
                } else if (progress == 100) {
                    binding.newLimitNormalInputView.setSelectedValue(String.format(Locale.ENGLISH, "%.0f", maxCreditLimitValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSelfChange = false;
                binding.newLimitNormalInputView.setEnabled(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.newLimitNormalInputView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                creditCardVCLView.hideKeyBoard();
                isInputValid();
            }
            return false;
        });

        binding.newLimitNormalInputView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.newLimitNormalInputView.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    binding.limitSlider.setProgress(calculatePercentageBasedOnInput("0"));
                } else {
                    binding.limitSlider.setProgress(calculatePercentageBasedOnInput(StringExtensions.removeCurrency(s.toString())));
                }
                isSelfChange = true;
            }
        });

        binding.reviewDetailsButton.setOnClickListener(v -> {
            if (isInputValid()) {
                AnalyticsUtil.INSTANCE.trackAction("Review information clicked");
                vclDataModel.setCreditLimitIncreaseAmount(binding.newLimitNormalInputView.getSelectedValue());
                creditCardVCLView.updateVCLDataModel(vclDataModel);
                getParentActivity().navigateToNextFragment(CreditCardVCLIncreaseConfirmationFragment.newInstance());
            }
        });
    }

    private int calculatePercentageBasedOnInput(String value) {
        if (value.isEmpty()) {
            value = "0.00";
        }
        if ((int) creditLimitDifference == 0) {
            return 100;
        }
        Double decimalPercentage = (Double.parseDouble(value) - minCreditLimitValue) / creditLimitDifference;
        return (int) (decimalPercentage * 100);
    }

    private String getCurrentLimit() {
        String currentCreditLimit;
        if (vclDataModel != null && vclDataModel.getCurrentCreditLimit() != null) {
            currentCreditLimit = vclDataModel.getCurrentCreditLimit();
        } else {
            currentCreditLimit = "0.00";
        }
        return currentCreditLimit;
    }

    private String getCurrentMinimumLimitSliderValue() {
        if (vclDataModel != null
                && vclDataModel.getCurrentCreditLimit() != null) {
            minCreditLimitValue = Double.parseDouble(vclDataModel.getCurrentCreditLimit()) + CreditCardVCLBaseActivity.CREDIT_LIMIT_INCREASE_AMOUNT;
        } else {
            minCreditLimitValue = CreditCardVCLBaseActivity.CREDIT_LIMIT_INCREASE_AMOUNT;
        }
        return String.valueOf(minCreditLimitValue);
    }

    private String getCurrentMaximumLimitSliderValue() {
        if (vclDataModel != null && vclDataModel.getNewCreditLimitAmount() != null) {
            maxCreditLimitValue = Double.parseDouble(vclDataModel.getNewCreditLimitAmount()) + Double.parseDouble(vclDataModel.getCurrentCreditLimit());
        } else {
            maxCreditLimitValue = CreditCardVCLBaseActivity.CREDIT_LIMIT_INCREASE_AMOUNT;
        }
        return String.valueOf(maxCreditLimitValue);
    }

    public boolean isInputValid() {
        boolean isInputValid;
        if (!binding.newLimitNormalInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.newLimitNormalInputView.getSelectedValueUnmasked()) >= minCreditLimitValue && Double.parseDouble(binding.newLimitNormalInputView.getSelectedValueUnmasked()) <= maxCreditLimitValue) {
            binding.newLimitNormalInputView.clearError();
            isInputValid = true;
        } else if (!binding.newLimitNormalInputView.getSelectedValueUnmasked().isEmpty() && Double.parseDouble(binding.newLimitNormalInputView.getSelectedValueUnmasked()) > maxCreditLimitValue) {
            isInputValid = false;
            binding.newLimitNormalInputView.setError(getString(R.string.vcl_income_max_limit_error, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(maxCreditLimitValue))));
        } else {
            isInputValid = false;
            binding.newLimitNormalInputView.setError(getString(R.string.vcl_income_can_not_be_less, TextFormatUtils.formatBasicAmountAsRand(String.valueOf(minCreditLimitValue))));
        }
        return isInputValid;
    }
}
