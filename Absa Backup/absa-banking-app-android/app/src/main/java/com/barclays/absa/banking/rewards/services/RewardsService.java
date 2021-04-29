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

package com.barclays.absa.banking.rewards.services;

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards;
import com.barclays.absa.banking.boundary.model.rewards.RewardMembershipDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsMembershipUpdatedDetails;
import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface RewardsService {
    void fetchFilteredTransactions(String fromDate,
                                   String toDate,
                                   ExtendedResponseListener<TransactionWrapper> transactionsResponseListener);

    void fetchRewardsData(ExtendedResponseListener<TransactionWrapper> transactionsResponseListener,
                                 ExtendedResponseListener<RewardsDetails> rewardsDetailsResponseListener,
                                 ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener);

    void validateRewardsMembershipDetails(String accountNumber,
                                          String debitOrderFrequency,
                                          String debitDay, ExtendedResponseListener<RewardMembershipDetails> rewardMembershipDetailsExtendedResponseListener);

    void updateMembershipDetails(String transactionReferenceId, ExtendedResponseListener<RewardsMembershipUpdatedDetails> rewardsMembershipDetailsValidateResponseListener);

    void fetchRewardsRedeemData(ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener);
}
