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

package com.barclays.absa.banking.manage.devices;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.databinding.ManageDeviceActivityBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AlertBox;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.DeviceUtils;
import com.barclays.absa.utils.ProfileManager;

import static android.view.View.GONE;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_KEY;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_CURRENT_DEVICE_VERIFICATION_DEVICE;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_NOMINATE_PRIMARY;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_NOMINATE_VERIFICATION_DEVICE;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.TOTAL_DEVICES_KEY;

public class ManageDeviceActivity extends ConnectivityMonitorActivity implements ManageDeviceView {
    private ManageDeviceActivityBinding binding;
    private ManageDevicePresenter presenter;
    private Device deviceDetails;
    private int totalDevice;
    private MenuItem editMenuItem;
    private boolean isNominateVerificationDevice;
    private boolean currentDeviceIsTheVerificationDevice;

    static final int NOMINATE_PRIMARY_DEVICE_REQUEST_CODE = 2;

    private final SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    presenter.changePrimaryDevice(deviceDetails), 250);
        }

        @Override
        public void onSureCheckFailed() {
            super.onSureCheckFailed();
            showErrorResultScreen(true);
        }

        @Override
        public void onSureCheckRejected() {
            showErrorResultScreen(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.manage_device_activity, null, false);
        setContentView(binding.getRoot());

        presenter = new ManageDevicePresenter(this);

        setToolBarBack(R.string.manage_device_device_title);

        deviceDetails = (Device) getIntent().getSerializableExtra(DEVICE_KEY);
        totalDevice = (int) getIntent().getSerializableExtra(TOTAL_DEVICES_KEY);
        isNominateVerificationDevice = (boolean) getIntent().getSerializableExtra(IS_NOMINATE_VERIFICATION_DEVICE);
        currentDeviceIsTheVerificationDevice = (boolean) getIntent().getSerializableExtra(IS_CURRENT_DEVICE_VERIFICATION_DEVICE);

        if (isNominateVerificationDevice) {
            presenter.isSurecheckDeviceAvailableInvoked();
        }

        populateUIComponents();
        setUIComponentsListeners();
        setEditableComponents(false);
    }

    private void populateUIComponents() {
        binding.deviceNickname.setText(deviceDetails.getNickname());
        binding.deviceModel.setText(deviceDetails.getModel());

        binding.nicknameInputView.setSelectedValue(deviceDetails.getNickname());

        if (deviceDetails.getManufacturer() != null) {
            if (!getString(R.string.manage_device_manufacturer_name_apple).equalsIgnoreCase(deviceDetails.getManufacturer())) {
                binding.deviceManufacturerImageView.setBackgroundResource(R.drawable.ic_device_android);
            } else {
                binding.deviceManufacturerImageView.setBackgroundResource(R.drawable.ic_device_apple);
            }
        } else {
            binding.deviceManufacturerImageView.setBackgroundResource(-1);
        }

        binding.primaryDeviceOptionActionView.setVisibility(deviceDetails.isPrimarySecondFactorDevice() ? View.VISIBLE : View.GONE);
        binding.primaryDeviceOptionActionView.leftImageView.setImageTintList(null);
        binding.currentDeviceOptionActionView.setVisibility(DeviceUtils.isCurrentDevice(deviceDetails) ? View.VISIBLE : View.GONE);
    }

    private void setUIComponentsListeners() {
        binding.saveDetailsButton.setOnClickListener(v -> {
            preventDoubleClick(binding.saveDetailsButton);

            binding.nicknameInputView.showError(false);
            if (TextUtils.isEmpty(binding.nicknameInputView.getSelectedValue())) {
                binding.nicknameInputView.setError(getString(R.string.manage_device_nickname_error));
                return;
            }

            presenter.requestEditDeviceNickname(deviceDetails, binding.nicknameInputView.getSelectedValue());
        });

        binding.delinkDeviceButton.setOnClickListener(v -> displayDelinkPopup());

        binding.makeDeviceVerificationDeviceButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            getAppCacheService().setDelinkedPrimaryDevice(null);
            presenter.isSurecheckDeviceAvailableInvoked();
        });
    }

    private void setEditableComponents(boolean isEditable) {

        boolean isEditDeviceCurrentDevice = DeviceUtils.isCurrentDevice(deviceDetails);
        boolean isEditDevicePrimaryDevice = deviceDetails.isPrimarySecondFactorDevice();

        setNicknameInputAnimationListener(isEditable);

        if (totalDevice > 1) {
            binding.saveDetailsButton.setVisibility(isEditable ? View.VISIBLE : GONE);
            if (isEditDevicePrimaryDevice) {
                binding.makeDeviceVerificationDeviceButton.setVisibility(View.GONE);
            } else {
                binding.makeDeviceVerificationDeviceButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
            }

            if (isEditDevicePrimaryDevice && isEditDeviceCurrentDevice) {
                binding.delinkDeviceButton.setVisibility(View.VISIBLE);
                if (isEditable) {
                    binding.delinkDeviceButton.setVisibility(View.GONE);
                    binding.saveDetailsButton.setVisibility(View.VISIBLE);
                }
            } else {
                binding.delinkDeviceButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
            }
        } else {
            binding.saveDetailsButton.setVisibility(isEditable ? View.VISIBLE : GONE);
            if (isEditDevicePrimaryDevice) {
                binding.makeDeviceVerificationDeviceButton.setVisibility(View.GONE);
            } else {
                binding.makeDeviceVerificationDeviceButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
            }

            binding.delinkDeviceButton.setVisibility(View.GONE);
        }

        if (isNominateVerificationDevice) {
            binding.saveDetailsButton.setVisibility(GONE);
            binding.makeDeviceVerificationDeviceButton.setVisibility(View.GONE);
        }
    }

    private void setNicknameInputAnimationListener(boolean isEditable) {
        if (isEditable) {
            binding.contentConstraintLayout.animate().scaleX(0).scaleY(0).setDuration(300).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    binding.contentConstraintLayout.setVisibility(View.GONE);
                    //binding.deviceNameSecondaryContentAndLabelView.setLayoutTransition(new LayoutTransition());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            binding.nicknameInputView.setVisibility(View.VISIBLE);
            binding.nicknameInputView.animate().scaleX(1).scaleY(1).setDuration(600).setListener(null);
        } else {
            binding.nicknameInputView.animate().scaleX(0).scaleY(0).setDuration(300).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    binding.nicknameInputView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            binding.contentConstraintLayout.setVisibility(View.VISIBLE);
            binding.contentConstraintLayout.animate().scaleX(1).scaleY(1).setDuration(600).setListener(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.manage_device_menu, menu);
        editMenuItem = menu.findItem(R.id.editMenuItem);
        editMenuItem.setVisible(!isNominateVerificationDevice);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editMenuItem) {
            setEditableComponents(true);
            editMenuItem.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void responseEditDeviceNickname(ManageDeviceResult mangeDeviceResult) {
        finish();
    }

    private void displayDelinkPopup() {
        String message = deviceDetails.isPrimarySecondFactorDevice() ?
                getString(R.string.you_are_about_to_de_link_primary) :
                getString(R.string.device_delink_popup_msg);
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.remove_device_title))
                .message(message)
                .positiveDismissListener((dialog, which) -> {
                    if (deviceDetails.isPrimarySecondFactorDevice()) {
                        getAppCacheService().setDelinkedPrimaryDevice(deviceDetails);
                        nominateAnotherPrimaryDevice();
                    } else {
                        getAppCacheService().setDelinkedPrimaryDevice(null);
                        boolean isCurrentDevice = DeviceUtils.isCurrentDevice(deviceDetails);
                        presenter.delinkDeviceInvoked(deviceDetails, isCurrentDevice);
                    }
                }));
    }

    public void navigateToGenericResultScreen(boolean isFailureResult, boolean isCurrentDeviceDeleted) {
        final Intent intent = new Intent(ManageDeviceActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_tick);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_delinked_success_message);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (isCurrentDeviceDeleted) {
            intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, -1); // Hide button
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close);
            GenericResultActivity.bottomOnClickListener = v -> deleteProfile();
        } else {
            intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.manage_devices); // R.string.manage_devices
            GenericResultActivity.topOnClickListener = v -> {
                mScreenName = mSiteSection = BMBConstants.SETTINGS_CONST;
                AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                Intent deviceSelectIntent = new Intent(ManageDeviceActivity.this, DeviceListActivity.class);
                deviceSelectIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(IntentFactory.getManageDevices(ManageDeviceActivity.this));
            };

            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndShowHomeScreenWithAccountsList();
        }

        if (isFailureResult) {
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_linked_successfully);
        } else {
            intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_tick);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.remove_device_message);
        }
        startActivity(intent);
    }

    private void deleteProfile() {
        ProfileManager.getInstance().deleteProfile(ProfileManager.getInstance().getActiveUserProfile(), new ProfileManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                logoutAndGoToStartScreen();
            }

            @Override
            public void onFailure() {
                BaseAlertDialog.INSTANCE.showRetryErrorDialog("Profile could not be deleted on device.", new AlertBox.AlertRetryListener() {
                    @Override
                    public void retry() {
                        deleteProfile();
                    }
                });
            }
        });
    }

    @Override
    public void navigateToIsSurecheckDeviceAvailableScreen() {
        if (currentDeviceIsTheVerificationDevice) {
            getAppCacheService().setCurrentDeviceProcessingSureCheck(true);
            presenter.changePrimaryDevice(deviceDetails);
        } else if (deviceDetails.isSecondFactorEnabled()) {
            Intent intent = new Intent(ManageDeviceActivity.this, VerificationDeviceAvailableActivity.class);
            intent.putExtra(DEVICE_OBJECT, deviceDetails);
            startActivity(intent);
        } else {
            toastLong(R.string.upgrade_device_to_2fa);
        }
    }

    private void nominateAnotherPrimaryDevice() {
        final Intent intent = new Intent(ManageDeviceActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_lock);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.yes);
        intent.putExtra(GenericResultActivity.IS_NOMINATE_PRIMARY, true);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.no);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.you_will_have_to_nominate_another_device);
        GenericResultActivity.topOnClickListener = v -> {
            Intent returnIntent = new Intent(ManageDeviceActivity.this, DeviceListActivity.class);
            returnIntent.putExtra(IS_NOMINATE_PRIMARY, true);
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(returnIntent);
            finish();
        };

        GenericResultActivity.bottomOnClickListener = v -> {
            // NO
            Intent deviceSelectIntent = new Intent(ManageDeviceActivity.this, DeviceListActivity.class);
            deviceSelectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(deviceSelectIntent);
            finish();
        };
        startActivityForResult(intent, NOMINATE_PRIMARY_DEVICE_REQUEST_CODE);
    }

    public void showPrimaryDeviceChangeSuccessfulScreen() {
        getAppCacheService().setPrimarySecondFactorDevice(true);

        Intent deviceDelinkingSuccessIntent = new Intent(ManageDeviceActivity.this, GenericResultActivity.class);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_lock);

        if (!DeviceUtils.isCurrentDevice(deviceDetails)) {
            String deviceNickName = deviceDetails.getNickname();
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.DEVICE_NICKNAME, deviceNickName);
        }

        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.this_is_now_your_surecheck_2_0_device);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.surecheck_2_0_instruction);


        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok);
        GenericResultActivity.bottomOnClickListener = v -> logoutAndGoToStartScreen();

        final Device delinkedPrimaryDevice = getAppCacheService().getDelinkedPrimaryDevice();
        if (delinkedPrimaryDevice != null) {
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, -1);// hide it
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close);

            GenericResultActivity.bottomOnClickListener = v -> new ManageDevicesInteractor().delinkDevice(delinkedPrimaryDevice,
                    new ExtendedResponseListener<ManageDeviceResult>(ManageDeviceActivity.this) {

                        @Override
                        public void onSuccess(ManageDeviceResult successResponse) {
                            getBaseView().dismissProgressDialog();
                            logoutAndGoToStartScreen();
                        }

                        @Override
                        public void onFailure(final ResponseObject failureResponse) {
                            getBaseView().dismissProgressDialog();
                            BaseAlertDialog.INSTANCE.showErrorAlertDialog(failureResponse.getErrorMessage(), (dialog, which) -> logoutAndGoToStartScreen());
                        }
                    });
        }

        deviceDelinkingSuccessIntent.putExtra(BMBConstants.POST_LOGIN_LAYOUT, true);
        startActivity(deviceDelinkingSuccessIntent);
    }

    @Override
    public void showServerErrorFromDevice(String message) {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(message);
    }

    @Override
    public void onReceivedSureCheckVertificationType(TransactionVerificationType transactionVerificationType) {
        switch (transactionVerificationType) {
            case SURECHECKV2Required:
                sureCheckDelegate.initiateV2CountDownScreen();
                break;
            case SURECHECKV2_FALLBACK:
            case SURECHECKV2_FALLBACKRequired:
                sureCheckDelegate.initiateOfflineOtpScreen();
                break;
            case NoPrimaryDevice:
                showNoPrimaryDeviceScreen();
                break;
        }
    }

    private void showErrorResultScreen(boolean isFailureResult) {
        GenericResultActivity.topOnClickListener = v -> {
            mScreenName = mSiteSection = BMBConstants.SETTINGS_CONST;
            startActivity(IntentFactory.getManageDevices(ManageDeviceActivity.this));
        };

        GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();

        Intent intent = new Intent(ManageDeviceActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        if (isFailureResult) {
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed);
        } else {
            intent.putExtra(GenericResultActivity.IS_GENERAL_ALERT, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected);
        }
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.manage_devices);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        startActivity(intent);
    }
}