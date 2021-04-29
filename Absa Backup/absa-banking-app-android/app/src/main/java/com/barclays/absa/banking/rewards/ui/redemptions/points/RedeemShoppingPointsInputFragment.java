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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionPartner;
import com.barclays.absa.banking.databinding.RedeemShoppingPointsInputFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.banking.rewards.services.dto.RewardsRedeemConfirmRequest;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.TextFormatUtils;
import com.barclays.absa.utils.ValidationUtils;

import java.util.List;

import styleguide.forms.SelectorList;

public class RedeemShoppingPointsInputFragment extends AbsaBaseFragment<RedeemShoppingPointsInputFragmentBinding> {

    private final String THANK_U_PARTNER_ID = "136";
    private final double PARTNER_MINIMUM = 10.00;
    private final double THANK_U_PARTNER_MINIMUM = 25.00;

    private RedeemShoppingPointsView shoppingPointsView;
    private RedeemRewards redeemRewards;
    private Uri contactUri;
    private final int SELECT_CONTACT_NO_REQUEST_CODE = 3;
    private final int CELLPHONE_LENGTH = 10;
    private boolean isThankUSelected;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public RedeemShoppingPointsInputFragment() {
    }

    public static RedeemShoppingPointsInputFragment newInstance() {
        return new RedeemShoppingPointsInputFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.redeem_shopping_points_input_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingPointsView = (RedeemShoppingPointsView) getActivity();

        getRewards();
        initViews();
    }

    private void getRewards() {
        if (rewardsCacheService.getRewardsRedemption() != null) {
            redeemRewards = rewardsCacheService.getRewardsRedemption();
        } else {
            showGenericErrorMessage();
        }
    }

    private void initViews() {
        populateRetailPartners();
        binding.mobileNumberNormalInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(binding.mobileNumberNormalInputView.getEditText(), R.string.selFrmPhoneBookMsg, getContext(), SELECT_CONTACT_NO_REQUEST_CODE, this));
        binding.nextButton.setOnClickListener(v -> {
            RedeemPointsInputFields userInput = getUserInput();
            if (isValidAmount()) {
                shoppingPointsView.navigateToRedeemPointsConfirmationFragment(userInput);
            }
        });

