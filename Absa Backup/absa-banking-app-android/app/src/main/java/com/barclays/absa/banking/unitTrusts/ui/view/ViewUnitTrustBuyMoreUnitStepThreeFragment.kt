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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustBuyMoreUnitStepThreeFragmentBinding
import com.barclays.absa.banking.debiCheck.ui.DebiCheckContracts
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.utils.viewModel
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class ViewUnitTrustBuyMoreUnitStepThreeFragment : AbsaBaseFragment<ViewUnitTrustBuyMoreUnitStepThreeFragmentBinding>() {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var hostActivity: ViewUnitTrustHostActivity

    override fun getLayoutResourceId(): Int = R.layout.view_unit_trust_buy_more_unit_step_three_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as ViewUnitTrustHostActivity
        viewUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()

        binding.acceptButton.setOnClickListener {
            if (binding.acceptTermsCheckBox.isChecked) {
                setupObservers()
                viewUnitTrustViewModel.buyMoreFunds(false)
            } else {
                binding.acceptTermsCheckBox.setErrorMessage(R.string.plz_accept_conditions)
            }
        }
        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_1, getString(R.string.buy_unit_trust_legal_terms), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(activity as BaseActivity, UNIT_TRUST_LEGAL_TERMS)
            }
        }, binding.termOneListItem.contentTextView)

        CommonUtils.makeTextClickable(hostActivity, R.string.buy_unit_trust_terms_of_use_2, getString(R.string.buy_more_units_mdd), object : ClickableSpan() {
            override fun onClick(widget: View) {
                val pdfUrl = viewUnitTrustViewModel.buyMoreUnitsData.value?.fund?.fundPdfUrl
                pdfUrl?.let {
                    PdfUtil.showPDFInApp(activity as BaseActivity, it)
                }
            }
        }, binding.termTwoListItem.contentTextView)

        initialiseSureCheckDelegate()
    }

    private fun setUpToolbar() {
        val hostActivity = activity as ViewUnitTrustHostActivity
        hostActivity.apply {
            setToolBar(getString(R.string.buy_unit_trust_terms_and_conditions))
            showProgressIndicator()
            setStep(3)
        }
    }

    private fun initialiseSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(activity as BaseActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewUnitTrustViewModel.buyMoreFunds(true)
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                dismissProgressDialog()
            }
        }
    }

    private fun navigateToSuccessResultScreen(fundName: String?) {
        hideToolBar()
        (activity as ViewUnitTrustHostActivity).hideProgressIndicator()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.succes_text))
                .setDescription(getString(R.string.view_unit_trust_successfull_buy_units, fundName))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            baseActivity.loadAccountsAndShowHomeScreenWithAccountsList()
        }
        navigate(ViewUnitTrustBuyMoreUnitStepThreeFragmentDirections.actionViewUnitTrustBuyMoreUnitStepThreeFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureResultScreen() {
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.status_unsuccessful).toSentenceCase())
                .setDescription(getString(R.string.buy_unit_trust_unsuccessful_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        navigate(ViewUnitTrustBuyMoreUnitStepThreeFragmentDirections.actionViewUnitTrustBuyMoreUnitStepThreeFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun setupObservers() {
        viewUnitTrustViewModel.buyMoreFundsResponseLiveData.observe(hostActivity, {
            if (it?.transactionStatus.equals(BMBApplication.CONST_SUCCESS, true)) {
                sureCheckDelegate.processSureCheck(activity as BaseActivity, it) {
                    navigateToSuccessResultScreen(viewUnitTrustViewModel.buyMoreUnitsData.value?.fund.let { fund -> fund?.fundName.toTitleCase() })
                    viewUnitTrustViewModel.buyMoreFundsResponseLiveData.removeObservers(hostActivity)
                }
            } else {
                navigateToFailureResultScreen()
                viewUnitTrustViewModel.buyMoreFundsResponseLiveData.removeObservers(hostActivity)
            }
            dismissProgressDialog()
        })
    }

    companion object {
        const val UNIT_TRUST_LEGAL_TERMS = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/FFS/Disclaimers/Unit_Trusts_TC_en.pdf"
    }
}