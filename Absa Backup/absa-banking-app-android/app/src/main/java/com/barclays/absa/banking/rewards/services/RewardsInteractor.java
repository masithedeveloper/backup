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
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.rewards.services.dto.RewardsDetailsRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsMembershipDetailsUpdateRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsMembershipDetailsValidateRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsRedeemRequest;
import com.barclays.absa.banking.rewards.services.dto.RewardsTransactionsRequest;

public class RewardsInteractor extends AbstractInteractor implements RewardsService {

    @Override
    public void fetchFilteredTransactions(String fromDate,
                                          String toDate,
                                          ExtendedResponseListener<TransactionWrapper> transactionsResponseListener) {

        RewardsTransactionsRequest<TransactionWrapper> rewardsTransactionsRequest
                = new RewardsTransactionsRequest<>(fromDate, toDate, transactionsResponseListener);
        ServiceClient serviceClient = new ServiceClient(rewardsTransactionsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateRewardsMembershipDetails(String accountNumber,
                                                 String debitOrderFrequency,
                                                 String debitDay,
                                                 ExtendedResponseListener<RewardMembershipDetails> rewardMembershipDetailsExtendedResponseListener) {
        RewardsMembershipDetailsValidateRequest<RewardMembershipDetails> rewardsMembershipDetailsValidateRequest
                = new RewardsMembershipDetailsValidateRequest<>(rewardMembershipDetailsExtendedResponseListener, accountNumber, debitOrderFrequency, debitDay);
        ServiceClient serviceClient = new ServiceClient(rewardsMembershipDetailsValidateRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void updateMembershipDetails(String transactionReferenceId, ExtendedResponseListener<RewardsMembershipUpdatedDetails> rewardsMembershipDetailsValidateResponseListener) {
        RewardsMembershipDetailsUpdateRequest<RewardsMembershipUpdatedDetails> rewardsMembershipDetailsUpdateRequest
                = new RewardsMembershipDetailsUpdateRequest<>(rewardsMembershipDetailsValidateResponseListener, transactionReferenceId);
        ServiceClient serviceClient = new ServiceClient(rewardsMembershipDetailsUpdateRequest);
        serviceClient.submitRequest();
    }

    public void fetchRewardsData(ExtendedResponseListener<TransactionWrapper> transactionsResponseListener,
                                 ExtendedResponseListener<RewardsDetails> rewardsDetailsResponseListener,
                                 ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener) {

        RewardsDetailsRequest<RewardsDetails> rewardsDetailsRequest = new RewardsDetailsRequest<>(rewardsDetailsResponseListener);
        RewardsTransactionsRequest<TransactionWrapper> transactionsHistoryRequest = new RewardsTransactionsRequest<>(transactionsResponseListener);
        RewardsRedeemRequest<RedeemRewards> rewardsRedeemRequest = new RewardsRedeemRequest<>(redeemRewardsResponseListener);
        submitQueuedRequests(transactionsHistoryRequest, rewardsDetailsRequest, rewardsRedeemRequest);
    }

    @Override
    public void fetchRewardsRedeemData(ExtendedResponseListener<RedeemRewards> redeemRewardsResponseListener) {
        RewardsRedeemRequest<RedeemRewards> rewardsRedeemRequest = new RewardsRedeemRequest<>(redeemRewardsResponseListener);
        submitQueuedRequests(rewardsRedeemRequest);
    }
}
