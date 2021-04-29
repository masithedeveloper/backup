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
import com.barclays.absa.banking.framework.utils.BMBLogger
import java.net.InetAddress
import java.net.Socket
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

private const val TAG = "DocHandlerSSLSocketFactory"
private const val TLS_VERSION = "TLSv1.2"

class DocHandlerSSLSocketFactory : SSLSocketFactory() {
    private val trustManager = DocHandlerTrustManager()
    private lateinit var sslSocketFactory: SSLSocketFactory

    init {
        val keyManagers: Array<KeyManager>? = null
        val secureRandom: SecureRandom? = null
        val trustManagers = arrayOf(trustManager)

        try {
            val sslContext = SSLContext.getInstance(TLS_VERSION)
            sslContext.init(keyManagers, trustManagers, secureRandom)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            if (BuildConfig.DEBUG) {
                BMBLogger.d(TAG, "Failed to initialise SSL Context $e")
            }
        } catch (e: KeyManagementException) {
            if (BuildConfig.DEBUG) {
                BMBLogger.d(TAG, "Failed to initialise SSL Context $e")
            }
        }
    }

    override fun getDefaultCipherSuites(): Array<String?> {
        val defaultCipherSuites = sslSocketFactory.defaultCipherSuites
        val extendedCipherSuites = arrayOfNulls<String>(defaultCipherSuites.size + 1)
        System.arraycopy(defaultCipherSuites, 0, extendedCipherSuites, 0, defaultCipherSuites.size)
        extendedCipherSuites[extendedCipherSuites.size - 1] = "SSL_RSA_WITH_3DES_EDE_CBC_SHA"

        return extendedCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return sslSocketFactory.supportedCipherSuites
    }

    override fun createSocket(socket: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(socket, host, port, autoClose))
    }

    override fun createSocket(host: String?, port: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port))
    }

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port, localHost, localPort))
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port))
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(address, port, localAddress, localPort))
    }

    private fun createCompatibleSocket(socket: Socket): Socket? {
        if (socket is SSLSocket) {
            socket.enabledCipherSuites = defaultCipherSuites
            return socket
        }
        return socket
    }

    fun getTrustManager(): DocHandlerTrustManager {
        return trustManager
    }
}