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
import com.barclays.absa.banking.databinding.NewToBankChooseBankCardFragmentBinding;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

public class NewToBankChooseBankCardFragment extends ExtendedFragment<NewToBankChooseBankCardFragmentBinding> {

    private final String CARD_DELIVERY_METHOD = "BRANCH";
    private NewToBankView newToBankView;
    private String visitedBranch;

    public NewToBankChooseBankCardFragment() {

    }

    public static NewToBankChooseBankCardFragment newInstance() {
        return new NewToBankChooseBankCardFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_choose_bank_card_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getFragmentManager(), getToolbarTitle());
        newToBankView = (NewToBankView) getActivity();

        if (newToBankView != null) {
            if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_BranchCardSelection_ScreenDisplayed");
            } else {
                newToBankView.trackCurrentFragment(NewToBankConstants.BANKING_CARD_SELECTION_IN_BRANCH);
            }
        }
        initViews();
        populateViews();
    }

    private void initViews() {
        binding.standardCardRadioButtonView.setItemCheckedInterface(index -> {
            binding.orderCardRadioButtonView.clearSelection();
            hideBranchCollectionSection();
        });

        binding.orderCardRadioButtonView.setItemCheckedInterface(index -> {
            binding.standardCardRadioButtonView.clearSelection();
            showBranchCollectionSection();
        });

        binding.selectBranchRadioButtonView.setItemCheckedInterface(index -> {
            binding.selectBranchRadioButtonView.hideError();
            if (index == 1) {
                newToBankView.getAllBranches();
            }
        });

        binding.nextButton.setOnClickListener(v -> {
            if (binding.selectBranchRadioButtonView.getVisibility() != View.VISIBLE) {
                newToBankView.createCombiCardAccount(getDeliveryCombiDetails(false));
                return;
            }

            if ((binding.selectBranchRadioButtonView.getSelectedIndex() == 1)) {
                newToBankView.navigateToSelectPreferredBranchFragment();
            } else if (!TextUtils.isEmpty(visitedBranch)) {
                newToBankView.createCombiCardAccount(getDeliveryCombiDetails(true));
            } else {
                binding.selectBranchRadioButtonView.setErrorMessage(getString(R.string.new_to_bank_please_select_branch));
            }
        });
    }

    private CreateCombiDetails getDeliveryCombiDetails(boolean personalised) {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        CreateCombiDetails createCombiDetails = new CreateCombiDetails();

        if (newToBankTempData != null && newToBankTempData.getInBranchInfo() != null) {
            InBranchInfo inBranchInfo = newToBankTempData.getInBranchInfo();
            createCombiDetails.setDeliveryBranch(inBranchInfo.getInBranchCode());
            createCombiDetails.setDeliveryMethod(CARD_DELIVERY_METHOD);
            createCombiDetails.setPersonalised(personalised);
        }

        return createCombiDetails;
    }

    private void populateViews() {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        if (newToBankTempData != null) {
            visitedBranch = StringExtensions.toTitleCase(newToBankView.getNewToBankTempData().getInBranchInfo().getInBranchName());
        }

        binding.goodNewsTitleAndDescriptionView.setDescription(getString(R.string.new_to_bank_get_card_now, visitedBranch));
        populateRadioView();
    }

    @SuppressWarnings("unchecked")
    private void populateRadioView() {
        SelectorList<StringItem> standardCardSelectorList = new SelectorList<>();
        standardCardSelectorList.add(new StringItem(getString(R.string.new_to_bank_get_standard_card)));

        SelectorList<StringItem> orderCardSelectorList = new SelectorList<>();
        orderCardSelectorList.add(new StringItem(getString(R.string.new_to_bank_order_personalised_card)));

        SelectorList<StringItem> selectBranchSelectorList = new SelectorList<>();
        selectBranchSelectorList.add(new StringItem(getString(R.string.new_to_bank_branch_name, visitedBranch)));
        selectBranchSelectorList.add(new StringItem(getString(R.string.new_to_bank_a_different_branch)));

        binding.standardCardRadioButtonView.setDataSource(standardCardSelectorList);
        binding.orderCardRadioButtonView.setDataSource(orderCardSelectorList);
        binding.selectBranchRadioButtonView.setDataSource(selectBranchSelectorList);
    }

    private void hideBranchCollectionSection() {
        binding.selectBranchRadioButtonView.setVisibility(View.GONE);
        binding.whereToCollectCardHeadingView.setVisibility(View.GONE);
    }

    private void showBranchCollectionSection() {
        binding.selectBranchRadioButtonView.setVisibility(View.VISIBLE);
        binding.whereToCollectCardHeadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_get_my_bank_card);
    }
}