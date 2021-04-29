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

package com.barclays.absa.banking.payments.international

import android.app.Activity
import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.BaseExpandableListView
import java.util.*

class InternationalPaymentsStateSelectorActivity : BaseExpandableListView<String>() {

    private var childItemSource: ArrayList<String>? = null

    override fun getIntentExtraData(baseExpandableListView: BaseExpandableListView<*>) {
        val chooseBranchListActivity = baseExpandableListView as InternationalPaymentsStateSelectorActivity
        val states = chooseBranchListActivity.intent.getStringArrayListExtra(InternationalPaymentsConstants.INTERNATIONAL_STATE_LIST)
        states?.let {
            states.sortWith(kotlin.Comparator { leftHandSide, rightHandSide ->
                if (leftHandSide != null && rightHandSide != null) {
                    return@Comparator leftHandSide.compareTo(rightHandSide, true)
                }
                0
            })
            childItemSource = states
        }
    }

    override fun onItemClick(item: String) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(InternationalPaymentsConstants.SELECTED_STATE, item)
        })
        finish()
    }

    override fun onListItemSearch(searchString: String): List<String>? {
        val tempStates = ArrayList<String>()
        childItemSource?.forEach { state ->
            if(state.contains(searchString, true)) {
                tempStates.add(state)
            }
        }
        return tempStates
    }

    override fun isSearchRequired() = true

    override fun getPrimaryData(item: String) = item

    override fun getFirstLetter(item: String?) = item?.substring(0, 1) ?: ""

    override fun getNavigationTitle() = getString(R.string.select_state)

    override fun getChildItemSource() = childItemSource

    override fun getChildTemplateId() = R.layout.primary_content_and_label_row

    override fun getHeaderTemplateId() = R.layout.section_view

    override fun isSecondaryLabelRequired() = false
}