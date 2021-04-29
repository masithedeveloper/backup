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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.FuneralCoverPolicyHeaderFragmentBinding;
import com.barclays.absa.banking.presentation.homeLoan.HomeloanPerilsClaimDetailsActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.Nullable;

import static com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity.policyType;

public class FuneralCoverPolicyHeaderFragment extends Fragment {

    FuneralCoverPolicyHeaderFragmentBinding binding;

    public FuneralCoverPolicyHeaderFragment() {
    }

    public static FuneralCoverPolicyHeaderFragment newInstance(PolicyDetail policyDetail) {
        FuneralCoverPolicyHeaderFragment coverPolicyHeaderFragment = new FuneralCoverPolicyHeaderFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL, policyDetail);
        coverPolicyHeaderFragment.setArguments(arguments);
        return coverPolicyHeaderFragment;
    }

    private boolean isSubmitClaimPolicy(String productCode) {
        switch (productCode) {
            case "HOC":
            case "BROKEHOC":
            case "ABHSTAFF":
            case "AFORDHOC":
            case "COMMUNIT":
            case "SANLMHOC":
            case "SASOLHOC":
            case "SCITRHOC":
            case "NOFRILLS":
            case "STAFFHOC":
            case "UNIBANK":
            case "VMSAHOC":
            case "ABSA HOC":
                return true;
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.funeral_cover_policy_header_fragment, container, false);
        if (getArguments() != null) {
            PolicyDetail policyDetail = (PolicyDetail) getArguments().getSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL);
            if (policyDetail != null) {
                boolean isSubmitClaimPolicy = false;
                String productCode = "";
                if (policyDetail.getPolicy() != null && policyDetail.getPolicy().getProductCode() != null) {
                    productCode = policyDetail.getPolicy().getProductCode();
                    isSubmitClaimPolicy = isSubmitClaimPolicy(productCode);
                }

                if (isSubmitClaimPolicy) {
                    binding.claimNotificationButton.setText(R.string.txt_submit_claim);
                }

                if ("ABCO".equalsIgnoreCase(productCode) || "FAOL".equalsIgnoreCase(productCode)) {
                    binding.claimNotificationButton.setVisibility(View.GONE);
                } else if (isSubmitClaimPolicy) {
                    binding.claimNotificationButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), HomeloanPerilsClaimDetailsActivity.class);
                        intent.putExtra(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL, policyDetail);
                        intent.putExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, true);
                        startActivity(intent);
                    });
                } else {
                    binding.claimNotificationButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), FuneralCoverClaimNotificationActivity.class);
                        intent.putExtra(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL, policyDetail);
                        startActivity(intent);
                        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_HubScreen_ReportIncidentButtonClicked");
                    });
                }
            } else {
                startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            PolicyDetail policyDetail = (PolicyDetail) getArguments().getSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL);
            if (policyDetail != null) {
                if (policyDetail.getPolicy() != null) {
                    binding.balanceTextView.setText(TextFormatUtils.formatBasicAmount(policyDetail.getPolicy().getCoverAmount()));
                }
            }
        }
    }
}
