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

package com.barclays.absa.banking.manage.profile.ui.widgets

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.manage_profile_confirmation_widget.view.*

class ConfirmScreenItemWidget constructor(context: Context, confirmScreenItemList: ConfirmScreenItem) : ConstraintLayout(context, null, 0) {

    init {
        inflate(context, R.layout.manage_profile_confirmation_widget, this)
        if (confirmScreenItemList.label.isNotEmpty()) {
            confirmationItem.setLabelText(confirmScreenItemList.label)
        }

        if (confirmScreenItemList.value.isNotEmpty()) {
            confirmationItem.setContentText(confirmScreenItemList.value)
        }
    }
}