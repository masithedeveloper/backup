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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.boundary.model.MeterNumberObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.INSERT_SPACE_AFTER_FOUR_DIGIT
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.prepaid_electricity_someone_new_meter_details_fragment.*
import styleguide.utils.extensions.insertSpaceAtIncrements
import java.util.*

class PrepaidElectricitySomeoneNewMeterDetailsFragment : BaseFragment(R.layout.prepaid_electricity_someone_new_meter_details_fragment) {

    private var prepaidElectricityView: PrepaidElectricityView? = null
    private val beneficiaryCacheService: IBeneficiaryCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepaidElectricityView = activity as? PrepaidElectricityView
        prepaidElectricityView?.setToolbarTitle(getString(R.string.prepaid_electricity_meter_details_toolbar_title))

        val meterNumberObject = prepaidElectricityView?.meterNumberObject
        meterNumberObject?.let { initViews(it) } ?: showGenericErrorMessageThenFinish()

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_MeterDetailsScreen_ScreenDisplayed")
    }

    private fun initViews(meterNumberObject: MeterNumberObject) {
        CommonUtils.setInputFilter(nameNormalInputView.editText)

        meterNumberSecondaryContentAndLabelView.setContentText(meterNumberObject.meterNumber.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT))
        customerNameForMeterSecondaryContentAndLabelView.setContentText(meterNumberObject.customerName)
        addressSecondaryContentAndLabelView.setContentText(meterNumberObject.customerAddress)
        utilityProviderSecondaryContentAndLabelView.setContentText(meterNumberObject.utility)
        arrearsAmountSecondaryContentAndLabelView.setContentText(String.format(Locale.ENGLISH, "R ${TextFormatUtils.formatBasicAmount(meterNumberObject.arrearsAmount)}"))

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_MeterDetailsScreen_ContinueButtonClicked")

            beneficiaryCacheService.setTabPositionToReturnTo("PPE")
            if (nameNormalInputView.selectedValue.isNotEmpty()) {
                prepaidElectricityView?.addPrepaidElectricityBeneficiary(nameNormalInputView.selectedValue, meterNumberObject.meterNumber, meterNumberObject.utility)
            } else {
                val errorMessage = String.format(getString(R.string.invalidError), getString(R.string.beneficiary_name_prepaid).toLowerCase(Locale.getDefault()))
                nameNormalInputView.setError(errorMessage)
            }
        }
    }
}