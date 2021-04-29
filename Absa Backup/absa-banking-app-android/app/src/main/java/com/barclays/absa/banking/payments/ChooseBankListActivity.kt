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
package com.barclays.absa.banking.payments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BankDetails
import com.barclays.absa.banking.boundary.model.ExergyBankListResponse
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.ui.EditPolicyPaymentDetailsFragment.Companion.IS_EXERGY_POLICY
import com.barclays.absa.banking.payments.PaymentsConstants.BANK_LIST
import com.barclays.absa.banking.payments.PaymentsConstants.BANK_NAME
import java.util.*

class ChooseBankListActivity : BaseExpandableListView<String>() {

    private var childItemSource: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.SELECT_TO_BANK_CONST, BMBConstants.PAYMENTS_CONST, BMBConstants.TRUE_CONST)
    }

    override fun getIntentExtraData(activity: BaseExpandableListView<*>) {
        val chooseBankListActivity = activity as ChooseBankListActivity
        if (chooseBankListActivity.intent.getBooleanExtra(IS_EXERGY_POLICY, false)) {
            val bankDetails = chooseBankListActivity.intent.getSerializableExtra(BANK_LIST) as? ExergyBankListResponse
            bankDetails?.let {
                childItemSource = it.bankNameList
                childItemSource?.sort()
            }
        } else {
            val bankDetails = chooseBankListActivity.intent.getSerializableExtra(BANK_LIST) as? BankDetails
            bankDetails?.let {
                childItemSource = it.bankList
                childItemSource?.sort()
            }
        }
    }

    override fun onItemClick(item: String) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra(BANK_NAME, item) })
        finish()
    }

    override fun onListItemSearch(searchString: String): List<String>? {
        val tempInstitutions = ArrayList<String>()
        childItemSource?.forEach { bankName ->
            run {
                if (bankName.contains(searchString, true)) {
                    tempInstitutions.add(bankName)
                }
            }
        }
        return tempInstitutions
    }

    override fun getUniversalListItemSource() = null

    override fun getChildItemSource() = childItemSource

    override fun getChildTemplateId() = R.layout.primary_content_and_label_row

    override fun getHeaderTemplateId() = R.layout.section_view

    override fun getNavigationTitle() = getString(R.string.select_bank)

    override fun getFirstLetter(item: String?) = item?.substring(0, 1)

    override fun getPrimaryData(item: String?) = item

    override fun isSecondaryLabelRequired() = false

    override fun isUniversalListRequired() = true
}