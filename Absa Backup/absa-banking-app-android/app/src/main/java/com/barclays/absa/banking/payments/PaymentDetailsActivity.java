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
package com.barclays.absa.banking.payments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetails;
import com.barclays.absa.banking.boundary.model.BeneficiaryTransactionDetails;
import com.barclays.absa.banking.boundary.model.FaxDetails;
import com.barclays.absa.banking.boundary.model.MyNotificationDetails;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper;
import com.barclays.absa.banking.databinding.ActivityPayHubPaymentDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.dto.FutureDatePayment;
import com.barclays.absa.banking.payments.services.dto.FutureDatedPaymentListResponse;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.AccountsView;
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.banking.shared.DigitalLimitState;
import com.barclays.absa.banking.shared.DigitalLimitsHelper;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.OperatorPermissionUtils;
import com.barclays.absa.utils.TextFormatUtils;
import com.barclays.absa.utils.ValidationUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.forms.notificationmethodview.NotificationMethodData;
import styleguide.utils.extensions.StringExtensions;

public class PaymentDetailsActivity extends BaseActivity implements DialogInterface.OnCancelListener, TextWatcher, View.OnClickListener, AccountsView {

    public static final String PRE_SELECTED_FROM_ACCOUNT = "preSelectedFromAccount";
    public static final String PAYMENT_NOTIFICATION_MAP = "PAYMENT_NOTIFICATION_MAP";
    private ActivityPayHubPaymentDetailsBinding binding;

    private SelectorList<AccountObjectWrapper> fromAccounts;
    private AddBeneficiaryPaymentObject addBeneficiarySuccessObject;
    private PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject = new PayBeneficiaryPaymentConfirmationObject();
    private BeneficiaryDetailObject beneficiaryDetail;
    private boolean iipStatus, isIIPChecked;
    private boolean hasImage;
    private AccountObject selectedFromAccount;
    private String paymentDate = "";
    private String bankName, branchCode, accountType;
    private int selectedIndex;
    public static final String BENEFICIARY_TRANSACTION_OBJECT = "beneficiaryTransactionObject";
    private boolean isMultiplePayments = false;
    private static final String AMOUNT = "amount";
    private BeneficiaryDetails beneficiaryDetails;
    private NotificationMethodData beneficiaryNotificationMethod, myNotificationMethod;

    private final ExtendedResponseListener<FutureDatedPaymentListResponse> futureDatedPaymentListResponseListener = new ExtendedResponseListener<FutureDatedPaymentListResponse>() {
        @Override
        public void onSuccess(final FutureDatedPaymentListResponse successResponse) {
            dismissProgressDialog();
            List<FutureDatePayment> futureDatePayments = successResponse.getFutureDatePayments();
            if (futureDatePayments != null) {
                boolean isBeneficiaryAccountFoundInList = checkBeneficiaryAccountInFutureDatedList(futureDatePayments);
                if (isBeneficiaryAccountFoundInList) {
                    displayChoiceDialog();
                } else {
                    requestForValidation();
                }
            } else {
                requestForValidation();
            }
        }
    };

    private final ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject> nextStepExtendedResponseListener = new ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject>() {
        @Override
        public void onSuccess(final PayBeneficiaryPaymentConfirmationObject successResponse) {
            getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(() -> {
                dismissProgressDialog();
                Intent intent = new Intent(PaymentDetailsActivity.this, PaymentConfirmationActivity.class);
                intent.putExtra(RESULT, successResponse);
                Bundle bundle = new Bundle();
                bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, payBeneficiaryConfirmationObject.getAccountNumber());
                bundle.putString(PaymentsConstants.BANK_NAME, bankName);
                bundle.putString(PaymentsConstants.BRANCH_CODE, branchCode);
                bundle.putString(PaymentsConstants.ACCOUNT_TYPE, accountType);
                bundle.putBoolean(BMBConstants.HAS_IMAGE, beneficiaryDetail == null ? hasImage : beneficiaryDetail.getHasImage());
                bundle.putString(BMBConstants.IMAGE_NAME, beneficiaryDetail == null ? addBeneficiarySuccessObject.getImageName() : beneficiaryDetail.getImageName());
                bundle.putString(BMBConstants.BEN_TYPE, beneficiaryDetail == null ? addBeneficiarySuccessObject.getBenStatusTyp() : beneficiaryDetail.getStatus() == null ? addBeneficiarySuccessObject.getBenStatusTyp() : beneficiaryDetail.getStatus());
                intent.putExtras(bundle);
                startActivity(intent);
            });
        }

        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            onFailureResponse();
        }
    };

    private void onFailureResponse() {
        if (getAppCacheService().hasErrorResponse()) {
            checkDeviceState();
        } else {

            GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndShowHomeScreenWithAccountsList();

            Intent intent = new Intent(PaymentDetailsActivity.this, GenericResultActivity.class);
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
            intent.putExtra(GenericResultActivity.TOP_BUTTON_SHOULD_RETURN_TO_PREVIOUS_SCREEN, true);
            intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.retry);
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeyboardUtils.hideKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeviceProfilingInteractor().notifyTransaction();
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_pay_hub_payment_details, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.payment_details));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           /* if (extras.containsKey(MultiplePaymentsSelectedBeneficiariesActivity.MULTIPLE_PAYMENTS))
                isMultiplePayments = extras.getBoolean(MultiplePaymentsSelectedBeneficiariesActivity.MULTIPLE_PAYMENTS);*/
            if (extras.containsKey(BeneficiaryDetailsActivity.BENEFICIARY_DETAIL_OBJECT)) {
                beneficiaryDetail = (BeneficiaryDetailObject) getIntent().getSerializableExtra(BeneficiaryDetailsActivity.BENEFICIARY_DETAIL_OBJECT);
                if (beneficiaryDetail != null) {
                    if (!"No".equalsIgnoreCase(beneficiaryDetail.getBenNotice())) {
                        populateBeneficiaryNotificationMethod(beneficiaryDetail);
                    }
                    if (!"No".equalsIgnoreCase(beneficiaryDetail.getMyNotice())) {
                        populateSelfNotificationMethod(beneficiaryDetail);
                    }
                }
            }
            if (extras.containsKey(BENEFICIARY_TRANSACTION_OBJECT)) {
                BeneficiaryTransactionDetails beneficiaryTransactionDetails = (BeneficiaryTransactionDetails) getIntent().getSerializableExtra(BENEFICIARY_TRANSACTION_OBJECT);
                if (beneficiaryTransactionDetails != null) {
                    beneficiaryDetails = beneficiaryTransactionDetails.getBeneficiaryDetails();
                    if (beneficiaryDetails != null) {
                        String paymentNotificationString = getPayMentNotificationString(beneficiaryDetails.getBeneficiaryId());
                        if (paymentNotificationString != null)
                            populateBeneficiaryNotificationMethod(beneficiaryDetails, paymentNotificationString);
                        else if (!"No".equalsIgnoreCase(beneficiaryDetails.getBeneficiaryNotice())) {
                            populateBeneficiaryNotificationMethod(beneficiaryDetails, null);
                        }

                        if (paymentNotificationString != null) {
                            populateSelfNotificationMethod(beneficiaryDetails, paymentNotificationString);
                        } else if (!"No".equalsIgnoreCase(beneficiaryDetails.getMyNotice())) {
                            populateSelfNotificationMethod(beneficiaryDetails, beneficiaryTransactionDetails.getMyNotificationDetails());
                        }
                    }
                }
            }
