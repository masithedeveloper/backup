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

package com.barclays.absa.banking.presentation.shared;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService;
import com.barclays.absa.banking.beneficiaries.services.dto.MyNotification;
import com.barclays.absa.banking.boundary.model.FaxDetails;
import com.barclays.absa.banking.boundary.model.MyNotificationDetails;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.KeyboardUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorInterface;
import styleguide.forms.notificationmethodview.NotificationMethodData;
import styleguide.forms.notificationmethodview.NotificationMethodView;
import styleguide.forms.validation.ValueRequiredValidationHidingTextWatcher;
import styleguide.utils.ValidationUtils;
import styleguide.utils.extensions.StringExtensions;

import static styleguide.forms.notificationmethodview.ContactRadioEditView.PICKER_REQUEST_CODE;
import static styleguide.forms.notificationmethodview.ContactRadioEditView.PICKER_REQUEST_CODE_SELF;

public class NotificationMethodSelectionActivity<T extends SelectorInterface> extends BaseActivity {

    public static final int NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE = 6000;
    public static final int NOTIFICATION_METHOD_SELF_REQUEST_CODE = 6001;
    public static final int NOTIFICATION_METHOD_SELF_AND_BENEFICIARY_REQUEST_CODE = 6002;

    public static final String TOOLBAR_TITLE = "toolbar_title";
    public static final String SHOW_BENEFICIARY_NOTIFICATION_TITLE = "show_beneficiary_notification_title";
    public static final String SHOW_BENEFICIARY_NOTIFICATION_METHOD = "show_beneficiary_notification_method";
    public static final String SHOW_SELF_NOTIFICATION_METHOD = "show_self_notification_method";
    public static final String SHOW_SELF_AND_BENEFICIARY_NOTIFICATION_METHOD = "show_self_and_beneficiary_notification_method";

    public static final String LAST_SELECTED_TYPE_FOR_BENEFICIARY = "last_selected_type";
    public static final String LAST_SELECTED_VALUE_FOR_BENEFICIARY = "last_selected_value";
    public static final String LAST_SELECTED_TYPE_FOR_SELF = "last_self_selected_type";
    public static final String LAST_SELECTED_VALUE_FOR_SELF = "last_self_selected_value";
    public static final String HIDE_NOTIFICATION_TITLE = "hide_notification_title";

    private String lastSelectedTypeForBeneficiary = "N", lastSelectedTypeForSelf = "N";
    private String lastSelectedValueForBeneficiary = "", lastSelfSelectedValueForSelf = "";

    private boolean showBeneficiaryNotificationMethodView;
    private boolean showSelfNotificationMethodView;
    private boolean showSelfAndBeneficiaryNotificationMethodView = true;
    private NotificationMethodView beneficiaryNotificationMethodView;
    private NotificationMethodView selfNotificationMethodView;

    private boolean showBeneficiaryNotificationTitle;
    private MyNotification myNotificationSuccessResponse;

    private boolean hideNotificationTitle = false;