        binding.cardNumberNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.cardNumberNormalInputView.hideError();
            }
        });

        binding.retailPartnersNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.retailPartnersNormalInputView.hideError();
                setPointsMinimumAmount();

            }
        });

        binding.conversionAmountNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.conversionAmountNormalInputView.hideError();
            }
        });

        binding.cardNumberNormalInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (TextUtils.isEmpty(binding.retailPartnersNormalInputView.getText())) {
                binding.retailPartnersNormalInputView.setError(getString(R.string.select_retail_partner));
            } else {
                validateFields();
            }
        });

        binding.conversionAmountNormalInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (TextUtils.isEmpty(binding.cardNumberNormalInputView.getText())) {
                binding.cardNumberNormalInputView.setError(getString(R.string.invalid_card_number));
            } else {
                validateFields();
            }
        });

        binding.mobileNumberNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }
        });

        binding.mobileNumberNormalInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (TextUtils.isEmpty(binding.conversionAmountNormalInputView.getText())) {
                binding.conversionAmountNormalInputView.setError(getString(R.string.redeem_rewards_invalid_amount));
            } else {
                validateFields();
            }
        });
    }

    private void setPointsMinimumAmount() {
        String selectedRetailPartner = binding.retailPartnersNormalInputView.getText();
        String selectedPartnerID = getPartnerID(selectedRetailPartner);
        if (THANK_U_PARTNER_ID.equalsIgnoreCase(selectedPartnerID)) {
            binding.conversionAmountNormalInputView.setHintText(String.format(getString(R.string.redeem_amount_minimum_value), TextFormatUtils.formatBasicAmount(THANK_U_PARTNER_MINIMUM)));
            isThankUSelected = true;
        } else {
            binding.conversionAmountNormalInputView.setHintText(String.format(getString(R.string.redeem_amount_minimum_value), TextFormatUtils.formatBasicAmount(PARTNER_MINIMUM)));
            isThankUSelected = false;
        }
    }

    protected boolean isValidAmount() {
        if (ValidationUtils.validateInput(binding.conversionAmountNormalInputView, this.getString(R.string.amount))) {
            Double enteredAmount = Double.parseDouble(getAmountString());
            double minimumAmount = isThankUSelected ? THANK_U_PARTNER_MINIMUM : PARTNER_MINIMUM;
            if (ValidationUtils.validateAmountInput(binding.conversionAmountNormalInputView, this.getString(R.string.amount), enteredAmount, minimumAmount, 1000000000.00)) {
                final Amount balance = redeemRewards.getBalance();
                if (balance != null && enteredAmount > balance.getAmountDouble()) {
                    binding.conversionAmountNormalInputView.setError(String.format("%s %s", getString(R.string.balance_exceeded), getString(R.string.you_have_available, balance.toString())));
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void validateFields() {
        if (!TextUtils.isEmpty(binding.conversionAmountNormalInputView.getText()) &&
                !TextUtils.isEmpty(binding.cardNumberNormalInputView.getText()) &&
                !TextUtils.isEmpty(binding.retailPartnersNormalInputView.getText()) &&
                !TextUtils.isEmpty(binding.mobileNumberNormalInputView.getText()) &&
                binding.mobileNumberNormalInputView.getSelectedValueUnmasked().length() == CELLPHONE_LENGTH) {
            binding.nextButton.setEnabled(true);
            return;
        }
        binding.nextButton.setEnabled(false);
    }

    private RedeemPointsInputFields getUserInput() {
        RedeemPointsInputFields userInput = new RedeemPointsInputFields();
        userInput.setAmount(binding.conversionAmountNormalInputView.getText());
        userInput.setPartnerName(binding.retailPartnersNormalInputView.getText());
        userInput.setPartnerID(getPartnerID(binding.retailPartnersNormalInputView.getText()));
        userInput.setRedeemType(RewardsRedeemConfirmRequest.REDEEM_AS_PARTNER);
        userInput.setCellphoneNumber(binding.mobileNumberNormalInputView.getSelectedValueUnmasked());
        userInput.setCardNumber(binding.cardNumberNormalInputView.getSelectedValueUnmasked());
        userInput.setToAccountList(redeemRewards.getToAccountList());

        return userInput;
    }

    private String getPartnerID(String selectedPartner) {
        String selectedPartnerID = "";
        if (redeemRewards != null && redeemRewards.getPartnerList() != null) {
            for (RewardsRedemptionPartner partner : redeemRewards.getPartnerList()) {
                if (selectedPartner.equals(partner.getPartnerName())) {
                    selectedPartnerID = partner.getPartnerId();
                    break;
                }
            }
        } else {
            showGenericErrorMessage();
        }
        return selectedPartnerID;
    }

    @SuppressWarnings("unchecked")
    private void populateRetailPartners() {
        if (redeemRewards != null && redeemRewards.getPartnerList() != null && !redeemRewards.getPartnerList().isEmpty()) {
            SelectorList<RewardsRedemptionPartner> rewardsPartners = new SelectorList<>();
            List<RewardsRedemptionPartner> rewardsPartnersList = redeemRewards.getPartnerList();
            rewardsPartners.addAll(rewardsPartnersList);
            binding.retailPartnersNormalInputView.setList(rewardsPartners, getString(R.string.select_retail_partner));
            final Amount balance = redeemRewards.getBalance();
            binding.conversionAvailableAmountDescriptionView.setDescription(getString(R.string.redeem_amount_availabe, balance != null ? balance.toString() : "0.00"));
        } else {
            showGenericErrorMessage();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_CONTACT_NO_REQUEST_CODE:
                    contactUri = data.getData();
                    PermissionHelper.requestContactsReadPermission(getActivity(), this::readContact);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.length > 0) {
            int permissionStatus = grantResults[0];
            switch (permissionStatus) {
                case PackageManager.PERMISSION_GRANTED:
                    readContact();
                    break;
                case PackageManager.PERMISSION_DENIED:
                    PermissionHelper.requestContactsReadPermission(getActivity(), this::readContact);
                    break;
                default:
                    break;
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(getContext(), contactUri);
        final PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            binding.mobileNumberNormalInputView.setText(phoneNumbers.getMobile());
        }
    }

    public String getAmountString() {
        return binding.conversionAmountNormalInputView.getSelectedValueUnmasked();
    }

}