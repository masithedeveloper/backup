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

package com.barclays.absa.banking.funeralCover.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.notification.SubmitClaim;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.databinding.FuneralCoverClaimConfirmationActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;

import styleguide.utils.extensions.StringExtensions;

public class FuneralCoverClaimConfirmationActivity extends BaseActivity {

    private FuneralCoverClaimConfirmationActivityBinding binding;
    private String callCentreNumber;
    InsurancePolicyClaimViewModel insurancePolicyClaimViewModel = new InsurancePolicyClaimViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_claim_confirmation_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.confirm_details);

        Intent intent = getIntent();
        Policy policy = (Policy) intent.getSerializableExtra(BMBConstants.POLICY_KEY);
        attachObservers();

        if (("ST").equalsIgnoreCase(policy.getType())) {
            if (("ABSA IDIRECT").equalsIgnoreCase(policy.getDescription())) {
                callCentreNumber = getString(R.string.short_term_absa_i_direct_contact);
            } else {
                callCentreNumber = getString(R.string.short_term_hoc_contact);
            }
        } else {
            callCentreNumber = getString(R.string.life_policy_contact);
        }

        PolicyClaimItem policyClaimItem = (PolicyClaimItem) intent.getSerializableExtra(FuneralCoverClaimNotificationActivity.POLICY_CLAIM_ITEM);
        if (policyClaimItem != null) {
            displayPolicyClaimOverView(policyClaimItem);
        } else {
            startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message));
        }

        binding.confirmButton.setOnClickListener(v -> {
            if (policyClaimItem != null) {

                final String incidentDateInEnglish = intent.getStringExtra(FuneralCoverClaimNotificationActivity.INCIDENT_DATE_IN_ENGLISH);
                if (incidentDateInEnglish != null) {
                    policyClaimItem.setIncidentDate(incidentDateInEnglish.toString());
                }
                insurancePolicyClaimViewModel.requestPolicyClaimNotification(policyClaimItem);
            } else {
                startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message));
            }
        });
    }

    private void displayPolicyClaimOverView(@NonNull PolicyClaimItem policyClaimItem) {
        binding.descriptionOfIncidentContentAndLabelView.setContentText(policyClaimItem.getClaimDescription());
        binding.incidentDateContentAndLabelView.setContentText(policyClaimItem.getIncidentDate());
        binding.phoneNumberContentAndLabelView.setContentText(StringExtensions.toFormattedCellphoneNumber(policyClaimItem.getContactNumber()));
        binding.typeOfClaimContentAndLabelView.setContentText(policyClaimItem.getClaimType());
    }

    public void showSuccessScreen(SubmitClaim submitClaim) {
        String claimNotificationSuccessMessage = getString(R.string.report_incident_success_screen_description, callCentreNumber);
        startActivity(IntentFactory.getClaimNotificationSuccessfulScreen(this, claimNotificationSuccessMessage));
    }

    public void showFailureScreen() {
        String claimNotificationFailureMessage = getString(R.string.report_incident_failure_screen_description, callCentreNumber);
        startActivity(IntentFactory.getClaimNotificationFailureScreen(this, claimNotificationFailureMessage));
    }

    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.generic_error));
    }

    public void attachObservers() {
        insurancePolicyClaimViewModel.getClaimNotificationLiveData().observe(this, claimNotification -> {
            if (claimNotification != null && claimNotification.getTransactionReferenceId() != null && !claimNotification.getTransactionReferenceId().isEmpty()) {
                String reference = claimNotification.getTransactionReferenceId();
                insurancePolicyClaimViewModel.submitInsurancePolicyClaim(reference);
            } else {
                showSomethingWentWrongScreen();
                dismissProgressDialog();
            }
        });

        insurancePolicyClaimViewModel.getSubmitClaimLiveData().observe(this, submitClaim -> {
            if (submitClaim != null && submitClaim.isClaimSuccessful()) {
                showSuccessScreen(submitClaim);
            } else {
                showFailureScreen();
            }
            dismissProgressDialog();
        });

        insurancePolicyClaimViewModel.getFailureResponse().observe(this, transactionResponse -> {
            showSomethingWentWrongScreen();
            dismissProgressDialog();
        });
    }
}
