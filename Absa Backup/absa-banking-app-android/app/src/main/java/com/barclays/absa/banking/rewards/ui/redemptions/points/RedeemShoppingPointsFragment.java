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

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.RedeemShoppingPointsFragmentBinding;
import com.barclays.absa.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

public class RedeemShoppingPointsFragment extends Fragment {

    private RedeemShoppingPointsFragmentBinding binding;
    private RedeemShoppingPointsView shoppingPointsView;

    public RedeemShoppingPointsFragment() {
    }

    public static RedeemShoppingPointsFragment newInstance() {
        return new RedeemShoppingPointsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.redeem_shopping_points_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingPointsView = (RedeemShoppingPointsView) getActivity();
        initViews();

        CommonUtils.makeTextClickable(getContext(), R.string.partner_membership_or_account_balance, getString(R.string.absa_rewards), new ClickableSpan() {
            @Override
            public void onClick(@NotNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www." + getString(R.string.absa_rewards)));
                startActivity(browserIntent);
            }
        }, binding.shoppingPointsTitleAndDescriptionView.getDescriptionTextView());
    }

    private void initViews() {
        binding.okButton.setOnClickListener(v -> {
            shoppingPointsView.retrieveRewards();
        });
    }
}