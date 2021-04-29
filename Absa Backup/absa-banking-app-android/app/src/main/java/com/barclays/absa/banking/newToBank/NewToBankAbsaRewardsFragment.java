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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankAbsaRewardsFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.Packages;
import com.barclays.absa.banking.newToBank.services.dto.PerformPhotoMatchAndMobileLookupDTO;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.TextFormatUtils;
import styleguide.buttons.OptionActionButtonView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

import java.util.ArrayList;
import java.util.Locale;

public class NewToBankAbsaRewardsFragment extends ExtendedFragment<NewToBankAbsaRewardsFragmentBinding> {

    private static final int MAX_DAYS = 31;
    private NewToBankView newToBankView;

    public NewToBankAbsaRewardsFragment() {

    }

    public static NewToBankAbsaRewardsFragment newInstance() {
        return new NewToBankAbsaRewardsFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_absa_rewards_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.showProgressIndicator();
            newToBankView.setToolbarTitle(getToolbarTitle());
            newToBankView.trackCurrentFragment(NewToBankConstants.REWARDS_OFFER);
        }

        setUpDebitDaySelector();
        initViews();
        showRewardStores();
        showRewardRewardPartnerInfo();
    }

    private void initViews() {
        newToBankView.showToolbar();

        String rewardsAmount = TextFormatUtils.formatBasicAmountAsRand(newToBankView.getNewToBankTempData().getRewardsAmount());
        binding.rewardsSpecialOffersCheckBoxView.setChecked(false);
        binding.cashBackTitleAndDescriptionView.setTitle(newToBankView.getNewToBankTempData().getRewardsInfo().getDescription3());
        binding.cashBackTitleAndDescriptionView.setDescription(getCashbackDescription());
        binding.termsAndConditionsDescriptionView.setDescription(newToBankView.getNewToBankTempData().getRewardsInfo().getPackages().get(0).getAsterix());
        binding.rewardsDescriptionView.setDescription(getRewardsDescription());
        binding.rewardsSpecialOffersCheckBoxView.setDescription(newToBankView.getNewToBankTempData().getRewardsInfo().getDescription5());
        binding.whatYouEarnDescriptionView.setDescription(newToBankView.getNewToBankTempData().getRewardsInfo().getDescription7());
        binding.makeOfferDescriptionView.setDescription(getOfferDescription());
        binding.debitDayNormalInputView.setTitleTextStyle(R.style.LargeTextRegularDark);
        binding.debitDayNormalInputView.setTitleText(String.format("%s %s %s", newToBankView.getNewToBankTempData().getRewardsInfo().getDescription8(), rewardsAmount, newToBankView.getNewToBankTempData().getRewardsInfo().getDescription9()));

        binding.nextButton.setOnClickListener(view1 -> {
            if (binding.rewardsSpecialOffersCheckBoxView.getIsValid()) {
                newToBankView.saveRewardsData(binding.debitDayNormalInputView.getSelectedValue(), true);
            } else {
                newToBankView.saveRewardsData("", false);
            }
        });

        binding.rewardsSpecialOffersCheckBoxView.setOnCheckedListener(isChecked -> {
            if (isChecked) {
                binding.debitDayNormalInputView.setVisibility(View.VISIBLE);
                binding.nextButton.setEnabled(false);
            } else {
                binding.debitDayNormalInputView.setVisibility(View.GONE);
                binding.debitDayNormalInputView.clear();
                binding.nextButton.setEnabled(true);
            }
        });

        binding.showMoreOptionActionButtonView.setOnClickListener(v -> {
            if (rewardsInformationShowing()) {
                hideRewardsInformation();
            } else {
                showRewardsInformation();
            }
        });
    }

    private boolean rewardsInformationShowing() {
        return binding.rewardsDescriptionView.getVisibility() == View.VISIBLE;
    }

    private void hideRewardsInformation() {
        binding.showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_show_me_more_information));
        binding.showMoreOptionActionButtonView.setIcon(R.drawable.ic_add_dark);
        binding.rewardsPartnersLinearLayout.setVisibility(View.GONE);
        binding.rewardsDescriptionView.setVisibility(View.GONE);
        binding.whatYouEarnDescriptionView.setVisibility(View.GONE);
        binding.termsAndConditionsDescriptionView.setVisibility(View.GONE);
    }

    private void showRewardsInformation() {
        newToBankView.trackFragmentAction(NewToBankConstants.REWARDS_OFFER, NewToBankConstants.REWARDS_SHOW_MORE);
        binding.showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_show_me_less_information));
        binding.showMoreOptionActionButtonView.setIcon(R.drawable.ic_dash_dark);
        binding.rewardsDescriptionView.setVisibility(View.VISIBLE);
        binding.rewardsPartnersLinearLayout.setVisibility(View.VISIBLE);
        binding.whatYouEarnDescriptionView.setVisibility(View.VISIBLE);
        binding.termsAndConditionsDescriptionView.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    private void setUpDebitDaySelector() {
        SelectorList<StringItem> debitDayList = new SelectorList<>();
        for (int i = 1; i <= MAX_DAYS; i++) {
            debitDayList.add(new StringItem(String.valueOf(i)));
        }
        binding.debitDayNormalInputView.setList(debitDayList, getString(R.string.new_to_bank_debit_day));
        binding.debitDayNormalInputView.setItemSelectionInterface(index -> validateInput());
    }

    private String getCashbackDescription() {
        return String.format(Locale.ENGLISH, "%s\n%s", newToBankView.getNewToBankTempData().getRewardsInfo().getDescription4(), newToBankView.getNewToBankTempData().getRewardsInfo().getPackages().get(0).getAccountPackage());
    }

    private String getRewardsDescription() {
        return String.format(Locale.ENGLISH, "%s\n%s", newToBankView.getNewToBankTempData().getRewardsInfo().getDescription6(), newToBankView.getNewToBankTempData().getRewardsInfo().getDescription7());
    }

    private String getOfferDescription() {
        PerformPhotoMatchAndMobileLookupDTO photoMatchAndMobileLookupResponse = newToBankView.getNewToBankTempData().getPhotoMatchAndMobileLookupResponse();
        if (photoMatchAndMobileLookupResponse != null && photoMatchAndMobileLookupResponse.getNameAndSurname() != null) {
            String customerFullName = StringExtensions.toTitleCaseRemovingCommas(photoMatchAndMobileLookupResponse.getNameAndSurname());
            String customerFirstName = customerFullName.split(" ")[0];
            return String.format(Locale.ENGLISH, "%s %s%s", newToBankView.getNewToBankTempData().getRewardsInfo().getDescription1(), customerFirstName, newToBankView.getNewToBankTempData().getRewardsInfo().getDescription2());
        }
        return "";
    }

    private void showRewardStores() {
        binding.rewardStoresLinearLayout.removeAllViews();

        ArrayList<Packages> packages = newToBankView.getNewToBankTempData().getRewardsInfo().getPackages();
        if (packages.size() > 0) {
            for (int i = 0; i < packages.get(0).getBulletPoints().size(); i++) {
                OptionActionButtonView optionActionButtonView = new OptionActionButtonView(getContext());
                optionActionButtonView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                optionActionButtonView.removeTopAndBottomMargins();
                optionActionButtonView.setIcon(R.drawable.ic_tick_black_24);
                optionActionButtonView.setImageViewVisibility(View.VISIBLE);
                String formattedRewardStores = packages.get(0).getBulletPoints().get(i).replace(",", "");
                optionActionButtonView.setCaptionText(formattedRewardStores);
                optionActionButtonView.hideRightArrowImage();

                binding.rewardStoresLinearLayout.addView(optionActionButtonView);
            }
        }
    }

    private void showRewardRewardPartnerInfo() {
        binding.rewardsPartnersLinearLayout.removeAllViews();

        ArrayList<Packages> packages = newToBankView.getNewToBankTempData().getRewardsInfo().getPackages();
        if (packages.size() > 1) {
            for (int i = 0; i < packages.get(1).getBulletPoints().size(); i++) {

                OptionActionButtonView optionActionButtonView = new OptionActionButtonView(getContext());
                optionActionButtonView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                optionActionButtonView.removeTopAndBottomMargins();
                optionActionButtonView.setIcon(R.drawable.ic_tick_black_24);
                optionActionButtonView.setImageViewVisibility(View.VISIBLE);
                String formattedRewardPartner = packages.get(1).getBulletPoints().get(i);
                optionActionButtonView.setCaptionText(formattedRewardPartner);
                optionActionButtonView.hideRightArrowImage();
                binding.rewardsPartnersLinearLayout.addView(optionActionButtonView);
            }
        }
    }

    private void validateInput() {
        if (binding.rewardsSpecialOffersCheckBoxView.getIsValid() && binding.debitDayNormalInputView.getSelectedIndex() > -1) {
            binding.nextButton.setEnabled(true);
        } else {
            binding.nextButton.setEnabled(false);
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_absa_rewards);
    }
}