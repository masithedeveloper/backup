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

package com.barclays.absa.banking.rewards.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.databinding.RewardsPurchaseOverviewFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.utils.AccessibilityUtils;

import styleguide.content.Profile;
import styleguide.utils.ImageUtils;

public class PrepaidRewardsPurchaseOverviewFragment extends Fragment {

    private static final String PURCHASE_DETAILS_OBJECT = "purchaseDetailsObject";
    private static final String REWARDS_REDEEM_CONFIRMATION = "rewardsRedeemConfirmation";

    private RewardsPurchaseOverviewFragmentBinding binding;
    private BuyAirtimeWithAbsaRewardsView buyAirtimeView;
    private RewardsPurchaseDetailsObject detailsObject;
    private RewardsRedeemConfirmation rewardsRedeemConfirmation;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public PrepaidRewardsPurchaseOverviewFragment() {
    }

    public static PrepaidRewardsPurchaseOverviewFragment newInstance(RewardsPurchaseDetailsObject detailsObject, RewardsRedeemConfirmation rewardsRedeemConfirmation) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PURCHASE_DETAILS_OBJECT, detailsObject);
        bundle.putSerializable(REWARDS_REDEEM_CONFIRMATION, rewardsRedeemConfirmation);
        PrepaidRewardsPurchaseOverviewFragment prepaidRewardsPurchaseOverviewFragment = new PrepaidRewardsPurchaseOverviewFragment();
        prepaidRewardsPurchaseOverviewFragment.setArguments(bundle);
        return prepaidRewardsPurchaseOverviewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.rewards_purchase_overview_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buyAirtimeView = (BuyAirtimeWithAbsaRewardsView) getActivity();
        getData();
        initViews();
        setupTalkBack();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String[] accountInfo = AccessibilityUtils.getSplitAccountInfo((binding.accountTitleAndDescriptionView.getDescription()));
            final String accountName = accountInfo[0];
            final String accountNumber = accountInfo[1];
            final String mobileNumber = binding.mobileNumberTitleAndDescriptionView.getDescription();
            final String mobileNetwork = binding.netWorkOperatorTitleAndDescriptionView.getDescription();
            final String purchasePrice = binding.rewardsAvailableBeneficiaryView.getAccountDescriptionTextView().getText().toString();
            binding.rewardsAvailableBeneficiaryView.getNameTextView().setContentDescription(getString(R.string.talkback_prepaid_rewards_overview_purchase_summary, AccessibilityUtils.getTalkBackRandValueFromString(detailsObject.getRedemptionVoucherName()), (AccessibilityUtils.getTalkBackRandValueFromString(purchasePrice))));
            binding.rewardsAvailableBeneficiaryView.getAccountDescriptionTextView().setContentDescription(getString(R.string.talkback_prepaid_rewards_overview_purchase__discount, AccessibilityUtils.getTalkBackRandValueFromString(detailsObject.getRedemptionVoucherName()), (AccessibilityUtils.getTalkBackRandValueFromString(purchasePrice))));
            binding.mobileNumberTitleAndDescriptionView.setContentDescription(getString(R.string.talkback_prepaid_rewards_overview_mobile_number, mobileNumber));
            binding.netWorkOperatorTitleAndDescriptionView.setContentDescription(getString(R.string.talkback_prepaid_rewards_overview_mobile_network_operator, mobileNetwork));
            binding.accountTitleAndDescriptionView.setContentDescription(getString(R.string.talkback_prepaid_rewards_overview_account_source, accountName, accountNumber));
        }
    }

    private void getData() {
        assert getArguments() != null;
        detailsObject = (RewardsPurchaseDetailsObject) getArguments().getSerializable(PURCHASE_DETAILS_OBJECT);
        rewardsRedeemConfirmation = (RewardsRedeemConfirmation) getArguments().getSerializable(REWARDS_REDEEM_CONFIRMATION);
    }

    private void initViews() {
        populateRewardsAvailableBeneficiaryView();
        populateViews();
        binding.buyButton.setOnClickListener(v -> {
            if (appCacheService.isInNoPrimaryDeviceState()) {
                buyAirtimeView.showNoPrimaryDeviceScreen();
            } else {
                buyAirtimeView.redeemRewards(rewardsRedeemConfirmation);
            }
        });
    }

    private void populateViews() {
        if (detailsObject != null && rewardsRedeemConfirmation != null) {
            binding.mobileNumberTitleAndDescriptionView.setDescription(detailsObject.getPhoneNumber());
            binding.netWorkOperatorTitleAndDescriptionView.setDescription(detailsObject.getNetworkOperator());
            binding.accountTitleAndDescriptionView.setDescription(BMBConstants.REWARDS_ACCOUNT_TYPE + " | " + rewardsRedeemConfirmation.getFromAccountNumber());
        }
    }

    private void populateRewardsAvailableBeneficiaryView() {
        if (rewardsRedeemConfirmation != null) {
            Bitmap profileImage = ImageUtils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_rewards);
            String discountAmount = rewardsRedeemConfirmation.getAirtimeCostIncludingDiscount() != null ? rewardsRedeemConfirmation.getAirtimeCostIncludingDiscount().getCurrency() + " " + rewardsRedeemConfirmation.getAirtimeCostIncludingDiscount().getAmount() : "R 0.00";
            Profile profile = new Profile(getString(R.string.prepaid_you_are_buying_a) + "\n" + detailsObject.getRedemptionVoucherName(), discountAmount, profileImage);
            binding.rewardsAvailableBeneficiaryView.setProfile(profile);
        }
    }
}