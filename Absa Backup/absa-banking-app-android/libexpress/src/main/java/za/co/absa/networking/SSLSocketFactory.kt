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

package za.co.absa.networking

import android.util.Log
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.*
import javax.net.ssl.SSLSocketFactory

class SSLSocketFactory : SSLSocketFactory() {
    val TAG = "OkHttpSSLSocketFactory"
    private val TLS_VERSION = "TLSv1"
    private val expressTrustManager by lazy { ExpressTrustManager() }
    private val permissiveTrustManager by lazy { PermissiveTrustManager() }

    private lateinit var sslSocketFactory: SSLSocketFactory

    init {
        initialiseSSLContext()
    }

    private fun initialiseSSLContext() {
        val keyManagers: Array<KeyManager>? = null
        val secureRandom: SecureRandom? = null
        val trustManagers: Array<X509TrustManager> = if (BuildConfig.ENABLE_CERT_PINNING) arrayOf(expressTrustManager) else arrayOf(permissiveTrustManager)
        try {
            val sslContext = SSLContext.getInstance(TLS_VERSION)
            sslContext.init(keyManagers, trustManagers, secureRandom)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to initialise SSL Context", e)
            }
        } catch (e: KeyManagementException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to initialise SSL Context", e)
            }
        }
    }

    override fun getDefaultCipherSuites(): Array<String?>? {
        val defaultCipherSuites = sslSocketFactory.defaultCipherSuites
        val extendedCipherSuites = arrayOfNulls<String>(defaultCipherSuites.size + 1)
        System.arraycopy(defaultCipherSuites, 0, extendedCipherSuites, 0, defaultCipherSuites.size)
        extendedCipherSuites[extendedCipherSuites.size - 1] = "SSL_RSA_WITH_3DES_EDE_CBC_SHA"
        return extendedCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String?>? {
        return sslSocketFactory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class)
    override fun createSocket(host: String?, port: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket? {
        return createCompatibleSocket(sslSocketFactory.createSocket(address, port, localAddress, localPort))
    }

    fun getTrustManager(): ExpressTrustManager {
        return expressTrustManager
    }

    private fun createCompatibleSocket(socket: Socket): Socket? {
        if (socket is SSLSocket) {
            socket.enabledCipherSuites = defaultCipherSuites
            return socket
        }
        return socket
    }
}