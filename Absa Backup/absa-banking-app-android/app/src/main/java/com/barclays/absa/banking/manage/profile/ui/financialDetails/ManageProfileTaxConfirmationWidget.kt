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
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.models.DisplayForeignTaxCountry
import kotlinx.android.synthetic.main.manage_profile_tax_confirmation_widget.view.*

class ManageProfileTaxConfirmationWidget constructor(context: Context, foreignTaxDisplayValues: DisplayForeignTaxCountry) : ConstraintLayout(context, null, 0) {

    init {
        inflate(context, R.layout.manage_profile_tax_confirmation_widget, this)

        if (foreignTaxDisplayValues.taxNumber.isEmpty()) {
            taxNumberContent.setContentText(context.getString(R.string.none))
        } else {
            taxNumberContent.setContentText(foreignTaxDisplayValues.taxNumber)
        }

        if (foreignTaxDisplayValues.taxCountry.isEmpty()) {
            taxCountryContent.visibility = GONE
        } else {
            taxCountryContent.visibility = VISIBLE
            taxCountryContent.setContentText(foreignTaxDisplayValues.taxCountry)
        }

        if (foreignTaxDisplayValues.reasonForNoTaxNumberCode.isEmpty() || foreignTaxDisplayValues.reasonForNoTaxNumberCode == " " || foreignTaxDisplayValues.reasonForNoTaxNumberCode == "0") {
            reasonContentView.visibility = GONE
        } else {
            reasonContentView.visibility = VISIBLE
            reasonContentView.setContentText(foreignTaxDisplayValues.reasonForNoTaxNumber)
        }
    }

    fun hideDivider() {
        divider.visibility = GONE
    }
}