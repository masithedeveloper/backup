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

import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionCitiesListExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionStatesListExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionCountriesListResponse
import styleguide.utils.extensions.toTitleCaseRemovingCommas
import java.lang.ref.WeakReference

class InternationalPaymentsPaymentDetailsPresenter(globalView: InternationalPaymentsContract.InternationalPaymentsExtraDetailsView) : AbstractPresenter(WeakReference(globalView)), InternationalPaymentsContract.InternationalPaymentsExtraDetailsPresenter {

    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val westernUnionStatesListExtendedResponseListener: WesternUnionStatesListExtendedResponseListener by lazy { WesternUnionStatesListExtendedResponseListener(this) }
    private val westernUnionCitiesListExtendedResponseListener: WesternUnionCitiesListExtendedResponseListener by lazy { WesternUnionCitiesListExtendedResponseListener(this) }

    override fun countryChanged(countryCode: String) {
        showProgressIndicator()
        internationalPaymentsInteractor.getAllWesternUnionStatesList(countryCode, westernUnionStatesListExtendedResponseListener)
    }

    override fun stateListReturned(westernUnionCountries: WesternUnionCountriesListResponse?) {
        val listOfStates: ArrayList<String> = arrayListOf()
        val stateList = westernUnionCountries?.westernUnionCountriesList?.westernUnionCountries?.get(0)?.westernUnionStatesList
        stateList?.forEach { westernUnionStates ->
            westernUnionStates.stateName?.let { listOfStates.add(it.toTitleCaseRemovingCommas()) }
        }
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsExtraDetailsView

        when {
            listOfStates.size > 0 -> view.populateStateList(listOfStates)
            else -> view.noStateRequired()
        }

        dismissProgressIndicator()
    }

    override fun stateChanged(countryCode: String, state: String) {
        showProgressIndicator()
        internationalPaymentsInteractor.getAllWesternUnionCitiesList(countryCode, state, westernUnionCitiesListExtendedResponseListener)
    }

    override fun cityListReturned(westernUnionCountriesListResponse: WesternUnionCountriesListResponse) {
        val listOfCities: ArrayList<String> = arrayListOf()
        val cityList = westernUnionCountriesListResponse.westernUnionCountriesList?.westernUnionCountries?.get(0)?.westernUnionStatesList?.get(0)?.citiesList
        cityList?.forEach { cityName ->
            cityName.let { listOfCities.add(it.toTitleCaseRemovingCommas()) }
        }
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsExtraDetailsView
        when {
            listOfCities.size > 0 -> view.populateLongCityList(listOfCities)
            else -> view.allowUserToEnterCity()
        }
        dismissProgressIndicator()
    }

    override fun cityEnteredOrChanged(shouldShowSecurityQuestionAndAnswer: String) {
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsExtraDetailsView
        if (shouldShowSecurityQuestionAndAnswer == "Y") {
            view.showSecurityQuestion()
        } else {
            view.hideSecurityQuestion()
        }
    }
}