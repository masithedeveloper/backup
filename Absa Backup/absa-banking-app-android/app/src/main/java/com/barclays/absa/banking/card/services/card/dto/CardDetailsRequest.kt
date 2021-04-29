/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.services.card.dto

import android.util.Base64
import com.barclays.absa.banking.card.services.card.CardService.OP2132_VIEW_CARD_DETAILS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.crypto.AsymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper

class CardDetailsRequest<T>(val cardNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        val cryptoHelper = SymmetricCryptoHelper.getInstance()
        val symmetricKeyBuffer = cryptoHelper.secretKeyBytes
        val publicKeyEncryptedSymmetricKeyBuffer = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKeyBuffer)
        val base64EncodedEncryptedSymmetricKey: String = Base64.encodeToString(publicKeyEncryptedSymmetricKeyBuffer, Base64.NO_WRAP)

        val iv = cryptoHelper.randomIVBytes
        val base64EncodedIV: String = Base64.encodeToString(iv, Base64.NO_WRAP)

        params = RequestParams.Builder(OP2132_VIEW_CARD_DETAILS)
                .put("symmetricKey", base64EncodedEncryptedSymmetricKey)
                .put("initializationVector", base64EncodedIV)
                .put("cardNumber", cardNumber)
                .build()

        mockResponseFile = "card/op2132_card_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = VirtualCardDetailsResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}