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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.databinding.RedeemShoppingPointsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsActivity;

import styleguide.screens.GenericResultScreenFragment;
import styleguide.screens.GenericResultScreenProperties;

import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.IS_FAILURE;

public class RedeemShoppingPointsActivity extends BaseActivity implements RedeemShoppingPointsView {

    private RedeemShoppingPointsActivityBinding binding;
    private RedeemPointsPresenter redeemPointsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.redeem_shopping_points_activity, null, false);
        setContentView(binding.getRoot());

        redeemPointsPresenter = new RedeemPointsPresenter(this);
        navigateToRewardsRedeemFragment();
        setUpToolBar();
    }

    private void setUpToolBar() {
        View.OnClickListener onBackListener = v -> onBackPressed();
        setToolBarBack(getString(R.string.convert_to_points_title), onBackListener);
    }

    @Override
    public void navigateToRewardsRedeemFragment() {
        RedeemShoppingPointsFragment redeemShoppingPointsFragment = RedeemShoppingPointsFragment.newInstance();
        startFragment(redeemShoppingPointsFragment);
    }

    @Override
    public void navigateToRedeemShoppingPointsInputFragment(RedeemRewards redeemRewards) {
        RedeemShoppingPointsInputFragment redeemShoppingPointsInputFragment = RedeemShoppingPointsInputFragment.newInstance();
        startFragment(redeemShoppingPointsInputFragment);
    }

    @Override
    public void navigateToRedeemPointsConfirmationFragment(RedeemPointsInputFields userInput) {
        RedeemPointsConfirmationFragment redeemPointsConfirmationFragment = RedeemPointsConfirmationFragment.newInstance(userInput);
        getToolBar().setTitle(getString(R.string.confirm_points));
        startFragment(redeemPointsConfirmationFragment);
    }

    @Override
    public void navigateToRedeemPointsSuccessScreen() {
        setToolBar("", null);
        View.OnClickListener onClickListener = v -> loadAccountsClearingRewardsBalanceAndShowHomeScreen();
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setDescription(getString(R.string.your_points_may_take_up_to))
                .setTitle(getString(R.string.your_points_are_on_their_way))
                .setPrimaryButtonLabel(getString(R.string.home))
                .build(true);

        GenericResultScreenFragment genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, true, onClickListener, null);
        startFragment(genericResultScreenFragment);
    }

    @Override
    public void navigateToRedeemPointsFailureScreen() {
        setToolBar("", null);
        View.OnClickListener onClickListener = v -> onBackPressed();
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_failure.json")
                .setDescription(getString(R.string.points_unsuccessfully_redeemed))
                .setTitle(getString(R.string.your_points_conversion_was_unsuccessful))
                .setSecondaryButtonLabel(getString(R.string.done_action))
                .build(false);

        GenericResultScreenFragment genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, true, null, onClickListener);
        startFragment(genericResultScreenFragment);
    }

    private void startFragment(Fragment fragment) {
        startFragment(fragment, R.id.fragmentContainer, true, AnimationType.FADE, true, fragment.getClass().getName());
    }

    @Override
    public void retrieveRewards() {
        redeemPointsPresenter.pullRewards();
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof GenericResultScreenFragment) {
            Intent intent = new Intent(this, BehaviouralRewardsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            this.startActivity(intent);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void convertRewards(Bundle bundle) {
        redeemPointsPresenter.convertRewards(bundle);
    }

    @Override
    public Intent buildResultIntent(String message) {
        GenericResultActivity.bottomOnClickListener = v -> navigateToHomeScreenWithoutReloadingAccounts();

        Intent intent = new Intent(RedeemShoppingPointsActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        intent.putExtra(IS_FAILURE, true);
        if (!TextUtils.isEmpty(message)) {
            intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, message);
        }
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        return intent;
    }

    @Override
    public void launchSureCheckFailedResultScreen() {
        Intent intent = buildResultIntent(getString(R.string.points_unsuccessfully_redeemed));
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.your_points_conversion_was_unsuccessful);
        intent.putExtra(IS_FAILURE, true);
        startActivity(intent);
    }
}
