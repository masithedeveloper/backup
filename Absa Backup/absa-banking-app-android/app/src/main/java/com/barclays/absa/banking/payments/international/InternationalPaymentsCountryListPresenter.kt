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

package com.barclays.absa.banking.payments.international

import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.payments.international.data.InternationalCountryList
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionCountryListExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionCountriesListResponse
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class InternationalPaymentsCountryListPresenter(globalView: InternationalPaymentsContract.InternationalPaymentsCountryListView) : AbstractPresenter(WeakReference(globalView)), InternationalPaymentsContract.InternationalPaymentsCountryListPresenter {

    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val westernUnionBeneficiaryListExtendedResponseListener: WesternUnionCountryListExtendedResponseListener by lazy { WesternUnionCountryListExtendedResponseListener(this) }
    private lateinit var internationalCountryList: InternationalCountryList

    override fun fetchListOfCountries() {
        internationalPaymentsInteractor.getAllWesternUnionCountriesList(westernUnionBeneficiaryListExtendedResponseListener)
    }

    fun countryListReturned(westernUnionCountriesList: WesternUnionCountriesListResponse) {
        val listOfInternationalCountries: ArrayList<InternationalCountryList> = arrayListOf()
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsCountryListView

        view.getLifecycleCoroutineScope().launch {
            westernUnionCountriesList.westernUnionCountriesList?.westernUnionCountries?.forEach { westernUnionCountry ->
                internationalCountryList = InternationalCountryList()
                internationalCountryList.countryCode = westernUnionCountry.countryCode
                internationalCountryList.countryDescription = westernUnionCountry.countryDescription
                internationalCountryList.requiresSecurityQuestion = westernUnionCountry.displayQuestionsAndAnswers
                listOfInternationalCountries.add(internationalCountryList)
            }

            view.countryListReturned(listOfInternationalCountries)
            dismissProgressIndicator()
        }
    }
}