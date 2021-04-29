/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem

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
import kotlinx.android.synthetic.main.switch_unit_trust_fund_terms_and_conditions_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class SwitchTermsAndConditionsFragment : SwitchFundBaseFragment(R.layout.switch_unit_trust_fund_terms_and_conditions_fragment) {

    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var fromUnitTrustFundName: String
    private lateinit var toUnitTrustFundName: String

    companion object {
        const val LEGAL_TERMS_PDF_LINK = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/FFS/Disclaimers/Unit_Trusts_TC_en.pdf"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_terms_and_conditions))
        hostActivity.progressIndicatorStep(3)

        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
            fromUnitTrustFundName = fromFundName
            toUnitTrustFundName = toFundName
        }
        setupUI()
        initializeSureCheckDelegate()
    }

    private fun setupUI() {
        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_1, getString(R.string.buy_unit_trust_legal_terms), object : ClickableSpan() {
            override fun onClick(widget: View) = PdfUtil.showPDFInApp(hostActivity, LEGAL_TERMS_PDF_LINK)
        }, firstTermListItem.contentTextView)

        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_2, getString(R.string.buy_more_units_mdd), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(hostActivity, viewUnitTrustViewModel.switchToFundPdfUrl)
            }
        }, secondTermListItem.contentTextView)

        acceptButton.setOnClickListener {
            if (acceptTermsAndConditionsCheckBoxView.isChecked) {
                viewUnitTrustViewModel.switchUnitTrustFund()
                viewUnitTrustViewModel.switchFundResponseLiveData.observe(hostActivity, {
                    if (it?.transactionStatus.equals(BMBApplication.CONST_SUCCESS, true)) {
                        sureCheckDelegate.processSureCheck(hostActivity, it) {
                            navigate(SwitchTermsAndConditionsFragmentDirections.actionSwitchFundTermsAndConditionsFragmentToGenericResultScreenFragment(
                                    successResultScreen(fromUnitTrustFundName, toUnitTrustFundName), true)
                            )
                            viewUnitTrustViewModel.switchFundResponseLiveData.removeObservers(hostActivity)
                            dismissProgressDialog()
                        }
                    } else {
                        navigate(SwitchTermsAndConditionsFragmentDirections.actionSwitchFundTermsAndConditionsFragmentToGenericResultScreenFragment(failureResultScreen(), true))
                    }
                    hideToolBar()
                    hostActivity.hideProgressIndicator()
                })
            } else {
                acceptTermsAndConditionsCheckBoxView.setErrorMessage(R.string.plz_accept_conditions)
            }
        }
    }

    private fun initializeSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(hostActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewUnitTrustViewModel.switchUnitTrustFund()
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                dismissProgressDialog()
            }
        }
    }

    private fun successResultScreen(fromUnitTrustFund: String, toUnitTrustFund: String): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.buy_new_fund_success_title))
                .setDescription(getString(R.string.switch_fund_success_message, fromUnitTrustFund.toTitleCase(), toUnitTrustFund.toTitleCase()))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    private fun failureResultScreen(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.status_unsuccessful).toSentenceCase())
                .setDescription(getString(R.string.buy_unit_trust_unsuccessful_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}