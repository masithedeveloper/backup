/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.manage.profile.ui.addressDetails

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.manage.profile.services.ManageProfileInteractor
import com.barclays.absa.banking.manage.profile.services.ManageProfileService
import com.barclays.absa.banking.manage.profile.services.dto.PostalCodeLookupItem
import com.barclays.absa.banking.manage.profile.services.responselisteners.SuburbLookupExtendedResponseListener
import com.barclays.absa.banking.manage.profile.ui.models.SuburbLookupResults
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class ManageProfileAddressDetailsViewModel : BaseViewModel() {
    private val suburbLookupExtendedResponseListener = SuburbLookupExtendedResponseListener(this)
    var suburbLookupResults = MutableLiveData<ArrayList<SuburbLookupResults>>()
    var isPoBox: Boolean = false

    private val manageProfileInteractor: ManageProfileService by lazy { ManageProfileInteractor() }

    var selectedPostSuburb = SuburbLookupResults()

    fun fetchPostalCodes(isPoBox: Boolean, area: String) {
        this.isPoBox = isPoBox
        manageProfileInteractor.fetchSuburbs(area, suburbLookupExtendedResponseListener)
    }

    fun fetchSuburbLookupValues(postalCodeLookUpResponse: ArrayList<PostalCodeLookupItem>) {
        val suburbLookupResultList = ArrayList<SuburbLookupResults>()
        postalCodeLookUpResponse.forEach { lookupItem ->
            if (!isPoBox) {
                SuburbLookupResults().apply {
                    suburbPostalCode = lookupItem.streetPostCode
                    suburbName = lookupItem.suburbName
                    townName = lookupItem.townOrCityName
                    suburbLookupResultList.add(this)
                }
            }

            if (isPoBox && !lookupItem.streetPostCode.equals(lookupItem.postalCode, true)) {
                SuburbLookupResults().apply {
                    suburbPostalCode = lookupItem.postalCode
                    suburbName = lookupItem.suburbName
                    townName = lookupItem.townOrCityName
                    suburbLookupResultList.add(this)
                }
            }
        }
        suburbLookupResults.value = suburbLookupResultList
    }

    fun clearData() {
        suburbLookupResults = MutableLiveData()
        selectedPostSuburb = SuburbLookupResults()
    }
}