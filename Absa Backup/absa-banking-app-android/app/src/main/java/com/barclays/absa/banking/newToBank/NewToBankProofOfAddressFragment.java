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

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankProofOfAddressFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.Packages;
import com.barclays.absa.banking.newToBank.services.dto.ProofOfResidenceResponse;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.buttons.OptionActionButtonView;

public class NewToBankProofOfAddressFragment extends ExtendedFragment<NewToBankProofOfAddressFragmentBinding> {

    private NewToBankView newToBankView;
    private Packages currentPackage;
    private boolean isShowingMore = false;

    public static NewToBankProofOfAddressFragment newInstance() {
        return new NewToBankProofOfAddressFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_proof_of_address_fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        newToBankView = (NewToBankView) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView.setToolbarTitle(getToolbarTitle());
        newToBankView.setProgressStep(8);
        newToBankView.showProgressIndicator();
        ProofOfResidenceResponse proofOfResidenceInfo = newToBankView.getNewToBankTempData().getProofOfResidenceInfo();
        if (proofOfResidenceInfo != null) {
            currentPackage = proofOfResidenceInfo.getPackages().get(0);
        }
        showAcceptedDocuments(currentPackage, false);
        setUpClickListeners();
        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_PoRExamples_Instructions");
        } else {
            newToBankView.trackCurrentFragment(NewToBankConstants.PROOF_OF_RESIDENCE_SCREEN);
        }
    }

    private void setUpClickListeners() {
        binding.showMoreOptionActionButtonView.setOnClickListener(v -> {
            String showMoreOrLessInfo = isShowingMore ? getString(R.string.new_to_bank_show_me_less_information) : getString(R.string.new_to_bank_show_me_more);
            showAcceptedDocuments(currentPackage, isShowingMore);
            binding.showMoreOptionActionButtonView.setCaptionText(showMoreOrLessInfo);
            isShowingMore = !isShowingMore;
        });
        binding.takePhotoButton.setOnClickListener(v -> newToBankView.navigateToScanAddressFragment());
    }

    private void showAcceptedDocuments(Packages currentPackage, boolean showAll) {
        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_PoRExamples_ShowMeMore");
        } else {
            newToBankView.trackFragmentAction(NewToBankConstants.PROOF_OF_RESIDENCE_SCREEN, NewToBankConstants.PROOF_OF_RESIDENCE_SHOWMORE);
        }
        binding.documentsLinearLayout.removeAllViews();

        if (newToBankView.isStudentFlow()) {
            addPackageOptionView(getString(R.string.new_to_bank_proof_of_residence_letter_option));
        }

        for (int i = 0; i < currentPackage.getBulletPoints().size(); i++) {
            addPackageOptionView(currentPackage.getBulletPoints().get(i));
            if (i > 2 && !showAll) {
                break;
            }
        }
    }

    private void addPackageOptionView(String option) {
        OptionActionButtonView optionActionButtonView = new OptionActionButtonView(getContext());
        optionActionButtonView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        optionActionButtonView.setIcon(R.drawable.ic_tick_black_24);
        optionActionButtonView.setImageViewVisibility(View.VISIBLE);
        optionActionButtonView.setCaptionText(option);
        optionActionButtonView.hideRightArrowImage();
        optionActionButtonView.setClickable(false);
        optionActionButtonView.removeTopAndBottomMargins();
        binding.documentsLinearLayout.addView(optionActionButtonView);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_where_you_live);
    }
}
