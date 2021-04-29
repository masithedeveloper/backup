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

package com.barclays.absa.banking.newToBank;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSureCheckFragmentBinding;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import styleguide.utils.extensions.StringExtensions;

public class NewToBankSurecheckFragment extends ExtendedFragment<NewToBankSureCheckFragmentBinding> {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int MILLIS_DURATION = 60000;
    private NewToBankView newToBankView;
    private CountDownTimer countDownTimer;
    private CustomerDetails customerDetails;

    public NewToBankSurecheckFragment() {
        // Required empty public constructor
    }

    public static NewToBankSurecheckFragment newInstance() {
        return new NewToBankSurecheckFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_sure_check_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarTitle(getToolbarTitle());

        int totalDuration = MILLIS_DURATION / COUNT_DOWN_INTERVAL;
        binding.countDownCircularView.setDuration(totalDuration);
        binding.countDownCircularView.setDisplayText(String.valueOf(totalDuration));
        customerDetails = newToBankView != null ? newToBankView.getNewToBankTempData().getCustomerDetails() : null;
        setupListeners();
        setupCountdownTimer();
        startCountdownTimer();
    }

    private void setupListeners() {
        newToBankView.trackCurrentFragment(NewToBankConstants.VERIFY_CONTACT_SURECHECK_SCREEN);
        String maskedNumber = StringExtensions.toMaskedCellphoneNumber(customerDetails.getCellphoneNumber());
        binding.sureCheckDisclaimerTextView.setText(getString(R.string.new_to_bank_surecheck_text, maskedNumber));
        binding.resendSureCheckButton.setOnClickListener(v -> {
            if (customerDetails != null) {
                newToBankView.trackCurrentFragment(NewToBankConstants.VERIFY_CONTACT_SURECHECK_RETRY_SCREEN);
                countDownTimer.start();
                binding.resendSureCheckButton.setVisibility(View.GONE);
                binding.remainingApprovalTextView.setVisibility(View.VISIBLE);
                binding.sureCheckDisclaimerTextView.setVisibility(View.VISIBLE);
                newToBankView.resendSecurityNotification(customerDetails.getCellphoneNumber());
                binding.resendSureCheckButton.setEnabled(false);
            } else {
                newToBankView.showGenericErrorMessage();
            }
        });
    }

    public void setupCountdownTimer() {
        countDownTimer = new CountDownTimer(MILLIS_DURATION, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / COUNT_DOWN_INTERVAL);
                updateCountdownCircle(secondsLeft);
            }

            @Override
            public void onFinish() {
                stopTimer();
                displayResendOption();
            }
        };
    }

    public void startCountdownTimer() {
        if (customerDetails != null) {
            countDownTimer.start();
        }
    }

    public void stopTimer() {
        binding.countDownCircularView.setDisplayText("00");
        countDownTimer.cancel();
    }

    public void updateCountdownCircle(int secondsRemaining) {
        String prefix = "";
        if (secondsRemaining < 10) {
            prefix = "0";
        }
        String displayValue = prefix + String.valueOf(secondsRemaining);
        binding.countDownCircularView.setDisplayText(displayValue);
    }

    public void displayResendOption() {
        binding.remainingApprovalTextView.setVisibility(View.GONE);
        binding.sureCheckDisclaimerTextView.setVisibility(View.GONE);
        binding.resendSureCheckButton.setVisibility(View.VISIBLE);
        binding.resendSureCheckButton.setEnabled(true);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_surecheck_verification);
    }
}