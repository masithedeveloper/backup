/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.featureSwitching

import android.util.LruCache

object FeatureSwitchingCache {

    private const val MAXIMUM_CACHE_SIZE = 80
    private val BACKING_STORE = LruCache<String, Any>(MAXIMUM_CACHE_SIZE)

    private const val FEATURE_SWITCHING = "feature_switching"

    var featureSwitchingToggles: FeatureSwitching
        get() {
            return BACKING_STORE.get(FEATURE_SWITCHING) as FeatureSwitching
        }
        set(featureSwitching) {
            BACKING_STORE.put(FEATURE_SWITCHING, featureSwitching)
        }
}