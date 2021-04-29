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

import androidx.annotation.DrawableRes
import androidx.annotation.StringDef
import androidx.annotation.StringRes
import com.barclays.absa.banking.R

data class Shortcut(var position: Int, @ShortcutFeature var featureName: String, var isEnabled: Boolean = true) {

    companion object {
        const val FEATURE_PAY = "PAY"
        const val FEATURE_TRANSFER = "TRANSFER"
        const val FEATURE_BUY_ELECTRICITY = "BUY ELECTRICITY"
        const val FEATURE_STOP_CARD = "STOP_CARD"
        const val FEATURE_QR_PAYMENTS = "QR_PAYMENTS"
    }

    @StringDef(FEATURE_PAY, FEATURE_TRANSFER,FEATURE_BUY_ELECTRICITY,FEATURE_STOP_CARD, FEATURE_QR_PAYMENTS)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ShortcutFeature

    @StringRes
    fun getNameResId():Int = when(featureName) {
        FEATURE_PAY -> R.string.pay
        FEATURE_TRANSFER -> R.string.transfer
        FEATURE_BUY_ELECTRICITY -> R.string.buy_electricity
        FEATURE_STOP_CARD -> R.string.stop_card_heading
        FEATURE_QR_PAYMENTS -> R.string.scan_to_pay_shortcut_name
        else -> -1
    }

    @DrawableRes
    fun getDrawableResId(): Int = when (featureName) {
        FEATURE_PAY -> R.drawable.ic_pay_dark
        FEATURE_TRANSFER -> R.drawable.ic_action_transfer
        FEATURE_BUY_ELECTRICITY -> R.drawable.ic_electricity_dark
        FEATURE_STOP_CARD -> R.drawable.ic_stop_card_dark
        FEATURE_QR_PAYMENTS -> R.drawable.ic_qr_dark
        else -> -1
    }
}
