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

import android.content.Context

interface IAppShortcutsHandler {
    fun createOutsideAppShortcuts(context: Context)
    fun isEmptyCachedUserShortcutList(): Boolean
    fun getCachedUserShortcutList(userId: String? = null): List<Shortcut>
    fun setCachedUserShortcutList(shortcutList: List<Shortcut>)
    fun setDefaultCachedUserShortcutList(context: Context)
    fun getDefaultUserShortcutList(context: Context): List<Shortcut>
    fun getUpgradedCachedUserShortcutList(context: Context): List<Shortcut>
    fun isFeatureVisible(featureName: String): Boolean
    fun isFeatureDisabled(featureName: String): Boolean
}