/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.creditCardHotLeads

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.creditCardHotLeads.HotLeadApplicationCode.*
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ValidationUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.hot_leads_contact_details_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toTenDigitPhoneNumber

class HotLeadsContactDetailsFragment : BaseFragment(R.layout.hot_leads_contact_details_fragment) {

    private lateinit var hotLeadsHostActivity: HotLeadsHostActivity
    private lateinit var hotLeadsViewModel: HotLeadsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hotLeadsHostActivity = activity as HotLeadsHostActivity
        hotLeadsViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hotLeadsHostActivity.apply {
            setToolbarTitle(getString(R.string.hot_leads_call_me_back))
            showToolbarBackArrow()
            showToolbar()
        }
        setUpObserver()
        callMeBackButton.setOnClickListener {
            if (isValidContactNumber()) {
                hotLeadsViewModel.applyForCreditCard(cellphoneNumberInputView.selectedValueUnmasked)
            }
        }

        cellphoneNumberInputView.selectedValue = appCacheService.getCellphoneNumber().toTenDigitPhoneNumber()
    }

    private fun setUpObserver() {
        hotLeadsViewModel.hotLeadApplicationResponse.observe(this, {
            dismissProgressDialog()
            when (it.responseCode) {
                HOT_LEAD_APPLICATION_APPROVED.key -> navigateToSuccessScreen()
                HOT_LEAD_SYSTEM_ERROR.key -> navigateToSystemErrorResultScreen()
                HOT_LEAD_GENERAL_ERROR.key -> navigateToSystemErrorResultScreen()
                else -> navigateToFailureScreen()
            }
        })

        hotLeadsViewModel.failureResponse.observe(this, {
            dismissProgressDialog()
            showMessageError(it.transactionMessage.toString())
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_menu_item -> {
                AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_ConfirmContactDetails_CancelButtonClicked")
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .title(getString(R.string.hot_leads_are_you_sure))
                        .message(getString(R.string.hot_leads_come_back_soon))
                        .positiveDismissListener { _, _ ->
                            AnalyticsUtils.getInstance().trackCancelButton(BaseActivity.mScreenName, BaseActivity.mSiteSection)
                            BaseAlertDialog.dismissAlertDialog()
                            activity?.finish()
                        })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToSuccessScreen() {
        hotLeadsHostActivity.hideToolbar()
        AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_SuccessfulScreen_ScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.hot_leads_success_title))
                .setDescription(getString(R.string.hot_leads_success_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            hotLeadsHostActivity.navigateToHomeScreenAndShowAccountsList()
        }
        navigate(HotLeadsContactDetailsFragmentDirections.actionHotLeadsContactDetailsFragmentToHotLeadsResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureScreen() {
        hotLeadsHostActivity.hideToolbar()
        AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_DeclinedScreen_ScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.hot_leads_failure_title))
                .setDescription(getString(R.string.hot_leads_failure_description))
                .setPrimaryButtonLabel(getString(R.string.home))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            hotLeadsHostActivity.navigateToHomeScreenAndShowAccountsList()
        }
        navigate(HotLeadsContactDetailsFragmentDirections.actionHotLeadsContactDetailsFragmentToHotLeadsResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToSystemErrorResultScreen() {
        hotLeadsHostActivity.hideToolbar()
        AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_ErrorScreen_ScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.system_error))
                .setDescription(getString(R.string.hot_leads_system_error_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            hotLeadsHostActivity.navigateToHomeScreenAndShowAccountsList()
        }
        navigate(HotLeadsContactDetailsFragmentDirections.actionHotLeadsContactDetailsFragmentToHotLeadsResultScreenFragment(resultScreenProperties))
    }

    private fun isValidContactNumber(): Boolean {
        if (cellphoneNumberInputView.selectedValue.isEmpty() || !ValidationUtils.validatePhoneNumberInput(cellphoneNumberInputView.selectedValue)) {
            cellphoneNumberInputView.setError(getString(R.string.hot_leads_contact_number_error))
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        hotLeadsViewModel.hotLeadApplicationResponse.removeObservers(this)
        hotLeadsViewModel.failureResponse.removeObservers(this)
    }
}