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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import za.co.absa.networking.ExpressNetworkingConfig
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class JsonDateSerializer(private var dateFormat: String) : JsonSerializer<Date?>(), ContextualSerializer {

    @Suppress("unused")
    constructor() : this("yyyy-MM-dd HH:mm")

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(date: Date?, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        val formattedDate = SimpleDateFormat(dateFormat, ExpressNetworkingConfig.applicationLocale).format(date ?: Date())
        jsonGenerator.writeString(formattedDate)
    }

    override fun createContextual(provider: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> {
        val jsonDateFormatAnnotation = property?.getAnnotation(JsonDateFormat::class.java)
        val dateFormat = jsonDateFormatAnnotation?.dateFormat ?: "yyyy-MM-dd HH:mm"
        return JsonDateSerializer(dateFormat)
    }
}
