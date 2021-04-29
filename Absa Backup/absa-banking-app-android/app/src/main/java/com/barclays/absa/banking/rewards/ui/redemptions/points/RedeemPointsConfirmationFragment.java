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

package com.barclays.absa.banking.rewards.ui.redemptions.points;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.databinding.RedeemPointsConfirmationFragmentBinding;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.rewards.services.dto.RewardsRedeemConfirmRequest;

import styleguide.utils.extensions.StringExtensions;

public class RedeemPointsConfirmationFragment extends Fragment {

    private static final String USER_INPUT = "user input";

    private RedeemPointsConfirmationFragmentBinding binding;
    private RedeemShoppingPointsView shoppingPointsView;
    private RedeemPointsInputFields userInput;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public RedeemPointsConfirmationFragment() {
    }

    public static RedeemPointsConfirmationFragment newInstance(RedeemPointsInputFields userInput) {
        Bundle bundle = new Bundle();
        RedeemPointsConfirmationFragment confirmationFragment = new RedeemPointsConfirmationFragment();
        bundle.putSerializable(USER_INPUT, userInput);
        confirmationFragment.setArguments(bundle);
        return confirmationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.redeem_points_confirmation_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingPointsView = (RedeemShoppingPointsView) getActivity();
        initViews();
        populateFields();
    }

    private void populateFields() {
        assert getArguments() != null;
        userInput = (RedeemPointsInputFields) getArguments().getSerializable(USER_INPUT);

        binding.amountTitleAndDescriptionView.setTitle(userInput.getAmount());
        binding.cardNumberTitleAndDescriptionView.setTitle(userInput.getCardNumber());
        binding.cellNumberDescriptionView.setTitle(StringExtensions.toFormattedCellphoneNumber(userInput.getCellphoneNumber()));
        binding.retailPartnerTitleAndDescriptionView.setTitle(userInput.getPartnerName());

        AccountObject accountObject = rewardsCacheService.getRewardsAccount();
        if (accountObject != null) {
            binding.rewardsAccountDescriptionView.setTitle(getString(R.string.rewards_account_value,
                    accountObject.getDescription(),
                    StringExtensions.toFormattedAccountNumber(accountObject.getAccountNumber())));
        }
    }

    private void initViews() {
        binding.convertPointsButton.setOnClickListener(v -> shoppingPointsView.convertRewards(bundleUserInput()));
    }

    private Bundle bundleUserInput() {
        Bundle bundle = new Bundle();
        bundle.putString(TransactionParams.Transaction.REDEEM_TYPE.getKey(), RewardsRedeemConfirmRequest.REDEEM_AS_PARTNER);
        if (userInput.getAmount() != null) {
            final String value = StringExtensions.removeCurrency(userInput.getAmount());
            bundle.putString(TransactionParams.Transaction.REDEMPTION_AMOUNT.getKey(), value);
        }
        bundle.putString(TransactionParams.Transaction.REDEMPTION_PARTNER.getKey(), userInput.getPartnerName());
        bundle.putString(TransactionParams.Transaction.REDEMPTION_ID.getKey(), userInput.getPartnerID());
        bundle.putString(TransactionParams.Transaction.SERVICE_CELL_NUMBER.getKey(), userInput.getCellphoneNumber());
        bundle.putString(TransactionParams.Transaction.CARD_NUMBER.getKey(), userInput.getCardNumber() != null ? userInput.getCardNumber().replaceAll(" ", "") : "");
        return bundle;
    }
}
