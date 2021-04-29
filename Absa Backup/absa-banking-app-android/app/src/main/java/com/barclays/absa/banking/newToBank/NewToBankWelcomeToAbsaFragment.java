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

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankWelcomeToAbsaFragmentBinding;
import com.barclays.absa.banking.framework.app.ScreenshotHelper;
import com.barclays.absa.banking.framework.utils.AlertInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.DateUtils;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ISLAMIC_ACCOUNT;

public class NewToBankWelcomeToAbsaFragment extends ExtendedFragment<NewToBankWelcomeToAbsaFragmentBinding> {
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String SHOW_DETAILS_DESCRIPTION = "show details description";
    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private NewToBankView newToBankView;
    private boolean showDetailsDescription;
    private boolean hasSavedImage;

    public NewToBankWelcomeToAbsaFragment() {

    }

    public static NewToBankWelcomeToAbsaFragment newInstance(boolean showDetailsDescription) {
        Bundle bundle = new Bundle();
        NewToBankWelcomeToAbsaFragment newToBankWelcomeToAbsaFragment = new NewToBankWelcomeToAbsaFragment();
        bundle.putBoolean(SHOW_DETAILS_DESCRIPTION, showDetailsDescription);
        newToBankWelcomeToAbsaFragment.setArguments(bundle);

        return newToBankWelcomeToAbsaFragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_welcome_to_absa_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (getArguments() != null && newToBankView != null) {
            showDetailsDescription = getArguments().getBoolean(SHOW_DETAILS_DESCRIPTION);
            if (newToBankView.isBusinessFlow()) {
                newToBankView.trackSoleProprietorCurrentFragment(newToBankView.getNewToBankTempData().getSelectedBusinessEvolveProduct().getAnalyticTag());
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_WelcomeToABSAScreen_ScreenDisplayed");
            } else if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_WelcomeToABSAScreen_ScreenDisplayed");
            } else {
                newToBankView.trackCurrentFragment(NewToBankConstants.SIGNUP_RESULT_SUCCESSFUL);
            }
        }

        hideToolBar();
        initViews();
        populateViews();
    }

    private void initViews() {
        binding.completeApplicationButton.setOnClickListener(v -> {
            if (hasSavedImage) {
                newToBankView.navigateToGetBankCardFragment();
            } else {
                AlertInfo alert = new AlertInfo();
                alert.setTitle(getString(R.string.new_to_bank_save_your_account_details));
                alert.setMessage(getString(R.string.new_to_bank_save_screenshot));
                alert.setPositiveButtonText(getString(R.string.save));
                alert.setNegativeButtonText(getString(R.string.no_thanks));
                alert.setPositiveDismissListener((dialog, which) -> saveImage());
                alert.setNegativeDismissListener((dialog, which) -> newToBankView.navigateToGetBankCardFragment());

                showCustomAlertDialog(alert);
            }
        });

        binding.exportImageView.setOnClickListener(v -> saveImage());

        binding.saveAccountDetailsButton.setOnClickListener(v -> saveImage());
    }

    private void saveImage() {
        hasSavedImage = true;
        if (hasStoragePermissions()) {
            Bitmap bitmap = ScreenshotHelper.INSTANCE.screenShot(binding.scrollView, true);
            ScreenshotHelper.INSTANCE.saveImage(bitmap);
        }
    }

    private void populateViews() {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
        String customerName = "";
        String accountNumber = "";
        if (newToBankTempData != null) {
            if (newToBankTempData.getPhotoMatchAndMobileLookupResponse() != null && newToBankTempData.getPhotoMatchAndMobileLookupResponse().getNameAndSurname() != null) {
                customerName = StringExtensions.toTitleCaseRemovingCommas(newToBankTempData.getPhotoMatchAndMobileLookupResponse().getNameAndSurname().toLowerCase().split(" ")[0]);
            }
            accountNumber = StringExtensions.toFormattedAccountNumber(newToBankTempData.getRegistrationDetails().getAccountNumber());
        }
        binding.welcomeHeadingView.setTitle(getString(R.string.new_to_bank_welcome_to_absa, customerName));
        binding.branchCodeLineItemView.setLineItemViewContent(NewToBankConstants.ABSA_BRANCH_CODE);
        binding.dateTimeLineItemView.setLineItemViewContent(String.format("%s, %s", DateUtils.getTodaysDate(DATE_FORMAT), DateUtils.getCurrentTime()));
        binding.accountNumberLineItemView.setLineItemViewContent(accountNumber);
        if (showDetailsDescription) {
            if (newToBankView.isBusinessFlow()) {
                binding.completeApplicationButton.setText(getString(R.string.relationship_banking_close));
                binding.accountDetailsDescriptionView.setDescription(getString(R.string.relationship_banking_checking_your_details));
                binding.completeApplicationButton.setOnClickListener(v -> {
                    getActivity().finish();
                });
            } else {
                binding.accountDetailsDescriptionView.setDescription(getString(R.string.new_to_bank_currently_checking_your_details));
                binding.businessHoursOnlyDescriptionView.setVisibility(View.VISIBLE);
            }
        } else if (!showDetailsDescription && newToBankView.isBusinessFlow()) {
            binding.accountDetailsDescriptionView.setDescription(getString(R.string.relationship_banking_account_details_description));
        }
    }

    @Override
    public String getToolbarTitle() {
        return null;
    }

    public boolean hasStoragePermissions() {
        int permission = -1;
        if (getActivity() != null) {
            permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == REQUEST_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (newToBankView.isStudentFlow()) {
                    newToBankView.trackStudentAccount("StudentAccount_WelcomeToABSAScreen_AllowButtonClicked");
                } else {
                    newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_WelcomeToABSAScreen_AllowButtonClicked");
                }
                ScreenshotHelper.INSTANCE.saveImage(ScreenshotHelper.INSTANCE.screenShot(binding.scrollView, true));
            }
        }
    }
}