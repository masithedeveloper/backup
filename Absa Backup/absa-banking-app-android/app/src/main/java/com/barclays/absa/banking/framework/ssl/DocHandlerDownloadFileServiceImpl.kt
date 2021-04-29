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

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.BuildConfigHelper.docHandlerHostNameBaseUrl
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerFileToFetchDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

class DocHandlerDownloadFileServiceImpl : DocHandlerDownloadFileService {
    override suspend fun downloadFileAsync(manageProfileDocHandlerFileToFetchDetails: ManageProfileDocHandlerFileToFetchDetails): FileResponse {
        val url = "https://${docHandlerHostNameBaseUrl}/Dochandler/api/documentcontroller/getdocument/${manageProfileDocHandlerFileToFetchDetails.caseId}/${manageProfileDocHandlerFileToFetchDetails.documentId}/${manageProfileDocHandlerFileToFetchDetails.encodedPassword}"
        val sslSocketFactory = DocHandlerSSLSocketFactory()
        val client = OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager())
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        return try {
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                BMBLogger.d("x-doc", response.code.toString())
                if (response.code == HttpURLConnection.HTTP_OK) {
                    val responseBody = response.body
                    if (responseBody == null) {
                        FileResponse.FailureResponse("No file content")
                    } else {
                        FileResponse.SuccessResponse(responseBody.bytes())
                    }
                } else {
                    FileResponse.FailureResponse(response.code.toString())
                }
            }
        } catch (ex: SocketTimeoutException) {
            if (BuildConfig.DEBUG) {
                ex.printStackTrace()
            }
            FileResponse.FailureResponse("Socket Timeout")
        } catch (ex: Exception) {
            if (BuildConfig.DEBUG) {
                ex.printStackTrace()
            }
            FileResponse.FailureResponse("Generic error")
        }
    }
}

@Suppress("ArrayInDataClass")
sealed class FileResponse {
    data class SuccessResponse(val byteArray: ByteArray) : FileResponse()
    data class FailureResponse(val message: String) : FileResponse()
}