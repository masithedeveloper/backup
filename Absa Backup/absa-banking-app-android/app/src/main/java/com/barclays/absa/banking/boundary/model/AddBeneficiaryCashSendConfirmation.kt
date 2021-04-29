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
package com.barclays.absa.banking.boundary.model

class AddBeneficiaryCashSendConfirmation : AddBeneficiaryCashSendConfirmationObject() {

    var addUpdBen: AddUpdBen? = null

    override var cellNumber: String?
        get() = if (addUpdBen != null) addUpdBen!!.cellNo else super.cellNumber
        set(value: String?) {
            super.cellNumber = value
        }

    override var firstName: String?
        get() = if (addUpdBen != null) addUpdBen!!.benNam else super.firstName
        set(value: String?) {
            super.firstName = value
        }

    override var surname: String?
        get() = if (addUpdBen != null) addUpdBen!!.benSurNam else super.surname
        set(value: String?) {
            super.surname = value
        }

    override var nickName: String?
        get() = if (addUpdBen != null) addUpdBen!!.shortName else super.nickName
        set(value: String?) {
            super.nickName = value
        }

    override var myReference: String?
        get() = if (addUpdBen != null) addUpdBen!!.myRef else super.myReference
        set(value: String?) {
            super.myReference = value
        }

    override var beneficiaryType: String?
        get() = if (addUpdBen != null) addUpdBen!!.benTyp else super.beneficiaryType
        set(value: String?) {
            super.beneficiaryType = value
        }

    override var favourites: String?
        get() = if (addUpdBen != null) addUpdBen!!.isFav else super.favourites
        set(value: String?) {
            super.favourites = value
        }

    override var beneficiaryId: String?
        get() = if (addUpdBen != null) addUpdBen!!.benId else super.beneficiaryId
        set(value: String?) {
            super.beneficiaryId = value
        }
}