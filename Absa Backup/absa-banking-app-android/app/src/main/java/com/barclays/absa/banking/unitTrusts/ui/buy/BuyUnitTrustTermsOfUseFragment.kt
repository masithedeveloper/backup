/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.ui.DebiCheckContracts
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.buy_unit_trust_terms_of_use_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toSentenceCase

class BuyUnitTrustTermsOfUseFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_terms_of_use_fragment) {

    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_terms_and_conditions))

        hostActivity.apply {
            if (buyUnitTrustViewModel.isBuyNewFund) {
                hostActivity.trackEvent("UTBuyNewFund_TermsAndConditionsScreen_ScreenDisplayed")
            } else {
                trackCurrentFragment("WIMI_UT_BuyNew_Step4_TCs")
            }
            setCurrentProgress(4)
        }

        acceptButton.setOnClickListener {
            if (acceptTermsCheckBox.isChecked) {
                if (buyUnitTrustViewModel.isBuyNewFund) {
                    buyUnitTrustViewModel.buyNewFund()
                    buyUnitTrustViewModel.buyMoreFundsResponseLiveData.observe(hostActivity, {
                        if (it?.transactionStatus.equals(BMBApplication.CONST_SUCCESS, true)) {
                            sureCheckDelegate.processSureCheck(hostActivity, it) {
                                navigate(BuyUnitTrustTermsOfUseFragmentDirections.actionBuyUnitTrustTermsOfUseFragmentToGenericResultScreenFragment(
                                        successResultScreen(buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustAccountNumber), true))
                                buyUnitTrustViewModel.buyMoreFundsResponseLiveData.removeObservers(hostActivity)
                            }
                        } else {
                            navigate(BuyUnitTrustTermsOfUseFragmentDirections.actionBuyUnitTrustTermsOfUseFragmentToGenericResultScreenFragment(failureResultScreen(), true))
                            buyUnitTrustViewModel.buyMoreFundsResponseLiveData.removeObservers(hostActivity)
                        }
                        hideToolBar()
                        hostActivity.hideStepIndicator()
                        dismissProgressDialog()
                    })
                } else {
                    buyUnitTrustViewModel.performUnitTrustAccountApplication()
                    buyUnitTrustViewModel.buyUnitTrustResultResponse.observe(hostActivity, {
                        val unitTrustAccountNumber = it?.processCreateUnitTrustAcctRespDTO?.unitTrustAccountNumber
                        val genericResultScreenProperties = if (unitTrustAccountNumber.isNullOrEmpty()) {
                            failureResultScreen()
                        } else {
                            successResultScreen(unitTrustAccountNumber)
                        }
                        navigate(BuyUnitTrustTermsOfUseFragmentDirections.actionBuyUnitTrustTermsOfUseFragmentToGenericResultScreenFragment(genericResultScreenProperties, true))
                        dismissProgressDialog()
                        hideToolBar()
                        hostActivity.hideStepIndicator()
                    })
                }
            } else {
                acceptTermsCheckBox.setErrorMessage(R.string.plz_accept_conditions)
            }
        }

        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_1, getString(R.string.buy_unit_trust_legal_terms), object : ClickableSpan() {
            override fun onClick(widget: View) = PdfUtil.showPDFInApp(hostActivity, UNIT_TRUST_LEGAL_TERMS)
        }, termOneListItem.contentTextView)

        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_2, getString(R.string.buy_more_units_mdd), object : ClickableSpan() {
            override fun onClick(widget: View) {
                buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund.fundPdfUrl?.let {
                    PdfUtil.showPDFInApp(hostActivity, it)
                }
            }
        }, termTwoListItem.contentTextView)

        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_5, getString(R.string.buy_unit_trust_tax_declaration_form), object : ClickableSpan() {
            override fun onClick(widget: View) = PdfUtil.showPDFInApp(hostActivity, UNIT_TRUST_TAX_DECLARATION)
        }, termFiveListItem.contentTextView)

        initializeSureCheckDelegate()
    }

    private fun initializeSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(hostActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    buyUnitTrustViewModel.buyNewFund()
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                dismissProgressDialog()
            }
        }
    }

    private fun successResultScreen(accountNumber: String): GenericResultScreenProperties {
        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_SuccessScreen_ScreenDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Success")
        }
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(if (buyUnitTrustViewModel.isBuyNewFund) getString(R.string.buy_new_fund_success_title) else getString(R.string.buy_unit_trust_success_title))
                .setDescription(if (buyUnitTrustViewModel.isBuyNewFund) getString(R.string.buy_new_fund_success_message, accountNumber) else getString(R.string.buy_unit_trust_success_result, accountNumber))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    private fun failureResultScreen(): GenericResultScreenProperties {
        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_FailureScreen_ScreenDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Failure")
        }
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.status_unsuccessful).toSentenceCase())
                .setDescription(getString(R.string.buy_unit_trust_unsuccessful_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    companion object {
        const val UNIT_TRUST_LEGAL_TERMS = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/FFS/Disclaimers/Unit_Trusts_TC_en.pdf"
        const val UNIT_TRUST_TAX_DECLARATION = "https://www.absainvestmentmanagement.co.za/content/dam/wimi/investments/pdf/forms/Absa-5191-EX.pdf"
    }
}
