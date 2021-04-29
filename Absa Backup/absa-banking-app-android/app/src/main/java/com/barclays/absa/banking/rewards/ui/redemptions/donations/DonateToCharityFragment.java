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

package com.barclays.absa.banking.rewards.ui.redemptions.donations;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.databinding.DonateToCharityFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.rewards.ui.redemptions.RedeemRewardsContract;
import com.barclays.absa.utils.TextFormatUtils;

import java.util.List;

import styleguide.forms.ItemSelectionInterface;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

public class DonateToCharityFragment extends AbsaBaseFragment<DonateToCharityFragmentBinding> implements RedeemRewardsContract.DonateToCharityView, ItemSelectionInterface {

    private static final Double MINIMUM_AMOUNT = 0.99;
    private boolean isCharityValid;
    private boolean isInputValid;
    private Double inputValue = 0.00;
    private Double currentBalance;
    private String selectedCharityId;
    private List<CharityDataModel> charities;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public DonateToCharityFragment() {
    }

    public static DonateToCharityFragment newInstance() {
        return new DonateToCharityFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.donate_to_charity_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DonateToCharityPresenter donateToCharityPresenter = new DonateToCharityPresenter(this);
        donateToCharityPresenter.fetchListOfCharities();
        setToolBar(getString(R.string.donate_to_charity_toolbar), v -> getActivity().finish());
        binding.selectCharityView.setItemSelectionInterface(this);
        binding.nextButton.setEnabled(false);
        initUiElements();
        validateAmount();
        validateCharity();
    }

    private void initUiElements() {
        binding.nextButton.setOnClickListener(v -> {
            if (getActivity() != null && isAdded()) {
                DonateToCharityDataModel donateToCharityDataModel = new DonateToCharityDataModel();
                donateToCharityDataModel.setAccountNumber(rewardsCacheService.getRewardsAccount().getAccountNumber());
                donateToCharityDataModel.setCharityId(selectedCharityId);
                donateToCharityDataModel.setCharityToDonateTo(binding.selectCharityView.getText());
                donateToCharityDataModel.setDonationAmount(new Amount(inputValue.toString()));
                ((RedeemRewardsActivity) getActivity()).changeFragment(DonateToCharityConfirmationFragment.newInstance(donateToCharityDataModel), this);
            } else {
                showGenericMessageError();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateListOfCharities(@NonNull List<CharityDataModel> charities) {
        this.charities = charities;
        SelectorList<StringItem> listOfCharities = new SelectorList<>();
        for (CharityDataModel charityDataModel : charities) {
            StringItem charityItem = new StringItem();
            charityItem.setItem(charityDataModel.getCharityName());
            listOfCharities.add(charityItem);
        }
        binding.selectCharityView.setList(listOfCharities, getString(R.string.select_charity));
    }

    private void validateAmount() {
        AccountObject rewardsAccount = rewardsCacheService.getRewardsAccount();
        if (rewardsAccount != null && rewardsAccount.getAvailableBalance() != null) {
            currentBalance = rewardsAccount.getAvailableBalance().getAmountValue().doubleValue();
        } else {
            showGenericMessageError();
        }
        binding.amountToDonate.setDescription(getString(R.string.account_available_balance, TextFormatUtils.formatBasicAmount(new Amount(currentBalance.toString()))));
        binding.amountToDonate.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    validateAmountEntered(s.toString());
                }
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void validateCharity() {
        binding.selectCharityView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCharityValid = s.length() > 1;
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void validateInputs() {
        if (isInputValid && isCharityValid) {
            binding.nextButton.setEnabled(true);
        } else {
            binding.nextButton.setEnabled(false);
        }
    }

    @Override
    public void onItemClicked(int index) {
        selectedCharityId = charities.get(index).getCharityId();
    }

    private void validateAmountEntered(String amount) {
        amount = StringExtensions.removeCurrency(amount);
        if (!amount.isEmpty()) {
            inputValue = Double.parseDouble(amount);
        } else {
            inputValue = 0.00;
        }
        if (inputValue < currentBalance &&
                inputValue > MINIMUM_AMOUNT) {
            isInputValid = true;
            binding.amountToDonate.showError(false);
        } else if (inputValue > currentBalance) {
            isInputValid = false;
            binding.amountToDonate.setError(getString(R.string.error_please_enter_valid_amount));
        } else if (inputValue < MINIMUM_AMOUNT) {
            isInputValid = false;
            binding.amountToDonate.setError(getString(R.string.error_more_than_r1));
        } else {
            isInputValid = false;
            showGenericMessageError();
        }
    }

    @Override
    public void navigateToPreviousDevicePasscodeEntryScreen() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.navigateToPreviousDevicePasscodeEntryScreen();
        }
    }
}