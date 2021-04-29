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
 */

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.models.DisplayForeignTaxCountry
import kotlinx.android.synthetic.main.manage_profile_tax_overview_widget.view.*

class ManageProfileTaxOverviewWidget constructor(context: Context, taxDisplayValues: DisplayForeignTaxCountry, isLastItem: Boolean) : ConstraintLayout(context, null, 0) {
    init {
        inflate(context, R.layout.manage_profile_tax_overview_widget, this)

        if (taxDisplayValues.taxCountry.isEmpty()) {
            taxCountryContentView.visibility = View.GONE
        } else {
            taxCountryContentView.visibility = View.VISIBLE
            taxCountryContentView.setContentText(taxDisplayValues.taxCountry)
        }

        if (taxDisplayValues.taxNumber.isEmpty()) {
            taxNumberContentView.setContentText(context.getString(R.string.none))
        } else {
            taxNumberContentView.setContentText(taxDisplayValues.taxNumber)
        }

        if (taxDisplayValues.reasonForNoTaxNumber.isEmpty()) {
            taxReasonContentView.visibility = View.GONE
        } else {
            taxReasonContentView.visibility = View.VISIBLE
            taxReasonContentView.setContentText(taxDisplayValues.reasonForNoTaxNumber)
        }

        if (taxDisplayValues.reasonForNoTaxNumber.isNotEmpty() && !isLastItem) {
            taxReasonContentView.setPadding(0, 0, 0, 20)
        } else if (taxDisplayValues.taxNumber.isNotEmpty() && !isLastItem) {
            taxNumberContentView.setPadding(0, 0, 0, 20)
        }
    }
}