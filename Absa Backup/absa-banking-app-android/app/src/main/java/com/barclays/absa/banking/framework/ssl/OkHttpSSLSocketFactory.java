/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.ssl;

import android.util.Log;

import com.barclays.absa.banking.BuildConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

class OkHttpSSLSocketFactory extends SSLSocketFactory {
    private static final String TAG = "OkHttpSSLSocketFactory";
    private static final String TLS_VERSION = "TLSv1";
    private final LegacyTrustManager TRUST_MANAGER = new LegacyTrustManager();
    private SSLSocketFactory sslSocketFactory;

    OkHttpSSLSocketFactory() {
        initialiseSSLContext();
    }

    private void initialiseSSLContext() {
        final KeyManager[] KEY_MANAGERS = null;
        final SecureRandom SECURE_RANDOM = null;
        final LegacyTrustManager[] TRUST_MANAGERS = {TRUST_MANAGER};

        try {
            SSLContext sslContext = SSLContext.getInstance(TLS_VERSION);
            sslContext.init(KEY_MANAGERS, TRUST_MANAGERS, SECURE_RANDOM);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to initialise SSL Context", e);
            }
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        String[] defaultCipherSuites = sslSocketFactory.getDefaultCipherSuites();
        String[] extendedCipherSuites = new String[defaultCipherSuites.length + 1];
        System.arraycopy(defaultCipherSuites, 0, extendedCipherSuites, 0, defaultCipherSuites.length);
        extendedCipherSuites[extendedCipherSuites.length - 1] = "SSL_RSA_WITH_3DES_EDE_CBC_SHA";

        return extendedCipherSuites;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return createCompatibleSocket(sslSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return createCompatibleSocket(sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return createCompatibleSocket(sslSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    LegacyTrustManager getTrustManager() {
        return TRUST_MANAGER;
    }

    private Socket createCompatibleSocket(Socket socket) {
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;
            sslSocket.setEnabledCipherSuites(getDefaultCipherSuites());
            return sslSocket;
        }
        return socket;
    }
}