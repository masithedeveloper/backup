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
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.AppVersion
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.inAppNotifications.InAppNotificationSectionAdapter.SectionViewHolder
import com.barclays.absa.banking.presentation.inAppNotifications.InAppTreads.*
import kotlinx.android.synthetic.main.notification_section_item.view.*

class InAppNotificationSectionAdapter internal constructor(var notificationList: ArrayList<InAppSection>, private val onSectionItemClickListener: OnSectionItemClickListener) : RecyclerView.Adapter<SectionViewHolder>(), Filterable {
    private lateinit var searchText: String
    private val originalList: ArrayList<InAppSection> = ArrayList()
    private val customFilter: CustomFilter = CustomFilter()

    init {
        this.notificationList = NotificationUtil.sortSectionsByDate(notificationList)
        originalList.addAll(notificationList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder = SectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_section_item, parent, false))

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val inAppItem = notificationList[position]
        inAppItem.threadId?.title?.let {
            val description = it
            if (::searchText.isInitialized && searchText.isNotEmpty()) {
                val startPosition = description.toLowerCase().indexOf(searchText)
                val endPosition = startPosition + searchText.length
                if (startPosition != -1) {
                    val spannable: Spannable = SpannableString(description)
                    val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(BMBApplication.getInstance(), R.color.filter_color)))
                    val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.transactionDescriptionTextView.text = spannable
                } else {
                    holder.transactionDescriptionTextView.text = description
                }
            } else {
                holder.transactionDescriptionTextView.text = description
            }
        }

        inAppItem.lastMessage.notificationMessage
        if (!inAppItem.lastMessage.messageRead) {
            holder.notificationIndicatorImageView.visibility = View.VISIBLE
            holder.navigateImageView.visibility = View.INVISIBLE
        } else {
            holder.notificationIndicatorImageView.visibility = View.GONE
            holder.navigateImageView.visibility = View.VISIBLE
        }

        if (shouldShowHiddenContent(inAppItem.lastMessage)) {
            inAppItem.lastMessage.notificationMessage.extras.getString("hiddenMessage")?.let { hiddenMessage ->
                inAppItem.lastMessage.notificationMessage.message = hiddenMessage
            }
        }

        holder.subMessageTextView.text = inAppItem.lastMessage.notificationMessage.message
        when (inAppItem.threadId?.id) {
            GENERAL_THREAD.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_general)
            ALERTS_THREAD.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_alert)
            MARKETING_THREAD.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_marketing)
            BANKING_TIPS_THREAD.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_tips)
            SASOL_THREAD.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_sasol)
            BANKING_APP_THREAD.threadId, BANKING_APP_THREAD_TWO.threadId, BANKING_APP_THREAD_THREE.threadId, BANKING_APP_THREAD_FOUR.threadId -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_banking_app)
            else -> holder.messageSectionImageView.setImageResource(R.drawable.ic_in_app_random)
        }
        holder.messageConstraintLayout.setOnClickListener { onSectionItemClickListener.onInAppSectionClicked(inAppItem) }
    }

    override fun getItemCount(): Int = notificationList.size

    fun updateNotifications(sections: ArrayList<InAppSection>) {
        notificationList = NotificationUtil.sortSectionsByDate(sections)
        notifyDataSetChanged()
    }

    private fun shouldShowHiddenContent(notification: InAppMessage): Boolean {
        notification.notificationMessage.extras.getString("minAndroidVersion")?.let {
            return AppVersion(BuildConfig.VERSION_NAME).compareTo(AppVersion(it.replace("v", ""))) > 0
        }
        return false
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageSectionImageView: ImageView = itemView.iconGroupImageView
        var notificationIndicatorImageView: ImageView = itemView.notificationIndicatorImageView
        var navigateImageView: ImageView = itemView.arrowImageView
        var messageConstraintLayout: ConstraintLayout = itemView.messageConstraintLayout
        var transactionDescriptionTextView: TextView = itemView.inAppTitleTextView
        var subMessageTextView: TextView = itemView.subMessageTextView
    }

    override fun getFilter(): Filter = customFilter

    fun search(text: String?) {
        filter.filter(text)
    }

    @Suppress("UNCHECKED_CAST")
    internal inner class CustomFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            searchText = charSequence.toString().toLowerCase().trim { it <= ' ' }
            val filteredList: MutableList<InAppSection> = ArrayList()
            if (::searchText.isInitialized && searchText.isEmpty()) {
                filteredList.addAll(originalList)
            } else {
                originalList.forEach { transactionItem ->
                    transactionItem.threadId?.title?.let {
                        val smallCaseFilter = it.toLowerCase()
                        val smallCaseSubTextFilter = transactionItem.lastMessage.notificationMessage.message.toLowerCase()
                        if (smallCaseFilter.contains(searchText) || smallCaseSubTextFilter.contains(searchText)) {
                            filteredList.add(transactionItem)
                        }
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
            notificationList.addAll(filterResults.values as List<InAppSection>)
            notifyDataSetChanged()
        }
    }

    internal interface OnSectionItemClickListener {
        fun onInAppSectionClicked(inAppSection: InAppSection)
    }

    private fun String.toLowerCase() = this.toLowerCase(BMBApplication.getApplicationLocale())
}