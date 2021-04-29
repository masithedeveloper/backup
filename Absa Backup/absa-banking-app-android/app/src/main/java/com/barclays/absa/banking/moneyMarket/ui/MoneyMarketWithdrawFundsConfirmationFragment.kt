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
package com.barclays.absa.banking.moneyMarket.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MoneyMarketWithdrawFundsConfirmationFragmentBinding
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedAmount

class MoneyMarketWithdrawFundsConfirmationFragment : MoneyMarketBaseFragment(R.layout.money_market_withdraw_funds_confirmation_fragment) {
    private val binding by viewBinding(MoneyMarketWithdrawFundsConfirmationFragmentBinding::bind)
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    moneyMarketViewModel.withdrawMoneyMarket()
                }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.money_market_confirm_title)
        with(binding) {
            continueButton.setOnClickListener { moneyMarketViewModel.withdrawMoneyMarket() }
            noticeTextView.text = getString(R.string.money_market_withdraw_funds_description, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.currentBalance.getAmount().toFormattedAmount())
            accountNamePrimaryContentAndLabelView.setContentText(moneyMarketViewModel.moneyMarketFlowModel.moneyMarketDestinationAccount.displayName)
            accountNumberPrimaryContentAndLabelView.setContentText(moneyMarketViewModel.moneyMarketFlowModel.moneyMarketDestinationAccount.accountNumber.toFormattedAccountNumber())
        }
        addObserver()
    }

    private fun addObserver() {
        moneyMarketViewModel.moneyMarketWithdrawLiveData.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(baseActivity, it) {
                moneyMarketViewModel.hideMoneyMarketOfferBanner(moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.accountNumber)
                showWithdrawMoneyMarketSuccessScreen()
            }
            dismissProgressDialog()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moneyMarketViewModel.moneyMarketWithdrawLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun showWithdrawMoneyMarketSuccessScreen() {
        moneyMarketActivity.hideToolBar()
        moneyMarketActivity.hideToolbarSeparator()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.money_market_withdraw_funds_success_title))
                .setDescription(getString(R.string.money_market_withdraw_funds_success_description, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.currentBalance.getAmount().toFormattedAmount(), moneyMarketViewModel.moneyMarketFlowModel.moneyMarketDestinationAccount.accountNumber.takeLast(4)))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            moneyMarketActivity.loadAccountsAndShowHomeScreenWithAccountsList()
        }
        navigate(MoneyMarketWithdrawFundsConfirmationFragmentDirections.actionMoneyMarketWithdrawFundsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }
}