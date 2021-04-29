/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.framework.data.cache

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails
import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.dto.BehaviouralRewardsAllChallengesResponse
import com.barclays.absa.banking.express.userProfile.dto.ExpressRewardsDetails
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionInfo
import com.barclays.absa.banking.rewards.ui.rewardsHub.EarnRate

interface IRewardsCacheService {

    fun getTransactions(): TransactionWrapper?
    fun setTransactions(transactionWrapper: TransactionWrapper)

    fun getRewardsDetails(): RewardsDetails?
    fun setRewardsDetails(rewardsDetails: RewardsDetails)

    fun getEarnRate(): EarnRate?
    fun setEarnRate(earnRate: EarnRate)

    fun getRewardsAccount(): AccountObject?
    fun setRewardsAccount(rewardsAccount: AccountObject)

    fun getRewardsRedemption(): RedeemRewards?
    fun setRewardsRedemption(redeemRewards: RedeemRewards)

    fun getRedeemVoucherInfo(): VoucherRedemptionInfo?
    fun setRedeemVoucherInfo(voucherRedemptionInfo: VoucherRedemptionInfo)
    fun deleteRedeemVoucherInfo()

    fun getMembershipTier(): String?
    fun setMembershipTier(membershipTier: String)

    fun hasRewards(): Boolean
    fun getExpressRewardsDetails(): ExpressRewardsDetails
    fun setExpressRewardsDetails(rewardsDetails: ExpressRewardsDetails)

    fun getBehaviouralRewardsChallenges(): BehaviouralRewardsAllChallengesResponse?
    fun setBehaviouralRewardsChallenges(behaviouralRewardsAllChallengesResponse: BehaviouralRewardsAllChallengesResponse)
    fun clearBehaviouralRewardsChallenges()

    fun clear()
}