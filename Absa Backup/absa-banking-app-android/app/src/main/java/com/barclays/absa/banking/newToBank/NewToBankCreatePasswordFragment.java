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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankCreatePasswordFragmentBinding;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.PasswordValidator;

import java.util.HashMap;
import java.util.Map;

import styleguide.buttons.OptionActionButtonView;
import styleguide.forms.NormalInputView;

public class NewToBankCreatePasswordFragment extends ExtendedFragment<NewToBankCreatePasswordFragmentBinding> {

    private NewToBankView newToBankView;
    private final Map<String, Boolean> passwordRulesMap = new HashMap<>();

    public NewToBankCreatePasswordFragment() {
    }

    public static NewToBankCreatePasswordFragment newInstance() {
        return new NewToBankCreatePasswordFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_create_password_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();

        if (newToBankView != null) {
            newToBankView.showToolbar();
            newToBankView.setToolbarTitle(getToolbarTitle());
        }

        initViews();

        binding.continueButton.setOnClickListener(v -> {
            if (!isValidInput()) {
                return;
            }

            newToBankView.setOnlineBankingPassword(binding.userPasswordNormalInputView.getSelectedValueUnmasked());
        });

        binding.userPasswordNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.userPasswordNormalInputView.clearError();
                isValidPassword(binding.userPasswordNormalInputView);
            }
        });

        binding.userPasswordConfirmationNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.userPasswordConfirmationNormalInputView.clearError();
                isValidPassword(binding.userPasswordConfirmationNormalInputView);
            }
        });
    }

    private void initViews() {
        binding.mustNotContainName.setEnabled(false);
        binding.minimumLengthView.setEnabled(false);
        binding.noSequenceView.setEnabled(false);
        binding.numbersAndLettersView.setEnabled(false);
        binding.specialCharactersView.setEnabled(false);
    }

    protected void scrollToTopOfView(View view) {
        binding.scrollView.post(() -> binding.scrollView.smoothScrollTo(0, (int) view.getY()));
    }

    public boolean isValidInput() {
        if (!isValidPassword(binding.userPasswordNormalInputView)) {
            binding.userPasswordNormalInputView.setError(getString(R.string.new_to_bank_valid_password));
            scrollToTopOfView(binding.userPasswordNormalInputView);
            return false;
        } else if (!isValidPassword(binding.userPasswordConfirmationNormalInputView)) {
            binding.userPasswordConfirmationNormalInputView.setError(getString(R.string.new_to_bank_valid_password));
            scrollToTopOfView(binding.userPasswordConfirmationNormalInputView);
            return false;
        } else if (!binding.userPasswordConfirmationNormalInputView.getText().equalsIgnoreCase(binding.userPasswordNormalInputView.getText())) {
            binding.userPasswordConfirmationNormalInputView.setError(getString(R.string.fields_same));
            scrollToTopOfView(binding.userPasswordConfirmationNormalInputView);
            return false;
        }

        return true;
    }

    public boolean isValidPassword(NormalInputView userPasswordNormalInputView) {
        String password = userPasswordNormalInputView.getSelectedValueUnmasked();
        if (password.length() != 0) {
            passwordRulesMap.clear();
            passwordRulesMap.putAll(PasswordValidator.buildValidator(password, newToBankView.getNewToBankTempData().getCustomerDetails().getFullName()));
            return checkPasswordStatus();
        } else {
            markAllPasswordRulesInvalid();
        }
        return false;
    }

    private boolean checkPasswordStatus() {
        boolean containsBothLettersAndNumbers = passwordRulesMap.get(PasswordValidator.UPPER_N_LOWERCASE) && passwordRulesMap.get(PasswordValidator.DIGIT);
        changeLeftDrawable(containsBothLettersAndNumbers, binding.numbersAndLettersView);

        boolean hasValidLength = passwordRulesMap.get(PasswordValidator.LENGTH_RESTRICTION);
        changeLeftDrawable(hasValidLength, binding.minimumLengthView);

        boolean hasNotSpacesOrSpecialCharacters = !passwordRulesMap.get(PasswordValidator.SPECIAL_CASE) && !passwordRulesMap.get(PasswordValidator.WHITESPACE);
        changeLeftDrawable(hasNotSpacesOrSpecialCharacters, binding.specialCharactersView);

        boolean doesNotContainName = !passwordRulesMap.get(PasswordValidator.NAME);
        changeLeftDrawable(doesNotContainName, binding.mustNotContainName);

        boolean doesNotContainSequence = !passwordRulesMap.get(PasswordValidator.SEQUENTIAL_ASCENDING);
        changeLeftDrawable(doesNotContainName, binding.noSequenceView);

        return containsBothLettersAndNumbers && hasValidLength && hasNotSpacesOrSpecialCharacters && doesNotContainName && doesNotContainSequence;
    }

    public void markAllPasswordRulesInvalid() {
        changeLeftDrawable(false, binding.numbersAndLettersView);
        changeLeftDrawable(false, binding.minimumLengthView);
        changeLeftDrawable(false, binding.specialCharactersView);
        changeLeftDrawable(false, binding.mustNotContainName);
        changeLeftDrawable(false, binding.noSequenceView);
    }

    private void changeLeftDrawable(boolean isValid, OptionActionButtonView optionActionButtonView) {
        int markingDrawable = isValid ? R.drawable.ic_check_dark : R.drawable.ic_close;
        optionActionButtonView.setIcon(markingDrawable);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_your_digital_profile);
    }
}