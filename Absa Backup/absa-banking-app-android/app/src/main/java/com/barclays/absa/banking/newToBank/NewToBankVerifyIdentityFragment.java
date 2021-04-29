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
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankVerifyIdentityFragmentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.NetworkUtils;
import com.barclays.absa.utils.PdfUtil;
import com.barclays.absa.utils.ValidationUtils;

import org.jetbrains.annotations.NotNull;

import styleguide.forms.validation.ValidationExtensions;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.NEW_TO_BANK_BUSINESS_EVOLVE_TERMS_OF_USE_URL;

public class NewToBankVerifyIdentityFragment extends ExtendedFragment<NewToBankVerifyIdentityFragmentBinding> {

    private NewToBankView newToBankView;

    public NewToBankVerifyIdentityFragment() {

    }

    public static NewToBankVerifyIdentityFragment newInstance() {
        return new NewToBankVerifyIdentityFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_verify_identity_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.setToolbarBackTitle(getToolbarTitle());
            newToBankView.showToolbar();

            if (newToBankView.isBusinessFlow()) {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYouScreen_ScreenDisplayed");
            } else if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_WhoYouAreScreen_ScreenDisplayed");
            } else {
                newToBankView.trackCurrentFragment(NewToBankConstants.VERIFY_IDENTITY_SCREEN);
            }
        }

        setUpComponentListeners();
    }

    private void setUpComponentListeners() {
        binding.nextButton.setOnClickListener(v -> {
            if (newToBankView.isBusinessFlow()) {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYouScreen_NextButtonClicked");
            } else if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_WhoYouAreScreen_NextButtonClicked");
            }

            String idNumber = binding.idNumberNormalInputView.getSelectedValueUnmasked();

            if (!isInputValid()) {
                return;
            }

            if (!newToBankView.isStudentFlow() && !isValidAge(idNumber)) {
                binding.idNumberNormalInputView.setError(getString(R.string.new_to_bank_min_age_error_message));
                return;
            }

            if (newToBankView.isStudentFlow() && !ValidationUtils.isValidAgeRange(idNumber, 18, 27)) {
                newToBankView.trackStudentAccount("StudentAccount_NotAppropriateAgeErrorScreen_ScreenDisplayed");
                newToBankView.navigateToGenericResultFragment(getString(R.string.new_to_bank_not_appropriate_age_message),
                        ResultAnimations.generalFailure, getString(R.string.new_to_bank_not_appropriate_age_title), false,
                        getString(R.string.new_to_bank_view_account_options), v1 -> newToBankView.navigateToChooseAccountScreen(), false);
                return;
            }

            saveCustomerDetails();
        });

        binding.emailAddressNormalInputView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.idNumberNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.surnameNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.cellphoneNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.emailAddressNormalInputView);

        binding.agreeToTermsCheckBoxView.setClickableLinkTitle(R.string.new_to_bank_agree_personal_client_agreement, R.string.new_to_bank_client_terms_link_text, performClickOnAgreement, R.color.dark_grey);
        if (newToBankView.isBusinessFlow()) {
            binding.agreeToTermsCheckBoxView.setClickableLinkTitle(R.string.relationship_banking_agree_business_client_agreement, R.string.relationship_banking_business_terms_link_text, performClickOnAgreement, R.color.dark_grey);
            binding.iAgreeTermsAndConditionsCheckBox.setClickableLinkTitle(R.string.relationship_banking_business_evolve_terms_of_use, R.string.business_evolve_terms_of_use, performClickOnBusinessEvolveTermsOfUse, R.color.dark_grey);
        } else {
            binding.iAgreeTermsAndConditionsCheckBox.setVisibility(View.GONE);
            binding.agreeToTermsCheckBoxView.setClickableLinkTitle(R.string.new_to_bank_agree_personal_client_agreement, R.string.new_to_bank_client_terms_link_text, performClickOnAgreement, R.color.dark_grey);
        }
    }

    private boolean isValidAge(String age) {
        if (!ValidationUtils.isOlderThan18(age)) {
            if (isBusinessAccount()) {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_DisplayUnderAgeErrorScreen_ScreenDisplayed");
            } else {
                newToBankView.trackStudentAccount("StudentAccount_DisplayUnderAgeErrorScreen_ScreenDisplayed");
            }
            newToBankView.navigateToGenericResultFragment(getString(R.string.new_to_bank_older_than_18),
                    ResultAnimations.generalFailure, getString(R.string.new_to_bank_older_than_18_title), false,
                    getString(R.string.close), v1 -> newToBankView.navigateToWelcomeActivity(), false);

            return false;
        }
        return true;
    }

    private void saveCustomerDetails() {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setIdNumber(binding.idNumberNormalInputView.getSelectedValueUnmasked());
        customerDetails.setCellphoneNumber(binding.cellphoneNormalInputView.getSelectedValueUnmasked());
        customerDetails.setEmail(binding.emailAddressNormalInputView.getSelectedValue());
        customerDetails.setSurname(binding.surnameNormalInputView.getSelectedValue().trim());
        customerDetails.setClientTypeGroup(newToBankView.isBusinessFlow() ? "SOLE_TRADER" : "INDIVIDUALS");
        newToBankView.saveCustomerDetails(customerDetails);
    }

    private boolean isInputValid() {
        binding.surnameNormalInputView.setText(binding.surnameNormalInputView.getText().replace("  ", " ").replace(" - ", "-").replace(" ' ", "'"));

        if (!ValidationUtils.isValidSouthAfricanIdNumber(binding.idNumberNormalInputView.getSelectedValueUnmasked())) {
            binding.idNumberNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_id));
            scrollToTopOfView(binding.idNumberNormalInputView);
            return false;
        } else {
            binding.idNumberNormalInputView.clearError();
        }

        if (!ValidationUtils.isValidSurname(binding.surnameNormalInputView.getSelectedValueUnmasked())) {
            binding.surnameNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_surname));
            scrollToTopOfView(binding.surnameNormalInputView);
            return false;
        } else {
            binding.surnameNormalInputView.clearError();
        }

        if (!ValidationUtils.isValidMobileNumber(binding.cellphoneNormalInputView.getSelectedValueUnmasked())) {
            binding.cellphoneNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_cellphone_number));
            scrollToTopOfView(binding.cellphoneNormalInputView);
            return false;
        } else {
            binding.cellphoneNormalInputView.clearError();
        }

        if (!ValidationUtils.isValidEmailAddress(binding.emailAddressNormalInputView.getSelectedValueUnmasked())) {
            binding.emailAddressNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_email));
            scrollToTopOfView(binding.emailAddressNormalInputView);
            return false;
        } else {
            binding.emailAddressNormalInputView.clearError();
        }

        if (!binding.agreeToTermsCheckBoxView.getIsValid()) {
            binding.agreeToTermsCheckBoxView.setErrorMessage(getString(newToBankView.isBusinessFlow() ? R.string.relationship_banking_conditions_checkbox_error : R.string.new_to_bank_checkbox_error));
            scrollToTopOfView(binding.agreeToTermsCheckBoxView);
            return false;
        }

        if (!binding.iAgreeTermsAndConditionsCheckBox.getIsValid() && newToBankView.isBusinessFlow()) {
            binding.iAgreeTermsAndConditionsCheckBox.setErrorMessage(getString(R.string.new_to_bank_agree_terms_of_conditions));
            scrollToTopOfView(binding.iAgreeTermsAndConditionsCheckBox);
            return false;
        }

        return true;
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = binding.scrollView;
        scrollView.post(() -> scrollView.smoothScrollTo(0, (int) view.getY()));
    }

    private final ClickableSpan performClickOnAgreement = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View widget) {
            getPersonalClientAgreement();
        }
    };

    private void getPersonalClientAgreement() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (NetworkUtils.INSTANCE.isNetworkConnected() && baseActivity != null) {
            String personalAgreement = newToBankView.isBusinessFlow() ? "B" : "I";
            PdfUtil.INSTANCE.showTermsAndConditionsClientAgreement(baseActivity, personalAgreement);

            if (newToBankView.isBusinessFlow()) {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYouScreen_IUnderstandAndAgreeToTheBusinessClientAgreementLinkClicked");
            } else {
                newToBankView.trackFragmentAction(NewToBankConstants.VERIFY_IDENTITY_SCREEN, NewToBankConstants.IDENTITY_SCREEN_TERMS_CLICKACTION);
            }
        } else {
            showMessageError(getString(R.string.network_connection_error));
        }
    }

    private final ClickableSpan performClickOnBusinessEvolveTermsOfUse = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View widget) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            if (NetworkUtils.INSTANCE.isNetworkConnected() && baseActivity != null) {
                PdfUtil.INSTANCE.showPDFInApp(baseActivity, NEW_TO_BANK_BUSINESS_EVOLVE_TERMS_OF_USE_URL);
            } else {
                showMessageError(getString(R.string.network_connection_error));
            }
        }
    };

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_who_you_are);
    }
}