/*
            if (extras.containsKey(MultiplePaymentsSelectedBeneficiariesActivity.SAVED_BENEFICIARY_DETAILS)) {
                payBeneficiaryConfirmationObject = (PayBeneficiaryPaymentConfirmationObject) getIntent().getSerializableExtra(MultiplePaymentsSelectedBeneficiariesActivity.SAVED_BENEFICIARY_DETAILS);
            }*/
            if (extras.containsKey(AppConstants.RESULT)) {
                addBeneficiarySuccessObject = (AddBeneficiaryPaymentObject) getIntent().getSerializableExtra(AppConstants.RESULT);
                if (addBeneficiarySuccessObject != null) {
                    if (!"No".equalsIgnoreCase(addBeneficiarySuccessObject.getBeneficiaryNotice())) {
                        populateBeneficiaryNotificationMethod(addBeneficiarySuccessObject);
                    }
                    if (!"No".equalsIgnoreCase(addBeneficiarySuccessObject.getMyNotice())) {
                        populateSelfNotificationMethod(addBeneficiarySuccessObject);
                    }
                }
            }
            iipStatus = extras.getBoolean(PaymentsConstants.IIP_STATUS);
            if (extras.containsKey(BMBConstants.HAS_IMAGE)) {
                hasImage = getIntent().getBooleanExtra(BMBConstants.HAS_IMAGE, false);
            }
            populateBeneficiaryData();
            if (extras.containsKey(PRE_SELECTED_FROM_ACCOUNT)) {
                selectedFromAccount = (AccountObject) extras.getSerializable(PRE_SELECTED_FROM_ACCOUNT);
                setFromAccount();
            }
        }

        populateViews();
        setupTalkBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMultiplePayments && fromAccounts == null) {
            requestFromAccount();
        }
    }

    private void setupTalkBack() {
        binding.amountLargeInputView.setContentDescription(getString(R.string.talkback_payment_details_amount_payment_description));
        binding.theirReferenceView.setContentDescription(getString(R.string.talkback_payment_details_recipient_reference));
        binding.myReferenceView.setContentDescription(getString(R.string.talkback_payment_details_my_reference));
        binding.paymentTypeRadioView.setContentDescription(getString(R.string.talkback_payment_date_timespan));
        binding.paymentNotificationView.setContentDescription(getString(R.string.talkback_payment_details_notification));
        binding.continueToPaymentOverview.setContentDescription(getString(R.string.talkback_payment_continue_to_payment_overview));
    }

    private void populateBeneficiaryNotificationMethod(AddBeneficiaryPaymentObject addBeneficiarySuccessObject) {
        beneficiaryNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = addBeneficiarySuccessObject.getBeneficiaryMethod();

        String methodDetails = "";
        final String beneficiaryMethodDetails = addBeneficiarySuccessObject.getBeneficiaryMethodDetails();
        if ("S".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.SMS;
            if (beneficiaryMethodDetails.startsWith("27")) {
                methodDetails = beneficiaryMethodDetails.replaceFirst("27", "0");
            }
        } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.EMAIL;
            methodDetails = beneficiaryMethodDetails;
        } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.FAX;
            methodDetails = beneficiaryMethodDetails;
            if (methodDetails.startsWith("27")) {
                methodDetails = methodDetails.replaceFirst("27", "0");
            }
        }

        beneficiaryNotificationMethod.setNotificationMethodType(notificationMethodType);
        beneficiaryNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    NotificationMethodData.TYPE toNotificationMethodDataType(String s) {
        if (s != null)
            switch (s.toUpperCase()) {
                case "S":
                case "SMS":
                    return NotificationMethodData.TYPE.SMS;
                case "E":
                case "EMAIL":
                    return NotificationMethodData.TYPE.EMAIL;
                case "F":
                case "FAX":
                    return NotificationMethodData.TYPE.FAX;
            }
        return NotificationMethodData.TYPE.NONE;
    }

    private void populateSelfNotificationMethod(AddBeneficiaryPaymentObject addBeneficiarySuccessObject) {
        myNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = addBeneficiarySuccessObject.getBeneficiaryMethod();
        String methodDetails = "";
        final String myMethodDetails = addBeneficiarySuccessObject.getMyMethodDetails();
        if ("S".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.SMS;
            if (myMethodDetails.startsWith("27")) {
                methodDetails = myMethodDetails.replaceFirst("27", "0");
            }
        } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.EMAIL;
            methodDetails = myMethodDetails;
        } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.FAX;
            methodDetails = myMethodDetails;
            if (methodDetails.startsWith("27")) {
                methodDetails = myMethodDetails.replaceFirst("27", "0");
            }
        }

        myNotificationMethod.setNotificationMethodType(notificationMethodType);
        myNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    private void populateBeneficiaryNotificationMethod(BeneficiaryDetails beneficiaryDetails, String paymentNotificationDetail) {
        beneficiaryNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = beneficiaryDetails.getBeneficiaryNoticeType();

        String methodDetails = "";

        if (paymentNotificationDetail != null) {
            String[] paymentMethodDetails = paymentNotificationDetail.split("; ");
            String beneficiaryContactDetail = paymentMethodDetails[0].trim();

            if (ValidationUtils.isValidEmailAddress(beneficiaryContactDetail)) {
                notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                methodDetails = beneficiaryContactDetail;
            } else if (ValidationUtils.validatePhoneNumberInput(beneficiaryContactDetail)) {
                notificationMethodType = NotificationMethodData.TYPE.SMS;
                methodDetails = beneficiaryContactDetail;
            } else if (ValidationUtils.validatePhoneNumberInput(beneficiaryContactDetail)) {
                notificationMethodType = NotificationMethodData.TYPE.FAX;
                methodDetails = beneficiaryContactDetail;
            }
        } else {
            final String beneficiaryNoticeDetail = beneficiaryDetails.getBeneficiaryNoticeDetail();
            if ("S".equalsIgnoreCase(beneficiaryMethod)) {
                notificationMethodType = NotificationMethodData.TYPE.SMS;
                if (beneficiaryNoticeDetail.startsWith("27")) {
                    methodDetails = beneficiaryNoticeDetail.replaceFirst("27", "0");
                }
            } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
                notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                methodDetails = beneficiaryNoticeDetail;
            } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
                notificationMethodType = NotificationMethodData.TYPE.FAX;
                methodDetails = beneficiaryNoticeDetail;
                if (methodDetails.startsWith("27")) {
                    methodDetails = methodDetails.replaceFirst("27", "0");
                }
            }
        }

        beneficiaryNotificationMethod.setNotificationMethodType(notificationMethodType);
        beneficiaryNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    private void populateSelfNotificationMethod(BeneficiaryDetails beneficiaryDetails, MyNotificationDetails myNotificationDetails) {
        myNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = beneficiaryDetails.getMyNoticeType();

        String methodDetails = "";
        if ("S".equalsIgnoreCase(beneficiaryMethod)) {
            String actualCellphoneNumber = myNotificationDetails.getActualCellphoneNumber();
            if (actualCellphoneNumber != null) {
                notificationMethodType = NotificationMethodData.TYPE.SMS;
                methodDetails = actualCellphoneNumber.replaceFirst("27", "0");
            }
        } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
            List<String> email = myNotificationDetails.getEmail();
            if (email != null) {
                notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                methodDetails = email.get(0);
            }
        } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
            List<FaxDetails> faxDetails = myNotificationDetails.getFaxDetails();
            final String faxCode = faxDetails != null ? faxDetails.get(0).getFaxCode() : "";
            if (faxDetails != null && faxDetails.size() > 0 && faxCode != null) {
                notificationMethodType = NotificationMethodData.TYPE.FAX;
                methodDetails = String.format("%s%s", faxCode.replaceFirst("27", "0"), faxDetails.get(0).getFaxNumber());
            }
        }

        myNotificationMethod.setNotificationMethodType(notificationMethodType);
        myNotificationMethod.setNotificationMethodDetail(methodDetails);

    }

    private void populateSelfNotificationMethod(BeneficiaryDetails beneficiaryDetails, String paymentNotificationDetail) {
        myNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = beneficiaryDetails.getMyNoticeType();
        String methodDetails = "";

        if (paymentNotificationDetail != null) {
            String[] paymentMethodDetails = paymentNotificationDetail.split("; ");
            if (paymentMethodDetails.length > 1) {
                String selfContactDetail = paymentMethodDetails[1];

                if (ValidationUtils.isValidEmailAddress(selfContactDetail)) {
                    notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                    methodDetails = selfContactDetail;
                } else if (ValidationUtils.validatePhoneNumberInput(selfContactDetail)) {
                    notificationMethodType = NotificationMethodData.TYPE.SMS;
                    methodDetails = selfContactDetail;
                } else if (ValidationUtils.validatePhoneNumberInput(selfContactDetail)) {
                    notificationMethodType = NotificationMethodData.TYPE.FAX;
                    methodDetails = selfContactDetail;
                }
            }
        } else {
            final String myNoticeDetail = beneficiaryDetails.getMyNoticeDetail();
            if ("S".equalsIgnoreCase(beneficiaryMethod)) {
                if (myNoticeDetail.startsWith("27")) {
                    methodDetails = myNoticeDetail.replaceFirst("27", "0");
                }
                notificationMethodType = NotificationMethodData.TYPE.SMS;
            } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
                notificationMethodType = NotificationMethodData.TYPE.EMAIL;
                methodDetails = myNoticeDetail;
            } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
                notificationMethodType = NotificationMethodData.TYPE.FAX;
                methodDetails = myNoticeDetail;
                if (methodDetails.startsWith("27")) {
                    methodDetails = methodDetails.replaceFirst("27", "0");
                }
            }
        }

        myNotificationMethod.setNotificationMethodType(notificationMethodType);
        myNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    private void populateBeneficiaryNotificationMethod(BeneficiaryDetailObject beneficiaryDetailObject) {
        beneficiaryNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = beneficiaryDetailObject.getBenNoticeType();

        String methodDetails = "";
        final String benNoticeDetail = beneficiaryDetailObject.getBenNoticeDetail();
        if ("S".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.SMS;
            if (benNoticeDetail.startsWith("27")) {
                methodDetails = benNoticeDetail.replaceFirst("27", "0");
            }
        } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.EMAIL;
            methodDetails = benNoticeDetail;
        } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.FAX;
            methodDetails = benNoticeDetail;
            if (methodDetails.startsWith("27")) {
                methodDetails = methodDetails.replaceFirst("27", "0");
            }
        }

        beneficiaryNotificationMethod.setNotificationMethodType(notificationMethodType);
        beneficiaryNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    private void populateSelfNotificationMethod(BeneficiaryDetailObject beneficiaryDetailObject) {
        myNotificationMethod = new NotificationMethodData();
        NotificationMethodData.TYPE notificationMethodType = NotificationMethodData.TYPE.NONE;
        final String beneficiaryMethod = beneficiaryDetailObject.getMyNoticeType();

        String methodDetails = "";
        if ("S".equalsIgnoreCase(beneficiaryMethod)) {
            final String actualCellNo = beneficiaryDetailObject.getActualCellNo();
            methodDetails = actualCellNo.replaceFirst("27", "0");
            notificationMethodType = NotificationMethodData.TYPE.SMS;
        } else if ("E".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.EMAIL;
            methodDetails = beneficiaryDetailObject.getEmail();
        } else if ("F".equalsIgnoreCase(beneficiaryMethod)) {
            notificationMethodType = NotificationMethodData.TYPE.FAX;
            final String benFaxCode = beneficiaryDetailObject.getBenFaxCode();
            final String faxNumber = beneficiaryDetailObject.getFaxNumber();
            methodDetails = String.format("%s%s", benFaxCode.replaceFirst("27", "0"), faxNumber);
        }

        myNotificationMethod.setNotificationMethodType(notificationMethodType);
        myNotificationMethod.setNotificationMethodDetail(methodDetails);
    }

    private void displayChoiceDialog() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.future_dated_payment_error_message))
                .positiveButton(getString(R.string.continue_title))
                .positiveDismissListener((dialog, which) -> requestForValidation())
                .negativeButton(getString(R.string.cancel))
                .negativeDismissListener((dialog, which) -> finish())
                .build());
    }

    private void requestForValidation() {
        if (addBeneficiarySuccessObject != null) {
            payBeneficiaryConfirmationObject.setBeneficiaryName(addBeneficiarySuccessObject.getBeneficiaryName());
            payBeneficiaryConfirmationObject.setBeneficiaryId(addBeneficiarySuccessObject.getBeneficiaryId());

            if (INTERNAL.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("ABSA");
            } else if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("BILL");
            } else {
                payBeneficiaryConfirmationObject.setBeneficiaryType(addBeneficiarySuccessObject.getBenStatusTyp());
            }

            payBeneficiaryConfirmationObject.setBranchCode(addBeneficiarySuccessObject.getBranchCode() != null ? addBeneficiarySuccessObject.getBranchCode().replaceAll(" ", ""): "");
            payBeneficiaryConfirmationObject.setInstitutionName(addBeneficiarySuccessObject.getBeneficiaryName());
            payBeneficiaryConfirmationObject.setBranchName(addBeneficiarySuccessObject.getBranchName());
            payBeneficiaryConfirmationObject.setBankName(addBeneficiarySuccessObject.getBankName());
            payBeneficiaryConfirmationObject.setAccountNumber(addBeneficiarySuccessObject.getAccountNumber() != null ? addBeneficiarySuccessObject.getAccountNumber().replaceAll(" ", ""): "");
            final String accountType = addBeneficiarySuccessObject.getAccountType();
            this.accountType = (accountType != null && !"null".equalsIgnoreCase(accountType)) ? accountType : "Unknown";
            payBeneficiaryConfirmationObject.setAccountType(this.accountType);

            if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                payBeneficiaryConfirmationObject.setAcctAtInst(addBeneficiarySuccessObject.getAcctAtInst() != null ? addBeneficiarySuccessObject.getAcctAtInst().replaceAll(" ", ""): "");
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setMyMethod(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setBeneficiaryMethod(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setReferenceNumber(addBeneficiarySuccessObject.getAcctAtInst().replaceAll(" ", ""));
                payBeneficiaryConfirmationObject.setBeneficiaryReference(addBeneficiarySuccessObject.getBeneficiaryReference() != null ? addBeneficiarySuccessObject.getBeneficiaryReference().replaceAll(" ", ""): "");

            } else {
                putBeneficiaryNotificationMethodData();
            }

            putMyNotificationMethodData();
        } else if (beneficiaryDetail != null) {
            payBeneficiaryConfirmationObject.setBeneficiaryName(beneficiaryDetail.getBeneficiaryName());
            payBeneficiaryConfirmationObject.setBeneficiaryId(beneficiaryDetail.getBeneficiaryId());
            if (INTERNAL.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("ABSA");
            } else if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("BILL");
            } else {
                payBeneficiaryConfirmationObject.setBeneficiaryType(beneficiaryDetail.getStatus());
            }
            payBeneficiaryConfirmationObject.setBranchCode(beneficiaryDetail.getBranchCode());
            payBeneficiaryConfirmationObject.setBranchName(beneficiaryDetail.getBranch());
            payBeneficiaryConfirmationObject.setBankName(beneficiaryDetail.getBankName());
            final String beneficiaryAcctNo = beneficiaryDetail.getBeneficiaryAcctNo();
            payBeneficiaryConfirmationObject.setAccountNumber(beneficiaryAcctNo.replaceAll(" ", ""));
            final String accountType = beneficiaryDetail.getAccountType();
            this.accountType = !"null".equalsIgnoreCase(accountType) ? accountType : "Unknown";
            payBeneficiaryConfirmationObject.setAccountType(this.accountType);
            if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                final String benAcctNumAtInst = beneficiaryDetail.getBenAcctNumAtInst();
                payBeneficiaryConfirmationObject.setAcctAtInst(benAcctNumAtInst.replaceAll(" ", ""));
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setMyMethod(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setBeneficiaryMethod(BMBConstants.NO);
                final String myReference = beneficiaryDetail.getMyReference();
                payBeneficiaryConfirmationObject.setReferenceNumber(myReference.replaceAll(" ", ""));
                final String benReference = beneficiaryDetail.getBenReference();
                payBeneficiaryConfirmationObject.setBeneficiaryReference(benReference.replaceAll(" ", ""));
            } else {
                putBeneficiaryNotificationMethodData();
            }

            putMyNotificationMethodData();
        } else if (beneficiaryDetails != null) {
            payBeneficiaryConfirmationObject.setBeneficiaryName(beneficiaryDetails.getBeneficiaryName());
            payBeneficiaryConfirmationObject.setBeneficiaryId(beneficiaryDetails.getBeneficiaryId());
            if (INTERNAL.equalsIgnoreCase(beneficiaryDetails.getStatus())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("ABSA");
            } else if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetails.getStatus())) {
                payBeneficiaryConfirmationObject.setBeneficiaryType("BILL");
            } else {
                payBeneficiaryConfirmationObject.setBeneficiaryType(beneficiaryDetails.getStatus());
            }
            payBeneficiaryConfirmationObject.setBranchCode(beneficiaryDetails.getBranchCode());
            payBeneficiaryConfirmationObject.setBranchName(beneficiaryDetails.getBranch());
            payBeneficiaryConfirmationObject.setBankName(beneficiaryDetails.getBankName());
            final String accountNumber = beneficiaryDetails.getAccountNumber();
            payBeneficiaryConfirmationObject.setAccountNumber(accountNumber != null ? accountNumber.replaceAll(" ", "") : "");
            final String accountType = beneficiaryDetails.getAccountType();
            this.accountType = (accountType != null && !"null".equalsIgnoreCase(accountType)) ? accountType : "Unknown";
            payBeneficiaryConfirmationObject.setAccountType(this.accountType);
            if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetails.getStatus())) {
                final String beneficiaryInstitutionAccountNumber = beneficiaryDetails.getBeneficiaryInstitutionAccountNumber();
                payBeneficiaryConfirmationObject.setAcctAtInst(beneficiaryInstitutionAccountNumber != null ? beneficiaryInstitutionAccountNumber.replaceAll(" ", "") : "");
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setMyMethod(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setBeneficiaryMethod(BMBConstants.NO);
                payBeneficiaryConfirmationObject.setReferenceNumber(beneficiaryInstitutionAccountNumber != null ? beneficiaryInstitutionAccountNumber.replaceAll(" ", "") : "");
                payBeneficiaryConfirmationObject.setBeneficiaryReference(beneficiaryInstitutionAccountNumber != null ? beneficiaryInstitutionAccountNumber.replaceAll(" ", "") : "");
            } else {
                putBeneficiaryNotificationMethodData();
            }

            putMyNotificationMethodData();
        }

        payBeneficiaryConfirmationObject.setPaymentDate(paymentDate);
        if (isMultiplePayments) {
            navigateToSelectedBeneficiary(payBeneficiaryConfirmationObject);
        } else if (isIIPChecked) {
            Intent iipIntent = new Intent(this, ImmediateInterbankPaymentActivity.class);
            iipIntent.putExtra(ImmediateInterbankPaymentActivity.PAY_BENEFICIARY_CONFIRMATION_OBJECT, payBeneficiaryConfirmationObject);
            startActivity(iipIntent);
        } else {
            PaymentsInteractor paymentsInteractor = new PaymentsInteractor();
            paymentsInteractor.validatePayment(payBeneficiaryConfirmationObject, nextStepExtendedResponseListener);
        }
    }

    private void navigateToSelectedBeneficiary(PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(BENEFICIARY_TRANSACTION_OBJECT, payBeneficiaryConfirmationObject);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void putBeneficiaryNotificationMethodData() {
        if (beneficiaryNotificationMethod != null && !TextUtils.isEmpty(binding.paymentNotificationView.getSelectedValueUnmasked())) {
            NotificationMethodData.TYPE notificationType = beneficiaryNotificationMethod.getNotificationMethodType();
            if (!NotificationMethodData.TYPE.NONE.equals(notificationType)) {
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(YES);
                payBeneficiaryConfirmationObject.setBeneficiaryMethod(notificationType.name());
                final String notificationMethodDetail = beneficiaryNotificationMethod.getNotificationMethodDetail();
                payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(notificationMethodDetail.replaceAll(" ", ""));
                if (NotificationMethodData.TYPE.FAX.equals(beneficiaryNotificationMethod.getNotificationMethodType())) {
                    payBeneficiaryConfirmationObject.setBenFaxCode(!notificationMethodDetail.isEmpty() ? notificationMethodDetail.substring(0, 3) : "");
                    payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(!notificationMethodDetail.isEmpty() ? notificationMethodDetail.substring(3).replaceAll(" ", "") : "");
                }
            } else {
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(NO);
                payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails("None");
            }
        } else if (beneficiaryDetail != null) {
            payBeneficiaryConfirmationObject.setBeneficiaryNotice(beneficiaryDetail.getBenNotice());
            payBeneficiaryConfirmationObject.setBeneficiaryMethod(getPaymentNotification(beneficiaryDetail.getBenNoticeType()));
            final String benNoticeDetail = beneficiaryDetail.getBenNoticeDetail();
            payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(benNoticeDetail.replaceAll(" ", ""));
            payBeneficiaryConfirmationObject.setBenFaxCode(beneficiaryDetail.getBenFaxCode());
        } else if (addBeneficiarySuccessObject != null) {
            if (NO.equalsIgnoreCase(addBeneficiarySuccessObject.getBeneficiaryNotice())) {
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(NO);
            } else {
                payBeneficiaryConfirmationObject.setBeneficiaryNotice(YES);
            }
            payBeneficiaryConfirmationObject.setBeneficiaryMethod(getPaymentNotification(addBeneficiarySuccessObject.getBeneficiaryNotice()));
            payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(addBeneficiarySuccessObject.getBeneficiaryMethodDetails());
            payBeneficiaryConfirmationObject.setBenFaxCode(addBeneficiarySuccessObject.getBenFaxCode());
        } else if (beneficiaryDetails != null) {
            payBeneficiaryConfirmationObject.setBeneficiaryNotice(beneficiaryDetails.getBeneficiaryNotice());
            final String beneficiaryNoticeType = beneficiaryDetails.getBeneficiaryNoticeType();
            payBeneficiaryConfirmationObject.setBeneficiaryMethod(getPaymentNotification(beneficiaryNoticeType != null ? beneficiaryNoticeType : ""));
            payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(beneficiaryDetails.getBeneficiaryNoticeDetail().replaceAll(" ", ""));
        }
    }

    private void putMyNotificationMethodData() {
        if (myNotificationMethod != null && !TextUtils.isEmpty(binding.paymentNotificationView.getSelectedValueUnmasked())) {
            NotificationMethodData.TYPE notificationType = myNotificationMethod.getNotificationMethodType();
            if (!NotificationMethodData.TYPE.NONE.equals(notificationType)) {
                payBeneficiaryConfirmationObject.setMyNotice(YES);
                payBeneficiaryConfirmationObject.setMyMethod(notificationType.name());
                final String notificationMethodDetail = myNotificationMethod.getNotificationMethodDetail();
                payBeneficiaryConfirmationObject.setMyMethodDetails(notificationMethodDetail.replaceAll(" ", ""));
                if (NotificationMethodData.TYPE.FAX.equals(myNotificationMethod.getNotificationMethodType())) {
                    payBeneficiaryConfirmationObject.setMyFaxCode(!notificationMethodDetail.isEmpty() ? notificationMethodDetail.substring(0, 3) : "");
                    payBeneficiaryConfirmationObject.setMyMethodDetails(!notificationMethodDetail.isEmpty() ? notificationMethodDetail.substring(3) : "");
                }
            } else {
                payBeneficiaryConfirmationObject.setMyNotice(NO);
                payBeneficiaryConfirmationObject.setMyMethodDetails(getString(R.string.notification_method_none));
            }
        } else if (beneficiaryDetail != null) {
            payBeneficiaryConfirmationObject.setMyNotice(beneficiaryDetail.getMyNotice());
            payBeneficiaryConfirmationObject.setMyMethod(getPaymentNotification(beneficiaryDetail.getMyNoticeType()));
            payBeneficiaryConfirmationObject.setMyMethodDetails(beneficiaryDetail.getMyNoticeDetail().replaceAll(" ", ""));
            payBeneficiaryConfirmationObject.setMyFaxCode(beneficiaryDetail.getFaxCode());
        } else if (addBeneficiarySuccessObject != null) {
            payBeneficiaryConfirmationObject.setMyNotice(addBeneficiarySuccessObject.getMyNotice());
            payBeneficiaryConfirmationObject.setMyMethod(getPaymentNotification(addBeneficiarySuccessObject.getMyMethod() != null ? addBeneficiarySuccessObject.getMyMethod(): ""));
            payBeneficiaryConfirmationObject.setMyMethodDetails(addBeneficiarySuccessObject.getMyMethodDetails().replaceAll(" ", ""));
            payBeneficiaryConfirmationObject.setMyFaxCode(addBeneficiarySuccessObject.getMyFaxCode());
        } else if (beneficiaryDetails != null) {
            payBeneficiaryConfirmationObject.setMyNotice(beneficiaryDetails.getMyNotice());
            payBeneficiaryConfirmationObject.setMyMethod(getPaymentNotification(beneficiaryDetails.getMyNoticeType() != null ? beneficiaryDetails.getMyNoticeType() : ""));
            payBeneficiaryConfirmationObject.setMyMethodDetails(beneficiaryDetails.getMyNoticeDetail().replaceAll(" ", ""));
        }
    }

    private boolean checkBeneficiaryAccountInFutureDatedList(List<FutureDatePayment> futureDatePayments) {
        boolean isAccountFound = false;
        boolean accountNumberStartsWithAZero = payBeneficiaryConfirmationObject.getAccountNumber().startsWith("0");
        for (FutureDatePayment futureDatePayment : futureDatePayments) {
            String beneficiaryAccountNumber = futureDatePayment.getBeneficiaryAccountNumber();
            if (beneficiaryAccountNumber != null
                    && (beneficiaryAccountNumber.equals(payBeneficiaryConfirmationObject.getAccountNumber()) ||
                    (accountNumberStartsWithAZero
                            && beneficiaryAccountNumber.equals(payBeneficiaryConfirmationObject.getAccountNumber().substring(1))))) {
                isAccountFound = true;
                break;
            }
        }
        return isAccountFound;
    }

    private void setFromAccount() {
        if (selectedFromAccount != null) {
            payBeneficiaryConfirmationObject.setFromAccountNumber(selectedFromAccount.getAccountNumber());
            payBeneficiaryConfirmationObject.setFromAccountType(selectedFromAccount.getDescription());
            payBeneficiaryConfirmationObject.setMaskedFromAccountNumber(selectedFromAccount.getMaskedAccountNumber());
            binding.availableAmountView.setVisibility(View.VISIBLE);
            String availableString = selectedFromAccount.getAvailableBalanceFormated() + " " + getString(R.string.available).toLowerCase();
            binding.availableAmountView.setText(availableString);
            checkSufficientFund();
        }
    }

    private void populateBeneficiaryData() {
        BeneficiaryListItem beneficiaryListItem = new BeneficiaryListItem();
        if (beneficiaryDetail != null) {
            bankName = beneficiaryDetail.getBankName();
            branchCode = beneficiaryDetail.getBranchCode();
            accountType = beneficiaryDetail.getAccountType();
            beneficiaryListItem.setName(beneficiaryDetail.getBeneficiaryName());
            beneficiaryListItem.setAccountNumber(StringExtensions.toFormattedAccountNumber(beneficiaryDetail.getBankActNo()));
            String lastPaymentDetail = null;
            final ArrayList<TransactionObject> transactions = beneficiaryDetail.getTransactions();
            if (transactions != null && !transactions.isEmpty()) {
                TransactionObject lastSuccessfulTransaction = findLastSuccessfulTransaction(transactions);
                if (lastSuccessfulTransaction != null) {
                    Amount lastTransactionAmount = lastSuccessfulTransaction.getAmount();
                    String lastTransactionDate = lastSuccessfulTransaction.getDate();
                    if (lastTransactionAmount != null) {
                        lastPaymentDetail = getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), getString(R.string.paid), lastTransactionDate);
                    }
                }
                beneficiaryListItem.setLastTransactionDetail(lastPaymentDetail);
            }
            binding.beneficiaryView.setBeneficiary(beneficiaryListItem);
            if (beneficiaryDetail != null && PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                binding.theirReferenceView.setVisibility(View.GONE);
                if (YES.equalsIgnoreCase(beneficiaryDetail.getMyNotice())) {
                    binding.paymentNotificationView.setText(beneficiaryDetail.getMyNoticeDetail());
                    myNotificationMethod.setNotificationMethodDetail(beneficiaryDetail.getMyNoticeDetail());
                }
            } else {
                binding.theirReferenceView.setSelectedValue(beneficiaryDetail != null ? beneficiaryDetail.getBenReference(): "");
                StringBuilder paymentNotificationValue = new StringBuilder();
                if (YES.equalsIgnoreCase(beneficiaryDetail.getBenNotice())) {
                    paymentNotificationValue.append(beneficiaryDetail.getBenNoticeDetail());
                    beneficiaryNotificationMethod.setNotificationMethodDetail(beneficiaryDetail.getBenNoticeDetail());
                }
                if (YES.equalsIgnoreCase(beneficiaryDetail.getMyNotice())) {
                    paymentNotificationValue.append(YES.equalsIgnoreCase(beneficiaryDetail.getBenNotice()) ? "; " : "").append(beneficiaryDetail.getMyNoticeDetail());
                    myNotificationMethod.setNotificationMethodDetail(beneficiaryDetail.getMyNoticeDetail());
                }
                binding.paymentNotificationView.setText(paymentNotificationValue.toString());
            }
            binding.myReferenceView.setSelectedValue(beneficiaryDetail.getMyReference());
        } else if (addBeneficiarySuccessObject != null) {
            bankName = addBeneficiarySuccessObject.getBankName();
            branchCode = addBeneficiarySuccessObject.getBranchCode();
            accountType = addBeneficiarySuccessObject.getAccountType();
            beneficiaryListItem.setName(addBeneficiarySuccessObject.getBeneficiaryName());
            if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                binding.theirReferenceView.setVisibility(View.GONE);
                beneficiaryListItem.setAccountNumber(StringExtensions.toFormattedAccountNumber(addBeneficiarySuccessObject.getAcctAtInst()));
            } else {
                beneficiaryListItem.setAccountNumber(StringExtensions.toFormattedAccountNumber(addBeneficiarySuccessObject.getAccountNumber()));
                binding.theirReferenceView.setSelectedValue(addBeneficiarySuccessObject.getBeneficiaryReference());
                if (!NO.equalsIgnoreCase(addBeneficiarySuccessObject.getBeneficiaryNotice())) {
                    beneficiaryNotificationMethod.setNotificationMethodDetail(addBeneficiarySuccessObject.getBeneficiaryMethodDetails());
                }
                if (YES.equalsIgnoreCase(addBeneficiarySuccessObject.getMyNotice())) {
                    myNotificationMethod.setNotificationMethodDetail(addBeneficiarySuccessObject.getMyMethodDetails());
                }
            }
            binding.beneficiaryView.setBeneficiary(beneficiaryListItem);
            binding.myReferenceView.setSelectedValue(addBeneficiarySuccessObject.getMyReference());
        } else if (beneficiaryDetails != null) {
            bankName = beneficiaryDetails.getBankName();
            branchCode = beneficiaryDetails.getBranchCode();
            accountType = beneficiaryDetails.getAccountType();
            beneficiaryListItem.setName(beneficiaryDetails.getBeneficiaryName());
            beneficiaryListItem.setAccountNumber(StringExtensions.toFormattedAccountNumber(beneficiaryDetails.getAccountNumber()));
            binding.beneficiaryView.setBeneficiary(beneficiaryListItem);
            if (beneficiaryDetails != null && PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(beneficiaryDetails.getStatus())) {
                binding.theirReferenceView.setVisibility(View.GONE);
                if (YES.equalsIgnoreCase(beneficiaryDetails.getMyNotice()))
                    binding.paymentNotificationView.setText(beneficiaryDetails.getMyNoticeDetail());
            } else {
                binding.theirReferenceView.setSelectedValue(beneficiaryDetails != null ? beneficiaryDetails.getBeneficiaryReference(): "");
                StringBuilder paymentNotificationValue = new StringBuilder();
                if (YES.equalsIgnoreCase(beneficiaryDetails.getBeneficiaryNotice())) {
                    paymentNotificationValue.append(beneficiaryDetails.getBeneficiaryNoticeDetail());
                    beneficiaryNotificationMethod.setNotificationMethodDetail(beneficiaryDetails.getBeneficiaryNoticeDetail());
                }
                if (YES.equalsIgnoreCase(beneficiaryDetails.getMyNotice())) {
                    paymentNotificationValue.append(YES.equalsIgnoreCase(beneficiaryDetails.getMyNotice()) ? "; " : "").append(beneficiaryDetails.getMyNoticeDetail());
                    myNotificationMethod.setNotificationMethodDetail(beneficiaryDetails.getMyNoticeDetail());
                }
                binding.paymentNotificationView.setText(paymentNotificationValue.toString());
            }

            if (isMultiplePayments && getIntent().getExtras() != null) {
                String payMentNotificationString = getPayMentNotificationString(beneficiaryDetails.getBeneficiaryId());
                if (payMentNotificationString != null) {
                    binding.paymentNotificationView.setText(payMentNotificationString);
                }
            }

            binding.myReferenceView.setSelectedValue(beneficiaryDetails.getMyReference());
        }

        binding.beneficiaryView.animate()
                .setStartDelay(200)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(400).start();
    }

    private TransactionObject findLastSuccessfulTransaction(ArrayList<TransactionObject> transactions) {
        TransactionObject result = null;
        for (TransactionObject transaction : transactions) {
            if (PaymentsConstants.SUCCESSFUL.equalsIgnoreCase(transaction.getTransactionStatus()) || PaymentsConstants.SUCCESSFUL_AFRIKAANS.equalsIgnoreCase(transaction.getTransactionStatus())) {
                result = transaction;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String getPayMentNotificationString(String beneficiaryId) {
        if (beneficiaryId != null && getIntent().getExtras() != null) {
            HashMap<String, String> map = (HashMap<String, String>) getIntent().getExtras().getSerializable(PAYMENT_NOTIFICATION_MAP);
            if (map != null && map.containsKey(beneficiaryId)) {
                return map.get(beneficiaryId);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void populateViews() {
        nextStepExtendedResponseListener.setView(this);
        futureDatedPaymentListResponseListener.setView(this);

      /*  String stringExtra = getIntent().getStringExtra(MultiplePaymentsSelectedBeneficiariesActivity.AMOUNT_VALUE);*/
     /*   if (stringExtra != null && !stringExtra.equals("0")) {
            binding.amountLargeInputView.setSelectedValue(stringExtra);
        }*/

        SelectorList<StringItem> paymentTypes = new SelectorList<>();
        paymentTypes.add(new StringItem(getResources().getString(R.string.normal_24_48)));

        if (iipStatus || ((beneficiaryDetails != null && "true".equalsIgnoreCase(beneficiaryDetails.getImmediatePaymentAllowed())) || (beneficiaryDetail != null && beneficiaryDetail.getImmediatePaymentAllowed()))) {
            paymentTypes.add(new StringItem(getResources().getString(R.string.iip)));
        }
        paymentTypes.add(new StringItem(getResources().getString(R.string.future_dated_payment)));
        binding.paymentTypeRadioView.setDataSource(paymentTypes, 0);
        binding.paymentTypeRadioView.setSelectedIndex(0);
        binding.paymentDateView.addValueViewTextWatcher(this);
        paymentDate = NOW;
        binding.amountLargeInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.amountLargeInputView.hideError();
                binding.continueToPaymentOverview.setEnabled(validateFields());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSufficientFund();
            }
        });

        final double amount = getIntent().getDoubleExtra(AMOUNT, 0);
        if (amount > 0) {
            binding.amountLargeInputView.setSelectedValue(StringExtensions.removeCurrency(TextFormatUtils.formatBasicAmount(amount)));
        }

        binding.paymentDateView.setOnClickListener(view -> showDatePickerDialog());
        binding.paymentNotificationView.setOnClickListener(view -> showNotificationMethodScreen());

        binding.paymentTypeRadioView.setItemCheckedInterface((index) -> {
            selectedIndex = index;
            if (getString(R.string.normal_24_48).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                payBeneficiaryConfirmationObject.setImmediatePay(BMBConstants.NO);
                binding.paymentDateView.setVisibility(View.GONE);
                paymentDate = NOW;
                isIIPChecked = false;
            } else if (getString(R.string.iip).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments();
                payBeneficiaryConfirmationObject.setImmediatePay(BMBConstants.YES);
                binding.paymentDateView.setVisibility(View.GONE);
                paymentDate = NOW;
                isIIPChecked = true;
            } else if (getString(R.string.future_dated_payment).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                payBeneficiaryConfirmationObject.setImmediatePay(BMBConstants.NO);
                binding.paymentDateView.setVisibility(View.VISIBLE);
                showDatePickerDialog();
                isIIPChecked = false;
            }

            if (TextUtils.isEmpty(binding.paymentDateView.getText())) {
                binding.paymentDateView.setContentDescription(getString(R.string.talkback_payment_future_date_no_date_selected));
            } else {
                binding.paymentDateView.setContentDescription(getString(R.string.talkback_payment_future_date_chosen, binding.paymentDateView.getEditText().getText().toString()));
            }

            binding.continueToPaymentOverview.setEnabled(validateFields());
        });

        if (beneficiaryNotificationMethod == null || TextUtils.isEmpty(beneficiaryNotificationMethod.getNotificationMethodDetail())) {
            if (beneficiaryDetail == null || !BUSINESS_USER.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                beneficiaryNotificationMethod = new NotificationMethodData();
                beneficiaryNotificationMethod.setNotificationMethodDetail(getString(R.string.none));
                beneficiaryNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.NONE);
            }
        }

        if (myNotificationMethod == null || TextUtils.isEmpty(myNotificationMethod.getNotificationMethodDetail())) {
            myNotificationMethod = new NotificationMethodData();
            myNotificationMethod.setNotificationMethodDetail(getString(R.string.none));
            myNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.NONE);
        }

        if (isMultiplePayments) {
            payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(beneficiaryNotificationMethod.getNotificationMethodDetail());
            payBeneficiaryConfirmationObject.setBeneficiaryMethod(beneficiaryNotificationMethod.getNotificationMethodType().toString());

            if (!"BILL".equalsIgnoreCase(payBeneficiaryConfirmationObject.getBeneficiaryType())) {
                payBeneficiaryConfirmationObject.setMyMethod(myNotificationMethod.getNotificationMethodType().toString());
                payBeneficiaryConfirmationObject.setMyMethodDetails(myNotificationMethod.getNotificationMethodDetail());
            }
        }

        if (myNotificationMethod != null && "BILL".equalsIgnoreCase(payBeneficiaryConfirmationObject.getBeneficiaryType())) {
            binding.paymentNotificationView.setSelectedValue(myNotificationMethod.getNotificationMethodDetail());
        } else if (beneficiaryNotificationMethod != null && myNotificationMethod != null && !"BILL".equalsIgnoreCase(payBeneficiaryConfirmationObject.getBeneficiaryType())) {
            binding.paymentNotificationView.setSelectedValue(beneficiaryNotificationMethod.getNotificationMethodDetail() + "; " + myNotificationMethod.getNotificationMethodDetail());
        } else if (beneficiaryNotificationMethod != null) {
            binding.paymentNotificationView.setSelectedValue(beneficiaryNotificationMethod.getNotificationMethodDetail());
        } else if (myNotificationMethod != null) {
            binding.paymentNotificationView.setSelectedValue(myNotificationMethod.getNotificationMethodDetail());
        } else {
            binding.paymentNotificationView.setSelectedValue(getString(R.string.none));
        }

        if (!isMultiplePayments) {
            binding.accountSelectorView.setVisibility(View.VISIBLE);
            binding.accountSelectorView.setSelectedValue(getString(R.string.select_an_acc_from));
        } else {
            binding.accountSelectorView.setVisibility(View.GONE);
          /*  if (stringExtra != null && !stringExtra.equals("0")) {
                binding.continueToPaymentOverview.setEnabled(validateFields());
            } else {
                binding.continueToPaymentOverview.setEnabled(false);
            }*/
            binding.continueToPaymentOverview.setText(R.string.done);
        }

        binding.continueToPaymentOverview.setOnClickListener(this);

        if (isMultiplePayments && payBeneficiaryConfirmationObject != null) {
            final int FUTURE_INDEX = binding.paymentTypeRadioView.getDataSourceCount() < 3 ? 1 : 2;
            if ("Yes".equalsIgnoreCase(payBeneficiaryConfirmationObject.getImmediatePay())) {
                final int IIP_INDEX = 1;
                binding.paymentTypeRadioView.setSelectedIndex(IIP_INDEX);
            } else if (!TextUtils.isEmpty(payBeneficiaryConfirmationObject.getFutureDate())) {
                binding.paymentTypeRadioView.disableItemCheckInterface();
                binding.paymentTypeRadioView.setSelectedIndex(FUTURE_INDEX);
                binding.paymentDateView.setVisibility(View.VISIBLE);
                setPaymentDateView(payBeneficiaryConfirmationObject.getFutureDate());
                binding.paymentTypeRadioView.enableItemCheckInterface();
            }
        }
    }

    private boolean isFutureDate() {
        final int FUTURE_INDEX = binding.paymentTypeRadioView.getDataSourceCount() < 3 ? 1 : 2;
        return binding.paymentTypeRadioView.getSelectedIndex() == FUTURE_INDEX;
    }

    private void setPaymentDateView(String dateString) {
        if (dateString != null) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
            Date newDate = null;
            try {
                newDate = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", BMBApplication.getApplicationLocale());
            if (newDate != null) {
                binding.paymentDateView.setText(displayFormat.format(newDate));
            }
        }
    }

    private void showNotificationMethodScreen() {
        final Intent notificationMethodSelectionIntent = new Intent(this, NotificationMethodSelectionActivity.class);
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.payment_notification_title));
        if (addBeneficiarySuccessObject != null) {
            if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                startNotificationSelectionActivityForSelf(notificationMethodSelectionIntent);
            } else {
                startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent);
            }
        } else if (beneficiaryDetail != null) {
            if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetail.getStatus())) {
                startNotificationSelectionActivityForSelf(notificationMethodSelectionIntent);
            } else {
                startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent);
            }
        } else if (beneficiaryDetails != null) {
            if (BUSINESS_USER.equalsIgnoreCase(beneficiaryDetails.getStatus())) {
                startNotificationSelectionActivityForSelf(notificationMethodSelectionIntent);
            } else {
                startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent);
            }
        } else {
            showGenericErrorMessage();
        }
    }

    private void startNotificationSelectionActivityForSelfAndBeneficiary(Intent notificationMethodSelectionIntent) {
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true);
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD, true);

        if (isMultiplePayments && payBeneficiaryConfirmationObject != null) {
            if (beneficiaryNotificationMethod == null)
                beneficiaryNotificationMethod = new NotificationMethodData();
            if (myNotificationMethod == null)
                myNotificationMethod = new NotificationMethodData();

            if (payBeneficiaryConfirmationObject.getBeneficiaryMethodDetails() != null)
                beneficiaryNotificationMethod.setNotificationMethodDetail(payBeneficiaryConfirmationObject.getBeneficiaryMethodDetails());
            if (payBeneficiaryConfirmationObject.getBeneficiaryMethod() != null)
                beneficiaryNotificationMethod.setNotificationMethodType(toNotificationMethodDataType(payBeneficiaryConfirmationObject.getBeneficiaryMethod()));

            if (payBeneficiaryConfirmationObject.getMyMethodDetails() != null)
                myNotificationMethod.setNotificationMethodDetail(payBeneficiaryConfirmationObject.getMyMethodDetails());
            if (payBeneficiaryConfirmationObject.getMyMethod() != null)
                myNotificationMethod.setNotificationMethodType(toNotificationMethodDataType(payBeneficiaryConfirmationObject.getMyMethod()));
        }

        if (beneficiaryNotificationMethod != null) {
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, beneficiaryNotificationMethod.getNotificationMethodType().name());
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, beneficiaryNotificationMethod.getNotificationMethodDetail());
        }
        if (myNotificationMethod != null) {
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_SELF, myNotificationMethod.getNotificationMethodType().name());
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_SELF, myNotificationMethod.getNotificationMethodDetail());
        }
        startActivityForResult(notificationMethodSelectionIntent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_AND_BENEFICIARY_REQUEST_CODE);
    }

    private void startNotificationSelectionActivityForSelf(Intent notificationMethodSelectionIntent) {
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD, true);
        if (myNotificationMethod != null) {
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_SELF, myNotificationMethod.getNotificationMethodType().name());
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_SELF, myNotificationMethod.getNotificationMethodDetail());
        }
        startActivityForResult(notificationMethodSelectionIntent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(this, R.style.DatePickerDialogTheme, (stopAndReplaceDatePicker, year, month, day) -> {
            calendar.set(year, month, day);
            String dateStr = year + "/" + (++month) + "/" + day;
            final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", BMBApplication.getApplicationLocale());
            Calendar calendarTransaction = Calendar.getInstance();
            String transactionDate = year + "-" + (month) + "-" + day + "-" + formatter.format(calendarTransaction.getTime());
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", BMBApplication.getApplicationLocale());
            Date newDate = null;
            try {
                newDate = simpleDateFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", BMBApplication.getApplicationLocale());

            if (newDate != null) {
                binding.paymentDateView.setText(displayFormat.format(newDate));
            }
            paymentDate = DateUtils.hyphenateDate(transactionDate);
            payBeneficiaryConfirmationObject.setFutureDate(paymentDate);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar nextYear = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        try {
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            datePicker.getDatePicker().setMinDate(tomorrow.getTimeInMillis());
            nextYear.add(Calendar.YEAR, 1);
            datePicker.getDatePicker().setMaxDate(nextYear.getTimeInMillis());
        } catch (IllegalArgumentException e) {
            BMBLogger.d(e);
        }
        datePicker.show();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void fillAccountsView(List<AccountObject> accounts) {
        fromAccounts = new SelectorList<>();
        for (AccountObject accountObject : accounts) {
            fromAccounts.add(new AccountObjectWrapper(accountObject));
        }

        binding.accountSelectorView.setList(fromAccounts, getString(R.string.account_to_pay_from));
        binding.accountSelectorView.setItemSelectionInterface(this::selectAccount);

        if (fromAccounts.size() > 0) {
            selectedFromAccount = fromAccounts.get(0).getAccountObject();
            binding.accountSelectorView.setSelectedIndex(0);
            binding.accountSelectorView.setSelectedValue(fromAccounts.get(0).getFormattedValue());
            setFromAccount();
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    private void selectAccount(int index) {
        if (index > -1 && fromAccounts != null && index < fromAccounts.size()) {
            selectedFromAccount = fromAccounts.get(index).getAccountObject();
            binding.continueToPaymentOverview.setEnabled(validateFields());
            binding.accountSelectorView.setSelectedIndex(index);
            binding.accountSelectorView.setSelectedValue(fromAccounts.get(index).getFormattedValue());
            checkSufficientFund();
            setFromAccount();
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    private void requestFromAccount() {
        PayBeneficiaryPaymentObject responseObj = new PayBeneficiaryPaymentObject();
        List<AccountObject> accountObjects = new AccountInteractor().getFromAccountsFromCache(responseObj, ApplicationFlowType.ACCT_SUMMARY);
        if (accountObjects != null && !accountObjects.isEmpty()) {
            fillAccountsView(accountObjects);
        }
    }

    private void checkSufficientFund() {
        if (selectedFromAccount != null) {
            boolean isFundSufficient = isFundSufficient();
            if (!isFundSufficient) {
                binding.availableAmountView.setError(getString(R.string.amount_exceeds_available));
                AccessibilityUtils.announceErrorFromTextWidget(binding.availableAmountView);
            } else {
                binding.amountLargeInputView.clearError();
            }
        }
    }


    private String getTag() {
        return getClass().getSimpleName();
    }

    private boolean isFundSufficient() {
        BigDecimal availableAmount = selectedFromAccount.getAvailableBalance().getAmountValue();
        BigDecimal enteredAmount = getAmountBigDecimal();
        return availableAmount.compareTo(enteredAmount) >= 0;
    }

    private BigDecimal getAmountBigDecimal() {
        try {
            return BigDecimal.valueOf(getAmountDouble(binding.amountLargeInputView.getSelectedValueUnmasked()));
        } catch (NumberFormatException e) {
            BMBLogger.e(getTag(), e.getMessage());
        }
        return new BigDecimal("0");
    }

    private double getAmountDouble(String amountString) {
        double retValue = 0;
        if (amountString != null)
            try {
                retValue = Double.parseDouble(amountString.replace(getString(R.string.currency), "").replace(" ", "").replace(",", ""));
            } catch (NumberFormatException e) {
                BMBLogger.e(getTag(), e.getMessage());
                retValue = 0;
            }
        return retValue;
    }

    private boolean validateFields() {
        String unmaskedValue = binding.amountLargeInputView.getSelectedValueUnmasked();
        if (selectedFromAccount == null) {
            return false;
        } else {
            if (TextUtils.isEmpty(unmaskedValue)) {
                binding.amountLargeInputView.setError(R.string.enter_amount_to_pay);
                return false;
            }
            BigDecimal enteredAmount;
            try {
                enteredAmount = new BigDecimal(unmaskedValue);
            } catch (NumberFormatException e) {
                BMBLogger.e(PaymentDetailsActivity.class.getName(), e.getMessage());
                return false;
            }
            if ("0.0".equals(unmaskedValue) || "0.00".equals(unmaskedValue) || BigDecimal.ZERO.equals(enteredAmount)) {
                binding.amountLargeInputView.setError(R.string.validation_error_payment_amount);
                binding.amountLargeInputView.showError();
                return false;
            } else if (!isFundSufficient()) {
                binding.amountLargeInputView.setError(R.string.amount_exceeds_available);
                binding.amountLargeInputView.showError();
                return false;
            } else {
                payBeneficiaryConfirmationObject.setTransactionAmount(new Amount(getString(R.string.currency), unmaskedValue));
            }
        }
        if (binding.theirReferenceView.getVisibility() == View.VISIBLE && TextUtils.isEmpty(binding.theirReferenceView.getText())) {
            return false;
        } else {
            payBeneficiaryConfirmationObject.setBeneficiaryReference(binding.theirReferenceView.getText());
        }
        if (TextUtils.isEmpty(binding.myReferenceView.getText())) {
            return false;
        } else {
            payBeneficiaryConfirmationObject.setMyReference(binding.myReferenceView.getText());
        }
        return !getString(R.string.future_dated_payment).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(selectedIndex).getDisplayValue()) || !TextUtils.isEmpty(binding.paymentDateView.getEditText().getText());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        binding.continueToPaymentOverview.setEnabled(validateFields());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String notificationMethodDetail;
            switch (requestCode) {
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_AND_BENEFICIARY_REQUEST_CODE:
                    myNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD);
                    beneficiaryNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD);
                    StringBuilder notificationTypeStringBuilder = new StringBuilder();
                    if (beneficiaryNotificationMethod != null) {
                        notificationMethodDetail = beneficiaryNotificationMethod.getNotificationMethodDetail();
                        payBeneficiaryConfirmationObject.setBeneficiaryMethodDetails(notificationMethodDetail);
                        payBeneficiaryConfirmationObject.setBeneficiaryMethod(beneficiaryNotificationMethod.getNotificationMethodType().toString());
                        notificationTypeStringBuilder.append(notificationMethodDetail.isEmpty() ? getString(R.string.notification_none) : notificationMethodDetail).append("; ");
                    } else {
                        notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
                    }
                    if (myNotificationMethod != null) {
                        notificationMethodDetail = myNotificationMethod.getNotificationMethodDetail();
                        payBeneficiaryConfirmationObject.setMyMethodDetails(notificationMethodDetail);
                        payBeneficiaryConfirmationObject.setMyMethod(myNotificationMethod.getNotificationMethodType().toString());
                        notificationTypeStringBuilder.append(notificationMethodDetail.isEmpty() ? getString(R.string.notification_none) : notificationMethodDetail).append("; ");
                    } else {
                        notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
                    }
                    binding.paymentNotificationView.setText(notificationTypeStringBuilder.substring(0, notificationTypeStringBuilder.length() - 2));
                    binding.continueToPaymentOverview.setEnabled(validateFields());
                    break;
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE:
                    myNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD);
                    notificationTypeStringBuilder = new StringBuilder();
                    if (myNotificationMethod != null) {
                        notificationMethodDetail = myNotificationMethod.getNotificationMethodDetail();
                        payBeneficiaryConfirmationObject.setMyMethodDetails(notificationMethodDetail);
                        payBeneficiaryConfirmationObject.setMyMethod(myNotificationMethod.getNotificationMethodType().toString());
                        notificationTypeStringBuilder.append(notificationMethodDetail.isEmpty() ? getString(R.string.notification_none) : notificationMethodDetail).append("; ");
                    } else {
                        notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
                    }
                    binding.paymentNotificationView.setText(notificationTypeStringBuilder.substring(0, notificationTypeStringBuilder.length() - 2));
                    binding.continueToPaymentOverview.setEnabled(validateFields());
                    break;
            }
        }
    }

    private String getPaymentNotification(String notification) {
        switch (notification) {
            case NOTICE_TYPE_SMS_SHORT:
                return SMS;
            case NOTICE_TYPE_EMAIL_SHORT:
                return EMAIL;
            case NOTICE_TYPE_FAX_SHORT:
                return FAX;
            default:
                return notification;
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        binding.continueToPaymentOverview.setEnabled(validateFields());
    }

    @Override
    public void onClick(View view) {
        if (validateFields()) {
            if (OperatorPermissionUtils.isMainUser() && !isMultiplePayments) {
                DigitalLimitsHelper.Companion.checkPaymentAmount(this, payBeneficiaryConfirmationObject.getTransactionAmount(), isFutureDate());

                DigitalLimitsHelper.digitalLimitState.observe(this, digitalLimitState -> {
                    dismissProgressDialog();
                    if (digitalLimitState == DigitalLimitState.CHANGED || digitalLimitState == DigitalLimitState.UNCHANGED) {
                        requestForValidation();
                    }
                });
            } else {
                requestForValidation();
            }
        }
    }
}