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

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.rewards.RedeemRewards
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails
import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.dto.BehaviouralRewardsAllChallengesResponse
import com.barclays.absa.banking.express.userProfile.dto.ExpressRewardsDetails
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionInfo
import com.barclays.absa.banking.rewards.ui.rewardsHub.EarnRate
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class RewardsCacheServiceTest : DaggerTest() {

    private lateinit var rewardsCacheService: IRewardsCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        rewardsCacheService = BMBApplication.applicationComponent.getRewardsCacheService()
    }

    @Test
    fun getTransactions() {
        val transactionWrapper = TransactionWrapper()
        assertNull(rewardsCacheService.getTransactions()) { defaultValueChangedMessage }
        rewardsCacheService.setTransactions(transactionWrapper)
        assertTrue(rewardsCacheService.getTransactions() == transactionWrapper) { valueUpdateFailedMessage }
    }

    @Test
    fun getRewardsDetails() {
        val rewardsDetails = RewardsDetails()
        assertNull(rewardsCacheService.getRewardsDetails()) { defaultValueChangedMessage }
        rewardsCacheService.setRewardsDetails(rewardsDetails)
        assertTrue(rewardsCacheService.getRewardsDetails() == rewardsDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getEarnRate() {
        val earnRate = EarnRate()
        assertNull(rewardsCacheService.getEarnRate()) { defaultValueChangedMessage }
        rewardsCacheService.setEarnRate(earnRate)
        assertTrue(rewardsCacheService.getEarnRate() == earnRate) { valueUpdateFailedMessage }
    }

    @Test
    fun getRewardsAccount() {
        val accountObject = AccountObject()
        assertNull(rewardsCacheService.getRewardsAccount()) { defaultValueChangedMessage }
        rewardsCacheService.setRewardsAccount(accountObject)
        assertTrue(rewardsCacheService.getRewardsAccount() == accountObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getRewardsRedemption() {
        val redeemRewards = RedeemRewards()
        assertNull(rewardsCacheService.getRewardsRedemption()) { defaultValueChangedMessage }
        rewardsCacheService.setRewardsRedemption(redeemRewards)
        assertTrue(rewardsCacheService.getRewardsRedemption() == redeemRewards) { valueUpdateFailedMessage }
    }

    @Test
    fun getRedeemVoucherInfo() {
        val voucherRedemptionInfo = VoucherRedemptionInfo()
        assertNull(rewardsCacheService.getRedeemVoucherInfo()) { defaultValueChangedMessage }
        rewardsCacheService.setRedeemVoucherInfo(voucherRedemptionInfo)
        assertTrue(rewardsCacheService.getRedeemVoucherInfo() == voucherRedemptionInfo) { valueUpdateFailedMessage }
    }

    @Test
    fun deleteRedeemVoucherInfo() {
        val voucherRedemptionInfo = VoucherRedemptionInfo()
        rewardsCacheService.setRedeemVoucherInfo(voucherRedemptionInfo)
        rewardsCacheService.deleteRedeemVoucherInfo()
        assertNull(rewardsCacheService.getRedeemVoucherInfo()) { defaultValueChangedMessage }
    }

    @Test
    fun getMembershipTier() {
        assertNull(rewardsCacheService.getMembershipTier())
        rewardsCacheService.setMembershipTier("Tier1")
        assertTrue(rewardsCacheService.getMembershipTier() == "Tier1") { defaultValueChangedMessage }
    }

    @Test
    fun getExpressRewardsDetails() {
        val expressRewardsDetails = Gson().fromJson("""{
            "rewardsMembershipNumber": "190904004771",
            "rewardsAccountDetailSet": true,
            "rewardsAccountBalanceSet": true,
            "rewardsTranHistorySet": false,
            "accountTypeDescription": "Absa Rewards",
            "accountName": "Absa Rewards"
        }""", ExpressRewardsDetails::class.java)
        assertNotNull(rewardsCacheService.getExpressRewardsDetails())
        assertFalse(rewardsCacheService.hasRewards())
        rewardsCacheService.setExpressRewardsDetails(expressRewardsDetails)
        assertTrue(rewardsCacheService.getExpressRewardsDetails() == expressRewardsDetails) { defaultValueChangedMessage }
        assertTrue(rewardsCacheService.hasRewards())
    }

    @Test
    fun getBehaviouralRewardsChallenges() {
        val behaviouralRewardsAllChallengesResponse = BehaviouralRewardsAllChallengesResponse()
        assertNull(rewardsCacheService.getBehaviouralRewardsChallenges())
        rewardsCacheService.setBehaviouralRewardsChallenges(behaviouralRewardsAllChallengesResponse)
        assertTrue(rewardsCacheService.getBehaviouralRewardsChallenges() == behaviouralRewardsAllChallengesResponse) { defaultValueChangedMessage }
    }

    @Test
    fun clearBehaviouralRewardsChallenges() {
        rewardsCacheService.clearBehaviouralRewardsChallenges()
        assertNull(rewardsCacheService.getBehaviouralRewardsChallenges())
    }

    @Test
    fun clear() {
        rewardsCacheService.clear()
        assertNull(rewardsCacheService.getTransactions()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getRewardsDetails()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getEarnRate()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getRewardsAccount()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getRewardsRedemption()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getRedeemVoucherInfo()) { defaultValueChangedMessage }
        assertNull(rewardsCacheService.getMembershipTier())
        assertNotNull(rewardsCacheService.getExpressRewardsDetails())
        assertFalse(rewardsCacheService.hasRewards())
        assertNull(rewardsCacheService.getBehaviouralRewardsChallenges())
    }
}