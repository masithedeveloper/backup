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

package com.barclays.absa.banking.newToBank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankRegisteredForOnlineBankingFragmentBinding;
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.newToBank.dto.RegistrationDetails;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.utils.extensions.StringExtensions;

public class NewToBankRegisteredForOnlineBankingFragment extends ExtendedFragment<NewToBankRegisteredForOnlineBankingFragmentBinding> {

    private NewToBankView newToBankView;

    public NewToBankRegisteredForOnlineBankingFragment() {
        // Required empty public constructor
    }

    public static NewToBankRegisteredForOnlineBankingFragment newInstance() {
        return new NewToBankRegisteredForOnlineBankingFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_registered_for_online_banking_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarTitle(getToolbarTitle());
        newToBankView.showToolbar();

        setupComponentListeners();
    }

    private void setupComponentListeners() {

        RegistrationDetails registrationDetails = newToBankView.getNewToBankTempData().getRegistrationDetails();

        binding.accountNumberView.setContentText(StringExtensions.toFormattedAccountNumber(registrationDetails.getAccountNumber()));
        binding.surephraseView.setContentText(registrationDetails.getSurePhrase());
        binding.userNumberView.setContentText("1");

        binding.loginButton.setOnClickListener(v -> {
            getAppCacheService().setShouldRevertToOldLinkingFlow(true);
            Intent intent = new Intent(getActivity(), AccountLoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(AccountLoginActivity.ACCESS_ACCOUNT_KEY, registrationDetails.getAccountNumber());
            bundle.putString(AccountLoginActivity.ACCESS_PIN_KEY, registrationDetails.getPIN());
            bundle.putString(AccountLoginActivity.USER_NO_KEY, "1");
            bundle.putString(AccountLoginActivity.SOURCE_ACTIVITY, BMBConstants.REGISTER_CONST);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_your_digital_profile);
    }
}