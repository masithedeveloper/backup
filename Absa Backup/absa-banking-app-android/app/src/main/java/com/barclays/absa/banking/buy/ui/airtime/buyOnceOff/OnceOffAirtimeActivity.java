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
package com.barclays.absa.banking.buy.ui.airtime.buyOnceOff;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.ui.AccountObjectWrapper;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.NetworkProvider;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOff;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOffConfirmation;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.ui.airtime.AirtimeHelper;
import com.barclays.absa.banking.buy.ui.airtime.NetworkProviderWrapper;
import com.barclays.absa.banking.buy.ui.airtime.PrepaidAmountWrapper;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.FilterAccountList;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

public class OnceOffAirtimeActivity extends BaseActivity implements View.OnClickListener {

    private final String NETWORK_PROVIDER_WRAPPER = "networkProvider";
    private final String MOBILE_NUMBER = "mobileNumber";
    private final String FROM_ACCOUNT = "fromAccount";
    private final String PREPAID_TYPE = "prepaidType";
    private final String AMOUNT_WRAPPER = "amountWrapper";

    private TextView prepaidLegalInformationTextView;
    private Button btnContinue;

    private AirtimeOnceOff airtimeOnceOff;
    private AirtimeOnceOffConfirmation airtimeOnceOffConfirmation;
    private ArrayList<String> networkCodeVal;
    private SelectorList<PrepaidAmountWrapper> prepaidAmountWrapperSelectorList;
    private AccountObject fromAccount;
    private String prepaidType;
    private PrepaidAmountWrapper selectedPrepaidAmountWrapper;
    private Uri contactUri;
    private NetworkProviderWrapper selectedNetworkProviderWrapper;
    private NormalInputView mobileNetworkInputView, mobileNumberInputView, fromAccountInputView, prepaidTypeInputView, amountInputView, ownAmountInputView;

    private ExtendedResponseListener<AirtimeOnceOffConfirmation> confirmExtendedResponseListener = new ExtendedResponseListener<AirtimeOnceOffConfirmation>() {
        @Override
        public void onSuccess(final AirtimeOnceOffConfirmation successResponse) {
            dismissProgressDialog();
            Intent intent = new Intent(OnceOffAirtimeActivity.this, OnceOffAirtimeConfirmActivity.class);
            successResponse.setInstitutionCode(selectedNetworkProviderWrapper.getNetworkProvider().getName());
            intent.putExtra(RESULT, successResponse);
            startActivity(intent);
        }
    };

    public NetworkProvider getSelectedNetworkProvider() {
        return selectedNetworkProviderWrapper == null ? null : selectedNetworkProviderWrapper.getNetworkProvider();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BMBLogger.d("x-t", "onCreate");
        setContentView(R.layout.once_off_prepaid_rebuild);

        initViews();

        setToolBarBack(R.string.buy_once_off_title, v -> handleBackAction());

        airtimeOnceOffConfirmation = new AirtimeOnceOffConfirmation();
        mScreenName = BMBConstants.BUY_ONCEOFF_PREPAID_CONST;
        mSiteSection = BMBConstants.PREPAID_CONST;

        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.BUY_ONCEOFF_PREPAID_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST);
        ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra(AppConstants.RESULT);
        onPopulateView(responseObject);

