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
 *
 */

package com.barclays.absa.banking.newToBank

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.businessBanking.ui.BusinessBankingViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.NewToBankActivity.NEW_TO_BANK_TEMP_DATA_EXTRAS
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.viewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.new_to_bank_optional_account_extras_fragment.*

class NewToBankOptionalAccountExtrasFragment : BaseFragment(R.layout.new_to_bank_optional_account_extras_fragment) {
    private lateinit var newToBankView: NewToBankView
    private lateinit var optionalAccountExtrasAdapter: NewToBankOptionalAccountExtrasRecyclerViewAdapter
    private lateinit var businessBankingViewModel: BusinessBankingViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
        businessBankingViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessOptionalAccountExtras_ScreenDisplayed")
        newToBankView.hideProgressIndicator()
        setToolBar(R.string.business_banking_optional_account_extras)
        showToolBar()
        businessBankingViewModel.fetchBusinessEvolveOptionalExtrasPackage()
        setUpObservers()
        setUpOnClickListeners()
    }

    private fun setUpObservers() {
        businessBankingViewModel.businessEvolveOptionalExtrasMutableLiveData = MutableLiveData()
        businessBankingViewModel.businessEvolveOptionalExtrasMutableLiveData.observe(viewLifecycleOwner, {
            optionalAccountExtrasTitleTextView.text = it.headerText
            compliantOptionsTextView.text = it.footerText
            optionalAccountExtrasAdapter = NewToBankOptionalAccountExtrasRecyclerViewAdapter(it.optionsExtras)
            optionalAccountExtrasRecyclerView.adapter = optionalAccountExtrasAdapter
            optionalAccountExtrasAdapter.notifyDataSetChanged()
            dismissProgressDialog()
        })
    }

    private fun setUpOnClickListeners() {
        continueButton.setOnClickListener {
            newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessOptionalAccountExtras_ContinueClicked")
            optionalAccountExtrasAdapter.selectedAccountExtras.forEach { selectedAccountExtra ->
                newToBankView.newToBankTempData.apply {
                    when (selectedAccountExtra.key) {
                        NewToBankParams.SHORT_AND_MEDIUM_TERM_FUNDING_REWARDS_VALUE_ADDED_SERVICE.key -> shortAndMediumTermFundingRewardsValueAddedService = selectedAccountExtra.value
                        NewToBankParams.ASSET_FINANCE_REWARDS_VALUE_ADDED_SERVICE.key -> assetFinanceRewardsValueAddedService = selectedAccountExtra.value
                        NewToBankParams.MAKE_AND_RECEIVE_PAYMENTS_REWARDS_VALUE_ADDED_SERVICE.key -> makeAndReceivePaymentsRewardsValueAddedService3 = selectedAccountExtra.value
                        NewToBankParams.SAVING_AND_INVESTMENT_PRODUCTS_REWARDS_VALUE_ADDED_SERVICE.key -> savingAndInvestmentProductsRewardsValueAddedService4 = selectedAccountExtra.value
                    }
                }
            }

            if (BMBApplication.getInstance().userLoggedInStatus) {
                navigateToAlertResultScreen()
            } else {
                newToBankView.navigateToStartApplicationFragment()
            }
        }
    }

    private fun navigateToAlertResultScreen() {
        val primaryEventHandler = View.OnClickListener { logout() }
        val secondaryEventHandler = View.OnClickListener { BMBApplication.getInstance().topMostActivity.finish() }

        startActivity(IntentFactory.getAlertResultScreenWithEventHandlers(activity,
                R.string.please_note,
                R.string.business_bank_logout_alert_message,
                primaryEventHandler,
                secondaryEventHandler)
        )
    }

    private fun logout() {
        val bmbApplication = BMBApplication.getInstance()
        bmbApplication.logout()
        ProfileManager.getInstance().loadAllUserProfiles(object : ProfileManager.OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                val activity = bmbApplication.topMostActivity as BaseActivity
                val expressAuthenticationHelper = ExpressAuthenticationHelper(activity)
                expressAuthenticationHelper.performHello {
                    dismissProgressDialog()
                    bmbApplication.updateLanguage(baseActivity, BMBConstants.ENGLISH_CODE)
                    activity.finish()
                    with(baseActivity) {
                        intent.putExtra(NEW_TO_BANK_TEMP_DATA_EXTRAS, Gson().toJson(newToBankView.newToBankTempData))
                        recreate()
                    }
                }
            }

            override fun onProfilesLoadFailed() {}
        })
    }
}