/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.HomeContainerActivity
import com.barclays.absa.banking.presentation.shared.AppUpdateUtil
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.imimobile.connect.core.messaging.ICMessage
import kotlinx.android.synthetic.main.in_app_notification_fragment.*
import java.util.*


class InAppNotificationMessageFragment : InAppNotificationBaseFragment(R.layout.in_app_notification_fragment), SearchView.OnQueryTextListener {
    private lateinit var adapter: InAppNotificationMessageAdapter
    private lateinit var homeContainerActivity: HomeContainerActivity
    private lateinit var toolBar: Toolbar
    private lateinit var menu: Menu
    private lateinit var section: InAppSection

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContainerActivity = context as HomeContainerActivity
    }

    private fun initViews() {
        notificationRecyclerView?.apply {
            isNestedScrollingEnabled = false
            isFocusable = false
        }
        setUpToolbar()
    }

    private fun setUpToolbar() {
        toolBar = toolbar as Toolbar
        toolBar.apply {
            navigationIcon = getDrawable(homeContainerActivity, R.drawable.ic_arrow_back_dark)
            setNavigationOnClickListener { homeContainerActivity.supportFragmentManager.popBackStack() }
            inflateMenu(R.menu.search_and_delete_menu_dark)
            title = if (::section.isInitialized) section.threadId?.title else getString(R.string.home_container_notifications)
        }
        menu = toolBar.menu

        val searchItem = menu.findItem(R.id.action_search)
        searchItem.setOnMenuItemClickListener {
            val searchManager = homeContainerActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            var searchView: SearchView? = null
            searchItem?.let {
                searchView = it.actionView as? SearchView
                setupSearchView(searchView)
            }
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(homeContainerActivity.componentName))
            true
        }
        menu.findItem(R.id.action_delete).setOnMenuItemClickListener {
            if (::adapter.isInitialized) {
                adapter.isDeleteActive = true
            }
            notificationSwipeLayout.isRefreshing = false
            showDeleteToolbar()
            true
        }
        menu.findItem(R.id.action_cancel).setOnMenuItemClickListener {
            if (::adapter.isInitialized) {
                adapter.isDeleteActive = false
            }
            showSearchToolbar()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeContainerActivity.setFragment(this)
        homeContainerActivity.hideBadge()

        section = inAppNotificationViewModel.lastSelectedSection

        fetchMessagesForCurrentThread()
        if (featureUnavailable()) {
            return
        }
        context?.let {
            notificationSwipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(it, R.color.lotto_card_background_color))
        }
        notificationSwipeLayout.setColorSchemeResources(R.color.pink,
                R.color.energy,
                R.color.dark_pink,
                R.color.dark_purple, R.color.lotto_card_background_color)

        notificationSwipeLayout.isRefreshing = true
        notificationSwipeLayout.setOnRefreshListener {
            fetchMessagesForCurrentThread()
        }
    }

    fun deleteInAppMessage(message: ICMessage) {
        inAppNotificationViewModel.apply {
            deleteMessage(message)
            onDeleteCompleted = MutableLiveData()
            onDeleteCompleted.observe(viewLifecycleOwner, {
                if (adapter.itemCount == 0) {
                    homeContainerActivity.supportFragmentManager.popBackStack()
                }
            })
        }
    }

    private fun fetchMessagesForCurrentThread() {
        if (::adapter.isInitialized) {
            adapter.updateNotifications(arrayListOf())
        }
        notificationSwipeLayout.isRefreshing = true
        fetchMessagesForCurrentThread(inAppNotificationViewModel.currentDate)
    }

    private fun fetchMessagesForCurrentThread(beforeDate: Date) {
        inAppNotificationViewModel.inAppTreadMessages = MutableLiveData()
        if (BuildConfig.STUB) {
            inAppNotificationViewModel.fetchInAppMessages("STUB", Date())
        } else if (::section.isInitialized) {
            section.threadId?.id?.let { inAppNotificationViewModel.fetchInAppMessages(it, beforeDate) }
        }

        inAppNotificationViewModel.inAppTreadMessages.observe(viewLifecycleOwner, {
            updateMessageList(it)
        })
    }

    private fun showFeatureUnavailable() {
        startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_in_app_messages)), true))
    }

    private fun featureUnavailable(): Boolean {
        return if (featureSwitchingToggles.inAppMailbox == FeatureSwitchingStates.DISABLED.key || appCacheService.getSecureHomePageObject() == null || CustomerProfileObject.instance.mailboxProfileId.isNullOrEmpty()) {
            showFeatureUnavailable()
            true
        } else {
            initViews()
            false
        }
    }

    private fun updateMessageList(messages: ArrayList<InAppMessage>?) {
        if (!messages.isNullOrEmpty()) {
            toolBar.visibility = View.VISIBLE
            emptyStateAnimationView.visibility = View.GONE
            notificationRecyclerView?.visibility = View.VISIBLE
            if (!::adapter.isInitialized) {
                adapter = InAppNotificationMessageAdapter(messages, object : InAppNotificationMessageAdapter.OnManageMessage {
                    override fun onDeleteIconClicked(message: InAppMessage) {
                        deleteInAppMessage(message.notificationMessage)
                    }

                    override fun markMessageAsRead(transactionId: String) {
                        inAppNotificationViewModel.markAllMessagesAsRead(transactionId)
                    }

                    override fun onUpdateButtonClicked() {
                        AppUpdateUtil.launchPlayStore(homeContainerActivity)
                    }
                }, inAppNotificationViewModel.hasMoreMessagesAvailable)
                notificationRecyclerView.adapter = adapter
            } else {
                adapter.lazyLoadNotifications(messages, inAppNotificationViewModel.hasMoreMessagesAvailable)
            }

            loadMessages()
        } else {
            emptyStateAnimationView.visibility = View.VISIBLE
        }
        notificationSwipeLayout?.isRefreshing = false

    }

    private fun loadMessages() {
        var loading = true
        var pastVisibleItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int

        notificationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    with(notificationRecyclerView.layoutManager as LinearLayoutManager) {
                        visibleItemCount = childCount
                        totalItemCount = itemCount
                        pastVisibleItems = findFirstVisibleItemPosition()
                    }

                    if (loading && visibleItemCount + pastVisibleItems >= totalItemCount) {
                        loading = false
                        if (inAppNotificationViewModel.hasMoreMessagesAvailable) {
                            inAppNotificationViewModel.inAppTreadMessages.value?.last()?.notificationMessage?.submittedAt?.let { fetchMessagesForCurrentThread(it) }
                        }
                    }
                }
            }
        })
    }

    private fun showSearchToolbar() {
        menu.apply {
            findItem(R.id.action_search).isVisible = true
            findItem(R.id.action_delete).isVisible = true
            findItem(R.id.action_cancel).isVisible = false
        }
    }

    private fun showDeleteToolbar() {
        menu.apply {
            findItem(R.id.action_search).isVisible = false
            findItem(R.id.action_delete).isVisible = false
            findItem(R.id.action_cancel).isVisible = true
        }
    }

    private fun setupSearchView(searchView: SearchView?) {
        searchView?.apply {
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@InAppNotificationMessageFragment)
            isSubmitButtonEnabled = false
            isQueryRefinementEnabled = false
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
        fun newInstance(): InAppNotificationMessageFragment = InAppNotificationMessageFragment()
    }
}