        btnContinue.setOnClickListener(this);
        requestAccounts();
    }

    private void initViews() {
        confirmExtendedResponseListener.setView(this);

        btnContinue = findViewById(R.id.btnContinue);
        prepaidLegalInformationTextView = findViewById(R.id.onceoffdisclaimerMsg);

        fromAccountInputView = findViewById(R.id.fromAccountInputView);

        mobileNumberInputView = findViewById(R.id.mobileNumberInputView);
        mobileNumberInputView.setIconImageViewDescription(getString(R.string.talkback_add_contact_icon));
        mobileNumberInputView.setImageViewVisibility(View.VISIBLE);

        mobileNumberInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(mobileNumberInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, REQUEST_CODE_MY_REFERENCE_DETAILS, null));

        mobileNumberInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumberInputView.hideError();
            }
        });

        mobileNetworkInputView = findViewById(R.id.mobileNetworkInputView);
        prepaidTypeInputView = findViewById(R.id.prepaidTypeInputView);
        prepaidTypeInputView.setCustomOnClickListener(v -> {
            if (TextUtils.isEmpty(mobileNetworkInputView.getText())) {
                animate(mobileNetworkInputView, R.anim.shake);
                mobileNetworkInputView.requestFocus();
            } else {
                prepaidTypeInputView.clearCustomOnClickListener();
                prepaidTypeInputView.performClick();
            }
        });

        amountInputView = findViewById(R.id.amountInputView);
        ownAmountInputView = findViewById(R.id.ownAmountInputView);
        amountInputView.setCustomOnClickListener(v -> {
            if (TextUtils.isEmpty(prepaidTypeInputView.getText())) {
                animate(prepaidTypeInputView, R.anim.shake);
            } else {
                amountInputView.clearCustomOnClickListener();
                amountInputView.performClick();
            }
        });
    }

    private void setFromAccount(Object accountInfo) {
        if (accountInfo instanceof AccountObjectWrapper) {
            AccountObjectWrapper wrapper = (AccountObjectWrapper) accountInfo;
            fromAccount = wrapper.getAccountObject();
            fromAccountInputView.setText(getFromAccountString());
            prepaidTypeInputView.setVisibility(View.VISIBLE);
        } else if (accountInfo instanceof AccountObject) {
            fromAccount = (AccountObject) accountInfo;
            fromAccountInputView.setText(getFromAccountString());
            prepaidTypeInputView.setVisibility(View.VISIBLE);
        }
    }

    private String getFromAccountString() {
        return fromAccount == null ? "" : fromAccount.getAccountInformation();
    }

    private void setNetworkProvider(Object networkProviderInfo) {
        if (networkProviderInfo instanceof NetworkProviderWrapper) {
            this.selectedNetworkProviderWrapper = (NetworkProviderWrapper) networkProviderInfo;
            mobileNetworkInputView.setText(selectedNetworkProviderWrapper.getDisplayValue());
            mobileNetworkInputView.hideError();
            setPrepaidTypes();
            mobileNumberInputView.setVisibility(View.VISIBLE);
            fromAccountInputView.setVisibility(View.VISIBLE);
        }
    }

    private void setPrepaidType(Object selectedItem) {
        if (selectedItem instanceof StringItem) {
            prepaidType = ((StringItem) selectedItem).getDisplayValue();
        } else if (selectedItem instanceof String) {
            prepaidType = (String) selectedItem;
        }
        if (prepaidType != null) {
            prepaidTypeInputView.setText(prepaidType);
            prepaidTypeInputView.hideError();
            updatePrepaidAmountValues();
            amountInputView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            PayBeneficiaryPaymentObject paymentObject = new PayBeneficiaryPaymentObject();
            AbsaCacheManager.getInstance().getModelForAccounts(paymentObject, ApplicationFlowType.ACCT_SUMMARY);
            if (paymentObject.getFromAccounts() != null && paymentObject.getFromAccounts().size() == 1) {
                fromAccount = paymentObject.getFromAccounts().get(0);
                setFromAccount(fromAccount);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BMBLogger.d("x-t", "onSave " + getSelectedNetworkProvider());
        outState.putSerializable(NETWORK_PROVIDER_WRAPPER, selectedNetworkProviderWrapper);
        outState.putString(MOBILE_NUMBER, getMobileNumber());
        outState.putString(PREPAID_TYPE, prepaidType);
        outState.putSerializable(FROM_ACCOUNT, fromAccount);
        outState.putInt(AMOUNT_WRAPPER, amountInputView.getSelectedIndex());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle inState) {
        super.onRestoreInstanceState(inState);

        setNetworkProvider(inState.getSerializable(NETWORK_PROVIDER_WRAPPER));
        mobileNumberInputView.setText(inState.getString(MOBILE_NUMBER));
        setPrepaidType(inState.getString(PREPAID_TYPE));
        setFromAccount(inState.getSerializable(FROM_ACCOUNT));
        setAmount(inState.getInt(AMOUNT_WRAPPER));
    }

    private void setAmount(int index) {
        setAmount(index, false);
    }

    private void setAmount(int index, boolean ownAmountRequestFocus) {
        if (prepaidAmountWrapperSelectorList != null && !prepaidAmountWrapperSelectorList.isEmpty() && index < prepaidAmountWrapperSelectorList.size() && index > -1) {
            selectedPrepaidAmountWrapper = prepaidAmountWrapperSelectorList.get(index);
            amountInputView.setText(selectedPrepaidAmountWrapper.getDisplayValue());
            amountInputView.hideError();
            boolean isOwnAmount = amountInputView.getText().trim().equalsIgnoreCase(OWN);
            if (isOwnAmount && ownAmountRequestFocus) {
                ownAmountInputView.requestFocus();
            }
            ownAmountInputView.setVisibility(isOwnAmount ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isOwnAmount() {
        return ownAmountInputView.getVisibility() == View.VISIBLE;
    }

    private boolean showInvalidOwnAmountError() {
        try {
            final double minOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMinRechargeAmount());
            final double maxOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMaxRechargeAmount());
            final String errorMessage = String.format(BMBApplication.getInstance().getString(R.string.own_amount_error_message), (int) minOwnAmount, (int) maxOwnAmount);
            ownAmountInputView.setError(errorMessage);
            ownAmountInputView.showError();
            animate(ownAmountInputView, R.anim.shake);
            ownAmountInputView.requestFocus();
        } catch (NumberFormatException e) {
            BMBLogger.d(e);
        }
        return false;
    }

    private String getMobileNumber() {
        return mobileNumberInputView.getText().replace(" ", "");
    }

    private boolean validateOnceOffPurchaseDetails() {
        if (getSelectedNetworkProvider() != null) {
            airtimeOnceOffConfirmation.setInstitutionCode(getSelectedNetworkProvider().getInstitutionCode());
            airtimeOnceOffConfirmation.setNetworkProviderName(getSelectedNetworkProvider().getName());
            mobileNetworkInputView.hideError();
        } else {
            toastShort(R.string.select_network_operator);
            animate(mobileNetworkInputView, R.anim.shake);
            return false;
        }
        if (ValidationUtils.validatePhoneNumberInput(getMobileNumber())) {
            airtimeOnceOffConfirmation.setCellNumber(getMobileNumber());
            mobileNumberInputView.hideError();
        } else {
            String errorMessage = String.format(getString(R.string.invalid_mobile_number), getString(R.string.ben_mobile_tv));
            animate(mobileNumberInputView, R.anim.shake);
            mobileNumberInputView.setError(errorMessage);
            return false;
        }

        if (fromAccount != null) {
            airtimeOnceOffConfirmation.setFromAccount(fromAccount.getAccountNumber());
            airtimeOnceOffConfirmation.setDescription(fromAccount.getDescription());
        } else {
            animate(fromAccountInputView, R.anim.shake);
            Toast.makeText(OnceOffAirtimeActivity.this, getString(R.string.select_an_acc_from), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (prepaidType != null) {
            prepaidTypeInputView.hideError();
        } else {
            animate(prepaidTypeInputView, R.anim.shake);
            prepaidTypeInputView.setError(getString(R.string.select_prepaid_type));
            toastLong(R.string.select_prepaid_type);
            return false;
        }

        if (isOwnAmount()) {
            try {
                EditText enterOwnAmount = ownAmountInputView.getEditText();

                String stringOwnAmount = enterOwnAmount.getText().toString().replace("R", "").replace(" ", "");
                double ownAmount = Double.parseDouble(stringOwnAmount);

                final double minOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMinRechargeAmount());
                final double maxOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMaxRechargeAmount());

                if (minOwnAmount <= ownAmount && ownAmount <= maxOwnAmount) {
                    airtimeOnceOffConfirmation.setAmount(new Amount(getString(R.string.currency), stringOwnAmount));
                    ownAmountInputView.hideError();
                } else {
                    return showInvalidOwnAmountError();
                }
            } catch (NumberFormatException e) {
                return showInvalidOwnAmountError();
            }
        } else {
            if (!TextUtils.isEmpty(amountInputView.getText().trim())) {
                if (amountInputView.getText().contains(getString(R.string.forStr))) {
                    airtimeOnceOffConfirmation
                            .setAmount(new Amount(getString(R.string.currency),
                                    amountInputView.getText().substring(amountInputView.getText().indexOf("R") + 1, amountInputView.getText().indexOf(getString(R.string.f_Str))).trim()));
                } else {
                    airtimeOnceOffConfirmation
                            .setAmount(new Amount(getString(R.string.currency),
                                    amountInputView.getText().replace("R ", "").trim()));
                }
            } else {
                toastShort(R.string.sel_airtime_amount);
                animate(amountInputView, R.anim.shake);
                return false;
            }
        }
        return true;
    }

    private void requestAccounts() {
        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            ArrayList<AccountObject> accounts = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PREPAID);

            AccountList accountList = new AccountList();
            accountList.setAccountsList(accounts);
            setFromAccountView(accountList);
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    @SuppressWarnings("unchecked")
    private void setFromAccountView(AccountList accountList) {
        if (accountList != null) {
            ArrayList<AccountObject> transactionalAccounts = FilterAccountList.getTransactionalAccounts(accountList.getAccountsList());
            if (transactionalAccounts != null) {
                SelectorList<AccountObjectWrapper> accountObjectWrappers = new SelectorList<>();
                for (AccountObject accountObject : transactionalAccounts) {
                    accountObjectWrappers.add(new AccountObjectWrapper(accountObject));
                }
                fromAccountInputView.setList(accountObjectWrappers, getString(R.string.select_account_toolbar_title));
                fromAccountInputView.setItemSelectionInterface(index -> setFromAccount(accountObjectWrappers.get(index)));

            } else {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
            }
        }
    }

    private void onPopulateView(ResponseObject responseObject) {
        airtimeOnceOff = (AirtimeOnceOff) responseObject;
        if (airtimeOnceOff != null && airtimeOnceOff.getNetworkProviders() != null) {
            networkCodeVal = new ArrayList<>();

            for (NetworkProvider networkProvider : airtimeOnceOff.getNetworkProviders()) {
                networkCodeVal.add(networkProvider.getInstitutionCode());
            }

            setNetworkProvidersSelector(airtimeOnceOff.getNetworkProviders());
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    @SuppressWarnings("unchecked")
    private void setNetworkProvidersSelector(List<NetworkProvider> networkProviders) {
        if (networkProviders != null) {
            SelectorList<NetworkProviderWrapper> providers = new SelectorList<>();
            for (int i = 0; i < networkProviders.size(); i++) {
                providers.add(new NetworkProviderWrapper(networkProviders.get(i)));
            }
            mobileNetworkInputView.setList(providers, getString(R.string.select_network_operator));
            mobileNetworkInputView.setItemSelectionInterface(index -> setNetworkProvider(providers.get(index)));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS) {
                contactUri = data.getData();
                PermissionHelper.requestContactsReadPermission(this, this::readContact);
                mobileNumberInputView.clearError();
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(this, contactUri);
        final PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            mobileNumberInputView.setText(phoneNumbers.getMobile());
            AirtimeHelper.validateMobileNumber(this, mobileNumberInputView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value) {
            if (grantResults.length > 0) {
                int permissionStatus = grantResults[0];
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        if (contactUri == null) {
                            CommonUtils.pickPhoneNumber(mobileNumberInputView.getEditText(), REQUEST_CODE_MY_REFERENCE_DETAILS);
                        } else {
                            readContact();
                        }
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        PermissionHelper.requestContactsReadPermission(this, this::readContact);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String getPrepaidType() {
        return prepaidTypeInputView.getText().trim();
    }

    private void clearPrepaidType() {
        prepaidType = null;
        prepaidTypeInputView.setText("");
        prepaidTypeInputView.setSelectedIndex(-1);
    }

    private void clearSelectedAmount() {
        selectedPrepaidAmountWrapper = null;
        amountInputView.setText("");
        amountInputView.setSelectedIndex(-1);
        ownAmountInputView.setText("");
        ownAmountInputView.setVisibility(View.GONE);
    }

    @SuppressWarnings("unchecked")
    private void setPrepaidTypes() {
        clearSelectedAmount();
        clearPrepaidType();
        SelectorList<StringItem> prepaidTypeList = new SelectorList<>();
        if (airtimeOnceOff != null && airtimeOnceOff.getNetworkProviders() != null) {
            if (getSelectedNetworkProvider() != null) {
                if (getSelectedNetworkProvider().getRechargeAmount() != null && !getSelectedNetworkProvider().getRechargeAmount().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.airtime)));
                }
                if (getSelectedNetworkProvider().getRechargeSMS() != null && !getSelectedNetworkProvider().getRechargeSMS().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.smsBundles)));
                }
                if (getSelectedNetworkProvider().getRechargeDataBundleVoucher() != null && !getSelectedNetworkProvider().getRechargeDataBundleVoucher().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.dataBundles_30days)));
                }
                if (getSelectedNetworkProvider().getRechargeDataMonthlyVoucher() != null && !getSelectedNetworkProvider().getRechargeDataMonthlyVoucher().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.dataBundles_12months)));
                }
                if (getSelectedNetworkProvider().getRechargeBlackberry() != null && !getSelectedNetworkProvider().getRechargeBlackberry().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.blackberryBundles)));
                }
                if (getSelectedNetworkProvider().getRechargeTelkomVoucher() != null && !getSelectedNetworkProvider().getRechargeTelkomVoucher().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.telkom_freeme_data_bundles)));
                }
                if (getSelectedNetworkProvider().getRechargeWhatsappDataBundleVouchers() != null && !getSelectedNetworkProvider().getRechargeWhatsappDataBundleVouchers().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.whatsapp_data_bundles)));
                }
                if (getSelectedNetworkProvider().getRechargeYoutubeDataBundleVouchers() != null && !getSelectedNetworkProvider().getRechargeYoutubeDataBundleVouchers().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.youtube_data_bundles)));
                }
                if (getSelectedNetworkProvider().getRechargeTwitterDataBundleVouchers() != null && !getSelectedNetworkProvider().getRechargeTwitterDataBundleVouchers().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.twitter_data_bundles)));
                }
                if (getSelectedNetworkProvider().getRechargeFacebookDataBundleVouchers() != null && !getSelectedNetworkProvider().getRechargeFacebookDataBundleVouchers().isEmpty()) {
                    prepaidTypeList.add(new StringItem(getString(R.string.facebook_data_bundles)));
                }
                prepaidTypeInputView.setList(prepaidTypeList, getString(R.string.select_prepaid_type));
                prepaidTypeInputView.setItemSelectionInterface(index -> {
                    if (!prepaidTypeList.isEmpty() && index < prepaidTypeList.size()) {
                        setPrepaidType(prepaidTypeList.get(index));
                    }
                });
            }
        }
    }

    private void updatePrepaidAmountValues() {
        clearSelectedAmount();
        if (getSelectedNetworkProvider() != null) {
            if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.airtime))) {
                setupPrepaidAmountView((getSelectedNetworkProvider().getRechargeAmount()));
                prepaidLegalInformationTextView.setText(R.string.onceoff_airtime_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.smsBundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeSMS());
                prepaidLegalInformationTextView.setText(getString(R.string.onceoff_databundle_message_rebuild).replace(getString(R.string.dataBundles), getString(R.string.smsBundles)));
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.dataBundles_30days))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeDataBundleVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.dataBundles_12months))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeDataMonthlyVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.blackberryBundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeBlackberry());
                prepaidLegalInformationTextView.setText(getString(R.string.onceoff_databundle_message_rebuild).replace(getString(R.string.dataBundles), getString(R.string.blackberryBundles)));
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.telkom_freeme_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeTelkomVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.whatsapp_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeWhatsappDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.youtube_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeYoutubeDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.twitter_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeTwitterDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeInputView.getText().equalsIgnoreCase(getString(R.string.facebook_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeFacebookDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setupPrepaidAmountView(List<String> prepaidServerAmounts) {
        prepaidAmountWrapperSelectorList = AirtimeHelper.setupPrepaidAmountView(this, amountInputView, prepaidServerAmounts);
        amountInputView.setItemSelectionInterface(index -> {
            String prepaidDescription = prepaidServerAmounts.get(index);
            int lastIndexOfAfterFor = prepaidDescription.lastIndexOf("for") + 4;
            airtimeOnceOffConfirmation.setVoucherDescription(prepaidDescription.substring(lastIndexOfAfterFor));
            setAmount(index, true);
        });
    }

    private void requestConfirmation() {
        String isOwnAmount = String.valueOf(isOwnAmount()).toLowerCase();
        new PrepaidInteractor().onceOffAirtimeConfirm(prepaidType, isOwnAmount, selectedPrepaidAmountWrapper.getCode(), airtimeOnceOffConfirmation, confirmExtendedResponseListener);
    }

    private void handleBackAction() {
        if (fromAccount != null || prepaidType != null || selectedPrepaidAmountWrapper != null || !TextUtils.isEmpty(getMobileNumber()) ||
                !mobileNetworkInputView.getText().equalsIgnoreCase("")) {
            BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                    .message(getString(R.string.prepaid_cancel_msg))
                    .positiveButton(getString(R.string.yes))
                    .negativeButton(getString(R.string.cancel))
                    .positiveDismissListener((dialog, which) -> finish())
                    .build());
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackAction();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinue:
                if (validateOnceOffPurchaseDetails()) {
                    requestConfirmation();
                }
                break;
        }
    }
}