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

package com.barclays.absa.banking.framework

import com.barclays.absa.banking.BuildConfig
import za.co.absa.networking.ExpressNetworkingConfig

object BuildConfigHelper {

    var docHandlerHostNameBaseUrl = BuildConfig.docHandlerHostNameBaseUrl
    var docHandlerKeyRoot = BuildConfig.docHandlerKeyRoot
    var docHandlerKeyLeaf = BuildConfig.docHandlerKeyLeaf
    var pinRetrievalCertificatePublicKeyHash = BuildConfig.pinRetrievalCertificatePublicKeyHash
    var gatewayHostName = BuildConfig.gatewayHostName
    var pinRetrievalHostName = BuildConfig.pinRetrievalHostName
    var pinEncryptHostName = BuildConfig.pinEncryptHostName
    var serverPath = BuildConfig.serverPath
    var serverImagePath = BuildConfig.serverImagePath
    var pinEncryptServerPath = BuildConfig.pinEncryptServerPath
    var pinRetrievalServerPath = BuildConfig.pinRetrievalServerPath
    var nCipherServerPath = BuildConfig.nCipherServerPath
    var ocrServiceUrl = BuildConfig.ocrServiceUrl
    var rootCertificatePublicKeyHash = BuildConfig.rootCertificatePublicKeyHash
    var alternativeRootCertificatePublicKeyHash = BuildConfig.alternativeRootCertificatePublicKeyHash

    @JvmField
    var STUB = BuildConfig.STUB

    fun activeUatI() {
        pinRetrievalCertificatePublicKeyHash = BuildConfig.UAT_I_pinRetrievalCertificatePublicKeyHash
        serverPath = BuildConfig.UAT_I_serverPath
        serverImagePath = BuildConfig.UAT_I_serverImagePath
        pinEncryptServerPath = BuildConfig.UAT_I_pinEncryptServerPath
        pinRetrievalServerPath = BuildConfig.UAT_I_pinRetrievalServerPath
        nCipherServerPath = BuildConfig.UAT_I_nCipherServerPath
        ExpressNetworkingConfig.expressEnvironment = "SIT1"
        STUB = false
    }

    fun activeUatR() {
        pinRetrievalCertificatePublicKeyHash = BuildConfig.UAT_R_pinRetrievalCertificatePublicKeyHash
        serverPath = BuildConfig.UAT_R_serverPath
        serverImagePath = BuildConfig.UAT_R_serverImagePath
        pinEncryptServerPath = BuildConfig.UAT_R_pinEncryptServerPath
        pinRetrievalServerPath = BuildConfig.UAT_R_pinRetrievalServerPath
        nCipherServerPath = BuildConfig.UAT_R_nCipherServerPath
        ExpressNetworkingConfig.expressEnvironment = "UAT1"
        STUB = false
    }

    fun activeUat2() {
        pinRetrievalCertificatePublicKeyHash = BuildConfig.UAT_2_pinRetrievalCertificatePublicKeyHash
        serverPath = BuildConfig.UAT_2_serverPath
        serverImagePath = BuildConfig.UAT_2_serverImagePath
        pinEncryptServerPath = BuildConfig.UAT_2_pinEncryptServerPath
        pinRetrievalServerPath = BuildConfig.UAT_2_pinRetrievalServerPath
        nCipherServerPath = BuildConfig.UAT_2_nCipherServerPath
        ExpressNetworkingConfig.expressEnvironment = "UAT2"
        STUB = false
    }

    fun activateStub() {
        STUB = true
    }
}