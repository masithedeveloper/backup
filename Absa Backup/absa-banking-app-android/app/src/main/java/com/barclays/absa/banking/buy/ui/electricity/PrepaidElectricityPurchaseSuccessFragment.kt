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
package com.barclays.absa.banking.buy.ui.electricity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricityToken
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.INSERT_SPACE_AFTER_FOUR_DIGIT
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FragmentPermissionHelper
import com.barclays.absa.utils.TorchUtil
import kotlinx.android.synthetic.main.prepaid_electricity_purchase_successful_fragment.*
import styleguide.content.PrimaryContentAndLabelView
import styleguide.utils.extensions.insertSpaceAtIncrements

class PrepaidElectricityPurchaseSuccessFragment : BaseFragment(R.layout.prepaid_electricity_purchase_successful_fragment), TorchUtil.TorchConfiguredCallback {
    private lateinit var prepaidElectricityView: PrepaidElectricityView
    private var prepaidElectricityTokens: List<PrepaidElectricityToken>? = null
    private var torch: TorchUtil? = null
    private lateinit var prepaidElectricityResponse: PrepaidElectricity

    companion object {
        const val PREPAID_ELECTRICITY_RESPONSE = "prepaidElectricityResponse"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepaidElectricityView = activity as PrepaidElectricityView
        prepaidElectricityView.hideToolbar()

        prepaidElectricityResponse = if (savedInstanceState == null) {
            prepaidElectricityView.prepaidElectricityResponse
        } else {
            savedInstanceState.getSerializable(PREPAID_ELECTRICITY_RESPONSE) as PrepaidElectricity
        }

        val beneficiaryTokens = prepaidElectricityResponse.beneficiaryTokens
        if (beneficiaryTokens != null) {
            prepaidElectricityTokens = beneficiaryTokens.normalTokens
        } else {
            prepaidElectricityView.showGenericErrorMessage()
            baseActivity.finish()
        }

        initViews()
        setupComponentListeners()
        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseSuccessfulScreen_ScreenDisplayed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::prepaidElectricityResponse.isInitialized) {
            outState.putSerializable(PREPAID_ELECTRICITY_RESPONSE, prepaidElectricityResponse)
        }
        super.onSaveInstanceState(outState)
    }

    private fun initViews() {
        setTorchToInitialOffState()
        if (!prepaidElectricityTokens.isNullOrEmpty()) {
            prepaidElectricityTokens?.forEach {
                showTokens(it.tokenNumber.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT), it.tokenDescription)
            }
        } else {
            showTokens(getString(R.string.prepaid_electricity_arrears_error), "")
        }
    }

    private fun setupComponentListeners() {
        doneButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseSuccessfulScreen_DoneButtonClicked")
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
            prepaidElectricityView.doneClicked()
        }

        viewReceiptButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseSuccessfulScreen_ViewReceiptButtonClicked")
            prepaidElectricityView.navigateToPurchaseReceiptFragment()
        }

        torchOffFloatingActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseSuccessfulScreen_TurnTorchOffButtonClicked")
            requestCameraPermissions()
        }

        torchOnFloatingActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseSuccessfulScreen_TurnTorchOnButtonClicked")
            requestCameraPermissions()
        }
    }

    private fun toggleTorch() {
        if (torch!!.toggleTorch()) {
            torchOffFloatingActionButtonView.visibility = View.INVISIBLE
            torchOnFloatingActionButtonView.visibility = View.VISIBLE
        } else {
            torchOnFloatingActionButtonView.visibility = View.INVISIBLE
            torchOffFloatingActionButtonView.visibility = View.VISIBLE
        }
    }

    private fun setTorchToInitialOffState() {
        val hasFeature = context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
                ?: false

        if (hasFeature) {
            torchOffFloatingActionButtonView.visibility = View.VISIBLE
            torchOnFloatingActionButtonView.visibility = View.INVISIBLE
        } else {
            torchOffFloatingActionButtonView.visibility = View.GONE
            torchOnFloatingActionButtonView.visibility = View.GONE
        }
    }

    private fun requestCameraPermissions() {
        FragmentPermissionHelper.requestCameraAccessPermission(this) {
            if (torch == null) {
                torch = context?.let { TorchUtil(it, this) }
            } else {
                toggleTorch()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == FragmentPermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (torch == null) {
                    torch = context?.let { TorchUtil(it, this) }
                }
            }
        }
    }

    private fun showTokens(tokenNumber: String?, tokenDescription: String?) {
        tokensLinearLayout.addView(PrimaryContentAndLabelView(context).apply {
            setContentText(tokenNumber)
            setLabelText(tokenDescription)
        })
    }

    override fun onResume() {
        super.onResume()
        setTorchToInitialOffState()
    }

    override fun onPause() {
        super.onPause()
        torch?.setTorchModeOff()
    }

    override fun onDestroy() {
        super.onDestroy()
            torch?.closeCamera()
    }

    override fun configureComplete() {
        toggleTorch()
    }
}