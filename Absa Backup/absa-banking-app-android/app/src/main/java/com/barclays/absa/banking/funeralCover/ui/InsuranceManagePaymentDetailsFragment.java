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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.FragmentInsuranceManagePaymentDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.policy_beneficiaries.ui.ManageBeneficiaryActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AnalyticsUtil;

import org.jetbrains.annotations.NotNull;

import static com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity.policyType;

public class InsuranceManagePaymentDetailsFragment extends ItemPagerFragment {

    private FragmentInsuranceManagePaymentDetailsBinding binding;

    public InsuranceManagePaymentDetailsFragment() {
    }

    public static InsuranceManagePaymentDetailsFragment newInstance(String description) {
        InsuranceManagePaymentDetailsFragment fragment = new InsuranceManagePaymentDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_insurance_manage_payment_details, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        if (featureSwitchingToggles.getManageInsurancePolicy() == FeatureSwitchingStates.GONE.getKey()) {
            binding.managePolicyPaymentDetailsView.setVisibility(View.GONE);
        }

        binding.managePolicyPaymentDetailsView.setOnClickListener(view1 -> {
            if (policyType != null) {
                AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ManageScreen_ManagePaymentDetailsButtonClicked");
            }
            BaseActivity.preventDoubleClick(view1);
            navigate(InsuranceManagePaymentDetailsActivity.class);
        });

        binding.manageBeneficiariesDetailsView.setOnClickListener(view2 -> {
            if (policyType != null) {
                AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ManageScreen_ManageBeneficiariesButtonClicked");
            }
            BaseActivity.preventDoubleClick(view2);
            navigate(ManageBeneficiaryActivity.class);
        });
        if (policyType != null) {
            AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ManageScreen_ScreenDisplayed");
        }
    }

    private void navigate(Class className) {
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getManageInsurancePolicy() == FeatureSwitchingStates.DISABLED.getKey()) {
            startActivity(IntentFactory.capabilityUnavailable(getActivity(), R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_change_payment_details))));
        } else {
            startActivity(new Intent(getActivity(), className));
        }
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getString(Companion.getTAB_DESCRIPTION_KEY());
        }
        return "";
    }
}
