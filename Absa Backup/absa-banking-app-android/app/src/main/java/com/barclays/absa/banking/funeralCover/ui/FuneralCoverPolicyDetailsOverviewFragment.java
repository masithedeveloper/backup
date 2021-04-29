/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.databinding.FuneralCoverPolicyDetailsOverviewFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.ultimateProtector.services.dto.BeneficiaryInfo;
import com.barclays.absa.banking.ultimateProtector.ui.UltimateProtectorViewModel;
import com.barclays.absa.utils.DateUtils;

import styleguide.utils.extensions.StringExtensions;

public class FuneralCoverPolicyDetailsOverviewFragment extends AbsaBaseFragment<FuneralCoverPolicyDetailsOverviewFragmentBinding> {
    private static final String POLICY_OVERVIEW_SCREEN_NAME = "Policy overview and quotation";
    private UltimateProtectorViewModel ultimateProtectorViewModel;
    private FuneralCoverActivity activity;

    public static FuneralCoverPolicyDetailsOverviewFragment newInstance() {
        return new FuneralCoverPolicyDetailsOverviewFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.funeral_cover_policy_details_overview_fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FuneralCoverActivity) context;
        ultimateProtectorViewModel = new ViewModelProvider(activity).get(UltimateProtectorViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolBar(R.string.policy_confirmation);
        activity.setStep(3);
        displayPolicyInformation();
        binding.overviewContinueButton.setOnClickListener(v -> {
            activity.startFragment(FuneralCoverQuotationFragment.newInstance(), true, BaseActivity.AnimationType.SLIDE);
        });
        AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_Screen3_PolicyOverview", POLICY_OVERVIEW_SCREEN_NAME, BMBConstants.TRUE_CONST);
    }

    private void displayPolicyInformation() {
        FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
        String memberName = "";
        if (activity.getBeneficiarySelected()) {
            BeneficiaryInfo beneficiaryInfo = ultimateProtectorViewModel.getUltimateProtectorInfo().getBeneficiaryInfo();
            if (beneficiaryInfo != null) {
                funeralCoverDetails.setBeneficiaryInfo(beneficiaryInfo);
                String relationship = "";
                if (beneficiaryInfo.getRelationship() != null) {
                    relationship = beneficiaryInfo.getRelationship().getDescription();
                }

                memberName = String.format(BMBApplication.getApplicationLocale(), "%s %s (%s)", beneficiaryInfo.getFirstName(), beneficiaryInfo.getSurname(), relationship);
            }
        } else {
            memberName = String.format(BMBApplication.getApplicationLocale(), "%s %s", getString(R.string.estate_late), CustomerProfileObject.getInstance().getCustomerName());
        }

        binding.mainMemberNamePrimaryContentAndLabelView.setContentText(memberName);
        binding.debitDaySecondaryContentAndLabelView.setContentText(funeralCoverDetails.getDebitDate());
        binding.accountToDebitSecondaryContentAndLabelView.setContentText(String.format(BMBApplication.getApplicationLocale(), "%s - %s", StringExtensions.toTitleCaseRemovingCommas(funeralCoverDetails.getAccountDescription()), (funeralCoverDetails.getAccountNumber())));
        binding.yearlyIncreaseSecondaryContentAndLabelView.setContentText("true".equalsIgnoreCase(funeralCoverDetails.getYearlyIncrease()) ? getString(R.string.yes) : getString(R.string.no));
        binding.policyStartDateSecondaryContentAndLabelView.setContentText(DateUtils.formatDate(funeralCoverDetails.getPolicyStartDate(), "dd/MM/yyyy", "dd MMM yyyy"));
        String sourceOfFunds = funeralCoverDetails.getSourceOfFunds();
        if (!TextUtils.isEmpty(sourceOfFunds)) {
            binding.sourceOfFundsSecondaryContentAndLabelView.setContentText(sourceOfFunds.substring(3));
        }

        String employmentStatus = funeralCoverDetails.getEmploymentStatus();
        if (!TextUtils.isEmpty(employmentStatus)) {
            binding.employmentStatusSecondaryContentAndLabelView.setContentText(StringExtensions.toTitleCase(employmentStatus));
        }

        String occupation = funeralCoverDetails.getOccupation();
        if (!TextUtils.isEmpty(occupation)) {
            binding.occupationSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
            binding.occupationSecondaryContentAndLabelView.setContentText(StringExtensions.toTitleCase(occupation));
        }
    }
}
