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
import com.barclays.absa.banking.boundary.model.BankBranches
import com.barclays.absa.banking.boundary.model.Branch
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.ui.ChangePolicyPaymentRequestParameters.Companion.BANK_ID
import com.barclays.absa.banking.funeralCover.ui.ChangePolicyPaymentRequestParameters.Companion.BRANCH_ID
import com.barclays.absa.banking.payments.PaymentsConstants.BRANCH_CODE
import com.barclays.absa.banking.payments.PaymentsConstants.BRANCH_NAME
import com.barclays.absa.banking.payments.PaymentsConstants.IIP_STATUS
import java.util.ArrayList
import kotlin.Comparator

class ChooseBranchListActivity : BaseExpandableListView<Branch>() {
    private var childItemSource: MutableList<Branch>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackCustomScreenView(BMBConstants.SELECT_TO_BRANCH_CONST, BMBConstants.PAYMENTS_CONST, BMBConstants.TRUE_CONST)
    }

    override fun getIntentExtraData(activity: BaseExpandableListView<*>) {
        val chooseBranchListActivity = activity as ChooseBranchListActivity
        val bankBranches = chooseBranchListActivity.intent.getSerializableExtra(BMBConstants.RESULT) as? BankBranches
        bankBranches?.apply {
            childItemSource = bankBranches.branchList?.toMutableList()
            childItemSource?.sortWith(Comparator { branch1, branch2 ->
                val branch1Name = branch1.branchName
                val branch2Name = branch2.branchName
                return@Comparator if (branch1Name != null && branch2Name != null) branch1Name.compareTo(branch2Name, true) else 0
            })
        }
    }

    override fun onItemClick(item: Branch) {
        val returnIntent = Intent().apply {
            putExtra(BRANCH_NAME, item.branchName)
            putExtra(BRANCH_CODE, item.branchCode)
            putExtra(IIP_STATUS, item.immediatePaymentAllowed)
            putExtra(BANK_ID, item.bankId)
            putExtra(BRANCH_ID, item.branchId)
        }

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onListItemSearch(searchString: String): List<Branch>? {
        val tempBranch = ArrayList<Branch>()
        childItemSource?.forEach { branch ->
            run {
                val branchName = branch.branchName ?: ""
                val branchCode = branch.branchCode ?: ""
                if (branchName.contains(searchString, true) || branchCode.contains(searchString, true)) {
                    tempBranch.add(branch)
                }
            }
        }
        return tempBranch
    }

    override fun getChildItemSource() = childItemSource

    override fun getChildTemplateId() = R.layout.primary_content_and_label_row

    override fun getHeaderTemplateId() = R.layout.section_view

    override fun getNavigationTitle() = getString(R.string.select_branch)

    override fun getFirstLetter(branch: Branch?) = branch?.branchName?.substring(0, 1)

    override fun getPrimaryData(branch: Branch?) = branch?.branchName

    override fun getSecondaryData(branch: Branch?) = branch?.branchCode

    override fun isSecondaryLabelRequired() = true
}