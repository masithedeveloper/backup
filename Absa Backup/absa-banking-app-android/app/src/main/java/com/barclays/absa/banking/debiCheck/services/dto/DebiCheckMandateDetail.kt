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
package com.barclays.absa.banking.debiCheck.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel

class DebiCheckMandateDetail() : BaseModel, KParcelable {

    var messageID: String = ""
    var creationDateTime: String = ""
    var initiatingParty: String = ""
    var instructingAgent: String = ""
    var instructedAgent: String = ""
    var acceptedIndicator: Boolean = false
    var rejectedReasonCode: String = ""
    var clientReference: String = ""
    var contractReference: String = ""
    var contractRefNo: String = ""
    var trackingInd: String = ""
    var trackingIndicator: String = ""
    var debtorAuthRequired: String = ""
    var installmentSequenceType: String = ""
    var frequency: String = ""
    var mandateInitiationDate: String = ""
    var firstCollectDate: String = ""
        set(firstCollectDate) {
            field = firstCollectDate
            firstCollectionDate = field
        }
    var firstCollectionDate: String = ""
    var initialAmount: String = ""
    var maxCollectionAmount: String = ""
    var maxCollectAmount: String = ""
    var installmentAmount: String = ""
    var adjustmentCategory: String = ""
    var adjustmentRate: String = ""
    var adjustmentAmount: String = ""
    var collectionDay: String = ""
    var dateAdjustmentRuleInd: String = ""
    var creditorName: String = ""
    var creditorAbbreviatedShortName: String = ""
    var creditorShortName: String = ""
    var fromAccount: String = ""
    var reason: String = ""
    var debtorAccountNumber: String = ""
    var debtorAccNo: String = ""
    var debitType: String = ""
    var debitValueType: String = ""
    var mandateReferenceNumber: String = ""
    var mandateRequestTransactionId: String = ""
    var amendmentReason: String = ""
    var mandateType: String = ""
    var amendmentChangedItems: List<AmendmentChangedItem> = emptyList()

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { messageID = it }
        parcel.readString()?.let { creationDateTime = it }
        parcel.readString()?.let { initiatingParty = it }
        acceptedIndicator = parcel.readValue(Boolean::class.java.classLoader) as Boolean
        parcel.readString()?.let { instructingAgent = it }
        parcel.readString()?.let { instructedAgent = it }
        parcel.readString()?.let { rejectedReasonCode = it }
        parcel.readString()?.let { clientReference = it }
        parcel.readString()?.let { contractReference = it }
        parcel.readString()?.let { contractRefNo = it }
        parcel.readString()?.let { trackingIndicator = it }
        parcel.readString()?.let { trackingInd = it }
        parcel.readString()?.let { debtorAuthRequired = it }
        parcel.readString()?.let { installmentSequenceType = it }
        parcel.readString()?.let { frequency = it }
        parcel.readString()?.let { mandateInitiationDate = it }
        parcel.readString()?.let { firstCollectionDate = it }
        parcel.readString()?.let { initialAmount = it }
        parcel.readString()?.let { maxCollectionAmount = it }
        parcel.readString()?.let { maxCollectAmount = it }
        parcel.readString()?.let { installmentAmount = it }
        parcel.readString()?.let { adjustmentCategory = it }
        parcel.readString()?.let { adjustmentRate = it }
        parcel.readString()?.let { adjustmentAmount = it }
        parcel.readString()?.let { collectionDay = it }
        parcel.readString()?.let { dateAdjustmentRuleInd = it }
        parcel.readString()?.let { creditorName = it }
        parcel.readString()?.let { creditorAbbreviatedShortName = it }
        parcel.readString()?.let { creditorShortName = it }
        parcel.readString()?.let { fromAccount = it }
        parcel.readString()?.let { reason = it }
        parcel.readString()?.let { debtorAccountNumber = it }
        parcel.readString()?.let { debtorAccNo = it }
        parcel.readString()?.let { debitType = it }
        parcel.readString()?.let { debitValueType = it }
        parcel.readString()?.let { mandateReferenceNumber = it }
        parcel.readString()?.let { mandateRequestTransactionId = it }
        parcel.readString()?.let { amendmentReason = it }
        parcel.readString()?.let { mandateType = it }
        parcel.createTypedArrayList(AmendmentChangedItem.CREATOR)?.let {
            amendmentChangedItems = it
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageID)
        parcel.writeString(creationDateTime)
        parcel.writeString(initiatingParty)
        parcel.writeValue(acceptedIndicator)
        parcel.writeString(instructingAgent)
        parcel.writeString(instructedAgent)
        parcel.writeString(rejectedReasonCode)
        parcel.writeString(clientReference)
        parcel.writeString(contractReference)
        parcel.writeString(contractRefNo)
        parcel.writeString(trackingIndicator)
        parcel.writeString(trackingInd)
        parcel.writeString(debtorAuthRequired)
        parcel.writeString(installmentSequenceType)
        parcel.writeString(frequency)
        parcel.writeString(mandateInitiationDate)
        parcel.writeString(firstCollectionDate)
        parcel.writeString(initialAmount)
        parcel.writeString(maxCollectionAmount)
        parcel.writeString(maxCollectAmount)
        parcel.writeString(installmentAmount)
        parcel.writeString(adjustmentCategory)
        parcel.writeString(adjustmentRate)
        parcel.writeString(adjustmentAmount)
        parcel.writeString(collectionDay)
        parcel.writeString(dateAdjustmentRuleInd)
        parcel.writeString(creditorName)
        parcel.writeString(creditorAbbreviatedShortName)
        parcel.writeString(creditorShortName)
        parcel.writeString(fromAccount)
        parcel.writeString(reason)
        parcel.writeString(debtorAccountNumber)
        parcel.writeString(debtorAccNo)
        parcel.writeString(debitType)
        parcel.writeString(debitValueType)
        parcel.writeString(mandateReferenceNumber)
        parcel.writeString(mandateRequestTransactionId)
        parcel.writeString(amendmentReason)
        parcel.writeString(mandateType)
        parcel.writeTypedList(amendmentChangedItems)
    }

    companion object CREATOR : Parcelable.Creator<DebiCheckMandateDetail> {
        override fun createFromParcel(parcel: Parcel): DebiCheckMandateDetail {
            return DebiCheckMandateDetail(parcel)
        }

        override fun newArray(size: Int): Array<DebiCheckMandateDetail?> {
            return arrayOfNulls(size)
        }
    }
}