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

package com.barclays.absa.banking.newToBank;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSetPinFragmentBinding;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.forms.validation.ValidationExtensions;

public class NewToBankSetPinFragment extends ExtendedFragment<NewToBankSetPinFragmentBinding> {

    private NewToBankView newToBankView;

    public NewToBankSetPinFragment() {
    }

    public static NewToBankSetPinFragment newInstance() {
        return new NewToBankSetPinFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_set_pin_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();

        if (newToBankView != null) {
            newToBankView.showToolbar();
            newToBankView.hideProgressIndicator();
            newToBankView.setToolbarTitle(getToolbarTitle());
            String surePhrase = newToBankView.getNewToBankTempData().getCustomerDetails().getFullName();
            newToBankView.getNewToBankTempData().getRegistrationDetails().setSurePhrase(surePhrase);
            binding.clientSurePhraseContentLabelView.setContentText(surePhrase);
        }

        binding.nextButton.setOnClickListener(v -> {

            if (!isValidInput()) {
                return;
            }

            newToBankView.setOnlineBankingPIN(binding.enterPinNormalInputView.getSelectedValueUnmasked());
        });

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.enterPinNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.confirmPinNormalInputView);
    }

    protected void scrollToTopOfView(View view) {
        binding.scrollView.post(() -> binding.scrollView.smoothScrollTo(0, (int) view.getY()));
    }

    public boolean isValidInput() {
        if (binding.enterPinNormalInputView.getText().length() != 5) {
            binding.enterPinNormalInputView.setError(getString(R.string.new_to_bank_please_enter_5_digit_pin));
            scrollToTopOfView(binding.enterPinNormalInputView);
            return false;
        } else if (binding.confirmPinNormalInputView.getText().length() != 5) {
            binding.confirmPinNormalInputView.setError(getString(R.string.new_to_bank_please_enter_5_digit_pin));
            scrollToTopOfView(binding.confirmPinNormalInputView);
            return false;
        } else if (!binding.confirmPinNormalInputView.getText().equalsIgnoreCase(binding.enterPinNormalInputView.getText())) {
            binding.confirmPinNormalInputView.setError(getString(R.string.error_enter_same_pin));
            scrollToTopOfView(binding.confirmPinNormalInputView);
            return false;
        }

        return true;
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_your_digital_profile);
    }
}