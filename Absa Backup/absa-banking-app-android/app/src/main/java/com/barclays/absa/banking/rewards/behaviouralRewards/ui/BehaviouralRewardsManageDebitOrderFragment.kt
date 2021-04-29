/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.rewards.RewardMembershipDetails
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails
import com.barclays.absa.banking.databinding.BehaviouralRewardsManageDebitOrderFragmentBinding
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.rewards.ui.rewardsHub.AccountItem
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_DD_MM
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMM
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toFormattedAccountNumber
import java.util.*

class BehaviouralRewardsManageDebitOrderFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_manage_debit_order_fragment) {
    private val binding by viewBinding(BehaviouralRewardsManageDebitOrderFragmentBinding::bind)

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()
    private lateinit var rewardsDetails: RewardsDetails
    private lateinit var newRewardsDetails: RewardsDetails

    companion object {
        private const val MONTH_INDEX = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.behavioural_rewards_manage_debit_order)
        rewardsDetails = rewardsCacheService.getRewardsDetails() ?: return

        initViews()
        setUpOnClickListeners()
        setUpAccountView()
        setUpDebitOrderFrequencyView()
        trackAnalytics("ManageDebitOrder_ScreenDisplayed")
    }

    private fun initViews() {
        binding.fromAccountNormalInputView.addRequiredValidationHidingTextWatcher()
        binding.debitOrderFrequencyNormalInputView.addRequiredValidationHidingTextWatcher()
        binding.debitDateNormalInputView.addRequiredValidationHidingTextWatcher(R.string.behavioural_rewards_debit_order_date)

        val accountObject = AbsaCacheManager.getInstance().getRewardAccount(rewardsDetails)
        val frequencyIndex = rewardsDetails.chargeFrequencyId?.toInt() ?: -1

        newRewardsDetails = RewardsDetails().apply {
            fromAccount = rewardsDetails.fromAccount
            chargeFrequencyId = rewardsDetails.chargeFrequencyId
            orderFrequencyDate = rewardsDetails.orderFrequencyDate
        }

        with(binding.fromAccountNormalInputView) {
            when {
                accountObject != null -> {
                    selectedValue = "${accountObject.description}(${accountObject.accountNumber.toFormattedAccountNumber()})"
                    hideError()
                }
                rewardsViewModel.accountItems.isNotEmpty() -> {
                    selectedIndex = 0
                    selectedValue = "${rewardsViewModel.accountItems.first().displayValue}(${rewardsViewModel.accountItems.first().displayValueLine2.toFormattedAccountNumber()})"
                    hideError()
                }
                else -> {
                    showError(true)
                    setError(getString(R.string.select_account_to_pay_from))
                }
            }
        }

        if (frequencyIndex == MONTH_INDEX) {
            binding.debitOrderFrequencyNormalInputView.selectedIndex = 0
            binding.debitDateNormalInputView.selectedValue = DateUtils.formatCollectionDay(getOrderFrequency(rewardsDetails.orderFrequencyDate ?: ""))
        } else {
            binding.debitDateNormalInputView.visibility = View.GONE
        }

        binding.debitOrderFrequencyNormalInputView.setDescription(getDebitOrderFrequency(frequencyIndex, rewardsDetails.monthlyFee, rewardsDetails.annualFee))
        binding.debitOrderFrequencyNormalInputView.selectedValue = if (frequencyIndex == MONTH_INDEX) getString(R.string.rewards_monthly) else getString(R.string.yearly)

        binding.debitDateNormalInputView.setTitleText(getDebitOrderDateLabel(frequencyIndex))
        binding.debitDateNormalInputView.setHintText(getDebitOrderDateHint(frequencyIndex))
    }

    fun setUpOnClickListeners() {
        binding.saveButton.setOnClickListener {
            if (binding.debitOrderFrequencyNormalInputView.selectedIndex != 0) {
                binding.debitOrderFrequencyNormalInputView.setError(getString(R.string.behavioural_rewards_monthly_only))
            } else if (validateFields()) {
                rewardsViewModel.rewardMembershipDetailsMutableLiveData = MutableLiveData()
                rewardsViewModel.rewardMembershipDetailsMutableLiveData.observe(viewLifecycleOwner, { rewardMembershipDetails: RewardMembershipDetails -> rewardsViewModel.updateRewardsMembership(rewardMembershipDetails.transactionReferenceId) })
                rewardsViewModel.validateMembershipDetails(newRewardsDetails.fromAccount, newRewardsDetails.chargeFrequencyId, newRewardsDetails.orderFrequencyDate)

                rewardsViewModel.rewardsUpdatedMembershipDetailsMutableLiveData = MutableLiveData()
                rewardsViewModel.rewardsUpdatedMembershipDetailsMutableLiveData.observe(viewLifecycleOwner, {
                    with(rewardsDetails) {
                        fromAccount = it.fromAccount
                        chargeFrequencyId = it.chargeFrequencyID
                        orderFrequencyDate = it.orderFrequencyDate
                        rewardsCacheService.setRewardsDetails(this)
                    }
                    val intent: Intent = IntentFactory.getSuccessfulResultScreen(activity, R.string.succes_text, getString(R.string.debit_details_updated))
                    startActivity(intent)
                })
            }
        }

        binding.debitDateNormalInputView.setOnClickListener { showDatePickerDialog() }
    }

    private fun validateFields(): Boolean = binding.fromAccountNormalInputView.validate() && binding.debitOrderFrequencyNormalInputView.validate() && binding.debitDateNormalInputView.validateIfVisible()

    private fun getOrderFrequency(frequencyValue: String): Int = frequencyValue.toIntOrNull() ?: 0

    private fun setUpAccountView() {
        with(binding.fromAccountNormalInputView) {
            setList(rewardsViewModel.accountItems, getString(R.string.select_account_rewards_details))
            setItemSelectionInterface { index ->
                val accountItem = binding.fromAccountNormalInputView.selectedItem as AccountItem
                newRewardsDetails.fromAccount = accountItem.displayValueLine2
                selectedIndex = index
                selectedValue = "${accountItem.displayValue}(${accountItem.displayValueLine2.toFormattedAccountNumber()})"
                hideError()
            }
        }
    }

    private fun setUpDebitOrderFrequencyView() {
        val monthlySelectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.rewards_monthly)))
        }
        binding.debitOrderFrequencyNormalInputView.setList(monthlySelectorList, getString(R.string.debit_order_frequency))
        binding.debitOrderFrequencyNormalInputView.showDescription(true)
        binding.debitOrderFrequencyNormalInputView.setItemSelectionInterface {
            val frequencyIndex: Int = binding.debitOrderFrequencyNormalInputView.selectedIndex
            newRewardsDetails.chargeFrequencyId = frequencyIndex.toString()
            binding.debitOrderFrequencyNormalInputView.setDescription(getDebitOrderFrequency(frequencyIndex, rewardsDetails.monthlyFee, rewardsDetails.annualFee))

            with(binding.debitDateNormalInputView) {
                setTitleText(getDebitOrderDateLabel(frequencyIndex))
                setHintText(getDebitOrderDateHint(frequencyIndex))
                visibility = View.VISIBLE
                selectedValue = ""
            }
        }
    }

    private fun getDebitOrderFrequency(frequencyIndex: Int, monthlyFee: Amount?, annualFee: Amount?): String = if (frequencyIndex == MONTH_INDEX) String.format(getString(R.string.rewards_fee_per_month), monthlyFee) else String.format(getString(R.string.rewards_fee_per_annum), annualFee)

    private fun getDebitOrderDateLabel(frequencyIndex: Int): String = if (frequencyIndex == MONTH_INDEX) getString(R.string.debit_day) else getString(R.string.debit_date)

    private fun getDebitOrderDateHint(frequencyIndex: Int): String = if (frequencyIndex == MONTH_INDEX) getString(R.string.debit_date_hint_monthly) else getString(R.string.debit_date_hint_yearly)

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(baseActivity, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            val frequencyIndex = newRewardsDetails.chargeFrequencyId?.toInt() ?: -1
            val debitOrderDate = if (frequencyIndex == MONTH_INDEX) {
                binding.debitDateNormalInputView.selectedValue = (DateUtils.formatCollectionDay(day))
                day.toString()
            } else {
                binding.debitDateNormalInputView.selectedValue = (DateTimeHelper.formatDate(calendar.time, SPACED_PATTERN_DD_MMM))
                DateTimeHelper.formatDate(calendar.time, DASHED_PATTERN_DD_MM)
            }
            newRewardsDetails.orderFrequencyDate = debitOrderDate
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }
}