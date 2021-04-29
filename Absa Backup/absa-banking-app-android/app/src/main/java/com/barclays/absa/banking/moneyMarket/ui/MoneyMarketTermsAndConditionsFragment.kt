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
import com.barclays.absa.banking.databinding.MoneyMarketTermsAndConditionsFragmentBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toUpperCaseWithLocale

class MoneyMarketTermsAndConditionsFragment : MoneyMarketBaseFragment(R.layout.money_market_terms_and_conditions_fragment) {
    private val binding by viewBinding(MoneyMarketTermsAndConditionsFragmentBinding::bind)
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    moneyMarketViewModel.convertMoneyMarketAccount()
                }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.money_market_terms_and_conditions_toolbar_title)
        binding.termsAndConditionsCheckBoxView.setOnCheckedListener { binding.termsAndConditionsCheckBoxView.clearError() }
        binding.nextButton.setOnClickListener {
            if (binding.termsAndConditionsCheckBoxView.isChecked) {
                moneyMarketViewModel.convertMoneyMarketAccount()
            } else {
                binding.termsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.money_market_withdraw_funds_terms_and_conditions_error))
            }
        }
        showTermsAndConditions()

        setUpObserver()
    }

    private fun showTermsAndConditions() {
        val languageIndicator = BMBApplication.getApplicationLocale().toString().toUpperCaseWithLocale()
        val termsAndConditionsUrl = if (isBusinessAccount) {
            "https://ib.absa.co.za/absa-online/assets/Assets/Absa_Investment_Tracker_$languageIndicator.pdf"
        } else {
            "https://ib.absa.co.za/absa-online/assets/Assets/Assets/Cash_Invest_Tracker_$languageIndicator.pdf"
        }
        binding.moneyMarketTermsAndConditionsPdfView.showPDF(termsAndConditionsUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moneyMarketViewModel.moneyMarketConvertAccountLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun setUpObserver() {
        moneyMarketViewModel.moneyMarketConvertAccountLiveData.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(baseActivity, it) {
                moneyMarketViewModel.hideMoneyMarketOfferBanner(moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.accountNumber)
                showConvertMoneyMarketSuccessScreen()
            }
            dismissProgressDialog()
        })
    }

    private fun showConvertMoneyMarketSuccessScreen() {
        moneyMarketActivity.hideToolBar()
        moneyMarketActivity.hideToolbarSeparator()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.money_market_convert_to_cash_success_title))
                .setDescription(getString(R.string.money_market_convert_to_cash_success_description, getInvestmentAccountType()))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            moneyMarketActivity.loadAccountsAndShowHomeScreenWithAccountsList()
        }
        navigate(MoneyMarketTermsAndConditionsFragmentDirections.actionMoneyMarketTermsAndConditionsFragmentToGenericResultScreenFragment2(resultScreenProperties))
    }
}