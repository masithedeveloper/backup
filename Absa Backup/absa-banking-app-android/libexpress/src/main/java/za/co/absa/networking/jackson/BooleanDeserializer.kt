/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package za.co.absa.networking.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class BooleanDeserializer : JsonDeserializer<Boolean>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Boolean {
        return "Y".equals(parser.text, ignoreCase = true) || "Yes".equals(parser.text, ignoreCase = true) || "true".equals(parser.text, ignoreCase = true)
    }

    override fun getNullValue(ctxt: DeserializationContext?): Boolean {
        return false
    }
}