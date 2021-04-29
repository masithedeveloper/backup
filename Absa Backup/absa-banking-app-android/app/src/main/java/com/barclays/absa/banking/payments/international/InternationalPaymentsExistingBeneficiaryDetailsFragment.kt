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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.InternationalCountryList
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters.Companion.NON_RESIDENT_OTHER
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryDetails
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_existing_beneficiary_details.*
import styleguide.content.BeneficiaryListItem
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import java.util.*

class InternationalPaymentsExistingBeneficiaryDetailsFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_existing_beneficiary_details),
        InternationalPaymentsContract.InternationalPaymentsCountryListView {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private var westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails? = null
    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsCountryListPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PayExistingBeneficiaryDetailsScreen_ScreenDisplayed")
        presenter = InternationalPaymentsCountryListPresenter(this)
        setToolBar(getString(R.string.beneficiary_details))
        internationalPaymentsActivity.setProgressStep(2)
        internationalPaymentsActivity.showProgressIndicator()

        westernUnionBeneficiaryDetails = if (savedInstanceState != null) {
            internationalPaymentCacheService.getBeneficiaryDetails()
        } else {
            internationalPaymentsActivity.westernUnionBeneficiaryDetails
        }

        beneficiaryView.setBeneficiary(BeneficiaryListItem(westernUnionBeneficiaryDetails?.beneficiaryFirstName?.split("\\s".toRegex())?.first(), "", ""))

        val westernUnionDetails = westernUnionBeneficiaryDetails?.westernUnionDetails
        val gender = if ("M".equals(westernUnionDetails?.gender, true)) getString(R.string.male) else getString(R.string.female)

        beneficiaryGenderSecondaryContentAndLabelView.setContentText(gender)
        beneficiaryAddressSecondaryContentAndLabelView.setContentText(westernUnionDetails?.streetAddress)
        beneficiaryCitySecondaryContentAndLabelView.setContentText(westernUnionDetails?.residingCountry?.cityName)
        beneficiaryCountrySecondaryContentAndLabelView.setContentText(westernUnionDetails?.residingCountry?.countryDescription)

        val stateName = westernUnionDetails?.residingCountry?.stateName ?: ""
        if (stateName.isNotEmpty()) {
            beneficiaryStateSecondaryContentAndLabelView.apply {
                visibility = View.VISIBLE
                setContentText(stateName)
            }
        }
        beneficiaryResidentialStatusLabel.text = getString(R.string.beneficiary_residential_status, internationalPaymentsActivity.flowTypeString)

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PayExistingBeneficiaryDetailsScreen_NextButtonClicked")
            presenter.fetchListOfCountries()

            val beneficiaryDetails = BeneficiaryEnteredDetails().apply {
                beneficiaryNames = westernUnionBeneficiaryDetails?.beneficiaryFirstName
                beneficiarySurname = westernUnionBeneficiaryDetails?.beneficiarySurname
                beneficiaryCitizenship = residentialStatusRadioButtonView.selectedValue?.displayValue
            }
            internationalPaymentsActivity.updateBeneficiaryBeneficiaryDetailsDataModel(beneficiaryDetails)
        }

        setUpRadioButtons()
    }

    override fun countryListReturned(westernUnionCountries: ArrayList<InternationalCountryList>) {
        navigate(InternationalPaymentsExistingBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsExistingBeneficiaryDetailsFragmentToInternationalPaymentsPaymentDetailsFragment(westernUnionCountries.toTypedArray()))
    }

    override fun getLifecycleCoroutineScope() = lifecycleScope

    private fun setUpRadioButtons() {
        residentialStatusRadioButtonView.apply {
            setDataSource(SelectorList<StringItem>().apply {
                add(StringItem(getString(R.string.temporarily_abroad)))
                add(StringItem(getString(R.string.non_sa_resident)))
            })
            selectedIndex = if (NON_RESIDENT_OTHER.toLowerCase(Locale.getDefault()) == westernUnionBeneficiaryDetails?.nonResidentAccountIdentifier?.toLowerCase(Locale.getDefault())) 1 else 0
        }
    }
}