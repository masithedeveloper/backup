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

package com.barclays.absa.banking.framework.ssl

import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerFileToFetchDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

class DocHandlerRemoveFileService {
    suspend fun removeFileAsync(manageProfileDocHandlerFileToFetchDetails: ManageProfileDocHandlerFileToFetchDetails): RemoveResponse {
        val url = "https://${BuildConfigHelper.docHandlerHostNameBaseUrl}/Dochandler/api/document/delete/${manageProfileDocHandlerFileToFetchDetails.caseId}/${manageProfileDocHandlerFileToFetchDetails.documentId}"
        val sslSocketFactory = DocHandlerSSLSocketFactory()
        val client = OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager())
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic ${manageProfileDocHandlerFileToFetchDetails.encodedPassword.trim()}")
                .addHeader("Content-Type", "application/json")
                .url(url)
                .delete()
                .build()

        return try {
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                BMBLogger.d("x-doc", response.code.toString())
                if (response.code == HttpURLConnection.HTTP_OK) {
                    val responseBody = response.body
                    if (responseBody == null) {
                        RemoveResponse.FailureResponse("No file content")
                    } else {
                        RemoveResponse.SuccessResponse(responseBody.string())
                    }
                } else {
                    RemoveResponse.FailureResponse(response.code.toString())
                }
            }
        } catch (ex: SocketTimeoutException) {
            RemoveResponse.FailureResponse("Socket Timeout")
        } catch (ex: Exception) {
            RemoveResponse.FailureResponse("Generic error")
        }
    }
}

sealed class RemoveResponse {
    data class SuccessResponse(val message: String) : RemoveResponse()
    data class FailureResponse(val message: String) : RemoveResponse()
}