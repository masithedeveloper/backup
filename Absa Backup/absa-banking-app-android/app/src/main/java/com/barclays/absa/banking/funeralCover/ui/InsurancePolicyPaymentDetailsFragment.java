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

package com.barclays.absa.banking.funeralCover.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountInfo;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.FragmentInsurancePolicyPaymentDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity.policyType;

public class InsurancePolicyPaymentDetailsFragment extends InsurancePolicyBaseFragment<FragmentInsurancePolicyPaymentDetailsBinding> {

    public static final String MONTHLY_FREQUENCY = "12";
    public static final String YEARLY_FREQUENCY = "1";

    public InsurancePolicyPaymentDetailsFragment() {
    }

    public static InsurancePolicyPaymentDetailsFragment newInstance() {
        return new InsurancePolicyPaymentDetailsFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_insurance_policy_payment_details;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.editPaymentDetailsView.setOnClickListener(view1 -> {
            AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ManagePaymentDetailsScreen_EditPaymentDetailsButtonClicked");
            navigateToEditPolicyPaymentDetailsFragment();
        });
        setToolBar(getString(R.string.manage_payment_details), view1 -> getActivity().finish());
        getBaseActivity().getProgressIndicatorView().setVisibility(View.GONE);
        PolicyDetail policyDetail = getAppCacheService().getPolicyDetail();
        if (policyDetail != null) {
            populateBankDetails(policyDetail);
        }
        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ManagePaymentDetailsScreen_ScreenDisplayed");
    }

    private void populateBankDetails(PolicyDetail policyDetail) {
        AccountInfo accountInfo = policyDetail.getAccountInfo();
        if (accountInfo != null) {
            binding.bankNameView.setContentText(StringExtensions.toTitleCase(accountInfo.getBankName()));
            binding.accountNumberView.setContentText(accountInfo.getAccountNumber());
            binding.accountTypeView.setContentText(getBaseActivity().getAccountDescription().get(accountInfo.getAccountType()));
            binding.branchCodeView.setContentText(accountInfo.getBranchCode());
            binding.branchNameView.setContentText(accountInfo.getBranchName());
            binding.nextPremiumDeductionView.setContentText(DateUtils.getDateWithMonthNameFromHyphenatedString(accountInfo.getNextPremiumDate()));
            binding.premiumFrequencyView.setContentText(getPolicyPremiumFrequency(accountInfo.getPremiumFrequency()));
            binding.annualIncreaseView.setContentText(accountInfo.getAnnualPremiumIncreaseRate());
        }
    }

    private void navigateToEditPolicyPaymentDetailsFragment() {
        getBaseActivity().startFragment(EditPolicyPaymentDetailsFragment.newInstance(), true, BaseActivity.AnimationType.FADE);
    }

    private String getPolicyPremiumFrequency(String frequency) {
        String premiumFrequency = "";
        if (MONTHLY_FREQUENCY.equalsIgnoreCase(frequency) || "Monthly".equalsIgnoreCase(frequency)) {
            premiumFrequency = getString(R.string.monthly);
        } else if (YEARLY_FREQUENCY.equalsIgnoreCase(frequency) || getString(R.string.yearly).equalsIgnoreCase(frequency)) {
            premiumFrequency = getString(R.string.yearly);
        }
        return premiumFrequency;
    }
}