/*
 * This class copied from https://stackoverflow.com/questions/25509296/trusting-all-certificates-with-okhttp
 * So yeah, not copyrighting this
 */
package za.co.absa.networking

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class PermissiveTrustManager : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
}