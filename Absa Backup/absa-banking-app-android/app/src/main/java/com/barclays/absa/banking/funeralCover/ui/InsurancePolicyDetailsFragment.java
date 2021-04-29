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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyComponent;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.FuneralCoverPolicyDetailsFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import styleguide.utils.extensions.StringExtensions;

import static android.view.View.GONE;

public class InsurancePolicyDetailsFragment extends ItemPagerFragment {

    FuneralCoverPolicyDetailsFragmentBinding binding;

    public InsurancePolicyDetailsFragment() {
    }

    public static InsurancePolicyDetailsFragment newInstance(String description, PolicyDetail policyDetail) {
        InsurancePolicyDetailsFragment insurancePolicyDetailsFragment = new InsurancePolicyDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        arguments.putSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL, policyDetail);
        insurancePolicyDetailsFragment.setArguments(arguments);
        return insurancePolicyDetailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.funeral_cover_policy_details_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PolicyDetail policyDetail = (PolicyDetail) getArguments().getSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL);
        String policyType = null;
        if (policyDetail != null) {
            Policy policy = policyDetail.getPolicy();
            if (policy != null) {
                binding.monthlyPremiumView.setTitle(TextFormatUtils.formatBasicAmount(policy.getMonthlyPremium()));
                binding.accountNumberView.setLineItemViewContent(StringExtensions.toFormattedAccountNumber(policy.getNumber()));
                binding.statusView.setLineItemViewContent(policy.getStatus());
                binding.endDateView.setLineItemViewContent(DateUtils.getDateWithMonthNameFromHyphenatedString(policy.getRenewalDate()));
                policyType = policy.getType();
                if (("LI").equalsIgnoreCase(policy.getType()) || BMBConstants.EXERGY_POLICY_TYPE.equalsIgnoreCase(policy.getType())) {
                    binding.endDateView.setVisibility(GONE);
                }
            } else {
                startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
            }
            if (policyDetail.getAccountInfo() != null) {
                if ("12".equalsIgnoreCase(policyDetail.getAccountInfo().getPremiumFrequency()) || "Monthly".equalsIgnoreCase(policyDetail.getAccountInfo().getPremiumFrequency())) {
                    binding.premiumFrequencyView.setLineItemViewContent(getString(R.string.monthly));
                } else if ("1".equalsIgnoreCase(policyDetail.getAccountInfo().getPremiumFrequency()) || "Annual".equalsIgnoreCase(policyDetail.getAccountInfo().getPremiumFrequency())) {
                    binding.premiumFrequencyView.setLineItemViewContent(getString(R.string.annually));
                } else {
                    binding.premiumFrequencyView.setLineItemViewContent(StringExtensions.toSentenceCase(policyDetail.getAccountInfo().getPremiumFrequency()));
                }
            }
            binding.startDateView.setLineItemViewContent(DateUtils.getDateWithMonthNameFromHyphenatedString(policyDetail.getInceptionDate()));
            binding.serviceFeeView.setLabelText(policyDetail.getPolicyFee().toString());
            if (policyType != null) {
                populatePolicyBreakDownData(policyDetail);
            }

        } else {
            startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
        }
        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", "InsuranceHub_PolicyDetailScreen_ScreenDisplayed");
    }

    private void populatePolicyBreakDownData(PolicyDetail policyDetail) {
        Policy policy = policyDetail.getPolicy();
        List<PolicyComponent> policyComponents = BMBConstants.LONG_TERM_POLICY_TYPE.equalsIgnoreCase(policy.getType()) || BMBConstants.EXERGY_POLICY_TYPE.equalsIgnoreCase(policy.getType()) ? policyDetail.getLongTermPolicyComponents() : policyDetail.getShortTermPolicyComponents();
        if (policyComponents != null && !policyComponents.isEmpty()) {
            InsurancePolicyComponentAdapter insurancePolicyComponentAdapter = new InsurancePolicyComponentAdapter(policyComponents);
            binding.policyComponentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.policyComponentsRecyclerView.setAdapter(insurancePolicyComponentAdapter);
        } else {
            binding.policyComponentsRecyclerView.setVisibility(GONE);
        }
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        return getArguments().getString(Companion.getTAB_DESCRIPTION_KEY());
    }
}
