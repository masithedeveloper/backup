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

import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.hmac.utils.HmacUtils

class Request : BaseRequest() {

    var symmetricKey = ""
    var publicKeyId = ""

    init {
        publicKeyId = ExpressNetworkingConfig.publicKeyId
        symmetricKey = HmacUtils.generateAndEncodeAesSymmetricKey(ExpressNetworkingConfig.publicKey)
    }

    class Builder {

        val request = Request()

        constructor()

        constructor(baseBuilder: BaseRequest.Builder) {
            request.header.service = baseBuilder.request.header.service
            request.header.operation = baseBuilder.request.header.operation
        }

        fun service(service: String): Builder {
            request.header.service = service
            return this
        }

        fun operation(operation: String): Builder {
            request.header.operation = operation
            return this
        }

        fun addParameter(parameterName: String, parameterValue: String): Builder {
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

        fun addObjectParameter(parameterName: String, anyObject: Any): Builder {
            request.parameters[parameterName] = anyObject
            return this
        }

        fun build() = request
    }
}