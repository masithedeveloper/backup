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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankConfirmIdentityFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.newToBank.services.dto.PerformAddressLookup;
import com.barclays.absa.banking.newToBank.services.dto.PerformPhotoMatchAndMobileLookupDTO;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.forms.SelectorList;
import styleguide.utils.extensions.StringExtensions;

public class NewToBankConfirmIdentityFragment extends ExtendedFragment<NewToBankConfirmIdentityFragmentBinding> implements NewToBankConfirmIdentityView {

    private NewToBankView newToBankView;
    private NewToBankConfirmIdentityPresenter presenter;

    public NewToBankConfirmIdentityFragment() {
    }

    public static NewToBankConfirmIdentityFragment newInstance() {
        return new NewToBankConfirmIdentityFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_confirm_identity_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.showToolbar();
            newToBankView.showProgressIndicator();
            newToBankView.trackCurrentFragment(NewToBankConstants.CONFIRM_IDENTITY_SCREEN);
        }

        presenter = new NewToBankConfirmIdentityPresenter(this);

        initViews();
    }

    private void initViews() {
        PerformPhotoMatchAndMobileLookupDTO photoMatchAndMobileLookupResponse = newToBankView.getNewToBankTempData().getPhotoMatchAndMobileLookupResponse();

        setNationalityList();

        binding.fullNamePrimaryContentAndLabelView.setContentText(newToBankView.getNewToBankTempData().getCustomerDetails().getFullName());
        binding.countryOfBirthPrimaryContentAndLabelView.setContentText(StringExtensions.toTitleCaseRemovingCommas(photoMatchAndMobileLookupResponse.getCountryOfBirth()));
        binding.genderPrimaryContentAndLabelView.setContentText(photoMatchAndMobileLookupResponse.getGender());
        binding.idNumberPrimaryContentAndLabelView.setContentText(StringExtensions.toFormattedIdNumber(photoMatchAndMobileLookupResponse.getIdNumber()));

        binding.yesContinueButton.setOnClickListener(v -> {
            newToBankView.trackFragmentAction(NewToBankConstants.CONFIRM_IDENTITY_SCREEN, NewToBankConstants.CONFIRM_IDENTITY_ACTION_CONFIRM_YES);
            CodesLookupDetailsSelector newToBankNationality = (CodesLookupDetailsSelector) binding.nationalityTypeNormalInputView.getSelectedItem();
            if (newToBankNationality != null) {
                newToBankView.getNewToBankTempData().getCustomerDetails().setNationalityCode(newToBankNationality.getItemCode());
            }
            presenter.performAddressLookup();
        });

        binding.incorrectButton.setOnClickListener(v -> {
            newToBankView.trackFragmentAction(NewToBankConstants.CONFIRM_IDENTITY_SCREEN, NewToBankConstants.CONFIRM_IDENTITY_ACTION_CONFIRM_NO);
            newToBankView.trackCurrentFragment(NewToBankConstants.CONFIRM_IDENTITY_ERROR_SCREEN);
            newToBankView.navigateToGenericResultFragment(getString(R.string.new_to_bank_confirm_id_error),
                    ResultAnimations.generalFailure, getString(R.string.new_to_bank_generic_header_unable_to_continue),
                    false, getString(R.string.close), v1 -> newToBankView.navigateToWelcomeActivity(), true);
        });
    }

    @SuppressWarnings("unchecked")
    private void setNationalityList() {
        SelectorList<CodesLookupDetailsSelector> newToBankNationalities = newToBankView.getNewToBankTempData().getNationalityList();
        binding.nationalityTypeNormalInputView.setList(newToBankNationalities, getString(R.string.new_to_bank_nationality));

        String idNumber = newToBankView.getNewToBankTempData().getCustomerDetails().getIdNumber();
        if (idNumber != null && idNumber.charAt(10) == '0') {
            int index = searchSouthAfricanIndex(newToBankNationalities);
            if (index > 0) {
                binding.nationalityTypeNormalInputView.setOnClickListener(null);
                binding.nationalityTypeNormalInputView.setSelectedIndex(index);
            }
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_verify_your_identity);
    }

    @Override
    public void navigateToConfirmAddressFragment(PerformAddressLookup addressLookup) {
        newToBankView.navigateToConfirmAddressFragment(addressLookup);
    }

    @Override
    public void navigateToGenericResultFragment(boolean retainState, boolean isConnectionError, String description, String animation) {
        newToBankView.navigateToGenericResultFragment(retainState, isConnectionError, description, animation);
    }

    private int searchSouthAfricanIndex(SelectorList<CodesLookupDetailsSelector> nationalityList) {
        for (int i = 0; i < nationalityList.size(); i++) {
            if (nationalityList.get(i).getEngCodeDescription().equalsIgnoreCase("South African")) {
                return i;
            }
        }
        return -1;
    }
}