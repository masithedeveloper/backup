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

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.AppVersion
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.imimobile.connect.core.enums.ICMessageStatus
import kotlinx.android.synthetic.main.in_app_notification_loading.view.*
import kotlinx.android.synthetic.main.notification_item.view.*
import java.util.*

class InAppNotificationMessageAdapter constructor(var notificationList: ArrayList<InAppMessage>, private var onManageMessage: OnManageMessage, private var hasMoreMessagesToLoad: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private lateinit var searchText: String
    private val originalList: ArrayList<InAppMessage> = ArrayList()
    private val customFilter: CustomFilter = CustomFilter()
    var isDeleteActive = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
        this.notificationList = NotificationUtil.sortBeneficiariesByFirstName(notificationList)
        originalList.addAll(notificationList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == 0) {
        NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false))
    } else {
        LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.in_app_notification_loading, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is NotificationViewHolder) {
            val notification = notificationList[position]
            bindMessage(notification, viewHolder)
            bindNotificationDates(notification, viewHolder)
            bindMessageReadStatus(notification)
            bindDelete(viewHolder, position, notification)
            bindDeepLink(viewHolder, notification)
        } else {
            (viewHolder as LoadingViewHolder).progressBar.visibility = View.VISIBLE
        }
    }

    private fun bindDeepLink(viewHolder: NotificationViewHolder, notification: InAppMessage) {
        if (shouldShowHiddenContent(notification)) {
            val deepLinkButton = notification.notificationMessage.extras.getParcelableArray("buttons")?.firstOrNull() as? Bundle ?: return
            viewHolder.deepLinkButton.visibility = View.VISIBLE
            viewHolder.deepLinkButton.text = deepLinkButton.getString("text")
            viewHolder.deepLinkButton.setOnClickListener { DeepLinkUtil.deepLinkTo(deepLinkButton.getString("value") ?: "") }
        } else if (!notification.notificationMessage.extras.getString("minAndroidVersion").isNullOrBlank()) {
            viewHolder.deepLinkButton.visibility = View.VISIBLE
            viewHolder.deepLinkButton.text = viewHolder.cardView.context.getString(R.string.in_app_notifications_update_app)
            viewHolder.deepLinkButton.setOnClickListener { onManageMessage.onUpdateButtonClicked() }
        } else {
            viewHolder.deepLinkButton.visibility = View.GONE
        }
    }

    private fun bindMessage(notification: InAppMessage, viewHolder: NotificationViewHolder) {
        if (shouldShowHiddenContent(notification)) {
            notification.notificationMessage.extras.getString("hiddenMessage")?.let { hiddenMessage ->
                notification.notificationMessage.message = hiddenMessage
            }
        }

        notification.notificationMessage.message?.let {
            val description = it
            if (::searchText.isInitialized && searchText.isNotEmpty()) {
                val startPosition = description.toLowerCase().indexOf(searchText)
                val endPosition = startPosition + searchText.length
                if (startPosition != -1) {
                    val spannable: Spannable = SpannableString(description)
                    val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(viewHolder.messageTextView.context, R.color.filter_color)))
                    val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    viewHolder.messageTextView.text = spannable
                } else {
                    viewHolder.messageTextView.text = description
                }
            } else {
                viewHolder.messageTextView.text = notification.notificationMessage.message
            }
        }
    }

    private fun shouldShowHiddenContent(notification: InAppMessage): Boolean {
        notification.notificationMessage.extras.getString("minAndroidVersion")?.let {
            return AppVersion(BuildConfig.VERSION_NAME).compareTo(AppVersion(it.replace("v", ""))) > 0
        }
        return false
    }

    private fun bindMessageReadStatus(notification: InAppMessage) {
        if (ICMessageStatus.Read != notification.notificationMessage.status) {
            onManageMessage.markMessageAsRead(notification.notificationMessage.transactionId.trim { it <= ' ' })
        }
    }

    private fun bindDelete(viewHolder: NotificationViewHolder, position: Int, notification: InAppMessage) {
        if (isDeleteActive) {
            showDeleteIcon(viewHolder)
            viewHolder.cardView.animate().translationX(-viewHolder.messageTextView.context.resources.getDimension(R.dimen.icon_size)).setDuration(300).start()
        } else {
            viewHolder.cardView.animate().translationX(0f).setDuration(300).start()
            hideDeleteIcon(viewHolder)
        }
        viewHolder.deleteImageView.setOnClickListener {
            if (isDeleteActive) {
                removeAt(position)
                onManageMessage.onDeleteIconClicked(notification)
            }
        }
    }

    private fun bindNotificationDates(notification: InAppMessage, viewHolder: NotificationViewHolder) {
        val dateNow = Calendar.getInstance().time
        val yesterdayDate = Calendar.getInstance()
        yesterdayDate.add(Calendar.DATE, -1)
        var transactionDate = dateNow.toString()
        notification.notificationMessage.submittedAt?.let { transactionDate = DateUtils.format(it, DATE_DISPLAY_PATTERN) }
        var transactionTime: String? = dateNow.toString()
        notification.notificationMessage.submittedAt?.let { transactionTime = DateUtils.format(it, "HH:mm") }
        viewHolder.timeTextView.text = transactionTime

        when (transactionDate) {
            DateUtils.format(dateNow, DATE_DISPLAY_PATTERN) -> viewHolder.dateTextView.setText(R.string.credit_card_hub_today)
            DateUtils.format(yesterdayDate.time, DATE_DISPLAY_PATTERN) -> viewHolder.dateTextView.setText(R.string.credit_card_hub_yesterday)
            else -> viewHolder.dateTextView.text = transactionDate
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == notificationList.size) 1 else super.getItemViewType(position)

    private fun showDeleteIcon(holder: NotificationViewHolder) {
        with(holder.deleteImageView) {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setStartDelay(200).setDuration(400).start()
        }
    }

    private fun hideDeleteIcon(holder: NotificationViewHolder) {
        holder.deleteImageView.animate().alpha(0f).setStartDelay(0).setDuration(250).withEndAction { holder.deleteImageView.visibility = View.GONE }.start()
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = if (hasMoreMessagesToLoad) notificationList.size + 1 else notificationList.size

    override fun getFilter(): Filter = customFilter

    fun search(text: String?) {
        filter.filter(text)
    }

    fun updateNotifications(messages: ArrayList<InAppMessage>) {
        notificationList.clear()
        notificationList = NotificationUtil.sortBeneficiariesByFirstName(messages)
        updateOriginalNotificationList(notificationList)
        notifyDataSetChanged()
    }

    fun lazyLoadNotifications(messages: ArrayList<InAppMessage>, hasMoreMessagesToLoad: Boolean) {
        this.hasMoreMessagesToLoad = hasMoreMessagesToLoad
        notificationList.addAll(messages)
        updateOriginalNotificationList(notificationList)
        notifyDataSetChanged()
    }

    private fun updateOriginalNotificationList(messages: ArrayList<InAppMessage>) {
        originalList.clear()
        originalList.addAll(messages)
    }

    internal inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeTextView: TextView = itemView.timeTextView
        var dateTextView: TextView = itemView.dateTextView
        var messageTextView: TextView = itemView.messageTextView
        var notificationIndicatorImageView: ImageView = itemView.notificationIndicatorImageView
        var deepLinkButton: Button = itemView.deepLinkButton
        var cardView: CardView = itemView.cardView
        var deleteImageView: ImageView = itemView.deleteImageView
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.progressBar
    }

    @Suppress("UNCHECKED_CAST")
    internal inner class CustomFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            searchText = charSequence.toString().toLowerCase().trim { it <= ' ' }
            val filteredList: MutableList<InAppMessage> = ArrayList()
            if (searchText.isEmpty()) {
                filteredList.addAll(originalList)
            } else {
                originalList.forEach { transactionItem ->
                    val smallCaseFilter = transactionItem.notificationMessage.message.toLowerCase()
                    if (smallCaseFilter.contains(searchText)) {
                        filteredList.add(transactionItem)
                    }
                }
            }
            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            notificationList.clear()
            notificationList.addAll((filterResults.values as List<InAppMessage>))
            notifyDataSetChanged()
        }
    }

    private fun removeAt(position: Int) {
        if (position < notificationList.size) {
            notificationList.removeAt(position)
        }
        notifyDataSetChanged()
    }

    interface OnManageMessage {
        fun onDeleteIconClicked(message: InAppMessage)
        fun markMessageAsRead(transactionId: String)
        fun onUpdateButtonClicked()
    }

    private fun String.toLowerCase() = this.toLowerCase(BMBApplication.getApplicationLocale())
}