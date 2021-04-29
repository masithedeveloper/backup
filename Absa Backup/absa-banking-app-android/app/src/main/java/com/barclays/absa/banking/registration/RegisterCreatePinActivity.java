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
package com.barclays.absa.banking.registration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.databinding.Activity2faRegisterPinBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;

import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class RegisterCreatePinActivity extends BaseActivity implements View.OnClickListener, RegisterCreatePinView {

    public static final String SECONDARY_CARD_FAILURE_INDICATOR = "H0823 062";
    private RegisterCreatePinPresenter presenter;
    private Activity2faRegisterPinBinding binding;
    private RegisterProfileDetail registerProfileDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_register_pin, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.register), view -> finish());
        initViews();
        configureTalkBack();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.CREATE_ONLINE_BANKING_PIN_CONST, BMBConstants.TRUE_CONST);
    }

    private void configureTalkBack() {
        binding.confirmUserPinView.setContentDescription(getString(R.string.talkback_digital_banking_verify_pin_field));
        binding.whatIsSurecheckPhraseButton.setContentDescription(getString(R.string.talkback_digital_banking_surephrase_help));
        binding.clientSurePhraseContentLabelView.setContentDescription(getString(R.string.talkback_digital_banking_surephrase_field, binding.clientSurePhraseContentLabelView.getContentTextView().getText().toString()));
        binding.nextButton.setContentDescription(getString(R.string.talkback_digital_banking_next_button));

        if (isAccessibilityEnabled()) {
            binding.enterUserPinView.setOnClickListener(v -> binding.enterUserPinView.getEditText().sendAccessibilityEvent(AccessibilityNodeInfoCompat.ACTION_FOCUS));
            binding.confirmUserPinView.setOnClickListener(v -> binding.confirmUserPinView.getEditText().sendAccessibilityEvent(AccessibilityNodeInfoCompat.ACTION_FOCUS));
        }
    }

    private void initViews() {
//        tilFieldsForReset = new TextInputLayout[]{binding.enterUserPinView.isChecked(), binding.confirmUserPinView};
        registerProfileDetail = (RegisterProfileDetail) getIntent().getSerializableExtra(getString(R.string.register_profile_detail_obj));
        presenter = new RegisterCreatePinPresenter(this, registerProfileDetail);
        binding.nextButton.setOnClickListener(this);
        binding.whatIsSurecheckPhraseButton.setOnClickListener(this);
        if (registerProfileDetail != null) {
            binding.clientSurePhraseContentLabelView.setContentText(getString(R.string.surephrase_fix_lint_crying, StringExtensions.toTitleCase(registerProfileDetail.getFirstname()), StringExtensions.toTitleCase(registerProfileDetail.getSurname())));
        }

        binding.enterUserPinView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateToEnablePin(binding.enterUserPinView, s);
            }
        });

        binding.confirmUserPinView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateToEnablePin(binding.confirmUserPinView, s);
            }
        });
    }

    private void validateToEnablePin(NormalInputView input, Editable editable) {
        if (editable.length() < 5) {
            input.setImageViewImage(R.drawable.ic_cross_dark);
            input.setImageViewVisibility(View.VISIBLE);

        } else {
            input.setImageViewImage(R.drawable.ic_check_dark);
            input.setImageViewVisibility(View.VISIBLE);
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
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextButton:
                String pin = binding.enterUserPinView.getSelectedValue().trim();
                String reEnterPin = binding.confirmUserPinView.getSelectedValue().trim();
                binding.enterUserPinView.showError(false);
                binding.confirmUserPinView.showError(false);
                presenter.nextButtonTapped(pin, reEnterPin);
                break;
            case R.id.whatIsSurecheckPhraseButton:
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .message(getString(R.string.register_2fa_sure_phrase_help_msg))
                        .build());
                break;
        }
    }

    @Override
    public void onPinInvalidInput() {
        binding.enterUserPinView.showError(true);
        binding.enterUserPinView.setError(getString(R.string.pin_size));
        announceErrors(binding.enterUserPinView.getErrorTextView());
    }

    @Override
    public void onConfirmPinInvalidInput() {
        binding.confirmUserPinView.showError(true);
        binding.confirmUserPinView.setError(getString(R.string.register_online_banking_confirm_pin_hint));
        announceErrors(binding.confirmUserPinView.getErrorTextView());
    }

    @Override
    public void onPinDoesNotMatch() {
        binding.confirmUserPinView.showError(true);
        binding.confirmUserPinView.setError(getString(R.string.error_enter_same_pin));
        announceErrors(binding.confirmUserPinView.getErrorTextView());
    }

    @Override
    public void launchCreatePasswordActivity(RegisterProfileDetail registerProfileDetail) {
        binding.confirmUserPinView.setIconViewImage(R.drawable.ic_tick_black);
        Intent confirmIntent = new Intent(this, RegisterCreatePasswordActivity.class);
        confirmIntent.putExtra(getString(R.string.register_profile_detail_obj), registerProfileDetail);
        String surePhrase;
        String firstNameInitial = "";
        String surname = "";
        if (registerProfileDetail.getFirstname() != null && !registerProfileDetail.getFirstname().isEmpty()) {
            firstNameInitial = registerProfileDetail.getFirstname().substring(0, 1);
        }
        if (registerProfileDetail.getSurname() != null) {
            surname = registerProfileDetail.getSurname();
        }

        surePhrase = getString(R.string.surephrase_fix_lint_crying, StringExtensions.toTitleCase(firstNameInitial), StringExtensions.toTitleCase(surname));

        confirmIntent.putExtra(BMBConstants.SURE_PHRASE, surePhrase);
        startActivity(confirmIntent);
    }

    @Override
    public void launchRegistrationResultActivity(RegisterAOLProfileResponse resultObject) {
        Intent confirmIntent = new Intent(this, RegistrationResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BMBConstants.ONLINE_PIN, registerProfileDetail.getOnlinePin());
        String surePhrase;
        String firstName = "";
        String surname = "";
        if (registerProfileDetail.getFirstname() != null) {
            firstName = registerProfileDetail.getFirstname().substring(0, 1);
        }
        if (registerProfileDetail.getSurname() != null) {
            surname = registerProfileDetail.getSurname();
        }

        surePhrase = getString(R.string.surephrase_fix_lint_crying, StringExtensions.toTitleCase(firstName), StringExtensions.toTitleCase(surname));
        bundle.putString(BMBConstants.SURE_PHRASE, surePhrase);
        bundle.putSerializable(RESULT, resultObject);
        confirmIntent.putExtras(bundle);
        startActivity(confirmIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
                int permissionStatus = grantResults[0];
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        presenter.registerUserProfile();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        presenter.onPermissionDenied();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void requestDeviceStateAccessPermission() {
        PermissionFacade.requestDeviceStatePermission(this, () -> presenter.registerUserProfile());
    }

    private void announceErrors(TextView textView) {
        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceRandValueTextFromView(textView);
        }
    }

    @Override
    public void registrationFailed(String failureResponse) {
        if (failureResponse.contains(SECONDARY_CARD_FAILURE_INDICATOR)) {
            startActivity(IntentFactory.getSecondaryCardRegistrationFailureScreen(this));
        } else {
            startActivity(IntentFactory.getPartialRegistrationFailureScreen(this, R.string.register_bdp_unsuccess_msg, failureResponse));
        }
    }

    @Override
    public void showAlreadyRegisteredErrorDialog() {
        startActivity(IntentFactory.getPreLoginFailureScreen(this, R.string.register_result_already_registered_title, R.string.register_result_already_registered_message));
    }
}
