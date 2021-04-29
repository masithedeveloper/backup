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

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.BuildConfigHelper;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import za.co.absa.networking.crypto.SHA256Encrytion;

public class DocHandlerTrustManager implements javax.net.ssl.X509TrustManager {

    private static final String COMMON_NAME_TAG = "CN";
    private static final String ROOT_CERTIFICATE_CN = "Entrust Root Certification Authority - G2";

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new UnsupportedOperationException("Operation not implemented");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (BuildConfig.ENABLE_CERT_PINNING) {
            checkTrustedInit(chain, authType);
            checkKeyExchangeAlgorithm(authType);
            checkPublicKeyHashes(chain);
            checkHostname(chain);
            chain[0].checkValidity();
            checkCertificateRevocation(chain);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }

    private void checkTrustedInit(X509Certificate[] chain, String authType) {
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("The certificate chain is null or empty");
        }

        if (authType == null || authType.isEmpty()) {
            throw new IllegalArgumentException("The key exchange algorithm type is null or empty");
        }
    }

    private void checkKeyExchangeAlgorithm(String authType) throws CertificateException {
        if (!(authType.equalsIgnoreCase("RSA")
                || authType.equalsIgnoreCase("DHE_RSA")
                || authType.equalsIgnoreCase("ECDHE_RSA"))) {
            throw new CertificateException("Key exchange algorithm is invalid");
        }
    }

    private void checkPublicKeyHashes(X509Certificate[] chain) throws CertificateException {
        X509Certificate rootCert = getRootCertificate(chain);
        String publicKey = getCertificatePublicKey(rootCert);

        if (!BuildConfigHelper.INSTANCE.getDocHandlerKeyRoot().equalsIgnoreCase(publicKey) && !BuildConfigHelper.INSTANCE.getDocHandlerKeyLeaf().equalsIgnoreCase(publicKey)) {
            throw new CertificateException("Server is not trusted");
        }
    }

    private void checkHostname(X509Certificate[] chain) throws CertificateException {
        String cn = getSubjectTagValue(chain[0]);
        if (!cn.equalsIgnoreCase(BuildConfigHelper.INSTANCE.getDocHandlerHostNameBaseUrl())) {
            throw new CertificateException("Hostname is not trusted");
        }
    }

    private void checkCertificateRevocation(X509Certificate[] chain) throws CertificateException {
        X509Certificate leafCert = chain[0];
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        x509CRLSelector.setCertificateChecking(leafCert);
        final String CERT_STORE_TYPE = "Collection";
        try {
            List<X509Certificate> certs = new ArrayList<>();
            certs.add(leafCert);
            CertStoreParameters storeParams = new CollectionCertStoreParameters(certs);
            CertStore certStore = CertStore.getInstance(CERT_STORE_TYPE, storeParams);
            Collection<CRL> crls = (Collection<CRL>) certStore.getCRLs(x509CRLSelector);
            for (CRL crl : crls) {
                if (crl.isRevoked(leafCert)) {
                    throw new CertificateException("Certificate is revoked");
                }
            }

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | CertStoreException e) {
            throw new CertificateException("Certificate revocation processing failure");
        }
    }

    private X509Certificate getRootCertificate(X509Certificate[] chain) {
        X509Certificate leafCert = chain[0];
        String leafSubjectCN = getSubjectTagValue(leafCert);

        if (leafSubjectCN.equalsIgnoreCase(BuildConfigHelper.INSTANCE.getDocHandlerHostNameBaseUrl())) {
            for (X509Certificate cert : chain) {
                String certSubjectCN = getSubjectTagValue(cert);

                if (ROOT_CERTIFICATE_CN.equalsIgnoreCase(certSubjectCN)) {
                    return cert;
                }

            }
        }
        return chain[chain.length - 1];
    }

    private String getSubjectTagValue(X509Certificate cert) {

        if (cert == null)
            throw new IllegalArgumentException("Certificate is null");

        if (COMMON_NAME_TAG.isEmpty()) {
            throw new IllegalArgumentException("Tag is empty");
        }

        X500Principal subject = cert.getSubjectX500Principal();
        String subjectName = subject.getName();
        String[] pairs = subjectName.split(",");
        for (String pair : pairs) {
            if (pair.startsWith(COMMON_NAME_TAG.concat("="))) {
                return pair.substring(pair.indexOf("=") + 1);
            }
        }
        return " ";
    }

    private String getCertificatePublicKey(X509Certificate certificate) {
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
        String encoded = new BigInteger(1, publicKey.getEncoded()).toString(16);
        return SHA256Encrytion.getHash(encoded);
    }
}
