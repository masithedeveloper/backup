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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSelectCurrentLocationFragmentBinding;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.CardPackage;
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.banking.relationshipBanking.ui.NewToBankBusinessAccountViewModel;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.SELECTED_ITEM;

public class NewToBankSelectCurrentLocationFragment extends ExtendedFragment<NewToBankSelectCurrentLocationFragmentBinding> {

    private static final int ITEM_SELECTION_REQUEST_CODE = 00004;

    private NewToBankView newToBankView;
    private boolean branchSelected;
    private List<SiteFilteredDetailsVO> allSiteFilteredDetails;
    private NewToBankBusinessAccountViewModel viewModel;

    public static NewToBankSelectCurrentLocationFragment newInstance() {
        return new NewToBankSelectCurrentLocationFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_select_current_location_fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        newToBankView = (NewToBankView) context;
        viewModel = new ViewModelProvider((NewToBankActivity) context).get(NewToBankBusinessAccountViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView.showToolbar();
        newToBankView.setToolbarBackTitle(getToolbarTitle());
        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_CurrentLocationFragment_ScreenDisplayed");
        } else {
            newToBankView.trackCurrentFragment(NewToBankConstants.PRE_REGISTRATION);
        }

        populateRadioView();
    }

    @SuppressWarnings("unchecked")
    private void populateRadioView() {
        SelectorList<StringItem> selectorList = new SelectorList<>();
        selectorList.add(new StringItem(getString(R.string.new_to_bank_not_in_branch)));
        selectorList.add(new StringItem(getString(R.string.new_to_bank_in_branch)));
        binding.branchRadioButtonView.setDataSource(selectorList);

        if (newToBankView.isStudentFlow()) {
            SelectorList<StringItem> studentSelectorList = new SelectorList<>();
            studentSelectorList.add(new StringItem(getString(R.string.new_to_bank_im_with_consultant_on_my_campus)));
            binding.consultantRadioButtonView.setDataSource(studentSelectorList);
        } else {
            binding.consultantRadioButtonView.setVisibility(View.GONE);
        }

        binding.nextButton.setOnClickListener(v -> {
            if (branchSelected) {
                InBranchInfo inBranchInfo = getSelectedBranchInfo(binding.selectBranchNormalInputView.getSubText());
                newToBankView.trackStudentAccount("StudentAccount_CurrentLocationScreen_InBranchOptionSelected");
                newToBankView.setBranchVisitedInfo(inBranchInfo);
            } else {
                newToBankView.trackStudentAccount("StudentAccount_CurrentLocationScreen_NotInBranchOptionSelected");
                newToBankView.setBranchVisitedInfo(new InBranchInfo(false, "", ""));
            }
            if (newToBankView.isStudentFlow()) {
                viewModel.getStudentAccountLiveData().observe(getViewLifecycleOwner(), cardPackageResponse -> {
                    viewModel.getStudentAccountLiveData().removeObservers(this);
                    final CardPackage cardPackage = cardPackageResponse.getCardPackage();
                    final NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
                    newToBankTempData.setSelectedPackage(cardPackage);
                    newToBankView.trackStudentAccount("StudentAccount_CurrentLocationScreen_OnCampusOptionSelected");
                    newToBankView.navigateToChooseYourProductFragment();
                });
                viewModel.fetchStudentAccountBundle();
            }

            newToBankView.fetchCifCodes();
        });

        binding.consultantRadioButtonView.setItemCheckedInterface(index -> {
            branchSelected = false;
            binding.branchRadioButtonView.clearGroupChecks();
            binding.nextButton.setEnabled(true);
            hideSelectorViews();
        });

        binding.branchRadioButtonView.setItemCheckedInterface(index -> {
            binding.consultantRadioButtonView.clearGroupChecks();

            if (index == 1 && isCitySelectorViewHidden()) {
                showSelectorViews();
                newToBankView.getAllBranches();
                if (getString(R.string.new_to_bank_select_branch).equals(binding.selectBranchNormalInputView.getSubText())) {
                    binding.nextButton.setEnabled(false);
                } else {
                    binding.nextButton.setEnabled(true);
                }
            } else if (index == 0 && !isCitySelectorViewHidden()) {
                hideSelectorViews();
                branchSelected = false;
                binding.nextButton.setEnabled(true);
            } else if (index == 0) {
                branchSelected = false;
                binding.nextButton.setEnabled(true);
            }
        });

        binding.selectBranchNormalInputView.setOnClickListener(v -> showBranchList());
        binding.selectBranchNormalInputView.setDetailsIconVisibility(View.VISIBLE);
    }

    private InBranchInfo getSelectedBranchInfo(String siteName) {
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
        allSiteFilteredDetails = null;
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
                results.add(StringExtensions.toTitleCase(branchDetails.getSiteName()));
            }
        }

        return results;
    }

    private void showSelectorViews() {
        binding.whichBranchDescriptionView.setVisibility(View.VISIBLE);
        binding.selectBranchNormalInputView.setVisibility(View.VISIBLE);
    }

    private void hideSelectorViews() {
        binding.selectBranchNormalInputView.setVisibility(View.GONE);
        binding.whichBranchDescriptionView.setVisibility(View.GONE);
    }

    private boolean isCitySelectorViewHidden() {
        return (binding.selectBranchNormalInputView.getVisibility() == View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_SELECTION_REQUEST_CODE) {
                String selectedItem = data.getStringExtra(SELECTED_ITEM);
                binding.selectBranchNormalInputView.setSubText(selectedItem);
                branchSelected = true;
                binding.nextButton.setEnabled(true);
            }
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_where_you_are);
    }
}