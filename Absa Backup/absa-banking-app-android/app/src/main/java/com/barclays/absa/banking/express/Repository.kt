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

package com.barclays.absa.banking.express

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.express.authentication.tokens.RefreshTokenRepository
import com.barclays.absa.banking.express.authentication.tokens.RefreshTokenResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.crypto.SecureUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.hmac.service.BaseRepository
import za.co.absa.networking.hmac.service.BaseRequest
import java.util.*

abstract class Repository : BaseRepository() {

    protected abstract fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest

    final override var failureHeaderLiveData: MutableLiveData<ResponseHeader> = MutableLiveData()

    override var logFailureToNewRelic: Boolean = true
    open var showProgressDialog: Boolean = true
    open var allowEmptyParameters: Boolean = false

    open var headers: Map<String, String> = emptyMap()
    open val service: String = ""
    open val operation: String = ""

    abstract var mockResponseFile: String

    override lateinit var request: BaseRequest

    override var forceStub: Boolean
        get() = BuildConfigHelper.STUB
        set(value) {}

    companion object {
        const val USER_SESSION_LOGGED_OFF_STATUS_CODE = "9999"
        var requestLock = Mutex()
    }

    init {
        ExpressNetworkingConfig.deviceId = SecureUtils.getDeviceID()
        ExpressNetworkingConfig.applicationLocale = BMBApplication.getApplicationLocale()
        ExpressNetworkingConfig.appContext = BMBApplication.getInstance().applicationContext
        ExpressNetworkingConfig.appVersion = BuildConfig.VERSION_NAME
    }

    private fun logFailureEvent(header: ResponseHeader) {
        if (logFailureToNewRelic) {
            val location = (BMBApplication.getInstance().topMostActivity as? BaseActivity)?.javaClass?.simpleName ?: ""
            MonitoringInteractor().logExpressServiceErrorEvent(location, this.javaClass.simpleName, header.resultMessages.first().responseMessage)
        }
    }

    override fun handleFailureEvent(header: ResponseHeader) {
        logFailureEvent(header)
        if (header.statuscode == USER_SESSION_LOGGED_OFF_STATUS_CODE) {
            BMBApplication.getInstance().forceSignOut()
        } else {
            failureHeaderLiveData.postValue(header)
        }
    }

    val failureLiveData: MutableLiveData<ResponseHeader>
        get() {
            failureHeaderLiveData.removeObserver(observer)
            failureHeaderLiveData = MutableLiveData()
            return failureHeaderLiveData
        }

    val observer = Observer<ResponseHeader> {
        (BMBApplication.getInstance().topMostActivity as? BaseActivity)?.apply {
            dismissProgressDialog()
            if (it.resultMessages.isNotEmpty()) {
                showMessageError(it.resultMessages.first().responseMessage)
            }
        }
        failureHeaderLiveData = MutableLiveData()
    }

    override fun handleSureCheckEvent() {}

    override fun createRequest() {
        createDefaultObserver()

        if (showProgressDialog) {
            (BMBApplication.getInstance().topMostActivity as BaseActivity).showProgressDialog()
        }

        val baseRequest = BaseRequest.Builder(service, operation, allowEmptyParameters)

        ExpressNetworkingConfig.sessionMap[serviceEndpoint]?.apply {
            if (jsessionid.isNotEmpty()) {
                baseRequest.jsessionid(jsessionid)
            }
            if (nonce.isNotEmpty()) {
                baseRequest.nonce(nonce)
            }
        }

        request = buildRequest(baseRequest)
    }

    private fun createDefaultObserver() {
        if (!failureHeaderLiveData.hasObservers()) {
            Handler(Looper.getMainLooper()).post {
                failureHeaderLiveData = MutableLiveData()
                if (javaClass != RefreshTokenRepository::class.java) {
                    failureHeaderLiveData.observeForever(observer)
                }
            }
        }
    }

    fun removeFailureLiveDataObserver() {
        Handler(Looper.getMainLooper()).post { failureHeaderLiveData.removeObserver(observer) }
    }

    suspend inline fun <reified T> submitRequest(): T? {

        return if (javaClass == RefreshTokenRepository::class.java) {
            performRequest<T>()
        } else {
            requestLock.lock()
            return if (shouldPerformRequest()) {
                requestLock.unlock()
                performRequest<T>()
            } else {
                refreshTokenLiveDataAsync().await()
                requestLock.unlock()
                performRequest<T>()
            }
        }
    }

    suspend inline fun <reified T> performRequest(): T? {
        val safeApiCall = safeApiCall<T>(headers, mockResponseFile)
        removeFailureLiveDataObserver()
        return safeApiCall
    }

    fun shouldPerformRequest(): Boolean {
        return ExpressNetworkingConfig.tokenExpireTime == null || Calendar.getInstance().time.before(ExpressNetworkingConfig.tokenExpireTime)
    }

    suspend fun refreshTokenLiveDataAsync() = GlobalScope.async {
        if (BMBApplication.getInstance().userLoggedInStatus) {
            RefreshTokenRepository().let { refreshTokenRepository ->
                refreshTokenRepository.showProgressDialog = showProgressDialog
                refreshTokenRepository.submitRequest<RefreshTokenResponse>()?.let {
                    ExpressNetworkingConfig.accessToken = it.accessToken
                    ExpressNetworkingConfig.setTokenExpiryTime(it.expiresInSeconds)
                } ?: run {
                    BMBApplication.getInstance().forceSignOut()
                }
            }
        }
    }
}