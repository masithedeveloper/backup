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
 *
 */
package com.barclays.absa.banking.card.ui.debitCard.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList
import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees
import com.barclays.absa.banking.boundary.model.ClientContactInformationList
import com.barclays.absa.banking.boundary.model.debitCard.*
import com.barclays.absa.banking.card.services.card.DebitCardInteractor
import com.barclays.absa.banking.card.services.card.dto.BranchDeliveryDetailsListRequest
import com.barclays.absa.banking.card.services.card.dto.ClientContactInformationRequest
import com.barclays.absa.banking.card.services.card.dto.DebitCardProductTypeListRequest
import com.barclays.absa.banking.card.services.card.dto.DebitCardReplacementReasonListRequest
import com.barclays.absa.banking.card.ui.debitCard.services.*
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class DebitCardDetailsViewModel : BaseViewModel() {
    var debitCardInteractor = DebitCardInteractor()
    var debitCardReplacementDetailsConfirmation = DebitCardReplacementDetailsConfirmation()
    var selectedPreferredBranch = -1

    private val cardReplacementAndFetchFeesExtendedResponseListener: ExtendedResponseListener<CardReplacementAndFetchFees> by lazy { CardReplacementAndFetchFeesExtendedResponseListener(this) }
    private val debitCardReasonListExtendedResponseListener: ExtendedResponseListener<DebitCardReplacementReasonList> by lazy { DebitCardReasonListExtendedResponseListener(this) }
    private val debitCardProductTypeListExtendedResponseListener: ExtendedResponseListener<DebitCardProductTypeList> by lazy { DebitCardProductTypeListExtendedResponseListener(this) }
    private val debitCardTypeExtendedResponseListener: ExtendedResponseListener<DebitCardTypeList> by lazy { DebitCardTypeExtendedResponseListener(this) }
    private val clientContactInformationListExtendedResponseListener: ExtendedResponseListener<ClientContactInformationList> by lazy { ClientContactInformationListExtendedResponseListener(this) }
    private val branchDeliveryDetailsListExtendedResponseListener: ExtendedResponseListener<BranchDeliveryDetailsList> by lazy { BranchDeliveryDetailsListExtendedResponseListener(this) }
    private val debitCardReplacementConfirmationResponseListener: ExtendedResponseListener<DebitCardReplacementConfirmation> by lazy { DebitCardReplacementConfirmationResponseListener(this) }

    var cardReplacementAndFetchFeesExtendedResponse = MutableLiveData<CardReplacementAndFetchFees>()
    var debitCardReasonListExtendedResponse = MutableLiveData<DebitCardReplacementReasonList>()
    var debitCardProductTypeListExtendedResponse = MutableLiveData<DebitCardProductTypeList>()
    var debitCardTypeExtendedResponse = MutableLiveData<DebitCardTypeList>()
    var clientContactInformationListExtendedResponse = MutableLiveData<ClientContactInformationList>()
    var branchDeliveryDetailsListExtendedResponse = MutableLiveData<BranchDeliveryDetailsList>()
    var debitCardReplacementConfirmationResponse = MutableLiveData<DebitCardReplacementConfirmation>()

    lateinit var debitCardRequestList: ArrayList<ExtendedRequest<*>>

    fun fetchDebitCardData() {
        debitCardRequestList = ArrayList<ExtendedRequest<*>>().apply {
            add(ClientContactInformationRequest(clientContactInformationListExtendedResponseListener))
            add(DebitCardReplacementReasonListRequest(debitCardReasonListExtendedResponseListener))
            add(DebitCardProductTypeListRequest(debitCardProductTypeListExtendedResponseListener))
            add(BranchDeliveryDetailsListRequest(branchDeliveryDetailsListExtendedResponseListener))
            debitCardInteractor.fetchDebitCardData(this)
        }
    }

    fun cardTypeSelected(productType: DebitCardProductType) {
        debitCardInteractor.fetchCardTypeList(productType, debitCardTypeExtendedResponseListener)
    }

    fun fetchReplacementFee(debitCardReplacementDetailsConfirmation: DebitCardReplacementDetailsConfirmation) {
        debitCardInteractor.fetchReplacementFee(debitCardReplacementDetailsConfirmation, cardReplacementAndFetchFeesExtendedResponseListener)
    }

    fun debitCardReplacement(debitCardReplacementDetailsConfirmation: DebitCardReplacementDetailsConfirmation) {
        debitCardInteractor.replaceCard(debitCardReplacementDetailsConfirmation, debitCardReplacementConfirmationResponseListener)
    }
}