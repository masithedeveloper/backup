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
package com.barclays.absa.banking.presentation.inAppNotifications

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.HomeContainerActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import kotlinx.android.synthetic.main.in_app_notification_fragment.*

class InAppNotificationSectionFragment : InAppNotificationBaseFragment(R.layout.in_app_notification_fragment), SearchView.OnQueryTextListener {
    private lateinit var adapter: InAppNotificationSectionAdapter
    private lateinit var homeContainerActivity: HomeContainerActivity
    private var isFromPushNotification = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContainerActivity = context as HomeContainerActivity
    }

    private fun verifyCapability() {
        if (featureSwitchingToggles.inAppMailbox == FeatureSwitchingStates.DISABLED.key) {
            showFeatureUnavailable()
        } else {
            if (appCacheService.getSecureHomePageObject() == null || CustomerProfileObject.instance.mailboxProfileId.isNullOrEmpty()) {
                showFeatureUnavailable()
            } else {
                initViews()
            }
        }
    }

    private fun showFeatureUnavailable() {
        startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_in_app_messages)), true))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeContainerActivity.setFragment(this)
        verifyCapability()
        requestMessages()

        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            notificationIndicatorCardView.visibility = VISIBLE
        }

        appSettingsButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                startActivity(intent)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }

            notificationIndicatorCardView.visibility = GONE
        }
    }

    private fun requestMessages() {
        notificationSwipeLayout.isRefreshing = true
        inAppNotificationViewModel.apply {
            inAppMessageThreads = MutableLiveData()
            buildNotificationSectionList()
            inAppMessageThreads.observe(viewLifecycleOwner, {
                notificationSwipeLayout?.isRefreshing = false
                updateMessageList(it, isFromPushNotification)
            })
        }
    }

    private fun initViews() {
        notificationRecyclerView?.apply {
            isNestedScrollingEnabled = false
            isFocusable = false
        }
        notificationSwipeLayout.setOnRefreshListener { requestMessages() }
        context?.let {
            notificationSwipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(it, R.color.lotto_card_background_color))
        }
        notificationSwipeLayout.setColorSchemeResources(R.color.pink,
                R.color.energy,
                R.color.dark_pink,
                R.color.dark_purple, R.color.lotto_card_background_color)
        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = toolbar as Toolbar
        toolbar.inflateMenu(R.menu.search_menu_dark)
        toolbar.title = getString(R.string.home_container_notifications)
        val menu = toolbar.menu
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager = homeContainerActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        var searchView: SearchView? = null
        searchItem?.let {
            searchView = it.actionView as SearchView
            setupSearchView(searchView)
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(homeContainerActivity.componentName))
    }

    private fun setupSearchView(searchView: SearchView?) {
        searchView?.apply {
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@InAppNotificationSectionFragment)
            isSubmitButtonEnabled = false
            isQueryRefinementEnabled = false
        }
    }

    private fun updateMessageList(sections: ArrayList<InAppSection>, isFromPush: Boolean) {
        if (!sections.isNullOrEmpty()) {
            toolbar?.let { it.visibility = VISIBLE }
            emptyStateAnimationView?.visibility = GONE
            notificationSwipeLayout?.visibility = VISIBLE
            notificationRecyclerView?.visibility = VISIBLE
            if (!::adapter.isInitialized) {
                adapter = InAppNotificationSectionAdapter(sections, object : InAppNotificationSectionAdapter.OnSectionItemClickListener {
                    override fun onInAppSectionClicked(inAppSection: InAppSection) {
                        inAppNotificationViewModel.lastSelectedSection = inAppSection
                        homeContainerActivity.startContentFragment(InAppNotificationMessageFragment.newInstance())
                    }
                })
                notificationRecyclerView?.adapter = adapter
            } else {
                notificationRecyclerView?.adapter = adapter
                adapter.updateNotifications(sections)
            }
            if (isFromPush) {
                isFromPushNotification = false
                if (::homeContainerActivity.isInitialized) {
                    inAppNotificationViewModel.lastSelectedSection = sections.firstOrNull { inAppSection -> inAppSection.threadId?.id == inAppNotificationViewModel.customTags.threadId } ?: InAppSection()

                    if (!inAppNotificationViewModel.lastSelectedSection.threadId?.id.isNullOrBlank()) {
                        homeContainerActivity.startContentFragment(InAppNotificationMessageFragment.newInstance())
                    }
                }
            }
        } else {
            notificationRecyclerView?.visibility = GONE
            emptyStateAnimationView.visibility = VISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (::adapter.isInitialized) {
            adapter.search(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (::adapter.isInitialized) {
            adapter.search(newText)
        }
        return false
    }

    companion object {
        @JvmStatic
        fun newInstance(isFromPushNotification: Boolean): InAppNotificationSectionFragment {
            val fragment = InAppNotificationSectionFragment()
            fragment.isFromPushNotification = isFromPushNotification
            return fragment
        }
    }
}