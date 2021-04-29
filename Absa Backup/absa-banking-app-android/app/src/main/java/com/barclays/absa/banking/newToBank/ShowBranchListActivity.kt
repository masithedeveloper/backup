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
package com.barclays.absa.banking.newToBank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import kotlinx.android.synthetic.main.show_city_list_activity.*
import styleguide.content.TertiaryContentAndLabelView

class ShowBranchListActivity : BaseActivity(R.layout.show_city_list_activity) {

    private lateinit var branchesSiteNames: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.new_to_bank_select_branch) { onBackPressed() }
        branchesSiteNames = intent.extras?.getStringArrayList(NewToBankConstants.RETRIEVED_BRANCHES) ?: ArrayList()
        branchesSiteNames.sort()
        populateBranchesList()
        initViews()
    }

    private fun populateBranchesList() {
        itemsRecyclerView.adapter = ShowBranchListAdapter(branchesSiteNames).apply {
            setItemClickListener { view, _ ->
                if (view.id == R.id.tertiaryContentAndLabelView) {
                    returnSelectedItem(view as TertiaryContentAndLabelView)
                }
            }
        }
    }

    private fun initViews() = with(searchNormalInputView) {
        addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                (itemsRecyclerView.adapter as ShowBranchListAdapter).filter.filter(s.toString())
                itemsRecyclerView.layoutManager?.scrollToPosition(0)
            }
        })
        setValueViewEditorActionListener { _, _, _ -> false }
    }

    private fun returnSelectedItem(tertiaryContentAndLabelView: TertiaryContentAndLabelView) {
        val selectedItemText = tertiaryContentAndLabelView.contentTextViewValue
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(NewToBankConstants.SELECTED_ITEM, selectedItemText)
        })
        finish()
    }
}