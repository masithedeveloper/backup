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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.prepaid_electricity_recipient_meter_number_fragment.*
import java.util.*

class PrepaidElectricityRecipientMeterNumberFragment : BaseFragment(R.layout.prepaid_electricity_recipient_meter_number_fragment) {

    private var prepaidElectricityView: PrepaidElectricityView? = null
    private var infoMenuItem: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setToolBar(getString(R.string.prepaid_electricity_buy_for_someone_new_toolbar_title))

        // TODO: Remove at some point
        AnalyticsUtils.getInstance().trackCustomScreenView(BUY_ELECTRICITY_FOR_SOMEONE_NEW_ENTER_METER_NUMBER_CONST, BUY_PREPAID_ELECTRICITY_CONST, TRUE_CONST)

        prepaidElectricityView = activity as PrepaidElectricityView?
        prepaidElectricityView?.setToolbarIcon(R.drawable.ic_arrow_back_dark)
        initViews()

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_ElectricitySomeoneNewScreen_ScreenDisplayed")
    }

    private fun initViews() {
        recipientMeterNumberNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                recipientMeterNumberNormalInputView.hideError()
                if (recipientMeterNumberNormalInputView.selectedValueUnmasked.isNotEmpty() && checkForDuplicateMeter()) {
                    showMeterError(getString(R.string.ben_exist_dialog_msg))
                    return
                }
                validateInput()
            }
        })

        doneButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_ElectricitySomeoneNewScreen_DoneButtonClicked")

            if (checkForDuplicateMeter()) {
                prepaidElectricityView?.showBeneficiaryExistDialog()
            } else {
                prepaidElectricityView?.validateRecipientMeterNumber(recipientMeterNumberNormalInputView.selectedValueUnmasked)
            }
        }

        validateInput()
    }

    private fun checkForDuplicateMeter(): Boolean {
        val beneficiaryListObject = prepaidElectricityView?.beneficiaryListObject
        val electricityBeneficiaryList = beneficiaryListObject?.electricityBeneficiaryList
                ?: ArrayList<BeneficiaryObject>()

        for (beneficiary in electricityBeneficiaryList) {
            if (recipientMeterNumberNormalInputView.selectedValueUnmasked == beneficiary?.beneficiaryAccountNumber) {
                return true
            }
        }
        return false
    }

    private fun validateInput() {
        doneButton.isEnabled = recipientMeterNumberNormalInputView.selectedValue.isNotEmpty()
    }

    fun showMeterError(error: String) {
        infoMenuItem?.isVisible = true

        val errorMessage = when {
            error.isEmpty() -> getString(R.string.generic_error)
            "Common/General/Host/Error/H0549".equals(error, ignoreCase = true) -> getString(R.string.invalid_or_blocked_meter_number)
            "Payments/PurchasePrepaid/Validation/TandemTimeOut_RC91".equals(error, ignoreCase = true) -> getString(R.string.claim_error_text)
            else -> error
        }

        recipientMeterNumberNormalInputView?.setError(errorMessage)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        infoMenuItem = menu.findItem(R.id.action_info)
        infoMenuItem?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item === infoMenuItem) {
            prepaidElectricityView?.navigateToImportantInformationFragment()
        }
        return super.onOptionsItemSelected(item)
    }
}