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

package com.barclays.absa.banking.overdraft.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.overdraft.ui.BusinessOverdraftActivity.Companion.BUSINESS_OVERDRAFT_ANALYTIC_TAG
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_business_overdraft_consent.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class BusinessOverdraftConsentFragment : BaseFragment(R.layout.fragment_business_overdraft_consent) {
    private val businessOverdraftViewModel by activityViewModels<BusinessOverdraftViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_Declaration_ScreenDisplayed")
        setToolBar(R.string.sole_prop_vcl_overdraft_we_need_consent, true)
        showToolBar()

        CommonUtils.makeTextClickable(baseActivity, getString(R.string.sole_prop_vcl_overdraft_I_accept_business_client_agreement), getString(R.string.sole_prop_vcl_overdraft_business_client_agreement), object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_Declaration_BCALinkTapped")
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(businessOverdraftViewModel.businessBankOverdraftData.bcaUrl))
                startActivity(browserIntent)
            }
        }, declarationConsentThreeOptionActionButtonView.captionTextView, R.color.graphite)

        continueButton.setOnClickListener {
            if (acceptDeclarationCheckBoxView.isValid) {
                AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_Declaration_ContinueTapped")
                with(businessOverdraftViewModel.businessBankOverdraftData) {
                    showProgressDialog()
                    businessOverdraftViewModel.applyBusinessOverdraft(offersBBOverdraftEnum, existingBBOverdraftLimit, vclOfferAmt)
                }
            } else {
                acceptDeclarationCheckBoxView.setErrorMessage(R.string.you_are_required_to_agree_terms)
            }
        }

        businessOverdraftViewModel.applyBusinessOverdraftResponse.observe(viewLifecycleOwner, {
            if (SUCCESS.equals(it.transactionStatus, true)) {
                if (it.bcmsReferenceNumberForBB.isNotEmpty()) {
                    navigateToSuccessResultScreen(it.bcmsReferenceNumberForBB)
                } else {
                    navigateToSomethingWentWrongScreen()
                }
            } else {
                navigateToFailureResultScreen()
            }
            dismissProgressDialog()
        })
    }

    private fun navigateToSuccessResultScreen(referenceNumber: String) {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.business_overdraft_application_sent))
            setDescription(getString(R.string.business_overdraft_absa_will_contact_you, referenceNumber))
            setPrimaryButtonLabel(getString(R.string.done))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                appCacheService.setShouldUpdateExploreHub(true)
                navigateToHomeScreenSelectingHomeIcon()
            }
            AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_ResultSuccess_ScreenDisplayed")
            navigate(BusinessOverdraftConsentFragmentDirections.actionOverdraftConsentFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToFailureResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.unable_to_continue))
            setDescription(getString(R.string.business_overdraft_unable_to_submit_application))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenSelectingHomeIcon()
            }
            AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_ResultError_ScreenDisplayed")
            navigate(BusinessOverdraftConsentFragmentDirections.actionOverdraftConsentFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToSomethingWentWrongScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalError)
            setTitle(getString(R.string.business_overdraft_something_went_wrong))
            setDescription(getString(R.string.business_overdraft_we_are_having_trouble))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenSelectingHomeIcon()
            }
            AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_ResultError_ScreenDisplayed")
            navigate(BusinessOverdraftConsentFragmentDirections.actionOverdraftConsentFragmentToGenericResultFragment(build(false)))
        }
    }
}