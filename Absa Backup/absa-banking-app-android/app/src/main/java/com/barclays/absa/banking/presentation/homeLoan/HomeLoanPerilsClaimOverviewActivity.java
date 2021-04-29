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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AllPerils;
import com.barclays.absa.banking.boundary.model.PolicyClaim;
import com.barclays.absa.banking.databinding.HomeLoanPerilsClaimOverviewActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity;
import com.barclays.absa.banking.home.ui.IHomeCacheService;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.DateUtils;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity.ACCOUNT_OBJECT;

public class HomeLoanPerilsClaimOverviewActivity extends BaseActivity implements HomeLoanPerilsClaimSubmitClaimView {
    HomeLoanPerilsClaimOverviewActivityBinding perilsClaimOverviewActivityBinding;
    private HomeLoanPerilsClaimSubmitClaimPresenter submitClaimPresenter;
    private PolicyClaim policyClaim;
    private boolean isFromPropertyInsurance;
    private final IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        perilsClaimOverviewActivityBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.home_loan_perils_claim_overview_activity, null, false);
        setContentView(perilsClaimOverviewActivityBinding.getRoot());

        setToolBarBack(R.string.claim_confirmation);
        policyClaim = (PolicyClaim) getIntent().getSerializableExtra(HomeLoanPerilsContactDetailsActivity.POLICY_CLAIM_DETAILS);
        isFromPropertyInsurance = getIntent().getBooleanExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, false);
        if (policyClaim != null) {
            displayPolicyClaimOverView(policyClaim);
        }
        submitClaimPresenter = new HomeLoanPerilsClaimSubmitClaimPresenter(this);
        perilsClaimOverviewActivityBinding.claimOverviewContinueButton.setOnClickListener(v -> submitClaimPresenter.submitPerilClaim(policyClaim, BMBConstants.ALPHABET_N));
    }

    private void displayPolicyClaimOverView(@NonNull PolicyClaim policyClaim) {
        perilsClaimOverviewActivityBinding.policyTypeView.setContentText(policyClaim.getPolicyDetail().getPolicy() != null ? policyClaim.getPolicyDetail().getPolicy().getDescription() : "");
        perilsClaimOverviewActivityBinding.incidentDateView.setContentText(DateUtils.formatDate(policyClaim.getIncidentDate(), "yyyy-MM-dd", "dd MMM yyyy"));
        perilsClaimOverviewActivityBinding.propertyAddressView.setContentText(policyClaim.getPropertyAddress());
        perilsClaimOverviewActivityBinding.claimTypeView.setContentText(policyClaim.getTypeOfClaim());
        perilsClaimOverviewActivityBinding.itemAffectedStatusView.setContentText(policyClaim.getItemAffected());
        if (getString(R.string.no).equalsIgnoreCase(policyClaim.getAdditionalDamage())) {
            perilsClaimOverviewActivityBinding.additionalDamagesView.setContentText(getString(R.string.no));
        } else {
            perilsClaimOverviewActivityBinding.additionalDamagesView.setContentText(policyClaim.getAdditionalDamagedItems());
        }
        perilsClaimOverviewActivityBinding.contactNumberView.setContentText(policyClaim.getBeneficiaryDetail().getActualCellNo());
        perilsClaimOverviewActivityBinding.emailAddressView.setContentText(policyClaim.getBeneficiaryDetail().getEmail());
        if (!policyClaim.getAlternativeContactName().isEmpty() && !policyClaim.getAlternativeContactNumber().isEmpty()) {
            perilsClaimOverviewActivityBinding.alternativeContactPersonNumberView.setContentText(policyClaim.getAlternativeContactNumber());
            perilsClaimOverviewActivityBinding.alternativeContactPersonView.setContentText(policyClaim.getAlternativeContactName());
        } else {
            perilsClaimOverviewActivityBinding.alternativeContactPersonNumberView.setVisibility(View.GONE);
            perilsClaimOverviewActivityBinding.alternativeContactPersonView.setVisibility(View.GONE);
        }
    }

    @Override
    public void launchFailureScreen() {
        GenericResultActivity.topOnClickListener = v1 -> loadAccountsAndGoHome();
        Intent intent = new Intent(HomeLoanPerilsClaimOverviewActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.claim_not_registered_text);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, getString(R.string.claim_contact_support_text));
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.done);
        startActivity(intent);
    }

    @Override
    public void launchSuccessScreen(AllPerils allPerils) {
        String claimNumber = allPerils.getPolicyClaim() != null ? allPerils.getPolicyClaim().getClaimNumber() : "";
        String successMessage = String.format("%s %s\n\n%s", getString(R.string.claim_number_msg), claimNumber, getString(R.string.claim_what_now));
        GenericResultActivity.topOnClickListener = v1 -> loadAccountsAndGoHome();
        Intent intent = new Intent(HomeLoanPerilsClaimOverviewActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.claim_registered_text);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, successMessage);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.done);
        startActivity(intent);
    }

    @Override
    public void showDialog(AllPerils allPerils) {
        dismissProgressDialog();
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.duplicate_claim_warning_title))
                .message(getString(R.string.duplicate_claim_warning_msg))
                .positiveDismissListener((dialog, which) -> {
                    submitClaimPresenter.submitPerilClaim(policyClaim, BMBConstants.ALPHABET_Y);
                })
                .negativeDismissListener((dialog, which) -> {
                    Intent resultIntent;
                    if (isFromPropertyInsurance) {
                        resultIntent = new Intent(HomeLoanPerilsClaimOverviewActivity.this, InsurancePolicyClaimsBaseActivity.class);
                    } else {
                        resultIntent = new Intent(HomeLoanPerilsClaimOverviewActivity.this, HomeLoanPerilsHubActivity.class);
                        resultIntent.putExtra(ACCOUNT_OBJECT, homeCacheService.getSelectedHomeLoanAccount());
                    }
                    resultIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(resultIntent);
                }));
    }

    @Override
    public void launchSomethingWentWrongScreen() {
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.claim_error_text, R.string.try_later_text));
    }
}
