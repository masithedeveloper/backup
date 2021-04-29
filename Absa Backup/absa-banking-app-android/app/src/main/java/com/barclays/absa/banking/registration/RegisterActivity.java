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

package com.barclays.absa.banking.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterAccountListObject;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.databinding.RegisterAccountSelectionActivityBinding;
import com.barclays.absa.banking.deviceLinking.ui.TermsAndConditionsSelectorActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.shared.RegistrationSelectorAccount;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;

import java.util.ArrayList;

import styleguide.forms.SelectorList;
import styleguide.utils.extensions.StringExtensions;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, RegisterPersonalDetailsView {

    private RegisterPersonalDetailsPresenter presenter;
    private RegisterAccountSelectionActivityBinding binding;
    private RegisterProfileDetail registrationProfile;
    private ArrayList<RegisterAccountListObject> registrationAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.register_account_selection_activity, null, false);
        setContentView(binding.getRoot());

        presenter = new RegisterPersonalDetailsPresenter(this);
        setToolBarBackWithMenu(getString(R.string.register), R.menu.cancel_menu);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            registrationProfile = (RegisterProfileDetail) bundle.getSerializable(getString(R.string.register_profile_detail_obj));
            if (registrationProfile != null) {
                registrationAccounts = registrationProfile.getAccounts();
            }
        }

        populatePersonalDataViews();
        populateAccountViews();
    }

    private void configureTalkBack(RegisterProfileDetail registerProfileDetail) {
        if (registerProfileDetail != null) {
            if (isAccessibilityEnabled()) {
                binding.personalDetailsHeader.setContentDescription(getString(R.string.talkback_account_selection_personal_details_header));
                binding.firstNameContentView.setContentDescription(getString(R.string.talkback_account_selection_first_name_label, registrationProfile.getFirstname()));
                binding.surnameContentView.setContentDescription(getString(R.string.talkback_account_selection_surname_label, registerProfileDetail.getSurname()));
                binding.idPassportContentView.setContentDescription(getString(R.string.talkback_account_selection_id_passport_label, registerProfileDetail.getRsaIdNumber() != null ? registerProfileDetail.getRsaIdNumber().replace("", ",") : ""));
                binding.accessAccountSelector.setContentDescription(getString(R.string.talkback_account_selection_account_selector, binding.accessAccountSelector.getText().replace("", ",")));
                binding.accessAccountSelector.setEditTextContentDescription(binding.accessAccountSelector.getText().replace("", ","));
                binding.billingAccountSelector.setContentDescription(getString(R.string.talkback_account_selection_billing_account_selector, binding.billingAccountSelector.getText().replace("", ",")));
                binding.billingAccountSelector.setEditTextContentDescription(binding.billingAccountSelector.getText().replace("", ","));
                binding.agreeToTermsCheckBoxView.setContentDescription(getString(R.string.talkback_account_Selection_checkbox));
                binding.nextButton.setContentDescription(getString(R.string.talkback_account_selection_next_button));
                binding.agreeToTermsCheckBoxView.getCheckBox().setContentDescription(getString(R.string.talkback_account_selection_checbox_box_description));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void populatePersonalDataViews() {
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        registrationProfile = (RegisterProfileDetail) extras.getSerializable(getString(R.string.register_profile_detail_obj));

        if (registrationProfile != null) {
            if (registrationProfile.getTitle() != null) {
                binding.titleContentView.setLabelText(getString(R.string.registration_title));
                binding.titleContentView.setContentText(StringExtensions.toTitleCase(registrationProfile.getTitle()));
            }
            if (registrationProfile.getFirstname() != null) {
                binding.firstNameContentView.setLabelText(getString(R.string.register_first_name));
                binding.firstNameContentView.setContentText(StringExtensions.toTitleCase(registrationProfile.getFirstname()));
            }
            if (registrationProfile.getSurname() != null) {
                binding.surnameContentView.setLabelText(getString(R.string.register_surname));
                binding.surnameContentView.setContentText(StringExtensions.toTitleCase(registrationProfile.getSurname()));
            }
            if (registrationProfile.getRsaIdNumber() != null) {
                binding.idPassportContentView.setLabelText(getString(R.string.register_details_id_passport));
                getAppCacheService().setCustomerIdNumber(registrationProfile.getRsaIdNumber());
                binding.idPassportContentView.setContentText(registrationProfile.getRsaIdNumber());
            }

            ArrayList<RegisterAccountListObject> accessAccountList = null;
            ArrayList<RegisterAccountListObject> billingAccountList = null;

            if (registrationProfile.getAccounts() != null) {
                accessAccountList = RebuildUtils.getAccessAccount(registrationProfile);
                billingAccountList = RebuildUtils.getBillingsAccount(registrationProfile);
            }

            if (accessAccountList != null && accessAccountList.size() == 1) {
                String accessAccountNumber = accessAccountList.get(0).getAccessBillingAccountNumber();
                String accessAccountType = StringExtensions.toTitleCaseRemovingCommas(accessAccountList.get(0).getAccountDescription());

                registrationProfile.setSelectedAccessAccountNo(String.format("%s\n%s", accessAccountType, accessAccountNumber));
            } else if (billingAccountList != null && billingAccountList.size() == 1) {
                String billingAccountNumber = billingAccountList.get(0).getAccessBillingAccountNumber();
                String billingAccountType = StringExtensions.toTitleCaseRemovingCommas(billingAccountList.get(0).getAccountDescription());

                registrationProfile.setSelectedBillingAccountNo(String.format("%s\n%s", billingAccountType, billingAccountNumber));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateAccountViews() {
        binding.agreeToTermsCheckBoxView.setOnCheckedListener(isChecked -> binding.agreeToTermsCheckBoxView.setErrorTextViewVisibility(false));
        binding.agreeToTermsCheckBoxView.setClickableLinkTitle(R.string.register_personal_client_terms_agreement, R.string.register_personal_client_terms_link_text, performClickOnClientAgreement, R.color.dark_grey);
        binding.nextButton.setOnClickListener(this);
        if (registrationProfile == null) {
            showMessageError(getString(R.string.generic_error), (dialog, which) -> CommonUtils.callWelcomeActivity(RegisterActivity.this));
            return;
        }
        SelectorList<RegistrationSelectorAccount> accounts = new SelectorList<>();
        if (registrationAccounts != null) {
            if (registrationAccounts.size() == 1) {
                setDefaultSelectedValuesByDefault();
            }
            for (RegisterAccountListObject account : registrationAccounts) {
                RegistrationSelectorAccount selectorAccount = new RegistrationSelectorAccount(account);
                accounts.add(selectorAccount);
            }

            binding.accessAccountSelector.setItemSelectionInterface(index -> {
                RegisterAccountListObject selectedAccount = registrationAccounts.get(index);
                String accountDescription = StringExtensions.toTitleCaseRemovingCommas(selectedAccount.getAccountDescription());
                String accountNumber = selectedAccount.getAccessBillingAccountNumber();
                registrationProfile.setSelectedAccessAccountNo(accountDescription + "\n" + accountNumber);
            });

            binding.accessAccountSelector.setList(accounts, getString(R.string.register_access_account_selector_title));

            binding.billingAccountSelector.setItemSelectionInterface(index -> {
                RegisterAccountListObject selectedAccount = registrationAccounts.get(index);
                String accountDescription = StringExtensions.toTitleCaseRemovingCommas(selectedAccount.getAccountDescription());
                String accountNumber = selectedAccount.getAccessBillingAccountNumber();
                registrationProfile.setSelectedBillingAccountNo(accountDescription + "\n" + accountNumber);
            });

            binding.billingAccountSelector.setList(accounts, getString(R.string.register_billing_account_selector_title));
            configureTalkBack(registrationProfile);
        }
    }

    private void setDefaultSelectedValuesByDefault() {
        RegisterAccountListObject selectedAccount = registrationAccounts.get(0);
        String accountDescription = StringExtensions.toTitleCaseRemovingCommas(selectedAccount.getAccountDescription());
        String accountNumber = selectedAccount.getAccessBillingAccountNumber();
        binding.accessAccountSelector.setSelectedValue(accountDescription + " (" + accountNumber + ")");
        binding.accessAccountSelector.setIconViewImage(-1);
        binding.billingAccountSelector.setSelectedValue(accountDescription + " (" + accountNumber + ")");
        binding.billingAccountSelector.setIconViewImage(-1);
        registrationProfile.setSelectedAccessAccountNo(accountDescription + "\n" + accountNumber);
        registrationProfile.setSelectedBillingAccountNo(accountDescription + "\n" + accountNumber);
    }

    @Override
    public void onClick(View view) {
        boolean isDataValid = performValidation();
        if (isDataValid) {
            Intent intent = new Intent(this, RegisterCreatePinActivity.class);
            intent.putExtra(getString(R.string.register_profile_detail_obj), registrationProfile);
            startActivity(intent);
        }
    }

    private boolean performValidation() {
        boolean isValid = true;
        if (registrationProfile.getSelectedAccessAccountNo() == null) {
            binding.accessAccountSelector.setError("Please select an access account");
            binding.accessAccountSelector.showError(true);
            announceErrorText(binding.accessAccountSelector.getErrorTextView());
            isValid = false;
        } else {
            binding.accessAccountSelector.showError(false);
        }

        if (registrationProfile.getSelectedBillingAccountNo() == null) {
            binding.billingAccountSelector.setError("Please select a billing account");
            binding.billingAccountSelector.showError(true);
            announceErrorText(binding.billingAccountSelector.getErrorTextView());
            isValid = false;
        } else {
            binding.billingAccountSelector.showError(false);
        }

        binding.agreeToTermsCheckBoxView.validate();
        announceErrorText(binding.agreeToTermsCheckBoxView.getErrorTextView());

        isValid = isValid && binding.agreeToTermsCheckBoxView.getIsValid();
        if (!isValid) {
            binding.agreeToTermsCheckBoxView.setErrorMessage(R.string.register_terms_and_conditions_error_message);
            ScrollView scrollView = binding.scrollView;
            scrollView.post(() -> scrollView.scrollTo(0, (int) binding.agreeToTermsCheckBoxView.getY()));
        }

        return isValid;
    }

    final private ClickableSpan performClickOnClientAgreement = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View view) {
            presenter.onTermsOfUseClicked(registrationProfile.getClientType());
        }
    };

    @Override
    public void launchTermsAndConditionReader(byte[] clientAgreementDoc) {
        startActivity(new Intent(this, TermsAndConditionsSelectorActivity.class));
    }

    @Override
    public void onBackPressed() {
        CommonUtils.callWelcomeActivity(this);
    }

    private void announceErrorText(TextView errorText) {
        if (isAccessibilityEnabled() && errorText != null && errorText.getText().toString().length() > 0) {
            AccessibilityUtils.announceRandValueTextFromView(errorText);
        }
    }
}
