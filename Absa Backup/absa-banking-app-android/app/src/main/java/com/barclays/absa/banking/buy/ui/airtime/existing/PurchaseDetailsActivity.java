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

package com.barclays.absa.banking.buy.ui.airtime.existing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.NetworkProvider;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiaryConfirmation;
import com.barclays.absa.banking.boundary.modelHelpers.NetworkProviderHelper;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.ui.airtime.AirtimeBaseActivity;
import com.barclays.absa.banking.buy.ui.airtime.AirtimeHelper;
import com.barclays.absa.banking.buy.ui.airtime.NetworkProviderWrapper;
import com.barclays.absa.banking.databinding.PurchaseDetailsActivityBinding;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.FilterAccountList;

import java.util.List;
import java.util.regex.Pattern;

import styleguide.content.BeneficiaryListItem;
import styleguide.utils.extensions.StringExtensions;

public class PurchaseDetailsActivity extends AirtimeBaseActivity {

    private BeneficiaryDetailObject beneficiaryDetailObject;
    private AirtimeBuyBeneficiaryConfirmation airtimeBuyBeneficiaryConfirmation;
    private AirtimeBuyBeneficiary airtimeBuyBeneficiary;
    private PurchaseDetailsActivityBinding binding;

    public static String ADD_BENEFICIARY_COMPLETE = "addComplete";

