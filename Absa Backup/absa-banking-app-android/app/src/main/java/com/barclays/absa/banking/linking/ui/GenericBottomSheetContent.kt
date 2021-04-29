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
 */

package com.barclays.absa.banking.linking.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

class GenericBottomSheetContent : Serializable {
    @StringRes var toolbarTitle: Int = -1
    @StringRes var toolbarActionTitle: Int = -1
    @DrawableRes var bottomSheetImage: Int = -1
    @StringRes var contentTitleText: Int = -1
    @StringRes var contentText: Int = -1
    @StringRes var contactNumber: Int = -1
    @StringRes var contactName: Int = -1
}