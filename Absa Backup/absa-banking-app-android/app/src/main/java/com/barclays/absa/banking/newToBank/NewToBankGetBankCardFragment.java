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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankGetBankCardFragmentBinding;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.utils.extensions.StringExtensions;

public class NewToBankGetBankCardFragment extends ExtendedFragment<NewToBankGetBankCardFragmentBinding> {

    private NewToBankView newToBankView;
    private String visitedBranch = "";

    public NewToBankGetBankCardFragment() {
    }

    public static NewToBankGetBankCardFragment newInstance() {
        return new NewToBankGetBankCardFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_get_bank_card_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarBackTitle(getToolbarTitle());
        if (newToBankView.isBusinessFlow()) {
            newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_LandingScreen_ScreenDisplayed");
        } else if (newToBankView.isStudentFlow()) {
            newToBankView.trackSoleProprietorCurrentFragment("StudentAccount_LandingScreen_ScreenDisplayed");
        } else {
            newToBankView.trackCurrentFragment(NewToBankConstants.BANKING_NEXT_STEPS);
        }
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        newToBankView.showToolbar();
    }

    private void initViews() {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        InBranchInfo inBranchInfo = newToBankTempData.getInBranchInfo();
        visitedBranch = StringExtensions.toTitleCase(inBranchInfo.getInBranchName());

        binding.getCardButton.setOnClickListener(v -> {
            if (newToBankView.isBusinessFlow()) {
                newToBankView.navigateToGetBusinessBankingCardFragment();
            } else if (!inBranchInfo.getInBranchIndicator() && TextUtils.isEmpty(visitedBranch)) {
                newToBankView.navigateToSelectPreferredBranchFragment();
            } else {
                newToBankView.navigateToChooseBankCardFragment();
            }
        });
    }

    @Override
    public String getToolbarTitle() {
        return "";
    }
}
