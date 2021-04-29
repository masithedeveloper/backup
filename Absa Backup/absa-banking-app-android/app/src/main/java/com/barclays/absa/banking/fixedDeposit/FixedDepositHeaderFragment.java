/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.fixedDeposit;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.databinding.FixedDepositHeaderFragmentBinding;
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositAccountDetailsResponse;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.barclays.absa.banking.transfer.TransferConstants.FROM_ACCOUNT;
import static com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN;

public class FixedDepositHeaderFragment extends AbsaBaseFragment<FixedDepositHeaderFragmentBinding> {

    private AccountObject accountDetail;

    public FixedDepositHeaderFragment() {
    }

    public static FixedDepositHeaderFragment newInstance() {
        Bundle args = new Bundle();
        FixedDepositHeaderFragment fragment = new FixedDepositHeaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fixed_deposit_header_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.transferButton.setOnClickListener(view1 -> navigateToTransferScreen());
        setupTalkBack();
    }

    public void setupFragment(AccountObject accountDetail, FixedDepositAccountDetailsResponse fixedDepositAccountDetailsResponse) {
        this.accountDetail = accountDetail;

        if (accountDetail != null && accountDetail.getCurrentBalance() != null && accountDetail.getCurrentBalance() != null) {
            binding.currentBalanceTextView.setText(accountDetail.getCurrentBalance().toString());
        } else {
            binding.currentBalanceTextView.setText("N/A");
        }

        if (accountDetail != null && accountDetail.getAvailableBalance() != null) {
            if (accountDetail.getAvailableBalance().getAmountDouble() > 0) {
                binding.transferButton.setVisibility(View.VISIBLE);

                String availableBalance = String.format("%s: %s", getString(R.string.available_balance), accountDetail.getAvailableBalance().toString());
                SpannableString availableBalanceSpannable = new SpannableString(availableBalance);
                availableBalanceSpannable.setSpan(new StyleSpan(Typeface.BOLD), availableBalance.indexOf(accountDetail.getAvailableBalance().toString()), availableBalanceSpannable.length(), 0);

                binding.availableBalanceTextView.setText(availableBalanceSpannable);

                Date dateMaturity = DateUtils.parseDate(fixedDepositAccountDetailsResponse.getFixedDeposit().getMaturityDate());
                Calendar calendar = new GregorianCalendar();
                if (dateMaturity != null) {
                    calendar.setTime(dateMaturity);
                }
                calendar.add(Calendar.DAY_OF_MONTH, 5);

                String accountStatus = fixedDepositAccountDetailsResponse.getFixedDeposit().getAccountStatus();
                if ("matured".equalsIgnoreCase(accountStatus) || "verval".equalsIgnoreCase(accountStatus)) {
                    if (new GregorianCalendar().getTimeInMillis() > calendar.getTimeInMillis() && accountDetail.getAvailableBalance().getAmountDouble() < 1000) {
                        binding.automaticReinvestment.setText(R.string.fixed_deposit_insufficient_funds);
                    } else {
                        String formatDate = DateUtils.format(calendar.getTime(), DATE_DISPLAY_PATTERN);
                        String autoReinvestDate = String.format(getString(R.string.fixed_deposit_automatic_reinvest), formatDate);
                        SpannableString reinvestDateSpannable = new SpannableString(autoReinvestDate);
                        reinvestDateSpannable.setSpan(new StyleSpan(Typeface.BOLD), autoReinvestDate.indexOf(formatDate), reinvestDateSpannable.length(), 0);
                        binding.automaticReinvestment.setText(reinvestDateSpannable);
                    }
                } else {
                    binding.automaticReinvestment.setVisibility(View.GONE);
                }
            } else {
                binding.transferButton.setVisibility(View.GONE);
                binding.automaticReinvestment.setVisibility(View.GONE);
            }
        } else {
            binding.transferButton.setVisibility(View.GONE);
            binding.automaticReinvestment.setVisibility(View.GONE);
            binding.availableBalanceTextView.setVisibility(View.GONE);
        }
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            binding.transferButton.setContentDescription(getString(R.string.talkback_credit_transfer));
        }
    }

    private void navigateToTransferScreen() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            if (getArguments() != null) {
                baseActivity.trackButtonClick("Fixed deposit hub - Transfer button");
            }
            Intent transferFundActivity = new Intent(baseActivity, TransferFundsActivity.class);
            transferFundActivity.putExtra(FROM_ACCOUNT, accountDetail);

            startActivity(transferFundActivity);
        }
    }
}