/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.hub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCard;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardAccount;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.databinding.CreditCardHeaderFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.transfer.TransferFundsActivity;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.TextFormatUtils;

public class CreditCardHeaderFragment extends AbsaBaseFragment<CreditCardHeaderFragmentBinding> {
    private static final int ANIMATION_DELAY = 1500;
    private static final int MAX_CURVE_ANGLE = 180;

    private CreditCardGraphAnimation cardSpendIndicatorAnimation;
    private CreditCard creditCard;
    private CreditCardAccount creditCardAccount;
    private int numberOfAccounts;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_header_fragment;
    }

    public CreditCardHeaderFragment() {
    }

    public static CreditCardHeaderFragment newInstance() {
        Bundle args = new Bundle();
        CreditCardHeaderFragment fragment = new CreditCardHeaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    void setupFragment(CreditCardInformation creditCardInformation) {
        creditCard = creditCardInformation.getAccount();
        if (creditCard != null && creditCard.getAvailable() != null) {
            binding.totalTextView.setText(TextFormatUtils.spaceFormattedAmount(creditCard.getAvailable().getCurrency(), creditCard.getAvailable().getAmount()));
        } else {
            binding.totalTextView.setText("N/A");
        }

        if (creditCard.getBalance() != null) {
            Amount balance = creditCard.getBalance();
            if (!balance.toString().contains("-")) {
                binding.currentBalanceTextView.setText(balance.toString());
            } else {
                binding.currentBalanceTextView.setText(balance.toString());
            }
        } else {
            binding.currentBalanceTextView.setText("N/A");
        }
        showMinimumPaymentIfNeeded();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            final String availableToSpend = AccessibilityUtils.getTalkBackRandValueFromString(binding.totalTextView.getText().toString());
            final String currentBalance = AccessibilityUtils.getTalkBackRandValueFromString(binding.currentBalanceTextView.getText().toString());
            final String amountDue = AccessibilityUtils.getTalkBackRandValueFromString(binding.amountDueTextView.getText().toString());
            final String dueDate = binding.dateDueTextView.getText().toString();
            binding.amountDueTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.cardGraph.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.dateDueTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.currentBalanceTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.sendTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            binding.transferButton.setContentDescription(getString(R.string.talkback_credit_transfer));
            binding.totalTextView.requestFocus();
            binding.totalTextView.setContentDescription(getString(R.string.talkback_credit_account_information_overview, availableToSpend, currentBalance, amountDue, dueDate));
        }
    }

    private void animateCreditCardInfo() {
        cardSpendIndicatorAnimation = new CreditCardGraphAnimation(binding.cardGraph, calculateGraphAngle());
        cardSpendIndicatorAnimation.setDuration(ANIMATION_DELAY);
        binding.cardGraph.startAnimation(cardSpendIndicatorAnimation);
    }

    void animateCreditCardInfoAgain() {
        cardSpendIndicatorAnimation = new CreditCardGraphAnimation(binding.cardGraph, 180, calculateGraphAngle());
        cardSpendIndicatorAnimation.setDuration(ANIMATION_DELAY);
        binding.cardGraph.startAnimation(cardSpendIndicatorAnimation);
    }

    private int calculateGraphAngle() {
        if (creditCardAccount != null && creditCardAccount.getAvailable() != null && creditCardAccount.getCreditLimit() != null) {
            double amount = Double.parseDouble(creditCardAccount.getAvailable().getAmount().replace(",", ""));
            double limit = Double.parseDouble(creditCardAccount.getCreditLimit().replace(",", ""));
            double angleCalculation = (amount / limit) * MAX_CURVE_ANGLE;
            if (angleCalculation >= MAX_CURVE_ANGLE) {
                return MAX_CURVE_ANGLE;
            } else {
                return (int) Math.round(angleCalculation);
            }
        } else {
            return 0;
        }
    }

    private void showMinimumPaymentIfNeeded() {
        if (creditCardAccount != null && creditCardAccount.getMinimumAmountPayable() != null && creditCardAccount.getPaymentDueDate() != null) {
            if (!creditCardAccount.getMinimumAmountPayable().getAmount().isEmpty() && !"0.00".equalsIgnoreCase(creditCardAccount.getMinimumAmountPayable().getAmount())) {
                binding.amountDueLinearLayout.setVisibility(View.VISIBLE);
                String amountPayableText = TextFormatUtils.spaceFormattedAmount(creditCardAccount.getMinimumAmountPayable().getCurrency(),
                        creditCardAccount.getMinimumAmountPayable().getAmount()) + " ";
                binding.amountDueTextView.setText(amountPayableText);
                try {
                    String transactionDate = DateUtils.formatDateMonth(creditCardAccount.getPaymentDueDate());
                    String spaceFormatedDueDate = " " + DateUtils.removePeriod(transactionDate);
                    binding.dateDueTextView.setText(spaceFormatedDueDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (numberOfAccounts > 1) {
            binding.transferButton.setVisibility(View.VISIBLE);
            binding.transferButton.setOnClickListener(view -> {
                preventDoubleClick(binding.transferButton);
                navigateToTransferScreen();
            });
        }
        animateCreditCardInfo();
    }

    void setNumberOfAccounts(int numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }

    private void navigateToTransferScreen() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.trackButtonClick("Credit card hub - Transfer button");
            Intent creditCardPaymentIntent = new Intent(baseActivity, TransferFundsActivity.class);

            if (creditCardAccount != null &&
                    creditCardAccount.getMinimumAmountPayable() != null) {
                creditCardPaymentIntent.putExtra(CreditCardHubActivity.MINIMUM_PAYABLE_AMOUNT, creditCardAccount
                        .getMinimumAmountPayable()
                        .getAmount().replace(",", ""));
                creditCard = new CreditCard();
                creditCard.setAccountNo(creditCardAccount.getAccountNo());
                creditCard.setAccountTypeDescription(creditCardAccount.getAccountTypedescription());
                creditCard.setAvailableBalance(creditCardAccount.getAvailableBalance());
            }

            if (creditCard != null) {
                creditCardPaymentIntent.putExtra(CreditCardHubActivity.CREDIT_CARD, (Parcelable) creditCard);
            }
            startActivity(creditCardPaymentIntent);
        }
    }

    void setupFragment(CreditCardResponseObject successResponse) {
        creditCardAccount = successResponse.getCreditCardAccount();
        if (creditCardAccount != null && creditCardAccount.getAvailable() != null) {
            binding.totalTextView.setText(TextFormatUtils.formatBasicAmountAsRand(creditCardAccount.getAvailable().getAmount()));
        } else {
            binding.totalTextView.setText("N/A");
        }

        if (creditCardAccount != null && creditCardAccount.getBalance() != null) {
            Amount balance = creditCardAccount.getBalance();
            if (!balance.toString().contains("-")) {
                binding.currentBalanceTextView.setText(balance.toString());
            } else {
                binding.currentBalanceTextView.setText(balance.toString());
            }
        } else {
            binding.currentBalanceTextView.setText("N/A");
        }
        showMinimumPaymentIfNeeded();
        setupTalkBack();
    }
}