/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.utils

import com.adobe.mobile.Analytics
import com.barclays.absa.banking.cashSend.ui.CashSendActivity.isCashSendPlus
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import java.util.*

object AnalyticsUtil {
    private var featureName: String = ""
    private var featureScreen: String = ""
    private var contextData = HashMap<String, Any?>()

    fun trackAction(actionPerformed: String) {
        featureName = BMBApplication.getInstance().topMostActivity.localClassName
        featureScreen = actionPerformed
        contextData["absa.screenName"] = featureScreen
        Analytics.trackAction(featureName, contextData)
    }

    fun trackAction(featureName: String, actionPerformed: String) {
        this.featureName = featureName
        featureScreen = actionPerformed
        contextData["absa.screenName"] = featureScreen
        Analytics.trackAction(featureName, contextData)
    }

    @JvmStatic
    fun trackActionFromStaticContext(featureName: String, actionPerformed: String) {
        this.featureName = featureName
        featureScreen = actionPerformed
        contextData["absa.screenName"] = featureScreen
        Analytics.trackAction(featureName, contextData)
    }

    fun tagCashSend(actionPerformedTag: String) {
        val featureName = if (isCashSendPlus) "BBCashSendPlus_" else "BBCashSend_"
        trackAction(BMBConstants.CASHSEND_CONST, "$featureName$actionPerformedTag")
    }
}