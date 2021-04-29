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

class RewardsCacheService : IRewardsCacheService {

    companion object {
        private const val BEHAVIOURAL_REWARDS_CHALLENGES = "behavioural_rewards_challenges"
        private const val TRANSACTION_HISTORY = "TRANSACTION_HISTORY"
        private const val REWARDS_DETAILS = "REWARDS_DETAILS"
        private const val EARN_RATES = "EARN_RATES"
        private const val REWARDS_ACCOUNT = "REWARDS_ACCOUNT"
        private const val REWARDS_REDEEM = "REWARDS_REDEEM"
        private const val REDEEM_VOUCHER = "redeem_voucher"
        private const val REWARDS_TIER = "REWARDS_TIER"
        private const val EXPRESS_REWARDS_DETAILS = "EXPRESS_REWARDS_DETAILS"
    }

    private var CACHE = HashMap<String, Any>()

    override fun getTransactions(): TransactionWrapper? = CACHE[TRANSACTION_HISTORY] as? TransactionWrapper
    override fun setTransactions(transactionWrapper: TransactionWrapper) {
        CACHE[TRANSACTION_HISTORY] = transactionWrapper
    }

    override fun getRewardsDetails(): RewardsDetails? = CACHE[REWARDS_DETAILS] as? RewardsDetails
    override fun setRewardsDetails(rewardsDetails: RewardsDetails) {
        CACHE[REWARDS_DETAILS] = rewardsDetails
    }

    override fun getEarnRate(): EarnRate? = CACHE[EARN_RATES] as? EarnRate
    override fun setEarnRate(earnRate: EarnRate) {
        CACHE[EARN_RATES] = earnRate
    }

    override fun getRewardsAccount(): AccountObject? = CACHE[REWARDS_ACCOUNT] as? AccountObject
    override fun setRewardsAccount(rewardsAccount: AccountObject) {
        CACHE[REWARDS_ACCOUNT] = rewardsAccount
    }

    override fun getRewardsRedemption(): RedeemRewards? = CACHE[REWARDS_REDEEM] as? RedeemRewards
    override fun setRewardsRedemption(redeemRewards: RedeemRewards) {
        CACHE[REWARDS_REDEEM] = redeemRewards
    }

    override fun getRedeemVoucherInfo(): VoucherRedemptionInfo? = CACHE[REDEEM_VOUCHER] as? VoucherRedemptionInfo
    override fun setRedeemVoucherInfo(voucherRedemptionInfo: VoucherRedemptionInfo) {
        CACHE[REDEEM_VOUCHER] = voucherRedemptionInfo
    }
    override fun deleteRedeemVoucherInfo() {
        CACHE.remove(REDEEM_VOUCHER)
    }

    override fun getMembershipTier(): String? = CACHE[REWARDS_TIER] as? String
    override fun setMembershipTier(membershipTier: String) {
        CACHE[REWARDS_TIER] = membershipTier
    }

    override fun hasRewards(): Boolean = getExpressRewardsDetails().rewardsMembershipNumber.isNotEmpty()
    override fun getExpressRewardsDetails(): ExpressRewardsDetails = (CACHE[EXPRESS_REWARDS_DETAILS] as? ExpressRewardsDetails) ?: ExpressRewardsDetails()
    override fun setExpressRewardsDetails(rewardsDetails: ExpressRewardsDetails) {
        CACHE[EXPRESS_REWARDS_DETAILS] = rewardsDetails
    }

    override fun getBehaviouralRewardsChallenges(): BehaviouralRewardsAllChallengesResponse? = CACHE[BEHAVIOURAL_REWARDS_CHALLENGES] as? BehaviouralRewardsAllChallengesResponse
    override fun setBehaviouralRewardsChallenges(behaviouralRewardsAllChallengesResponse: BehaviouralRewardsAllChallengesResponse) {
        CACHE[BEHAVIOURAL_REWARDS_CHALLENGES] = behaviouralRewardsAllChallengesResponse
    }
    override fun clearBehaviouralRewardsChallenges() {
        CACHE.remove(BEHAVIOURAL_REWARDS_CHALLENGES)
    }

    override fun clear() {
        CACHE = HashMap()
    }
}