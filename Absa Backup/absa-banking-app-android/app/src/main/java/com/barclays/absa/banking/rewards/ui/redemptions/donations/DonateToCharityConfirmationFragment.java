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

package com.barclays.absa.banking.rewards.ui.redemptions.donations;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult;
import com.barclays.absa.banking.databinding.RedeemRewardsDonateToCharityConfirmFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsActivity;
import com.barclays.absa.banking.rewards.ui.redemptions.RedeemRewardsContract;
import com.barclays.absa.utils.AccountBalanceUpdateHelper;

import org.jetbrains.annotations.NotNull;

import styleguide.screens.GenericResultScreenFragment;
import styleguide.screens.GenericResultScreenProperties;
import styleguide.utils.extensions.StringExtensions;

public class DonateToCharityConfirmationFragment extends AbsaBaseFragment<RedeemRewardsDonateToCharityConfirmFragmentBinding>
        implements RedeemRewardsContract.DonateToCharityConfirmationView {
    private DonateToCharityConfirmationPresenter presenter;
    private static final String REDEEM_TYPE = "RWTCHA";
    private static final String DONATE_TO_DATA_MODEL = "donateToDataObject";
    private RewardsRedeemConfirmation rewardsRedeemConfirmation;
    private SureCheckDelegate sureCheckDelegate;
    private DonateToCharityDataModel donateToCharityDataModel;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public DonateToCharityConfirmationFragment() {
        sureCheckDelegate = new SureCheckDelegate(getBaseActivity()) {
            @Override
            public void onSureCheckProcessed() {
                new Handler(Looper.getMainLooper()).postDelayed(() -> presenter.redeemRewardsRequest(rewardsRedeemConfirmation), 250);
            }

            @Override
            public void onSureCheckCancelled() {
                launchFailureScreen();
            }

            @Override
            public void onSureCheckFailed() {
                launchFailureScreen();
            }
        };
    }

    public static DonateToCharityConfirmationFragment newInstance(DonateToCharityDataModel donateToCharityDataModel) {
        Bundle args = new Bundle();
        DonateToCharityConfirmationFragment fragment = new DonateToCharityConfirmationFragment();
        args.putSerializable(DONATE_TO_DATA_MODEL, donateToCharityDataModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.redeem_rewards_donate_to_charity_confirm_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            donateToCharityDataModel = (DonateToCharityDataModel) getArguments().getSerializable(DONATE_TO_DATA_MODEL);
        }

        binding.charityToDonateTo.setLabelText(getString(R.string.redeem_rewards_charity));
        binding.amountToDonate.setLabelText(getString(R.string.redeem_rewards_amount));
        setToolBar(getFragmentManager(), getString(R.string.donate_to_charity_toolbar_confirm));
        presenter = new DonateToCharityConfirmationPresenter(this, sureCheckDelegate);

        rewardsRedeemConfirmation = new RewardsRedeemConfirmation();
        rewardsRedeemConfirmation.setAmount(donateToCharityDataModel.getDonationAmount());
        rewardsRedeemConfirmation.setCharityName(donateToCharityDataModel.getCharityToDonateTo());
        rewardsRedeemConfirmation.setRedemptionId(Integer.parseInt(donateToCharityDataModel.getCharityId()));
        rewardsRedeemConfirmation.setReedemptionCode(REDEEM_TYPE);
        binding.charityToDonateTo.setContentText(donateToCharityDataModel.getCharityToDonateTo());
        binding.accountNumber.setContentText(getString(R.string.account_formatting_rewards, rewardsCacheService.getRewardsAccount().getDescription(), StringExtensions.toFormattedAccountNumber(donateToCharityDataModel.getAccountNumber())));
        binding.amountToDonate.setContentText(rewardsRedeemConfirmation.getAmount() != null ? rewardsRedeemConfirmation.getAmount().toString() : "0.00");
        binding.donateButton.setOnClickListener(v -> presenter.redeemRewardsRequest(rewardsRedeemConfirmation));
    }

    @NonNull
    @Override
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void launchSuccessScreen(@NotNull RewardsRedeemResult successResponse) {
        dismissProgressDialog();
        hideToolBar();

        View.OnClickListener primaryButtonClick = v -> new AccountBalanceUpdateHelper(getBaseActivity()).updateRewardsBalance(new AccountRefreshInterface() {
            @Override
            public void onSuccess() {
                goToRewardsHub();
            }

            @Override
            public void onFailure() {
                goToRewardsHub();
            }
        });

        View.OnClickListener secondaryButtonClick = v -> new AccountBalanceUpdateHelper(getBaseActivity()).updateRewardsBalance(new AccountRefreshInterface() {
            @Override
            public void onSuccess() {
                navigateToDonateToCharityFragment();
            }

            @Override
            public void onFailure() {
                navigateToDonateToCharityFragment();
            }

            private void navigateToDonateToCharityFragment() {
                if (getActivity() != null) {
                    dismissProgressDialog();
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((RedeemRewardsActivity) getActivity()).startFragment(DonateToCharityFragment.newInstance());
                }
            }
        });

        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setTitle(getString(R.string.donation_successful))
                .setDescription(getString(R.string.donation_description, donateToCharityDataModel.getCharityToDonateTo()))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setSecondaryButtonLabel(getString(R.string.rewards_make_another_donation))
                .build(true);

        if (getActivity() != null) {
            ((RedeemRewardsActivity) getActivity()).startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties, true, primaryButtonClick, secondaryButtonClick));
        }
    }

    @Override
    public void launchFailureScreen() {
        View.OnClickListener primaryButtonClick = v -> goToRewardsHub();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            hideToolBar();
            GenericResultScreenProperties failureResultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                    .setResultScreenAnimation("general_failure.json")
                    .setTitle(getString(R.string.donation_unsuccessful))
                    .setDescription(getString(R.string.donate_unsuccessful_message, donateToCharityDataModel.getCharityToDonateTo()))
                    .setPrimaryButtonLabel(getString(R.string.done))
                    .build(true);

            if (getActivity() != null) {
                ((RedeemRewardsActivity) getActivity()).startFragment(GenericResultScreenFragment.newInstance(failureResultScreenProperties, false, primaryButtonClick, null));
            }
        }, 200);
    }

    private void goToRewardsHub() {
        RedeemRewardsActivity redeemRewardsActivity = (RedeemRewardsActivity) getActivity();
        if (redeemRewardsActivity != null) {
            Intent intent = new Intent(redeemRewardsActivity, BehaviouralRewardsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}