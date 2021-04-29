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

package com.barclays.absa.banking.saveAndInvest

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.KeyboardUtils
import styleguide.bars.ProgressIndicatorView
import styleguide.utils.extensions.removeSpaces

const val CASA_REFERENCE = "CASA REFERENCE"
const val PRODUCT_TYPE = "SAVE AND INVEST"
const val NO = "N"
const val YES = "Y"

abstract class SaveAndInvestActivity : BaseActivity() {

    abstract var featureName: String
    abstract var productType: SaveAndInvestProductType
    protected lateinit var progressIndicatorView: ProgressIndicatorView
    protected lateinit var toolbar: View
    private val saveAndInvestViewModel by viewModels<SaveAndInvestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveAndInvestViewModel.casaReference = intent.getStringExtra(CASA_REFERENCE) ?: ""
    }

    fun trackAnalyticsAction(action: String) {
        AnalyticsUtil.trackAction(featureName, "${featureName.removeSpaces()}_${action}")
    }

    fun setProgressStep(step: Int) {
        if (::progressIndicatorView.isInitialized) {
            progressIndicatorView.setNextStep(step)
            progressIndicatorView.animateNextStep()
        }
    }

    fun showProgressIndicatorAndToolbar() {
        if (::progressIndicatorView.isInitialized && ::toolbar.isInitialized) {
            toolbar.visibility = View.VISIBLE
            progressIndicatorView.visibility = View.VISIBLE
        }
    }

    fun hideProgressIndicatorAndToolbar() {
        if (::progressIndicatorView.isInitialized && ::toolbar.isInitialized) {
            toolbar.visibility = View.GONE
            progressIndicatorView.visibility = View.GONE
        }
    }

    fun hideProgressIndicator() {
        if (::progressIndicatorView.isInitialized) {
            progressIndicatorView.visibility = View.GONE
        }
    }

    fun showToolbar() {
        if (::toolbar.isInitialized) {
            toolbar.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        KeyboardUtils.hideKeyboard(this)
        if (currentFragment is SaveAndInvestFundYourAccountFragment) {
            saveAndInvestViewModel.maturityDate = ""
            saveAndInvestViewModel.investmentTerm = MutableLiveData<String>()
        }
        super.onBackPressed()
    }
}