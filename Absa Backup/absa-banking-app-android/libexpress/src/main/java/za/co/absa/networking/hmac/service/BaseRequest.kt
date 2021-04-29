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
package za.co.absa.networking.hmac.service

import android.util.Log
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import za.co.absa.networking.dto.Header
import za.co.absa.networking.dto.PaginationContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

open class BaseRequest {
    val header = Header()

    @JsonIgnore
    var parameters = mutableMapOf<String, Any>()

    @JsonIgnore
    var allowEmptyParameters: Boolean = false

    init {
        setSourceIpAddress()
    }

    open class Builder {

        val request = BaseRequest()

        constructor()

        constructor(service: String, operation: String, allowEmpties: Boolean) {
            request.header.service = service
            request.header.operation = operation
            request.allowEmptyParameters = allowEmpties
        }

        fun service(service: String): Builder {
            request.header.service = service
            return this
        }

        fun operation(operation: String): Builder {
            request.header.operation = operation
            return this
        }

        fun jsessionid(jsessionid: String): Builder {
            request.header.jsessionid = jsessionid
            return this
        }

        fun nonce(nonce: String): Builder {
            request.header.nonce = nonce
            return this
        }

        fun enablePagination(): Builder {
            request.header.paginationContext = PaginationContext()
            return this
        }

        fun addParameter(parameterName: String, parameterValue: Any): Builder {
            request.parameters[parameterName] = parameterValue
            return this
        }

        fun addParameter(parameterName: String, parameterValue: Boolean): Builder {
            request.parameters[parameterName] = parameterValue
            return this
        }

        fun addDictionaryParameter(parameterName: String, subMap: Map<String, String?>): Builder {
            request.parameters[parameterName] = subMap
            return this
        }

        fun addDictionaryParameter(keyValuePair: Pair<String, Map<String, String>>): Builder {
            request.parameters[keyValuePair.first] = keyValuePair.second
            return this
        }

        fun addListParameter(parameterName: String, subList: List<Map<String, String?>>): Builder {
            request.parameters[parameterName] = subList
            return this
        }

        @Deprecated("Use addParameter")
        fun addObjectParameter(parameterName: String, anyObject: Any): Builder {
            request.parameters[parameterName] = anyObject
            return this
        }

        open fun build() = request
    }

    private fun setSourceIpAddress() {
        header.sourceip = getNetworkInterfaceIpAddress()
    }

    private fun getNetworkInterfaceIpAddress(): String {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val host: String = inetAddress.getHostAddress()
                        if (host.isNotEmpty()) {
                            return host
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("IP Address", "getLocalIpAddress", ex)
        }
        return ""
    }

    override fun toString(): String {
        val objectMapper = ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            if (!allowEmptyParameters) {
                setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            }
        }

        val baseRequest = objectMapper.writeValueAsString(this)

        return if (parameters.isNotEmpty() && parameters.values.any { it.toString().isNotEmpty() }) {
            baseRequest.substringBeforeLast("}") + ",${objectMapper.writeValueAsString(parameters).substring(1)}"
        } else {
            baseRequest
        }
    }
}