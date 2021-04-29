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
import com.barclays.absa.banking.account.ui.AccountObjectWrapper;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
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
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.TextFormatUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.forms.notificationmethodview.NotificationMethodData;

public class OnceOffPaymentDetailsActivity extends BaseActivity implements DialogInterface.OnCancelListener, TextWatcher, View.OnClickListener, AccountsView {

    private ActivityPayHubPaymentDetailsBinding binding;

    private SelectorList<AccountObjectWrapper> fromAccounts;
    private AddBeneficiaryPaymentObject addBeneficiarySuccessObject;
    private final OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject = new OnceOffPaymentConfirmationObject();
    private boolean iipStatus, isIIPChecked;
    private AccountObject selectedFromAccount;
    private String paymentDate = "";
    private String bankName, branchCode, accountType;
    private int selectedIndex;
    private NotificationMethodData beneficiaryNotificationMethod, myNotificationMethod;

    private ExtendedResponseListener<FutureDatedPaymentListResponse> futureDatedPaymentListResponseListener = new ExtendedResponseListener<FutureDatedPaymentListResponse>() {
        @Override
        public void onSuccess(final FutureDatedPaymentListResponse successResponse) {
            dismissProgressDialog();
            List<FutureDatePayment> futureDatePayments = successResponse.getFutureDatePayments();
            if (futureDatePayments != null) {
                boolean isBeneficiaryAccountFoundInList = checkBeneficiaryAccountInFutureDatedList(futureDatePayments);
                if (isBeneficiaryAccountFoundInList) {
                    displayChoiceDialog();
                } else {
                    validateAndPay();
                }
            } else {
                validateAndPay();
            }
        }
    };

