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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSelectPreferredBranchFragmentBinding;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails;
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import java.util.ArrayList;
import java.util.List;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.SELECTED_ITEM;

public class NewToBankSelectPreferredBranchFragment extends ExtendedFragment<NewToBankSelectPreferredBranchFragmentBinding> {

    private static final int ITEM_SELECTION_REQUEST_CODE = 00004;
    private final String CARD_DELIVERY_METHOD = "BRANCH";

    private NewToBankView newToBankView;
    private List<SiteFilteredDetailsVO> allSiteFilteredDetails;

    public NewToBankSelectPreferredBranchFragment() {

    }

    public static NewToBankSelectPreferredBranchFragment newInstance() {
        return new NewToBankSelectPreferredBranchFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_select_preferred_branch_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        setToolBar(getFragmentManager(), getToolbarTitle());
        newToBankView.trackCurrentFragment(NewToBankConstants.BANKING_CARD_ORDER_BRANCH_SELECTION);

        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_SelectBranch_ScreenDisplayed");
        }

        initViews();
    }

    private void initViews() {
        binding.selectBranchNormalInputView.setOnClickListener(v -> showBranchList());

        binding.nextButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.selectBranchNormalInputView.getSubText())) {
                binding.selectBranchNormalInputView.setError(getString(R.string.new_to_bank_select_branch));
            } else {
                InBranchInfo inBranchInfo = getSelectedBranchInfo(binding.selectBranchNormalInputView.getSubText());
                newToBankView.createCombiCardAccount(getDeliveryCombiDetails(inBranchInfo));
            }
        });
    }

    private CreateCombiDetails getDeliveryCombiDetails(InBranchInfo inBranchInfo) {
        CreateCombiDetails createCombiDetails = new CreateCombiDetails();

        if (inBranchInfo != null) {
            createCombiDetails.setDeliveryBranch(inBranchInfo.getInBranchCode());
            createCombiDetails.setDeliveryMethod(CARD_DELIVERY_METHOD);
            createCombiDetails.setPersonalised(true);
        }

        return createCombiDetails;
    }

    private InBranchInfo getSelectedBranchInfo(String siteName) {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        allSiteFilteredDetails = null;
        if (newToBankTempData != null) {
            allSiteFilteredDetails = newToBankView.getNewToBankTempData().getListOfBranches();
        }

        InBranchInfo inBranchInfo = null;
        if (allSiteFilteredDetails != null && siteName != null) {
            for (SiteFilteredDetailsVO branchSitedDetails : allSiteFilteredDetails) {
                if (branchSitedDetails.getSiteName() != null && branchSitedDetails.getSiteCode() != null) {
                    if (siteName.equals(StringExtensions.toTitleCase(branchSitedDetails.getSiteName()))) {
                        inBranchInfo = new InBranchInfo();
                        inBranchInfo.setInBranchIndicator(true);
                        inBranchInfo.setInBranchName(branchSitedDetails.getSiteName());
                        inBranchInfo.setInBranchCode(branchSitedDetails.getSiteCode());
                        break;
                    }
                }
            }
        }
        return inBranchInfo;
    }

    private void showBranchList() {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        List<SiteFilteredDetailsVO> allSiteFilteredDetails = null;
        if (newToBankTempData != null) {
            allSiteFilteredDetails = newToBankView.getNewToBankTempData().getListOfBranches();
        }
        Intent intent = new Intent(getActivity(), ShowBranchListActivity.class);
        intent.putExtra(NewToBankConstants.RETRIEVED_BRANCHES, getAllSiteNames(allSiteFilteredDetails));
        startActivityForResult(intent, ITEM_SELECTION_REQUEST_CODE);
    }

    private ArrayList<String> getAllSiteNames(List<SiteFilteredDetailsVO> allSiteFilteredDetails) {
        ArrayList<String> results = new ArrayList<>();

        if (allSiteFilteredDetails != null) {
            for (SiteFilteredDetailsVO branchDetails : allSiteFilteredDetails) {
                if (branchDetails.getSiteName() != null) {
                    results.add(StringExtensions.toTitleCase(branchDetails.getSiteName()));
                }
            }
        }

        return results;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_SELECTION_REQUEST_CODE) {
                String selectedItem = data.getStringExtra(SELECTED_ITEM);
                binding.selectBranchNormalInputView.setSubText(selectedItem);
                binding.nextButton.setEnabled(true);
            }
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_get_my_bank_card);
    }
}