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

package com.barclays.absa.banking.shared.services

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.shared.services.dto.*
import styleguide.forms.SelectorList
import styleguide.utils.extensions.removeSpaceAfterForwardSlash
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class SharedViewModel : BaseViewModel() {
    var sharedService: SharedService = SharedInteractor()

    private val getCifCodesExtendedResponseListener: ExtendedResponseListener<LookupResult> by lazy { GetCIFCodesExtendedResponseListener(this) }
    private val suburbsExtendedResponseListener by lazy { SuburbsExtendedResponseListener(suburbsMutableLiveData) }

    val sourceOfFundsResponse = MutableLiveData<LookupResult?>()
    var selectedSourceOfFunds = MutableLiveData<ArrayList<LookupItem>>()
    var codesLiveData = MutableLiveData<LookupResult>()
    var suburbsMutableLiveData = MutableLiveData<List<SuburbResult>>()

    fun getCIFCodes(cifGroupCode: CIFGroupCode) {
        sharedService.performLookup(cifGroupCode, getCifCodesExtendedResponseListener)
    }

    fun getSuburbs(area: String) {
        sharedService.fetchSuburbs(area, suburbsExtendedResponseListener)
    }

    fun getMatchingLookupIndex(keyword: String, lookupItems: List<LookupItem>): Int {
        return lookupItems.indexOfFirst { it.itemCode == keyword }
    }

    fun getMatchingLookupIndex(keyword: String, lookupItems: SelectorList<CodesLookupDetailsSelector>?): Int {
        return lookupItems?.indexOfFirst { it.itemCode == keyword } ?: -1
    }

    fun buildSortedSelectorList(lookupItems: List<LookupItem>): SelectorList<LookupItem> {
        val selectorList = SelectorList<LookupItem>()
        lookupItems.forEach {
            selectorList.add(LookupItem(it.itemCode, it.groupCode, it.defaultLabel.removeSpaceAfterForwardSlash().toTitleCaseRemovingCommas(), it.cmsKey))
        }
        selectorList.sortBy { lookupItem -> lookupItem.defaultLabel }
        return selectorList
    }
}