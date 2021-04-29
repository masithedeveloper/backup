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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankNumberConfirmationFragmentBinding;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.utils.extensions.StringExtensions;

public class NewToBankNumberConfirmationFragment extends ExtendedFragment<NewToBankNumberConfirmationFragmentBinding> {

    private NewToBankView newToBankView;

    public NewToBankNumberConfirmationFragment() {
        // Required empty public constructor
    }

    public static NewToBankNumberConfirmationFragment newInstance() {
        return new NewToBankNumberConfirmationFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_number_confirmation_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarBackTitle(getToolbarTitle());
        newToBankView.showProgressIndicator();

        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_SureCheckPreparationScreen_ScreenDisplayed");
        }

        setupComponentListeners();
    }

    private void setupComponentListeners() {
        String cellphoneNumber = newToBankView.getNewToBankTempData().getCustomerDetails().getCellphoneNumber();
        String cellNumberFormatted = StringExtensions.toFormattedCellphoneNumber(cellphoneNumber);
        binding.ntbCellphoneNumberSureCheckTextView.setText(cellNumberFormatted);
        binding.ntbNumberConfirmationNextButton.setOnClickListener(v -> {
            if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_DisplayVerifyRequestScreen_ScreenDisplayed");
            }
            newToBankView.performSecurityNotification(cellphoneNumber);
        });
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_surecheck_keeping_you_secure);
    }
}