    private BeneficiaryListItem listItem;

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(ADD_BENEFICIARY_COMPLETE, false)) {
            loadAccountsAndGoHome();
        } else {
            super.onBackPressed();
        }
    }

    private ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> confirmationExtendedResponseListener = new ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation>() {
        @Override
        public void onSuccess(final AirtimeBuyBeneficiaryConfirmation response) {
            Intent intent = new Intent(PurchaseDetailsActivity.this, PurchaseOverview.class);
            response.setNetworkProvider(selectedNetworkProviderWrapper.getNetworkProvider().getName());
            intent.putExtra(RESULT, response);
            intent.putExtra("Image_Name", beneficiaryDetailObject.getImageName());
            intent.putExtra("FROM_ACTIVITY", getIntent().getStringExtra("FromActivity"));
            intent.putExtra("AirtimeBuyBeneficiaryConfirmation", response);
            startActivity(intent);
            dismissProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeviceProfilingInteractor().notifyTransaction();
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.purchase_details_activity, null, false);
        setContentView(binding.getRoot());

        prepaidTypeNormalInputView = findViewById(R.id.prepaidTypeNormalInputView);
        amountNormalInputView = findViewById(R.id.amountNormalInputView);
        ownAmountNormalInputView = findViewById(R.id.ownAmountNormalInputView);
        fromAccountNormalInputView = findViewById(R.id.fromAccountNormalInputView);
        prepaidLegalInformationTextView = findViewById(R.id.prepaidLegalInformationTextView);

        confirmationExtendedResponseListener.setView(this);
        binding.prepaidPurchaseContinueButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            if (validateAirtimeBuyBeneficiary()) {
                requestConfirmation();
            }
        });

        amountNormalInputView.setCustomOnClickListener(v -> {
            if (TextUtils.isEmpty(fromAccountNormalInputView.getText())) {
                fromAccountNormalInputView.setError(getString(R.string.please_select_account));
            } else if (TextUtils.isEmpty(prepaidTypeNormalInputView.getText())) {
                animate(prepaidTypeNormalInputView, R.anim.shake);
            } else {
                amountNormalInputView.clearCustomOnClickListener();
                amountNormalInputView.performClick();
            }
        });

        beneficiaryDetailObject = new BeneficiaryDetailObject();
        airtimeBuyBeneficiary = (AirtimeBuyBeneficiary) getIntent().getSerializableExtra(AppConstants.RESULT);
        if (airtimeBuyBeneficiary != null) {
            beneficiaryDetailObject = airtimeBuyBeneficiary.getBeneficiaryDetails();
            if (beneficiaryDetailObject != null && airtimeBuyBeneficiary.getNetworkProviders() != null) {
                NetworkProvider networkProvider = NetworkProviderHelper.getNetworkProviderObject(beneficiaryDetailObject.getMyReference(), airtimeBuyBeneficiary.getNetworkProviders());
                selectedNetworkProviderWrapper = new NetworkProviderWrapper(networkProvider);
                updateBeneficiaryDetail();
            } else {
                showGenericErrorMessage();
            }
        }

        setToolBarBack(getString(R.string.purchase_details));
        requestAccounts();
        setupTalkBack();
    }

    @Override
    public void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            super.setupTalkBack();
            prepaidTypeNormalInputView.setHintText(null);
            amountNormalInputView.setHintText(null);
            fromAccountNormalInputView.setHintText(null);
            prepaidTypeNormalInputView.requestFocus();
            String beneficiaryName = binding.prepaidBeneficiaryView.getNameTextView().getText().toString();
            String[] beneficiaryDetails = binding.prepaidBeneficiaryView.getAccountNumberTextView().getText().toString().split(Pattern.quote(" - "));
            String mobileNetwork = beneficiaryDetails[0];
            String mobileNumber = beneficiaryDetails[1];
            binding.prepaidBeneficiaryView.getRoundedImageView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.prepaidBeneficiaryView.getNameTextView().setContentDescription(getString(R.string.talkback_prepaid_purchase_beneficiary_name, beneficiaryName));
            binding.prepaidBeneficiaryView.getAccountNumberTextView().setContentDescription(getString(R.string.talkback_prepaid_purchase_number_with_network, mobileNetwork, mobileNumber));

            prepaidTypeNormalInputView.setOnFocusChangeListener((v, hasFocus) -> {
                if (prepaidTypeNormalInputView.getSelectedValueUnmasked().length() > 0) {
                    final String prepaidType = prepaidTypeNormalInputView.getSelectedValueUnmasked();
                    prepaidTypeNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_selected_prepaid_type, prepaidType));
                    prepaidTypeNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_selected_prepaid_type, prepaidType));
                }
            });

            amountNormalInputView.setOnFocusChangeListener((v, hasFocus) -> {
                if (amountNormalInputView.getSelectedValueUnmasked().length() > 0) {
                    final String amountToPurchase = AccessibilityUtils.getTalkBackRandValueFromString(amountNormalInputView.getSelectedValueUnmasked());
                    amountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_amount_selected, amountToPurchase));
                    amountNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_amount_selected, amountToPurchase));
                }
            });

            fromAccountNormalInputView.setOnFocusChangeListener((v, hasFocus) -> {
                if (fromAccountNormalInputView.getSelectedValueUnmasked().length() > 0) {
                    final String accountSource = fromAccountNormalInputView.getSelectedValueUnmasked();
                    fromAccountNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_purchase_select_from_account, accountSource));
                    fromAccountNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_purchase_select_from_account, accountSource));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            PayBeneficiaryPaymentObject paymentObject = new PayBeneficiaryPaymentObject();
            fromAccountList = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PREPAID);
            paymentObject.setFromAccounts(fromAccountList);
            if (paymentObject.getFromAccounts() != null && paymentObject.getFromAccounts().size() == 1) {
                fromAccount = paymentObject.getFromAccounts().get(0);
                setFromAccount(fromAccount);
            }
        }
    }

    private boolean validateAirtimeBuyBeneficiary() {
        airtimeBuyBeneficiaryConfirmation = new AirtimeBuyBeneficiaryConfirmation();
        if (!TextUtils.isEmpty(binding.prepaidBeneficiaryView.toString())) {
            if (listItem != null && listItem.getAccountNumber() != null && listItem.getName() != null) {
                airtimeBuyBeneficiaryConfirmation.setBeneficiaryName(listItem.getName());
            }
        } else {
            return false;
        }

        if (prepaidType != null) {
            prepaidTypeNormalInputView.hideError();
        } else {
            prepaidTypeNormalInputView.setError(AirtimeHelper.getErrorPleaseEnterValidPrepaidType(this));
            announceError(prepaidTypeNormalInputView.getErrorTextView());
            return false;
        }

        if (fromAccount != null) {
            airtimeBuyBeneficiaryConfirmation.setFromAccountNumber(fromAccount.getAccountNumber() != null ? fromAccount.getAccountNumber() : "");
            airtimeBuyBeneficiaryConfirmation.setDescription(fromAccount.getDescription() != null ? fromAccount.getDescription() : "");
            fromAccountNormalInputView.hideError();
        } else {
            fromAccountNormalInputView.setError(getString(R.string.select_an_acc_from));
            announceError(fromAccountNormalInputView.getErrorTextView());
            return false;
        }

        if (beneficiaryDetailObject != null) {
            airtimeBuyBeneficiaryConfirmation.setCellNumber(beneficiaryDetailObject.getActNo());
            airtimeBuyBeneficiaryConfirmation.setNetworkProviderName(beneficiaryDetailObject.getNetworkProviderName());
            airtimeBuyBeneficiaryConfirmation.setNetworkProviderCode(beneficiaryDetailObject.getNetworkProviderCode());
            airtimeBuyBeneficiaryConfirmation.setBeneficiaryName(beneficiaryDetailObject.getBeneficiaryName());
            airtimeBuyBeneficiaryConfirmation.setBeneficiaryID(beneficiaryDetailObject.getBeneficiaryId());
            airtimeBuyBeneficiaryConfirmation.setVoucherDescription(voucherDescription);
        } else {
            return false;
        }

        if (isOwnAmount()) {
            try {
                EditText enterOwnAmount = ownAmountNormalInputView.getEditText();

                String stringOwnAmount = enterOwnAmount.getText().toString().replace("R", "").replace(" ", "");
                double ownAmount = Double.parseDouble(stringOwnAmount);

                final double minOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMinRechargeAmount());
                final double maxOwnAmount = Double.parseDouble(getSelectedNetworkProvider().getMaxRechargeAmount());

                if (minOwnAmount <= ownAmount && ownAmount <= maxOwnAmount) {
                    this.airtimeBuyBeneficiaryConfirmation.setAmount(new Amount(getString(R.string.currency), stringOwnAmount));
                    ownAmountNormalInputView.hideError();
                } else {
                    return showInvalidOwnAmountError();
                }
                if (fromAccount != null && fromAccount.getAvailableBalance() != null && Double.parseDouble(ownAmountNormalInputView.getSelectedValueUnmasked()) > fromAccount.getAvailableBalance().getAmountDouble()) {
                    ownAmountNormalInputView.setError(String.format("%s %s", getString(R.string.balance_exceeded), getString(R.string.you_have_available, fromAccount.getAvailableBalanceFormated())));
                    announceError(ownAmountNormalInputView.getErrorTextView());
                    return false;
                }
            } catch (NumberFormatException e) {
                return showInvalidOwnAmountError();
            }
        } else {
            if (!TextUtils.isEmpty(amountNormalInputView.getText().trim())) {
                if (amountNormalInputView.getText().contains(getString(R.string.forStr))) {
                    airtimeBuyBeneficiaryConfirmation
                            .setAmount(new Amount(getString(R.string.currency),
                                    String.valueOf(amountNormalInputView.getText()).substring(amountNormalInputView.getText().indexOf("R") + 1, amountNormalInputView.getText().indexOf(getString(R.string.f_Str))).trim()));
                } else {
                    airtimeBuyBeneficiaryConfirmation
                            .setAmount(new Amount(getString(R.string.currency),
                                    String.valueOf(amountNormalInputView.getText()).replace("R ", "").trim()));
                }
                amountNormalInputView.hideError();
            } else {
                amountNormalInputView.setError(AirtimeHelper.getErrorPleaseEnterValidAmount(this));
                announceError(amountNormalInputView.getErrorTextView());
                return false;
            }
        }
        return true;
    }

    private void updateBeneficiaryDetail() {
        if (beneficiaryDetailObject != null) {
            listItem = new BeneficiaryListItem();
            listItem.setName(beneficiaryDetailObject.getBeneficiaryName());
            String name = beneficiaryDetailObject.getNetworkProviderName();
            String number = StringExtensions.toFormattedCellphoneNumber(beneficiaryDetailObject.getActNo());
            listItem.setAccountNumber(String.format("%s - %s", name, number));
            binding.prepaidBeneficiaryView.setBeneficiary(listItem);
            setBeneficiaryImage();
            if (beneficiaryDetailObject.getNetworkProviderName() != null) {
                setPrepaidTypes();
            }
        }
    }

    private void setBeneficiaryImage() {
        if (beneficiaryDetailObject.getHasImage()) {
            AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(this);
            AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO
                    .getBeneficiary(beneficiaryDetailObject.getImageName());
            BMBLogger.d("x-i", "imageName " + beneficiaryDetailObject.getImageName());
            if (null != addBeneficiaryObject) {
                byte[] oldImage = addBeneficiaryObject.getImageData();
                if (oldImage != null) {
                    Bitmap benBitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
                    binding.prepaidBeneficiaryView.setImage(benBitmap);
                }
            }
        }
    }

    @Override
    protected List<NetworkProvider> getNetworkProviders() {
        return airtimeBuyBeneficiary == null ? null : airtimeBuyBeneficiary.getNetworkProviders();
    }

    private void requestConfirmation() {
        airtimeBuyBeneficiaryConfirmation.setNetworkProvider(selectedPrepaidAmountWrapper.getCode());
        String isOwnAmount = String.valueOf(isOwnAmount()).toLowerCase();
        new PrepaidInteractor().buyAirtimeConfirm(getPrepaidType(), isOwnAmount, airtimeBuyBeneficiaryConfirmation, confirmationExtendedResponseListener);
    }

    public void announceError(TextView textView) {
        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceRandValueTextFromView(textView);
        }
    }
}