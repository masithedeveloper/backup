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

package com.barclays.absa.banking.presentation.homeLoan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.PolicyClaim;
import com.barclays.absa.banking.databinding.ActivityHomeLoanPerilsContactDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.ValidationUtils;

import styleguide.utils.extensions.StringExtensions;

public class HomeLoanPerilsContactDetailsActivity extends BaseActivity implements HomeLoanPerilsContactDetailsView {
    public static final String POLICY_CLAIM_DETAILS = "policyClaimDetails";
    ActivityHomeLoanPerilsContactDetailsBinding contactDetailsBinding;
    private static final int MINIMUM_CONTACT_NAME_LENGTH = 3;
    private PolicyClaim policyClaim;
    private boolean isFromPropertyInsurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactDetailsBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_home_loan_perils_contact_details, null, false);
        setContentView(contactDetailsBinding.getRoot());

        setToolBarBack(R.string.contact_details);
        HomeLoanPerilsContactDetailsPresenter loanPerilsContactDetailsPresenter = new HomeLoanPerilsContactDetailsPresenter(this);
        loanPerilsContactDetailsPresenter.loadContactDetails();
        policyClaim = (PolicyClaim) getIntent().getSerializableExtra(HomeloanPerilsClaimDetailsActivity.POLICY_CLAIM_DETAILS);
        isFromPropertyInsurance = getIntent().getBooleanExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, false);

        contactDetailsBinding.contactDetailsContinueButton.setOnClickListener(v -> {
            if (isValidData()) {
                setPolicyClaimDetails();
                Intent overViewIntent = new Intent(HomeLoanPerilsContactDetailsActivity.this, HomeLoanPerilsClaimOverviewActivity.class);
                overViewIntent.putExtra(POLICY_CLAIM_DETAILS, policyClaim);
                overViewIntent.putExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, isFromPropertyInsurance);
                startActivity(overViewIntent);
            }
        });

        contactDetailsBinding.alternativeContactPersonView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > MINIMUM_CONTACT_NAME_LENGTH) {
                    contactDetailsBinding.alternativeContactPersonView.hideError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contactDetailsBinding.alternativePhoneNumberNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactDetailsBinding.alternativePhoneNumberNormalInputView.hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setPolicyClaimDetails() {
        BeneficiaryDetailObject beneficiaryDetailObject = new BeneficiaryDetailObject();
        beneficiaryDetailObject.setActualCellNo(contactDetailsBinding.phoneNumberNormalInputView.getText() == null ? "" : contactDetailsBinding.phoneNumberNormalInputView.getText());
        beneficiaryDetailObject.setEmail(contactDetailsBinding.emailAddressNormalInputView.getText() == null ? "" : contactDetailsBinding.emailAddressNormalInputView.getText());
        policyClaim.setContactNumber(contactDetailsBinding.phoneNumberNormalInputView.getText());
        policyClaim.setAlternativeContactName(contactDetailsBinding.alternativeContactPersonView.getText() == null ? "" : contactDetailsBinding.alternativeContactPersonView.getText());
        policyClaim.setAlternativeContactNumber(contactDetailsBinding.alternativePhoneNumberNormalInputView.getText() == null ? "" : contactDetailsBinding.alternativePhoneNumberNormalInputView.getText());
        policyClaim.setBeneficiaryDetail(beneficiaryDetailObject);
    }

    @Override
    public void displayContactDetails(BeneficiaryDetailObject beneficiaryDetailObject) {
        String contactNumber = beneficiaryDetailObject.getActualCellNo();
        if (contactNumber != null) {
            String formattedCellNumber = StringExtensions.getUnFormattedPhoneNumber(beneficiaryDetailObject.getActualCellNo());
            StringBuilder builder = new StringBuilder(formattedCellNumber);
            builder.insert(3, " ");
            builder.insert(7, " ");
            contactDetailsBinding.phoneNumberNormalInputView.setText(builder.toString());
            if (beneficiaryDetailObject.getEmailList() != null && !beneficiaryDetailObject.getEmailList().isEmpty()) {
                contactDetailsBinding.emailAddressNormalInputView.setText(beneficiaryDetailObject.getEmailList().get(0));
            }
        }
    }

    @Override
    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getFailureResultScreen(HomeLoanPerilsContactDetailsActivity.this, R.string.claim_error_text, R.string.try_later_text));
    }

    private boolean isValidData() {

        if (!ValidationUtils.validatePhoneNumber(contactDetailsBinding.phoneNumberNormalInputView, getString(R.string.enter_valid_number))) {
            return false;
        }

        if (!ValidationUtils.validateEmailInputBase(contactDetailsBinding.emailAddressNormalInputView, getString(R.string.invalid_mail_address))) {
            return false;
        }

        if (!TextUtils.isEmpty(contactDetailsBinding.alternativeContactPersonView.getText().trim())
                && (TextUtils.isEmpty(contactDetailsBinding.alternativePhoneNumberNormalInputView.getText().trim())
                || contactDetailsBinding.alternativeContactPersonView.getText().length() <= MINIMUM_CONTACT_NAME_LENGTH)) {
            if (contactDetailsBinding.alternativeContactPersonView.getText().length() <= MINIMUM_CONTACT_NAME_LENGTH) {
                contactDetailsBinding.alternativeContactPersonView.setError(R.string.min_contact_length);
            } else {
                contactDetailsBinding.alternativePhoneNumberNormalInputView.setError(getString(R.string.enter_valid_number));
            }
            return false;
        }

        if (!TextUtils.isEmpty(contactDetailsBinding.alternativePhoneNumberNormalInputView.getText().trim())
                && !ValidationUtils.validatePhoneNumber(contactDetailsBinding.alternativePhoneNumberNormalInputView, getString(R.string.enter_valid_number))) {
            return false;
        }

        if (!TextUtils.isEmpty(contactDetailsBinding.alternativePhoneNumberNormalInputView.getText().trim())
                && TextUtils.isEmpty(contactDetailsBinding.alternativeContactPersonView.getText().trim())) {
            contactDetailsBinding.alternativeContactPersonView.setError(getString(R.string.alternative_name_length_error));
            return false;
        }

        return true;
    }

}
