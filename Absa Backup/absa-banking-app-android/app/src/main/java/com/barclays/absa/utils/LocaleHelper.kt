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
package com.barclays.absa.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import java.util.*

object LocaleHelper {
    private const val ENGLISH_LANGUAGE = "E"
    private const val AFRIKAANS_LANGUAGE = "A"

    @JvmStatic
    fun onAttach(context: Context): Context {
        val appInstance = BMBApplication.getInstance()
        val currentLanguageCode: String = if (appInstance != null && !appInstance.userLoggedInStatus) {
            BMBConstants.ENGLISH_CODE
        } else {
            persistedData
        }
        return setLocale(context, currentLanguageCode)
    }

    @JvmStatic
    val language: String
        get() = persistedData

    @JvmStatic
    fun setLocale(context: Context, language: String): Context {
        persist(language)
        val appInstance = BMBApplication.getInstance()
        val currentLanguageCode: String = if (appInstance != null && !appInstance.userLoggedInStatus) {
            BMBConstants.ENGLISH_CODE
        } else {
            language
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) updateResources(context, currentLanguageCode) else updateResourcesLegacy(context, currentLanguageCode)
    }

    private val persistedData: String
        get() = if (AFRIKAANS_LANGUAGE.equals(instance.languageCode, ignoreCase = true)) BMBConstants.AFRIKAANS_CODE else BMBConstants.ENGLISH_CODE

    private fun persist(language: String) {
        instance.languageCode = if (BMBConstants.AFRIKAANS_CODE.equals(language, ignoreCase = true)) AFRIKAANS_LANGUAGE else ENGLISH_LANGUAGE
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String) = Locale(language).let { locale ->
        Locale.setDefault(locale)
        with(context.resources.configuration) {
            setLocale(locale)
            setLayoutDirection(locale)
            context.createConfigurationContext(this)
        }
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        Locale(language).let { locale ->
            Locale.setDefault(locale)
            with(context.resources) {
                configuration.locale = locale
                configuration.setLayoutDirection(locale)
                updateConfiguration(configuration, displayMetrics)
            }
        }
        return context
    }
}