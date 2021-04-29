/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package styleguide.bars

import androidx.fragment.app.Fragment

abstract class FragmentPagerItem(layout: Int) : Fragment(layout) {

    constructor() : this(0)

    protected abstract fun getTabDescription(): String

    companion object {
        val TAB_DESCRIPTION_KEY = "TAB_DESCRIPTION_KEY"
    }
}