/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.rewards.behaviouralRewards.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.barclays.absa.banking.boundary.model.AccrualData;
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardMembershipDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsAccountDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsMembershipUpdatedDetails;
import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.rewards.services.RewardsInteractor;
import com.barclays.absa.banking.rewards.services.RewardsService;
import com.barclays.absa.banking.rewards.ui.rewardsHub.AccountItem;
import com.barclays.absa.banking.rewards.ui.rewardsHub.EarnRate;
import com.barclays.absa.banking.rewards.ui.rewardsHub.State;

import java.util.List;

import styleguide.forms.SelectorList;

public class RewardsViewModel extends ViewModel {
    private final RewardsService rewardsService = new RewardsInteractor();
    private MutableLiveData<State> state = new MutableLiveData<>();
    public MutableLiveData<TransactionWrapper> transactionsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<RewardMembershipDetails> rewardMembershipDetailsMutableLiveData;
    MutableLiveData<RewardsMembershipUpdatedDetails> rewardsUpdatedMembershipDetailsMutableLiveData;
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);
    private boolean isFilteredTransaction = false;

    //Service callbacks
    private ExtendedResponseListener<TransactionWrapper> transactionsResponseListener = new ExtendedResponseListener<TransactionWrapper>() {

        @Override
        public void onRequestStarted() {
            super.onRequestStarted();
            state.setValue(State.STARTED);
        }

        @Override
        public void onSuccess(final TransactionWrapper transactionWrapper) {
            if (isFilteredTransaction) {
                state.setValue(State.COMPLETED);
                transactionsMutableLiveData.setValue(transactionWrapper);
            }
            rewardsCacheService.setTransactions(transactionWrapper);
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            state.setValue(State.FAILED);
        }
    };

    private final ExtendedResponseListener<RewardsDetails> rewardsDetailsResponseListener = new ExtendedResponseListener<RewardsDetails>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(RewardsDetails rewardsDetails) {

            final String PERCENTAGE_SIGN = "%";
            final String MEMBERSHIP_TIER = "MembershipTier";
            final String BANK_CREDIT_EARN_RATES = "BankEarnRateCR";
            final String BANK_DEBIT_EARN_RATES = "BankEarnRateDT";
            final String FUEL_DEBIT_EARN_RATES = "FuelEarnRateDT";
            final String FUEL_CREDIT_EARN_RATES = "FuelEarnRateCR";
            final String GROCERY_CREDIT_EARN_RATES = "GroceryEarnRateCR";
            final String GROCERY_DEBIT_EARN_RATES = "GroceryEarnRateDT";

            EarnRate earnRate = new EarnRate();

            if (rewardsDetails.getAccrualDataList() != null) {
                for (AccrualData accrualData : rewardsDetails.getAccrualDataList()) {
                    String earnRateValue = accrualData.getFieldValue() + PERCENTAGE_SIGN;
                    if (accrualData.getFieldName() != null) {
                        if (accrualData.getFieldName().equalsIgnoreCase(MEMBERSHIP_TIER)) {
                            if (accrualData.getFieldValue() != null) {
                                rewardsCacheService.setMembershipTier(accrualData.getFieldValue());
                            }
                        } else if (accrualData.getFieldName().equalsIgnoreCase(BANK_CREDIT_EARN_RATES)) {
                            earnRate.setBankCreditEarnRate(earnRateValue);
                        } else if (accrualData.getFieldName().equalsIgnoreCase(BANK_DEBIT_EARN_RATES)) {
                            earnRate.setBankDebitEarnRate(earnRateValue);
                        } else if (accrualData.getFieldName().equalsIgnoreCase(FUEL_DEBIT_EARN_RATES)) {
                            earnRate.setFuelDebitEarnRate(earnRateValue);
                        } else if (accrualData.getFieldName().equalsIgnoreCase(FUEL_CREDIT_EARN_RATES)) {
                            earnRate.setFuelCreditEarnRate(earnRateValue);
                        } else if (accrualData.getFieldName().equalsIgnoreCase(GROCERY_CREDIT_EARN_RATES)) {
                            earnRate.setGroceryCreditEarnRate(earnRateValue);
                        } else if (accrualData.getFieldName().equalsIgnoreCase(GROCERY_DEBIT_EARN_RATES)) {
                            earnRate.setGroceryDebitEarnRate(earnRateValue);
                        }
                    }
                }
                rewardsCacheService.setEarnRate(earnRate);
            }
            rewardsCacheService.setRewardsDetails(rewardsDetails);
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            state.setValue(State.FAILED);
        }
    };

    private final ExtendedResponseListener<RedeemRewards> redeemRewardsExtendedResponseListener = new ExtendedResponseListener<RedeemRewards>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(RedeemRewards redeemRewards) {
            state.setValue(State.QUEUE_COMPLETED);
            rewardsCacheService.setRewardsRedemption(redeemRewards);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            state.setValue(State.FAILED);
        }
    };

    private final ExtendedResponseListener<RewardMembershipDetails> rewardsMembershipDetailsValidateResponseListener = new ExtendedResponseListener<RewardMembershipDetails>() {

        @Override
        public void onSuccess(RewardMembershipDetails rewardMembershipDetails) {
            rewardMembershipDetailsMutableLiveData.setValue(rewardMembershipDetails);
        }
    };

    private final ExtendedResponseListener<RewardsMembershipUpdatedDetails> rewardsMembershipDetailsUpdateResponseListener = new ExtendedResponseListener<RewardsMembershipUpdatedDetails>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final RewardsMembershipUpdatedDetails rewardsMembershipUpdatedDetails) {
            rewardsUpdatedMembershipDetailsMutableLiveData.setValue(rewardsMembershipUpdatedDetails);
        }
    };

    public void fireAllRewardsDataRequest() {
        isFilteredTransaction = false;
        rewardsService.fetchRewardsData(transactionsResponseListener, rewardsDetailsResponseListener, redeemRewardsExtendedResponseListener);
    }

    public void fetchRedeemRewards() {
        rewardsService.fetchRewardsRedeemData(redeemRewardsExtendedResponseListener);
    }

    public void getFilteredTransactions(String fromDate, String toDate) {
        isFilteredTransaction = true;
        rewardsService.fetchFilteredTransactions(fromDate, toDate, transactionsResponseListener);
    }

    void validateMembershipDetails(String accountNumber, String debitOrderFrequency, String debitDay) {
        rewardsService.validateRewardsMembershipDetails(accountNumber, debitOrderFrequency, debitDay, rewardsMembershipDetailsValidateResponseListener);
    }

    void updateRewardsMembership(String transactionReferenceId) {
        rewardsService.updateMembershipDetails(transactionReferenceId, rewardsMembershipDetailsUpdateResponseListener);
    }

    SelectorList<AccountItem> getAccountItems() {
        final SelectorList<AccountItem> accountItems = new SelectorList<>();
        RewardsDetails rewardsDetails = rewardsCacheService.getRewardsDetails();
        if (rewardsDetails != null) {
            List<RewardsAccountDetails> rewardsAccountDetailsList = rewardsDetails.getAccountList();
            if (rewardsAccountDetailsList != null) {
                for (RewardsAccountDetails rewardsAccountDetails : rewardsAccountDetailsList) {
                    String description = rewardsAccountDetails.getDescription() != null && rewardsAccountDetails.getDescription().isEmpty() ? rewardsAccountDetails.getAccountNumber() : rewardsAccountDetails.getDescription();
                    String accountNumber = rewardsAccountDetails.getAccountNumber();
                    if (accountNumber != null) {
                        AccountItem accountItem = new AccountItem(description != null ? description : accountNumber, accountNumber);
                        accountItems.add(accountItem);
                    }
                }
            }
        }

        return accountItems;
    }

    public MutableLiveData<State> getState() {
        return state;
    }

    public void resetState() {
        state = new MutableLiveData<>();
    }
}
