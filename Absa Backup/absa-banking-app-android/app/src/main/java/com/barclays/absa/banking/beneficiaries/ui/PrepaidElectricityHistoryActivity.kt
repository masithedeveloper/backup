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
package com.barclays.absa.banking.beneficiaries.ui

import android.os.Bundle
import android.view.Menu
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity.PPE_BENEFICIARY_OBJECT
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity.PPE_TRANSACTION_OBJECT
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.TransactionObject
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityPurchaseReceiptFragment
import com.barclays.absa.banking.framework.BaseActivity

class PrepaidElectricityHistoryActivity : BaseActivity(R.layout.prepaid_electricity_history_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.prepaid_electricity_purchase_receipt)
        intent.extras?.apply {
            val beneficiaryDetailObject = getSerializable(PPE_BENEFICIARY_OBJECT) as BeneficiaryDetailObject
            val transactionObject = getSerializable(PPE_TRANSACTION_OBJECT) as TransactionObject
            startFragment(PrepaidElectricityPurchaseReceiptFragment.newInstance(beneficiaryDetailObject, null, null, transactionObject), true, AnimationType.NONE, "")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.electricity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}