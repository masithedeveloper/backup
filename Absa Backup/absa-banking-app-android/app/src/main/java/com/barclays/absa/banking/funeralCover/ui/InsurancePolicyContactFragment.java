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

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.FuneralCoverPolicyContactFragmentBinding;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;
import com.barclays.absa.banking.presentation.shared.IntentFactory;

import com.barclays.absa.banking.shared.ItemPagerFragment;

import org.jetbrains.annotations.NotNull;

import com.barclays.absa.utils.AnalyticsUtil;

import styleguide.content.Contact;

public class InsurancePolicyContactFragment extends ItemPagerFragment implements View.OnClickListener {

    public static final String POLICY_TYPE = "policyType";
    public static final String CONTACT_ITEM = "contactItem";
    private FuneralCoverPolicyContactFragmentBinding binding;
    private ContactItem contactItem;
    private PolicyType policyType;

    public InsurancePolicyContactFragment() {
    }

    public static InsurancePolicyContactFragment newInstance(String description, ContactItem contactItem, PolicyType policyType) {
        InsurancePolicyContactFragment insurancePolicyContactFragment = new InsurancePolicyContactFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        arguments.putSerializable(POLICY_TYPE, policyType);
        arguments.putSerializable(CONTACT_ITEM, contactItem);
        insurancePolicyContactFragment.setArguments(arguments);
        return insurancePolicyContactFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            contactItem = (ContactItem) arguments.getSerializable(CONTACT_ITEM);
            policyType = (PolicyType) arguments.getSerializable(POLICY_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.funeral_cover_policy_contact_fragment, container, false);

        if (contactItem != null) {
            showShortTermPolicyContactUs(contactItem);
        } else {
            startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
        }

        binding.callBackContactView.setOnClickListener(this);
        binding.contactCentreContactView.setOnClickListener(this);
        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ContactScreen_ScreenDisplayed");
        return binding.getRoot();
    }

    private void showShortTermPolicyContactUs(ContactItem contactItem) {
        if (policyType == PolicyType.SHORT_TERM) {
            binding.callBackContactView.setVisibility(View.GONE);
            binding.titleDividerView.setVisibility(View.GONE);
        }
        if (contactItem.getCallBackContactItem() != null) {
            binding.callBackContactView.setContact(contactItem.getCallBackContactItem());
        }
        if (contactItem.getCallCentreContactItem() != null) {
            binding.contactCentreContactView.setContact(contactItem.getCallCentreContactItem());
        }
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        return getArguments().getString(Companion.getTAB_DESCRIPTION_KEY());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callBackContactView:
                InsurancePolicyClaimsBaseActivity callingActivity = (InsurancePolicyClaimsBaseActivity) getActivity();
                if (callingActivity != null) {
                    new InsurancePolicyInteractor().requestCustomerDetails(callingActivity.beneficiaryDetailListener);
                } else {
                    startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
                }
                AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ContactScreen_CallYouBackButtonClicked");
                break;
            case R.id.contactCentreContactView:
                Contact centreContactItem = contactItem.getCallCentreContactItem();
                if (centreContactItem != null) {
                    TelephoneUtil.call(getActivity(), centreContactItem.getContactName());
                }
                AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", policyType.value + "_ContactScreen_CallButtonClicked");
                break;
        }
    }
}
