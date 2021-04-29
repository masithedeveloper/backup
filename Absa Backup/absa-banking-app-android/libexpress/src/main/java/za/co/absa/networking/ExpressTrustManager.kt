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
import za.co.absa.networking.crypto.SHA256Encrytion
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.cert.*
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.net.ssl.X509TrustManager

class ExpressTrustManager : X509TrustManager {
    val TAG = ExpressTrustManager::class.java.simpleName

    companion object {
        private const val COMMON_NAME_TAG = "CN"
        private const val ROOT_CERTIFICATE_CN = "DigiCert High Assurance EV Root CA"
        private const val ALTERNATIVE_ROOT_CERTIFICATE_CN = "VeriSign Class 3 Public Primary Certification Authority - G5"
        private const val XSWP_ROOT_CERTIFICATE_CN = "DigiCert Global Root CA"
    }

    @Throws(UnsupportedOperationException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        throw UnsupportedOperationException("Operation not implemented")
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        checkTrustedInit(chain, authType)
        checkCertificateChainLength(chain)
        checkKeyExchangeAlgorithm(authType)
        checkPublicKeyHashes(chain)
        chain[0].checkValidity()
        checkCertificateRevocation(chain)
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }

    private fun checkTrustedInit(chain: Array<X509Certificate>?, authType: String?) {
        require(!(chain == null || chain.isEmpty())) { "The certificate chain is null or empty" }
        require(!(authType == null || authType.isEmpty())) { "The key exchange algorithm type is null or empty" }
    }

    @Throws(CertificateException::class)
    private fun checkCertificateChainLength(chain: Array<X509Certificate>) {
        if (chain.size == 1) {
            throw CertificateException("Invalid certificate chain...")
        }
    }

    @Throws(CertificateException::class)
    private fun checkKeyExchangeAlgorithm(authType: String) {
        if (!(authType.equals("RSA", ignoreCase = true)
                        || authType.equals("DHE_RSA", ignoreCase = true)
                        || authType.equals("ECDHE_RSA", ignoreCase = true))) {
            throw CertificateException("Key exchange algorithm is invalid")
        }
    }

    @Throws(CertificateException::class)
    private fun checkPublicKeyHashes(chain: Array<X509Certificate>) {
        val rootCert = getRootCertificate(chain)
        val publicKey = getCertificatePublicKey(rootCert)
        if (BuildConfig.DEBUG) {
            try {
                val leafCertificate = getLeafCertificate(chain)
                val leafPublicKey = getCertificatePublicKey(leafCertificate)
                Log.i(TAG, "Leaf cert public key for " + getSubjectTagValue(leafCertificate) + " : " + leafPublicKey)
            } catch (e: SecurityException) {
                Log.e(TAG, e.message.toString())
            }
        }
        if (!(BuildConfig.rootCertificatePublicKeyHash.equals(publicKey, ignoreCase = true) || BuildConfig.xswpRootCertificatePublicKeyHash.equals(publicKey, ignoreCase = true))) {
            throw CertificateException("Server is not trusted")
        }
    }

    @Throws(CertificateException::class)
    private fun checkCertificateRevocation(chain: Array<X509Certificate>) {
        val leafCert = chain[0]
        val x509CRLSelector = X509CRLSelector()
        x509CRLSelector.certificateChecking = leafCert
        val CERT_STORE_TYPE = "Collection"
        try {
            val certs: MutableList<X509Certificate?> = ArrayList()
            certs.add(leafCert)
            val storeParams: CertStoreParameters = CollectionCertStoreParameters(certs)
            val certStore = CertStore.getInstance(CERT_STORE_TYPE, storeParams)
            val crls = certStore.getCRLs(x509CRLSelector) as Collection<CRL>
            for (crl in crls) {
                if (crl.isRevoked(leafCert)) {
                    throw CertificateException("Certificate is revoked")
                }
            }
        } catch (e: InvalidAlgorithmParameterException) {
            throw CertificateException("Certificate revocation processing failure")
        } catch (e: NoSuchAlgorithmException) {
            throw CertificateException("Certificate revocation processing failure")
        } catch (e: CertStoreException) {
            throw CertificateException("Certificate revocation processing failure")
        }
    }

    @Throws(SecurityException::class)
    private fun getRootCertificate(chain: Array<X509Certificate>): X509Certificate {
        val leafCert = chain[0]
        val leafSubjectCN = getSubjectTagValue(leafCert)
        if (leafSubjectCN == BuildConfig.expressHostName || leafSubjectCN == BuildConfig.xsmsHostName || leafSubjectCN == BuildConfig.nCipherHostName || leafSubjectCN == BuildConfig.xswpHostName) {
            for (cert in chain) {
                val certSubjectCN = getSubjectTagValue(cert)
                if (ROOT_CERTIFICATE_CN.equals(certSubjectCN, ignoreCase = true) || ALTERNATIVE_ROOT_CERTIFICATE_CN.equals(certSubjectCN, ignoreCase = true) || XSWP_ROOT_CERTIFICATE_CN.equals(certSubjectCN, ignoreCase = true)) {
                    return cert
                }
            }
        }
        throw SecurityException("Untrusted host")
    }

    @Throws(SecurityException::class)
    private fun getLeafCertificate(chain: Array<X509Certificate>): X509Certificate {
        val leafCertificate = chain[0]
        val leafPublicKey = getCertificatePublicKey(leafCertificate)
        Log.i(TAG, "Leaf cert public key for " + getSubjectTagValue(leafCertificate) + " is " + leafPublicKey)
        return leafCertificate
    }

    private fun getSubjectTagValue(cert: X509Certificate?): String {
        requireNotNull(cert) { "Certificate is null" }
        val subject = cert.subjectX500Principal
        val subjectName = subject.name
        val pairs = subjectName.split(",").toTypedArray()
        for (pair in pairs) {
            if (pair.startsWith("$COMMON_NAME_TAG=")) {
                return pair.substring(pair.indexOf("=") + 1)
            }
        }
        return " "
    }

    private fun getCertificatePublicKey(certificate: X509Certificate): String {
        val publicKey = certificate.publicKey as RSAPublicKey
        val encoded = BigInteger(1, publicKey.encoded).toString(16)
        return SHA256Encrytion.getHash(encoded)
    }
}