    private final ExtendedResponseListener<OnceOffPaymentConfirmationResponse> validateOnceOffPaymentExtendedResponseListener = new ExtendedResponseListener<OnceOffPaymentConfirmationResponse>() {
        @Override
        public void onSuccess(final OnceOffPaymentConfirmationResponse successResponse) {
            getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(() -> {
                dismissProgressDialog();
                Intent intent = new Intent(OnceOffPaymentDetailsActivity.this, PaymentConfirmationActivity.class);
                intent.putExtra(RESULT, successResponse);
                Bundle bundle = new Bundle();
                bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, onceOffPaymentConfirmationObject.getAccountNumber());
                bundle.putString(PaymentsConstants.BANK_NAME, bankName);
                bundle.putString(PaymentsConstants.BRANCH_CODE, branchCode);
                bundle.putString(PaymentsConstants.ACCOUNT_TYPE, accountType);
                intent.putExtras(bundle);
                startActivity(intent);
            });
        }

        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            onFailureResponse(failureResponse);

        }
    };

    private void onFailureResponse(ResponseObject failureResponse) {
        if (getAppCacheService().hasErrorResponse()) {
            checkDeviceState();
        } else {
            GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndShowHomeScreenWithAccountsList();

            Intent intent = new Intent(OnceOffPaymentDetailsActivity.this, GenericResultActivity.class);
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
            intent.putExtra(GenericResultActivity.TOP_BUTTON_SHOULD_RETURN_TO_PREVIOUS_SCREEN, true);
            intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.retry);
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_pay_hub_payment_details, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.payment_details));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(AppConstants.RESULT))
                addBeneficiarySuccessObject = (AddBeneficiaryPaymentObject) getIntent().getSerializableExtra(AppConstants.RESULT);
            iipStatus = extras.getBoolean(PaymentsConstants.IIP_STATUS);
            populateBeneficiaryData();
            if (extras.containsKey(PaymentsConstants.PRE_SELECTED_FROM_ACCOUNT)) {
                selectedFromAccount = (AccountObject) extras.getSerializable(PaymentsConstants.PRE_SELECTED_FROM_ACCOUNT);
                setFromAccount();
            }
        }
        requestFromAccount();
        populateViews();
    }

    private void displayChoiceDialog() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .message(getString(R.string.future_dated_payment_error_message))
                .positiveButton(getString(R.string.continue_title))
                .positiveDismissListener((dialog, which) -> validateAndPay())
                .negativeButton(getString(R.string.cancel))
                .negativeDismissListener((dialog, which) -> finish())
                .build());
    }

    private void requestForValidation() {
        if (addBeneficiarySuccessObject != null) {
            onceOffPaymentConfirmationObject.setBeneficiaryName(addBeneficiarySuccessObject.getBeneficiaryName());

            if (INTERNAL.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                onceOffPaymentConfirmationObject.setBeneficiaryType("ABSA");
            } else if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                onceOffPaymentConfirmationObject.setBeneficiaryType("BILL");
            } else {
                onceOffPaymentConfirmationObject.setBeneficiaryType(addBeneficiarySuccessObject.getBeneficiaryType());
            }

            onceOffPaymentConfirmationObject.setBranchCode(addBeneficiarySuccessObject.getBranchCode());
            onceOffPaymentConfirmationObject.setBranchName(addBeneficiarySuccessObject.getBranchName());
            onceOffPaymentConfirmationObject.setBankName(addBeneficiarySuccessObject.getBankName());
            String accountNumber = addBeneficiarySuccessObject.getAccountNumber();
            if (accountNumber != null) {
                onceOffPaymentConfirmationObject.setAccountNumber(accountNumber.replaceAll(" ", ""));
            }
            onceOffPaymentConfirmationObject.setAccountType(addBeneficiarySuccessObject.getAccountType());
            onceOffPaymentConfirmationObject.setInstCode(addBeneficiarySuccessObject.getInstCode());

            updatePaymentReference();

            if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                onceOffPaymentConfirmationObject.setAcctAtInst(addBeneficiarySuccessObject.getAcctAtInst());
            }
        }
        onceOffPaymentConfirmationObject.setPaymentDate(paymentDate);

        if (BMBConstants.FAX.equals(onceOffPaymentConfirmationObject.getBeneficiaryMethod())) {
            onceOffPaymentConfirmationObject.setBenFaxCode(onceOffPaymentConfirmationObject.getBeneficiaryMethodDetails().substring(0, 3));
            onceOffPaymentConfirmationObject.setBeneficiaryMethodDetails(onceOffPaymentConfirmationObject.getBeneficiaryMethodDetails().substring(3));
        }

        if (BMBConstants.FAX.equals(onceOffPaymentConfirmationObject.getMyMethod())) {
            onceOffPaymentConfirmationObject.setMyFaxCode(onceOffPaymentConfirmationObject.getMyMethodDetails().substring(0, 3));
            onceOffPaymentConfirmationObject.setMyFaxNum(onceOffPaymentConfirmationObject.getMyMethodDetails().substring(3));
            onceOffPaymentConfirmationObject.setMyMethodDetails(onceOffPaymentConfirmationObject.getMyMethodDetails().substring(3));
        }

        if (isIIPChecked) {
            Intent iipIntent = new Intent(this, ImmediateInterbankPaymentActivity.class);
            iipIntent.putExtra(ImmediateInterbankPaymentActivity.IS_ONCE_OFF_FLOW, true);
            iipIntent.putExtra(ImmediateInterbankPaymentActivity.ONCE_OFF_CONFIRMATION_OBJECT, onceOffPaymentConfirmationObject);
            startActivity(iipIntent);
        } else {
            PaymentsInteractor paymentsInteractor = new PaymentsInteractor();
            paymentsInteractor.validateOnceOffPayment(onceOffPaymentConfirmationObject, validateOnceOffPaymentExtendedResponseListener);
        }
    }

    private boolean checkBeneficiaryAccountInFutureDatedList(List<FutureDatePayment> futureDatePayments) {
        boolean isAccountFound = false;
        boolean accountNumberStartsWithAZero = onceOffPaymentConfirmationObject.getAccountNumber().startsWith("0");
        for (FutureDatePayment futureDatePayment : futureDatePayments) {
            String beneficiaryAccountNumber = futureDatePayment.getBeneficiaryAccountNumber();
            if (beneficiaryAccountNumber != null
                    && (beneficiaryAccountNumber.equals(onceOffPaymentConfirmationObject.getAccountNumber()) ||
                    (accountNumberStartsWithAZero
                            && beneficiaryAccountNumber.equals(onceOffPaymentConfirmationObject.getAccountNumber().substring(1))))) {
                isAccountFound = true;
                break;
            }
        }
        return isAccountFound;
    }

    private void setFromAccount() {
        if (selectedFromAccount != null) {
            onceOffPaymentConfirmationObject.setFromAccountNumber(selectedFromAccount.getAccountNumber());
            onceOffPaymentConfirmationObject.setFromAccountType(selectedFromAccount.getAccountType());
            onceOffPaymentConfirmationObject.setMaskedFromAccountNumber(selectedFromAccount.getMaskedAccountNumber());
            binding.availableAmountView.setVisibility(View.VISIBLE);
            String availableString = selectedFromAccount.getAvailableBalanceFormated() + " " + getString(R.string.available).toLowerCase();
            binding.availableAmountView.setText(availableString);
            checkSufficientFund();
        }
    }

    private void populateBeneficiaryData() {
        BeneficiaryListItem beneficiaryListItem = new BeneficiaryListItem();
        if (addBeneficiarySuccessObject != null) {
            bankName = addBeneficiarySuccessObject.getBankName();
            branchCode = addBeneficiarySuccessObject.getBranchCode();
            accountType = addBeneficiarySuccessObject.getAccountType();
            beneficiaryListItem.setName(addBeneficiarySuccessObject.getBeneficiaryName());
            if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                beneficiaryListItem.setAccountNumber(addBeneficiarySuccessObject.getAcctAtInst());
            } else {
                beneficiaryListItem.setAccountNumber(addBeneficiarySuccessObject.getBankAccountNo());
            }

            binding.beneficiaryView.setBeneficiary(beneficiaryListItem);
            if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                binding.theirReferenceView.setVisibility(View.GONE);
                if (!NO.equalsIgnoreCase(addBeneficiarySuccessObject.getMyNotice()))
                    binding.paymentNotificationView.setText(addBeneficiarySuccessObject.getMyMethodDetails());
            } else {
                binding.theirReferenceView.setSelectedValue(addBeneficiarySuccessObject.getBeneficiaryReference());
                StringBuilder paymentNotificationValue = new StringBuilder();
                if (!NO.equalsIgnoreCase(addBeneficiarySuccessObject.getBeneficiaryNotice())) {
                    paymentNotificationValue.append(addBeneficiarySuccessObject.getBeneficiaryMethodDetails());
                    beneficiaryNotificationMethod = new NotificationMethodData();
                    final String beneficiaryMethod = addBeneficiarySuccessObject.getBeneficiaryMethod();
                    beneficiaryNotificationMethod.setNotificationMethodDetail(addBeneficiarySuccessObject.getBeneficiaryMethodDetails());
                    if (BMBConstants.EMAIL.equalsIgnoreCase(beneficiaryMethod)) {
                        beneficiaryNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.EMAIL);
                    } else if (BMBConstants.FAX.equalsIgnoreCase(beneficiaryMethod)) {
                        beneficiaryNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.FAX);
                        paymentNotificationValue.insert(0, addBeneficiarySuccessObject.getBenFaxCode());
                        beneficiaryNotificationMethod.setNotificationMethodDetail(addBeneficiarySuccessObject.getBenFaxCode() + beneficiaryNotificationMethod.getNotificationMethodDetail());
                    } else if (BMBConstants.SMS.equalsIgnoreCase(beneficiaryMethod)) {
                        beneficiaryNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.SMS);
                    }
                }
                if ((!NO.equalsIgnoreCase(addBeneficiarySuccessObject.getMyNotice()) && addBeneficiarySuccessObject.getMyNotice() != null))
                    paymentNotificationValue.append(YES.equalsIgnoreCase(addBeneficiarySuccessObject.getBeneficiaryNotice()) ? "; " : "").append(addBeneficiarySuccessObject.getMyMethodDetails());
                binding.paymentNotificationView.setText(paymentNotificationValue.toString());
            }
            binding.myReferenceView.setSelectedValue(addBeneficiarySuccessObject.getMyReference());
        }
        binding.beneficiaryView.animate()
                .setStartDelay(200)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(400).start();
    }

    @SuppressWarnings("unchecked")
    private void populateViews() {
        validateOnceOffPaymentExtendedResponseListener.setView(this);
        futureDatedPaymentListResponseListener.setView(this);
        binding.accountSelectorView.setSelectedValue(getString(R.string.select_an_acc_from));
        SelectorList<StringItem> paymentTypes = new SelectorList<>();
        paymentTypes.add(new StringItem(getResources().getString(R.string.normal_24_48)));
        if (iipStatus)
            paymentTypes.add(new StringItem(getResources().getString(R.string.iip)));
        paymentTypes.add(new StringItem(getResources().getString(R.string.future_dated_payment)));
        binding.paymentTypeRadioView.setDataSource(paymentTypes, 0);
        binding.paymentTypeRadioView.setSelectedIndex(0);
        binding.theirReferenceView.addValueViewTextWatcher(this);
        binding.myReferenceView.addValueViewTextWatcher(this);
        binding.paymentDateView.addValueViewTextWatcher(this);
        paymentDate = NOW;

        binding.amountLargeInputView.setHintText(TextFormatUtils.formatBasicAmountAsRand(""));
        binding.amountLargeInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.amountLargeInputView.clearError();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSufficientFund();
            }
        });

        binding.paymentDateView.setOnClickListener(view -> showDatePickerDialog());

        onceOffPaymentConfirmationObject.setImmediatePay(BMBConstants.NO);
        binding.paymentTypeRadioView.setItemCheckedInterface(index -> {
            selectedIndex = index;
            if (getString(R.string.normal_24_48).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                onceOffPaymentConfirmationObject.setImmediatePay(BMBConstants.NO);
                binding.paymentDateView.setVisibility(View.GONE);
                paymentDate = NOW;
                isIIPChecked = false;
            } else if (getString(R.string.iip).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                onceOffPaymentConfirmationObject.setImmediatePay(BMBConstants.YES);
                binding.paymentDateView.setVisibility(View.GONE);
                paymentDate = NOW;
                isIIPChecked = true;
            } else if (getString(R.string.future_dated_payment).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(index).getDisplayValue())) {
                onceOffPaymentConfirmationObject.setImmediatePay(BMBConstants.NO);
                binding.paymentDateView.setVisibility(View.VISIBLE);
                showDatePickerDialog();
                isIIPChecked = false;
            }
            validateFields();
        });

        binding.paymentNotificationView.setOnClickListener(view -> showNotificationScreen());
        binding.continueToPaymentOverview.setOnClickListener(this);

        if (beneficiaryNotificationMethod == null || TextUtils.isEmpty(beneficiaryNotificationMethod.getNotificationMethodDetail())) {
            beneficiaryNotificationMethod = new NotificationMethodData();
            beneficiaryNotificationMethod.setNotificationMethodDetail(getString(R.string.none));
            beneficiaryNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.NONE);
        }

        if (myNotificationMethod == null || TextUtils.isEmpty(myNotificationMethod.getNotificationMethodDetail())) {
            myNotificationMethod = new NotificationMethodData();
            myNotificationMethod.setNotificationMethodDetail(getString(R.string.none));
            myNotificationMethod.setNotificationMethodType(NotificationMethodData.TYPE.NONE);
        }

        if (beneficiaryNotificationMethod != null && myNotificationMethod != null && !BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
            binding.paymentNotificationView.setSelectedValue(beneficiaryNotificationMethod.getNotificationMethodDetail() + "; " + myNotificationMethod.getNotificationMethodDetail());
        } else if (beneficiaryNotificationMethod != null) {
            binding.paymentNotificationView.setSelectedValue(beneficiaryNotificationMethod.getNotificationMethodDetail());
        } else if (myNotificationMethod != null) {
            binding.paymentNotificationView.setSelectedValue(myNotificationMethod.getNotificationMethodDetail());
        } else {
            binding.paymentNotificationView.setSelectedValue(getString(R.string.none));
        }
    }

    private void showNotificationScreen() {
        final Intent notificationMethodSelectionIntent = new Intent(this, NotificationMethodSelectionActivity.class);
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.payment_notification_title));
        if (addBeneficiarySuccessObject != null) {
            if (BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
                startNotificationSelectionActivityForSelf(notificationMethodSelectionIntent);
            } else {
                startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent);
            }
        }
    }

    private void startNotificationSelectionActivityForSelfAndBeneficiary(Intent notificationMethodSelectionIntent) {
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true);
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD, true);
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
            onceOffPaymentConfirmationObject.setFutureDate(paymentDate);
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
    public void fillAccountsView(List<AccountObject> accountObjects) {
        fromAccounts = new SelectorList<>();
        for (AccountObject accountObject : accountObjects) {
            fromAccounts.add(new AccountObjectWrapper(accountObject));
        }
        binding.accountSelectorView.setList(fromAccounts, getString(R.string.account_to_pay_from));
        binding.accountSelectorView.setItemSelectionInterface(index -> {
            if (index > -1 && index < fromAccounts.size()) {
                selectedFromAccount = fromAccounts.get(index).getAccountObject();
                validateFields();
                binding.accountSelectorView.setSelectedIndex(index);
                setFromAccount();
            } else {
                showGenericErrorMessageThenFinish();
            }
        });
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
            } else {
                binding.availableAmountView.setError(null);
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
            return new BigDecimal(getAmountDouble(binding.amountLargeInputView.getSelectedValueUnmasked()));
        } catch (NumberFormatException e) {
            BMBLogger.e(getTag(), e.getMessage());
        }
        return new BigDecimal("0");
    }

    private double getAmountDouble(String amountString) {
        double retValue = 0;
        if (amountString != null)
            try {
                retValue = Double.valueOf(amountString.replace(getString(R.string.currency), "").replace(" ", "").replace(",", ""));
            } catch (NumberFormatException e) {
                BMBLogger.e(getTag(), e.getMessage());
                retValue = 0;
            }
        return retValue;
    }

    private boolean validateFields() {
        String unmaskedValue = binding.amountLargeInputView.getSelectedValueUnmasked();
        if (TextUtils.isEmpty(unmaskedValue)) {
            binding.amountLargeInputView.setError(R.string.validation_error_payment_amount);
            return false;
        } else if (selectedFromAccount == null) {
            binding.accountSelectorView.setError(getString(R.string.please_select_account));
            return false;
        } else {
            BigDecimal enteredAmount;
            try {
                enteredAmount = new BigDecimal(unmaskedValue);
            } catch (NumberFormatException e) {
                BMBLogger.e(PaymentDetailsActivity.class.getName(), e.getMessage());
                return false;
            }
            if ("0.0".equals(unmaskedValue) || "0.00".equals(unmaskedValue) || BigDecimal.ZERO.equals(enteredAmount)) {
                binding.amountLargeInputView.setError(R.string.validation_error_payment_amount);
                return false;
            } else if (!isFundSufficient()) {
                binding.amountLargeInputView.setError(R.string.amount_exceeds_available);
                return false;
            } else {
                onceOffPaymentConfirmationObject.setTransactionAmount(new Amount(getString(R.string.currency), unmaskedValue));
            }
        }
        if (binding.theirReferenceView.getVisibility() == View.VISIBLE && TextUtils.isEmpty(binding.theirReferenceView.getText())) {
            return false;
        } else {
            onceOffPaymentConfirmationObject.setBeneficiaryReference(binding.theirReferenceView.getText());
        }
        if (TextUtils.isEmpty(binding.myReferenceView.getText())) {
            return false;
        } else {
            onceOffPaymentConfirmationObject.setMyReference(binding.myReferenceView.getText());
        }
        return !getString(R.string.future_dated_payment).equalsIgnoreCase(binding.paymentTypeRadioView.getSelectedValue(selectedIndex).getDisplayValue()) || !TextUtils.isEmpty(binding.paymentDateView.getEditText().getText());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        validateFields();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        validateFields();
    }

    @Override
    public void onClick(View view) {
        if (validateFields()) {
            DigitalLimitsHelper.Companion.checkPaymentAmount(this, onceOffPaymentConfirmationObject.getTransactionAmount(), isFutureDate());

            DigitalLimitsHelper.digitalLimitState.observe(this, digitalLimitState -> {
                if (digitalLimitState == DigitalLimitState.CANCELLED) {
                    dismissProgressDialog();
                } else if (digitalLimitState == DigitalLimitState.CHANGED || digitalLimitState == DigitalLimitState.UNCHANGED) {
                    requestForValidation();
                }
            });
        }
    }

    private boolean isFutureDate() {
        final int FUTURE_INDEX = binding.paymentTypeRadioView.getDataSourceCount() < 3 ? 1 : 2;
        return binding.paymentTypeRadioView.getSelectedIndex() == FUTURE_INDEX;
    }

    private void validateAndPay() {
        if (validateFields()) {
            requestForValidation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_AND_BENEFICIARY_REQUEST_CODE:
                    myNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD);
                    beneficiaryNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD);
                    updatePaymentReference();
                    StringBuilder notificationTypeStringBuilder;
                    break;
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE:
                    myNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD);
                    notificationTypeStringBuilder = new StringBuilder();
                    if (myNotificationMethod != null && !myNotificationMethod.getNotificationMethodDetail().isEmpty()) {
                        notificationTypeStringBuilder.append(myNotificationMethod.getNotificationMethodDetail().isEmpty() ? getString(R.string.notification_none) : myNotificationMethod.getNotificationMethodDetail()).append("; ");
                    } else {
                        notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
                    }
                    binding.paymentNotificationView.setText(notificationTypeStringBuilder.substring(0, notificationTypeStringBuilder.length() - 2));
                    validateFields();
                    break;
            }
        }
    }

    private void updatePaymentReference() {
        StringBuilder notificationTypeStringBuilder = new StringBuilder();
        if (beneficiaryNotificationMethod != null && beneficiaryNotificationMethod.getNotificationMethodType() != NotificationMethodData.TYPE.NONE) {
            onceOffPaymentConfirmationObject.setBeneficiaryNotice(BMBConstants.YES);
            onceOffPaymentConfirmationObject.setBeneficiaryMethodDetails(beneficiaryNotificationMethod.getNotificationMethodDetail());
            if (NotificationMethodData.TYPE.EMAIL == beneficiaryNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setBeneficiaryMethod(BMBConstants.EMAIL);
            } else if (NotificationMethodData.TYPE.FAX == beneficiaryNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setBeneficiaryMethod(BMBConstants.FAX);
            } else if (NotificationMethodData.TYPE.SMS == beneficiaryNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setBeneficiaryMethod(BMBConstants.SMS);
            }
            notificationTypeStringBuilder.append(beneficiaryNotificationMethod.getNotificationMethodDetail().isEmpty() ? getString(R.string.notification_none) : beneficiaryNotificationMethod.getNotificationMethodDetail()).append("; ");
        } else if (!BUSINESS_USER.equalsIgnoreCase(addBeneficiarySuccessObject.getBenStatusTyp())) {
            onceOffPaymentConfirmationObject.setBeneficiaryNotice(BMBConstants.NO);
            notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
        }

        if (myNotificationMethod != null && myNotificationMethod.getNotificationMethodType() != NotificationMethodData.TYPE.NONE) {
            notificationTypeStringBuilder.append(myNotificationMethod.getNotificationMethodDetail().isEmpty() ? getString(R.string.notification_none) : myNotificationMethod.getNotificationMethodDetail()).append("; ");
            onceOffPaymentConfirmationObject.setMyNotice(BMBConstants.YES);
            onceOffPaymentConfirmationObject.setMyMethodDetails(myNotificationMethod.getNotificationMethodDetail());
            if (NotificationMethodData.TYPE.EMAIL == myNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setMyMethod(BMBConstants.EMAIL);
            } else if (NotificationMethodData.TYPE.FAX == myNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setMyMethod(BMBConstants.FAX);
            } else if (NotificationMethodData.TYPE.SMS == myNotificationMethod.getNotificationMethodType()) {
                onceOffPaymentConfirmationObject.setMyMethod(BMBConstants.SMS);
            }
        } else {
            notificationTypeStringBuilder.append(getString(R.string.notification_none)).append("; ");
            onceOffPaymentConfirmationObject.setMyNotice(BMBConstants.NO);
        }
        binding.paymentNotificationView.setText(notificationTypeStringBuilder.substring(0, notificationTypeStringBuilder.length() - 2));
        validateFields();
    }
}
