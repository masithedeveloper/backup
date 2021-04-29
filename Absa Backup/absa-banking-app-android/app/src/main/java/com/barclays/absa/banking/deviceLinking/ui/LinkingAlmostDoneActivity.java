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
package com.barclays.absa.banking.deviceLinking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.databinding.LinkingAlmostDoneActivityBinding;
import com.barclays.absa.banking.deviceLinking.ui.verifyAlias.VerifyAliasDetails2faActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.manage.devices.linking.DeviceLimitReached2faActivity;
import com.barclays.absa.banking.passcode.createPasscode.CreatePasscodeActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

public class LinkingAlmostDoneActivity extends ConnectivityMonitorActivity implements View.OnClickListener, AlmostDoneView {

    private AlmostDonePresenter presenter;
    private LinkingAlmostDoneActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.linking_almost_done_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack("");
        mScreenName = BMBConstants.NEW_LOGIN_INTRO_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(
                BMBConstants.NEW_LOGIN_INTRO_CONST,
                BMBConstants.SIMPLIFIED_LOGIN_CONST,
                BMBConstants.TRUE_CONST);

        initViews();
        presenter = new AlmostDonePresenter(this);
    }

    private void initViews() {
        binding.createPasscodeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        preventDoubleClick(view);
        if (getAppCacheService().isPasscodeResetFlow()) {
            showCreatePasscodeScreen();
        } else {
            presenter.onContinueInvoked();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                    .title(getString(R.string.are_you_sure_stop_setup))
                    .message(getString(R.string.this_will_end_session))
                    .positiveDismissListener((dialog, which) -> {
                        AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                        BaseAlertDialog.INSTANCE.dismissAlertDialog();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        CustomerProfileObject.updateCustomerProfileObject(new CustomerProfileObject());
                        startActivity(intent);
                        finish();
                    }));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.are_you_sure_stop_setup))
                .positiveDismissListener((dialog, which) -> {
                    AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    CustomerProfileObject.updateCustomerProfileObject(new CustomerProfileObject());
                    startActivity(intent);
                    finish();
                }));
    }

    @Override
    public void showCreatePasscodeScreen() {
        Intent passcodeIntent = new Intent(this, CreatePasscodeActivity.class);
        startActivity(passcodeIntent);
    }

    @Override
    public void showDeviceLimitReachedScreen() {
        Intent deviceLimitReachedIntent = new Intent(this, DeviceLimitReached2faActivity.class);
        startActivity(deviceLimitReachedIntent);
    }

    @Override
    public void goToVerifyAliasIdScreen() {
        Intent verifyAliasIdIntent = new Intent(this, VerifyAliasDetails2faActivity.class);
        startActivity(verifyAliasIdIntent);
    }

    @Override
    public void showLinkingFailedScreen(String errorMessage) {
        GenericResultActivity.bottomOnClickListener = v -> {
            Intent intent = new Intent(LinkingAlmostDoneActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        };

        Intent deviceLinkingFailedIntent = new Intent(this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IS_FAILURE, true);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_cannot_be_linked);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, errorMessage);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    @Override
    public void showFailureDialog(String failureResponse) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.alert))
                .message(failureResponse)
                .build());
    }
}