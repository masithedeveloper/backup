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
package com.barclays.absa.banking.buy.ui.airtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.NetworkProvider;
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.FilterAccountList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

public abstract class AirtimeBaseActivity extends BaseActivity {
    protected final String NETWORK_PROVIDER_WRAPPER = "networkProvider";
    protected final String MOBILE_NUMBER = "mobileNumber";
    protected final String FROM_ACCOUNT = "fromAccount";
    protected final String PREPAID_TYPE = "prepaidType";
    protected final String AMOUNT_WRAPPER = "amountWrapper";

    protected TextView prepaidLegalInformationTextView;
    private SelectorList<PrepaidAmountWrapper> prepaidAmountWrapperSelectorList;

    protected NormalInputView mobileNetworkInputView, mobileNumberInputView, fromAccountNormalInputView, prepaidTypeNormalInputView, amountNormalInputView, ownAmountNormalInputView;
    protected AccountObject fromAccount;
    protected String prepaidType;
    protected String voucherDescription;
    protected NetworkProviderWrapper selectedNetworkProviderWrapper;
    protected PrepaidAmountWrapper selectedPrepaidAmountWrapper;

    protected ArrayList<AccountObject> fromAccountList = new ArrayList<>();

    protected void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            prepaidTypeNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_select_type));
            prepaidTypeNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_select_type));
            amountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_select_amount));
            amountNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_select_amount));
            fromAccountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_select_from_account));
            fromAccountNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_select_from_account));
            ownAmountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_enter_own_amount));
            ownAmountNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_enter_own_amount));
        }
    }

    protected void requestAccounts() {
        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            ArrayList<AccountObject> accounts = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PREPAID);

            AccountList accountList = new AccountList();
            accountList.setAccountsList(accounts);
            setFromAccountView(accountList);
        } else {
            showGenericErrorMessageThenFinish();
        }
    }

    private void setAmount(int index) {
        setAmount(index, false);
    }

    private void setAmount(int index, boolean ownAmountRequestFocus) {
        if (prepaidAmountWrapperSelectorList != null && !prepaidAmountWrapperSelectorList.isEmpty() && index >= 0 && index < prepaidAmountWrapperSelectorList.size()) {
            selectedPrepaidAmountWrapper = prepaidAmountWrapperSelectorList.get(index);
            amountNormalInputView.setText(selectedPrepaidAmountWrapper.getDisplayValue());
            amountNormalInputView.hideError();

            boolean isOwnAmount = amountNormalInputView.getText().trim().equalsIgnoreCase(OWN);
            if (isOwnAmount && ownAmountRequestFocus) {
                ownAmountNormalInputView.requestFocus();
            }
            ownAmountNormalInputView.setVisibility(isOwnAmount ? View.VISIBLE : View.GONE);
            if (isOwnAmount) {
                ownAmountNormalInputView.requestFocus();
            } else {
                ownAmountNormalInputView.clearFocus();
                validateAmountInputView();
            }

            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                final String amountForAccessibility = AccessibilityUtils.getTalkBackRandValueFromString(amountNormalInputView.getSelectedValueUnmasked());
                amountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_amount_selected, amountForAccessibility));

                if (ownAmountNormalInputView.getVisibility() == View.VISIBLE) {

                    if (ownAmountNormalInputView.getSelectedValue().length() <= 0) {
                        ownAmountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_enter_own_amount));
                    } else {
                        final String ownAmountForAccessibility = AccessibilityUtils.getTalkBackRandValueFromString(ownAmountNormalInputView.getSelectedValueUnmasked());
                        ownAmountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_enter_own_amount_entered, ownAmountForAccessibility));
                    }

                }
            }
        }
    }

    private void validateAmountInputView() {
        if (selectedPrepaidAmountWrapper != null && !getString(R.string.own).equalsIgnoreCase(selectedPrepaidAmountWrapper.getDisplayValue())) {
            String amountValue = selectedPrepaidAmountWrapper.getDisplayValue().split(" ")[1];
            if (!amountValue.isEmpty() && Double.parseDouble(amountValue) > fromAccount.getAvailableBalance().getAmountDouble()) {
                amountNormalInputView.setError(String.format("%s %s", getString(R.string.balance_exceeded), getString(R.string.you_have_available, fromAccount.getAvailableBalanceFormated())));
                return;
            }
        }
        amountNormalInputView.clearError();
        ownAmountNormalInputView.clearError();
    }

    protected void setFromAccount(Object accountInfo) {
        if (accountInfo instanceof AccountObjectWrapper) {
            fromAccount = ((AccountObjectWrapper) accountInfo).getAccountObject();
        } else if (accountInfo instanceof AccountObject) {
            fromAccount = (AccountObject) accountInfo;
        }

        if (fromAccount != null) {
            fromAccountNormalInputView.hideError();
            fromAccountNormalInputView.setText(getFromAccountString());
            fromAccountNormalInputView.setDescription(getString(R.string.account_available_balance, fromAccount.getAvailableBalance().toString()));
            prepaidTypeNormalInputView.setVisibility(View.VISIBLE);

            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                final String fromAccountAccessibility = fromAccountNormalInputView.getSelectedValue();
                fromAccountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_account_chosen, fromAccountAccessibility));
            }
            validateAmountInputView();
        }
    }

    private String getFromAccountString() {
        return fromAccount == null ? "" : fromAccount.getAccountInformation();
    }

    protected boolean showInvalidOwnAmountError() {
        if (getSelectedNetworkProvider() != null && getSelectedNetworkProvider().getMinRechargeAmount() != null && getSelectedNetworkProvider().getMaxRechargeAmount() != null)
            try {
                final double minOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMinRechargeAmount());
                final double maxOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMaxRechargeAmount());
                final String errorMessage = String.format(BMBApplication.getInstance().getString(R.string.own_amount_error_message), (int) minOwnAmount, (int) maxOwnAmount);
                ownAmountNormalInputView.setError(errorMessage);
            } catch (NumberFormatException e) {
                BMBLogger.d(e);
            }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void setFromAccountView(AccountList accountList) {
        if (accountList != null) {
            ArrayList<AccountObject> transactionalAccounts = FilterAccountList.getTransactionalAccounts(accountList.getAccountsList());
            if (transactionalAccounts != null) {
                SelectorList<AccountObjectWrapper> accountObjectWrappers = new SelectorList<>();
                for (AccountObject accountObject : transactionalAccounts) {
                    accountObjectWrappers.add(new AccountObjectWrapper(accountObject));
                }
                fromAccountNormalInputView.setList(accountObjectWrappers, getString(R.string.select_account_toolbar_title));
                fromAccountNormalInputView.setItemSelectionInterface(index -> {
                    if (!accountObjectWrappers.isEmpty() && index < accountObjectWrappers.size()) {
                        setFromAccount(accountObjectWrappers.get(index));
                    }
                });
            } else {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> finish());
            }
        }
    }

    public NetworkProvider getSelectedNetworkProvider() {
        return selectedNetworkProviderWrapper == null ? null : selectedNetworkProviderWrapper.getNetworkProvider();
    }

    public String getPrepaidType() {
        return prepaidTypeNormalInputView.getText().trim();
    }

    private void clearPrepaidType() {
        prepaidType = null;
        prepaidTypeNormalInputView.setText("");
        prepaidTypeNormalInputView.setSelectedIndex(-1);
    }

    private void clearSelectedAmount() {
        selectedPrepaidAmountWrapper = null;
        amountNormalInputView.setText("");
        amountNormalInputView.setSelectedIndex(-1);
        ownAmountNormalInputView.setText("");
        ownAmountNormalInputView.setVisibility(View.GONE);
    }

    protected boolean isOwnAmount() {
        return ownAmountNormalInputView.getVisibility() == View.VISIBLE;
    }

    protected abstract List<NetworkProvider> getNetworkProviders();

    protected void setPrepaidTypes() {
        clearSelectedAmount();
        clearPrepaidType();
        SelectorList<StringItem> prepaidTypeList = new SelectorList<>();
        List<String> contentDescriptions = new ArrayList<>();
        if (getNetworkProviders() != null) {
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

                for (StringItem prepaidType : prepaidTypeList) {
                    contentDescriptions.add(AccessibilityUtils.getRandFromStringItem(prepaidType));
                }

                prepaidTypeNormalInputView.setList(prepaidTypeList, getString(R.string.select_prepaid_type));
                prepaidTypeNormalInputView.setItemSelectionInterface(index -> {
                    if (!prepaidTypeList.isEmpty() && index < prepaidTypeList.size()) {
                        setPrepaidType(prepaidTypeList.get(index));
                    }
                });

                prepaidTypeNormalInputView.setContentDescriptionsForList(contentDescriptions);
            }
        }
    }

    protected String getMobileNumber() {
        return mobileNumberInputView == null ? "" : mobileNumberInputView.getText().replace(" ", "");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(NETWORK_PROVIDER_WRAPPER, selectedNetworkProviderWrapper);
        outState.putString(MOBILE_NUMBER, getMobileNumber());
        outState.putString(PREPAID_TYPE, prepaidType);
        outState.putSerializable(FROM_ACCOUNT, fromAccount);
        outState.putInt(AMOUNT_WRAPPER, amountNormalInputView.getSelectedIndex());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle inState) {
        super.onRestoreInstanceState(inState);

        setNetworkProvider(inState.getSerializable(NETWORK_PROVIDER_WRAPPER));
        setMobileNumber(inState.getString(MOBILE_NUMBER));
        setPrepaidType(inState.getString(PREPAID_TYPE));
        setFromAccount(inState.getSerializable(FROM_ACCOUNT));
        setAmount(inState.getInt(AMOUNT_WRAPPER));
    }

    protected void setMobileNumber(String mobileNumber) {
        if (mobileNumberInputView != null) {
            mobileNumberInputView.setText(mobileNumber);
        }
    }

    protected void setupPrepaidAmountView(List<String> prepaidServerAmounts) {
        prepaidAmountWrapperSelectorList = AirtimeHelper.setupPrepaidAmountView(this, amountNormalInputView, prepaidServerAmounts);
        amountNormalInputView.setItemSelectionInterface(index -> {
            String prepaidDescription = prepaidServerAmounts.get(index);
            int lastIndexOfAfterFor = prepaidDescription.lastIndexOf("for") + 4;
            voucherDescription = prepaidDescription.substring(lastIndexOfAfterFor);
            setAmount(index, true);
        });
    }

    private void updatePrepaidAmountValues() {
        clearSelectedAmount();
        if (getSelectedNetworkProvider() != null) {
            if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.airtime))) {
                setupPrepaidAmountView((getSelectedNetworkProvider().getRechargeAmount()));
                prepaidLegalInformationTextView.setText(R.string.onceoff_airtime_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.smsBundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeSMS());
                prepaidLegalInformationTextView.setText(getString(R.string.onceoff_databundle_message_rebuild).replace(getString(R.string.dataBundles), getString(R.string.smsBundles)));
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.dataBundles_30days))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeDataBundleVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.dataBundles_12months))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeDataMonthlyVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.blackberryBundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeBlackberry());
                prepaidLegalInformationTextView.setText(getString(R.string.onceoff_databundle_message_rebuild).replace(getString(R.string.dataBundles), getString(R.string.blackberryBundles)));
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.telkom_freeme_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeTelkomVoucher());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.whatsapp_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeWhatsappDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.youtube_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeYoutubeDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.twitter_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeTwitterDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            } else if (prepaidTypeNormalInputView.getText().equalsIgnoreCase(getString(R.string.facebook_data_bundles))) {
                setupPrepaidAmountView(getSelectedNetworkProvider().getRechargeFacebookDataBundleVouchers());
                prepaidLegalInformationTextView.setText(R.string.onceoff_databundle_message_rebuild);
            }
        }
    }

    protected void setNetworkProvider(Object networkProviderInfo) {
        if (networkProviderInfo instanceof NetworkProviderWrapper && mobileNetworkInputView != null) {
            this.selectedNetworkProviderWrapper = (NetworkProviderWrapper) networkProviderInfo;
            mobileNetworkInputView.setText(selectedNetworkProviderWrapper.getDisplayValue());
            mobileNetworkInputView.hideError();
            setPrepaidTypes();
            mobileNumberInputView.setVisibility(View.VISIBLE);
            fromAccountNormalInputView.setVisibility(View.VISIBLE);
        }
    }

    protected void setPrepaidType(Object selectedItem) {
        if (selectedItem instanceof StringItem) {
            prepaidType = ((StringItem) selectedItem).getDisplayValue();
        } else if (selectedItem instanceof String) {
            prepaidType = (String) selectedItem;
        }
        if (prepaidType != null) {
            prepaidTypeNormalInputView.setText(prepaidType);
            prepaidTypeNormalInputView.hideError();
            updatePrepaidAmountValues();
            amountNormalInputView.setVisibility(View.VISIBLE);
        }
        validateAmountInputView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS) {
                String phone = AirtimeHelper.getPhone(this, data);
                AirtimeHelper.setMobileNumberInputView(this, mobileNumberInputView, phone);
            }
        }
    }
}