    private ExtendedResponseListener<MyNotification> myNotificationExtendedResponseListener = new ExtendedResponseListener<MyNotification>(this) {
        @Override
        public void onSuccess(MyNotification successResponse) {
            super.onSuccess();
            myNotificationSuccessResponse = successResponse;
            initViews();
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            showMessageError(ResponseObject.extractErrorMessage(failureResponse), (dialog, which) -> finish());
        }
    };

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            KeyboardUtils.hideSoftKeyboard(getCurrentFocus());
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_method_selection_activity);
        Bundle extras = getIntent().getExtras();

        String toolbarTitle = "";

        if (extras != null) {
            toolbarTitle = extras.getString(TOOLBAR_TITLE, "");

            lastSelectedTypeForBeneficiary = extras.getString(LAST_SELECTED_TYPE_FOR_BENEFICIARY, "N");
            lastSelectedValueForBeneficiary = extras.getString(LAST_SELECTED_VALUE_FOR_BENEFICIARY, "");
            lastSelectedTypeForSelf = extras.getString(LAST_SELECTED_TYPE_FOR_SELF, "N");
            lastSelfSelectedValueForSelf = extras.getString(LAST_SELECTED_VALUE_FOR_SELF, "");

            hideNotificationTitle = extras.getBoolean(HIDE_NOTIFICATION_TITLE, false);
            showBeneficiaryNotificationTitle = extras.getBoolean(SHOW_BENEFICIARY_NOTIFICATION_TITLE, true);
            showBeneficiaryNotificationMethodView = extras.containsKey(SHOW_BENEFICIARY_NOTIFICATION_METHOD) && extras.getBoolean(SHOW_BENEFICIARY_NOTIFICATION_METHOD);
            showSelfNotificationMethodView = extras.containsKey(SHOW_SELF_NOTIFICATION_METHOD) && extras.getBoolean(SHOW_SELF_NOTIFICATION_METHOD);
            showSelfAndBeneficiaryNotificationMethodView = extras.containsKey(SHOW_SELF_AND_BENEFICIARY_NOTIFICATION_METHOD) && extras.getBoolean(SHOW_SELF_AND_BENEFICIARY_NOTIFICATION_METHOD);
        }

        setToolBar(StringExtensions.toTitleCase(toolbarTitle), view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        if (showSelfNotificationMethodView || showSelfAndBeneficiaryNotificationMethodView) {
            BeneficiariesService beneficiariesService = new BeneficiariesInteractor();
            beneficiariesService.fetchMyNotificationDetails(myNotificationExtendedResponseListener);
        } else {
            initViews();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ViewGroup container = findViewById(R.id.containerView);

        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {

            Intent resultIntent = new Intent();
            NotificationMethodData beneficiaryNotificationMethod;
            NotificationMethodData selfNotificationMethod;

            if (showBeneficiaryNotificationMethodView || showSelfAndBeneficiaryNotificationMethodView) {
                beneficiaryNotificationMethod = beneficiaryNotificationMethodView.getContactDetailValue();
                resultIntent.putExtra(SHOW_BENEFICIARY_NOTIFICATION_METHOD, beneficiaryNotificationMethod);

                if (!validateInput(beneficiaryNotificationMethodView)) {
                    return;
                }
            }

            if (showSelfNotificationMethodView || showSelfAndBeneficiaryNotificationMethodView) {
                selfNotificationMethod = selfNotificationMethodView.getContactDetailValue();
                resultIntent.putExtra(SHOW_SELF_NOTIFICATION_METHOD, selfNotificationMethod);

                if (!validateInput(selfNotificationMethodView)) {
                    return;
                }
            }
            KeyboardUtils.hideKeyboard(this);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        if (!showBeneficiaryNotificationTitle && showSelfNotificationMethodView) {
            selfNotificationMethodView = new NotificationMethodView(this);
            selfNotificationMethodView.setTitleVisibility(View.GONE);
            if (myNotificationSuccessResponse != null && myNotificationSuccessResponse.getNotificationDetails() != null) {
                MyNotificationDetails notificationDetails = myNotificationSuccessResponse.getNotificationDetails();
                final List<FaxDetails> faxDetails = notificationDetails.getFaxDetails();
                String fax = (faxDetails != null && faxDetails.size() > 0) ? faxDetails.get(0).getFaxCode() + faxDetails.get(0).getFaxNumber() : "";
                fax = formatNumber(fax);
                String actualCellphoneNumber = formatNumber(notificationDetails.getActualCellphoneNumber());
                final List<String> email = notificationDetails.getEmail();
                selfNotificationMethodView.setMyNotificationDetails(actualCellphoneNumber, email != null ? email : new ArrayList<>(), formatNumber(fax));
            }
            selfNotificationMethodView.setNotificationType(NotificationMethodView.NotificationType.SELF);
            container.addView(selfNotificationMethodView);

            lastSelfSelectedValueForSelf = StringExtensions.removeSpaces(lastSelfSelectedValueForSelf);
            selfNotificationMethodView.setSelectedItem(lastSelectedTypeForSelf.substring(0, 1), lastSelfSelectedValueForSelf);

            if (hideNotificationTitle) {
                selfNotificationMethodView.setTitleVisibility(View.GONE);
            }

            return;
        }

        if (showBeneficiaryNotificationMethodView || showSelfAndBeneficiaryNotificationMethodView) {
            beneficiaryNotificationMethodView = new NotificationMethodView(this);
            beneficiaryNotificationMethodView.setTitle(R.string.notification_method_beneficiary);
            beneficiaryNotificationMethodView.setNotificationType(NotificationMethodView.NotificationType.BENEFICIARY);
            container.addView(beneficiaryNotificationMethodView);

            lastSelectedValueForBeneficiary = StringExtensions.removeSpaces(lastSelectedValueForBeneficiary);
            beneficiaryNotificationMethodView.setSelectedItem(lastSelectedTypeForBeneficiary.substring(0, 1), lastSelectedValueForBeneficiary);

            if (hideNotificationTitle) {
                beneficiaryNotificationMethodView.setTitleVisibility(View.GONE);
            }
        }

        if (showSelfNotificationMethodView || showSelfAndBeneficiaryNotificationMethodView) {
            selfNotificationMethodView = new NotificationMethodView(this);
            selfNotificationMethodView.setTitle(R.string.notification_method_self);
            if (myNotificationSuccessResponse != null && myNotificationSuccessResponse.getNotificationDetails() != null) {
                MyNotificationDetails notificationDetails = myNotificationSuccessResponse.getNotificationDetails();
                final List<FaxDetails> faxDetails = notificationDetails.getFaxDetails();
                String fax = (faxDetails != null && faxDetails.size() > 0) ? faxDetails.get(0).getFaxCode() + faxDetails.get(0).getFaxNumber() : "";
                fax = formatNumber(fax);
                String actualCellphoneNumber = formatNumber(notificationDetails.getActualCellphoneNumber());
                final List<String> email = notificationDetails.getEmail();
                selfNotificationMethodView.setMyNotificationDetails(actualCellphoneNumber, email != null ? email : new ArrayList<>(), formatNumber(fax));
            }
            selfNotificationMethodView.setNotificationType(NotificationMethodView.NotificationType.SELF);
            container.addView(selfNotificationMethodView);

            lastSelfSelectedValueForSelf = StringExtensions.removeSpaces(lastSelfSelectedValueForSelf);
            selfNotificationMethodView.setSelectedItem(lastSelectedTypeForSelf.substring(0, 1), lastSelfSelectedValueForSelf);

            if (hideNotificationTitle) {
                selfNotificationMethodView.setTitleVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PICKER_REQUEST_CODE || requestCode == PICKER_REQUEST_CODE_SELF) && data != null) {
            try {
                Uri contactUri = data.getData();
                if (contactUri != null) {
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DATA};
                    Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        String value = cursor.getString(numberIndex);
                        setNormalInputView(value, requestCode);
                        cursor.close();
                    }
                } else {
                    showGenericErrorMessage();
                }
            } catch (Exception e) {
                BMBLogger.e(e.getMessage());
                showGenericErrorMessage();
            }
        }
    }

    private void setNormalInputView(String value, int pickerCode) {
        NotificationMethodView notificationView;
        if (pickerCode == PICKER_REQUEST_CODE && beneficiaryNotificationMethodView != null) {
            notificationView = beneficiaryNotificationMethodView;
        } else {
            if (selfNotificationMethodView == null) {
                selfNotificationMethodView = new NotificationMethodView(this);
                selfNotificationMethodView.setTitle(R.string.notification_method_self);
            }
            notificationView = selfNotificationMethodView;
        }

        if (notificationView != null) {
            switch (notificationView.getNotificationMethodType()) {
                case SMS:
                    notificationView.getSmsRadioEditView().getNormalInputView().setSelectedValue(formatNumber(value));
                    break;
                case EMAIL:
                    notificationView.getEmailRadioEditView().getNormalInputView().setSelectedValue(value);
                    break;
                case FAX:
                    notificationView.getFaxRadioEditView().getNormalInputView().setSelectedValue(formatNumber(value));
                    break;
            }
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    private String formatNumber(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        value = value.replaceAll(" ", "");
        if (value.startsWith("27")) {
            value = value.replaceFirst("27", "0");
        }
        return value.replace("+27", "0").replaceAll(" ", "");
    }

    public boolean validateInput(NotificationMethodView notificationMethodView) {

        if (notificationMethodView.getSmsRadioEditView().isChecked()) {
            NormalInputView smsNormalInputView = notificationMethodView.getSmsRadioEditView().getNormalInputView();
            new ValueRequiredValidationHidingTextWatcher(smsNormalInputView);
            if (ValidationUtils.isValidMobileNumber(smsNormalInputView.getSelectedValueUnmasked())) {
                return true;
            } else {
                smsNormalInputView.setError(getString(R.string.enter_valid_number));
                scrollToTopOfView(smsNormalInputView);
            }
        } else if (notificationMethodView.getEmailRadioEditView().isChecked()) {
            NormalInputView emailNormalInputView = notificationMethodView.getEmailRadioEditView().getNormalInputView();
            new ValueRequiredValidationHidingTextWatcher(emailNormalInputView);
            if (ValidationUtils.isValidEmailAddress(emailNormalInputView.getText())) {
                return true;
            } else {
                emailNormalInputView.setError(getString(R.string.invalid_email_address));
                scrollToTopOfView(emailNormalInputView);
            }
        } else if (notificationMethodView.getFaxRadioEditView().isChecked()) {
            NormalInputView faxNormalInputView = notificationMethodView.getFaxRadioEditView().getNormalInputView();
            new ValueRequiredValidationHidingTextWatcher(faxNormalInputView);
            if (ValidationUtils.isValidFaxNumber(faxNormalInputView.getSelectedValueUnmasked())) {
                return true;
            } else {
                faxNormalInputView.setError(getString(R.string.invalid_fax_number));
                scrollToTopOfView(faxNormalInputView);
            }
        } else {
            return true;
        }

        return false;
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = findViewById(R.id.scrollView);

        if (scrollView != null) {
            scrollView.post(() -> scrollView.scrollTo(0, view.getBottom()));
        }
    }
}
