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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.databinding.OverdraftMarketingConsentFragmentBinding;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse;

import java.lang.ref.WeakReference;

import styleguide.forms.OnCheckedListener;

public class OverdraftMarketingConsentFragment extends OverdraftBaseFragment<OverdraftMarketingConsentFragmentBinding> implements OverdraftContracts.MarketingConsentView, OnCheckedListener, View.OnClickListener {
    private static final String OVERDRAFT_SET_UP_OBJECT = "overdraftSetUp";
    private static final String OVERDRAFT_MARKETING_OBJECT = "overdraftMarketing";
    private OverdraftContracts.MarketingConsentPresenter presenter;
    private OverdraftSetup overdraftSetup;
    private PersonalInformationResponse overdraftMarketingMethods;
    private boolean checkMarketingConsent;

    public static OverdraftMarketingConsentFragment newInstance(OverdraftSetup overdraftSetup, PersonalInformationResponse overdraftMarketingResponse) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OVERDRAFT_SET_UP_OBJECT, overdraftSetup);
        bundle.putSerializable(OVERDRAFT_MARKETING_OBJECT, overdraftMarketingResponse);
        OverdraftMarketingConsentFragment fragment = new OverdraftMarketingConsentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getParentActivity().setStep(4);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_marketing_consent_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getString(R.string.overdraft_marketing_consent_title), v -> getParentActivity().onBackPressed());

        presenter = new OverdraftMarketingConsentPresenter(new WeakReference<>(this));
        if (getArguments() != null) {
            overdraftSetup = (OverdraftSetup) getArguments().getSerializable(OVERDRAFT_SET_UP_OBJECT);
            overdraftMarketingMethods = (PersonalInformationResponse) getArguments().getSerializable(OVERDRAFT_MARKETING_OBJECT);
        }
        binding.nextButton.setOnClickListener(this);
        binding.marketingConsentCheckBox.setOnCheckedListener(this);
        setCommunicationChannel();
    }

    @Override
    public void navigateToOverdraftSetupConfirmationScreen() {
        if ((checkMarketingConsent) &&
                ((binding.postCheckbox.isChecked() && binding.postCheckbox.getVisibility() == View.VISIBLE)
                        || (binding.smsCheckbox.isChecked() && binding.smsCheckbox.getVisibility() == View.VISIBLE)
                        || (binding.emailCheckbox.isChecked() && binding.emailCheckbox.getVisibility() == View.VISIBLE))) {
            show(OverdraftCheckSetupAndConfirmFragment.newInstance(overdraftSetup));
        } else if (checkMarketingConsent) {
            if (binding.postCheckbox.getVisibility() == View.VISIBLE) {
                binding.postCheckbox.setErrorMessage(getString(R.string.overdraft_please_select_communication_method));
                animate(binding.marketingCommunicationLayout, R.anim.shake);
            } else if (binding.emailCheckbox.getVisibility() == View.VISIBLE) {
                binding.emailCheckbox.setErrorMessage(getString(R.string.overdraft_please_select_communication_method));
                animate(binding.marketingCommunicationLayout, R.anim.shake);
            } else if (binding.smsCheckbox.getVisibility() == View.VISIBLE) {
                binding.smsCheckbox.setErrorMessage(getString(R.string.overdraft_please_select_communication_method));
                animate(binding.marketingCommunicationLayout, R.anim.shake);
            }
        }

        if (!checkMarketingConsent) {
            show(OverdraftCheckSetupAndConfirmFragment.newInstance(overdraftSetup));
        }
    }

    public void showMarketingConsentChannelOptions() {
        binding.marketingConsentChannelView.setVisibility(View.VISIBLE);
        binding.marketingCommunicationLayout.setVisibility(View.VISIBLE);
        if (overdraftMarketingMethods.getEmailMarketing() != null) {
            binding.emailCheckbox.setVisibility(View.VISIBLE);
        }
        if (overdraftMarketingMethods.getSmsMarketing() != null) {
            binding.smsCheckbox.setVisibility(View.VISIBLE);
        }
        if (overdraftMarketingMethods.getMailMarketing() != null) {
            binding.postCheckbox.setVisibility(View.VISIBLE);
        }
    }

    public void hideMarketingConsentChannelOptions() {
        binding.marketingConsentChannelView.setVisibility(View.GONE);
        binding.marketingCommunicationLayout.setVisibility(View.GONE);
        binding.smsCheckbox.setChecked(false);
        binding.emailCheckbox.setChecked(false);
        binding.postCheckbox.setChecked(false);
    }

    private void setCommunicationChannel() {
        binding.smsCheckbox.setOnCheckedListener(isChecked -> {
            clearCheckBoxError();
            overdraftSetup.setSmsChannel(isChecked ? "Y" : "N");
        });

        binding.postCheckbox.setOnCheckedListener(isChecked -> {
            clearCheckBoxError();
            overdraftSetup.setDirectMailChannel(isChecked ? "Y" : "N");
        });

        binding.emailCheckbox.setOnCheckedListener(isChecked -> {
            clearCheckBoxError();
            overdraftSetup.setEmailChannel(isChecked ? "Y" : "N");
        });
    }

    private void clearCheckBoxError() {
        binding.smsCheckbox.clearError();
        binding.postCheckbox.clearError();
        binding.emailCheckbox.clearError();
    }

    @Override
    public void onChecked(boolean isChecked) {
        if (isChecked) {
            overdraftSetup.setMarketingConsent("Y");
            checkMarketingConsent = true;
            presenter.marketingConsentChecked();
        } else {
            presenter.marketingConsentNotChecked();
            checkMarketingConsent = false;
            overdraftSetup.setMarketingConsent("N");
        }
    }

    @Override
    public void onClick(View v) {
        presenter.onNextButtonClicked();
    }
}
