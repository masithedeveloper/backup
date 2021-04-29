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

package com.barclays.absa.banking.rewards.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionAirtime;
import com.barclays.absa.banking.buy.ui.airtime.AirtimeHelper;
import com.barclays.absa.banking.databinding.PrepaidRewardsPurchaseFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;

import java.util.List;

import styleguide.content.Profile;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.ImageUtils;

public class PrepaidRewardsPurchaseFragment extends Fragment {

    private final int SELECT_CONTACT_NO_REQUEST_CODE = 3;
    private PrepaidRewardsPurchaseFragmentBinding binding;
    private BuyAirtimeWithAbsaRewardsView buyAirtimeView;
    private String availableAmount;
    private SelectorList<StringItem> networkProviderList;
    private SelectorList<PrepaidVoucherWrapper> selectorVoucherList;
    private Uri contactUri;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public PrepaidRewardsPurchaseFragment() {
    }

    public static PrepaidRewardsPurchaseFragment newInstance() {
        return new PrepaidRewardsPurchaseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.prepaid_rewards_purchase_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buyAirtimeView = (BuyAirtimeWithAbsaRewardsView) getActivity();
        getData();
        initViews();
        setupTalkBack();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String accountBalance = binding.rewardsAvailableProfileView.getNameTextView().getText().toString();
            binding.mobileNumberNormalInputView.setHintText(null);
            binding.voucherNormalInputView.setHintText(null);
            binding.netWorkOperatorNormalInputView.setHintText(null);
            binding.rewardsAvailableProfileView.getNameTextView().setContentDescription(getString(R.string.talkback_prepaid_rewards_balance_available, AccessibilityUtils.getTalkBackRandValueFromString(accountBalance)));

            binding.mobileNumberNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_rewards_enter_mobile_number));
            binding.netWorkOperatorNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_rewards_select_mobile_operator));
            binding.voucherNormalInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_rewards_select_voucher));

            binding.mobileNumberNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_rewards_enter_mobile_number));
            binding.netWorkOperatorNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_rewards_select_mobile_operator));
            binding.voucherNormalInputView.setContentDescription(getString(R.string.talkback_prepaid_rewards_select_voucher));
            binding.buyButton.setContentDescription(getString(R.string.talkback_prepaid_rewards_buy_button));
        }
    }

    private void getData() {
        availableAmount = getAvailableAmount();
        networkProviderList = getNetworkProviderList();
    }

    public String getAvailableAmount() {
        RedeemRewards rewardsRedeem = getRewardsRedeem();
        String amount = "";
        if (rewardsRedeem != null && !TextUtils.isEmpty(rewardsRedeem.getBalance().getAmount())) {
            amount = rewardsRedeem.getBalance().getCurrency() + " " + rewardsRedeem.getBalance().getAmount();
        }
        return amount;
    }

    private SelectorList<StringItem> getNetworkProviderList() {
        SelectorList<StringItem> networkProviderList = new SelectorList<>();
        RedeemRewards rewardsRedeem = getRewardsRedeem();
        if (rewardsRedeem != null) {
            List<RewardsRedemptionAirtime> networkList = rewardsRedeem.getNetworkList();
            if (networkList != null)
                for (int i = 0; i < networkList.size(); i++) {
                    RewardsRedemptionAirtime redemptionAirtime = networkList.get(i);
                    if (redemptionAirtime != null && redemptionAirtime.getNetworkType() != null)
                        networkProviderList.add(new StringItem(redemptionAirtime.getNetworkType()));
                }
        }
        return networkProviderList;
    }

    private RedeemRewards getRewardsRedeem() {
        return rewardsCacheService.getRewardsRedemption();
    }

    @SuppressWarnings("unchecked")
    private void initViews() {
        binding.buyButton.setEnabled(true);
        populateRewardsAvailableProfileView();
        binding.buyButton.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                if (!AirtimeHelper.validateMobileNumber(activity, binding.mobileNumberNormalInputView)) {
                    return;
                }

                if (TextUtils.isEmpty(binding.netWorkOperatorNormalInputView.getText())) {
                    binding.netWorkOperatorNormalInputView.setError(getString(R.string.select_network_operator));
                    if (AccessibilityUtils.isExploreByTouchEnabled()) {
                        AccessibilityUtils.announceErrorFromTextWidget(binding.netWorkOperatorNormalInputView.getErrorTextView());
                    }
                    return;
                } else {
                    binding.netWorkOperatorNormalInputView.hideError();
                }

                if (TextUtils.isEmpty(binding.voucherNormalInputView.getText())) {
                    String errorMessage = getString(R.string.pleaseEnterValid, getString(R.string.voucher).toLowerCase());
                    binding.voucherNormalInputView.setError(errorMessage);
                    if (AccessibilityUtils.isExploreByTouchEnabled()) {
                        AccessibilityUtils.announceErrorFromTextWidget(binding.netWorkOperatorNormalInputView.getErrorTextView());
                    }
                    return;
                } else {
                    binding.voucherNormalInputView.hideError();
                }

                final String selectedNumber = binding.mobileNumberNormalInputView.getSelectedValueUnmasked();
                if (!TextUtils.isEmpty(selectedNumber)) {
                    buyAirtimeView.setEnterCellphoneNumber(selectedNumber);
                }

                buyAirtimeView.buyAirtimeWithAbsaRewards();
            }
        });

        binding.mobileNumberNormalInputView.setIconImageViewDescription(getString(R.string.talkback_add_contact_icon));
        binding.mobileNumberNormalInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(binding.mobileNumberNormalInputView.getEditText(), R.string.selFrmPhoneBookMsg, getContext(), SELECT_CONTACT_NO_REQUEST_CODE, this));
        AirtimeHelper.setMobileNumberValidation(getActivity(), binding.mobileNumberNormalInputView);

        binding.netWorkOperatorNormalInputView.setList(networkProviderList, getString(R.string.prepaid_select_network_operator));
        binding.netWorkOperatorNormalInputView.setItemSelectionInterface(index -> {
            StringItem networkProviderStringItem = networkProviderList.get(index);
            if (networkProviderStringItem != null) {
                binding.voucherNormalInputView.clear();
                binding.buyButton.setEnabled(true);
                selectNetworkProvider(networkProviderStringItem.getDisplayValue());
            }
        });

        binding.voucherNormalInputView.setCustomOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.netWorkOperatorNormalInputView.getText())) {
                binding.netWorkOperatorNormalInputView.setError(getString(R.string.select_network_operator));
                binding.netWorkOperatorNormalInputView.requestFocus();
                if (AccessibilityUtils.isExploreByTouchEnabled()) {
                    AccessibilityUtils.announceErrorFromTextWidget(binding.netWorkOperatorNormalInputView.getErrorTextView());
                }
            } else {
                binding.voucherNormalInputView.clearCustomOnClickListener();
                binding.voucherNormalInputView.performClick();
            }
        });
        binding.voucherNormalInputView.setItemSelectionInterface(index -> {
            PrepaidVoucherWrapper voucherWrapper = selectorVoucherList.get(index);
            if (voucherWrapper != null) {
                selectVoucher(voucherWrapper.getDisplayValue());
            }
        });
        binding.voucherNormalInputView.setSelected(false);
    }

    private void selectVoucher(String selectedVoucher) {
        binding.buyButton.setEnabled(true);
        binding.mobileNumberNormalInputView.hideError();
        buyAirtimeView.setEnterCellphoneNumber(binding.mobileNumberNormalInputView.getSelectedValueUnmasked());
        buyAirtimeView.setVoucherDetailsName(selectedVoucher);
        buyAirtimeView.setSelectedProvider(binding.netWorkOperatorNormalInputView.getSelectedValue());
        buyAirtimeView.updateVoucherDetails();
        buyAirtimeView.updateSelectedVoucherIndex();
        binding.voucherNormalInputView.setSelectedValue(selectedVoucher);
    }

    private void selectNetworkProvider(String networkProvider) {
        buyAirtimeView.setSelectedProvider(networkProvider);
        buyAirtimeView.updateVoucherDetails();
        populateVoucherList();
        binding.netWorkOperatorNormalInputView.hideError();
        binding.netWorkOperatorNormalInputView.setSelectedValue(networkProvider);
    }

    @SuppressWarnings("unchecked")
    private void populateVoucherList() {
        selectorVoucherList = buyAirtimeView.getProviderVoucherList();
        binding.voucherNormalInputView.setList(selectorVoucherList, getString(R.string.voucher));
    }

    @Override
    public void onResume() {
        super.onResume();
        final String selectedNetworkOperator = binding.netWorkOperatorNormalInputView.getSelectedValue();
        if (!TextUtils.isEmpty(selectedNetworkOperator)) {
            buyAirtimeView.setSelectedProvider(selectedNetworkOperator);
            buyAirtimeView.updateVoucherDetails();
            populateVoucherList();
        }
        final String selectedNumber = binding.mobileNumberNormalInputView.getSelectedValueUnmasked();
        if (!TextUtils.isEmpty(selectedNumber)) {
            buyAirtimeView.setEnterCellphoneNumber(selectedNumber);
        }
    }

    private void populateRewardsAvailableProfileView() {
        Bitmap profileImage = ImageUtils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_rewards);
        Profile profile = new Profile(availableAmount, getString(R.string.prepaid_cash_rewards_available), profileImage);
        binding.rewardsAvailableProfileView.setContentDescription(R.string.rewards_icon);
        if (profile.getName() != null) {
            binding.rewardsAvailableProfileView.getNameTextView().setContentDescription(String.format("%s %s", profile.getName().replace("R", ""), getString(R.string.talkback_currency_rands)));
        }
        binding.rewardsAvailableProfileView.setProfile(profile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_CONTACT_NO_REQUEST_CODE) {
            contactUri = data.getData();
            PermissionHelper.requestContactsReadPermission(BMBApplication.getInstance().getTopMostActivity(), this::readContact);
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
                    PermissionHelper.requestContactsReadPermission(BMBApplication.getInstance().getTopMostActivity(), this::readContact);
                    break;
                default:
                    break;
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(getContext(), contactUri);
        final PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
        binding.mobileNumberNormalInputView.setText(phoneNumbers != null ? phoneNumbers.getMobile() : "");
        Activity activity = BMBApplication.getInstance().getTopMostActivity();
        if (activity != null)
            AirtimeHelper.validateMobileNumber(activity, binding.mobileNumberNormalInputView);
    }
}