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
package com.barclays.absa.banking.card.ui.debitCard.ui

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object ViewModelReflectionUtil {

    @Suppress("UNCHECKED_CAST")
    fun <T> viewModelReflectionUtil(concreteClass: Any, variableName: String): T {
        val privateField = concreteClass.javaClass.kotlin.declaredMemberProperties.find { it.name.contains(variableName) }
        privateField?.let {
            it.isAccessible = true
            return it.get(concreteClass) as T
        }
        throw Exception("Incorrect concreteClass type")
    }
}