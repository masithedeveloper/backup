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
 *
 */
package com.barclays.absa.banking.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.Header
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountCardHelper
import com.barclays.absa.banking.home.ui.HomeContainerView
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.moneyMarket.ui.ACTIVE_ORBIT_STATUS
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviourRewardsChallengesStatus
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsChallengesFragment
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.OperatorPermissionUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.home_account_item.view.*
import styleguide.cards.Account
import styleguide.cards.AccountView
import styleguide.cards.BehaviouralAccountView
import styleguide.cards.ClusterCardView

class AccountAdapter(private var homeContainerView: HomeContainerView) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private val isBehaviouralRewardsActive = FeatureSwitchingCache.featureSwitchingToggles.behaviouralRewards == FeatureSwitchingStates.ACTIVE.key
    private var ciaAccountCount = 0
    private var ciaFeatureDisabled = false
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        const val CIA_ACCOUNT_DISPLAY_LIMIT = 5
        const val ACCOUNT_KEY = "account"
        const val NOTICE_DEPOSIT = "noticeDeposit"
        const val FIXED_DEPOSIT = "termDeposit"
        const val CREDIT_CARD = "creditCard"
        const val ABSA_REWARDS = "absaReward"
        const val ADVANTAGE = "advantage"
        const val HOME_LOAN = "homeLoan"
        const val PERSONAL_LOAN = "personalLoan"
        const val ABSA_VEHICLE_AND_ASSET_FINANCE = "absaVehicleAndAssetFinance"
        const val UNIT_TRUST_ACCOUNT = "unitTrustAccount"
        const val CIA_ACCOUNT = "cia"
        const val INSURANCE_CLUSTER = "insuranceCluster"
        const val INVESTMENT_CLUSTER = "investmentCluster"
    }

    enum class ViewType {
        CLUSTER,
        REWARDS_SMALL,
        SMALL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when (viewType) {
        ViewType.REWARDS_SMALL.ordinal -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_behavioural_account_item, parent, false).rootView)
        ViewType.CLUSTER.ordinal -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cluster_card_item, parent, false).rootView)
        else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_account_item, parent, false).rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val entry = getEntryAtPosition(position) ?: return
        holder.onBind()
        ciaFeatureDisabled = false

        holder.accountView.visibility = View.VISIBLE

        var accountName: String
        var cardNumber: String
        var balanceLabel: String
        var amount: String

        holder.headingTextView?.visibility = View.GONE

        holder.accountView.setOnClickListener(null)
        when (entry.entryType) {
            Entry.HEADER -> {
                if (holder.itemViewType == ViewType.SMALL.ordinal) {
                    val header = entry as Header
                    holder.headingTextView?.visibility = View.VISIBLE
                    holder.headingTextView?.text = header.label
                    holder.accountView.visibility = View.GONE
                }
            }
            Entry.OFFERS -> {
            }
            Entry.POLICY -> {
                val policy = entry as Policy
                accountName = policy.description ?: ""
                balanceLabel = context.getString(R.string.premium_amount)
                cardNumber = policy.number ?: ""
                amount = TextFormatUtils.formatBasicAmount(policy.monthlyPremium)

                holder.accountView.setOnClickListener { v ->
                    homeContainerView.onPolicyCardInformation(v.tag as Policy)
                }

                holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
            }
            Entry.ACCOUNT -> {
                val accountObject = entry as AccountObject
                val isSupported = CommonUtils.isAccountSupportedRebuild(accountObject)

                holder.accountView.hideAlertImageView()

                accountName = AccountCardHelper.getAccountDescription(accountObject)
                cardNumber = AccountCardHelper.getCardNumber(accountObject)
                balanceLabel = AccountCardHelper.getBalanceLabel(accountObject, isSupported, context)
                amount = AccountCardHelper.getAccountAmount(accountObject, isSupported)

                when (accountObject.accountType) {
                    CIA_ACCOUNT -> {
                        val ciaAccounts = homeContainerView.ciaAccounts
                        ciaAccountCount = ciaAccounts.size
                        cardNumber = accountObject.accountNumber
                        balanceLabel = ""

                        val featureSwitching = FeatureSwitchingCache.featureSwitchingToggles
                        when (featureSwitching.currencyInvestmentHub) {
                            FeatureSwitchingStates.GONE.key -> {
                                ciaFeatureDisabled = true
                                accountName = AccountCardHelper.getCiaAccountName(accountObject, false, context)
                                amount = AccountCardHelper.getAccountAmount(accountObject, isSupported)
                                balanceLabel = AccountCardHelper.getBalanceLabel(accountObject, isSupported, context)
                                holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                            }
                            FeatureSwitchingStates.DISABLED.key -> {
                                accountName = AccountCardHelper.getCiaAccountName(accountObject, false, context)
                                amount = AccountCardHelper.getAccountAmount(accountObject, isSupported)
                                balanceLabel = AccountCardHelper.getBalanceLabel(accountObject, isSupported, context)
                                holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                                if (BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS) {
                                    holder.accountView.setSingleCiaAccountAppearance()
                                }
                            }
                            FeatureSwitchingStates.ACTIVE.key -> {
                                if (ciaAccountCount == 1 || !BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS) {
                                    accountName = AccountCardHelper.getCiaAccountName(accountObject, false, context)
                                    amount = AccountCardHelper.getAccountAmount(accountObject, isSupported)
                                    balanceLabel = AccountCardHelper.getBalanceLabel(accountObject, isSupported, context)
                                    holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                                    if (BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS) {
                                        holder.accountView.setSingleCiaAccountAppearance()
                                    }
                                } else if (ciaAccountCount > 1) {
                                    accountName = AccountCardHelper.getCiaAccountName(accountObject, true, context)
                                    amount = ""
                                    var modifiedList = ciaAccounts
                                    if (ciaAccountCount > 5) {
                                        balanceLabel = "+ ${ciaAccountCount - 5} ${context.getString(R.string.currencies)}"
                                        modifiedList = ciaAccounts.subList(0, 5)
                                    }
                                    val currencyBuilder = StringBuilder()
                                    modifiedList.forEach {
                                        currencyBuilder.append((it as AccountObject).currency).append(", ")
                                    }
                                    currencyBuilder.deleteCharAt(currencyBuilder.length - 2)
                                    holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                                    holder.accountView.setMultipleCiaAccountAppearance(currencyBuilder.toString(), ciaAccountCount)
                                }
                            }
                        }
                    }

                    INSURANCE_CLUSTER -> {
                        with(holder.accountView as ClusterCardView) {
                            setClusterImageView(R.drawable.ic_insurance_small)
                            setClusterHeading(context.getString(R.string.cluster_insurance_heading))
                            val insurancePolicies = homeCacheService.getInsurancePolicies()
                            if (insurancePolicies.isNotEmpty()) {
                                when (val insurancePoliciesSize = insurancePolicies.size) {
                                    1 -> setNumberActiveCards(context.getString(R.string.insurance_cluster_only_one_policy, insurancePoliciesSize))
                                    else -> setNumberActiveCards(context.getString(R.string.insurance_cluster_multiple_policies, insurancePoliciesSize))
                                }
                            }
                        }
                    }

                    INVESTMENT_CLUSTER -> {
                        with(holder.accountView as ClusterCardView) {
                            setClusterImageView(R.drawable.ic_fixed_deposit_small)
                            setClusterHeading(context.getString(R.string.cluster_save_and_invest_heading))
                            val savingAndInvestmentAccount = homeCacheService.getSavingsAndInvestmentsAccounts()
                            if (savingAndInvestmentAccount.isNotEmpty()) {
                                when (val savingAndInvestmentAccountSize = savingAndInvestmentAccount.size) {
                                    1 -> setNumberActiveCards(context.getString(R.string.investment_cluster_only_one_account, savingAndInvestmentAccountSize))
                                    else -> setNumberActiveCards(context.getString(R.string.investment_cluster_multiple_accounts, savingAndInvestmentAccountSize))
                                }
                            }
                        }
                    }

                    NOTICE_DEPOSIT -> {
                        amount = accountObject.currentBalance.toString()
                        balanceLabel = context.getString(R.string.current_balance)
                        holder.accountView.setBackground(R.drawable.rounded_account_card_fixed_deposit)
                    }
                    ADVANTAGE -> {
                        balanceLabel = ""
                        amount = ""
                        setUpBehaviouralCard(context, holder)
                        holder.accountView.setBackground(R.drawable.rounded_account_card_rewards)
                    }
                    ABSA_REWARDS -> {
                        if (!rewardsCacheService.getExpressRewardsDetails().rewardsAccountBalanceSet) {
                            balanceLabel = ""
                            amount = ""
                        } else if (isBehaviouralRewardsActive) {
                            setUpBehaviouralCard(context, holder)
                        }
                        holder.accountView.setBackground(R.drawable.rounded_account_card_rewards)
                    }
                    PERSONAL_LOAN -> {
                        holder.accountView.setBackground(R.drawable.rounded_account_card_rewards)
                    }
                    ABSA_VEHICLE_AND_ASSET_FINANCE -> {
                        holder.accountView.setBackground(R.drawable.rounded_account_card_avaf)
                        amount = accountObject.currentBalance.toString()
                        balanceLabel = context.getString(R.string.current_balance)
                    }
                    CREDIT_CARD -> {
                        holder.accountView.setBackground(R.drawable.rounded_account_card_credit_card)
                    }
                    HOME_LOAN -> {
                        holder.accountView.setBackground(R.drawable.rounded_account_card_home_loans)
                        amount = accountObject.currentBalance.toString()
                        balanceLabel = context.getString(R.string.current_balance)
                    }
                    FIXED_DEPOSIT -> {
                        amount = accountObject.currentBalance.toString()
                        balanceLabel = context.getString(R.string.current_balance)
                        if (accountObject.availableBalance.amountDouble > 0 && !AccessPrivileges.instance.isOperator) {
                            holder.accountView.showAlertImageView()
                        }
                        holder.accountView.setBackground(R.drawable.rounded_account_card_fixed_deposit)
                    }
                    else -> {
                        holder.accountView.setBackground(R.drawable.rounded_account_card)
                        if (featureSwitchingToggles.projectOrbit == FeatureSwitchingStates.ACTIVE.key && homeCacheService.getMoneyMarketStatusList().any { orbitAccount -> orbitAccount.account == accountObject.accountNumber && orbitAccount.status == ACTIVE_ORBIT_STATUS }) {
                            holder.accountView.showAlertImageView()
                        }
                    }
                }

                setClickListeners(accountObject, holder)
                if (!isSupported) {
                    holder.accountView.setBackground(R.drawable.rounded_account_card_grey)
                    holder.accountView.setOnClickListener(null)
                } else if (OperatorPermissionUtils.canViewTransactions(accountObject) && "Y".equals(accountObject.isBalanceMasked, ignoreCase = true)) {
                    balanceLabel = context.getString(R.string.current_balance)
                    amount = accountObject.currentBalance.toString()
                    holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                }

                if (accountObject.accountType == CIA_ACCOUNT) {
                    if (ciaFeatureDisabled || !BuildConfig.TOGGLE_DEF_CIA_ACCOUNT_DETAILS) {
                        holder.accountView.setBackground(R.drawable.rounded_account_card_grey)
                        holder.accountView.setOnClickListener(null)
                    }
                } else {
                    holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))
                }
            }
            Entry.SECONDARY_CARD -> {
                holder.accountView.setBackground(R.drawable.rounded_account_card_grey)
                holder.accountView.setAccount(Account("", context.getString(R.string.secondary_card_account_hidden_message), context.getString(R.string.secondary_card_summary_secondary_card), ""))
            }
        }

        holder.accountView.tag = entry
    }

    private fun setUpBehaviouralCard(context: Context, holder: ViewHolder) {
        val behaviouralRewardsChallenges = rewardsCacheService.getBehaviouralRewardsChallenges()
        val behaviouralAccountView = holder.accountView as BehaviouralAccountView

        var bannerButtonText = context.getString(R.string.behavioural_rewards_challenge_get_more_with_advantage)
        val challenges = behaviouralRewardsChallenges?.challenges
        if (!challenges.isNullOrEmpty()) {
            challenges.firstOrNull { challenge -> challenge.active && challenge.customerChallengeStatus.status.isNotEmpty() }?.let {
                val customerStatus = it.customerChallengeStatus.status
                val completeOrExpired = customerStatus == BehaviourRewardsChallengesStatus.COMPLETE.key || customerStatus == BehaviourRewardsChallengesStatus.EXPIRED.key
                bannerButtonText = when {
                    completeOrExpired && BehaviouralRewardsChallengesFragment.OFFER_AVAILABLE.equals(it.customerChallengeStatus.voucherAllocationStatus, true) -> context.getString(R.string.behavioural_rewards_claim_your_voucher)
                    customerStatus == BehaviourRewardsChallengesStatus.IN_PROGRESS.key -> context.getString(R.string.behavioural_rewards_challenge_in_progress)
                    customerStatus == BehaviourRewardsChallengesStatus.ACCEPTED.key -> context.getString(R.string.behavioural_rewards_view_challenge)
                    customerStatus == BehaviourRewardsChallengesStatus.NEW.key -> context.getString(R.string.behavioural_rewards_challenge_new_challenge_available)
                    else -> context.getString(R.string.behavioural_rewards_challenge_get_more_with_advantage)
                }
            }
        }
        behaviouralAccountView.setBehaviouralText(bannerButtonText)
    }

    override fun getItemViewType(position: Int): Int {
        val accountType = (getEntryAtPosition(position) as? AccountObject)?.accountType
        return if (isBehaviouralRewardsActive && (accountType == ABSA_REWARDS || accountType == ADVANTAGE)) {
            ViewType.REWARDS_SMALL.ordinal
        } else if (accountType == INSURANCE_CLUSTER || accountType == INVESTMENT_CLUSTER) {
            ViewType.CLUSTER.ordinal
        } else {
            ViewType.SMALL.ordinal
        }
    }

    override fun getItemCount(): Int {
        val filteredHomeAccounts = homeCacheService.getFilteredHomeAccounts()
        return if (filteredHomeAccounts.isNotEmpty()) filteredHomeAccounts.size else homeContainerView.accounts.size
    }

    private fun setClickListeners(accountObject: AccountObject, holder: ViewHolder) {
        holder.accountView.setOnClickListener { v ->
            val account = v.tag as AccountObject

            when (accountObject.accountType) {
                CIA_ACCOUNT -> {
                    if (ciaAccountCount == 1) {
                        homeContainerView.navigateToAccountInformation(account)
                    } else {
                        homeContainerView.navigateToMultipleCiaAccountInformation()
                    }
                }
                NOTICE_DEPOSIT -> homeContainerView.navigateToAccountInformation(account)
                ABSA_REWARDS -> {
                    rewardsCacheService.setRewardsAccount(account)
                    homeContainerView.onAbsaRewardsCardClicked()
                }
                ADVANTAGE -> homeContainerView.onAdvantageCardClicked()
                CREDIT_CARD -> homeContainerView.creditCardRequest(account)
                FIXED_DEPOSIT -> homeContainerView.navigateToFixedDepositHub(account)
                HOME_LOAN -> homeContainerView.onHomeLoanAccountCardClicked(account)
                UNIT_TRUST_ACCOUNT -> homeContainerView.onUnitTrustCardClicked(account)
                INSURANCE_CLUSTER -> homeContainerView.onInsuranceClusterClicked(account)
                INVESTMENT_CLUSTER -> homeContainerView.onInvestmentClusterClicked(account)
                else -> homeContainerView.navigateToAccountInformation(account)
            }
        }
    }

    private fun getEntryAtPosition(position: Int): Entry? {
        val filteredHomeAccounts = homeCacheService.getFilteredHomeAccounts()
        val accounts = if (filteredHomeAccounts.isNotEmpty()) filteredHomeAccounts else homeContainerView.accounts
        return accounts?.get(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var accountView: AccountView = itemView.accountView
        var headingTextView: TextView? = itemView.headingTextView

        fun onBind() {
            with(itemView) {
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    when (itemViewType) {
                        ViewType.CLUSTER.ordinal -> accountView.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.cluster_card_height)
                        else -> accountView.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.account_card_small_height)
                    }
                    itemView.layoutParams = this
                }
            }
        }
    }
}