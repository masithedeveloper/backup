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
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.launch.SplashActivity
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import java.util.*
import kotlin.collections.ArrayList

object AppShortcutsHandler : IAppShortcutsHandler {

    override fun createOutsideAppShortcuts(context: Context) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return

        val shortcutsHeaders = ArrayList(Arrays.asList(*context.resources.getStringArray(R.array.shortcut_headings)))
        val shortcutIcons = context.resources.obtainTypedArray(R.array.shortcut_icons)
        val shortcutsInfo = ArrayList<ShortcutInfo>()

        shortcutsHeaders.forEachIndexed { index, shortcutName ->
            val intent = getIntentForShortcut(shortcutName, context)
            val shortcut = ShortcutInfo.Builder(context, shortcutName)
                    .setIntent(intent)
                    .setShortLabel(shortcutName)
                    .setIcon(Icon.createWithResource(context, shortcutIcons.getResourceId(index, -1)))
                    .build()
            shortcutsInfo.add(shortcut)
        }
        shortcutIcons.recycle()
        shortcutManager.dynamicShortcuts = shortcutsInfo
    }

    override fun isEmptyCachedUserShortcutList(): Boolean = getCachedUserShortcutList().isEmpty()

    override fun getCachedUserShortcutList(userId: String?): List<Shortcut> = SharedPreferenceService.getUserShortcutList(userId).sortedBy { shortcut -> shortcut.position }

    override fun setCachedUserShortcutList(shortcutList: List<Shortcut>) = SharedPreferenceService.setUserShortcutList(shortcutList)

    override fun setDefaultCachedUserShortcutList(context: Context) = setCachedUserShortcutList(getDefaultUserShortcutList(context))

    // Add new features to this list as they become available in the future
    override fun getDefaultUserShortcutList(context: Context): List<Shortcut> = listOf(
            Shortcut(0, Shortcut.FEATURE_PAY),
            Shortcut(1, Shortcut.FEATURE_TRANSFER),
            Shortcut(2, Shortcut.FEATURE_BUY_ELECTRICITY),
            Shortcut(3, Shortcut.FEATURE_STOP_CARD),
            Shortcut(4, Shortcut.FEATURE_QR_PAYMENTS)
    )

    override fun getUpgradedCachedUserShortcutList(context: Context): List<Shortcut> {
        val newUserShortcutList = getDefaultUserShortcutList(context)
        var cachedUserShortcutList = getCachedUserShortcutList().toMutableList()
        if (cachedUserShortcutList.isNullOrEmpty()) {
            cachedUserShortcutList = newUserShortcutList.toMutableList()
        }

        if (newUserShortcutList.size > cachedUserShortcutList.size) {
            val newItems = newUserShortcutList.subList(cachedUserShortcutList.size, newUserShortcutList.size)
            newItems.forEach { newShortcut: Shortcut ->
                val insertionIndex = (cachedUserShortcutList.indexOfLast { oldShortcut -> oldShortcut.isEnabled } + 1)
                cachedUserShortcutList.add(insertionIndex, newShortcut)
            }
            cachedUserShortcutList.forEachIndexed { index, shortcut -> shortcut.position = index }
        }
        val sortedList = cachedUserShortcutList.sortedBy { shortcut -> shortcut.position }
        setCachedUserShortcutList(sortedList)
        return sortedList
    }

    private fun getIntentForShortcut(shortcut: String, context: Context): Intent = Intent(context, SplashActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .putExtra("shortcut", shortcut)

    override fun isFeatureVisible(featureName: String): Boolean = when (featureName) {
        Shortcut.FEATURE_QR_PAYMENTS -> FeatureSwitchingCache.featureSwitchingToggles.scanToPay != FeatureSwitchingStates.GONE.key && BuildConfig.TOGGLE_DEF_SCAN_TO_PAY_ENABLED
        Shortcut.FEATURE_BUY_ELECTRICITY -> FeatureSwitchingCache.featureSwitchingToggles.prepaidElectricity != FeatureSwitchingStates.GONE.key
        else -> true
    }

    override fun isFeatureDisabled(featureName: String): Boolean = when (featureName) {
        Shortcut.FEATURE_QR_PAYMENTS -> ScanToPayViewModel.scanToPayDisabled()
        Shortcut.FEATURE_BUY_ELECTRICITY -> FeatureSwitchingCache.featureSwitchingToggles.prepaidElectricity == FeatureSwitchingStates.DISABLED.key
        else -> false
    }

}