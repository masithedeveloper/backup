/*
 * Copyright (c) 2018 Barclays Bank Plc, All Rights Reserved.
 *
 * This code is confidential to Barclays Bank Plc and shall not be disclosed
 * outside the Bank without the prior written permission of the Director of
 * CIO
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Barclays Bank Plc.
 */

package com.barclays.absa.banking.framework.ssl;

import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class DocHandlerRequestUtils {
    private final DocHandlerTrustManager DOC_HANDLER_TRUST_MANAGER = new DocHandlerTrustManager();

    public HttpsURLConnection createSecureConnection(final URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            final DocHandlerTrustManager[] docHandlerTrustManagers = {DOC_HANDLER_TRUST_MANAGER};

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, docHandlerTrustManagers, new SecureRandom());

            HostnameVerifier hostnameVerifier = (s, sslSession) -> url.getHost().equals(s);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(sslSocketFactory);
            connection.setHostnameVerifier(hostnameVerifier);

        } catch (NoSuchAlgorithmException e) {
            BMBLogger.e(e.getMessage());
        } catch (KeyManagementException e) {
            BMBLogger.e(e.getMessage());
        } catch (Exception e) {
            BMBLogger.e(e.getMessage());
        }

        return connection;
    }
}
