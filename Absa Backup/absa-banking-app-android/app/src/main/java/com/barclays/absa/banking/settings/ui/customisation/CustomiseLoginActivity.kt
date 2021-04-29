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
package com.barclays.absa.banking.settings.ui.customisation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.widget.AccountReorderCallbackHelper
import com.barclays.absa.banking.settings.ui.OnStartDragListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AppShortcutsHandler
import com.barclays.absa.utils.IAppShortcutsHandler
import com.barclays.absa.utils.Shortcut
import com.barclays.absa.utils.Shortcut.Companion.FEATURE_QR_PAYMENTS
import kotlinx.android.synthetic.main.activity_customise_login.*
import kotlinx.android.synthetic.main.manage_shortcuts_recyclerview_layout.view.*
import styleguide.content.ShortcutStatusView

class CustomiseLoginActivity : BaseActivity(R.layout.activity_customise_login), OnStartDragListener {

    companion object {
        fun isCustomisedLoginDisabled(): Boolean = FeatureSwitchingCache.featureSwitchingToggles.customiseLogin == FeatureSwitchingStates.GONE.key
                || !BuildConfig.TOGGLE_DEF_CUSTOMISE_LOGIN
    }

    private lateinit var appShortcutsHandler: IAppShortcutsHandler
    private lateinit var manageShortcutsAdapter: ManageShortcutsAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var initialUserShortcutList: List<Shortcut>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.customise_login_screen_title)

        appShortcutsHandler = AppShortcutsHandler

        initialUserShortcutList = appShortcutsHandler.getUpgradedCachedUserShortcutList(this)
        val userShortcutList = initialUserShortcutList.map { shortcut -> shortcut.copy() }.toMutableList()

        manageShortcutsAdapter = ManageShortcutsAdapter(userShortcutList, this)

        with(manageShortcutsRecyclerView) {
            setHasFixedSize(true)
            adapter = manageShortcutsAdapter
        }
        touchHelper = ItemTouchHelper(AccountReorderCallbackHelper(manageShortcutsAdapter))
        touchHelper.attachToRecyclerView(manageShortcutsRecyclerView)

        saveButton.setOnClickListener { saveUserShortcutPreference() }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) = touchHelper.startDrag(viewHolder)

    private fun saveUserShortcutPreference() {
        val updatedList = manageShortcutsAdapter.shortcutList.mapIndexed { index, shortcut -> shortcut.apply { position = index } }
        appShortcutsHandler.setCachedUserShortcutList(updatedList)
        finish()
    }

    private fun isListUpdated(): Boolean {
        val updatedList = manageShortcutsAdapter.shortcutList.mapIndexed { index, shortcut -> shortcut.apply { position = index } }
        updatedList.forEachIndexed { index, shortcut ->
            if (initialUserShortcutList[index] != shortcut) return true
        }
        return false
    }

    override fun onBackPressed() {
        if (isListUpdated()) {
            AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle(R.string.customise_login_screen_discard_changes_title)
                    .setMessage(R.string.customise_login_screen_discard_changes_message)
                    .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                        dialogInterface.cancel()
                        finish()
                    }
                    .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .create()
                    .show()
        } else {
            super.onBackPressed()
        }
    }

    class ManageShortcutsAdapter(val shortcutList: MutableList<Shortcut>, private val dragStartListener: OnStartDragListener)
        : RecyclerView.Adapter<ManageShortcutsAdapter.ManageShortcutsViewHolder>(), AccountReorderCallbackHelper.ItemTouchHelperAdapter {

        interface OnItemCheckedChangeListener {
            fun onItemCheckedChanged(position: Int, isChecked: Boolean)
        }

        private var onItemCheckedChangeListener: OnItemCheckedChangeListener = object : OnItemCheckedChangeListener {
            override fun onItemCheckedChanged(position: Int, isChecked: Boolean) {
                val shortcutItem = shortcutList[position]
                val lastCheckedIndex = shortcutList.indexOfLast { shortcut -> shortcut.isEnabled }
                if (isChecked) {
                    shortcutItem.isEnabled = !shortcutItem.isEnabled
                    shiftItem(position, lastCheckedIndex + 1)
                } else {
                    val newUncheckedIndex = if (lastCheckedIndex == -1) 0 else lastCheckedIndex
                    shortcutItem.isEnabled = !shortcutItem.isEnabled
                    shiftItem(position, newUncheckedIndex)
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ManageShortcutsViewHolder =
                ManageShortcutsViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.manage_shortcuts_recyclerview_layout, viewGroup, false))

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(manageShortcutsViewHolder: ManageShortcutsViewHolder, position: Int) {
            val shortcutStatusView = manageShortcutsViewHolder.shortcutStatusView
            val shortcutItem = shortcutList[position]

            with(shortcutStatusView) {
                shortcutName = context.getString(shortcutItem.getNameResId())
                setShortcutImage(if (shortcutItem.isEnabled) R.drawable.ic_account_menu_icon else shortcutItem.getDrawableResId())
                toggleBarStyle(shortcutItem.isEnabled, context)
                shortcutImageView.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN && shortcutToggle.isChecked) {
                        dragStartListener.onStartDrag(manageShortcutsViewHolder)
                    }
                    false
                }
                shortcutToggle.setOnCheckedChangeListener { _, isChecked ->
                    setAccountToggleStyle(shortcutToggle, isChecked)
                    setShortcutImage(if (isChecked) R.drawable.ic_account_menu_icon else shortcutItem.getDrawableResId())
                    onItemCheckedChangeListener.onItemCheckedChanged(manageShortcutsViewHolder.adapterPosition, isChecked)
                }
            }

            if (shortcutItem.featureName == FEATURE_QR_PAYMENTS && ScanToPayViewModel.customiseLoginOptionGone()) {
                shortcutStatusView.visibility = View.GONE
                shortcutStatusView.layoutParams = RecyclerView.LayoutParams(0,0)
            }
        }

        override fun onViewRecycled(holder: ManageShortcutsViewHolder) {
            super.onViewRecycled(holder)
            holder.shortcutStatusView.shortcutToggle.setOnCheckedChangeListener(null)
        }

        override fun getItemCount(): Int = shortcutList.size

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            if (isInvalidMove(fromPosition, toPosition)) return
            shiftItem(fromPosition, toPosition)
        }

        override fun onItemMoved() = notifyDataSetChanged()

        private fun shiftItem(fromPosition: Int, toPosition: Int) {
            val shiftItem = shortcutList.elementAt(fromPosition)
            shortcutList.removeAt(fromPosition)
            shortcutList.add(toPosition, shiftItem)
            notifyItemMoved(fromPosition, toPosition)
        }

        private fun isInvalidMove(fromPosition: Int, toPosition: Int): Boolean {
            val isEnabled = shortcutList[fromPosition].isEnabled
            val moveDown = toPosition > fromPosition
            val indexOfLastEnabled = shortcutList.indexOfLast { shortcut -> shortcut.isEnabled }
            return (isEnabled && moveDown && toPosition > indexOfLastEnabled)
        }

        class ManageShortcutsViewHolder(val view: View) : RecyclerView.ViewHolder(view.rootView) {
            val shortcutStatusView: ShortcutStatusView = view.shortcutStatusView
        }
    }
}